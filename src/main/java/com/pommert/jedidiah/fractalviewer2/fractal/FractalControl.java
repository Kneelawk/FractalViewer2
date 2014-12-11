package com.pommert.jedidiah.fractalviewer2.fractal;

import java.util.TreeMap;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.mandelbrot1.Mandelbrot1Fractal;
import com.pommert.jedidiah.fractalviewer2.util.Colour;

public class FractalControl {

	public static TreeMap<String, AbstractFractal> fractals = new TreeMap<String, AbstractFractal>();

	private static TreeMap<String, JPanel> configGuis = new TreeMap<String, JPanel>();

	public static Logger log;

	public static void initFractals() {
		log = LogManager.getLogger("FractalControl");
		log.info("Init Fractals");

		// add fractals
		addFractal(new Mandelbrot1Fractal());
	}

	private static void addFractal(AbstractFractal fract) {
		fract.init();
		fractals.put(fract.getName(), fract);
	}

	public static void starting(String name) {
		getFractal(name).starting();
	}

	public static Colour generatePixel(String name, int x, int y) {
		return getFractal(name).getPixel(x, y);
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
