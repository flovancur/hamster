package de.hsrm.cs.wwwvs.hamster.console;

/**
 * Simple command-line interface for the hamsterlib
 * @author hinkel
 */
public class CommandLineInterface {

	private static void printRtfm()
	{
		System.out.println("Usage: hamster {<Option>} <param1> {<param2>}");
		System.out.println("Function: Hamster management");
		System.out.println("Verbs:");
		System.out.println("     list {<owner>}                   - show current list of hamsters");
		System.out.println("     add <owner> <hamster> [<treats>] - add new hamster");
		System.out.println("     feed <owner> <hamster> <treats>  - feed treats to hamster");
		System.out.println("     state <owner> <hamster>          - how is my hamster doing?");
		System.out.println("     bill <owner>                     - the bill please!");
	}

	/**
	 * The main command-line interface, 
	 * TODO add your code here
	 * @param args
	 */
	public static void main(String[] args) {
		printRtfm();
	}

}
