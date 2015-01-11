package com.pommert.jedidiah.fractalviewer2.output.lwjgl;

import java.io.File;

import com.pommert.jedidiah.fractalviewer2.output.PassiveOutput;
import com.pommert.jedidiah.fractalviewer2.ui.opengl.GLControl;
import com.pommert.jedidiah.fractalviewer2.util.KColor;

public class LWJGLOutput extends PassiveOutput {

	@Override
	public void init() {

	}

	@Override
	public void setup(File file, int cols, int rows) {
		if (!GLControl.glInteractionEnabled)
			return;
		GLControl.open("Fractal Viewer: " + file.getName(), cols, rows);
	}

	@Override
	public void setPixel(int x, int y, KColor color) {
		if (!GLControl.glInteractionEnabled)
			return;
		GLControl.setPixel(x, y, color);
	}

	@Override
	public void save() {

	}

}
