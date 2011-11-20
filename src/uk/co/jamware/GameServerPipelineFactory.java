package uk.co.jamware;

import static org.jboss.netty.channel.Channels.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import uk.co.jamware.management.Player;
import uk.co.jamware.io.IO;

public class GameServerPipelineFactory implements ChannelPipelineFactory {
	private Player[] players = new Player[512];
	private int maxPlayers = 512;
	
	public GameServerPipelineFactory() {}
	
	private int findFreeSlot() {
		for(int i = 0; i < maxPlayers; i++)
			if(players[i] == null)
				return i;
		return -1;
	}
	
	public ChannelPipeline getPipeline() throws Exception {
		int slot = findFreeSlot();
		if(slot == -1) {
			//no free space
			return null;
		}
		IO thisIO = new IO();
		players[slot] = new Player(thisIO);
		new Thread(players[slot]).start();
		
		ChannelPipeline pipeline = pipeline();
		System.out.println("Client connecting to game server");
		pipeline.addLast("handler", thisIO);
		return pipeline;
	}
}
