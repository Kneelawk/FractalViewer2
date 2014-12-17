package com.pommert.jedidiah.fractalviewer2.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.FractalViewer2;
import com.pommert.jedidiah.fractalviewer2.fractal.FractalControl;
import com.pommert.jedidiah.fractalviewer2.ui.opengl.GLControl;
import com.pommert.jedidiah.fractalviewer2.ui.util.DataChangeListener;
import com.pommert.jedidiah.fractalviewer2.ui.util.IntTextFieldControl;

public class UIControl {

	public static Logger log;

	public static JFrame frame;
	public static JTextField outputDirField;
	public static JTextField outputNameField;
	public static File previousDir = new File(System.getProperty("user.home"));
	public static IntTextFieldControl seedField;
	public static Random seedRng;
	public static IntTextFieldControl widthField;
	public static IntTextFieldControl heightField;
	public static JComboBox<String> fractalSelector;
	public static JScrollPane configPanel;
	public static JProgressBar generation;
	public static JProgressBar overall;
	public static JButton reOpen;
	public static JButton run;
	public static JCheckBox useGl;

	public static String[] fractalList;

	public static void initUI() {
		log = LogManager.getLogger("UIControl");
		log.info("Init UI");

		// set the look and feel to the os default
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			log.warn("Unable to set look and feel to system default: ", e);
		} catch (InstantiationException e) {
			log.warn("Unable to set look and feel to system default: ", e);
		} catch (IllegalAccessException e) {
			log.warn("Unable to set look and feel to system default: ", e);
		} catch (UnsupportedLookAndFeelException e) {
			log.warn("Unable to set look and feel to system default: ", e);
		}

		// build the gui

