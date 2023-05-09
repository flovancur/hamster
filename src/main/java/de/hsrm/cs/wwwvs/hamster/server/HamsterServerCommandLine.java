package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.HamsterException;
import de.hsrm.cs.wwwvs.hamster.lib.HamsterLib;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

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

	private static byte[] charArrayToByteArray(byte[] c_array) {
		byte[] b_array = new byte[c_array.length];
		for(int i= 0; i < c_array.length; i++) {
			b_array[i] = (byte)(0xFF & (int)c_array[i]);
		}
		return b_array;
	}

	private static String generateName(byte[] bufferOwner){
		boolean end = false;
		int i = 0;
		while(!end && i<32){
			if(bufferOwner[i]=='\u0000'){
				end=true;
				break;
			}
			i++;
		}
		return new String(bufferOwner,0,i);
	}




	private static ByteBuffer sendHeader(ServerHeader inputHeader, int payloadLength, int flag){
		ByteBuffer retPayload = ByteBuffer.allocate(8+payloadLength);
		retPayload.put(inputHeader.version);
		retPayload.put((byte)flag);
		retPayload.putShort(inputHeader.messageId);
		retPayload.putShort((short)4);
		retPayload.putShort(inputHeader.rpcCall);
		return retPayload;
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
		HamsterLib hamsterLib = new HamsterLib();

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
			Socket socket = serverSocket.accept();
			while(true){
				InputStream input = socket.getInputStream();
				ByteBuffer header = ByteBuffer.allocate(8);
				input.read(header.array());
				ServerHeader inputHeader = new ServerHeader(header);
				ByteBuffer payload = ByteBuffer.allocate(inputHeader.payloadLength);
				input.read(payload.array());
				byte[] ownerIn = new byte[32];
				payload.get(ownerIn,0,32);
				byte[] hamsterIn = new byte[32];
				payload.get(hamsterIn,0,32);
				short treats = payload.getShort();
				OutputStream out = socket.getOutputStream();
				DataOutputStream outStream = new DataOutputStream(out);


				String owner = generateName(ownerIn);
				String hamster = generateName(hamsterIn);

				switch (inputHeader.rpcCall){
					case 1:
						try{
							int id = hamsterLib.new_(owner,hamster,treats);
							ByteBuffer retPayload = sendHeader(inputHeader, 4,1);
							retPayload.putInt(id);
							out.write(retPayload.array());
							out.flush();
						}catch (HamsterException e){
							ByteBuffer retPayload = sendHeader(inputHeader,4,2);
							retPayload.putInt(2);
							out.write(retPayload.array());
						}
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					default:
						System.out.println("Error");
				}

				System.out.println(generateName(ownerIn));
				System.out.println(generateName(hamsterIn));

			}
			//System.exit(printRtfm());
		} catch (Exception ex) {
			System.err.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}