package engineTester;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

/*
 * File:	MainGameLoop.java
 * Purpose:	Contains the main() method, inside of which the "infinite"
 * 			game loop is contained.
 */
public class MainGameLoop {

	private static int numBodies;
	private static int bodyRadius;	// Figure out how to use this with scale below!
	private static int timesteps;

	static FileReader input = null;
	static Scanner scan = null;
	
	private static Boolean pdb = true;

	public static void main(String[] args) {

		fileInit();

		// Open up the display.
		DisplayManager.createDisplay();

		// Create Loader, Renderer, & Shader so that we can use them.
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);

		// Load up RawModel & ModelTexture.
		RawModel model = OBJLoader.loadObjModel("Earth", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("Albedo"));
		// Make TexturedModel out of model & texture.
		TexturedModel texturedModel = new TexturedModel(model, texture);

		// Make Entity List, with each entity using the TexturedModel.
		List<Entity> entities = new ArrayList<Entity>();
		for (int i = 0; i < numBodies; i++) {
			float xpos = scan.nextFloat();
			float ypos = scan.nextFloat();
			float zpos = scan.nextFloat();
			if (pdb) {System.out.println(xpos + "  " + ypos + "  " + zpos);}

			// TODO:	TIE IN BODY RADIUS WITH THE SCALE VARIABLE!!! (accurately)
			entities.add(new Entity(texturedModel, new Vector3f(xpos, ypos, zpos), 0, 0, 0, 0.2f));
		}

		// Make Entity with TexturedModel.
//		Entity entity = new Entity(texturedModel, new Vector3f(0, -0.2f, -10), 0, 0, 0, 1);
		// Create camera.
		Camera camera = new Camera();

		// The actual game loop. Exit when user clicks 'x' button.
//		while (!Display.isCloseRequested()) {

		// JK you thought I was being serious?!?!?!?!?
		// Why in the world would the main game loop be commented out????
		// THis is the loop, executes until all timesteps rendered.
		// Starts at timestep 1 since we already loaded up 0th building entity list.
		for (int i = 1; i < timesteps; i++) {

			// Move the camera to where user requested it to be moved.
			camera.move();

			// Prepare the Renderer each frame.
			renderer.prepare();

			// Start the shader program before rendering.
			shader.start();

			// Load camera into shader.
			shader.loadViewMatrix(camera);

			for (Entity entity : entities) {

				// Rotate the entities just for shits n giggles.
				entity.increaseRotation(0, 0.5f, 0);

				// Pull positional coordinates from input file.
				Vector3f position = new Vector3f(scan.nextFloat(), scan.nextFloat(), scan.nextFloat());

				if (pdb) {System.out.println(position.x + "  " + position.y + "  " + position.z);}

				// Set the position of the current entity.
				entity.setPosition(position);

				// Render the model each frame.
				renderer.render(entity, shader);
			}

			// Stop shader after render finished.
			shader.stop();

			// Update the display each frame.
			DisplayManager.updateDisplay();

		}
//		} // END while()

		// CLEANUP, CLEANUP, EVERYBODY CLEAN UP!
		try {
			input.close();
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Cleanup shader & loader upon closing.
		shader.cleanUp();
		loader.cleanUp();
		// Close display once loop is exited.
		DisplayManager.closeDisplay();
	}

	private static void fileInit() {
		try {
			input = new FileReader("gui_input.txt");
			scan = new Scanner(input);

			// Read in # of bodies, radius of bodies, & how many timesteps.
			numBodies = scan.nextInt();
			bodyRadius = scan.nextInt();
			timesteps = scan.nextInt();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
