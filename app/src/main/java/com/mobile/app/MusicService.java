package com.mobile.app;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import java.io.IOException;

public class MusicService extends Service {

    private IBinder binder = new MusicBinder();

    MediaPlayer player;
    boolean playing = false;

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void play(){
        if(!playing){
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/1D10-0C16%3AMusic%2FMachine%20Head%20-%20Unto%20The%20Locust%20(2011)%20%5BSpecial%20Edition%5D%20%5Bmp3%40256%5D%2F01-machine_head-i_am_hell_(sonata_in_c).mp3");
            try{
                player.setDataSource(this,uri);
                player.prepareAsync();
            }catch(IOException e){

            }
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    playing = true;
                }
                });
        }else{
            player.start();
        }
    }

    public void pause(){
        player.pause();
    }
}
