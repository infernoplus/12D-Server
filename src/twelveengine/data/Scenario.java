package twelveengine.data;

import java.util.ArrayList;

import twelveengine.Game;
import twelveutil.*;

//TODO: We need to throw an exception if the scenario tag doesn't exsist.

public class Scenario {
	public Game game;
	
	public String file;
	
	public String name;
	public String bsp;
	public String script;
	
	public ArrayList<String> playerTag;
	public ArrayList<String> startingEquipmentTag;
	
	public ArrayList<Vertex> playerSpawns; //Spawn points... add team spawns later
	public ArrayList<Vertex> flags; //Points for the script language to get coordinates of. Used to like, teleport stuff to a flag.
	
	public Scenario(Game w, String s) {
		game = w;
		file = s;
		
		playerTag = new ArrayList<String>();
		startingEquipmentTag = new ArrayList<String>();
		
		playerSpawns = new ArrayList<Vertex>();
		flags = new ArrayList<Vertex>();
		
		readScenario();
	}
	
	//TODO: Support for more stuff... but get rid of unneeded shit like player spawns on client side...
	public void readScenario() {
		Tag t = game.tagger.openTag(file);
		name = t.getProperty("name", "DEFAULT");
		bsp = t.getProperty("bsp", "NULL");
		script = t.getProperty("script", "NULL");
		
		int i = 0;
		TagSubObject tso = t.getObject("player_spawns");
		int j = tso.getTotalObjects();
		while(i < tso.getTotalObjects()) {
			playerSpawns.add(TagUtil.makeVertex(tso.getObject(i)));
			i++;
		}
		
		i = 0;
		tso = t.getObject("player_character");
		j = tso.getTotalObjects();
		while(i < tso.getTotalObjects()) {
			TagSubObject arry = tso.getObject(i);
			playerTag.add(arry.getProperty("tag", "null")); //TODO: Default? id? 
			i++;
		}
		
		i = 0;
		tso = t.getObject("flags");
		j = tso.getTotalObjects();
		while(i < j) {
			flags.add(TagUtil.makeVertex(tso.getObject(i)));
			i++;
		}
	}
	
	public String getPlayerTag() {
		return playerTag.get(0); //TODO: make this thing
	}
	
	public int getSpawnEquipmentCount() {
		return startingEquipmentTag.size(); //TODO: and this thing
	}
	
	public String getSpawnEquipmentTag(int i) {
		return startingEquipmentTag.get(i); //TODO: and this thing
	}
}
