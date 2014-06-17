package twelveengine.network;

import java.util.ArrayList;

import twelveengine.Engine;
import twelveengine.Log;
import twelveengine.Settings;
import twelveengine.actors.*;
import twelveengine.data.*;
import twelvelib.net.*;
import twelvelib.net.packets.*;
import twelveutil.FileUtil;
import twelveutil.MathUtil;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryonet.*;

public class NetworkCore {
	public Engine engine;
	
	public Server server;
	
	public String name;
	public String motd;
	public String password;
	
	public int udp;
	public int tcp;
	
	public int limit;
	
	public boolean online;
	
	public long timeOut;
	
	public ArrayList<Packet> in; //Packets recieved from clients.
	public ArrayList<Packet> out; //Packets ready to send to clients.
	
	public ArrayList<FileSender> fs;
	private int fh;

	public NetworkCore(Engine a) {
		engine = a;
		timeOut = 5000;
		online = false;
		System.out.println("Network ready!");
	}

	
	public void step() {
		if(online) {
			while(in.size() > 0) {
				Packet p = in.get(0);
				try {
					int i = p.packetType();
					switch(i) {
					case 0:		packet0(p); break;
					case 1:		packet1(p); break;
					case 2:		packet2(p); break;
					case 3:		packet3(p); break;
					case 4:		packet4(p); break;
					case 5:		packet5(p); break;
					case 10: 	packet10(p); break;
					case 11:	packet11(p); break;
					case 12:	packet12(p); break;
					case 13:	packet13(p); break;
					case 20:	packet20(p); break;
					case 21:	packet21(p); break;
					case 30:	packet30(p); break;
					case 31:	packet31(p); break;
					case 32:	packet32(p); break;
					case 40:	packet40(p); break;
						default: System.err.println("Unknown packet from client! Type:" + i); break;
					}
				}
				catch(Exception e) {
					System.err.println("Bad packet from client threw Exception!");
					System.err.println("Reason:" + e.getMessage());
					e.printStackTrace();
				}
				in.remove(0);
			}
			int i = 0;
			while(i < fs.size()) {
				fs.get(i).step();
				if(fs.get(i).done) {
					fs.remove(i);
					i--;
				}
				i++;
			}
		}
	}
	
	public void register() {
		Kryo k = server.getKryo();
		Registrar.registerPackets(k);
	}
	
	public void start() {
		name = Settings.getString("netname");
		motd = Settings.getString("netmotd");
		password = Settings.getString("netpassword");
		tcp = Settings.getInt("nettcp");
		udp = Settings.getInt("netudp");
		limit = Settings.getInt("netlimit");
		System.out.println("- Server Information -");
		System.out.println("++ Name: " + name);
		System.out.println("++ MOTD: " + motd);
		System.out.println("++ Password: " + password);
		System.out.println("++ TCP Port: " + tcp);
		System.out.println("++ UDP Port: " + udp);
		System.out.println("++ Players: " + limit);
		System.out.println("- Starting Server -");
		if(!checkPort(tcp) || !checkPort(udp)) {
			System.err.println("## Ports are not open! Netstart fail.");
			return;
		}
		
		try {
    		in = new ArrayList<Packet>();
    		fs = new ArrayList<FileSender>();
			server = new Server();
			server.start();
			register();
			server.bind(tcp, udp);
			
			PacketListener l = new PacketListener(this);
		    server.addListener(l);
		
			online = true;
			System.out.println("- Server Running -");
		}
		catch(Exception e) {
			System.err.println("- Failed to start network server -");
			return;
		}
	}
	
	public void kick(String name, String reason) {
		Connection c;
		if((c = getConnection(name)) != null) {
			message("Kicked " + c.user + ". Reason: " + reason);
			c.close();
		}
		else
			System.out.println("No user by the name of " + name);
	}
	
	public void kick(int hash, String reason) {
		Connection c;
		if((c = getConnection(hash)) != null) {
			message("Kicked " + c.user + ". Reason: " + reason);
			c.close();
		}
		else
			System.out.println("No user by the name of " + name);
	}
	
	public void close() {
		server.close();
		online = false;
		System.out.println("- Server Stopped -");
	}
	
