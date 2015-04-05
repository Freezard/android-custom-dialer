package com.example.lab6.utils;

import java.io.File;

import android.os.Environment;

/**
 * Utils.java
 * 
 * Helper class with common operations.
 */
public class Utils {
	// Creates all directories needed to the specified location
	public static void makeDirs(String location) {
		File dir = new File(location);
		dir.mkdirs();
	}

	// Deletes the directory including all the contained files
	public static void deleteDir(File dir) {
		File[] files = dir.listFiles();

		if (files != null)
			for (File f : files)
				if (f.isDirectory())
					deleteDir(f);
				else
					f.delete();
		dir.delete();
	}

	// Checks if the specified location is a directory
	public static boolean isDirectory(String location) {
		File dir = new File(location);

		return dir.isDirectory();
	}

	// Checks if external storage is available to read and write
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();

		return Environment.MEDIA_MOUNTED.equals(state);
	}

	// Checks if external storage is available to read
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();

		return Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
}