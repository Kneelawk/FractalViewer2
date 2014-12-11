package com.pommert.jedidiah.fractalviewer2;

import java.lang.reflect.Field;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Full test
	 */
	public void testApp() {
		testClassLoader();
	}

	/**
	 * Test if ClassLoader is compatible
	 */
	public void testClassLoader() {
		Field[] declaredFields = ClassLoader.class.getDeclaredFields();
		boolean containsField = false;
		for (Field field : declaredFields) {
			if (field.getName().equals("usr_paths")) {
				if (field.getType().equals(String[].class)) {
					containsField = true;
				}
			}
		}
		assertTrue(ClassLoader.class.getName()
				+ " does not have a field: \"usr_paths\" of type String[]!",
				containsField);
	}
}
