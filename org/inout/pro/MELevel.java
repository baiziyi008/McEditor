package org.inout.pro;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;
import android.util.Log;

public class MELevel {
	private int gameType;
	private int generator = 0;
	private int dayCycleStopTime = -1;
	
	private ChangeMode	m_changeMode;
	
	public int getGameType(){
		return this.gameType;
	}
	
	public void setGameType(int gt){
		this.gameType = gt;
	}
	
	
	public void init() throws IOException{
		//is = getAssets().open("level.dat");
		int i=0;
		String [] strArray = new String[10];
		File file1 = new File(Environment
				.getExternalStorageDirectory(),
				"games/com.mojang/minecraftWorlds/惊险刺激的时光门");
		if (file1.exists()) {
			strArray[3] = file1.getAbsolutePath();
		}

		m_changeMode = new ChangeMode(strArray[3]);
		m_changeMode.init();
	}
}
