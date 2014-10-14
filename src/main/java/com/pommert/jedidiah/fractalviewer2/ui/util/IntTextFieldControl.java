package com.pommert.jedidiah.fractalviewer2.ui.util;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class IntTextFieldControl {
	public JTextField field;
	public int data;

	public IntTextFieldControl(JTextField f, int i) {
		field = f;
		data = i;

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
					data = Integer.parseInt(text);
				} catch (NumberFormatException nfe) {
					if (text.equals(""))
						data = 0;
					else
						field.setText(String.valueOf(data));
				}
			}
		});
	}

	public IntTextFieldControl(JTextField f) {
		this(f, Integer.parseInt(f.getText()));
	}

	public IntTextFieldControl addTo(Container to) {
		to.add(field);
		return this;
	}
}
