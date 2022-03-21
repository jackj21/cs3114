package cs3114.J3.CMD;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;

import cs3114.J3.DS.NameEntry;
import cs3114.J3.DS.HashTable;

/**
 * @author Jack Jiang
 * @version 3/21/21
 * 
 * Processes commands.
 */
public class CommandProcessor {
	private String gisFile;
	private long fileOffset;
	private Command command; 
	private FileWriter log; //log output file
	
	/**
	 * Creates new CommandProcessor with
	 * a provided output file to write to.
	 * @param fw log file to write processed commands to.
	 */
	public CommandProcessor(FileWriter fw) {
		this.log = fw;
	}
	
	/**
	 * Sets the command field.
	 * @param cmd Supplied command to be set to.
	 */
	public void setCommand(Command cmd) {
		this.command = cmd;
	}
	
	/**
	 * Processes the command containing keyword index.
	 */
	public HashTable<NameEntry> processIndex() throws IOException {
		HashTable<NameEntry> table = new HashTable<NameEntry>(null, 1.0);
		
		if (command.getCommand().contains("index")) {
			
				fileOffset = 0;
				boolean flag = true;
				gisFile = command.getName();
				RandomAccessFile gis = new RandomAccessFile(gisFile, "r");
				
				while(true) {
					
					gis.seek(fileOffset);
					if (flag) {
						gis.readLine(); //Skip header row
						flag = false;
					}
					fileOffset = gis.getFilePointer();
					String record = gis.readLine();
					
					if (record == null) break;
					
					String[] recs = record.split("\\|");
					
					String featureName = recs[1];
					Long featureOffset = fileOffset;
					
					fileOffset = gis.getFilePointer();
					
					NameEntry feature = new NameEntry(featureName, featureOffset);
					
					table.insert(feature);
					
				
					
				}
				
				gis.close();
			
		}
		
		
		
		return table;
	}
	
	/**
	 * Processes the debug command to display the HashTable.
	 * @param table HashTable to be displayed and printed.
	 */
	public void processDebug(HashTable<NameEntry> table) {
		try {
			table.display(log);
		} catch (IOException e) {
			System.err.println("Could not display HashTable.\n");
		}
	}
	
	public void processWhat(HashTable<NameEntry> table) {
		try {
			RandomAccessFile gis = new RandomAccessFile(gisFile, "r");
			
			
			
			String featureName = command.getName();
			
			int tableSlot = Hash(featureName) % table.getCapacity();
			
			
			
			ArrayList<Long> locations = table.get(tableSlot, featureName);
			if (locations.isEmpty()) {
				log.write("No record matches " + featureName + "\n");
				gis.close();
				return;
			}
			
			// *****************DEBUGGING********************
			//System.out.println(featureName + " Location: " + locations);
			
			for (int i=0; i<locations.size(); i++) {
				long fileOffset = locations.get(i);
				
				gis.seek(fileOffset);
				String record = gis.readLine();
				//System.out.println("record: " + record);
				String[] records = record.split("\\|");
				
				
				
				String featureClass = records[2];
				
				if (records[2] != null || !records[2].equals("Unknown")) {
					
				}
				String latDMS = records[7];
				String longDMS = records[8];
				
				String latClean = lat(latDMS);
				String longClean = lon(longDMS);
				
				log.write("   " + fileOffset + ":  " + featureClass + "  (" + longClean + 
						", " + latClean + ")\n"); 
			}
			
			
			
			
			gis.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("Could not find GIS record file.\n");
		} catch (IOException e) {
			System.err.println("Could not seek to GIs record file location.\n");
		}
	}
	
	public void quit() {
		
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private int Hash(String key) {
		int hashValue = key.length();
		for (int i = 0; i < key.length(); i++) {
			hashValue = ((hashValue << 5) ^ (hashValue >> 27)) ^ key.charAt(i);
		}
		return ( hashValue & 0x0FFFFFFF );
	}
	
	/**
	 * 
	 * @return
	 */
	private String lat(String latDMS) {
		String degrees = latDMS.substring(0,2);
		String minutes = latDMS.substring(2,4);
		if (Integer.parseInt(minutes) < 10) {
			minutes = latDMS.substring(3, 4);
		}
		String seconds = latDMS.substring(4,6);
		if (Integer.parseInt(seconds) < 10) {
			seconds = latDMS.substring(5, 6);
		}
		String direction = latDMS.substring(6);
		String dir = "";
		if (direction.equals("N")) dir = "North";
		
		if (direction.equals("S")) dir = "South";
		StringBuilder sb = new StringBuilder();
		sb.append(degrees + "d ");
		sb.append(minutes + "m ");
		sb.append(seconds + "s ");
		sb.append(dir );
		return sb.toString();
		
		
	}
	
	/**
	 * 
	 * @return
	 */
	private String lon(String longDMS) {
		String degrees = longDMS.substring(0,3);
		if (Integer.parseInt(degrees) < 100) {
			degrees = longDMS.substring(1, 3);
		}
		
		String minutes = longDMS.substring(3,5);
		if (Integer.parseInt(minutes) < 10) {
			minutes = longDMS.substring(4, 5);
		}
		
		String seconds = longDMS.substring(5,7);
		if (Integer.parseInt(seconds) < 10) {
			seconds = longDMS.substring(6, 7);
		}
		String direction = longDMS.substring(7);
		String dir = "";
		if (direction.equals("W")) dir = "West";
		
		if (direction.equals("E")) dir = "East";
		
		StringBuilder sb = new StringBuilder();
		sb.append(degrees + "d ");
		sb.append(minutes + "m ");
		sb.append(seconds + "s ");
		sb.append(dir );
		return sb.toString();

	}
	
	
}
