package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

/*
 * File:	Renderer.java
 * Purpose:	Renders the model from the VAO.
 */
public class Renderer {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private Matrix4f projectionMatrix;
	private StaticShader shader;

	public Renderer(StaticShader shader) {
		this.shader = shader;
		// Cull faces inside objects that we wouldn't see anyways to reduce
		// computations.
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	// Called once every frame to prepare OpenGL to render the game.
	public void prepare() {
		// Tell OpenGL to test which triangles are in front of each other.
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// Clear color from the last frame.
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// Set color of background.
		GL11.glClearColor(0.5f, 0.1f, 0.1f, 1);
	}

	// Renders the raw models. Broken up with helper methods in order to
	// save computations by doing as little work as possible on each entity
	// (instance) and by only rendering each TexturedModel once in total,
	// rather than once for each entity.
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			// Get all Entities correlated with this current TexturedModel.
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				// Prepare each entity (instance) for rendering.
				prepareInstance(entity);
				// Do the final render!
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

			}
			unbindTexturedModel();
		}
	}

	// Prepares all the TexturedModels.
	private void prepareTexturedModel(TexturedModel model) {
		// Extract RawModel out of TexturedModel.
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		// Activate the Attribute Lists in which our data is stored.
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		// Get shine variables and load them up into shader.
		ModelTexture modelTexture = model.getTexture();
		shader.loadShineVariables(modelTexture.getShineDamper(), modelTexture.getReflectivity());
		// Tell OpenGL which texture we would like to render.
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

	}

	// Unbinds the TexturedModel.
	private void unbindTexturedModel() {
		// Disable the Attribute Lists and un-bind the VAO now that we're done.
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	// Prepares the entities (instances) of each of the TexturedModels.
	private void prepareInstance(Entity entity) {
		// Load up entity's transformation to vertex shader.
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	// Creates the projection matrix.
	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float frustumLength = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);
		projectionMatrix.m33 = 0;
	}

}
