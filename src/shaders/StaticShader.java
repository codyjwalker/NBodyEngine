package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/shaders/vertexShader";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader";

	private int location_transformation_matrix;
	private int location_projection_matrix;
	private int location_view_matrix;
	private int location_light_position;
	private int location_light_color;
	private int location_shine_damper;
	private int location_reflectivity;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	// Bind attributes of VAO to variables.
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texture_coordinates");
		super.bindAttribute(2, "normal");
	}

	// Gets the locations of all uniform variables.
	@Override
	protected void getAllUniformLocations() {
		location_transformation_matrix = super.getUniformLocation("transformation_matrix");
		location_projection_matrix = super.getUniformLocation("projection_matrix");
		location_view_matrix = super.getUniformLocation("view_matrix");
		location_light_position = super.getUniformLocation("light_position");
		location_light_color = super.getUniformLocation("light_color");
		location_shine_damper = super.getUniformLocation("shine_damper");
		location_reflectivity = super.getUniformLocation("reflectivity");
	}

	// Loads transformation matrix to uniform variable.
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformation_matrix, matrix);
	}

	// Loads projection matrix to uniform variable.
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projection_matrix, projection);
	}

	// Loads view matrix to uniform variable.
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_view_matrix, viewMatrix);
	}

	// Loads up light uniform variables.
	public void loadLight(Light light) {
		super.loadVector(location_light_position, light.getPosition());
		super.loadVector(location_light_color, light.getColor());
	}

	// Loads up shine uniform variables.
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shine_damper, damper);
		super.loadFloat(location_reflectivity, reflectivity);

	}

}
