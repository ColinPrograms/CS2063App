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
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CreatePlaylist extends Activity {
    Button addSongs;
    Intent intent;
    DatabaseHelper mydb;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> artists = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_playlist);
        addSongs = findViewById(R.id.addSongsBtn);
        mydb = new DatabaseHelper(this);
        Cursor cursor = mydb.getTableRows("songs_table");
        if(cursor != null && cursor.moveToFirst()){
            titles.add(cursor.getString(1));
            artists.add(cursor.getString(2));
            while(cursor.moveToNext()){
                titles.add(cursor.getString(1));
                artists.add(cursor.getString(2));
            }
        }
        recyclerView = findViewById(R.id.recviewcreate);
        adapter = new RecyclerViewAdapter(titles,artists,this,"songs");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton savePlaylist = findViewById(R.id.savePlaylist);
        savePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CreatePlaylist.this, TagSelectActivity.class);
                startActivity(intent);
            }
        });

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
                addSong(uri);
            } else {
                ClipData clipdata = data.getClipData();
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    addSong(clipdata.getItemAt(i).getUri());
                }
            }
        }
    }

    public void addSong(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int displayName2 = 0;
        if(cursor.moveToFirst()){
            displayName2 = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        }
        String selection = MediaStore.Audio.Media.DISPLAY_NAME+"=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = cursor.getString(displayName2);
        String[] projection = new String[]{MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST};
        Cursor cursor2 = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
        if(cursor2 != null && cursor2.moveToFirst()){
            boolean isInserted = mydb.insertSong(cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Media.TITLE)),cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Media.ARTIST)),uri.toString());
            if(isInserted){
                Log.v("create: ","inserted");
                titles.add(cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                artists.add(cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                recyclerView.setAdapter(adapter);
            }else{
                Log.v("create: ","not inserted");
            }
        }
    }
}
