package com.pommert.jedidiah.fractalviewer2.ui.opengl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GLControl {
	public static Logger log;

	public static void initGL() {
		log = LogManager.getLogger("GLControl");
		log.info("Init GL");
	}
}
