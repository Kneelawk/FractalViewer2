package com.pommert.jedidiah.fractalviewer2.classpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CPControl {
	public static File parent;
	public static Logger log;

	public static void init() {
		log = LogManager.getLogger("ClassPathControl");
		log.info("Init Class Path Control");
	}

	public static void addNativesDir() {
		try {
			addRelativeDir("natives");
		} catch (IOException e) {
			log.fatal("Unable to add natives to classpath!", e);
			System.exit(-1);
		}
	}

	public static void addRelativeDir(String dirname) throws IOException {
		updateParent();
		File file = new File(parent, "natives");
		if (!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath()
					+ " does not exist!");
		addLibraryDir(file.getPath());
	}

	public static void updateParent() {
		if (parent == null) {
			parent = new File(CPControl.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath()).getParentFile();
		}
	}

	public static void addLibraryDir(String s) throws IOException {
		try {
			// This enables the java.library.path to be modified at runtime
			// From a Sun engineer at
			// http://forums.sun.com/thread.jspa?threadID=707176
			//
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (s.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
			System.setProperty("java.library.path",
					System.getProperty("java.library.path")
							+ File.pathSeparator + s);
		} catch (IllegalAccessException e) {
			throw new IOException(
					"Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException(
					"Failed to get field handle to set library path");
		}
	}
}
