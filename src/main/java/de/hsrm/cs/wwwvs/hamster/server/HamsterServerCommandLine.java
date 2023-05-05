package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.HamsterException;
import de.hsrm.cs.wwwvs.hamster.lib.HamsterLib;
import de.hsrm.cs.wwwvs.hamster.lib.HamsterState;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple command-line interface for the hamsterlib
 * 
 * @author hinkel
 */

public class HamsterServerCommandLine {

	private static int printRtfm() {
		System.out.println("Usage: java -jar hamsterServer.jar {<Option>} <param1> {<param2>}");
		System.out.println("Function: Hamster management server");
		System.out.println("Options:");
		System.out.println("     -p {<port>}		- port to run the server");
		System.out.println("     -h {<IP address>}	- IP address to run the server on (default: 127.0.0.1)");
		return 2;
	}

	private static void connect(){

	}

	private static byte[] charArrayToByteArray(char[] c_array) {
		byte[] b_array = new byte[c_array.length];
		for(int i= 0; i < c_array.length; i++) {
			b_array[i] = (byte)(0xFF & (int)c_array[i]);
		}
		return b_array;
	}

	private static String generateOwnerName(char[] bufferOwner){
		boolean end = false;
		int i = 0;
		while(!end && i<40){
			if(bufferOwner[i+8]=='\u0000'){
				end=true;
			}
			i++;
		}
		return new String(bufferOwner,8,4);
	}

	private static String generateHamsterName(char[] bufferOwner){
		boolean end = false;
		int i = 0;
		while(!end && i<40){
			if(bufferOwner[i+40]=='\u0000'){
				end=true;
			}
			i++;
		}
		return new String(bufferOwner,40,4);
	}


	/**
	 * The main command-line interface,
	 * TODO add your code here
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String hostName = "127.0.0.1";
		int port = 9000;

		if (args.length == 0) {
			System.exit(printRtfm());
		}

		for (int i = 0; i < args.length; i+= 2) {
			switch (args[i]) {
				case "-p":
					port = Integer.parseInt(args[i+1]);
					break;
				case "-h":
					hostName = args[i+1];
					break;
				default:
					System.exit(printRtfm());
			}
		}
		try {
			InetAddress address = Inet4Address.getByName(hostName);
			ServerSocket serverSocket = new ServerSocket(port,50,address);

			while(true){
				Socket socket = serverSocket.accept();
				InputStream input = socket.getInputStream();
				InputStreamReader reader = new InputStreamReader(input);
				char[] buffer = new char[socket.getReceiveBufferSize()];
				int size = reader.read(buffer);
				String output = new String(buffer);
				byte[] bytes = charArrayToByteArray(buffer);

				System.out.println(generateOwnerName(buffer));
				System.out.println(generateHamsterName(buffer));

				socket.close();


			}
			//System.exit(printRtfm());
		} catch (Exception ex) {
			System.err.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}