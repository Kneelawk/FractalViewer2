package com.pommert.jedidiah.fractalviewer2.ui.opengl;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.pommert.jedidiah.fractalviewer2.ui.UIControl;
import com.pommert.jedidiah.fractalviewer2.util.KColor;

import de.matthiasmann.twl.utils.PNGDecoder;

public class GLControl {
	public static final int DISPLAY_WIDTH = 1000;
	public static final int DISPLAY_HEIGHT = 600;
	public static final int EXTRA_X = 60;
	public static final int EXTRA_Y = 60;
	public static final float BG_TEX_COORD_X = DISPLAY_WIDTH / 16f;
	public static final float BG_TEX_COORD_Y = DISPLAY_HEIGHT / 16f;

	public static Logger log;
	public static Thread glThread;
	public static boolean isOpen;
	public static boolean hasBeenOpened = false;

	public static int imageX, imageY;
	public static int imageWidth, imageHeight;
	public static String displayTitle = "Fractal Viewer";
	public static ImageInfo background;

	public static boolean glInteractionEnabled = true;

	// fractal image
	public static ByteBuffer pixels;
	public static int imageId;

	public static class ImageInfo {
		public ByteBuffer buf;
		public int width, height, glImageType, imageId;

		public ImageInfo(ByteBuffer buf, int width, int height, int glImageType) {
			this.buf = buf;
			this.width = width;
			this.height = height;
			this.glImageType = glImageType;
		}

		public int registerGlTex() {
			imageId = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageId);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, glImageType, width,
					height, 0, glImageType, GL11.GL_UNSIGNED_BYTE, buf);

