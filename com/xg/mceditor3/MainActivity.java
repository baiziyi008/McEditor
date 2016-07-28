package com.xg.mceditor3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.inout.pro.ChangeMode;
import org.inout.pro.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Controller	controller;
	private Button button_change_mode;
	private Button button_change_day_night;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		InputStream is;
		//is = getAssets().open("level.dat");
		int i=0;
		String [] strArray = new String[10];
		File file1 = new File(Environment
				.getExternalStorageDirectory(),
				"games/com.mojang/minecraftWorlds/惊险刺激的时光门");
		if (file1.exists()) {
			strArray[3] = file1.getAbsolutePath();
		}
		Log.e("mceditor", "xx="+strArray[3].toString());
//		cm = new ChangeMode(strArray[3]);
//		cm.init();
		controller = Controller.getInstance();
		controller.setPath(strArray[3]);
		controller.loadData();
		
		button_change_mode = (Button)findViewById(R.id.button1);
		button_change_mode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					
					controller.changeCurMode();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		button_change_day_night = (Button)findViewById(R.id.button2);
		button_change_day_night.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					controller.changeDayOrNight();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
