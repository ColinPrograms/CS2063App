package com.mobile.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
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
        if (resultCode == RESULT_OK) {
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                } catch (Exception e) {
                    Log.v("bork", "Exception: " + e.getMessage());
                }
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                int displayName2 = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                if(cursor != null && cursor.moveToFirst()){
                    Log.v("CreatePlaylist", cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME) + "");
                    Log.v("CreatePlaylist", cursor.getColumnName(displayName2) + "");
                    Log.v("CreatePlaylist", cursor.getString(displayName2) + "");
                }
                String selection = MediaStore.Audio.Media.DISPLAY_NAME+"=?";
                String[] selectionArgs = new String[1];
                selectionArgs[0] = cursor.getString(displayName2);
//                Log.v("CreatePlaylist", selectionArgs[0] + "");
                Cursor cursor2 = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, null);
                if(cursor2 != null && cursor2.moveToFirst()){
                    int displayName = cursor2.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                    Log.v("CreatePlaylist", cursor2.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME) + "");
                    Log.v("CreatePlaylist", cursor2.getColumnName(displayName) + "");
                    Log.v("CreatePlaylist", cursor2.getString(displayName) + "");
                }

                Log.v("CreatePlaylist", uri.toString() + "");

            } else {
                ClipData clipdata = data.getClipData();
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    Log.v("CreatePlaylist", clipdata.getItemAt(i).getUri() + "");
                }
            }
        }
    }
}
