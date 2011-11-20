package uk.co.jamware.updateserver;

import java.util.zip.CRC32;

public class Checksums {
	private static int[] checksums;
	
	public static int[] getChecksums() {
		if(checksums == null)
			checksums = new int[9];
		else
			return checksums;
		checksums[0] = 1;
		CRC32 crc32 = new CRC32();
		FileLoader fileLoader = new FileLoader();
		Archive loadingArchive = new Archive(fileLoader.cache_dat(), fileLoader.cache_idx(0), 1, 0x7a120);
		for(int i = 1; i < 9; i++) {
			byte[] thisFileData = loadingArchive.getArchiveBytes(i);
			crc32.reset();
			crc32.update(thisFileData);
			checksums[i] = (int) crc32.getValue();
		}
		return checksums;
	}
	
	public static int getChecksum(byte[] data) {
		CRC32 crc32 = new CRC32();
		crc32.reset();
		crc32.update(data);
		return (int) crc32.getValue();
	}
}
