package uk.co.jamware.io;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;

public class IO extends SimpleChannelUpstreamHandler {
	private Channel channel;
	
	private DynamicChannelBuffer inBuffer = new DynamicChannelBuffer(100);
	private DynamicChannelBuffer outBuffer = new DynamicChannelBuffer(100);
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		this.channel = ctx.getChannel();
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		synchronized(inBuffer) {
			inBuffer.writeBytes((BigEndianHeapChannelBuffer) e.getMessage());
			inBuffer.notifyAll();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		try {
			throw e.getCause();
		} catch(java.lang.Throwable ez) {
			ez.printStackTrace();
		}
	}
	
	public void fillInStream(int amt) {
		synchronized(inBuffer) {
			while(!(inBuffer.readableBytes() >= amt)) {
				try {
					inBuffer.wait();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	
	public byte[] readBytes(int offset, int length) {
		if(availableBytes() < length)
			fillInStream(length - availableBytes());
		byte[] b = new byte[length];
		inBuffer.readBytes(b, offset, length);
		return b;
	}
	
	public int readByte() {
		if(availableBytes() < 1)
			fillInStream(1 - availableBytes());
		return inBuffer.readByte() & 0xff;
	}
	
	public int readShort() {
		return (readByte() << 8) + readByte();
	}
	
	public int readTriByte() {
		return (readByte() << 16) + readShort();
	}
	
	public int readInt() {
		return (readByte() << 24) + readTriByte();
	}
	
	public long readLong() {
		long l = (long) readInt() & 0xffffffffL;
		long l1 = (long) readInt() & 0xffffffffL;

		return (l << 32) + l1;
	}
	
	public String readRSString() {
		return new String(getRSStringByteHelper(0));
	}

	public String readJagString() {
		byte[] data = getRSStringByteHelper(0);
		inBuffer.skipBytes(1);
		return new String(data);
	}
	
	public byte[] getRSStringByteHelper(int n) {
		byte[] b = new byte[n + 1];
		int i;
		if((i = readByte()) != 10) {
			b = getRSStringByteHelper(n + 1);
			b[n] = (byte) i;
		}
		return b;
	}
	
	public int availableBytes() {
		return inBuffer.readableBytes();
	}
	
	public void writeByte(int i) {
		outBuffer.writeByte(i);
	}
	
	public void writeShort(int i) {
		outBuffer.writeByte(i >> 8);
		writeByte(i);
	}
	
	public void writeTriByte(int i) {
		outBuffer.writeByte(i >> 16);
		writeShort(i);
	}
	
	public void writeInt(int i) {
		outBuffer.writeByte(i >> 24);
		writeTriByte(i);
	}
	
	public void writeLong(long l) {
		for(int i = 7; i >= 0; i--)
			writeByte((int)l >> (8*i));
	}
	
	public void writeBytes(byte[] b, int offset, int length) {
		outBuffer.writeBytes(b, offset, length);
	}
	
	public void flushOutputStream() {
		channel.write(outBuffer);
	}
}