		// frame
		frame = new JFrame("Fractal Viewer 2");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				FractalViewer2.stop();
			}
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Image icon = null;
		try {
			icon = ImageIO
					.read(UIControl.class.getResourceAsStream("icon.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame.setIconImage(icon);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridBagLayout());
		frame.add("North", northPanel);
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.anchor = GridBagConstraints.WEST;

		// output dir
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		northPanel.add(new JLabel("Output Folder:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		outputDirField = new JTextField(30);
		northPanel.add(outputDirField, c);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 0;
		JButton selectFile = new JButton("Select...");
		northPanel.add(selectFile);

		// output file name
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 1;
		northPanel.add(new JLabel("Special File Identifier (optional):"), c);
		outputNameField = new JTextField(30);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		northPanel.add(outputNameField, c);

		// seed selection
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		northPanel.add(new JLabel("Fractal Seed:"), c);
		seedRng = new Random();
		int seed = seedRng.nextInt();
		seedField = new IntTextFieldControl(new JTextField(
				String.valueOf(seed), 30), seed);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		northPanel.add(seedField.field, c);
		JButton newSeedButton = new JButton("New Seed");
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		northPanel.add(newSeedButton, c);

		// size selection
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 3;
		northPanel.add(new JLabel("Image width:"), c);
		widthField = new IntTextFieldControl(new JTextField("1920", 30), 1920);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		northPanel.add(widthField.field, c);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 4;
		northPanel.add(new JLabel("Image height:"), c);
		heightField = new IntTextFieldControl(new JTextField("1080", 30), 1080);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		northPanel.add(heightField.field, c);

		// fractal selection
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 5;
		northPanel.add(new JLabel("Fractal selection:"), c);
		fractalList = FractalControl.list();
		fractalSelector = new JComboBox<String>(fractalList);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 5;
		northPanel.add(fractalSelector, c);

		// fractal config panel
		configPanel = new JScrollPane();
		configPanel.setBorder(new TitledBorder(new EtchedBorder(),
				fractalList[0] + " Properties"));
		frame.add("Center", configPanel);
		configPanel.setViewportView(FractalControl
				.getFractalConfigGui(fractalList[0]));
		configPanel.setPreferredSize(new Dimension(0, 10000));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		frame.add("South", buttonPanel);

		// progress bars
		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(progressPanel);
		// should display to opengl window
		useGl = new JCheckBox("Display fractal in OpenGL Window", true);
		progressPanel.add(useGl);
		generation = new JProgressBar();
		progressPanel.add(generation);
		generation.setString(getGenerationString(0));
		generation.setStringPainted(true);
		overall = new JProgressBar();
		progressPanel.add(overall);
		overall.setString(getOverallString(0));
		overall.setStringPainted(true);

		// control buttons
		JButton close = new JButton("Close");
		buttonPanel.add(close);
		reOpen = new JButton("Re-open Viewer");
		reOpen.setEnabled(false);
		buttonPanel.add(reOpen);
		run = new JButton("Run...");
		run.setEnabled(false);
		buttonPanel.add(run);

		// add Listeners

		// file possibility detection
		outputDirField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkRunButton();
			}
		});

		// more file possibility detection
		outputDirField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkRunButton();
			}
		});

		// file selection
		selectFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fd = new JFileChooser(previousDir);
				fd.setDialogTitle("Save Fractal Dir");
				fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fd.showOpenDialog(null);
				File file = fd.getSelectedFile().getAbsoluteFile();
				if (result == JFileChooser.APPROVE_OPTION) {
					previousDir = file;
					outputDirField.setText(file.getPath());
					checkRunButton();
				}
			}
		});

		// seed selection
		newSeedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seedField.set(seedRng.nextInt());
			}
		});

		// fractal selection
		fractalSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fractalName = (String) fractalSelector.getSelectedItem();
				configPanel.setBorder(new TitledBorder(new EtchedBorder(),
						fractalName + " Properties"));
				configPanel.setViewportView(FractalControl
						.getFractalConfigGui(fractalName));
			}
		});

		// use gl listener
		useGl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GLControl.glInteractionEnabled = useGl.isSelected();
			}
		});

		// control buttons
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FractalViewer2.stop();
			}
		});

		reOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GLControl.open();
			}
		});

		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fractalGenName = (String) fractalSelector.getSelectedItem();
				FractalControl.starting(fractalGenName, seed);
				
				String outputDir = outputDirField.getText();
				String outputName = outputNameField.getText();
				String outputFileName = outputName
						+ (outputName.length() > 0 ? "_" : "")
						+ FractalControl.getFileName(fractalGenName) + ".png";
				File outputFile = new File(outputDir, outputFileName);
				if (outputFile.exists()) {
					int num = 1;
					outputFileName = outputName
							+ (outputName.length() > 0 ? "_" : "")
							+ "#"
							+ num
							+ "_"
							+ FractalControl
									.getFileName(fractalGenName) + ".png";
					File newOutputFile = new File(outputDir, outputFileName);

					while (newOutputFile.exists()) {
						num++;
						outputFileName = outputName
								+ (outputName.length() > 0 ? "_" : "")
								+ "#"
								+ num
								+ "_"
								+ FractalControl
										.getFileName(fractalGenName) + ".png";
						newOutputFile = new File(outputDir, outputFileName);
					}

					int result = JOptionPane.CANCEL_OPTION;
					try {
						result = JOptionPane
								.showOptionDialog(
										frame,
										"Are you sure you want to override existing file:\n"
												+ outputFile.getCanonicalPath()
												+ "\nor do you just want to use the new file:\n"
												+ newOutputFile
														.getCanonicalPath(),
										"Override existing file?",
										JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.WARNING_MESSAGE, null,
										new Object[] {
												"Override Existing File",
												"Use New File", "Cancel" },
										"Use New File");
					} catch (HeadlessException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					if (result == JOptionPane.CANCEL_OPTION)
						return;
					else if (result == JOptionPane.NO_OPTION)
						outputFile = newOutputFile;
				}
				FractalViewer2.start(outputFile,
						fractalGenName,
						widthField.data, heightField.data);
			}
		});

		frame.setSize(800, 600);
		frame.setMinimumSize(new Dimension(800, 600));
	}

	public static void show() {
		log.info("Showing Frame");
		frame.setVisible(true);
	}

	public static boolean checkRunButton() {
		File file = new File(outputDirField.getText());

		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}

		if (checkFile(file) && (!file.exists() || file.isDirectory())) {
			updateRunButton(true);
			return true;
		} else {
			updateRunButton(false);
			return false;
		}
	}

	public static boolean checkFile(File file) {
		while (!file.exists()) {
			file = file.getParentFile();
		}
		return file.canWrite();
	}

	public static void updateRunButton(boolean hasOutput) {
		run.setEnabled(hasOutput);
	}

	public static void updateReopenButton(boolean isOpen, boolean hasBeenOpened) {
		reOpen.setEnabled(!isOpen && hasBeenOpened);
	}

	public static void setGenerationPercentDone(double progress) {
		generation.setValue((int) progress);
		generation.setString(getGenerationString(progress));
	}

	public static void setOverallPercentDone(double progress) {
		overall.setValue((int) progress);
		overall.setString(getOverallString(progress));
	}

	public static String getGenerationString(double progress) {
		return String.format("Fractal Generation: %s%%", progress);
	}

	public static String getOverallString(double progress) {
		return String.format("Overall Progress: %s%%", progress);
	}

	public static double getFractalWidth() {
		return widthField.data;
	}

	public static void addWidthDataChangeListener(
			DataChangeListener<Integer> listener) {
		widthField.addDataChangeListener(listener);
	}

	public static double getFractalHeight() {
		return heightField.data;
	}

	public static void addHeightDataChangeListener(
			DataChangeListener<Integer> listener) {
		heightField.addDataChangeListener(listener);
	}
}
