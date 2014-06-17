package twelveutil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import twelveengine.Engine;
import twelveengine.Log;

public class FileUtil {
	public static String dir = getDirectory();
	public static String mods[] = new String[0];
	public static boolean nix;
	
	public static InputStream getCfg(String f) throws FileNotFoundException {
		if(nix) {
			File re = new File(dir + f);
			if(re.exists())
				return new FileInputStream(dir + f);
			else
				return new DataInputStream(new FileInputStream("config.cfg"));
		}
		else
			return null;
	}
	
	public static InputStream getFile(String f) throws FileNotFoundException {
		if(nix) {
			int i = 0;
			while(i < mods.length) {
				InputStream in = unzip(dir + mods[i], f);
				if(in != null)
					return in;
				i++;
			}
			//If there is no mod that contains this, then load it from the jar.
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(f);
		}
		else {
			return null;
			//eww
		}
	}
	
	// Trys to extract <file> from <zip>. if it can't it will return null.
	public static InputStream unzip(String zip, String file) {
		try {
			FileInputStream fin = new FileInputStream(zip);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;

			Log.log("Decompress: " + zip + "." + file, "Engine ", 0);
			while ((ze = zin.getNextEntry()) != null) {

				if (ze.isDirectory()) {
					// SKIP!
				} 
				else {
					/**** Changes made below ****/
					if (ze.getName().toString().equals(file)) {
						return zin;
					} 
					else {
						//SKIP
					}
				}

				zin.closeEntry();
			}
			zin.close();
		} 
		catch (Exception e) {
			Log.log("Failed to unzip: " + zip + "." + file, "Engine", 2);
			e.printStackTrace();
		}
		return null;
	}

	public static String winblowsConvert(String s) {
		return null;
	}
	
	public static String getDirectory() {		
		try {	
			if(!System.getProperty("os.name").toLowerCase().contains("window")) {
				nix = true;
				String path = Engine.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String gameFolder = URLDecoder.decode(path, "UTF-8");
				Log.log("Server working directory: " + gameFolder, "Engine");
				return gameFolder;
			}
			else {
				nix = false;
				String path = Engine.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String gameFolder = URLDecoder.decode(path, "UTF-8");
				Log.log("Server working directory: " + gameFolder, "Engine");
				return gameFolder;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}
	
	public static void setMods(String m[]) {
		if(m.length < 1) {
			mods = m; 
			return;
		}
		int i = 0;
		String l = "";
		ArrayList<String> valid = new ArrayList<String>();
		while(i < m.length) {
			if(new File(dir + m[i]).exists()) {
				valid.add(m[i]);
				l += m[i] + ", ";
			}
			else {
				Log.log("Mod does not exist: " + m[i], "Engine", 2);
			}
			i++;
		}
		String v[] = valid.toArray(new String[valid.size()]);
		Log.log("Loading game with mods: " + l, "Engine", 0);
		mods = v;
	}
	
	public static String[] getMods() {
		return mods;
	}

}