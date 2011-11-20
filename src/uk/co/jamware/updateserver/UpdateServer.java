package uk.co.jamware.updateserver;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import uk.co.jamware.Misc;

public class UpdateServer implements Runnable {
	public UpdateServer(){}
		
	public void run() {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
			bootstrap.setPipelineFactory(new UpdateServerPipelineFactory());
			bootstrap.bind(new InetSocketAddress(43595));
			Misc.println("Update server running");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
