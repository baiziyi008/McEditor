package org.inout.pro;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



import android.util.Log;

public class ChangeMode {
	private DataInputStream	is;
	private DataOutputStream	os;
	
	private String flagStr = null;
	private final String NAME = "level.dat";
	private final String TMPNAME = "tmp_level.dat";
	private final String targetStr = "GameType";
	private String path = null;
	private int size = 0;	//文件字节数
	private int cursor = 0;	//文件位置偏移
	private int value = 0;		//当前设置的值
	
	private int gameType;
	private int time;
	
	/**
     * 转换游戏模式
     * param:strPath   level.dat所在绝对路径
     */
	public ChangeMode(String strPath){
		try {
			this.path = strPath;
			InputStream inputStream = new FileInputStream(path+"//"+NAME);
			this.is = new DataInputStream(inputStream);
			this.size = inputStream.available();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void init(){
		this.cursor = 11;
		this.value = 0;
		ReadTag(0);
	}
	
	public void process() throws IOException{
		
//		writeTag();
	}
	
	//获取当前的模式   0:生存  1：创造
	public int getCurMode(){
		return this.value;
	}
	//更改当前模式
	public void changeCurMode() throws IOException{
		this.flagStr = "GameType";
		writeTag(this.flagStr);
	}
	
	//获取当前时间 1:白天   0：黑夜
	public int getCurTime(){
		return (time>=0 && time<9600) ? 1 : 0;
	}
	
	//切换白天黑夜
	public void changeDayOrNight() throws IOException{
		this.flagStr = "Time";
		writeTag(this.flagStr);
	}
	
	public void ReadTag(int paramInt) {
		try {
			this.is.skip(11L);
			while (true) {
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
				Log.e("mceditor", "cursor="+this.cursor+" value="+array[1]+" str="+str+" i="+i);
				if (str.equals("Time")) {
					// record the point info
					this.value = array[1];
					this.is.close();
					return;
				}
				this.cursor += array[0];
				

			}

		} catch (Exception e) {
		} finally {
			try {
				this.is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int [] ReadTagPayload(int paramInt1, String str, int paramInt2)
			throws IOException {
		
		int offset = 0;
		int value = 0;
		int [] array = new int [2];
		switch (paramInt1) {
		case 0:

			break;
		case 1:
			this.is.readByte();
			offset = 1;
			break;
		case 2:
			this.is.readShort();
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
			this.is.readInt();
			offset = 4;
			break;
		case 6:
			this.is.readLong();
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
		return array;
	}
	
	public void writeTag(String str) throws IOException{
		try {
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
			newValue = this.value == 1 ? 0 : 1;
			int j = Integer.reverseBytes(newValue);
			try {
				this.os.writeInt(j);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			skip = 4L;
			break;
		case "DayCycleStopTime":
			newValue = this.value == -1 ? 5000 : -1;
			break;
		case "Time":
			Long newValue1 = (this.value>=0 && this.value<9600) ? 9600L : 0L;
			Long jl = Long.reverseBytes(newValue1);
			try {
				this.os.writeLong(jl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			skip = 8L;
			break;
		default:
			break;
		}
		
		return skip;
	}
}
