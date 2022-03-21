package cs3114.J3.CMD;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 * @author Jack Jiang
 * @version 3/22/21
 * 
 * Parses commands in script file.
 */
public class CommandParser {
	private long offset = 0;
	private RandomAccessFile raf;
	
	public CommandParser(RandomAccessFile raf , long offset) {
		this.raf = raf;
		this.offset = offset;
	}
	
	/**
	 * Moves script file pointer to a pointer offset
	 * @param findOffset offset to move file pointer to.
	 */
	public void seek(long findOffset) {
			this.offset = findOffset;
		
	}
	
	/*
	 * Parses the command.
	 */
	public Command parseCommand() {
		Command command = null;
		String line = "null";
		try {
			raf.seek(this.offset);
		
			line = raf.readLine();
		
			while (line.contains(";") || line.equals("")) {
				line = raf.readLine();
			}
			
			offset = raf.getFilePointer();
			Scanner scanner = new Scanner(line);
			
			String cmd = scanner.next();
			
			
			if (cmd.contains("what")) {
				StringBuilder sb = new StringBuilder("");
				String nextLine = scanner.nextLine();
				Scanner lineScanner = new Scanner(nextLine);
				while (lineScanner.hasNext()) {
					String token = lineScanner.next();
					sb.append(token);
					if (!lineScanner.hasNext()) break;
					sb.append(" ");
				}
				lineScanner.close();
				scanner.close();
				return new Command(cmd, sb.toString());
			}
			if (!scanner.hasNext()) {
				scanner.close();
				return null;
			}
			String name = scanner.next();
			command = new Command(cmd, name);
			
			scanner.close();
			
		
		} catch (IOException e) {
			System.err.println("Error processing file.");
		}
		return command;
	}
}
