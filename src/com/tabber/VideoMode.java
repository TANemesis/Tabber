package com.tabber;

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
import android.widget.Spinner;
import android.widget.TextView;

public class VideoMode extends Activity {
	  /**
	   * The name of the server hosting the YouTube GDATA feeds
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
        setContentView(R.layout.videomode);
        LinearLayout background = (LinearLayout) findViewById(R.id.scrollLL);
        background.setBackgroundColor(Color.WHITE);
        
        
        String[] videoIDs = {"sENM2wA_FTg", "qQkBeOisNM0","sENM2wA_FTg", "qQkBeOisNM0","sENM2wA_FTg", "qQkBeOisNM0","sENM2wA_FTg", "qQkBeOisNM0"};
        String[] songNames = {"It's Time", "Some Nights","It's Time", "Some Nights","It's Time", "Some Nights","It's Time", "Some Nights"};
        String[] artistNames = {"Imagine Dragons", "fun.","Imagine Dragons", "fun.","Imagine Dragons", "fun.","Imagine Dragons", "fun."};
        
        for(int i = 0; i < videoIDs.length; i++)
        {
        	final String videoID = videoIDs[i];
        	LinearLayout layout = new LinearLayout(this);
            TextView textview = new TextView(this);
            textview.setTextColor(Color.BLACK);
            textview.setTextSize(17);
            textview.setPadding(10, 0, 5, 0);
            textview.setText(songNames[i] + " - " + artistNames[i]);

    		//Bitmap bitmap = getPicture("http://img.youtube.com/vi/"
    		//		+videoID+"/default.jpg");
    		ImageView imview = new ImageView(this);
    		//imview.setImageBitmap(bitmap);
    		imview.setPadding(0, 10, 0, 0);
            
    		DownloadImage temp = new DownloadImage(imview);
    		temp.execute("http://img.youtube.com/vi/"+videoID+"/default.jpg");
    		
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.addView(imview);
            layout.addView(textview);
            layout.setPadding(10, 0, 0, 10);
            
            imview.setOnClickListener(new OnClickListener()
            {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startActivity(new Intent(Intent.ACTION_VIEW, 
    						Uri.parse("http://www.youtube.com/watch?v=" + videoID)));
				}
            	
            });
            textview.setOnClickListener(new OnClickListener()
            {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startActivity(new Intent(Intent.ACTION_VIEW, 
    						Uri.parse("http://www.youtube.com/watch?v=" + videoID)));
				}
            	
            });
            
            background.addView(layout);
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
	
	
	private void setImage(Drawable drawable, ImageView mImageView)
	{
	    mImageView.setImageDrawable(drawable);
	}
	public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

		ImageView imView;
		
		public DownloadImage(ImageView im)
		{
			this.imView = im;
		}
		
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

	            return new BitmapDrawable(bMap);

	        } catch (Exception e) {
	            Log.e("Error reading file", e.toString());
	        }

	        return null;
	    }

	}
	

}
