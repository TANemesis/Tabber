package com.tabber;

/***************************************************
 * 
 * This is the class that is called jam mode as per Ryan's description. 
 * I found the skeleton in an open source project
 * and modified it to fit our needs. It is a basic media player.
 * 
 * 
 *************************************************/

//imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidBuildingMusicPlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	//all of the variables to be used
	private ImageButton img_btnPlay;
	private ImageButton img_btnForward;
	private ImageButton img_btnBackward;
	private ImageButton img_btnNext;
	private ImageButton img_btnPrevious;
	private ImageButton img_btnPlaylist;
	private ImageButton img_btnRepeat;
	private ImageButton img_btnShuffle;
	private SeekBar songProgressBar;
	private TextView songArtistLabel;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private ImageView albumPicture;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String,String>> songsList = new ArrayList<HashMap<String,String>>();
	
	//called when class is started
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set content view to res->layout->player
		setContentView(R.layout.player);
		
		//initialize all the buttons based on player layout
		img_btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		img_btnForward = (ImageButton) findViewById(R.id.btnForward);
		img_btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		img_btnNext = (ImageButton) findViewById(R.id.btnNext);
		img_btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		img_btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		img_btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		img_btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		songArtistLabel = (TextView) findViewById(R.id.artistName);
		albumPicture = (ImageView) findViewById(R.id.albumPicture);
		
		//Initialize Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();
		utils = new Utilities();
		
		// Initialize Listeners
		//listeners are used to move seekbar and determine when a song is done
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		
		// Getting list of all songs
		//they will be returned in a cursor with 0 being the artist name etc.
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor;
		String[] projection = {
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.DISPLAY_NAME,
		        MediaStore.Audio.Media.ALBUM,
		        MediaStore.Audio.Media.ALBUM_ID
		};
		//query based on the projection listed above
		cursor = managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);

		//loop through all the songs
		while(cursor.moveToNext()){
			
			//set the strings to the song information to put in the hashmap
			String artist = cursor.getString(0);
			String data = cursor.getString(2);
			String title = cursor.getString(1);
			String album = cursor.getString(4);
			int id = cursor.getInt(5);
		    
		    
			//insert into the hashmap
			HashMap<String, String> song = new HashMap<String, String>();
			song.put("songTitle", title);
			song.put("songPath", data);
			song.put("albumTitle", album);
			song.put("artistName", artist);
			song.put("albumID", String.valueOf(id));
			//songs.add(artist);
			//songs.add(data);
			//songs.add(title);
			//songs.add(album);
			//songs.add(String.valueOf(id));
			
			// Adding each song to SongList
			songsList.add(song);
			
		

	}
		
		// By default play first song
		playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		img_btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						img_btnPlay.setImageResource(R.drawable.img_btn_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						img_btnPlay.setImageResource(R.drawable.img_btn_pause);
					}
				}
				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		img_btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		img_btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					mp.seekTo(0);
				}
				
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		img_btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(isShuffle){
					// shuffle is on - play a random song
					Random rand = new Random();
					currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
					playSong(currentSongIndex);
				}
				else if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}
				
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		img_btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
				
			}
		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		img_btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isRepeat){
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					img_btnRepeat.setImageResource(R.drawable.img_btn_repeat);
				}else{
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					img_btnRepeat.setImageResource(R.drawable.img_btn_repeat_pressed);
					img_btnShuffle.setImageResource(R.drawable.img_btn_shuffle);
				}	
			}
		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		img_btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isShuffle){
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					img_btnShuffle.setImageResource(R.drawable.img_btn_shuffle);
				}else{
					// make repeat to true
					isShuffle= true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					img_btnShuffle.setImageResource(R.drawable.img_btn_shuffle_pressed);
					img_btnRepeat.setImageResource(R.drawable.img_btn_repeat);
				}	
			}
		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		img_btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivityForResult(i, 100);			
			}
		});
		
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 currentSongIndex = data.getExtras().getInt("songIndex");
         	 // play selected song
             playSong(currentSongIndex);
        }
 
    }
	
	/**
	 * Function to play a song based on the songs index
	 * @param songIndex - index of song
	 * 
	 * 
	 * 
	 * THIS IS WHERE YOU WOULD WANT TO SEND THE DATA TO THE GUITAR IN JAM MODE!
	 * 
	 * 
	 * 
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
			//reset the media player
        	mp.reset();
        	//you have to set the path to the data part of what the cursor returned
        	//which is songPath in the hashmap
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			//always prepare the mediaplayer
			mp.prepare();
			//play the song
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
        	songTitleLabel.setText(songTitle);
        	
			//display the artist name
        	String artist = songsList.get(songIndex).get("artistName");
        	songArtistLabel.setText(artist);
        	
        	// Changing Button Image to pause image
			img_btnPlay.setImageResource(R.drawable.img_btn_pause);

			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			//set the album art
			String album = songsList.get(songIndex).get("albumTitle");
			Bitmap albumArt = main.getArt(album);
			
			//if there is no art, set it to the default android picture
			if(albumArt!=null)
			{
				albumPicture.setImageBitmap(albumArt);
			}
			else
			{
				albumPicture.setImageResource(R.drawable.adele);
			}
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   //get the duration and current position
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 *
	 * WILL USE TO SET THE LOOP
	 * DO NOT DELETE
	 * 
	 * 
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		//get duration and current position
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
	//when class goes away, stop the music
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	 }
	
}