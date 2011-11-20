package uk.co.jamware.updateserver;

import java.io.*;

public class Archive {
	private final RandomAccessFile cacheDataFile;
	private final RandomAccessFile cacheIDXFile;
	private final int archiveID;
	private final int maxDataLength;
	private byte[] archiveFilePartBytes = new byte[520];
	
	public Archive(RandomAccessFile dataFile, RandomAccessFile idxFile, int i, int maxFileLength) {
		this.cacheDataFile = dataFile;
		this.cacheIDXFile = idxFile;
		this.maxDataLength = maxFileLength;
		archiveID = i;
	}
	
	private void seekToPos(RandomAccessFile file, int pos) {
		if(pos < 0 || pos > 0x3c00000) {
			try {
				System.out.println("Seeking out of range! seekto: "+pos+" length: "+file.length());
			} catch(IOException e) {
				System.err.println("Error getting file length! Seek to:"+pos);
				e.printStackTrace();
			}
			pos = 0x3c00000;
		}
		try {
			file.seek(pos);
		} catch(IOException e) {
			System.err.println("Error seeking to position "+pos);
			e.printStackTrace();
		}
	}
	
	public int getPartSize(int partID) {
		return ((archiveFilePartBytes[0] & 0xff) << 16) + ((archiveFilePartBytes[1] & 0xff) << 8) + (archiveFilePartBytes[2] & 0xff);
	}
	
	public int getPartOffset(int partID) {
		return ((archiveFilePartBytes[3] & 0xff) << 16) + ((archiveFilePartBytes[4] & 0xff) << 8) + (archiveFilePartBytes[5] & 0xff);
	}
	
	public byte[] getArchiveBytes(int partID) {
		try {
			seekToPos(cacheIDXFile, partID * 6); //seek to pos 6
			int bytesRead = 0;
			for(int insertOffset = 0; insertOffset < 6; insertOffset += bytesRead) {
				//read idx header data
				bytesRead = cacheIDXFile.read(archiveFilePartBytes, insertOffset, 6 - bytesRead);
				//two 3 byte ints:
				// first: ?
				// second: offset of data in .dat file **divided by 520**
				if(bytesRead == -1) //not enough data!
					return null;
			}
		
			int dataLength = ((archiveFilePartBytes[0] & 0xff) << 16) + ((archiveFilePartBytes[1] & 0xff) << 8) + (archiveFilePartBytes[2] & 0xff);
			int dataFileOffset = ((archiveFilePartBytes[3] & 0xff) << 16) + ((archiveFilePartBytes[4] & 0xff) << 8) + (archiveFilePartBytes[5] & 0xff);
			
			if(dataLength < 0 || dataLength > maxDataLength) {
				System.out.println("Invalid file length! Length requested: "+dataLength);
				return null;
			}
			if(dataFileOffset <= 0 || (long) dataFileOffset > cacheDataFile.length() / 520L) {
				System.out.println("Offset out of bounds of file!");
				return null;
			}
			
			byte[] fileData = new byte[dataLength];
			int currentOffset = 0;
			for(int fileChunksRead = 0; currentOffset < dataLength; fileChunksRead++) {
				if(dataFileOffset == 0)
					return null;
				seekToPos(cacheDataFile, dataFileOffset * 520);
				
				int bytesToRead = dataLength - currentOffset;
				//read 512 byte chunks
				if(bytesToRead > 512)
					bytesToRead = 512;
				
				int dataBytesRead = 0;
				for(int insertOffset = 0; insertOffset < bytesToRead + 8; insertOffset += dataBytesRead) {
					dataBytesRead = cacheDataFile.read(archiveFilePartBytes, insertOffset, (bytesToRead + 8) - insertOffset);
					//8 byte header, used below
					if(dataBytesRead == -1)
						return null;
				}
				
				//short
				int cacheReportedPartID = ((archiveFilePartBytes[0] & 0xff) << 8) + (archiveFilePartBytes[1] & 0xff);
				//short
				int thisFileChunk = ((archiveFilePartBytes[2] & 0xff) << 8) + (archiveFilePartBytes[3] & 0xff);
				//3 byte int
				int nextChunkOffset = ((archiveFilePartBytes[4] & 0xff) << 16) + ((archiveFilePartBytes[5] & 0xff) << 8) + (archiveFilePartBytes[6] & 0xff);
				//this archive id
				int reportedArchiveID = archiveFilePartBytes[7] & 0xff;
				
				if(cacheReportedPartID != partID || thisFileChunk != fileChunksRead || this.archiveID != reportedArchiveID) {
					System.err.println("Error in cache file structure.\ncacheReportedPartID: "+cacheReportedPartID+", partID: "+partID);
					return null;
				}
				if(nextChunkOffset < 0 || (long)nextChunkOffset > cacheDataFile.length() / 520L) {
					System.err.println("Next chunk offset out of bounds!");
					return null;
				}
				for(int archiveFilePartBytesOffset = 0; archiveFilePartBytesOffset < bytesToRead; archiveFilePartBytesOffset++)
					fileData[currentOffset++] = archiveFilePartBytes[archiveFilePartBytesOffset + 8];
				dataFileOffset = nextChunkOffset;
			}
			return fileData;
		} catch(Exception e) {
			System.err.println("Error in getArchiveBytes("+partID+")");
			e.printStackTrace();
			return null;
		}
	}
	
}
