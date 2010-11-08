package edu.frp.util.knn;

import java.util.ArrayList;

public class KnnClass {

	private String name;
	
	private ArrayList<Object> objects;
	
	/**
	 * Class constructor
	 * @param name the name of the class
	 */
	public KnnClass (String name) {
		this.setName(name);
		this.setImages(new ArrayList<Object> ());
	}
	
	/**
	 * Class constructor
	 * @param name the name of the class
	 * @param data that compose the class
	 */
	public KnnClass (String name, ArrayList<Object> objects) {
		this.setName(name);
		this.setImages(objects);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param objects the images to set
	 */
	public void setImages(ArrayList<Object> objects) {
		this.objects = objects;
	}

	/**
	 * @return the images
	 */
	public ArrayList<Object> getObjects() {
		return objects;
	}

}
