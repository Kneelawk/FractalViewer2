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

	public boolean running = false;
	public double percentDone = 0;

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
		running = true;
		percentDone = 0;
		for (int y = 0; y < info.rows && running; y++) {
			int[] raw = new int[info.cols * 4];
			for (int x = 0; x < info.cols && running; x++) {
				Colour pixel = generatePixel(x, y);
				if (pixel == null)
					continue;
				raw[x * 4] = pixel.getRed();
				raw[x * 4 + 1] = pixel.getGreen();
				raw[x * 4 + 2] = pixel.getBlue();
				raw[x * 4 + 3] = pixel.getAlpha();

				// calculate percent done
				double currentPixel = (y * info.cols) + x;
				percentDone = (currentPixel * 100.0) / (info.rows * info.cols);

				// update output for things like garbage collection and
				// percentage updates
				updateOutput();
			}
			IImageLine line = new ImageLineInt(info, raw);
			writer.writeRow(line, y);
		}

		percentDone = 100;

		running = false;
	}

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public void save(boolean stopping) {
		writer.end();
		writer.close();
	}

	@Override
	public double getPercentDone() {
		return percentDone;
	}
}
