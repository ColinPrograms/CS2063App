package com.mobile.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "songs.db";
    public static final String TABLE_NAME = "songs_table";
    public static final String PLAYLISTS = "playlists_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Title";
    public static final String COL_3 = "Artist";
    public static final String COL_4 = "URI";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PLAYLISTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int addPlaylist(String playlistName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ PLAYLISTS + " where Title =" + playlistName ,null);
        if(cursor.getCount() > 0){
            return 2;
        }else{
            db.execSQL("create table " + playlistName + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT, Artist TEXT, URI TEXT)");
            return 1;
        }
    }

    public boolean insertSong(String title, String artist, String uri){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,title);
        contentValues.put(COL_3,artist);
        contentValues.put(COL_4,uri);
        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result== -1){
            return false;
        }else{
            return true;
        }
    }
    public Cursor getTableRows(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ tableName,null);
    return cursor;
    }
}
