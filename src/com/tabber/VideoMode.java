package com.tabber;
/***************************************************
 * 
 * This class is the first called video mode by Ryan
 * it will be the database of youtube videos
 * 
 * 
 *************************************************/

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Track;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoMode extends Activity {
	  /**
	   * The name of the server hosting the YouTube GDATA feeds
	   * NOT USED RIGHT NOW
	   * will use for custom youtube player
	   */
	  public static final String YOUTUBE_GDATA_SERVER = "http://gdata.youtube.com";
	  
	  public static final String USER_FEED_PREFIX = YOUTUBE_GDATA_SERVER
	  	+ "/feeds/api/users/";
	
	  /**
	   * The URL suffix of the test user's uploads feed
	   */
	  public static final String UPLOADS_FEED_SUFFIX = "/uploads";
	
	  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view
        setContentView(R.layout.videomode);
        
        //set backgorund color to white
        LinearLayout background = (LinearLayout) findViewById(R.id.scrollLL);
        background.setBackgroundColor(Color.WHITE);
        
        //list of video ids and song and artist names
        //will be pulled from database when that is ready
        String[] videoIDs = {"sENM2wA_FTg", "qQkBeOisNM0","sENM2wA_FTg", "qQkBeOisNM0","sENM2wA_FTg", "qQkBeOisNM0","sENM2wA_FTg", "qQkBeOisNM0"};
        String[] songNames = {"It's Time", "Some Nights","It's Time", "Some Nights","It's Time", "Some Nights","It's Time", "Some Nights"};
        String[] artistNames = {"Imagine Dragons", "fun.","Imagine Dragons", "fun.","Imagine Dragons", "fun.","Imagine Dragons", "fun."};
        
        //loop through all of the videos
        for(int i = 0; i < videoIDs.length; i++)
        {
        	//get the video id
        	final String videoID = videoIDs[i];
        	//create new linear layout and textview to display video information
        	LinearLayout layout = new LinearLayout(this);
            TextView textview = new TextView(this);
            //set text color and size
            textview.setTextColor(Color.BLACK);
            textview.setTextSize(17);
            //set the spacing around the text view
            textview.setPadding(10, 0, 5, 0);
            //set the text to artist name and song name
            textview.setText(songNames[i] + " - " + artistNames[i]);

    		//Bitmap bitmap = getPicture("http://img.youtube.com/vi/"
    		//		+videoID+"/default.jpg");
            //create new imageview to display thumbnail
    		ImageView imview = new ImageView(this);
    		//imview.setImageBitmap(bitmap);
    		//set the spacing around the imageview
    		imview.setPadding(0, 10, 0, 0);
            
    		//create a new AsyncTask to download the image
    		DownloadImage temp = new DownloadImage(imview);
    		//execute the task with this url to get the video thumbnail
    		temp.execute("http://img.youtube.com/vi/"+videoID+"/default.jpg");
    		
    		//set up the linear layout
            layout.setGravity(Gravity.CENTER_VERTICAL);
            //add views to layout
            layout.addView(imview);
            layout.addView(textview);
            //set spacing around layout
            layout.setPadding(10, 0, 0, 10);
            
            /**
             * Set the onclick listener for the image view
             * 
             * 
             */
            imview.setOnClickListener(new OnClickListener()
            {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					//start youtube activity
					playYoutubeVideo(videoID);
					
				}
            	
            });
            
            /**
             * Set the onclick listener for the textview
             * same as for above but if they click on the text instead
             *
             */
            textview.setOnClickListener(new OnClickListener()
            {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					//start youtube activity
					playYoutubeVideo(videoID);
				}
            	
            });
            
            //add the layout to the background view
            background.addView(layout);
            //create a line to go inbetween videos
            LinearLayout border = new LinearLayout(this);
            border.setLayoutParams(new LayoutParams(1000,1));
            //border.setPadding(0, 0, 0, 0);
            border.setBackgroundColor(Color.BLACK);
            background.addView(border);
        }
        
  
       /* try {
			searchSongsByTitle("It's Time", 10);
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

    }
	/**
	 * 
	 * @param imageURL
	 * @return picture
	 * 
	 * NOT CURRENTLY USED.
	 * moved to AsyncTask
	 */
	private Bitmap getPicture(String imageURL)
	{
		
		URL url = null;
		try {
			url = new URL(imageURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection connection;
		try {
			connection = url.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * 
	 * @param title
	 * @param results
	 * @throws EchoNestException
	 * 
	 * test echonet example
	 */
	public void searchSongsByTitle(String title, int results) throws EchoNestException {
		EchoNestAPI en = new EchoNestAPI("NFOSOJDBS0WMMPC1X");
		Params p = new Params();
		p.add("title", title);
		p.add("results", results);
		List<Song> songs = en.searchSongs(p);
		for (Song song : songs) {
			//dumpSong(song);
			//System.out.println(song.getTitle());
			//System.out.println(song.getArtistName());
			//System.out.println(song.getKey());
		}
}
	
	/**
	 * Set the thumbnail to the imageview
	 */
	private void setImage(Drawable drawable, ImageView mImageView)
	{
	    mImageView.setImageDrawable(drawable);
	}
	
	/**
	 * 
	 * @author Robin
	 *AsyncTask to download the thumbnails for the videos
	 *works a lot like a thread
	 *
	 */
	public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

		//image view of the video
		ImageView imView;
		
		/**
		 * Constructor
		 * @param im
		 */
		public DownloadImage(ImageView im)
		{
			this.imView = im;
		}
		
		/**
		 * This would be the run part of a thread
		 * takes in the address to download the thumbnail
		 */
	    @Override
	    protected Drawable doInBackground(String... arg0) {
	        // This is done in a background thread
	        return downloadImage(arg0[0]);
	    }

	    /**
	     * Called after the image has been downloaded
	     * -> this calls a function on the main thread again
	     */
	    protected void onPostExecute(Drawable image)
	    {
	        setImage(image, imView);
	    }


	    /**
	     * Actually download the Image from the _url
	     * @param _url
	     * @return
	     */
	    private Drawable downloadImage(String _url)
	    {
	        //Prepare to download image
	        URL url;        
	        BufferedOutputStream out;
	        InputStream in;
	        BufferedInputStream buf;

	        //BufferedInputStream buf;
	        try {
	            url = new URL(_url);
	            in = url.openStream();

	            /*
	             * THIS IS NOT NEEDED
	             * 
	             * YOU TRY TO CREATE AN ACTUAL IMAGE HERE, BY WRITING
	             * TO A NEW FILE
	             * YOU ONLY NEED TO READ THE INPUTSTREAM 
	             * AND CONVERT THAT TO A BITMAP
	            out = new BufferedOutputStream(new FileOutputStream("testImage.jpg"));
	            int i;

	             while ((i = in.read()) != -1) {
	                 out.write(i);
	             }
	             out.close();
	             in.close();
	             */

	            // Read the inputstream 
	            buf = new BufferedInputStream(in);

	            // Convert the BufferedInputStream to a Bitmap
	            Bitmap bMap = BitmapFactory.decodeStream(buf);
	            if (in != null) {
	                in.close();
	            }
	            if (buf != null) {
	                buf.close();
	            }
	            
	            if(bMap==null)
	    	    {
	    	    	return null;
	    	    }
	    	    int width = bMap.getWidth();
	            int height = bMap.getHeight();
	            float scaleWidth = ((float) 200) / width;
	            float scaleHeight = ((float) 180) / height;
	            // CREATE A MATRIX FOR THE MANIPULATION
	            Matrix matrix = new Matrix();
	            // RESIZE THE BIT MAP
	            matrix.postScale(scaleWidth, scaleHeight);


	            // RECREATE THE NEW BITMAP
	            Bitmap resizedBitmap = Bitmap.createBitmap(bMap, 0, 0, width, height, matrix, false);

	            return new BitmapDrawable(resizedBitmap);

	        } catch (Exception e) {
	            Log.e("Error reading file", e.toString());
	        }

	        return null;
	    }

	}
	/**
	 *  THIS IS WHERE YOU WANT TO SEND THE DATA TO THE GUITAR
	 *  Plays the youtube video in youtube app by default
	 * @param videoId
	 */
	private void playYoutubeVideo(String videoId)
	{
		startActivity(new Intent(Intent.ACTION_VIEW, 
				Uri.parse("http://www.youtube.com/watch?v=" + videoId)));
	}
	
	

}
