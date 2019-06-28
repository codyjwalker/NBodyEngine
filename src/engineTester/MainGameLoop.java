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
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
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

	private static Loader loader;
	private static RawModel rawModel;
	private static ModelTexture texture, modelTexture;
	private static TexturedModel texturedModel;
	private static Light light;
	private static Camera camera;
	private static MasterRenderer renderer;
	private static List<Entity> entities;

	private static int numBodies;
	private static int bodyRadius;	// Figure out how to use this with scale below!
	private static int timesteps;

	private static FileReader input = null;
	private static Scanner scan = null;
	

	private static Boolean pdb = true;

	public static void main(String[] args) {

		init();
//		while (!Display.isCloseRequested()) {

		// The Game Loop!
		// Starts at timestep 1 since we already loaded up 0th building entity list.
		for (int i = 1; i < timesteps; i++) {

			// Move the camera to where user requested it to be moved.
			camera.move();

			// For each entity, for each frame, process the entity.
			for (Entity entity : entities) {

				// Rotate the entities just for shits n giggles.
				entity.increaseRotation(0, 0.5f, 0);

				// Pull positional coordinates from input file.
				Vector3f position = new Vector3f(scan.nextFloat(), scan.nextFloat(), scan.nextFloat());

				if (pdb) {System.out.println(position.x + "  " + position.y + "  " + position.z);}

				// Set the position of the current entity.
				entity.setPosition(position);

				// Process the current entity.
				renderer.processEntity(entity);
			}
			// Render each frame.
			renderer.render(light, camera);

			// Update the display each frame.
			DisplayManager.updateDisplay();

		}
//		} // END while()
		terminate();
	}

	private static void init() {
		// Open up file and scanner.
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
		
		// Open up the display.
		DisplayManager.createDisplay();

		// Create Loader.
		loader = new Loader();

		// Load up RawModel & ModelTexture, make texturedModel, & extract its texture.
		rawModel = OBJLoader.loadObjModel("Ball", loader);
		texture = new ModelTexture(loader.loadTexture("sassy"));
		texturedModel = new TexturedModel(rawModel, texture);
		modelTexture = texturedModel.getTexture();

		// Set damper and reflectivity values.
		modelTexture.setShineDamper(10);
		modelTexture.setReflectivity(1);

	
		// Make Entity List, with each entity using the TexturedModel.
		entities = new ArrayList<Entity>();
		for (int i = 0; i < numBodies; i++) {
			float xpos = scan.nextFloat();
			float ypos = scan.nextFloat();
			float zpos = scan.nextFloat();
			if (pdb) {System.out.println(xpos + "  " + ypos + "  " + zpos);}

			// TODO:	TIE IN BODY RADIUS WITH THE SCALE VARIABLE!!! (accurately)
			entities.add(new Entity(texturedModel, new Vector3f(xpos, ypos, zpos), 0, 0, 0, 0.2f));
		}


		// Make lightsource.
		light = new Light(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));

		// Create camera.
		camera = new Camera();

		// Create the MasterRenderer
		renderer = new MasterRenderer();
	}
	
	private static void terminate() {
		// CLEANUP, CLEANUP, EVERYBODY CLEAN UP!
		try {
			input.close();
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Cleanup loader & renderer upon closing.
		renderer.cleanUp();
		loader.cleanUp();
		// Close display once loop is exited.
		DisplayManager.closeDisplay();
	
	}

}
