package uk.co.jamware;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import uk.co.jamware.updateserver.UpdateServer;

public class GameServer implements Runnable {
	public GameServer(){}
		
	public void run() {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
			bootstrap.setPipelineFactory(new GameServerPipelineFactory());
			bootstrap.bind(new InetSocketAddress(43594));
			Misc.println("Game server running");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}		
}
