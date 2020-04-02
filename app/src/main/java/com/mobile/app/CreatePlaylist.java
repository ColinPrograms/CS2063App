package com.mobile.app;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    SongsAdapter adapter;
    String playlistName;
    EditText playlistTxt;

    private String lng;
    private String lat;
    private String rad;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_playlist);
        addSongs = findViewById(R.id.addSongsBtn);
        mydb = new DatabaseHelper(this);
        Intent fromIntent = getIntent();
        Bundle bundle = fromIntent.getExtras();
        playlistName = bundle.getString("name");
        playlistTxt = findViewById(R.id.playlistName);
        playlistTxt.setText(playlistName);
        Cursor cursor = mydb.getTableRows(playlistName);
        if(cursor != null && cursor.moveToFirst()){
            titles.add(cursor.getString(1));
            artists.add(cursor.getString(2));
            while(cursor.moveToNext()){
                titles.add(cursor.getString(1));
                artists.add(cursor.getString(2));
            }
        }
        recyclerView = findViewById(R.id.recviewcreate);
        adapter = new SongsAdapter(titles,artists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton savePlaylist = findViewById(R.id.savePlaylist);
        savePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CreatePlaylist.this, MapsActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        addSongs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(ContextCompat.checkSelfPermission(CreatePlaylist.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreatePlaylist.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, 1);
                }
            }
        });

        playlistTxt.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && !playlistName.contentEquals(playlistTxt.getText())) {
                    String text = playlistTxt.getText().toString();
                    if (text.equals("")) {
                        playlistTxt.setText(playlistName);
                        Toast t = Toast.makeText(getApplicationContext(), "Name Cannot be empty", Toast.LENGTH_SHORT);
                        t.show();
                    } else if (Character.isDigit(text.charAt(0))) {
                        playlistTxt.setText(playlistName);
                        Toast t = Toast.makeText(getApplicationContext(), "Playlist cannot start with a Number", Toast.LENGTH_SHORT);
                        t.show();
                    } else {
                        int result = mydb.changePlaylistName(playlistTxt.getText().toString(), playlistName);
                        if (result == 2) {
                            playlistTxt.setText(playlistName);
                            Toast t = Toast.makeText(getApplicationContext(), "That Playlist Already Exists", Toast.LENGTH_SHORT);
                            t.show();
                        }
                        if (result == 1) {
                            playlistName = playlistTxt.getText().toString();
                            hideKeyboard(CreatePlaylist.this);
                            v.clearFocus();
                        }
                    }
                }
                return true;
            }
        });
        playlistTxt.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !playlistName.contentEquals(playlistTxt.getText())) {
                    String text = playlistTxt.getText().toString();
                    if (text.equals("")) {
                        playlistTxt.setText(playlistName);
                        Toast t = Toast.makeText(getApplicationContext(), "Name Cannot be empty", Toast.LENGTH_SHORT);
                        t.show();
                    } else if (Character.isDigit(text.charAt(0))) {
                        playlistTxt.setText(playlistName);
                        Toast t = Toast.makeText(getApplicationContext(), "Playlist cannot start with a Number", Toast.LENGTH_SHORT);
                        t.show();
                    } else {
                        int result = mydb.changePlaylistName(playlistTxt.getText().toString(), playlistName);
                        if (result == 2) {
                            playlistTxt.setText(playlistName);
                            Toast t = Toast.makeText(getApplicationContext(), "That Playlist Already Exists", Toast.LENGTH_SHORT);
                            t.show();
                        }
                        if (result == 1) {
                            playlistName = playlistTxt.getText().toString();
                            hideKeyboard(CreatePlaylist.this);
                            v.clearFocus();
                        }
                    }
                }
                return true;
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(CreatePlaylist.this,PlaylistsActivity.class);
        startActivity(intent);
    }


    private class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
        private ArrayList<String> songTitles;
        private ArrayList<String> songArtists;

        public SongsAdapter(ArrayList<String> songTitlesIn,ArrayList<String> songArtistsIn){
            songTitles = songTitlesIn;
            songArtists = songArtistsIn;
        }
        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView songTitle;
            private TextView songArtist;
            private ViewHolder(ConstraintLayout v){
                super(v);
                songTitle = v.findViewById(R.id.song_nametxt);
                songArtist = v.findViewById(R.id.song_artisttxt);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.song_layoutitem,parent,false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.songTitle.setText(songTitles.get(position));
            holder.songArtist.setText(songArtists.get(position));

        }

        public int getItemCount() {
            return songTitles.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if (resultCode == RESULT_OK && requestCode == 1) {
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
        else if (resultCode == RESULT_OK && requestCode == 2) {
            lat = data.getStringExtra("lat");
            lng = data.getStringExtra("lng");
            rad = data.getStringExtra("rad");

            Log.d("Result", lat + lng + rad);

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
            boolean isInserted = mydb.insertSong(cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Media.TITLE)),cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Media.ARTIST)),uri.toString(),playlistName);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, 1);
                }
            }

        }
    }
}
