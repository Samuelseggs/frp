package edu.frp.util.knn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import Jama.Matrix;


/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class Knn {

	private static Logger logger = Logger.getLogger(Knn.class.getName());
	
	private ArrayList<KnnClass> data;
	private ArrayList<KnnResult> distance;
	
	/**
	 * Class constructor
	 * @param data
	 * @param test
	 * @param n
	 * @param o
	 */
	public Knn (ArrayList<KnnClass> data, Matrix test, int n, double o) {
		logger.info("Initializing Knn...");
		this.data = data;
		this.distance = new ArrayList<KnnResult>();

		for (int classIndex = 0; classIndex < data.size(); classIndex++) {
			Iterator<Matrix> classIterator = data.get(classIndex).getObjects()
					.iterator();
			while (classIterator.hasNext()) {
				Matrix tempMatrix = (classIterator.next());
				this.distance.add(new KnnResult(data.get(classIndex).getName(),
						calcDistance(tempMatrix.getArray(), test
								.getArray(), o)));
			}
		}

		Collections.sort(this.distance);
		logger.info("Knn initialized.");
	}
	
	/**
	 * @param n
	 * @param maxDist
	 * @return
	 */
	public KnnClass getKnn (int n, double maxDist){
		long startTime = System.currentTimeMillis();
		HashMap<KnnResult, Integer> nearestNeighbors = new HashMap<KnnResult, Integer>();
		for (int i = 0; i < n; i++) {
			KnnResult currentResult = distance.get(i);
			if (nearestNeighbors.get(nearestNeighbors) == null) {
				nearestNeighbors.put(currentResult, 1);
			} else {
				nearestNeighbors.put(currentResult, nearestNeighbors
						.get(currentResult));
			}
		}

		Iterator<Entry<KnnResult, Integer>> nearestNeighborIterator = nearestNeighbors
				.entrySet().iterator();
		KnnResult nearest = (KnnResult) nearestNeighborIterator.next().getKey();

		while (nearestNeighborIterator.hasNext()) {
			KnnResult next = (KnnResult) nearestNeighborIterator.next()
					.getKey();
			if (nearest.compareTo(next) > 0) {
				nearest = next;
			}
		}

		
		for (int i = 0; i < data.size(); i++) {
			KnnClass currentClass = data.get(i);
			if ((nearest.getDistance() < maxDist) && (currentClass.getName().equals(nearest.getName()))) {
				long finalTime = System.currentTimeMillis() - startTime;
				logger.info("Class found in " + finalTime + "ms. Distance: " +nearest.getDistance()+".");
				return currentClass;
			}
		}
		long finalTime = System.currentTimeMillis() - startTime;
		logger.info("No class found after " + finalTime + "ms.");
		return null;
	}
	
	private double calcDistance(double[][] mat1, double[][] mat2, double o) {
		double distance = 0.0f;

		int h = mat1.length;
		int w = mat1[0].length;

		// Test pixel maps sizes
		if (mat2.length != h || mat2[0].length != w) {
			throw new IllegalArgumentException(
					"Input images cannot be compared due to different sizes.");
		}

		for (int j = 0; j < w; j++) {
			for (int i = 0; i < h; i++) {
				distance = distance
						+ Math.abs(Math.pow(mat1[i][j] - mat2[i][j], o));
			}
		}

		distance = Math.pow(distance, 1 / o);

		return distance;
	}
}
