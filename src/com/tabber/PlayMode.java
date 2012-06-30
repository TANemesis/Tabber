package com.tabber;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class PlayMode extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playmode);
        
        setUpPlayMode();

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
        
        for(int i = 0; i < 6; i++)
        {
        	for(int j = 0; j < 24; j++)
        	{
        			TextView temp = new TextView(this);
        			temp.setTextColor(Color.WHITE);
        			temp.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
        					LayoutParams.MATCH_PARENT, 1));
        			if(j>0)
        			{
        				if(i==0)
        				{
        					temp.setText(String.valueOf(j));
        				}
        				
        			}
        			stringOne.addView(temp);
        			
        		
        	}
        }
	}

}
