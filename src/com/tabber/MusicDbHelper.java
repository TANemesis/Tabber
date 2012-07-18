package com.tabber;

/***************************************************
 * 
 * Not used
 * different database class
 * may use later
 * 
 * 
 *************************************************/

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.MediaStore;
import android.util.Log;

public class MusicDbHelper {
    class Row extends Object {
        public long _Id;
        public String artist;
        public String title;
        public String data;
        public String display_name;
        public String duration;
        public String album;
    }

    private static final String DATABASE_CREATE =
        "CREATE TABLE IF NOT EXISTS SONGS(_id integer primary key, "
            + "artist text not null,"
            + "title text not null,"
            + "data text not null,"
            + "display_name text not null,"
            + "duration text not null,"
            + "album text not null"
            +");";

    private static final String DATABASE_NAME = "MUSICDB";

    private static final String DATABASE_TABLE = "SONGS";

    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public MusicDbHelper(Context ctx) {
    	try{
        db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        db.execSQL(DATABASE_CREATE);}
    	catch(SQLiteException e)
    	{
    		
    	}
        
    }

    public void close() {
        db.close();
    }

    public void createRow(int id, String artist, String title, String data, String display_name, String duration, String album) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("_id", id);
        initialValues.put("artist", artist);
        initialValues.put("title", title);
        initialValues.put("data", data);
        initialValues.put("display_name", display_name);
        initialValues.put("duration", duration);
        initialValues.put("album", album);
        db.insert(DATABASE_TABLE, null, initialValues);
    }

    public void deleteRow(long rowId) {
        db.delete(DATABASE_TABLE, "_id=" + rowId, null);
    }

    public List<Row> fetchAllRows() {
        ArrayList<Row> ret = new ArrayList<Row>();
        try {
            Cursor c =
                db.query(DATABASE_TABLE, new String[] {
                    "_id", "artist", "title", "data", "display_name", "duration"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Row row = new Row();
                row._Id = c.getLong(0);
                row.artist = c.getString(1);
                row.title = c.getString(2);
                row.data = c.getString(3);
                row.display_name = c.getString(4);
                row.duration = c.getString(5);
                row.album = c.getString(6);
                ret.add(row);
                c.moveToNext();
            }
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }
        return ret;
    }

    public Row fetchRow(long rowId) {
        Row row = new Row();
        Cursor c =
            db.query(DATABASE_TABLE, new String[] {
                    "_id", "artist", "title", "data", "display_name", "duration", "album"}, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            row._Id = c.getLong(0);
            row.artist = c.getString(1);
            row.title = c.getString(2);
            row.data = c.getString(3);
            row.display_name = c.getString(4);
            row.duration = c.getString(5);
            row.album = c.getString(6);
            return row;
        } else {
            row._Id = -1;
            
        }
        return row;
    }

    public void updateRow(int id, String artist, String title, String data, String display_name, String duration, String album) {
        ContentValues args = new ContentValues();
        args.put("_id", id);
        args.put("artist", artist);
        args.put("title", title);
        args.put("data", data);
        args.put("display_name", display_name);
        args.put("duration", duration);
        args.put("album", album);
        db.update(DATABASE_TABLE, args, "_id=" + id, null);
    }
    public Cursor GetAllRows() {
        try {
            return db.query(DATABASE_TABLE, new String[] {
                    "_id", "artist", "title", "data", "display_name", "duration", "album"}, null, null, null, null, null);
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
            return null;
        }
    }
    public Cursor showArtists()
    {
    	try {
            return db.rawQuery("SELECT DISTINCT _id FROM SONGS", null);
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
            return null;
        }
    }
}
