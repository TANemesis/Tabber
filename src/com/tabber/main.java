package com.tabber;
/***************************************************
 * 
 * This is the main class
 * first class to get called when app starts up
 * 
 * 
 *************************************************/

//imports
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
    //hashmap to store the album art
	//static so music player class can access
	private static HashMap<String, Bitmap> albumArt;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//set content view to main
        setContentView(R.layout.main);
        
        //set up the album art hashmap
        //may move to thread later for faster boot
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

        //set tab to first one (video mode)
        tabHost.setCurrentTab(0);

        
        
    }
    /**
     * Grabs all the album art and album ids and puts in albumArt hashmap for later use
     * may want to put in thread for faster boot time
     * 
     **/
    private void setUpHashMap()
    {
    	//initialize the hashmap
    	albumArt = new HashMap<String, Bitmap>();
    	//selection means to only grab media from sdcard that is considered music
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    	//cursor to query sd card
		Cursor cursor;
		//what we want to return
		//album will be cursor[0]
		String[] projection = {
		        MediaStore.Audio.Media.ALBUM,
		        MediaStore.Audio.Media.ALBUM_ID
		};

		//run the query
		cursor = managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);
		
		//loop through cursor til the end
		while(cursor.moveToNext())
		{
			//get the name and id of each album
			String name = cursor.getString(0);
			int id = cursor.getInt(1);
			
			//get the album art as a bitmap
			Bitmap bitmap = getAlbumart((long)id);
			
			//add the picture to the hashmap if it exists
			if(bitmap!=null)
			{
				albumArt.put(name, bitmap);
			}
			
			
		}
		
    }
    
    /**
     * Returns the album art based on the album name
     **/
    public static Bitmap getArt(String name)
    {
    	return albumArt.get(name);
    }
    
    /**
     * 
     * @param album_id
     * @return the albumArt as a resized bitmap from the sdcard
     */
    protected Bitmap getAlbumart(Long album_id) 
	   {
    	
    		//initialize bitmap
	        Bitmap bm = null;
	        try 
	        {
	        	//find all the artwork
	            final Uri sArtworkUri = Uri
	                .parse("content://media/external/audio/albumart");
	            //create uri
	            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
	            //open pfd
	            ParcelFileDescriptor pfd = this.getContentResolver()
	                .openFileDescriptor(uri, "r");
	            //convert to bitmap
	            if (pfd != null) 
	            {
	                FileDescriptor fd = pfd.getFileDescriptor();
	                bm = BitmapFactory.decodeFileDescriptor(fd);
	            }
	    } catch (Exception e) {
	    }
	    //return not found
	    if(bm==null)
	    {
	    	return null;
	    }
	    //scale bitmap
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
    /**
     * 
     * @param aname
     * @param aart
     *  Insert album art into hashmap
     */
    protected void putInHashMap(String aname, Bitmap aart)
    {
    	albumArt.put(aname, aart);
    }
    

}