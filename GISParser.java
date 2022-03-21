// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, including the Internet, either
// modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the supplied grading code.
//
// <Jack Jiang>
// <jackj21>

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
/**
 * @author Jack Jiang
 * @version 1/26/2021
 * 
 *
 */
public class GISParser {
		
		
	public static void main(String args[]) {
		if (args.length < 3 || args.length > 4) {
			System.out.println("Incorrect number of arguments. Please try again.");
			System.exit(0);
		}
		try {
			long offset = 0;
			
			
			RandomAccessFile data = new RandomAccessFile(args[1], "r");
			data.readLine();						//Skip first line the header
			offset = data.getFilePointer();		//Get the offset of the first line of records
			
			
			
			
			if (args[0].equals("-index")) {
				File out = new File(args[2]);
				FileWriter writer = new FileWriter(out);
				writer.write(args[1] + " contains the following records:\n\n");
				
				
				while (true) {
					try {		
					String line = data.readLine();
					if (line == null) break;
					Scanner scanner = new Scanner(line);
					scanner.useDelimiter("\\|");
					scanner.next();					//Skip ID to get pointer to name of record
					String name = scanner.next();	//Get name of record to write into output file
					
					writer.write(offset + "\t" + name + "\n");
					
					offset = data.getFilePointer();
					scanner.close();
					} catch (EOFException e) {
						break;
					}
				}
				
				writer.close();
			}
			
			else if (args[0].equals("-search")) {
				RandomAccessFile com = new RandomAccessFile(args[2], "r");
				File out = new File(args[3]);
				FileWriter writer = new FileWriter(out);
				int numCommands = 1;
				while (true) {
					try {
						
						String line = com.readLine();
						if (line == null || line.contains("quit")) {
							writer.write(numCommands + ": quit\n\tExiting");
							break;
						}
						if (line.contains(";")) continue;
						
						String[] lineSplit = line.split("\t");
						
						String command = lineSplit[0];
						
						String commandOffset = (lineSplit[1]);
						Long cOff = Long.valueOf(commandOffset);
						writer.write(numCommands + ": " + command + "\t" + commandOffset + "\n");
						numCommands += 1;
						
						
						if (cOff < 0) {
							writer.write("\tOffset is not positive\n");
							continue;
						
						}
						if (cOff > data.length()) {
							writer.write("\tOffset is too large\n");
							continue;
						}
						
						data.seek(cOff);
						char seekFirst = data.readChar();
						data.seek(cOff);
						String lineTest = data.readLine();
						data.seek(cOff);
						System.out.println("Linetest: " + lineTest);
						
						char readFirst = lineTest.charAt(0);
						System.out.println("seek char: " + seekFirst);
						System.out.println("read char: " + readFirst);
						if (Character.compare(seekFirst, readFirst) != 0) {
							writer.write("\tOffset is unaligned\n");
							continue;
						}
						
						if (command.contains("name")) {
							data.seek(cOff);
							
							String nameLine = data.readLine();
							String[] nameLineSplit = nameLine.split("\\|");
							String name = nameLineSplit[1];
							writer.write("\t" + name + "\n");
							continue;
						}
						
						if (command.contains("elevation")) {
							data.seek(cOff);
							String readElev = data.readLine();
							String[] elev = readElev.split("\\|");
						
							if (elev[16] == null || !elev[16].matches("[0-9]+")) {
								writer.write("\tElevation is not given\n");
								continue;
							}
							int elevation = Integer.parseInt(elev[16]);
							writer.write("\t" + elevation + "\n");
							continue;
					
						}
							
						if (command.contains("longitude")) {
							data.seek(cOff);
							String readLong = data.readLine();
							String[] longSplit = readLong.split("\\|");
							if (longSplit[8] == null || longSplit[8].equals("Unknown")) {
								writer.write("\tCoordinate is not given\n");
								continue;	
							}
							String longDMS = longSplit[8];
							//StringBuilder sb = new StringBuilder();
							String degrees = longDMS.substring(0,3);
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
							
							
							writer.write("\t" + degrees + "d " + minutes + "m " + 
									seconds + "s " + dir + "\n");
							
							continue;
						}
						
						if (command.contains("latitude")) {
							data.seek(cOff);
							String readLat = data.readLine();
							String[] latSplit = readLat.split("\\|");
							if (latSplit[7] == null || latSplit[7].equals("Unknown")) {
								writer.write("\tCoordinate is not given\n");
								continue;
							}
							String latDMS = latSplit[7];
							
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
							
							
							writer.write("\t" + degrees + "d " + minutes + "m " + 
									seconds + "s " + dir + "\n");
							
							continue;
						}
						
						
							
						
						
						
					} catch (EOFException e) {
						break;
					}
					
				}
				
				writer.close();
				com.close();
			}
			
			data.close();
			
			
			
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file: " + args[1]);
			
		} catch (IOException e) {
			System.err.println("Writing error: " + e);
		}
	
	}
}

