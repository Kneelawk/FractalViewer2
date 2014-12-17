package com.pommert.jedidiah.fractalviewer2;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.classpath.CPControl;
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

		// init ClassPathControl and add natives
		CPControl.init();
		CPControl.addNativesDir();

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

	public static void start(File file, String fractalGeneratorName, int width,
			int height, int seed) {
		log.info("Starting");

		// start Output Control
		OutputControl.start(file, fractalGeneratorName, width, height, seed);
	}

	public static void stop() {
		log.info("Stopping");

		OutputControl.stop();

		// close gl
		boolean wasOpen = GLControl.isOpen;
		GLControl.stop(true);
		// if the gl window was open, it will exit when it has released all it's
		// resources
		if (!wasOpen) {
			log.info("Exiting.");
			System.exit(0);
		}
	}
}