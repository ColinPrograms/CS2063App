package com.mobile.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class PlaylistsActivity extends Activity {
    private ArrayList<String> playlists = new ArrayList<>();
    LinearLayout createPlaylist;
    DatabaseHelper mydb;
    RecyclerView recyclerView;
    PlaylistsAdapter adapter;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        createPlaylist = findViewById(R.id.createplaylist_linearlayout);
        mydb = new DatabaseHelper(this);
        Cursor cursor = mydb.getTableRows("playlists_table");
        if(cursor != null && cursor.moveToFirst()) {
            playlists.add(cursor.getString(1));
            while(cursor.moveToNext()){
                playlists.add(cursor.getString(1));
            }
        }
        recyclerView = findViewById(R.id.playlist_recyclerView);
        adapter = new PlaylistsAdapter(playlists);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialog = new Dialog(this);
    }
    private class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {
        private ArrayList<String> playlists;

        public PlaylistsAdapter(ArrayList<String> playlistTitleIn){
            playlists = playlistTitleIn;
        }
        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView playlist;
            private ViewHolder(ConstraintLayout v){
                super(v);
                playlist = v.findViewById(R.id.playlist_nametxt);
                v.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(PlaylistsActivity.this,CreatePlaylist.class);
                        intent.putExtra("name",playlist.getText().toString());
                        startActivity(intent);
                    }
                });
            }
        }

        public PlaylistsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.playlist_layoutitem,parent,false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(PlaylistsAdapter.ViewHolder holder, int position) {
             holder.playlist.setText(playlists.get(position));
        }

        public int getItemCount() {
            return playlists.size();
        }
    }

    public void createPlaylistClick(View v) {
        dialog.setContentView(R.layout.playlist_nameinput);
        dialog.show();
        Button createPlaylist = dialog.findViewById(R.id.goToCreatePlaylistBtn);
        Button cancelCreate = dialog.findViewById(R.id.cancelCreateBtn);

        cancelCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        createPlaylist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextInputEditText input = dialog.findViewById(R.id.playlistNameInputField);
                String text = input.getText().toString();

                if(text.equals("")){
                    TextView errorText = dialog.findViewById(R.id.errorText);
                    errorText.setText("Input Playlist Name");
                }else if(Character.isDigit(text.charAt(0))) {
                    TextView errorText = dialog.findViewById(R.id.errorText);
                    errorText.setText("Playlist cannot start with a Number");
                }else{
                    int result = mydb.addPlaylist(text);
                    Log.e("playlistsActivity","here");
                    if(result == 2){
                        TextView errorText = dialog.findViewById(R.id.errorText);
                        errorText.setText("That playlist already exists");
                    }else{
                        Intent intent = new Intent(PlaylistsActivity.this,CreatePlaylist.class);
                        intent.putExtra("name",text);
                        startActivity(intent);
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(PlaylistsActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
