package com.tabber;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlayListActivity extends ListActivity {
	// Songs list
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

		SongsManager plm = new SongsManager();
		// get all songs from sdcard
		this.songsList = plm.getPlayList();

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor;
		String[] projection = {
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA
		};

		cursor = managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);


		while(cursor.moveToNext()){
		    String title = cursor.getString(0);
		    String data = cursor.getString(1);

			
			HashMap<String, String> song = new HashMap<String, String>();
			song.put("songTitle", title);
			song.put("songPath", data);
			
			// Adding each song to SongList
			songsListData.add(song);
			
		// return songs list array

	}


		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlistitem, new String[] { "songTitle" }, new int[] {
						R.id.songTitle });

		setListAdapter(adapter);

		// selecting single ListView item
		ListView lv = getListView();
		// listening to single listitem click
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting listitem index
				int songIndex = position;
				
				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						AndroidBuildingMusicPlayerActivity.class);
				// Sending songIndex to PlayerActivity
				in.putExtra("songIndex", songIndex);
				setResult(100, in);
				// Closing PlayListView
				finish();
			}
		});

	}
}
