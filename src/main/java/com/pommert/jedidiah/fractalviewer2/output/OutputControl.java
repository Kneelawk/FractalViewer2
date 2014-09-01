package com.pommert.jedidiah.fractalviewer2.output;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.FractalControl;
import com.pommert.jedidiah.fractalviewer2.output.lwjgl.LWJGLOutput;
import com.pommert.jedidiah.fractalviewer2.output.pngj.PngjOutput;
import com.pommert.jedidiah.fractalviewer2.util.Colour;

public class OutputControl {
	public static ArrayList<ActiveOutput> activeOutputs = new ArrayList<ActiveOutput>();
	public static ArrayList<PassiveOutput> passiveOutputs = new ArrayList<PassiveOutput>();

	public static Logger log;

	private static long updates = 0;

	private static void initActiveOutputs() {
		log.info("Init Active Outputs");
		addActiveOutput(new PngjOutput());
	}

	private static void addActiveOutput(ActiveOutput ao) {
		activeOutputs.add(ao);
		ao.init();
	}

	private static void initPassiveOutputs() {
		log.info("Init Passive Outputs");
		addPassiveOutput(new LWJGLOutput());
	}

	private static void addPassiveOutput(PassiveOutput po) {
		passiveOutputs.add(po);
		po.init();
	}

	public static void initOutputs() {
		log = LogManager.getLogger("OutputControl");
		log.info("Init Output");

		initActiveOutputs();
		initPassiveOutputs();
	}

	public static void start() {
		resetUpdates();
	}

	public static void resetUpdates() {
		updates = 0;
	}

	public static Colour generatePixel(int x, int y) {
		return FractalControl.generatePixel(x, y);
	}

	public static void update() {
		if (updates % 100000 == 99999) {
			log.warn("Collecting Garbage...");
			System.gc();
			log.info("Garbage Collected!");
		}
	}
}
