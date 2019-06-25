package engineTester;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.opengl.Display;
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
	private static int bodyRadius;
	private static int timesteps;
	
	static FileReader input = null;
	static Scanner scan = null;

	
	public static void main(String[] args) {

		fileInit();

		// Open up the display.
		DisplayManager.createDisplay();

		// Create Loader, Renderer, & Shader so that we can use them.
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);

		// Load up RawModel & ModelTexture.
		RawModel model = OBJLoader.loadObjModel("Ball", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("sassy"));
		// Make TexturedModel out of model & texture.
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		System.out.println("numBodies = " + numBodies);
		// Make Entity List.
		List<Entity> entities = new ArrayList<Entity>();
		for (int i = 0; i < numBodies; i++) {
			float xpos = scan.nextFloat();
			float ypos = scan.nextFloat();
			float zpos = scan.nextFloat();
			System.out.println(xpos + "  " + ypos + "  " + zpos);
			entities.add(new Entity(texturedModel, new Vector3f(xpos, ypos, zpos), 0, 0, 0, 0.2f));
		}

		// Make Entity with TexturedModel.
//		Entity entity = new Entity(texturedModel, new Vector3f(0, -0.2f, -10), 0, 0, 0, 1);
		// Create camera.
		Camera camera = new Camera();
		

		// The actual game loop. Exit when user clicks 'x' button.
		while (!Display.isCloseRequested()) {
			
			for (int i = 0; i < (timesteps - 1); i++) {
				
				

				// Move the camera to where user requested it to be moved.
				camera.move();

				// Prepare the Renderer each frame.
				renderer.prepare();

				// Start the shader program before rendering.
				shader.start();
				
				// Load camera into shader.
				shader.loadViewMatrix(camera);
				
				
								
				
				for (Entity entity:entities) {
						
//					entity.increaseRotation(0, 0.3f, 0);
					
					Vector3f position = new Vector3f(scan.nextFloat(), scan.nextFloat(), scan.nextFloat());
					
					System.out.println(position.x + "  " + position.y + "  " + position.z);
					entity.setPosition(position);

					// Render the model each frame.
					renderer.render(entity, shader);
				}
	
				// Stop shader after render finished.
				shader.stop();

			
				
				
				// Update the display each frame.
				DisplayManager.updateDisplay();

			}
		}
		
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
