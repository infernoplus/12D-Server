package twelveengine.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AnimationGroup {
	public String file;
	public Animation[] animations;
	public AnimationGroup(String f) {
		file = f;
		try {
			tafImport(f);
		} catch (IOException e) {
			System.err.println("~! Failed to read animation file? - " + f);
			e.printStackTrace();
		}
	}
	
	public void tafImport(String s) throws IOException {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    int i = 0;
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("begin "))
	    		i++;
		    currentLine=fileReader.readLine();
	    }
	
	    animations = new Animation[i];
	    i = 0;
	    int j = 0;
	    
		fileIn = new DataInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(s));
	    fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("begin ")) {
	    		String name = currentLine.split(" ")[1];
	    		ArrayList<AnimationFrame> nfr = new ArrayList<AnimationFrame>();
			    currentLine=fileReader.readLine();
	    		while(!currentLine.startsWith("end")) {
	    			nfr.add(new AnimationFrame(currentLine));
				    currentLine=fileReader.readLine();
		    		i++;
	    		}
	    		i = 0;
	    		AnimationFrame afrms[] = new AnimationFrame[nfr.size()];
	    		while(i < nfr.size()) {
	    			afrms[i] = nfr.get(i);
	    			i++;
	    		}
	    		animations[j] = new Animation(name, afrms);
	    	    i = 0;
		    	j++;
	    	}
		    currentLine=fileReader.readLine();
	    }
	    System.out.print("Animation file built. Group contains " + animations.length + " animations. <");
	    i = 0;
	    while(i < animations.length) {
	    	System.out.print(animations[i].name + ":" + animations[i].frames.length + ",");
	    	i++;
	    }
    	System.out.println(">");
	    
	}

	public Animation getAnimation(String s) {
		int i = 0;
		while(i < animations.length) {
			if(animations[i].name.equals(s)) {
				return animations[i];
			}
			i++;
		}
		return animations[0];
	}
}