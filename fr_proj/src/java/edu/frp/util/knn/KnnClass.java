package edu.frp.util.knn;

import java.io.Serializable;
import java.util.ArrayList;

import Jama.Matrix;

public class KnnClass implements Serializable {

	
	private static final long serialVersionUID = 6222371857035755863L;

	private String name;
	
	private ArrayList<Matrix> objects;
	
	/**
	 * Class constructor
	 * @param name the name of the class
	 */
	public KnnClass (String name) {
		this.setName(name);
		this.setImages(new ArrayList<Matrix> ());
	}
	
	/**
	 * Class constructor
	 * @param name the name of the class
	 * @param data that compose the class
	 */
	public KnnClass (String name, ArrayList<Matrix> objects) {
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
	public void setImages(ArrayList<Matrix> objects) {
		this.objects = objects;
	}

	/**
	 * @return the images
	 */
	public ArrayList<Matrix> getObjects() {
		return objects;
	}

}
