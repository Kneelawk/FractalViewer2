package com.pommert.jedidiah.fractalviewer2.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.FractalViewer2;
import com.pommert.jedidiah.fractalviewer2.fractal.FractalControl;

public class UIControl {

	public static Logger log;

	public static JFrame frame;
	public static JComboBox<String> fractalSelector;
	public static JScrollPane configPanel;
	public static JProgressBar generation;
	public static JProgressBar overall;

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

		JPanel selectionPanel = new JPanel();
		selectionPanel
				.setLayout(new BoxLayout(selectionPanel, BoxLayout.X_AXIS));
		selectionPanel.add(new JLabel("Fractal selection: ", JLabel.LEFT));
		fractalList = FractalControl.list();
		fractalSelector = new JComboBox<String>(fractalList);
		selectionPanel.add(fractalSelector);
		frame.add("North", selectionPanel);

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

		JPanel progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(progressPanel);
		generation = new JProgressBar();
		progressPanel.add(generation);
		generation.setString(getGenerationString(0));
		generation.setStringPainted(true);
		overall = new JProgressBar();
		progressPanel.add(overall);
		overall.setString(getOverallString(0));
		overall.setStringPainted(true);

		JButton close = new JButton("Close");
		buttonPanel.add(close);
		JButton run = new JButton("Run...");
		buttonPanel.add(run);

		// add Listeners
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				FractalViewer2.stop();
				System.exit(0);
			}
		});

		frame.setSize(600, 700);
	}

	public static void show() {
		log.info("Showing Frame");
		frame.setVisible(true);
	}

	public static String getGenerationString(int progress) {
		return String.format("Fractal Generation: %d%%", progress);
	}

	public static String getOverallString(int progress) {
		return String.format("Overall Progress: %d%%", progress);
	}
}
