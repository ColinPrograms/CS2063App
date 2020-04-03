package com.mobile.app;

import android.location.Location;

import java.util.ArrayList;

public class LocationFinder {

    public static String findPlaylist(ArrayList<PlaylistObject> playlists){
        return nearestBubble(playlists);
    }

    private static String nearestBubble(ArrayList<PlaylistObject> playlists){

        PlaylistObject closestPlaylist = playlists.get(0);
        double smallestDistance = 1000000000;
        for(int i = 0; i < playlists.size(); i++){
            //double distance = Location.distanceBetween(playlists.get(i).getLat(), playlists.get(i).getLng()) ;
            double distance = 10;
            distance -= playlists.get(i).getRad();
            if(distance < smallestDistance){
                smallestDistance = distance;
                closestPlaylist = playlists.get(i);
            }
        }
        return closestPlaylist.getPlaylistName();
    }

}
