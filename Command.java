package cs3114.J3.CMD;

/**
 * @author Jack
 * @version jackj21
 * Command type for commands that contains
 * the command keyword and the action.
 */
public class Command {
	private String command; // command word
	private String name; 
	
	/**
	 * Constructor that takes in
	 * the command keyword and the action and
	 * assigns it to its fields when created.
	 * 
	 * @param cmd Command keyword.
	 * @param instruc Action/Instruction keyword.
	 */
	public Command (String cmd, String instruc) {
		if (instruc == null) {
			this.name = null;
		}
		this.command = cmd;
		this.name = instruc;
	}
	
	/**
	 * Returns the command formatted
	 * to be written in an output file.
	 * @return F
	 */
	public String getCommand() {
		return this.command + " " + this.name;
		
	}
	
	/**
	 * Returns the command keyword field.
	 * @return the command field.
	 */
	public String getCom() {
		return this.command;
	}
	
	/**
	 * Returns the action/instruction field.
	 * @return the name field.
	 */
	public String getName() {
		return this.name;
	}
}
