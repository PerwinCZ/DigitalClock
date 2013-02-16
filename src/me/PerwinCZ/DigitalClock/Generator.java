package me.PerwinCZ.DigitalClock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Generator {
	public static void start(Clock clock) {
		int mins = clock.getAddMinutes();
		boolean cd = clock.isCountdownEnabled();
		int cdt = clock.getCountdownTime();

		Generator.createBackup(clock);
		
		String hours = Generator.getRealNumbers(mins)[0];
		String minutes = Generator.getRealNumbers(mins)[1];
		String seconds = Generator.getRealNumbers(mins)[2];

		if(cd && cdt != 0 && cdt < 360000) {
			hours = Generator.getCountdownNumbers(cdt)[0];
			minutes = Generator.getCountdownNumbers(cdt)[1];
			seconds = Generator.getCountdownNumbers(cdt)[2];
			clock.setCountdownTime(cdt-1);
		} else if(cd && cdt == 0) {
			Events.plugin.getServer().getScheduler().cancelTask(Events.plugin.clockTasks.get(clock.getName()));
			Events.plugin.clockTasks.remove(clock.getName());
			clock.enableCountdown(false);
			CountdownEndEvent event = new CountdownEndEvent(clock);
			Events.plugin.getServer().getPluginManager().callEvent(event);
		}
		
		Generator.generatingSequence(clock, hours, minutes, seconds);
	}
	
	protected static String[] getCountdownNumbers(int cdt) {
		String hours = "00";
		String minutes = "00";
		String seconds = "00";
		int ho = (int) Math.floor(cdt/3600);
		int mi = (int) Math.floor((cdt-ho*3600)/60);
		int se = cdt-ho*3600-mi*60;
		if(ho < 100 && mi < 60 && se < 60) {
			hours = ho + "";
			if(ho < 10) {
				hours = "0" + hours;
			}
			minutes = mi + "";
			if(mi < 10) {
				minutes = "0" + minutes;
			}
			seconds = se + "";
			if(se < 10) {
				seconds = "0" + seconds;
			}
		}
    	String[] result = {hours, minutes, seconds};
    	return result;
	}
	
	protected static String[] getRealNumbers(int mins) {
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.MINUTE, mins);
    	String hours = new SimpleDateFormat("HH").format(cal.getTime()) + "";
    	String minutes = new SimpleDateFormat("mm").format(cal.getTime()) + "";
    	String seconds = new SimpleDateFormat("ss").format(cal.getTime()) + "";
    	String[] result = {hours, minutes, seconds};
    	return result;
	}
	
	protected static void generatingSequence(Clock clock, String hours, String minutes, String seconds) {
		Location l = clock.getStartBlockLocation();
		BlockFace b = clock.getDirection();
		Material m = clock.getMaterial();
		Material f = clock.getFillingMaterial();
		byte d = clock.getData();
		byte fd = clock.getFillingData();
		boolean ss = clock.shouldShowSeconds();
		boolean bl = clock.isBlinking();
		boolean blm = clock.isBlinkingChangerON();
		boolean ampm = clock.getAMPM();

    	String letter = null;
    	int newHours = 0;
    	if(ampm) {
    		newHours = Integer.parseInt(hours);
    		if(newHours > 12) {
    			newHours -= 12;
    			hours = Integer.toString(newHours);
    			if(newHours < 10) {
    				hours = "0" + hours;
    			}
    			letter = "P";
    		} else {
    			letter = "A";
    		}
    	}
    	
		Generator.generate(0, Character.digit(hours.charAt(0), 10), l, b, m, d, f, fd);
		Generator.generate(Events.plugin.getSettings().getInt("width") + 1, hours.charAt(1) - '0', l, b, m, d, f, fd);
		
		if(bl) { 
			if(blm) {
				Generator.generate(Events.plugin.getSettings().getInt("width")*2 + 1, 10, l, b, f, d, f, fd);
				clock.setBlinkingChanger(false);
			} else {
				Generator.generate(Events.plugin.getSettings().getInt("width")*2 + 1, 10, l, b, m, d, f, fd); 
				clock.setBlinkingChanger(true);
			}
		} else {
			Generator.generate(Events.plugin.getSettings().getInt("width")*2 + 1, 10, l, b, m, d, f, fd);
		}
		
		Generator.generate(Events.plugin.getSettings().getInt("width")*3 + 1, minutes.charAt(0) - '0', l, b, m, d, f, fd);
		Generator.generate(Events.plugin.getSettings().getInt("width")*4 + 2, minutes.charAt(1) - '0', l, b, m, d, f, fd);
		
		if(ss) {
			if(bl) { 
				if(blm) {
			     	Generator.generate(Events.plugin.getSettings().getInt("width")*5 + 2, 10, l, b, f, d, f, fd);
					clock.setBlinkingChanger(false);
				} else {
			     	Generator.generate(Events.plugin.getSettings().getInt("width")*5 + 2, 10, l, b, m, d, f, fd); 
					clock.setBlinkingChanger(true);
				}
			} else {
		     	Generator.generate(Events.plugin.getSettings().getInt("width")*5 + 2, 10, l, b, m, d, f, fd);
			}
			
	    	Generator.generate(Events.plugin.getSettings().getInt("width")*6 + 2, seconds.charAt(0) - '0', l, b, m, d, f, fd);
	    	Generator.generate(Events.plugin.getSettings().getInt("width")*7 + 3, seconds.charAt(1) - '0', l, b, m, d, f, fd);
	    	
	    	if(ampm && letter != null) {
	    		if(letter == "A") {
	    	    	Generator.generate(Events.plugin.getSettings().getInt("width")*8 + 4, 11, l, b, m, d, f, fd);
	    		} else {
	    	    	Generator.generate(Events.plugin.getSettings().getInt("width")*8 + 4, 12, l, b, m, d, f, fd);
	    		}
    	    	Generator.generate(Events.plugin.getSettings().getInt("width")*9 + 5, 13, l, b, m, d, f, fd);
	    	}
		} else {
	    	if(ampm && letter != null) {
	    		if(letter == "A") {
	    	    	Generator.generate(Events.plugin.getSettings().getInt("width")*5 + 3, 11, l, b, m, d, f, fd);
	    		} else {
	    	    	Generator.generate(Events.plugin.getSettings().getInt("width")*5 + 3, 12, l, b, m, d, f, fd);
	    		}
		    	Generator.generate(Events.plugin.getSettings().getInt("width")*6 + 4, 13, l, b, m, d, f, fd);
	    	}
		}
	}
	
	private static void generate(int i, int n, Location l, BlockFace b, Material m, byte d, Material f, byte fd) {
		World w = l.getWorld();
		for(int q = 0; q < Events.plugin.getSettings().getInt("width"); q++) {
			int x = l.getBlockX();
			int z = l.getBlockZ();
			if(b == BlockFace.NORTH) {
				z = z + q + i;
			} else if(b == BlockFace.EAST) {
				x = x - q - i;
			} else if(b == BlockFace.SOUTH) {
				z = z - q - i;
			} else {
				x = x + q + i;
			}
			for(int p = 0; p < Events.plugin.getSettings().getInt("height"); p++) {
				int y = (l.getBlockY()+p);
				Block newBlock = w.getBlockAt(x,y,z);
				String[] r = Events.plugin.getSettings().getString("num" + n).split(";");
				String[] r2 = new StringBuffer(r[q]).reverse().toString().split(",");
				if(r2[p].equals("1")) {
					newBlock.setType(m);
					newBlock.setData(d);
				} else {
					newBlock.setType(f);
					newBlock.setData(fd);
				}
			}
		}
	}
	
	public static void removeClock(Clock clock) {
		if(clock.isSomethingMissing()) {
			clock.reloadFromConfig();
		}
		Location l = clock.getStartBlockLocation();
		BlockFace b = clock.getDirection();
		World w = l.getWorld();
		
		File file = new File(new File("plugins/DigitalClock/terrainbackups"), clock.getName() + ".txt");
		BufferedReader br;
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (IOException e) {
			Events.plugin.console.severe(e + "");
		}
		
		int n = 0;
		for(int u = 0; u < getTotalWidth(clock); u++) {
			int x = l.getBlockX();
			int z = l.getBlockZ();
			if(b == BlockFace.NORTH) {
				z += u;
			} else if(b == BlockFace.EAST) {
				x -= u;
			} else if(b == BlockFace.SOUTH) {
				z -= u;
			} else {
				x += u;
			}
			for(int v = 0; v < Events.plugin.getSettings().getInt("height"); v++) {
				int y = l.getBlockY() + v;
				String toSplit = lines.get(n);
				String[] data = toSplit.split(":");
				w.getBlockAt(x,y,z).setType(Material.valueOf(data[0]));
				w.getBlockAt(x,y,z).setData((byte) Integer.parseInt(data[1]));
				n++;
			}
		}
		file.delete();
	}
	
	private static void createBackup(Clock clock) {
		File dir = new File("plugins/DigitalClock/terrainbackups");
		if(!dir.exists()) {
			if(dir.mkdir()) {
				Events.plugin.console.info("[DigitalClock] Directory terrainbackups has been successfully created.");
			} else {
				Events.plugin.console.info("[DigitalClock] Directory terrainbackups couldn't be created!");
			}
		}
		File file = new File(dir, clock.getName() + ".txt");
		if(!file.exists()) {
			try {
				Location l = clock.getStartBlockLocation();
				BlockFace b = clock.getDirection();
				World w = l.getWorld();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for(int u = 0; u < getTotalWidth(clock); u++) {
					int x = l.getBlockX();
					int z = l.getBlockZ();
					if(b == BlockFace.NORTH) {
						z += u;
					} else if(b == BlockFace.EAST) {
						x -= u;
					} else if(b == BlockFace.SOUTH) {
						z -= u;
					} else {
						x += u;
					}
					for(int v = 0; v < Events.plugin.getSettings().getInt("height"); v++) {
						int y = l.getBlockY() + v;
						String block;
						if(u == 0 && v == 0) {
							block = "AIR:0";
						} else {
							block = w.getBlockAt(x,y,z).getType().name() + ":" + w.getBlockAt(x,y,z).getData();
						}
						writer.write(block);
						writer.newLine();
					}
				}
				writer.close();
			} catch (IOException e) {
				Events.plugin.console.severe(e + "");
			}
		}
	}
	
	private static int getTotalWidth(Clock clock) {
		int start = 5 * Events.plugin.getSettings().getInt("width") + 3;
		if(clock.shouldShowSeconds()) {
			start += 3 * Events.plugin.getSettings().getInt("width") + 1;
		}
		if(clock.getAMPM()) {
			start += 2 * Events.plugin.getSettings().getInt("width") + 2;
		}
		return start;
	}
}
