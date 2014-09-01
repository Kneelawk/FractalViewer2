package com.pommert.jedidiah.fractalviewer2.output.pngj;

import java.io.File;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;

import com.pommert.jedidiah.fractalviewer2.output.ActiveOutput;
import com.pommert.jedidiah.fractalviewer2.util.Colour;

public class PngjOutput extends ActiveOutput {

	public ImageInfo info;
	public PngWriter writer;

	@Override
	public void init() {
		// nothing happens here
	}

	@Override
	public void setup(File file, int cols, int rows) {
		info = new ImageInfo(cols, rows, 8, true);
		writer = new PngWriter(file, info);
	}

	@Override
	public void run() {
		for (int y = 0; y < info.rows; y++) {
			int[] raw = new int[info.cols * 4];
			for (int x = 0; x < info.cols; x++) {
				updateOutput();
				Colour pixel = generatePixel(x, y);
				if (pixel == null)
					continue;
				raw[x * 4] = pixel.getRed();
				raw[x * 4 + 1] = pixel.getGreen();
				raw[x * 4 + 2] = pixel.getBlue();
				raw[x * 4 + 3] = pixel.getAlpha();
			}
			IImageLine line = new ImageLineInt(info, raw);
			writer.writeRow(line, y);
		}
	}

	@Override
	public void save() {
		writer.end();
		writer.close();
	}
}
