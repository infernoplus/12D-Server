package twelveengine.network;



import java.io.*;

import twelveengine.Log;
import twelvelib.net.packets.*;
import twelveutil.FileUtil;

public class FileSender {
	public NetworkCore network;
	
	public int out;
	
	public int hash;
	public String path;
	public BufferedInputStream in;
	public File file;
	
	public int onByte;
	public int prt;
	public int ttl;
	
	public int timeOut;
	public boolean done;
	
	public FileSender(NetworkCore n, String f, int o, int h) {
		try {
			network = n;
			out = o;
			hash = h;
			path = f;
			file = new File(FileUtil.dir + f);
			FileInputStream fis;
			fis = new FileInputStream(FileUtil.dir + f);
			in = new BufferedInputStream(fis);
			
			if(file.length() > Integer.MAX_VALUE) {
				in.close();
				throw new Exception("FILE\" " + f + "\" TO LARGE TO SEND!");
			}
			
			prt = 0;
			ttl = (int) (file.length()/1024);
			onByte = 0;
			
			timeOut = 0;
			done = false;
			Log.log("Started upload of: " + path, "Network", 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void step() {
		timeOut++;
		if(timeOut > 9000)
			close();
	}
	
	public void sendFile() {
		try {
			timeOut = 0;
			if(onByte < file.length()) {
				if(onByte + 1024 > file.length()) {
					byte b[] = new byte[(int) (file.length() - onByte)];
					in.read(b, 0, (int) (file.length() - onByte));
					onByte += 1024;
					network.send(new Packet3Download(path, hash, b, prt, ttl), out);
					in.close();
					done = true;
					Log.log("Upload complete: " + path, "Network", 0);
				}
				else {
					byte b[] = new byte[1024];
					in.read(b, 0, 1024);
					onByte += 1024;
					network.send(new Packet3Download(path, hash, b, prt, ttl), out);
				}
				prt++;
			}
		}
		catch(Exception e) {
			Log.log("Failed to read:" + path, "Network", 2);
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			Log.log("Upload timed out: " + file + " "+ prt + "/" + ttl, "Network", 2);
			in.close();
			done = true;
		} catch (IOException e) {
			Log.log("Failed to close Upload: " + file + " "+ prt + "/" + ttl, "Network", 3);
			e.printStackTrace();
		}
	}
	
}