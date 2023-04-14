package de.hsrm.cs.wwwvs.hamster.console;
import de.hsrm.cs.wwwvs.hamster.lib.*;
/**
 * Simple command-line interface for the hamsterlib
 * @author hinkel
 */
public class CommandLineInterface{

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
	 * 
	 */
	
	public static void main(String[] args) {

		String owner = args.length>=2?args[1]:null;
		String hamster = args.length>=3?args[2]:null;
		HamsterLib store = new HamsterLib();
		var ownerOut = store.new OutString();
		var hamsterOut = store.new OutString();
		var priceOut = store.new OutShort();
		int id = 0;
		
		
		
		if(args.length != 0){
			String command = args[0];
			HamsterIterator iterator = store.iterator();
			
			try{
				short treats = args.length>=4?Short.parseShort(args[3]):0;
				switch (command) {
					case "list":
						try{
							if(args.length>2){
								printRtfm();
								System.exit(2);
							}
							boolean header = true;
							while(iterator.hasNext()){
								id=store.directory(iterator, owner, hamster);
								short treatsOut = store.readentry(id, ownerOut, hamsterOut, priceOut);
								if(header==true){
									System.out.println("Owner Name    Price   treats left");
									header= false;
								}
								System.out.println(ownerOut.getValue() + "\t" + hamsterOut.getValue() + "\t" + priceOut.getValue()+  " €\t"+ treatsOut);
							}								
						}
						catch(HamsterNotFoundException e){
							System.out.println("No hamsters matching criteria found");
							System.exit(2);
						}
						break;
	
					case "add":
					if(args.length>4 || args.length<3){
						printRtfm();
						System.exit(2);
					}
							int result = store.new_(owner, hamster, treats);
							
							System.out.println("Done!");
					
						break;
	
					case "feed":
					if(args.length>4 || args.length<4){
						printRtfm();
						System.exit(2);
					}
							id = store.lookup(owner, hamster);
							short treatsLeft = store.givetreats(id, treats);
							System.out.println("Done! " + treatsLeft + " treats remaining in store");
						
				
						break;
					
					case "state":
					if(args.length>3 || args.length<3){
						printRtfm();
						System.exit(2);
					}
						HamsterState hState = new HamsterState();
						id = store.lookup(owner, hamster);
						store.howsdoing(id, hState);
						if(hState.getRounds()>0){
							System.out.println(owner +"'s hamster " + hamster + " has done > 0" + " hamster wheel revolutions,");
							System.out.println("and has " + hState.getTreatsLeft() + " treats left in store. Current price is " + hState.getCost() + " €");
						}
					
						break;
					
					case "bill":
					if(args.length>2 || args.length<2){
						printRtfm();
						System.exit(2);
					}
							short price = store.collect(owner);
							System.out.println(owner + " has to pay " + price + " €");
					
						break;
					default:
						printRtfm();
						System.exit(2);
				}
			}
			catch(HamsterNameTooLongException | HamsterDatabaseCorruptException | HamsterStorageException e){
				System.out.println("Error: " + e.getMessage());
			}
			catch(HamsterNotFoundException e){
				System.out.println("Error: A hamster or hamster owner could not be found");
			}
			catch(HamsterAlreadyExistsException e){
				System.out.println("Error: " + e.getMessage());
			}
			catch (HamsterEndOfDirectoryException e){
				
			}
			catch (NumberFormatException e){
				printRtfm();
				System.exit(2);
			}
	
		}else{printRtfm();
			System.exit(2);
		}
	}

}
