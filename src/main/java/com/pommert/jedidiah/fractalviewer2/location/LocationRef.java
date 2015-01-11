package com.pommert.jedidiah.fractalviewer2.location;

import java.io.File;

public class LocationRef {
	public static final String PROGRAM_LOCATION_STRING = getProgramLocation();
	public static final File PROGRAM_LOCATION_FILE = new File(
			PROGRAM_LOCATION_STRING);
	public static final File PROGRAM_LOCATION_PARENT_FILE = PROGRAM_LOCATION_FILE
			.getParentFile();
	public static final String PROGRAM_LOCATION_PARENT_STRING = PROGRAM_LOCATION_FILE
			.getParent();

	private static String getProgramLocation() {
		return LocationRef.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath();
	}
}
