package twelveengine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.lwjgl.Sys;

import twelveengine.Game;
import twelveengine.network.NetworkCore;
import twelveengine.ui.CommandLine;
import twelvelib.net.packets.Packet1Load;
import twelveutil.FileUtil;

public class Engine {
	
	/** That's the name of the game, baby! */
	public final String gameTitle = "12D Server Alpha";
	public final String version = "v0.8.5 Dev";
	/** Core Instances */
	public Console console;
	public NetworkCore network;
	public Game game;
	public CommandLine command;
	/** Menu variables */
	public boolean menuIsOpen = false;
	/** Kills the program upon enable */
	public boolean stopRuntime = false;
	/** BSP Step Variables */
	public long currentTime;
	private long lastFrame = getTime();
	
	public Thread renderer = null;

	/** Engine starts here! */
	public void start() {
		System.out.println("Loading engine...");
		gameFiles();
		console = new Console(this); //Stars the console, and then runs all commands listed in config.cfg
		network = new NetworkCore(this);
		command = new CommandLine(this);
		System.out.println("\nEngine ready!");
		console.initFile(); //Runs all commands listed in init.cfg
	}
	
	//TODO:Not sure if I like this... show it to ethan?
	public void gameFiles() {
		try {
			File c = new File(FileUtil.dir + "config.cfg");
			File n = new File(FileUtil.dir + "init.cfg");
			if(!c.exists()) {
				Log.log("Creating config files...", "Engine");
				InputStream i = FileUtil.getCfg("config.cfg");
				FileOutputStream o = new FileOutputStream(FileUtil.dir + "config.cfg");
			    int bytesRead;
			    byte[] buffer = new byte[8 * 1024];
			    while ((bytesRead = i.read(buffer)) != -1) {
			    	o.write(buffer, 0, bytesRead);
				}			    
				i.close();
				o.close();
			}
			if(!n.exists()) {
				n.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadGame(String s, String m[]) {
		FileUtil.setMods(m);
		if(network.online) {
			network.send(new Packet1Load(true, game.scenario.file, FileUtil.getMods()));
			network.step();
		}
		if(game != null)
			game.unloadGame();
		game = new Game(this, s);
		if(network.online)
			network.message("Server changing map:" + game.scenario.name);
	}

	/** Engine runs here! */
	public void run() {
		while(game == null && !stopRuntime) {
			command.step();
		}
		while (!stopRuntime) {
			command.step();
			currentTime = getTime();
			if (currentTime - lastFrame > Game.stepTime) {
				network.step(); //Get all new packets from clients and process dat shit
			    game.step(); //Do a game tick
			    game.netStep(network); //Write new packets and send that shit out to clients yo
		       	lastFrame = currentTime;
			}
		}
    }

	/** Engine ends here! */
	public void end() {
		System.out.println("\nStopping engine...");
		network.close();
		if(game != null)
			game.unloadGame();
		System.out.println("Game stopped!");
	}
	
	/** Pauses/Unpauses the game and enables/disables the menu. */
	public void toggleMenu() {
		
	}

	/** Get method for the current time.
	 * @return The time in milliseconds, stored as a long.
	 */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
}
