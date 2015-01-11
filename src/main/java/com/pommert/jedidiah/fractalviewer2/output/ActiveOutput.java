package com.pommert.jedidiah.fractalviewer2.output;

import java.io.File;

import com.pommert.jedidiah.fractalviewer2.util.KColor;

public abstract class ActiveOutput {
	public abstract void init();

	public abstract void setup(File file, int cols, int rows);

	public abstract void run();

	/**
	 * Only used if the user cancels
	 */
	public abstract void stop();

	public abstract void save(boolean stopping);

	/**
	 * Used by the active outputs to tell the OutputControl to generate the
	 * pixel at the specified location.
	 * 
	 * @param x
	 *            the x location of the pixel to be generated.
	 * @param y
	 *            the y location of the pixel to be generated.
	 * @return the pixel generated.
	 */
	protected KColor generatePixel(int x, int y) {
		return OutputControl.generatePixel(x, y);
	}

	protected void updateOutput() {
		OutputControl.update();
	}

	public double getPercentDone() {
		return 50;
	}
}
