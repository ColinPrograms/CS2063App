package com.mobile.app;

import com.google.android.gms.maps.model.LatLng;

public class PlaylistObject {
    private String playlistName;
    private Double lat;
    private Double lng;
    private Double rad;

    public PlaylistObject(String playlistName, Double lat, Double lng, Double rad){
        this.playlistName = playlistName;
        this.lat = lat;
        this.lng = lng;
        this.rad = rad;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Double getRad() {
        return rad;
    }
}
