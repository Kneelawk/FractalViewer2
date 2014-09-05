package com.pommert.jedidiah.fractalviewer2.fractal.mandelbrot1;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pommert.jedidiah.fractalviewer2.fractal.AbstractFractal;
import com.pommert.jedidiah.fractalviewer2.ui.util.DoubleTextFieldControl;
import com.pommert.jedidiah.fractalviewer2.util.Colour;

public class Mandelbrot1Fractal extends AbstractFractal {

	// generation plane width
	private DoubleTextFieldControl widthField;

	// generation plane width
	private DoubleTextFieldControl heightField;
	private JCheckBox calculateHeight;

	@Override
	public void init() {

	}

	@Override
	public String getName() {
		return "Mandelbrot1";
	}

	@Override
	public void buildUI(JPanel panel) {
		JPanel root = new JPanel();
		root.setLayout(new GridBagLayout());
		panel.add(root);
		GridBagConstraints c = new GridBagConstraints();

		// generation plane width
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		root.add(new JLabel("Generation Plane Width: "), c);
		widthField = new DoubleTextFieldControl(new JTextField("5.0", 20));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		root.add(widthField.field, c);

		// generation plane height
		JPanel heightPanel = new JPanel();
		root.add(heightPanel);
	}

	@Override
	public void starting() {

	}

	@Override
	public String getFileName(String currentFileNameStart,
			String currentFileNameEnd) {
		return currentFileNameStart + "-" + getName() + "-"
				+ currentFileNameEnd;
	}

	@Override
	public Colour getPixel(int x, int y) {
		return null;
	}

	@Override
	public void finish() {

	}
}
