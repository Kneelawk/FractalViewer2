package com.pommert.jedidiah.fractalviewer2.ui.opengl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class GLControl {
	public static final int DISPLAY_WIDTH = 640;
	public static final int DISPLAY_HEIGHT = 480;

	public static Logger log;
	public static Thread glThread;
	public static boolean isOpen;

	public static int viewX, viewY;
	public static int imageWidth, imageHeight;

	public static void initGL() {
		log = LogManager.getLogger("GLControl");
		log.info("Init GL");
	}

	public static synchronized void open(int width, int height) {
		if (!isOpen) {
			isOpen = true;
			glThread = new Thread(new Runnable() {
				public void run() {
					imageWidth = width;
					imageHeight = height;
					viewX = ((imageWidth / 2) - (DISPLAY_WIDTH / 2));
					viewY = ((imageHeight / 2) - (DISPLAY_HEIGHT / 2));
					startGL();
				}
			}, "GL Render Thread");
			glThread.start();
		}
	}

	public static void startGL() {
		try {
			// open a display window
			openDisplay();

			// setup gl
			setupGL();

			// start display loop
			startLoop();

			// destroy when done
			destroy();
		} catch (Throwable t) {
			log.error("Error during opengl process!", t);
			throw new RuntimeException("Error during opengl process!", t);
		}
	}

	public static void openDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH,
					DISPLAY_HEIGHT));
			Display.setTitle("Fractal Viewer");
			Display.setVSyncEnabled(true);
			log.info("Opening GL Display!");
			Display.create();
		} catch (LWJGLException e) {
			log.error("LWJGLException while opening GL display window!", e);
			throw new RuntimeException(
					"LWJGLException while opening GL display window!", e);
		}
	}

	public static void setupGL() {
		// background color
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);

		// ortho
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, DISPLAY_WIDTH, DISPLAY_HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	public static void startLoop() {
		while (isOpen) {
			if (Display.isCloseRequested()) {
				stop();
			}

			pullInput();

			draw();

			Display.update();
		}
	}

	public static void pullInput() {

	}

	public static void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0f, 0f, 0f);
	}

	public static void stop() {
		isOpen = false;
	}

	public static void destroy() {
		isOpen = false;
		Display.destroy();
	}
}
