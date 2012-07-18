package com.tabber;
/***************************************************
 * 
 * First version.
 * use as a reference for AndroidBulidingMusicPlayerActivity
 * do not delete
 * 
 * 
 *************************************************/
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.tabber.MusicDbHelper.Row;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class JamMode extends Activity{
	protected static final int REFRESH = 0;
	private static final String PREFS_NAME = "TabberPrefs";
	MediaPlayer mMediaPlayer;
	Cursor cursor;
	DataBaseHelper myDbHelper;
	SharedPreferences settings;
    SharedPreferences.Editor editor;
    Handler makeJamMode;
    Handler makeNowPlaying;
    Handler hRefresh;
	
	
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jammode);
        
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        
        editor.putString("contentView", "jammode");
        editor.commit();
        
        mMediaPlayer = new MediaPlayer();
        
        makeJamMode = new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        	switch(msg.what){
        	     case REFRESH:
        	            /*Refresh UI*/
        	    	 String content = settings.getString("contentView", null);
        	    	 if(content.equals("jammode"))
        	    	 {
         	            setUpJamMode();		 
        	    	 }
        	    	 break;
        	   }
        	}
        	};
        	
        	makeNowPlaying = new Handler(){
            	@Override
            	public void handleMessage(Message msg) {
            	switch(msg.what){
            	     case REFRESH:
            	            /*Refresh UI*/
            	    	 String content = settings.getString("contentView", null);
            	    	 if(content.equals("nowplaying"))
            	    	 {
             	            setUpNowPlaying(settings.getString("song", ""), 
             	            		settings.getString("artist", ""), 
             	            		settings.getString("album", ""));		 
            	    	 }
            	    	 break;
            	   }
            	}
            	};
            	
            	hRefresh = new Handler(){
                	@Override
                	public void handleMessage(Message msg) {
                	switch(msg.what){
                	     case REFRESH:
                	            /*Refresh UI*/
                	    	 String content = settings.getString("contentView", null);
                	    	 //System.out.println(content);
                	    	 if(content.equals("nowplaying"))
                	    	 {
                 	            updateCurrent(mMediaPlayer.getCurrentPosition(), mMediaPlayer);		 
                	    	 }
                	    	 break;
                	   }
                	}
                	};
            	
                	
                	
            	makeJamMode.sendEmptyMessage(REFRESH);
        
    }
	
	public String[] determineKey(String song, String artist) throws EchoNestException
	{
		EchoNestAPI echoNest = new EchoNestAPI("NFOSOJDBS0WMMPC1X");
		Params p = new Params();
        p.add("title", song);
        p.add("results", 100);
        List<Song> songs = echoNest.searchSongs(p);
        if(!artist.equals(null) && !artist.equals(""))
        {
        	for(Song s : songs)
            {
            	if(s.getArtistName().toUpperCase().equals(artist.toUpperCase()))
            	{
            		int key = s.getKey();
            		int mode = s.getMode();
            		String result = new String();
            		switch (key) 
            		{
            		case 0:  result+="C";
            			break;
            		case 1:  result+="C-Sharp";
        				break;
            		case 2:  result+="D";
    					break;
            		case 3:  result+="E-Flat";
    					break;
            		case 4:  result+="E";
    					break;
            		case 5:  result+="F";
    					break;
            		case 6:  result+="F-Sharp";
    					break;
            		case 7:  result+="G";
    					break;
            		case 8:  result+="A-Flat";
    					break;
            		case 9:  result+="A";
    					break;
            		case 10:  result+="B-Flat";
    					break;
            		case 11:  result+="B";
    					break;
            		
            		}
            		
            		if(mode==0)
            		{
            			result+=" Minor";
            		}
            		else
            		{
            			result+=" Major";
            		}
            		String artistAndSong = new String(s.getTitle() + " - " + s.getArtistName());
            		String[] toReturn = {result, artistAndSong};
            		return toReturn;
            	}
            }
        }
        else
        {
        		Song s = songs.get(0);
        		int key = s.getKey();
        		int mode = s.getMode();
        		String result = new String();
        		switch (key)
        		{
        		case 0:  result+="C";
        			break;
        		case 1:  result+="C-Shapr";
    				break;
        		case 2:  result+="D";
					break;
        		case 3:  result+="E-Flat";
					break;
        		case 4:  result+="E";
					break;
        		case 5:  result+="F";
					break;
        		case 6:  result+="F-Sharp";
					break;
        		case 7:  result+="G";
					break;
        		case 8:  result+="A-Flat";
					break;
        		case 9:  result+="A";
					break;
        		case 10:  result+="B-Flat";
					break;
        		case 11:  result+="B";
					break;
        		
        		}
        		
        		if(mode==0)
        		{
        			result+=" Minor";
        		}
        		else
        		{
        			result+=" Major";
        		}
        		String artistAndSong = new String(s.getTitle() + " - " + s.getArtistName());
        		String[] toReturn = {result, artistAndSong};
        		return toReturn;
        	
        }
        
        String[] nullS = {"",""};
		return nullS;
	}
	
	private void determineSimilarArtist() throws EchoNestException
	{
		EchoNestAPI echoNest = new EchoNestAPI("NFOSOJDBS0WMMPC1X");
        List<Artist> artists = echoNest.searchArtists("Weezer");

        if (artists.size() > 0) {
            Artist weezer = artists.get(0);
            System.out.println("Similar artists for " + weezer.getName());
            for (Artist simArtist : weezer.getSimilar(10)) {
                System.out.println("   " + simArtist.getName());
            }
        }
		
	}
	
	private void listSongs() throws EchoNestException
	{
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
		        MediaStore.Audio.Media._ID,
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.DISPLAY_NAME,
		        MediaStore.Audio.Media.DURATION,
		        MediaStore.Audio.Media.ALBUM,
		        MediaStore.Audio.Media.ALBUM_ID
		};

		cursor = this.managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);

		List<String> songs = new ArrayList<String>();
		LinearLayout songList = (LinearLayout) findViewById(R.id.songList);

		while(cursor.moveToNext()){
			int id = Integer.valueOf(cursor.getString(0));
		    final String  a = cursor.getString(1).replaceAll("'", "///");
		    final String title = cursor.getString(2).replaceAll("'", "///");
		    String data = cursor.getString(3).replaceAll("'", "///");
		    String display = cursor.getString(4).replaceAll("'", "///");
		    String duration = cursor.getString(5).replaceAll("'", "///");
		    final String album = cursor.getString(6).replaceAll("'", "///");
			final TextView song = new TextView(this);
			song.setTextSize(20);
			song.setTextColor(Color.BLACK);
			song.setText(title);
			
			song.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					editor.putString("contentView", "nowplaying");
					editor.putString("song", title);
					editor.putString("artist", a);
					editor.putString("album", album);
					editor.commit();
					setContentView(R.layout.nowplaying);
					makeNowPlaying.sendEmptyMessage(REFRESH);
					findAndPlaySong(song.getText().toString());
					//setUpNowPlaying(title,
						//	a, album);
				}	
			});
			
			
			TextView artist = new TextView(this);
			artist.setTextSize(15);
			artist.setTextColor(Color.GRAY);
			artist.setText(cursor.getString(1));
			
			LinearLayout line = new LinearLayout(this);
			line.setBackgroundColor(Color.BLACK);
			line.setLayoutParams(new LayoutParams(1000,1));
			
	        int aid = cursor.getInt(7);
	        //Bitmap art = getAlbumart((long) aid);
	        //ImageView albumArt = new ImageView(this);
	        //System.out.println(a);
	        //if(art==null)
	        //{
	        //	albumArt.setImageResource(R.drawable.question);
	        //}
	        //else
	        //{
	        //	albumArt.setImageBitmap(art);
	        //}
	        
			
	        //songList.addView(albumArt);
			songList.addView(song);
			songList.addView(artist);
			songList.addView(line);
			
		    
			
		    songs.add(id + "||" + a +
		        		"||" +   title + "||" +   data + "||" 
		        		+  display + "||" +  duration + "||" + album);

		    
		    
		    myDbHelper.createRow(id, 
		    		a, 
		    		title, 
		    		data, 
		    		display, 
		    		duration,
		    		album);
		}
		
		
		
	}
	
	private void findAndPlaySong(String song)
	{
		cursor.moveToFirst();
		while(cursor.moveToNext())
		{
			if(cursor.getString(2).equals(song))
			{
				 String filename = cursor.getString(3);

                 try 
                 {
                	// System.out.println("Should Reset");
                	if (mMediaPlayer.isPlaying()) 
                	{
                		 //System.out.println("Resetting...");
                		 mMediaPlayer.reset();
                     }
                     mMediaPlayer.setDataSource(filename);
                     mMediaPlayer.prepare();
                     mMediaPlayer.start();
                     return;
                 } 
                 catch (Exception e) 
                 {

                 }
			
			}
		}
	}
	
	private void listSongsFromDatabase()
	{
		cursor = myDbHelper.GetAllRows();
		
		List<String> songs = new ArrayList<String>();
		LinearLayout songList = (LinearLayout) findViewById(R.id.songList);
		
		while(cursor.moveToNext()){
			final TextView song = new TextView(this);
			song.setTextSize(20);
			song.setTextColor(Color.BLACK);
			song.setText(cursor.getString(2));
			
			System.out.println(cursor.getString(2));
			
			final TextView artist = new TextView(this);
			artist.setTextSize(15);
			artist.setTextColor(Color.GRAY);
			artist.setText(cursor.getString(1));
			
			LinearLayout line = new LinearLayout(this);
			line.setBackgroundColor(Color.BLACK);
			line.setLayoutParams(new LayoutParams(1000,1));
			
			songList.addView(song);
			songList.addView(artist);
			songList.addView(line);
			
			song.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					setContentView(R.layout.nowplaying);
					findAndPlaySong(song.getText().toString().replaceAll("///", "'"));
					setUpNowPlaying(song.getText().toString().replaceAll("///", "'"),
							artist.getText().toString(), "AlbumName");
					
				}	
			});
			System.out.println("ALBUM: " + cursor.getString(6));
		    songs.add(cursor.getString(0) + "||" + cursor.getString(1) +
		        		"||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" 
		        		+  cursor.getString(4) + "||" +  cursor.getString(5) + "||" + cursor.getString(6));

		}
		
	}
	
	private void setUpNowPlaying(String sname, String sartist, String salbum)
	{
		TextView songName = (TextView) findViewById(R.id.songName);
		songName.setText(sname);
		TextView artistName = (TextView) findViewById(R.id.artistName);
		artistName.setText(sartist);
		TextView albumName = (TextView) findViewById(R.id.albumName);
		albumName.setText(salbum);
		TextView duration = (TextView) findViewById(R.id.songDuration);
		int millis = mMediaPlayer.getDuration();
		int i = (int) (TimeUnit.MILLISECONDS.toSeconds(millis) - 
	    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		
		if(i<10)
		{
			duration.setText(
					String.format("%d:0%d", 
						    TimeUnit.MILLISECONDS.toMinutes(millis),
						    i));
		}
		else
		{
			duration.setText(
					String.format("%d:%d", 
						    TimeUnit.MILLISECONDS.toMinutes(millis),
						    i)
						);
		}
		
		Button list = (Button) findViewById(R.id.listAllSongs);
		list.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editor.putString("contentView", "jammode");
				editor.commit();
				setContentView(R.layout.jammode);
				makeJamMode.sendEmptyMessage(REFRESH);
			}
			
		});
		
		final Button pause = (Button) findViewById(R.id.pauseButton);
		pause.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mMediaPlayer.isPlaying())
				{
					mMediaPlayer.pause();
					pause.setText("Play");
				}
				else
				{
					mMediaPlayer.start();
					pause.setText("Pause");
				}	
			}
			
		});
		
		//currentPos.setText("0:00");
		//final TextView currentPos = (TextView) findViewById(R.id.currentPosition);
		final SeekBar seekBar = (SeekBar) findViewById(R.id.currentTime);
		seekBar.setMax(mMediaPlayer.getDuration());
        seekBar.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			seekChange(arg0);
            return false;
		}
        });
         
        int id = getAlbumId(salbum);
        Bitmap art = getAlbumart((long) id);
        ImageView albumArt = (ImageView) findViewById(R.id.albumArt);
        albumArt.setImageBitmap(art);
        
        
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() 
        {
          @Override
          public void onPrepared(final MediaPlayer mp) 
          {
              seekBar.setMax(mp.getDuration());
                 new Thread(new Runnable() {

                         @Override
                         public void run() {
                        	 //System.out.println("Running...");
                                 while(mp!=null && mp.getCurrentPosition()<=mp.getDuration())
                                 {
                                	 boolean checkLoop = settings.getBoolean("isLoop2", false);
                                	 if(checkLoop)
                                	 {
                                		 int startTime = settings.getInt("startLoopTime", 0);
                                		 int endTime = settings.getInt("endLoopTime", mp.getDuration());
                                		 if(mp.getCurrentPosition()>=endTime)
                                		 {
                                			 mp.seekTo(startTime);
                                		 }
                                	 }
                                	 hRefresh.sendEmptyMessage(REFRESH);
                                     seekBar.setProgress(mp.getCurrentPosition());
                                     int millis = mp.getCurrentPosition();
                                     //updateCurrent(millis, mp);
                                     Message msg=new Message();

                                     msg.obj=millis/1000;
                                     //mHandler.sendMessage(msg);
                                      try {
                                          Thread.sleep(100);
                                      } 
                                      catch (InterruptedException e) {
                                         e.printStackTrace();
                                      }
                                 }
                         }
                 }).start();

          }
      });
		
		
		final SeekBar startLoop = (SeekBar) findViewById(R.id.startLoopTime);
		final SeekBar endLoop = (SeekBar) findViewById(R.id.endLoopTime);
		final TextView startText = (TextView) findViewById(R.id.startLoopText);
		final TextView endText = (TextView) findViewById(R.id.endLoopText);
		Button setLoop = (Button) findViewById(R.id.setLoopButton);
		Button endLoopButton = (Button) findViewById(R.id.endLoopButton);
		
		startLoop.setMax(mMediaPlayer.getDuration());
		endLoop.setMax(mMediaPlayer.getDuration());
		
		setLoop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(startLoop.getProgress()<endLoop.getProgress())
				{
					editor.putBoolean("isLoop2", true);
					//editor.putString("isLoop", "true");
					editor.putInt("startLoopTime", startLoop.getProgress());
					editor.putInt("endLoopTime", endLoop.getProgress());
					editor.commit();
					//make song repeat after this
					mMediaPlayer.seekTo(startLoop.getProgress());
				}
				else
				{
					//show error
				}
			}
			
		});
		
		endLoopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editor.putBoolean("isLoop2", false);
				editor.commit();
			}
			
		});
		
		startLoop.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int time, boolean arg2) {
				// TODO Auto-generated method stub
				int i = (int) (TimeUnit.MILLISECONDS.toSeconds(time) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
				
				if(i<10)
				{
					startText.setText(
							String.format("%d:0%d", 
								    TimeUnit.MILLISECONDS.toMinutes(time),
								    i));
				}
				else
				{
					startText.setText(
							String.format("%d:%d", 
								    TimeUnit.MILLISECONDS.toMinutes(time),
								    i)
								);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		endLoop.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int time, boolean arg2) {
				// TODO Auto-generated method stub
				int i = (int) (TimeUnit.MILLISECONDS.toSeconds(time) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
				
				if(i<10)
				{
					endText.setText(
							String.format("%d:0%d", 
								    TimeUnit.MILLISECONDS.toMinutes(time),
								    i));
				}
				else
				{
					endText.setText(
							String.format("%d:%d", 
								    TimeUnit.MILLISECONDS.toMinutes(time),
								    i)
								);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
	}
	
	private int getAlbumId(String name)
	{
		cursor.moveToFirst();
		while(cursor.moveToNext())
		{
			String n = cursor.getString(6);
			if(n.equals(name))
			{
				return cursor.getInt(7);
			}
		}
		return 0;
	}
	
	private void updateCurrent(int time, MediaPlayer mp)
	{
		final TextView currentPos = (TextView) findViewById(R.id.currentPosition);
		if(currentPos!=null)
		{
			int millis = mp.getCurrentPosition();
	         int i = (int) (TimeUnit.MILLISECONDS.toSeconds(millis) - 
	        		    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	        			
	        			if(i<10)
	        			{
	        				currentPos.setText(
	        						String.format("%d:0%d", 
	        							    TimeUnit.MILLISECONDS.toMinutes(millis),
	        							    i));
	        			}
	        			else
	        			{
	        				currentPos.setText(
	        						String.format("%d:%d", 
	        							    TimeUnit.MILLISECONDS.toMinutes(millis),
	        							    i)
	        							);
	        			}
		}
		 
        			
		
	}
	// This is event handler thumb moving event
    
	private void seekChange(View v){
        if(mMediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mMediaPlayer.seekTo(sb.getProgress());
        }
    }
	
    
	private void listArtistsFromDatabase()
	{
		cursor = myDbHelper.showArtists();
		
		List<String> songs = new ArrayList<String>();
		LinearLayout songList = (LinearLayout) findViewById(R.id.songList);
		
		while(cursor.moveToNext()){
			//final TextView song = new TextView(this);
			//song.setTextSize(20);
			//song.setTextColor(Color.BLACK);
			//song.setText(cursor.getString(1));
			
			//System.out.println(cursor.getString(1));
			
			final TextView artist = new TextView(this);
			artist.setTextSize(25);
			artist.setTextColor(Color.BLACK);
			artist.setText(cursor.getString(0));
			
			LinearLayout line = new LinearLayout(this);
			line.setBackgroundColor(Color.BLACK);
			line.setLayoutParams(new LayoutParams(1000,1));
			
			//songList.addView(song);
			songList.addView(artist);
			songList.addView(line);
			
			/*song.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					setContentView(R.layout.nowplaying);
					findAndPlaySong(song.getText().toString().replaceAll("///", "'"));
					setUpNowPlaying(song.getText().toString().replaceAll("///", "'"),
							artist.getText().toString(), "AlbumName");
					
				}	
			});*/
			
		    //songs.add(cursor.getString(0) + "||" + cursor.getString(1) +
		     //   		"||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" 
		      //  		+  cursor.getString(4) + "||" +  cursor.getString(5) + "||" + cursor.getString(6));

		}
		
	}
	private void listAlbumsFromDatabase()
	{
		cursor = myDbHelper.showAlbums();
		
		List<String> songs = new ArrayList<String>();
		LinearLayout songList = (LinearLayout) findViewById(R.id.songList);
		
		while(cursor.moveToNext()){
			//final TextView song = new TextView(this);
			//song.setTextSize(20);
			//song.setTextColor(Color.BLACK);
			//song.setText(cursor.getString(1));
			
			//System.out.println(cursor.getString(1));
			
			final TextView artist = new TextView(this);
			artist.setTextSize(25);
			artist.setTextColor(Color.BLACK);
			artist.setText(cursor.getString(0));
			
			LinearLayout line = new LinearLayout(this);
			line.setBackgroundColor(Color.BLACK);
			line.setLayoutParams(new LayoutParams(1000,1));
			
			//songList.addView(song);
			songList.addView(artist);
			songList.addView(line);
			
			/*song.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					setContentView(R.layout.nowplaying);
					findAndPlaySong(song.getText().toString().replaceAll("///", "'"));
					setUpNowPlaying(song.getText().toString().replaceAll("///", "'"),
							artist.getText().toString(), "AlbumName");
					
				}	
			});*/
			
		    //songs.add(cursor.getString(0) + "||" + cursor.getString(1) +
		     //   		"||" +   cursor.getString(2) + "||" +   cursor.getString(3) + "||" 
		      //  		+  cursor.getString(4) + "||" +  cursor.getString(5) + "||" + cursor.getString(6));

		}
		
	}

	private void listArtists() throws EchoNestException
	{
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
		        MediaStore.Audio.Media._ID,
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.DISPLAY_NAME,
		        MediaStore.Audio.Media.DURATION,
		        MediaStore.Audio.Media.ALBUM
		};

		cursor = this.managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);

		List<String> songs = new ArrayList<String>();
		LinearLayout songList = (LinearLayout) findViewById(R.id.songList);

		while(cursor.moveToNext()){
			int id = Integer.valueOf(cursor.getString(0));
		    String  a = cursor.getString(1).replaceAll("'", "///");
		    final String title = cursor.getString(2).replaceAll("'", "///");
		    String data = cursor.getString(3).replaceAll("'", "///");
		    String display = cursor.getString(4).replaceAll("'", "///");
		    String duration = cursor.getString(5).replaceAll("'", "///");
		    String album = cursor.getString(6).replaceAll("'", "///");
			
			if(!songs.contains(a))
			{
				final TextView artist = new TextView(this);
				artist.setTextSize(20);
				artist.setTextColor(Color.BLACK);
				artist.setText(a);
				
				artist.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						findAndPlaySong(title);
					}	
				});
				
				
				//TextView artist = new TextView(this);
				//artist.setTextSize(15);
				//artist.setTextColor(Color.GRAY);
				//artist.setText(cursor.getString(1));
				
				LinearLayout line = new LinearLayout(this);
				line.setBackgroundColor(Color.BLACK);
				line.setLayoutParams(new LayoutParams(1000,1));
				
				songList.addView(artist);
				//songList.addView(artist);
				songList.addView(line);
				
			    
				
			    songs.add(a);

			    
			    
			    myDbHelper.createRow(id, 
			    		a, 
			    		title, 
			    		data, 
			    		display, 
			    		duration,
			    		album);
			}
			
		}
		
		
		
	}

	private void listAlbums() throws EchoNestException
	{
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
		        MediaStore.Audio.Media._ID,
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.DISPLAY_NAME,
		        MediaStore.Audio.Media.DURATION,
		        MediaStore.Audio.Media.ALBUM
		};

		cursor = this.managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);

		List<String> songs = new ArrayList<String>();
		LinearLayout songList = (LinearLayout) findViewById(R.id.songList);

		while(cursor.moveToNext()){
			int id = Integer.valueOf(cursor.getString(0));
		    String  a = cursor.getString(1).replaceAll("'", "///");
		    final String title = cursor.getString(2).replaceAll("'", "///");
		    String data = cursor.getString(3).replaceAll("'", "///");
		    String display = cursor.getString(4).replaceAll("'", "///");
		    String duration = cursor.getString(5).replaceAll("'", "///");
		    String album = cursor.getString(6).replaceAll("'", "///");
			
			if(!songs.contains(a))
			{
				final TextView albums = new TextView(this);
				albums.setTextSize(20);
				albums.setTextColor(Color.BLACK);
				albums.setText(album);
				
				albums.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						findAndPlaySong(title);
					}	
				});
				
				
				//TextView artist = new TextView(this);
				//artist.setTextSize(15);
				//artist.setTextColor(Color.GRAY);
				//artist.setText(cursor.getString(1));
				
				LinearLayout line = new LinearLayout(this);
				line.setBackgroundColor(Color.BLACK);
				line.setLayoutParams(new LayoutParams(1000,1));
				
				songList.addView(albums);
				//songList.addView(artist);
				songList.addView(line);
				
			    
				
			    songs.add(a);

			    
			    
			    myDbHelper.createRow(id, 
			    		a, 
			    		title, 
			    		data, 
			    		display, 
			    		duration,
			    		album);
			}
			
		}
		
		
		
	}
	
	private void setUpJamMode()
	{
		editor.putString("contentView", "jammode");
        editor.commit();

        
        myDbHelper = new DataBaseHelper(this);
 
        try {
 
        	myDbHelper.createDataBase();
 
 	} catch (IOException ioe) {
 
 		throw new Error("Unable to create database");
 
 	}
 
 	try {
 
  		myDbHelper.openDataBase();
 
 	}catch(SQLException sqle){
 
 		throw sqle;
 
 	}
 	

        
 	LinearLayout songList = (LinearLayout) findViewById(R.id.songList);
		songList.setBackgroundColor(Color.WHITE);
        
		
		final Button listSong = (Button) findViewById(R.id.Songs);
		final Button listAlbum = (Button) findViewById(R.id.Albums);
		final Button listArtist = (Button) findViewById(R.id.Artists);
		
		listSong.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				listSong.setVisibility(View.GONE);
				listAlbum.setVisibility(View.GONE);
				listArtist.setVisibility(View.GONE);
				try {
					listSongs();
				} catch (EchoNestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		listArtist.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				listSong.setVisibility(View.GONE);
				listAlbum.setVisibility(View.GONE);
				listArtist.setVisibility(View.GONE);
				try {
					listArtists();
				} catch (EchoNestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		listAlbum.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				listSong.setVisibility(View.GONE);
				listAlbum.setVisibility(View.GONE);
				listArtist.setVisibility(View.GONE);
				try {
					listAlbums();
				} catch (EchoNestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	}
	
	public Bitmap getAlbumart(Long album_id) 
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
	    return bm;
	}
	
	protected void onPause(){
        super.onPause();

        // Necessary to clear first if we save preferences onPause. 
        //editor.clear();
        mMediaPlayer.pause();
        editor.putBoolean("isLoop2", false);
        editor.commit();
    }

}
