package com.pommert.jedidiah.fractalviewer2.fractal;

import javax.swing.JPanel;

import com.pommert.jedidiah.fractalviewer2.util.Colour;

public abstract class AbstractFractal {

	public abstract void init();

	public abstract String getName();

	public abstract void buildUI(JPanel panel);

	public abstract void starting(int seed);

	public abstract String getFileName();

	public abstract Colour getPixel(int x, int y);

	public abstract void finish();
}
