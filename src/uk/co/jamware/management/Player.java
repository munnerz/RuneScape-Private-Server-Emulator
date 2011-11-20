package uk.co.jamware.management;

import uk.co.jamware.io.IO;

public class Player implements Runnable {
	private IO io;
	
	public Player(IO io) {
		this.io = io;
	}
	
	public void run() {
		switch(io.readByte()) {
			case 14: //standard login
				login();
			break;
			
			case 15:
				for(int i = 0; i < 8; i++)
					io.writeByte(0);
				System.out.println("OnDemand Updating connection");
			break;
		}
	}
	
	public void login() {
		int usernameInt = io.readByte();
		for(int i = 0; i < 8; i++)
			io.writeByte(0);
		io.writeByte(0);
		io.writeLong((long)Math.random() * 99999999L);
		io.flushOutputStream();
		boolean flag = (io.readByte() == 18);
		int loginPacketSize = io.readByte();
		int loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2);
		
		if(io.readByte() != 255 || io.readShort() != 317)
			System.out.println("Invalid versions - kill client");
		boolean lowMem = (io.readByte() == 1);
		
		int[] crcs = new int[9];
		for(int i = 0; i < 9; i++) {
			crcs[i] = io.readInt();
		}
		
		loginEncryptPacketSize--;
		int tmp = io.readByte();
		if (loginEncryptPacketSize != tmp) {
			System.err.println("Encrypted packet data length ("
				+ loginEncryptPacketSize
				+ ") different from length byte thereof (" + tmp + ")");
			return;
		}
		
		if(io.readByte() != 10)
			return;
		
		long clientSessionKey = io.readLong();
		long serverSessionKey = io.readLong();
		int playerId = io.readInt();
		String username = io.readRSString();
		String password = io.readRSString();
		System.out.println(username+":"+password);
		io.writeByte(2);
		io.writeByte(2);
		io.writeByte(0);
	}
}
