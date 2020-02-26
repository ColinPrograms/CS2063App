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
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> artists = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(ArrayList<String> titlesIN,ArrayList<String> artistsIN,Context contextIN){
        titles = titlesIN;
        artists = artistsIN;
        context = contextIN;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_layoutitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
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
        ConstraintLayout parentLayout;
        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.song_nametxt);
            artist = itemView.findViewById(R.id.song_artisttxt);
            parentLayout = itemView.findViewById(R.id.constlayout);
        }

    }
}
