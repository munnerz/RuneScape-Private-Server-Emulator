package uk.co.jamware.updateserver;

import static org.jboss.netty.channel.Channels.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import uk.co.jamware.io.IO;

public class UpdateServerPipelineFactory implements ChannelPipelineFactory {
	private Updater[] updaters = new Updater[512];
	private int maxPlayers = 512;
	
	public UpdateServerPipelineFactory() {
		
	}
	
	private int findFreeSlot() {
		for(int i = 0; i < maxPlayers; i++)
			if(updaters[i] == null)
				return i;
		return -1;
	}
	
	public ChannelPipeline getPipeline() throws Exception {
		int slot = findFreeSlot();
		if(slot == -1) {
			//no free space
			return null;
		}
		IO io = new IO();
		updaters[slot] = new Updater(io);
		new Thread(updaters[slot]).start();
		
		ChannelPipeline pipeline = pipeline();
		System.out.println("Client connecting to update server");
		pipeline.addLast("handler", io);
		return pipeline;
	}
}
