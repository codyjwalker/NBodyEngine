package textures;

/*
 * File:	ModelTexture.java
 * Purpose:	Represents a texture to texture our models.
 */
public class ModelTexture {

	private int textureID;
	private float shineDamper = 1;
	private float reflectivity = 0;

	public ModelTexture(int ID) {
		this.textureID = ID;
	}

	public int getID() {
		return this.textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

}
