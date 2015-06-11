package com.pommert.jedidiah.fractalviewer2.output;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.FractalControl;
import com.pommert.jedidiah.fractalviewer2.output.lwjgl.LWJGLOutput;
import com.pommert.jedidiah.fractalviewer2.output.pngj.PngjOutput;
import com.pommert.jedidiah.fractalviewer2.ui.UIControl;
import com.pommert.jedidiah.fractalviewer2.util.KColor;

public class OutputControl {
	public static ArrayList<ActiveOutput> activeOutputs = new ArrayList<ActiveOutput>();
	public static ArrayList<PassiveOutput> passiveOutputs = new ArrayList<PassiveOutput>();
	public static boolean running = false;
	public static boolean stopping = false;
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

	public static synchronized void start(String fractalGeneratorName,
			int width, int height, int seed, String outputDirName,
			String outputName) {
		if (!running) {
			running = true;
			generationThread = new Thread(new Runnable() {
				@Override
				public void run() {
					_start(fractalGeneratorName, width, height, seed,
							outputDirName, outputName);
				}
			}, "Generation Thread");
			generationThread.start();
		}
	}

	protected static void _start(String fractalGeneratorName, int width,
			int height, int seed, String outputDirName, String outputName) {
		try {
			File file = setupFile(fractalGeneratorName, seed, outputDirName,
					width, height, outputName);

			resetUpdates();

			UIControl.updateCancelButton(running);

			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();

			log.info("Setting Up Outputs");
			for (ActiveOutput out : activeOutputs) {
				out.setup(file, width, height);
			}
			for (PassiveOutput out : passiveOutputs) {
				out.setup(file, width, height);
			}

			currentFractalGeneratorName = fractalGeneratorName;

			log.info("Setting Up Fractal Generator");

			log.info("Starting Fractal Generation");
			overallDone = 0;
			int maxOutput = activeOutputs.size();
			for (currentActiveIndex = 0; currentActiveIndex < maxOutput
					&& !stopping; currentActiveIndex++) {
				ActiveOutput out = activeOutputs.get(currentActiveIndex);
				out.run();
				out.save(stopping);
				if (!stopping)
					overallDone = ((double) currentActiveIndex * 100)
							/ maxOutput;
			}

			currentActiveIndex = 0;

			for (PassiveOutput out : passiveOutputs) {
				out.save();
			}

			if (!stopping)
				overallDone = 100;

			FractalControl.finish(fractalGeneratorName);

			updatePercentDone();
			collectGarbage();

			if (stopping)
				log.info("Stopping Incomplete Fractal Generation!");
			else
				log.info("Finished Fractal Generation!");

		} catch (GenerationFailedException e) {
			log.error("Error during fractal generation!", e);
			JOptionPane.showMessageDialog(UIControl.getFrame(),
					"Error during fractal generation:\n" + e.toString(),
					"Error during fractal generation!",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			UIControl.enableFrame();

			running = false;

			UIControl.updateCancelButton(running);

			stopping = false;
		}
	}

	private static File setupFile(String fractalGenName, int seed,
			String outputDirName, int imageWidth, int imageHeight,
			String outputName) {
		try {
			FractalControl.starting(fractalGenName, seed);
		} catch (Throwable t) {
			throw new GenerationFailedException(
					"Error starting fractal generator: " + fractalGenName, t);
		}

		File outputDir = new File(outputDirName).getAbsoluteFile();
		String outputFileName = outputName
				+ (outputName.length() > 0 ? "_" : "")
				+ FractalControl.getFileName(fractalGenName) + "_" + imageWidth
				+ "x" + imageHeight + ".png";
		File outputFile = new File(outputDir, outputFileName);
		if (outputFile.exists()) {
			int num = 1;
			outputFileName = outputName + (outputName.length() > 0 ? "_" : "")
					+ "#" + num + "_"
					+ FractalControl.getFileName(fractalGenName) + "_"
					+ imageWidth + "x" + imageHeight + ".png";
			File newOutputFile = new File(outputDir, outputFileName);

			while (newOutputFile.exists()) {
				num++;
				outputFileName = outputName
						+ (outputName.length() > 0 ? "_" : "") + "#" + num
						+ "_" + FractalControl.getFileName(fractalGenName)
						+ "_" + imageWidth + "x" + imageHeight + ".png";
				newOutputFile = new File(outputDir, outputFileName);
			}

			int result = JOptionPane.CANCEL_OPTION;
			try {
				result = JOptionPane
						.showOptionDialog(
								UIControl.getFrame(),
								"Are you sure you want to override existing file:\n"
										+ outputFile.getCanonicalPath()
										+ "\nor do you just want to use the new file:\n"
										+ newOutputFile.getCanonicalPath(),
								"Override existing file?",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE, null,
								new Object[] { "Override Existing File",
										"Use New File", "Cancel" },
								"Use New File");
			} catch (HeadlessException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (result == JOptionPane.CANCEL_OPTION)
				throw new GenerationFailedException("Generation Canceled");
			else if (result == JOptionPane.NO_OPTION)
				outputFile = newOutputFile;
		}
		return outputFile;
	}

	public static void resetUpdates() {
		updates = 0;
	}

	public static KColor generatePixel(int x, int y) {
		KColor c = null;
		try {
			c = FractalControl.generatePixel(currentFractalGeneratorName, x, y);
			for (PassiveOutput out : passiveOutputs) {
				out.setPixel(x, y, c);
			}
		} catch (Throwable t) {
			throw new GenerationFailedException(
					"Error during pixel generation: " + t.toString(), t);
		}
		return c == null ? new KColor(0, 0) : c;
	}

	public static void update() {
		updatePercentDone();
		if (updates >= 1000000) {
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
		UIControl.setOverallPercentDone(overallDone
				+ (overallDone == 100 ? 0
						: (percentDone / activeOutputs.size())));
	}

	public static void stop() {
		if (running) {
			stopping = true;
			ActiveOutput out = activeOutputs.get(currentActiveIndex);
			out.stop();
		}
	}
}
