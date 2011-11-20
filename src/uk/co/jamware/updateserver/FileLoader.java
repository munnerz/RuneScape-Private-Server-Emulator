package uk.co.jamware.updateserver;

import java.io.*;

public class FileLoader {
	private RandomAccessFile cache_dat;
	private RandomAccessFile[] cache_idx = new RandomAccessFile[5];
	
	public FileLoader() {
		loadDataFiles();
	}
	
	private void loadDataFiles() {
		try {
			cache_dat = new RandomAccessFile(getCacheDir() + "main_file_cache.dat", "rw");
			for(int i = 0; i < 5; i++)
				cache_idx[i] = new RandomAccessFile(getCacheDir() + "main_file_cache.idx" + i, "rw");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getCacheDir() {
		return "./cache/";
	}
	
	public RandomAccessFile cache_dat() {
		return this.cache_dat;
	}
	
	public RandomAccessFile cache_idx(int i) {
		return this.cache_idx[i];
	}
}
