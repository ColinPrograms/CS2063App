package com.mobile.app;

import android.database.Cursor;
import android.location.Location;

import java.util.ArrayList;

public class LocationFinder {

    public static ArrayList<PlaylistObject> createPlaylistArrayList(DatabaseHelper mydb, Location location){
       // DatabaseHelper mydb = new DatabaseHelper(this);
        ArrayList<PlaylistObject> playlistObjects = new ArrayList<PlaylistObject>();
        Cursor cursor = mydb.getTableRows("playlists_table");

        if(cursor != null && cursor.moveToFirst()) {

            String playlistName = cursor.getString(1);
            String[] playlistLocation = cursor.getString(2).split(" ");
            PlaylistObject newPlaylist = new PlaylistObject(playlistName, Double.valueOf(playlistLocation[0]), Double.valueOf(playlistLocation[1]) ,Double.valueOf(playlistLocation[2]));

            playlistObjects.add(newPlaylist);

            while(cursor.moveToNext()){
                
                playlistName = cursor.getString(1);
                playlistLocation = cursor.getString(2).split(" ");
                newPlaylist = new PlaylistObject(playlistName, Double.valueOf(playlistLocation[0]), Double.valueOf(playlistLocation[1]) ,Double.valueOf(playlistLocation[2]));

                playlistObjects.add(newPlaylist);
            }
        }

        return playlistObjects;
    }
    public static String findPlaylist(DatabaseHelper mydb, Location location){
        return nearestBubble(createPlaylistArrayList(mydb, location), location);
    }

    private static String nearestBubble(ArrayList<PlaylistObject> playlists, Location location){

        PlaylistObject closestPlaylist = playlists.get(0);
        double smallestDistance = 1000000000;
        float results[] = new float[10];
        for(int i = 0; i < playlists.size(); i++){

            Location.distanceBetween(playlists.get(i).getLat(), playlists.get(i).getLng(), location.getLatitude(), location.getLongitude(),results);

            double distance = results[0];

            distance -= playlists.get(i).getRad();
            if(distance < smallestDistance){
                smallestDistance = distance;
                closestPlaylist = playlists.get(i);
            }
        }
        return closestPlaylist.getPlaylistName();
    }

}
