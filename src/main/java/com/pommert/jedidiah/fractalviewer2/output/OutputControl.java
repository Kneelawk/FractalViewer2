package com.pommert.jedidiah.fractalviewer2.output;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pommert.jedidiah.fractalviewer2.util.Colour;

public class OutputControl {
	public static ArrayList<ActiveOutput> activeOutputs = new ArrayList<ActiveOutput>();
	public static ArrayList<PassiveOutput> passiveOutputs = new ArrayList<PassiveOutput>();

	private static void initActiveOutputs() {
		log.info("Init Active Outputs");
	}

	private static void initPassiveOutputs() {
		log.info("Init Passive Outputs");
	}

	public static Logger log;

	public static void initOutputs() {
		log = LogManager.getLogger("OutputControl");
		log.info("Init Output");

		initActiveOutputs();
		initPassiveOutputs();
	}

	public static Colour generatePixel(int x, int y) {
		return null;
	}
}
