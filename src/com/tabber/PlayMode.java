package com.tabber;

/***************************************************
 * 
 * This class is used for the third mode
 * described as playmode by Ryan
 * 
 * 
 *************************************************/

//imports
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class PlayMode extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view
        setContentView(R.layout.playmode);
         
        //set up the play mode
        setUpPlayMode();
        
        //intialize the horizontal scroll view and timer
        final HorizontalScrollView temp = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
        Timer time = new Timer();
        TabTask task = new TabTask(temp);
        //set the timer to go off every 50 milliseconds
        time.scheduleAtFixedRate(task, 0, 50);
        
	
	}
	
	/**
	 * 
	 * @param hs
	 * @param x
	 * 
	 * Moves the tabe over by x amount
	 */
	protected void updateTab(final HorizontalScrollView hs, final int x)
	{
		//move tab over
        hs.postDelayed(new Runnable() {
           public void run() {
        	   
           	hs.smoothScrollTo(x, 0);
           
               //temp.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
           }
       }, 0);
	}
	
	/**
	 * Sets up the playmode
	 * will be much more intense later on
	 */
	private void setUpPlayMode()
	{
		//initialize layout
        LinearLayout stringNumber = (LinearLayout) findViewById(R.id.stringNumber);
        LinearLayout stringOne = (LinearLayout) findViewById(R.id.stringOne);
        LinearLayout stringTwo = (LinearLayout) findViewById(R.id.stringTwo);
        LinearLayout stringThree = (LinearLayout) findViewById(R.id.stringThree);
        LinearLayout stringFour = (LinearLayout) findViewById(R.id.stringFour);
        LinearLayout stringFive = (LinearLayout) findViewById(R.id.stringFive);
        LinearLayout stringSix = (LinearLayout) findViewById(R.id.stringSix);
        
        
        
        
        //create the tab lines
        for(int i = 0; i < 6; i++)
        {
        	TextView temp = new TextView(this);
        	temp.setText("|-14---------------14---------------15----------" +
        			"-----7-------8---------------17p14----17p14----17p14--" +
        			"--17p14----17p14----14---------------------" +
        			"-------14---------------14---------------15----------" +
        			"-----7-------8---------------17p14----17p14----17p14----17p14" +
        			"----17p14----14---------------------------");
            temp.setTextColor(Color.WHITE);
            stringNumber.addView(temp);
        
        }
	}
	
	/**
	 * 
	 * @author Robin
	 * Takes care of moving the tab
	 */
	class TabTask extends TimerTask {
	    //times member represent calling times.
	    private int times = 0;
	    final HorizontalScrollView myHS;
	    
	    public TabTask(final HorizontalScrollView hs)
	    {
	    	myHS = hs;
	    }
	 
	    public void run() {
	        times+=10;
	        updateTab(myHS, times);
	    }
	}

}
