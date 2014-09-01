package com.pommert.jedidiah.fractalviewer2.ui.custom;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.Scrollable;

public class JSpring extends JComponent implements Scrollable {
	private static final long serialVersionUID = -9029516951053528054L;

	public JSpring(Dimension preferredSize) {
		setPreferredSize(preferredSize);
	}

	public JSpring(int prefWidth, int prefHeight) {
		this(new Dimension(prefWidth, prefHeight));
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(0, 0);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 50;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 100;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}
