package com.tabber;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Track;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.util.ServiceException;



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
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
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

    		Bitmap bitmap = getPicture("http://img.youtube.com/vi/"
    				+videoID+"/default.jpg");
    		ImageView imview = new ImageView(this);
    		imview.setImageBitmap(bitmap);
    		imview.setPadding(0, 10, 0, 0);
            
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
        
  
        try {
			searchSongsByTitle("It's Time", 10);
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	

}
