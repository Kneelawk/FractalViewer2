package com.pommert.jedidiah.fractalviewer2.ui.util;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import com.pommert.jedidiah.fractalviewer2.ui.UIControl;

public class DoubleTextFieldControl {
	public JTextField field;
	public double data;

	public DoubleTextFieldControl(JTextField f, double d) {
		field = f;
		data = d;

		field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = field.getText();
				try {
					data = Double.parseDouble(text);
				} catch (NumberFormatException nfe) {
					if (text.equals(""))
						data = 0;
					else
						field.setText(String.valueOf(data));

				}
				UIControl.log.debug("Input: " + text + ", data: " + data);
			}
		});
	}

	public DoubleTextFieldControl(JTextField field) {
		this(field, Double.parseDouble(field.getText()));
	}

	public DoubleTextFieldControl addTo(Container to) {
		to.add(field);
		return this;
	}
}
