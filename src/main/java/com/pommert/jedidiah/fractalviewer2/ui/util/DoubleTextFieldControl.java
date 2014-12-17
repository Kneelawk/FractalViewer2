package com.pommert.jedidiah.fractalviewer2.ui.util;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JTextField;

public class DoubleTextFieldControl {
	public JTextField field;
	public double data;
	public ArrayList<DataChangeListener<Double>> listeners;

	public DoubleTextFieldControl(JTextField f, double d) {
		field = f;
		data = d;
		
		listeners = new ArrayList<DataChangeListener<Double>>();

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
				activateListeners(data);
			}
		});
	}

	public DoubleTextFieldControl(JTextField field) {
		this(field, Double.parseDouble(field.getText()));
	}
	
	protected void activateListeners(Double data) {
		for(DataChangeListener<Double> listener : listeners) {
			listener.dataChanged(data);
		}
	}
	
	public void addDataChangeListener(DataChangeListener<Double> listener) {
		listeners.add(listener);
	}
	
	public boolean removeDataChangeListener(DataChangeListener<Double> listener) {
		return listeners.remove(listener);
	}

	public DoubleTextFieldControl addTo(Container to) {
		to.add(field);
		return this;
	}

	public void set(double d) {
		data = d;
		field.setText(String.valueOf(d));
	}
}
