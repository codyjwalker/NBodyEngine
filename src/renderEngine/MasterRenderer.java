package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

public class MasterRenderer {

	private StaticShader shader = new StaticShader();
	private Renderer renderer = new Renderer(shader);

	// Hashmap of all TexturedModels & their respective entities.
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

	public void render(Light light, Camera camera) {
		renderer.prepare();
		shader.start();
		shader.loadLight(light);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		entities.clear();
	}

	// Sorts all entities ready to be rendered into the correct list each frame.
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		// If batch for curr TexturedModel already exists, add entity straight to that
		// batch.
		if (batch != null) {
			batch.add(entity);
		} // Else, create new batch for curr TexturedModel.
		else {
			List<Entity> newBatch = new ArrayList<Entity>();
			// Add the entity to the new batch & add batch to HashMap.
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
