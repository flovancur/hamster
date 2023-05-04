package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.HamsterException;
import de.hsrm.cs.wwwvs.hamster.lib.HamsterLib;
import de.hsrm.cs.wwwvs.hamster.lib.HamsterState;

import java.io.BufferedReader;
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
//				BufferedReader bufReader = new BufferedReader(reader);
				char[] buffer = new char[socket.getReceiveBufferSize()];
				int character = reader.read(buffer);
				String output = new String(buffer,0,character);
//				String output = bufReader.readLine();

				System.out.println(output);
				System.out.println(output.length());
			}
			//System.exit(printRtfm());
		} catch (Exception ex) {
			System.err.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}