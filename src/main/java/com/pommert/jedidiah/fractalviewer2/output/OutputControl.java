package com.pommert.jedidiah.fractalviewer2.output;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.FractalControl;
import com.pommert.jedidiah.fractalviewer2.output.lwjgl.LWJGLOutput;
import com.pommert.jedidiah.fractalviewer2.output.pngj.PngjOutput;
import com.pommert.jedidiah.fractalviewer2.ui.UIControl;
import com.pommert.jedidiah.fractalviewer2.util.Colour;

public class OutputControl {
	public static ArrayList<ActiveOutput> activeOutputs = new ArrayList<ActiveOutput>();
	public static ArrayList<PassiveOutput> passiveOutputs = new ArrayList<PassiveOutput>();
	public static boolean running = false;
	public static int currentActiveIndex = 0;
	public static String currentFractalGeneratorName;
	public static Thread generationThread;

	public static double overallDone = 0;

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

	public static void start(File file, String fractalGeneratorName, int width,
			int height) {
		resetUpdates();
		if (!running) {
			running = true;
			generationThread = new Thread(new Runnable() {
				@Override
				public void run() {
					_start(file, fractalGeneratorName, width, height);
				}
			}, "Generation Thread");
			generationThread.start();
		}
	}

	protected static void _start(File file, String fractalGeneratorName,
			int width, int height) {
		log.info("Setting Up Outputs");
		for (ActiveOutput out : activeOutputs) {
			out.setup(file, width, height);
		}
		for (PassiveOutput out : passiveOutputs) {
			out.setup(file, width, height);
		}

		currentFractalGeneratorName = fractalGeneratorName;
		FractalControl.starting(currentFractalGeneratorName);

		log.info("Setting Up Fractal Generator");

		log.info("Starting Fractal Generation");
		overallDone = 0;
		int maxOutput = activeOutputs.size();
		for (currentActiveIndex = 0; currentActiveIndex < maxOutput && running; currentActiveIndex++) {
			ActiveOutput out = activeOutputs.get(currentActiveIndex);
			out.run();
			out.save(!running);
			overallDone = ((double) currentActiveIndex * 100) / maxOutput;
		}
		
		currentActiveIndex = 0;

		for (PassiveOutput out : passiveOutputs) {
			out.save();
		}

		overallDone = 100;

		updatePercentDone();
		collectGarbage();

		log.info("Finished Fractal Generation!");

		running = false;
	}

	public static void resetUpdates() {
		updates = 0;
	}

	public static Colour generatePixel(int x, int y) {
		Colour c = FractalControl.generatePixel(currentFractalGeneratorName, x,
				y);
		for (PassiveOutput out : passiveOutputs) {
			out.setPixel(x, y, c);
		}
		return c;
	}

	public static void update() {
		updatePercentDone();
		if (updates >= 10000) {
			collectGarbage();
			resetUpdates();
		}
		updates++;
	}

	public static void collectGarbage() {
		log.warn("Collecting Garbage...");
		System.gc();
		log.info("Garbage Collected!");
	}

	public static void updatePercentDone() {
		ActiveOutput out = activeOutputs.get(currentActiveIndex);
		double percentDone = out.getPercentDone();
		UIControl.setGenerationPercentDone(percentDone);
		UIControl.setOverallPercentDone(overallDone);
	}

	public static void stop() {
		if (running) {
			ActiveOutput out = activeOutputs.get(currentActiveIndex);
			out.stop();
		}
	}
}
