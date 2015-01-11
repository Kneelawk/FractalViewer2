package com.pommert.jedidiah.fractalviewer2.fractal;

import javax.swing.JPanel;

import com.pommert.jedidiah.fractalviewer2.util.KColor;

public abstract class AbstractFractal {

	public abstract void init();

	public abstract String getName();

	public abstract void buildUI(JPanel panel);

	public abstract void starting(int seed) throws FractalStartFailedException;

	public abstract String getFileName();

	public abstract KColor getPixel(int x, int y);

	public abstract void finish();
	
	public abstract void destroy();
}
