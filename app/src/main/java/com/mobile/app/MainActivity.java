package com.mobile.app;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobile.app.R;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    Button playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = findViewById(R.id.playbtn);
        player = MediaPlayer.create(this, R.raw.sway);
    }

    public void playbtnClick(View v) {
        if (!player.isPlaying()) {
            player.start();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
        }else{
            player.pause();
            playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
}
