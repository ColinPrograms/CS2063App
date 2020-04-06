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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class PlaylistsActivity extends Activity {
    private ArrayList<String> playlists = new ArrayList<>();
    ImageButton createPlaylist;
    DatabaseHelper mydb;
    RecyclerView recyclerView;
    PlaylistsAdapter adapter;
    Dialog dialog;
    Dialog editDelete;


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
        editDelete = new Dialog(this);
    }
    private class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {
        private ArrayList<String> playlists1;

        public PlaylistsAdapter(ArrayList<String> playlistTitleIn){
            playlists1 = playlistTitleIn;
        }
        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView playlist;
            private ConstraintLayout layout;
            private ViewHolder(ConstraintLayout v){
                super(v);
                playlist = v.findViewById(R.id.playlist_nametxt);
                layout = v;
            }
        }

        public PlaylistsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.playlist_layoutitem,parent,false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(PlaylistsAdapter.ViewHolder holder, final int position) {
            holder.playlist.setText(playlists.get(position).substring(1,playlists.get(position).length()-1));

            holder.layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    editDelete.setContentView(R.layout.playlist_edit_delete_dialog);
                    editDelete.show();
                    Button edit = editDelete.findViewById(R.id.editbtn);
                    Button delete = editDelete.findViewById(R.id.deletebtn);
                    TextView titletxt = editDelete.findViewById(R.id.titleTxt);
                    titletxt.setText(playlists.get(position).substring(1,playlists.get(position).length()-1));
                    edit.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(PlaylistsActivity.this,CreatePlaylist.class);
                            intent.putExtra("name",playlists.get(position));
                            startActivity(intent);
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            mydb.deletePlaylist(playlists.get(position));
                            editDelete.hide();
                            playlists = new ArrayList<>();
                            Cursor cursor = mydb.getTableRows("playlists_table");
                            if(cursor != null && cursor.moveToFirst()) {
                                playlists.add(cursor.getString(1));
                                while(cursor.moveToNext()){
                                    playlists.add(cursor.getString(1));
                                }
                            }
                            adapter = new PlaylistsAdapter(playlists);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            });
        }

        public int getItemCount() {
            return playlists1.size();
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
                String text = "[" + input.getText().toString() + "]";
                String textCheck = input.getText().toString();
                textCheck = textCheck.replace(" ", "");
                if(textCheck.length() == 0){
                    TextView errorText = dialog.findViewById(R.id.errorText);
                    errorText.setText("Playlist name cannot be empty");
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
        finish();
    }
}