	/**Send a message to everyone on the server**/
	public void message(String m) {
		send(new Packet5Message("SERVER says: " + m));
		System.out.println("SERVER says: " + m);
	}
	/**Whisper a message to a specific player on the server by their name**/
	public void whisper(String m, String w) {
		Connection c;
		if((c = getConnection(w)) != null) {
			send(new Packet5Message("SERVER whispers: " + m), c.hashCode());
			System.out.println("SERVER whispers: \"" + m + "\" to " + c.user);
		}
		else
			System.out.println("There is no user named \"" + w + "\" on the server.");
	}
	
	/**Sends a packet to all connected clients**/
	public void send(Packet p) {
		int i = 0;
		while(i < server.getConnections().length) {
			server.getConnections()[i].sendTCP(p);
			i++;
		}
	}
	
	/**Sends a packet to a specific client that matches the hash given**/
	public void send(Packet p, int hash) {
		Connection c;
		if((c = getConnection(hash)) != null)
			c.sendTCP(p);
	}
	
	/**Sends a packet to a client by their name**/
	public void sendFile(String f, String p) {
		FileSender s = new FileSender(this, f, getConnection(p).hashCode(), fileHash());
		fs.add(s);
		s.sendFile();
	}
	
	/**Sends a packet to a client by their connection hash**/
	public void sendFile(String f, int h) {
		FileSender s = new FileSender(this, f, h, fileHash());
		fs.add(s);
		s.sendFile();
	}
	
	private int fileHash() {
		fh++;
		return fh;
	}
	
    //When the listener recieves a new packet it gives it to this method that then decides what to do with it.
    public void recieved(Packet p) {
		in.add(p);
	}
    
    /** Gets a connection by it's hash **/
    public Connection getConnection(int hash) {
		int i = 0;
		while(i < server.getConnections().length) {
			if(server.getConnections()[i].hashCode() == hash)
				return server.getConnections()[i];
			i++;
		}
		return null;
    }
    
    /** Gets a connection by it's username **/
    public Connection getConnection(String user) {
		int i = 0;
		while(i < server.getConnections().length) {
			if(server.getConnections()[i].user != null)
				if(server.getConnections()[i].user.equals(user))
					return server.getConnections()[i];
			i++;
		}
		return null;
    }
    
    /** Login **/
	private void packet0(Packet p) {
		Packet0Login l = (Packet0Login) p;
		if(server.getConnections().length >= limit) {
			send(new Packet1Load(false, "null", new String[0]), l.hash);
			System.out.println(l.username + "-" + l.hash + " was rejected: Server is full!");
			return;
		}
		if(l.username.isEmpty()) {
			send(new Packet1Load(false, "null", new String[0]), l.hash);
			System.out.println("Nameless foggit-" + l.hash + " was rejected: Blank name!");
			return;
		}
		//TODO: query master server if the user name is logged in, then allow/deny access to server. if in offline mode then allow access regardless.
		
		if(!password.isEmpty())
			if(!l.password.equals(password)) {
				send(new Packet1Load(false, "null", new String[0]), l.hash);
				System.out.println(l.username + "-" + l.hash + " was rejected: Wrong Password!");
				return;
			}
		
		send(new Packet1Load(true, engine.game.scenario.file, FileUtil.getMods()), l.hash);
		Connection c;
		if((c = getConnection(l.hash)) != null) {
			@SuppressWarnings("unused")
			Connection d;
			if((d = getConnection(l.username)) != null) {
				int i = 1;
				while((d = getConnection(l.username + "+" + i)) != null)
					i++;
				c.user = l.username + "+" + i;
			}
			else
				c.user = l.username;
		}
		System.out.println(l.username + "-" + l.hash + " connected.");
	}
    /** Load **/
	private void packet1(Packet p) {
		
	}
    /** Request Download **/
	private void packet2(Packet p) {
		Packet2RequestDownload r = (Packet2RequestDownload) p;
		sendFile(r.mod, r.hash);
	}
    /** Download **/
	private void packet3(Packet p) {
		Packet3Download d = (Packet3Download) p;
		int i = 0;
		while(i < fs.size()) {
			if(d.hash == fs.get(i).hash)
				fs.get(i).sendFile();
			i++;
		}
	}
    /** Join **/
	private void packet4(Packet p) {
		Packet4Join j = (Packet4Join) p;
		if(j.loadSucsess) {
			System.out.println(getConnection(j.hash).user + "-" + j.hash + " joined the game.");
			int i = 0;
			while(i < engine.game.actors.size()) {
				engine.game.actors.get(i).netState(this, j.hash);
				i++;
			}
		}
		else
			System.out.println(getConnection(j.hash).user + "-" + j.hash + " failed to join the game.");
	}
    /** Message **/
	private void packet5(Packet p) {
		Packet5Message m = (Packet5Message) p;
		String name = "null";
		Connection c;
		if((c = getConnection(m.hash)) != null)
			name = c.user;
		if(!m.whisper.isEmpty())
			if((c = getConnection(m.whisper)) != null) {
				send(new Packet5Message(name + " whispers: " + m.message), c.hashCode());
				System.out.println(name + " whispers: \"" + m.message + "\" to " + c.user);
				return;
			}
			else {
				whisper("There is no player named \"" + m.whisper + "\" on the server.", getConnection(m.hash).user);
				return;
			}
		
		send(new Packet5Message(name + " says: " + m.message));
		System.out.println(name + " says: " + m.message);
	}
	
