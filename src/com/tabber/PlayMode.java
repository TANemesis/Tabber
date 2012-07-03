package com.tabber;

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
        setContentView(R.layout.playmode);
         
        setUpPlayMode();
        
        final HorizontalScrollView temp = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
        Timer time = new Timer();
        TabTask task = new TabTask(temp);
        time.scheduleAtFixedRate(task, 0, 10);
        
	
	}
	
	protected void updateTab(final HorizontalScrollView hs, final int x)
	{
		
        hs.postDelayed(new Runnable() {
           public void run() {
        	   
           	hs.smoothScrollTo(x, 0);
               //temp.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
           }
       }, 0);
	}
	
	private void setUpPlayMode()
	{
        LinearLayout stringNumber = (LinearLayout) findViewById(R.id.stringNumber);
        LinearLayout stringOne = (LinearLayout) findViewById(R.id.stringOne);
        LinearLayout stringTwo = (LinearLayout) findViewById(R.id.stringTwo);
        LinearLayout stringThree = (LinearLayout) findViewById(R.id.stringThree);
        LinearLayout stringFour = (LinearLayout) findViewById(R.id.stringFour);
        LinearLayout stringFive = (LinearLayout) findViewById(R.id.stringFive);
        LinearLayout stringSix = (LinearLayout) findViewById(R.id.stringSix);
        
        TextView temp = new TextView(this);
        temp.setTextColor(Color.WHITE);
        stringNumber.addView(temp);
        for(int i = 0; i < 1000; i++)
        {
        	temp.setText(temp.getText().toString() + "--" + String.valueOf(i));
        }
	}
	
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
