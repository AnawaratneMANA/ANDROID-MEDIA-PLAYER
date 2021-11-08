package com.example.media_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<MusicList> musicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout searchButton = findViewById(R.id.search_button);
        final LinearLayout menuButton = findViewById(R.id.menu_button);
        final RecyclerView musicRecyclerView = findViewById(R.id.recyclerview);
        final CardView playPauseCard = findViewById(R.id.play_pause_button);
        final ImageView playPauseImage = findViewById(R.id.play_pause_icon);
        final ImageView nextButton = findViewById(R.id.next_button);
        final ImageView prevButton = findViewById(R.id.previous_button);

        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}