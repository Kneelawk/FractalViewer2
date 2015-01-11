package com.pommert.jedidiah.fractalviewer2.fractal.customjs;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import com.pommert.jedidiah.fractalviewer2.fractal.AbstractFractal;
import com.pommert.jedidiah.fractalviewer2.fractal.FractalStartFailedException;
import com.pommert.jedidiah.fractalviewer2.ui.UIControl;
import com.pommert.jedidiah.fractalviewer2.util.KColor;

public class CustomJsFractal extends AbstractFractal {

	public Logger log;
	public Context cx;
	public Scriptable scope;
	public JTextField scriptFileField;
	public FileDialog fd;
	public int seed;
	public File scriptFile;
	public Reader scriptReader;
	public Function getFileNameFunc;
	public Function getPixelFunc;
	public Function finishFunc;
	public Object jsLogObj;
	public Function js_getLogger = new BaseFunction() {
		private static final long serialVersionUID = -7806263935431026055L;

		public Object call(Context cx, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			return jsLogObj;
		}

		public String getFunctionName() {
			return "getLogger";
		}
	};
	public Function js_getWidth = new BaseFunction() {
		private static final long serialVersionUID = -433407223427128257L;

		public Object call(Context cx, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			return Integer.valueOf(UIControl.getFractalWidth());
		}

		public String getFunctionName() {
			return "getWidth";
		}
	};
	public Function js_getHeight = new BaseFunction() {
		private static final long serialVersionUID = -3142364730065227287L;

		public Object call(Context cx, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			return Integer.valueOf(UIControl.getFractalHeight());
		}

		public String getFunctionName() {
			return "getHeight";
		}
	};
	public Function js_getSeed = new BaseFunction() {
		private static final long serialVersionUID = 6677578637932305L;

		public Object call(Context cx, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			return Integer.valueOf(seed);
		}

		public String getFunctionName() {
			return "getSeed";
		}
	};

	@Override
	public void init() {
		log = LogManager.getLogger("CustomJsFractal");
		log.info("Init CustomJs");
	}

	@Override
	public String getName() {
		return "CustomJs";
	}

	@Override
	public void buildUI(JPanel panel) {
		JPanel root = new JPanel();
		panel.add(root);
		root.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.anchor = GridBagConstraints.WEST;

		// file selection
		c.fill = 0;
		c.gridx = 0;
		c.gridy = 0;
		root.add(new JLabel("JS Script File:"), c);
		scriptFileField = new JTextField(20);
		c.fill = 2;
		c.gridx = 1;
		root.add(scriptFileField, c);
		JButton selectButton = new JButton("Select...");
		c.fill = 0;
		c.gridx = 2;
		root.add(selectButton);

		fd = new FileDialog((JFrame) null);
		fd.setMode(FileDialog.LOAD);
		fd.setFilenameFilter((File dir, String name) -> name.endsWith(".js"));

		// listeners

		// select button
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fd.setVisible(true);
				String dirName = fd.getDirectory();
				String fileName = fd.getFile();
				if (dirName != null && fileName != null) {
					scriptFileField.setText(dirName + fileName);
				}
			}
		});
	}

	@Override
	public void starting(int seed) throws FractalStartFailedException {
		try {
			this.seed = seed;

			cx = Context.enter();

			scriptFile = new File(scriptFileField.getText());

			try {
				scriptReader = new FileReader(scriptFile);
			} catch (FileNotFoundException e) {
				throw new FractalStartFailedException(
						"Could not create script file reader!", e);
			}

			Object obj;

			Logger jsLog = LogManager.getLogger("CustomJsFractal:"
					+ scriptFile.getName());

			scope = new ImporterTopLevel(cx);

			obj = new NativeJavaClass(scope, KColor.class);
			scope.put("KColor", scope, obj);

			jsLogObj = Context.javaToJS(jsLog, scope);

			// register functions
			scope.put("getLogger", scope, js_getLogger);
			scope.put("getWidth", scope, js_getWidth);
			scope.put("getHeight", scope, js_getHeight);
			scope.put("getSeed", scope, js_getSeed);

			try {
				cx.evaluateReader(scope, scriptReader, scriptFile.getName(), 1,
						null);
			} catch (IOException e) {
				throw new FractalStartFailedException(
						"Could not evaluate the script!", e);
			}

			obj = scope.get("functions", scope);
			if (obj == Scriptable.NOT_FOUND) {
				throw new FractalStartFailedException(
						"Could not find functions var!");
			}

			Scriptable functions = Context.toObject(obj, scope);

			obj = functions.get("fileName", functions);
			getFileNameFunc = null;
			if (obj instanceof Function)
				getFileNameFunc = (Function) obj;

			obj = functions.get("pixel", functions);
			getPixelFunc = null;
			if (obj instanceof Function)
				getPixelFunc = (Function) obj;

			obj = functions.get("finish", functions);
			finishFunc = null;
			if (obj instanceof Function)
				finishFunc = (Function) obj;

			log.info("Loaded js: " + scriptFile.getName());
		} catch (Throwable t) {
			throw new FractalStartFailedException(t);
		}
	}

	@Override
	public String getFileName() {
		String fname = getName() + ":" + scriptFile.getName();
		Object obj = null;
		if (getFileNameFunc != null)
			obj = getFileNameFunc.call(cx, scope, scope, new Object[0]);
		if (obj != null && obj != Undefined.instance) {
			fname += "_" + Context.toString(obj);
		}
		return fname;
	}

	@Override
	public KColor getPixel(int x, int y) {
		try {
			if (getPixelFunc != null) {
				Object obj = getPixelFunc.call(cx, scope, scope, new Object[] {
						x, y });
				return (KColor) Context.jsToJava(obj, KColor.class);
			}
		} catch (EvaluatorException e) {

		}
		return new KColor(0);
	}

	@Override
	public void finish() {
		if (finishFunc != null) {
			finishFunc.call(cx, scope, scope, new Object[0]);
		}
		Context.exit();
		try {
			scriptReader.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void destroy() {
		fd.dispose();
	}

}
