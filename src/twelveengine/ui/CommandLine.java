package twelveengine.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import twelveengine.Engine;
import twelveengine.Log;

public class CommandLine extends Thread {
	public Engine engine;
	public boolean run = true;
	public CommandLine(Engine g) {
		engine = g;
		start();
		Log.log("Command Line Ready!", "Console");
	}
	
	public void run() {
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		String i = "";
		while(!engine.stopRuntime && run) {
				try {
					i = in.readLine();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				s = i;
				n = true;
				yield();
		}
	}
	
	public boolean n = false;
	public String s = "";
	
	public void step() {
		if(n) {
			engine.console.parse(s);
			n = false;
		}
	}
}
