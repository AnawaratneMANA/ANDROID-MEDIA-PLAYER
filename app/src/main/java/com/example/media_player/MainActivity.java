package com.example.media_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.media_player.Adapter.MusicAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SongChangeListener{

    private final List<MusicList> musicLists = new ArrayList<>();
    private RecyclerView musicRecyleView;
    private MediaPlayer mediaPlayer;
    private TextView startTime, endTime;
    private boolean isPlaying = false;
    private SeekBar playerSeekBar;
    private ImageView playPauseImg;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View decodeView = getWindow().getDecorView();
        //int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        //decodeView.setSystemUiVisibility(options);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        final LinearLayout searchButton = findViewById(R.id.search_button);
        final LinearLayout menuButton = findViewById(R.id.menu_button);
        musicRecyleView = findViewById(R.id.recyclerview);
        final CardView playPauseCard = findViewById(R.id.play_pause_button);
        playPauseImg = findViewById(R.id.play_pause_icon);
        final ImageView nextButton = findViewById(R.id.next_button);
        final ImageView prevButton = findViewById(R.id.previous_button);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        playerSeekBar = findViewById(R.id.seek_bar);


        musicRecyleView.setHasFixedSize(true);
        musicRecyleView.setLayoutManager(new LinearLayoutManager(this));
        mediaPlayer = new MediaPlayer();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            getMusicFile();
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
            } else {
                getMusicFile();
            }
        }

        playPauseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    isPlaying = false;
                    mediaPlayer.pause();
                    playPauseImg.setImageResource(R.drawable.play_button);
                } else {
                    isPlaying = true;
                    mediaPlayer.start();
                    playPauseImg.setImageResource(R.drawable.pause_icon);
                }
            }
        });

        //Seeking mechanism for the Media Player.
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    if(isPlaying){
                        mediaPlayer.seekTo(i);
                    } else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }
    private void getMusicFile(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.DATA + " LIKE?", new String[]{"%.mp3%"}, null);
        if(cursor == null){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } else if (!cursor.moveToNext()) {
            Toast.makeText(this, "No Music Found!", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()){
                final String getMusicFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                final String getArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long cursorId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorId);
                String getDuration = "00:00";
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    getDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                }
                final MusicList musicList = new MusicList(getMusicFileName, getArtistName, getDuration, false, musicFileUri);
                musicLists.add(musicList);
            }
            musicRecyleView.setAdapter(new MusicAdapter(musicLists, MainActivity.this));
        }
        cursor.close();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMusicFile();
        } else {
            Toast.makeText(this, "Permission Declined by User", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            View decodeView = getWindow().getDecorView();
            int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decodeView.setSystemUiVisibility(options);
        }
    }

    @Override
    public void onChange(int position) {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.reset();
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource(MainActivity.this, musicLists.get(position).getMusicFile());
                    mediaPlayer.prepare();
                } catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Unable to Play the Track!", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                final int getTotalDuration = mediaPlayer.getDuration();

                String generateDuration = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(getTotalDuration) -
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));

                endTime.setText(generateDuration);
                isPlaying = true;
                mediaPlayer.start();
                playerSeekBar.setMax(getTotalDuration);
                playPauseImg.setImageResource(R.drawable.pause_icon);
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int getCurrentDuration = mediaPlayer.getCurrentPosition();
                        String generaDuration = String.format(Locale.getDefault(), "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                                TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration) -
                                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));
                        playerSeekBar.setProgress(getCurrentDuration);
                        startTime.setText(generaDuration);
                    }
                });
            }
        }, 1000, 1000);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                timer.purge();
                timer.cancel();
                isPlaying = false;
                playPauseImg.setImageResource(R.drawable.play_button);
                playerSeekBar.setProgress(0);
            }
        });
    }
}