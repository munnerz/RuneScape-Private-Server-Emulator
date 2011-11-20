package uk.co.jamware.updateserver;

import java.lang.*;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;

import uk.co.jamware.io.IO;

public class Updater implements Runnable {
	private IO io;
	public String[] cacheIndices = new String[]{"", "title", "config", "interface", "media", "versionlist", "textures", "wordenc", "sounds"};
	FileLoader fileLoader = new FileLoader();
	Archive loadingArchive;
		
	public Updater(IO io) {
		this.io = io;
		loadingArchive = new Archive(fileLoader.cache_dat(), fileLoader.cache_idx(0), 1, 0x7a120);
	}
	
	public int getCacheIndices(String s) {
		System.out.println(".."+s+"..");
		for(int i = 1; i < cacheIndices.length-1; i++)
			if(s.equalsIgnoreCase(cacheIndices[i]))
				return i;
		return 0;
	}
	
	public void run() {
		String typeCode = io.readJagString();
		System.out.println(typeCode);
		if(typeCode.startsWith("JAGGRAB /crc")) {
			int[] checksums = Checksums.getChecksums();
			int k1 = 1234;
			for (int l1 = 0; l1 < 9; l1++) {
				io.writeInt(checksums[l1]);
				k1 = (k1 << 1) + checksums[l1];
			}
			io.writeInt(k1);
			io.flushOutputStream();
		} else if(typeCode.startsWith("JAGGRAB /")) {
			typeCode = typeCode.substring(9);
			String cachePart = "";
			int i = 0;
			for(i = 0; i < typeCode.length(); i++)
				if(Character.isDigit(typeCode.charAt(i)) || typeCode.charAt(i) == '-')
					break;
				else
					cachePart += typeCode.charAt(i);
			String crcRequested = typeCode.substring(i, typeCode.length()-1);
			int cacheIndices = getCacheIndices(cachePart);
			byte[] requestedData = loadingArchive.getArchiveBytes(cacheIndices);
			for(int u = 0; u < 10; u++)
				System.out.print(requestedData[u] + ", ");
			System.out.println();
			for(int u = requestedData.length - 1; u > requestedData.length - 10; u--)
				System.out.print(requestedData[u] + ", ");
			System.out.println();
			
			System.out.println("Length: "+requestedData.length);
			System.out.println("Offset: "+loadingArchive.getPartOffset(cacheIndices));
			
			int bytesSent = 0;
			while(bytesSent < requestedData.length) {
				int bytesToSend = requestedData.length - bytesSent;
				if(bytesToSend > 1000)
					bytesToSend = 1000;
				io.writeBytes(requestedData, bytesSent, bytesToSend);
				bytesSent += bytesToSend;
				io.flushOutputStream();
			}
			System.out.println("Sent: "+bytesSent);
		} else {
			System.err.println("Type code invalid: "+typeCode);
		}
	}
}