			return imageId;
		}

		public int bind() {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageId);
			return imageId;
		}

		public static ImageInfo loadTextureResource(String name)
				throws IOException {
			PNGDecoder dec = new PNGDecoder(
					ImageInfo.class.getResourceAsStream(name));
			int width = dec.getWidth();
			int height = dec.getHeight();
			boolean halph = dec.hasAlpha();
			int glType = halph ? GL11.GL_RGBA : GL11.GL_RGB;
			int bpp = halph ? 4 : 3;
			PNGDecoder.Format fmt = halph ? PNGDecoder.Format.RGBA
					: PNGDecoder.Format.RGB;

			ByteBuffer buf = BufferUtils.createByteBuffer(width * height * bpp);
			dec.decode(buf, width * bpp, fmt);
			buf.flip();

			return new ImageInfo(buf, width, height, glType);
		}
	}

	public static void initGL() {
		log = LogManager.getLogger("GLControl");
		log.info("Init GL");

		try {
			background = ImageInfo.loadTextureResource("background.png");
		} catch (IOException ioe) {
			log.error("Error initializing background texture:", ioe);
		} catch (NullPointerException npe) {
			log.error("Error initializing background texture:", npe);
		}
	}

	public static synchronized void reCreateThread() {
		glThread = new Thread(() -> startGL(), "GL Render Thread");
	}

	public static synchronized void open(String title, int width, int height) {
		if (!glInteractionEnabled)
			return;
		if (width * height > imageWidth * imageHeight) {
			pixels = createImageBuffer(width, height);

			imageWidth = width;
			imageHeight = height;
		} else {
			imageWidth = width;
			imageHeight = height;

			pixels = createImageBuffer(width, height);
		}
		displayTitle = title;
		setupConstants();

		if (!isOpen) {
			reCreateThread();
			isOpen = true;
			hasBeenOpened = true;
			glThread.start();
		} else {
			Display.setTitle(displayTitle);
		}

		UIControl.updateReopenButton(isOpen, hasBeenOpened);
	}

	public static synchronized void open() {
		if (!isOpen && hasBeenOpened) {
			reCreateThread();
			isOpen = true;
			glThread.start();
		}
		UIControl.updateReopenButton(isOpen, hasBeenOpened);
	}

	public static synchronized void setupConstants() {
		imageX = (DISPLAY_WIDTH - imageWidth) / 2;
		imageY = (DISPLAY_HEIGHT - imageHeight) / 2;
	}

	public static synchronized ByteBuffer createImageBuffer(int width,
			int height) {
		ByteBuffer buf = BufferUtils.createByteBuffer(width * height
				* KColor.BYTES_PER_PIXEL);
		// Apparently there is no more setup needed

		return buf;
	}

	public static void startGL() {
		try {
			// open a display window
			openDisplay();

			// setup gl
			setupGL();

			// start display loop
			startLoop();

			// destroy when done
			destroy();
		} catch (Throwable t) {
			log.error("Error during opengl process!", t);
			throw new RuntimeException("Error during opengl process!", t);
		}
	}

	public static void openDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH,
					DISPLAY_HEIGHT));
			Display.setTitle(displayTitle);
			Display.setVSyncEnabled(true);
			log.info("Opening GL Display!");
			Display.create();
		} catch (LWJGLException e) {
			log.error("LWJGLException while opening GL display window!", e);
			throw new RuntimeException(
					"LWJGLException while opening GL display window!", e);
		}
	}

	public static void setupGL() {
		// background color
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);

		// ortho
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, DISPLAY_WIDTH, DISPLAY_HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		if (background != null)
			background.registerGlTex();

		// setup pixels
		setupPixels();
	}

	public static void setupPixels() {
		imageId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageId);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, imageWidth,
				imageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
	}

	public static void startLoop() {
		while (isOpen) {
			if (Display.isCloseRequested()) {
				stop();
			}

			pullInput();

			draw();

			Display.update();
		}
		UIControl.updateReopenButton(isOpen, hasBeenOpened);
	}

	public static void pullInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP) && imageY < EXTRA_Y) {
			if (imageY + 5 > EXTRA_Y)
				imageY += Math.max(EXTRA_Y - imageY, 0);
			else
				imageY += 5;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)
				&& imageY + imageHeight > DISPLAY_HEIGHT - EXTRA_Y) {
			if (imageY + imageHeight - 5 < DISPLAY_HEIGHT - EXTRA_Y)
				imageY += Math.min((DISPLAY_HEIGHT - EXTRA_Y)
						- (imageY + imageHeight), 0);
			else
				imageY -= 5;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && imageX < EXTRA_X) {
			if (imageX + 5 > EXTRA_X)
				imageX += Math.max(EXTRA_X - imageX, 0);
			else
				imageX += 5;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)
				&& imageX + imageWidth > DISPLAY_WIDTH - EXTRA_X) {
			if (imageX + imageWidth - 5 < DISPLAY_WIDTH - EXTRA_X)
				imageX += Math.min((DISPLAY_WIDTH - EXTRA_X)
						- (imageX + imageWidth), 0);
			imageX -= 5;
		}

		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			if (button != -1) {
				if (Mouse.getEventButtonState()) {
					if (button == 5) {
						if (imageX + 60 > EXTRA_X)
							imageX += Math.max(EXTRA_X - imageX, 0);
						else
							imageX += 60;
					} else if (button == 6) {
						if (imageX + imageWidth - 60 < DISPLAY_WIDTH - EXTRA_X)
							imageX += Math.min((DISPLAY_WIDTH - EXTRA_X)
									- (imageX + imageWidth), 0);
						else
							imageX -= 60;
					}
				}
			}
		}

		int wheel = Mouse.getDWheel() / 2;
		if (wheel != 0) {
			if (wheel < 0
					&& (imageY + imageHeight + wheel) < DISPLAY_HEIGHT
							- EXTRA_Y)
				wheel = Math.min((DISPLAY_HEIGHT - EXTRA_Y)
						- (imageY + imageHeight), 0);
			else if (wheel > 0 && imageY + wheel > EXTRA_Y)
				wheel = Math.max(EXTRA_Y - imageY, 0);
			imageY += wheel;
		}

		int mx = Mouse.getX();
		int my = DISPLAY_HEIGHT - Mouse.getY();
		if (Mouse.isButtonDown(0) && my >= imageY && my <= imageY + imageHeight
				&& mx >= imageX && mx <= imageX + imageWidth) {
			int dx = Mouse.getDX();
			int dy = -Mouse.getDY();
			if (dx != 0 || dy != 0) {
				if (dx < 0
						&& (imageX + imageWidth + dx) < DISPLAY_WIDTH - EXTRA_X)
					dx = Math.min((DISPLAY_WIDTH - EXTRA_X)
							- (imageX + imageWidth), 0);
				else if (dx > 0 && imageX + dx > EXTRA_X)
					dx = Math.max(EXTRA_X - imageX, 0);
				if (dy < 0
						&& (imageY + imageHeight + dy) < DISPLAY_HEIGHT
								- EXTRA_Y)
					dy = Math.min((DISPLAY_HEIGHT - EXTRA_Y)
							- (imageY + imageHeight), 0);
				else if (dy > 0 && imageY + dy > EXTRA_Y)
					dy = Math.max(EXTRA_Y - imageY, 0);
				imageX += dx;
				imageY += dy;
			}
		}
	}

	public static void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0f, 0f, 0f);

		if (background != null) {
			background.bind();
			GL11.glColor4f(1f, 1f, 1f, 1f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0f, 0f);
			GL11.glVertex2f(0f, 0f);
			GL11.glTexCoord2f(BG_TEX_COORD_X, 0f);
			GL11.glVertex2f(DISPLAY_WIDTH, 0f);
			GL11.glTexCoord2f(BG_TEX_COORD_X, BG_TEX_COORD_Y);
			GL11.glVertex2f(DISPLAY_WIDTH, DISPLAY_HEIGHT);
			GL11.glTexCoord2f(0f, BG_TEX_COORD_Y);
			GL11.glVertex2f(0f, DISPLAY_HEIGHT);
			GL11.glEnd();
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, imageWidth,
				imageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0f, 0f);
		GL11.glVertex2f(imageX, imageY);
		GL11.glTexCoord2f(1f, 0f);
		GL11.glVertex2f(imageX + imageWidth, imageY);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex2f(imageX + imageWidth, imageY + imageHeight);
		GL11.glTexCoord2f(0f, 1f);
		GL11.glVertex2f(imageX, imageY + imageHeight);
		GL11.glEnd();
	}

	public static void setPixel(int x, int y, KColor color) {
		if (!glInteractionEnabled)
			return;

		int index = (x + y * imageWidth) * 4;
		pixels.put(index, color.getRed());
		pixels.put(index + 1, color.getGreen());
		pixels.put(index + 2, color.getBlue());
		pixels.put(index + 3, color.getAlpha());
	}

	public static void stop() {
		isOpen = false;
	}

	public static void destroy() {
		isOpen = false;
		Display.destroy();
	}
}
