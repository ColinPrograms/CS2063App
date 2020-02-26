package com.mobile.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CreatePlaylist extends Activity {
    Button addSongs;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_playlist);
        addSongs = findViewById(R.id.addSongsBtn);

        addSongs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        Uri uri = data.getData();
        if(uri != null){
            try{
                getContentResolver().takePersistableUriPermission(uri,takeFlags);
            }catch(Exception e) {
                Log.v("bork", "Exception: " + e.getMessage());
            }
            Log.v("CreatePlaylist",uri.toString() + "");
        }else{
            ClipData clipdata = data.getClipData();
            for(int i = 0;i<clipdata.getItemCount();i++){
                Log.v("CreatePlaylist",clipdata.getItemAt(i).getUri() + "");
            }
        }

    }
}
