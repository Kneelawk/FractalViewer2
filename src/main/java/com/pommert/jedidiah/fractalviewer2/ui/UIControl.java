package com.pommert.jedidiah.fractalviewer2.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UIControl {

	public static Logger log;

	public static void initUI() {
		log = LogManager.getLogger("UIControl");
		log.info("Init UI");
	}
}
