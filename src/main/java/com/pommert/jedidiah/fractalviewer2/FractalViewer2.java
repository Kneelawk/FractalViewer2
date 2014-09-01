package com.pommert.jedidiah.fractalviewer2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.FractalControl;
import com.pommert.jedidiah.fractalviewer2.output.OutputControl;
import com.pommert.jedidiah.fractalviewer2.ui.UIControl;
import com.pommert.jedidiah.fractalviewer2.ui.opengl.GLControl;

public class FractalViewer2 {

	public static Logger log;

	public static void main(String[] args) {
		// create logger
		log = LogManager.getLogger("Core");
		log.info("Starting Fractal Viewer 2!");

		// init outputs
		OutputControl.initOutputs();

		// init fractals
		FractalControl.initFractals();

		// init ui
		UIControl.initUI();

		// init gl
		GLControl.initGL();

		// show ui
		UIControl.show();
	}

	public static void stop() {
		log.info("Stopping");
	}
}