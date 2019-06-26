package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

/*
 * File:	OBJLoader.java
 * Purpose:	Loads up .obj file as a RawModel.
 */
public class OBJLoader {

	public static RawModel loadObjModel(String fileName, Loader loader) {
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/" + fileName + ".obj"));
		} catch (FileNotFoundException e) {
			System.err.println("ERROR:  COULD NOT LOAD FILE!!!");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;

		try {
			while (true) {
				line = reader.readLine();
				String[] currLine = line.split(" ");
				// Vertex line.
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currLine[1]), Float.parseFloat(currLine[2]),
							Float.parseFloat(currLine[3]));
					vertices.add(vertex);
				}
				// Texture line.
				else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currLine[1]), Float.parseFloat(currLine[2]));
					textures.add(texture);
				}
				// Normal line.
				else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currLine[1]), Float.parseFloat(currLine[2]),
							Float.parseFloat(currLine[3]));
					normals.add(normal);
				}
				// Face line.
				else if (line.startsWith("f ")) {
					textureArray = new float[vertices.size() * 2];
					normalsArray = new float[vertices.size() * 3];
					break;
				}
			}

			while (line != null) {
				if (!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currLine = line.split(" ");
				String[] vertex1 = currLine[1].split("/");
				String[] vertex2 = currLine[2].split("/");
				String[] vertex3 = currLine[3].split("/");

				processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);

				line = reader.readLine();
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		verticesArray = new float[vertices.size() * 3];
		indicesArray = new int[indices.size()];
		int vertexPointer = 0;

		for (Vector3f vertex : vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}

		for (int i = 0; i < indices.size(); i++) {
			indicesArray[i] = indices.get(i);
		}

		return loader.loadToVAO(verticesArray, textureArray, indicesArray);

	}

	// Sorts out the texture coordinate & normal vector for the current vertex.
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
			List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
		int currVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currVertexPointer);
		Vector2f currTexture = textures.get(Integer.parseInt(vertexData[1]) - 1);
		textureArray[currVertexPointer * 2] = currTexture.x;
		textureArray[(currVertexPointer * 2) + 1] = 1 - currTexture.y;
		Vector3f currNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currVertexPointer * 3] = currNormal.x;
		normalsArray[(currVertexPointer * 3) + 1] = currNormal.y;
		normalsArray[(currVertexPointer * 3) + 2] = currNormal.z;
	}

}
