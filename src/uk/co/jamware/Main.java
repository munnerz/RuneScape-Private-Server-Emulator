package uk.co.jamware;

import uk.co.jamware.updateserver.UpdateServer;

public class Main {
	
	public static void main(String args[]) throws Exception {
		new Thread(new UpdateServer()).start();
		new Thread(new GameServer()).start();
	}
		
}
