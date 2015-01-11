package com.pommert.jedidiah.fractalviewer2.fractal.mandelbrot1;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.fractal.AbstractFractal;
import com.pommert.jedidiah.fractalviewer2.fractal.FractalStartFailedException;
import com.pommert.jedidiah.fractalviewer2.ui.UIControl;
import com.pommert.jedidiah.fractalviewer2.ui.util.DataChangeListener;
import com.pommert.jedidiah.fractalviewer2.ui.util.DoubleTextFieldControl;
import com.pommert.jedidiah.fractalviewer2.ui.util.IntTextFieldControl;
import com.pommert.jedidiah.fractalviewer2.util.KColor;

public class Mandelbrot1Fractal extends AbstractFractal {

	public Logger log;

	// generation plane center x
	private DoubleTextFieldControl planeCenterXField;
	private double planeCenterX;
	private double planeMinX;

	// generation plane canter y
	private DoubleTextFieldControl planeCenterYField;
	private double planeCenterY;
	private double planeMinY;

	// generation plane width
	private DoubleTextFieldControl planeWidthField;
	private double planeWidth;

	// generation plane width
	private DoubleTextFieldControl planeHeightField;
	private double planeHeight;
	private JCheckBox calculatePlaneHeightCheck;
	private boolean calculatePlaneHeight;

	// max iterations
	private IntTextFieldControl mitsField;
	private int mits;
	private JCheckBox useDefaultMitsCheck;
	private boolean useDefaultMits;

	// cross hairs
	private JCheckBox crossHairsCheck;
	private boolean crossHairs;

	// box outline
	private JPanel boxPanel;
	private DoubleTextFieldControl boxCenterXField;
	private double boxCenterX;
	private double boxMinX;
	private double boxMaxX;
	private DoubleTextFieldControl boxCenterYField;
	private double boxCenterY;
	private double boxMinY;
	private double boxMaxY;
	private DoubleTextFieldControl boxWidthField;
	private double boxWidth;
	private DoubleTextFieldControl boxHeightField;
	private double boxHeight;
	private JCheckBox calculateBoxHeightCheck;
	private boolean calculateBoxHeight;
	private JCheckBox drawBoxCheck;
	private boolean drawBox;

	// color scheme
	private static final String[] colorSchemes = { "Hue", "Random", "Segment",
			"Striped" };
	private JComboBox<String> colorSchemeCBox;
	private String colorScheme;
	private JPanel huePanel;
	private DoubleTextFieldControl hueMultiplierField;
	private double hueMultiplier;
	private DoubleTextFieldControl brightnessMultiplierField;
	private double brightnessMultiplier;

	private int seed;
	private Random rng;
	private int randomOffset;

