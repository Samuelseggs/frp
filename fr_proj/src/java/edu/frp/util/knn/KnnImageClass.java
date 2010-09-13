package edu.frp.util.knn;

import java.util.ArrayList;

import edu.frp.util.Image;

public class KnnImageClass {

	private String name;
	
	private ArrayList<Image> images;
	
	/**
	 * Class constructor
	 * @param name the name of the class
	 */
	public KnnImageClass (String name) {
		this.setName(name);
		this.setImages(new ArrayList<Image> ());
	}
	
	/**
	 * Class constructor
	 * @param name the name of the class
	 * @param images the images that compose the class
	 */
	public KnnImageClass (String name, ArrayList<Image> images) {
		this.setName(name);
		this.setImages(images);
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
	 * @param images the images to set
	 */
	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	/**
	 * @return the images
	 */
	public ArrayList<Image> getImages() {
		return images;
	}

}
