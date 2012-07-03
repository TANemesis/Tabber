package com.tabber;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlayListActivity extends ListActivity {
	// Songs list
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	private EditText songNameEditText;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

		songNameEditText = (EditText) findViewById(R.id.songEditText);
		
		SongsManager plm = new SongsManager();
		// get all songs from sdcard
		this.songsList = plm.getPlayList();

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor cursor;
		String[] projection = {
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.ARTIST
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
		    String artist = cursor.getString(2);

			
			HashMap<String, String> song = new HashMap<String, String>();
			song.put("songTitle", title);
			song.put("songPath", data);
			song.put("artist", artist);
			
			// Adding each song to SongList
			songsListData.add(song);
			
		// return songs list array

	}


		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlistitem, new String[] { "songTitle", "artist" }, new int[] {
						R.id.songTitle, R.id.artistName });

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
		
		songNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            //<requery/filter your adapter then set it to your listview>
            	//AlterAdapter();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

	}
	
	/*private void AlterAdapter() {
        if (songNameEditText.getText().toString().isEmpty()) {
            partialNames.clear();
            adapter.notifyDataSetChanged();
        }
        else {
            partialNames.clear();
            for (int i = 0; i < searchNames.size(); i++) {
                if (searchNames.get(i).toString().toUpperCase().contains(nameCapture.getText().toString().toUpperCase())) {
                    partialNames.add(searchNames.get(i).toString());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }*/
}
