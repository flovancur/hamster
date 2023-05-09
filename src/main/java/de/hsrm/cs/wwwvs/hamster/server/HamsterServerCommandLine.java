package de.hsrm.cs.wwwvs.hamster.server;

import de.hsrm.cs.wwwvs.hamster.lib.HamsterException;
import de.hsrm.cs.wwwvs.hamster.lib.HamsterLib;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

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

	private static byte[] getStaticAscii(String str) {

		if (str == null) {
			byte[] ret = new byte[32];
			Arrays.fill(ret, (byte) 0);
			return ret;
		}

		return Arrays.copyOf(str.getBytes(Charset.forName(StandardCharsets.US_ASCII.name())), 32);

	}



	private static ByteBuffer sendHeader(ServerHeader inputHeader, int payloadLength, int flag){
		ByteBuffer retPayload = ByteBuffer.allocate(8+payloadLength);
		retPayload.put(inputHeader.version);
		retPayload.put((byte)flag);
		retPayload.putShort(inputHeader.messageId);
		retPayload.putShort((short)payloadLength);
		retPayload.putShort(inputHeader.rpcCall);
		return retPayload;
	}

	private static ArrayList<Integer> list(HamsterLib lib, String owner) throws HamsterException {
		var it = lib.iterator();
		var name = lib.new OutString();
		var ownerName = lib.new OutString();
		var price = lib.new OutShort();
		ArrayList<Integer> results = new ArrayList<Integer>();

		while (it.hasNext()){
			var hamster = lib.directory(it, owner, null);
			//var treats = lib.readentry(hamster, ownerName, name, price);
			//HamsterReturn singleHamster = new HamsterReturn(ownerName.getValue(), name.getValue(), price.getValue(), treats);
			results.add(hamster);
		} ;
		return results;
	}

	private static ByteBuffer handleError(byte version, short messageId, short rpcCall){

		ByteBuffer retPayload = ByteBuffer.allocate(12);
		retPayload.put(version);
		retPayload.put((byte)2);
		retPayload.putShort(messageId);
		retPayload.putShort((short)4);
		retPayload.putShort(rpcCall);
		retPayload.putInt(2);
		return retPayload;
	}





//	private static class HamsterReturn {
//		public String owner;
//		public String hamster;
//		public Short price;
//		public Short treats;
//
//		public HamsterReturn(String owner, String hamster, Short price, Short treats){
//			this.owner = owner;
//			this.hamster = hamster;
//			this.price = price;
//			this.treats = treats;
//		}
//	}


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

				OutputStream out = socket.getOutputStream();
				DataOutputStream outStream = new DataOutputStream(out);


				switch (inputHeader.rpcCall){
					case 1:
						try{
							byte[] ownerIn = new byte[32];
							payload.get(ownerIn,0,32);
							byte[] hamsterIn = new byte[32];
							payload.get(hamsterIn,0,32);
							String owner = generateName(ownerIn);
							String hamster = generateName(hamsterIn);
							short treats = payload.getShort();
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
						try{
							ArrayList<Integer> hamsterList = list(hamsterLib,null);
							int capacity = 0;
							for(Integer id: hamsterList){
								capacity+=8;
							}
							ByteBuffer retPayload = sendHeader(inputHeader,capacity,1);
							for(Integer id: hamsterList){
								retPayload.putInt(id);
							}
							out.write(retPayload.array());
						}catch (HamsterException e){
							out.write(handleError(inputHeader.version, inputHeader.messageId, inputHeader.rpcCall).array());
						}

						break;
					case 4:
						break;
					case 5:
						var name = hamsterLib.new OutString();
						var ownerName = hamsterLib.new OutString();
						var price = hamsterLib.new OutShort();
						int id = payload.getInt();
						int treats = hamsterLib.readentry(id, ownerName, name, price);
						String outName = name.getValue();
						String outOwnerName = ownerName.getValue();
						short outPrice = price.getValue();
						ByteBuffer retPayload = sendHeader(inputHeader,70,1);
						retPayload.putInt(treats);
						retPayload.put(getStaticAscii(outOwnerName));
						retPayload.put(getStaticAscii(outName));
						retPayload.putShort(outPrice);
						out.write(retPayload.array());
						break;
					case 6:
						break;
					case 7:
						break;
					default:
						socket.close();
						System.out.println("Error");
				}

			/*	System.out.println(generateName(ownerIn));
				System.out.println(generateName(hamsterIn));*/

			}
			//System.exit(printRtfm());
		} catch (Exception ex) {
			System.err.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}