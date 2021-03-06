package com.pommert.jedidiah.fractalviewer2.fractal;

import java.util.TreeMap;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.customjs.CustomJsFractal;
import com.pommert.jedidiah.fractalviewer2.fractal.mandelbrot1.Mandelbrot1Fractal;
import com.pommert.jedidiah.fractalviewer2.util.KColor;

public class FractalControl {

	public static TreeMap<String, AbstractFractal> fractals = new TreeMap<String, AbstractFractal>();

	private static TreeMap<String, JPanel> configGuis = new TreeMap<String, JPanel>();

	public static Logger log;

	public static void initFractals() {
		log = LogManager.getLogger("FractalControl");
		log.info("Init Fractals");

		// add fractals
		addFractal(new Mandelbrot1Fractal());
		addFractal(new CustomJsFractal());
	}

	public static void destroyFractals() {
		fractals.forEach((String s, AbstractFractal f) -> f.destroy());
	}

	private static void addFractal(AbstractFractal fract) {
		fract.init();
		fractals.put(fract.getName(), fract);
	}

	public static void starting(String name, int seed)
			throws FractalStartFailedException {
		getFractal(name).starting(seed);
	}

	public static String getFileName(String name) {
		return getFractal(name).getFileName();
	}

	public static KColor generatePixel(String name, int x, int y) {
		KColor c = getFractal(name).getPixel(x, y);
		return (c != null ? c : new KColor(0, 0));
	}

	public static void finish(String name) {
		getFractal(name).finish();
	}

	public static String[] list() {
		return fractals.keySet().toArray(new String[0]);
	}

	public static JPanel getFractalConfigGui(String name) {
		if (configGuis.containsKey(name)) {
			return configGuis.get(name);
		}

		JPanel configGui = new JPanel();
		AbstractFractal fractal = getFractal(name);
		fractal.buildUI(configGui);
		configGuis.put(name, configGui);
		return configGui;
	}

	public static AbstractFractal getFractal(String name) {
		return fractals.get(name);
	}
}
