package com.pommert.jedidiah.fractalviewer2.output;

import com.pommert.jedidiah.fractalviewer2.util.Colour;

public abstract class PassiveOutput {
	public abstract void init();

	public abstract void file(String filename);

	public abstract void setPixel(int x, int y, Colour color);

	public abstract void save();
}
