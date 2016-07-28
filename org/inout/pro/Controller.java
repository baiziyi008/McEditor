package org.inout.pro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

public class Controller {
	private DataInputStream	is;
	private DataOutputStream	os;
	
	private String flagStr = null;
	private final String NAME = "level.dat";
	private final String TMPNAME = "tmp_level.dat";
	private String path = null;
	private int size = 0;	//文件字节数
	private int cursor = 0;	//文件位置偏移
	
	private int gameType;
	private long time;
	private int[] curItem;
	
	private HashMap<String, Object> maps = new HashMap<String, Object>();
	private Controller(){
		
	}
	
	private static class SingleTonHolder{
		private static final Controller	Instance = new Controller();
	}
	
	public static final Controller getInstance(){
		return SingleTonHolder.Instance;
	}
	
	public void init(){
		
	}
	
	public void setPath(String strPath){
		this.path = strPath;
	}
	public String getPath(){
		return this.path;
	}
	private void setCurMode(int value){
		this.gameType = value;
	}
	//获取当前的模式   0:生存  1：创造
	public int getCurMode(){
		return this.gameType;
	}
	
	private void setCurTime(Long value){
		this.time = value;
	}
	//获取当前时间 1:白天   0：黑夜
	public long getCurTime(){
		//return (this.time>=0 && this.time<12537) ? 1 : 0;
		return this.time;
	}

	//////////////////////////////////////////////////////
	//更改当前模式
	public void changeCurMode() throws IOException{
		this.flagStr = "GameType";
		writeTag(this.flagStr);
	}
	
	//切换白天黑夜
	public void changeDayOrNight() throws IOException{
		this.flagStr = "Time";
		writeTag(this.flagStr);
	}
	public void loadData(){
		try {
			this.is = new DataInputStream(new FileInputStream(path+"//"+NAME));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.size = is.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cursor = 0;
		if (!this.maps.isEmpty())
			this.maps.clear();
		ReadTag(0);
	}
	public void ReadTag(int paramInt) {
		try {
			this.is.skip(11L);
			this.cursor = 11;
			while (cursor < size) {
				int i = 0xFF & this.is.readByte();
				int j = 0;
				byte[] arrayOfByte = null;
				if (i != 0) {
					j = 0xFFFF & this.is.readShort();
					if (true)
						j = Short.reverseBytes((short) j);
					arrayOfByte = new byte[j];
					this.is.readFully(arrayOfByte);
				}
				String str = new String(arrayOfByte, MEConstants.CHARSET.name());
				this.cursor += 3 + j;
				int [] array = this.ReadTagPayload(i, str, paramInt);
				array[2] = this.cursor;
				Log.e("mceditor", "cursor="+this.cursor+" value="+array[1]+" str="+str+" i="+i);
				this.maps.put(str, array);
				this.cursor += array[0];
			}

		} catch (Exception e) {
			Log.e("mceditor", "catch");
		} finally {
			try {
				this.is.close();
				Log.e("mceditor", "finally");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int [] ReadTagPayload(int paramInt1, String str, int paramInt2)
			throws IOException {
		
		int offset = 0;
		int value = 0;
		int [] array = new int [3];
		switch (paramInt1) {
		case 0:

			break;
		case 1:
			this.is.readByte();
			offset = 1;
			break;
		case 2:
			value = Short.reverseBytes(this.is.readShort());
			offset = 2;
			break;
		case 3:
			value = Integer.reverseBytes(this.is.readInt());
			offset = 4;
			break;
		case 4:
			value = (int) Long.reverseBytes(this.is.readLong());
			offset = 8;
			break;
		case 5:
			value = Integer.reverseBytes(this.is.readInt());
			offset = 4;
			break;
		case 6:
			value = (int) Long.reverseBytes(this.is.readLong());
			offset = 8;
			break;
		case 7:
			int j = Integer.reverseBytes(this.is.readInt());
			byte[] arrayofByte = new byte[j];
			this.is.readFully(arrayofByte);
			offset = 4 + j;
			break;
		case 8:
			int j1 = Short.reverseBytes(this.is.readShort());
			byte[] arrayofByte1 = new byte[j1];
			this.is.readFully(arrayofByte1);
			offset = 2 + j1;
			break;
		case 9:

			break;
		case 10:

			break;
		case 11:

			break;
		case 100:

			break;
		default:
			break;
		}
		
		array[0] = offset;
		array[1] = value;
		recordInfo(str, value);
		return array;
	}
	
	private void recordInfo(String str, int value){
		switch (str) {
		case "GameType":
			setCurMode(value);
			break;
		case "Time":
			setCurTime((long)value);
			break;
		default:
			break;
		}
	}
	
	public void writeTag(String str) throws IOException{
		try {
			//find value of maps
			for (Map.Entry<String, Object> item : this.maps.entrySet()) {
				Log.e("mceditor", "str="+item.getKey());
				if (item.getKey().equals(str)) {
					this.curItem = (int[])item.getValue();
					Log.e("mceditor", "value="+this.curItem[0]+"|"+this.curItem[1]+"|"+this.curItem[2]);
					break;
				}
			}
			
			Log.e("mceditor", "11111111111111111");
			this.cursor = (int)this.curItem[2];
			InputStream inputStream = new FileInputStream(path + "//" +NAME);

			OutputStream  outputStream = new FileOutputStream(path + "//" +TMPNAME);

			int bytesWritten = 0;

			byte[] bytes = new byte[this.size];
			inputStream.read(bytes, 0, this.cursor);
			this.os = new DataOutputStream(outputStream);
			this.os.write(bytes, 0, this.cursor);
			bytesWritten += this.cursor;
			
			Long skip = this.writeValueTag(str);
			Log.e("mceditor", "skip1="+skip+" byteswritten1="+bytesWritten);
			bytesWritten += skip;
			Log.e("mceditor", "skip2="+skip+" byteswritten2="+bytesWritten);
			inputStream.skip(skip);
			inputStream.read(bytes, bytesWritten, size - bytesWritten);
			this.os.write(bytes, bytesWritten, size - bytesWritten);

			inputStream.close();
			outputStream.close();
			this.os.close();
			
			new File(path + "//" +NAME).delete();
			new File(path + "//" +TMPNAME).renameTo(new File(path + "//" +NAME));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private Long writeValueTag(String str){
		Long skip = 0L;
		int newValue=0;
		switch (str) {
		case "GameType":
			//this.value = (int)this.curItem[1];
			int curType = getCurMode();
			newValue = curType == 1 ? 0 : 1;
			int j = Integer.reverseBytes(newValue);
			try {
				this.os.writeInt(j);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			skip = 4L;
			setCurMode(newValue);
			break;
		case "DayCycleStopTime":
			//newValue = this.value == -1 ? 5000 : -1;
			break;
		case "Time":
			//this.value = (int)this.curItem[1];
			long curTime = this.getCurTime();
			Long newValue1 = (curTime>=0 && curTime<12537) ? 12537L : 0L;
			Long jl = Long.reverseBytes(newValue1);
			try {
				this.os.writeLong(jl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			skip = 8L;
			setCurTime(newValue1);
			break;
		default:
			break;
		}
		
		return skip;
	}
}