	/** Instantiate **/
	private void packet10(Packet p) {
		
	}
	
	/** Request Object **/
	private void packet11(Packet p) {
		Packet11RequestActor r = (Packet11RequestActor) p;
		Actor a = engine.game.getActor(r.nid);
		if(a != null)
			a.netState(this, r.hash);
	}
	
	/** Destroy **/
	private void packet12(Packet p) {
		
	}
	
	/** Kill **/
	private void packet13(Packet p) {
		
	}
	
	/** Location **/
	private void packet20(Packet p) {
		//TODO: Deny ridiculous location packets from the host (anti cheat) EX: locatin packets for other players/objects, location packets that teleport or push the player unreasonably quick
		Packet20Location l = (Packet20Location) p;
		Actor a = getActorCheck(l.nid, l.hash);
		if(a != null) {
			a.setLocation(new Vertex(l.x, l.y, l.z));
			a.setVelocity(new Vertex(l.a, l.b, l.c));
		}
	}
	
	/** Rotation **/
	private void packet21(Packet p) {
		Packet21Rotation r = (Packet21Rotation) p;
		Actor a = getActorCheck(r.nid, r.hash);
		if(a != null)
			a.setRotation(new Quat(r.i, r.j, r.k, r.l));
	}
	
	/** Inventory **/
	private void packet30(Packet p) {
		
	}
	
	/** Pickup **/
	private void packet31(Packet p) {
		Packet31Pickup i = (Packet31Pickup) p;
		Actor a = getActorCheck(i.nid, i.hash);
		Pawn b = null;
		if(a.getType().contains("Pawn"))
			b = (Pawn) a;
		Actor c = getActorCheck(i.item, i.hash);
		Item d = null;
		if(c.getType().contains("Item"))
			d = (Item) c;
		if(b != null && d != null)
			b.pickup(d);
	}
	
	/** Drop **/
	private void packet32(Packet p) {
		Packet32Drop i = (Packet32Drop) p;
		Actor a = getActorCheck(i.nid, i.hash);
		Pawn b = null;
		if(a.getType().contains("Pawn"))
			b = (Pawn) a;
		Actor c = getActorCheck(i.item, i.hash);
		Item d = null;
		if(c.getType().contains("Item"))
			d = (Item) c;
		if(b != null && d != null)
			b.drop(d, MathUtil.add(b.location, new Vertex(0,0,b.eye)), MathUtil.multiply(b.look, b.toss), b.rotation);
	}

	/** Ready **/
	private void packet40(Packet p) {
		Packet40Ready r = (Packet40Ready) p;
		Actor a = engine.game.spawnPlayer(getConnection(r.hash).user);
		send(new Packet41Control(a.nid), r.hash);
	}
	
	//Gets an actor by its NID. If the actor doesnt exsist we send a packet to the client to destroy this fluke. Unlikely to happen, but always good to check.
	public Actor getActorCheck(int n, int hash) {
		Actor a = engine.game.getActor(n);
		if(a != null)
			return a;
		send(new Packet12Destroy(n), hash);
		return null;
	}
	
	public boolean checkPort(int p) {
		return true;
	}
}
