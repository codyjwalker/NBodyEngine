package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Matrix4f;

/*
 * File:	ShaderProgram
 * Purpose:	Generic shader program containing all the attributes &
 * 			methods every shader program will have.
 */
public abstract class ShaderProgram {

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(String vertexFile, String fragmentFile) {
		// First load up shader files.
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		// Create a new program. Ties together vertex & fragment shaders.
		programID = GL20.glCreateProgram();
		// Attach shaders to program.
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		// Link them together & validate the program.
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void getAllUniformLocations();
	
	// Gets location of uniform variable in shader code.
	protected int getUniformLocation(String uniformName) {
			return GL20.glGetUniformLocation(programID, uniformName);
	}

	// Loads floats to uniform location.
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	// Loads vector to uniform location.
	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	// Loads boolean to uniform location.
	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}
	
	// Loads matrix to uniform location.
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}

	// Starts the program.
	public void start() {
		GL20.glUseProgram(programID);
	}

	// Stops the program.
	public void stop() {
		GL20.glUseProgram(0);
	}

	// Cleanup for memory management.
	public void cleanUp() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}

	// When we create implementation of this class must have this to link up
	// inputs to shader programs to one of attributes of VAO we pass in.
	protected abstract void bindAttributes();

	// Method to bind an attribute.
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	// Loads up shader source code files. Opens up source code files,
	// reads in all the lines, and connects them all together in one long sting.
	// Then, create new vertex or fragment shader, depending on value of 'type',
	// attaches string of source code to it, compiles it, and lastly prints any
	// errors found before returning ID of newly created shader.
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("ERROR:  COULD NOT READ FILE!!!");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		// NOTE: glGetShader() deprecated, using glGetShaderi instead.
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("ERROR:  COULD NOT COMPILE SHADER!!!");
			System.exit(-1);
		}
		return shaderID;
	}

}
