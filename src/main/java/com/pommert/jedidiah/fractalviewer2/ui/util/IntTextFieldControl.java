package com.pommert.jedidiah.fractalviewer2.ui.util;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JTextField;

public class IntTextFieldControl {
	public JTextField field;
	public int data;
	public ArrayList<DataChangeListener<Integer>> listeners;

	public IntTextFieldControl(JTextField f, int i) {
		field = f;
		data = i;
		listeners = new ArrayList<DataChangeListener<Integer>>();

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
				activateListeners(data);
			}
		});
	}

	public IntTextFieldControl(JTextField f) {
		this(f, Integer.parseInt(f.getText()));
	}

	protected void activateListeners(Integer data) {
		for (DataChangeListener<Integer> listener : listeners) {
			listener.dataChanged(data);
		}
	}

	public void addDataChangeListener(DataChangeListener<Integer> listener) {
		listeners.add(listener);
	}

	public boolean removeDataChangeListener(DataChangeListener<Integer> listener) {
		return listeners.remove(listener);
	}

	public IntTextFieldControl addTo(Container to) {
		to.add(field);
		return this;
	}

	public void set(int i) {
		data = i;
		field.setText(String.valueOf(i));
	}
}
