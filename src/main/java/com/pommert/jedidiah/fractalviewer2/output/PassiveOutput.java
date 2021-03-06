package com.pommert.jedidiah.fractalviewer2.output;

import java.io.File;

import com.pommert.jedidiah.fractalviewer2.util.KColor;

public abstract class PassiveOutput {
	public abstract void init();

	public abstract void setup(File file, int cols, int rows);

	public abstract void setPixel(int x, int y, KColor color);

	public abstract void save();
}