	@Override
	public void init() {
		log = LogManager.getLogger(getName());
		log.info("Init Mandelbrot1");
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
		c.ipadx = 5;

		// generation plane center x
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		root.add(new JLabel("Generation Plane X Center:"), c);
		planeCenterXField = new DoubleTextFieldControl(new JTextField("0.0"), 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		root.add(planeCenterXField.field, c);

		// generation plane center y
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 1;
		root.add(new JLabel("Generation Plane Y Center:"), c);
		planeCenterYField = new DoubleTextFieldControl(new JTextField("0.0"), 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		root.add(planeCenterYField.field, c);

		// generation plane width
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		root.add(new JLabel("Generation Plane Width:"), c);
		planeWidthField = new DoubleTextFieldControl(new JTextField("5.0", 20),
				5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		root.add(planeWidthField.field, c);

		// generation plane height
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 3;
		root.add(new JLabel("Generation Plane Height:"), c);
		double planeHeight = getHeight(5, UIControl.getFractalWidth(),
				UIControl.getFractalHeight());
		planeHeightField = new DoubleTextFieldControl(new JTextField(
				String.valueOf(planeHeight), 20), planeHeight);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		root.add(planeHeightField.field, c);
		planeHeightField.field.setEnabled(false);
		calculatePlaneHeightCheck = new JCheckBox("Calculate Height", true);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		root.add(calculatePlaneHeightCheck, c);

		// max iterations
		c.gridx = 0;
		c.gridy = 4;
		root.add(new JLabel("Max Iterations:"), c);
		mitsField = new IntTextFieldControl(new JTextField("100", 20), 100);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		root.add(mitsField.field, c);
		mitsField.field.setEnabled(false);
		useDefaultMitsCheck = new JCheckBox("Default Max Iterations", true);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		root.add(useDefaultMitsCheck, c);

		// cross hairs
		crossHairsCheck = new JCheckBox("Cross Hairs");
		c.gridx = 1;
		c.gridy = 5;
		root.add(crossHairsCheck, c);
		crossHairsCheck
				.setToolTipText("\"Cross Hairs\" forces the generator to put a red cross hair at 0,0");

		// box outline
		boxPanel = new JPanel();
		boxPanel.setLayout(new GridBagLayout());
		boxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Box Outline"));

		c.gridx = 0;
		c.gridy = 0;
		boxPanel.add(new JLabel("Box X Center:"), c);
		boxCenterXField = new DoubleTextFieldControl(new JTextField("0.0", 20),
				0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		boxPanel.add(boxCenterXField.field, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 1;
		boxPanel.add(new JLabel("Box Y Center:"), c);
		boxCenterYField = new DoubleTextFieldControl(new JTextField("0.0", 20),
				0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		boxPanel.add(boxCenterYField.field, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		boxPanel.add(new JLabel("Box Width:"), c);
		boxWidthField = new DoubleTextFieldControl(new JTextField("0.0", 20), 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		boxPanel.add(boxWidthField.field, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 3;
		boxPanel.add(new JLabel("Box Height:"), c);
		double boxHeightCalc = getHeight(0, UIControl.getFractalWidth(),
				UIControl.getFractalWidth());
		boxHeightField = new DoubleTextFieldControl(new JTextField(
				String.valueOf(boxHeightCalc), 20), boxHeightCalc);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		boxPanel.add(boxHeightField.field, c);
		calculateBoxHeightCheck = new JCheckBox("Calculate Box Height", true);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		boxPanel.add(calculateBoxHeightCheck, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 6;
		root.add(boxPanel, c);
		setEnabled(boxPanel, false, null);
		drawBoxCheck = new JCheckBox("Draw Box");
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 2;
		root.add(drawBoxCheck, c);

		// color scheme selection
		c.gridx = 0;
		c.gridy = 7;
		root.add(new JLabel("Color Scheme:"), c);
		colorSchemeCBox = new JComboBox<String>(colorSchemes);
		colorSchemeCBox.setSelectedIndex(0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		root.add(colorSchemeCBox, c);

		// hue arguemnts
		huePanel = new JPanel();
		huePanel.setLayout(new GridBagLayout());
		huePanel.setBorder(new TitledBorder(new EtchedBorder(),
				"Hue Color Scheme Arguments"));

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		huePanel.add(new JLabel("Hue Multiplier:"), c);
		hueMultiplierField = new DoubleTextFieldControl(new JTextField("3.3",
				30), 3.3);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		huePanel.add(hueMultiplierField.field, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 1;
		huePanel.add(new JLabel("Birghtness Multiplier:"), c);
		brightnessMultiplierField = new DoubleTextFieldControl(new JTextField(
				"16.0", 30), 16);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		huePanel.add(brightnessMultiplierField.field, c);

		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 8;
		root.add(huePanel, c);

		// listeners

		// image width change
		UIControl.addWidthDataChangeListener(new DataChangeListener<Integer>() {
			@Override
			public void dataChanged(Integer data) {
				if (calculatePlaneHeightCheck.isSelected()) {
					planeHeightField.set(getHeight(planeWidthField.data, data,
							UIControl.getFractalHeight()));
				}

				if (calculateBoxHeightCheck.isSelected()) {
					boxHeightField.set(getHeight(boxWidthField.data, data,
							UIControl.getFractalHeight()));
				}
			}
		});

		// image height change
		UIControl
				.addHeightDataChangeListener(new DataChangeListener<Integer>() {
					@Override
					public void dataChanged(Integer data) {
						if (calculatePlaneHeightCheck.isSelected()) {
							planeHeightField.set(getHeight(
									planeWidthField.data,
									UIControl.getFractalWidth(), data));
						}

						if (calculateBoxHeightCheck.isSelected()) {
							boxHeightField.set(getHeight(boxWidthField.data,
									UIControl.getFractalWidth(), data));
						}
					}
				});

		// plane width change
		planeWidthField.addDataChangeListener(new DataChangeListener<Double>() {
			@Override
			public void dataChanged(Double data) {
				if (calculatePlaneHeightCheck.isSelected()) {
					planeHeightField.set(getHeight(data,
							UIControl.getFractalWidth(),
							UIControl.getFractalHeight()));
				}
			}
		});

		// calculate plane height check change
		calculatePlaneHeightCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = calculatePlaneHeightCheck.isSelected();
				planeHeightField.field.setEnabled(!selected);

				if (selected) {
					planeHeightField.set(getHeight(planeWidthField.data,
							UIControl.getFractalWidth(),
							UIControl.getFractalHeight()));
				}
			}
		});

		// default mits check change
		useDefaultMitsCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = useDefaultMitsCheck.isSelected();
				mitsField.field.setEnabled(!selected);

				if (selected) {
					mitsField.set(100);
				}
			}
		});

		// draw box check change
		drawBoxCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = drawBoxCheck.isSelected();
				setEnabled(boxPanel, selected,
						new Component[] { boxHeightField.field });

				boxHeightField.field.setEnabled(selected
						&& !calculateBoxHeightCheck.isSelected());
			}
		});

		// box width change
		boxWidthField.addDataChangeListener(new DataChangeListener<Double>() {
			@Override
			public void dataChanged(Double data) {
				if (calculateBoxHeightCheck.isSelected()) {
					boxHeightField.set(getHeight(data,
							UIControl.getFractalWidth(),
							UIControl.getFractalHeight()));
				}
			}
		});

		// calculate box height check change
		calculateBoxHeightCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean enable = drawBoxCheck.isSelected()
						&& !calculateBoxHeightCheck.isSelected();
				boxHeightField.field.setEnabled(enable);

				if (!enable) {
					boxHeightField.set(getHeight(boxWidthField.data,
							UIControl.getFractalWidth(),
							UIControl.getFractalHeight()));
				}
			}
		});

		// color scheme change
		colorSchemeCBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEnabled(huePanel, colorSchemeCBox.getSelectedIndex() == 0,
						null);
			}
		});
	}

	private void setEnabled(Container cont, boolean enabled, Component[] notEdit) {
		List<Component> notEditList = null;
		if (notEdit != null && notEdit.length > 0) {
			notEditList = Arrays.<Component> asList(notEdit);
		} else {
			notEditList = new ArrayList<Component>();
		}

		Component[] comps = cont.getComponents();
		for (Component comp : comps) {
			if (!notEditList.contains(comp))
				comp.setEnabled(enabled);
		}
	}

	@Override
	public void starting(int seed) throws FractalStartFailedException {
		this.seed = seed;

		planeCenterX = planeCenterXField.data;

		planeCenterY = planeCenterYField.data;

		planeWidth = planeWidthField.data;

		planeHeight = planeHeightField.data;

		calculatePlaneHeight = calculatePlaneHeightCheck.isSelected();
		if (calculatePlaneHeight) {
			planeHeight = getHeight(planeWidth, UIControl.getFractalWidth(),
					UIControl.getFractalHeight());
			planeHeightField.set(planeHeight);
		}

		planeMinX = planeCenterX - planeWidth / 2;
		planeMinY = planeCenterY - planeHeight / 2;

		mits = mitsField.data;

		useDefaultMits = useDefaultMitsCheck.isSelected();
		if (useDefaultMits) {
			mits = 100;
			mitsField.set(mits);
		}

		crossHairs = crossHairsCheck.isSelected();

		boxCenterX = boxCenterXField.data;
		boxCenterY = boxCenterYField.data;
		boxWidth = boxWidthField.data;
		boxHeight = boxHeightField.data;
		calculateBoxHeight = calculateBoxHeightCheck.isSelected();
		if (calculateBoxHeight) {
			boxHeight = getHeight(boxWidth, UIControl.getFractalWidth(),
					UIControl.getFractalHeight());
			boxHeightField.set(boxHeight);
		}

		boxMinX = boxCenterX - boxWidth / 2;
		boxMaxX = boxCenterX + boxWidth / 2;
		boxMinY = boxCenterY - boxHeight / 2;
		boxMaxY = boxCenterY + boxHeight / 2;

		drawBox = drawBoxCheck.isSelected();

		colorScheme = (String) colorSchemeCBox.getSelectedItem();

		hueMultiplier = hueMultiplierField.data;

		brightnessMultiplier = brightnessMultiplierField.data;

		rng = new Random(seed);
		randomOffset = rng.nextInt();

		log.info(String.format("Mandelbrot1 Generator settings:\n"
				+ "generated plane xmin: %s,\ngenerated plane ymin %s,\n"
				+ "generated plane width: %s,\n"
				+ "generated plane height: %s,\nmax iterations: %d,\n"
				+ "color scheme: %s,\nhue multiplier: %s,\n"
				+ "brightness multiplier: %s,\nseed: %d,\n"
				+ "random offset: %d.", planeMinX, planeMinY, planeWidth,
				planeHeight, mits, colorScheme, hueMultiplier,
				brightnessMultiplier, seed, randomOffset));
	}

	@Override
	public String getFileName() {
		return getName()
				+ "_x"
				+ planeCenterX
				+ "_y"
				+ planeCenterY
				+ "_w"
				+ planeWidth
				+ "_h"
				+ planeHeight
				+ "_it"
				+ mits
				+ (crossHairs ? "_ch" : "")
				+ (drawBox ? "_box(" + boxCenterX + "," + boxCenterY + ")("
						+ boxWidth + "x" + boxHeight + ")" : "")
				+ "_cs:"
				+ colorScheme
				+ (colorScheme.equals("Random") ? "_seed:" + seed : "")
				+ (colorScheme.equals("Hue") ? "_hm" + hueMultiplier + "_bm"
						+ brightnessMultiplier : "");
	}

	@Override
	public KColor getPixel(int pixelX, int pixelY) {
		double x = (((double) pixelX) / ((double) UIControl.getFractalWidth()))
				* planeWidth + planeMinX;
		double y = (((double) pixelY) / ((double) UIControl.getFractalHeight()))
				* planeHeight + planeMinY;
		double nx = (((double) (pixelX + 1)) / ((double) UIControl
				.getFractalWidth())) * planeWidth + planeMinX;
		double ny = (((double) (pixelY + 1)) / ((double) UIControl
				.getFractalHeight())) * planeHeight + planeMinY;

		double a = x;
		double b = y;

		int n = 0;
		for (n = 0; n < mits; n++) {
			double aa = a * a;
			double bb = b * b;

			double twoab = 2.0 * a * b;

			a = aa - bb + x;
			b = twoab + y;

			if (aa + bb > 16.0) {
				break;
			}
		}

		if (drawBox
				&& ((y <= boxMaxY && boxMaxY < ny && boxMinX <= x && x <= boxMaxX)
						|| (y <= boxMinY && boxMinY < ny && boxMinX <= x && x <= boxMaxX)
						|| (x <= boxMaxX && boxMaxX < nx && boxMinY <= y && y <= boxMaxY) || (x <= boxMinX
						&& boxMinX < nx && boxMinY <= y && y <= boxMaxY))) {
			return new KColor(255);
		} else if (crossHairs && ((x <= 0 && 0 < nx) || (y <= 0 && 0 < ny))) {
			return new KColor(255, 0, 0);
		} else if (n >= mits) {
			return new KColor(0);
		} else {
			if (colorScheme.equals("Segment")) {
				// segment color scheme
				return new KColor((((n * 18) >> 0) % 0xF) * 16,
						(((n * 18) >> 4) % 0xF) * 16,
						(((n * 18) >> 8) % 0xF) * 16);
			} else if (colorScheme.equals("Striped")) {
				// striped color scheme
				return new KColor((n * 16 % 256), (int) a, (int) b);
			} else if (colorScheme.equals("Random")) {
				// random color scheme
				rng.setSeed(n + randomOffset);
				return new KColor(rng.nextInt(222), rng.nextInt(222),
						rng.nextInt(222));
			} else {
				// hue color scheme
				return KColor.fromHSB((n * hueMultiplier % 256f) / 256f, 1.0f,
						(n * brightnessMultiplier % 256f) / 256f);
			}
		}
	}

	private double getHeight(double planeWidth, double windowWidth,
			double windowHeight) {
		if (windowWidth <= 0)
			return 0;
		return ((0.595294 * planeWidth * windowHeight) / windowWidth) * 1.6;
	}

	@Override
	public void finish() {
		// Nothing to do here
	}

	@Override
	public void destroy() {
		// Nothing to do here
	}
}
