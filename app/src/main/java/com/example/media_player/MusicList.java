package com.example.media_player;

import android.net.Uri;

public class MusicList {
   private String title, artist, duration;
   private boolean isPlaying;
   private Uri musicFile;

    public MusicList(String title, String artist, String duration, boolean isPlaying, Uri musicFile) {
        this.title = title;
        this.artist = artist;
        this.isPlaying = isPlaying;
        this.duration = duration;
        this.musicFile = musicFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Uri getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(Uri musicFile) {
        this.musicFile = musicFile;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
