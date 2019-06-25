package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/*
 * File:	DisplayManager.java
 * Purpose:	Responsible for managing the display.
 */
public class DisplayManager {

	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 120;

	// Opens display upon starting of the engine.
	public static void createDisplay() {

		// Set attributes required for creating the display.
		ContextAttribs attribs = new ContextAttribs(3, 2)
				.withForwardCompatible(true)
				.withProfileCore(true);

		try {
			// Sets size of the display.
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			// Create the display.
			Display.create(new PixelFormat(), attribs);
			Display.setTitle("A Very Nice Display!");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// Tell OpenGL to render the game on the whole display.
		GL11.glViewport(0, 0, WIDTH, HEIGHT);

	}

	// Updates the display each frame.
	public static void updateDisplay() {
		// Tell engine to run at set FPS count.
		Display.sync(FPS_CAP);
		Display.update();

	}

	// Closes the display upon exiting.
	public static void closeDisplay() {

		Display.destroy();
	}

}
