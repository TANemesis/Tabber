package com.tabber;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TabHost;

public class main extends TabActivity {
    /** Called when the activity is first created. */
	private static HashMap<String, Bitmap> albumArt;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setUpHashMap();
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, VideoMode.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("artists").setIndicator("Video Mode",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, AndroidBuildingMusicPlayerActivity.class);
        spec = tabHost.newTabSpec("albums").setIndicator("Jam Mode",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PlayMode.class);
        spec = tabHost.newTabSpec("songs").setIndicator("Play Mode",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        
        
    }
    
    private void setUpHashMap()
    {
    	albumArt = new HashMap<String, Bitmap>();
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor;
		String[] projection = {
		        MediaStore.Audio.Media.ALBUM,
		        MediaStore.Audio.Media.ALBUM_ID
		};

		cursor = managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);
		
		while(cursor.moveToNext())
		{
			String name = cursor.getString(0);
			int id = cursor.getInt(1);
			
			
			Bitmap bitmap = getAlbumart((long)id);
			if(bitmap!=null)
			{
				albumArt.put(name, bitmap);
			}
			
			
		}
		
    }
    
    public static Bitmap getArt(String name)
    {
    	return albumArt.get(name);
    }
    
    protected Bitmap getAlbumart(Long album_id) 
	   {
	        Bitmap bm = null;
	        try 
	        {
	            final Uri sArtworkUri = Uri
	                .parse("content://media/external/audio/albumart");

	            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

	            ParcelFileDescriptor pfd = this.getContentResolver()
	                .openFileDescriptor(uri, "r");

	            if (pfd != null) 
	            {
	                FileDescriptor fd = pfd.getFileDescriptor();
	                bm = BitmapFactory.decodeFileDescriptor(fd);
	            }
	    } catch (Exception e) {
	    }
	    if(bm==null)
	    {
	    	return null;
	    }
	    int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) 300) / width;
        float scaleHeight = ((float) 300) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);


        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
	}
    
    protected void putInHashMap(String aname, Bitmap aart)
    {
    	albumArt.put(aname, aart);
    }
    

}