package com.mobile.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> titles;
    private ArrayList<String> artists;
    private Context context;
    private String type;

    public RecyclerViewAdapter(ArrayList<String> titlesIN,ArrayList<String> artistsIN,Context contextIN,String typeIn){
        titles = titlesIN;
        artists = artistsIN;
        context = contextIN;
        type = typeIn;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder holder;
        if(type.equals("songs")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layoutitem,parent,false);
            holder = new ViewHolder(view,"songs");
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_layoutitem,parent,false);
            holder = new ViewHolder(view,"playlists");
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.v("recycler","onBindviewholder called");
        holder.title.setText(titles.get(position));
        holder.artist.setText(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView artist;
        TextView playlistName;
        ConstraintLayout parentLayout;
        public ViewHolder(View itemView,String itemType){
                super(itemView);
                if(itemType.equals("songs")) {
                    title = itemView.findViewById(R.id.song_nametxt);
                    artist = itemView.findViewById(R.id.song_artisttxt);
                }else{
                    playlistName = itemView.findViewById(R.id.playlist_nametxt);
                }
                parentLayout = itemView.findViewById(R.id.constlayout);
        }

    }
}
