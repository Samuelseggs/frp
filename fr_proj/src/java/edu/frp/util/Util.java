package edu.frp.util; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import edu.frp.util.knn.KnnImageClass;
import edu.frp.util.knn.KnnResult;



/**
 * Util
 * @author Arlington
 * @author Saulo (scsm@ecomp.poli.br)<p>
 * {@link http://en.wikipedia.org/wiki/Image_moment}
 */
public class Util {

	private static Logger logger = Logger.getLogger(Util.class.getName());
	
	/**
	 * Calculate Raw Moment
	 * @param p
	 * @param q
	 * @param matrix
	 * @return The Raw Moment
	 */
	public static double calcRawMoment(int p, int q, double[][] matrix) {
		double m = 0;
		logger.finest("Calculating raw moment for p="+p+" and q="+q);
		for (int i = 0, k = matrix.length; i < k; i++) {
			for (int j = 0, l = matrix[i].length; j < l; j++) {
				m += Math.pow(i, p) * Math.pow(j, q) * matrix[i][j];
			}
		}
		return m;
	}

	/**
	 * Calculate Central Moment
	 * @param p
	 * @param q
	 * @param img
	 * @return The Central Moment
	 */
	public static double getCentralMoment(int p, int q, double[][] img) {
		double mc = 0;
		double m00 = Util.calcRawMoment(0, 0, img);
		double m10 = Util.calcRawMoment(1, 0, img);
		double m01 = Util.calcRawMoment(0, 1, img);
		double x0 = m10 / m00;
		double y0 = m01 / m00;
		for (int i = 0, k = img.length; i < k; i++) {
			for (int j = 0, l = img[i].length; j < l; j++) {
				mc += Math.pow((i - x0), p) * Math.pow((j - y0), q) * img[i][j];
			}
		}
		return mc;
	}

	/**
	 * Covariance XY
	 * @param matrix
	 * @return The covariance value.
	 */
	public static double getCovarianceXY(double[][] matrix) {
		double mc00 = Util.getCentralMoment(0, 0, matrix);
		double mc11 = Util.getCentralMoment(1, 1, matrix);
		return mc11 / mc00;
	}

	/**
	 * Returns the variance in x-direction
	 * @param p
	 * @param q
	 * @param matrix containing pixel map for one layer
	 * @return
	 */
	public static double getVarianceX(int p, int q, double[][] matrix) {
		double mc00 = Util.getCentralMoment(0, 0, matrix);
		double mc20 = Util.getCentralMoment(2, 0, matrix);
		return mc20 / mc00;
	}

	/**
	 * Returns the variance in y-direction
	 * @param p
	 * @param q
	 * @param matrix containing pixel map for one layer
	 * @return
	 */
	public static double getVarianceY(int p, int q, double[][] matrix) {
		double mc00 = Util.getCentralMoment(0, 0, matrix);
		double mc02 = Util.getCentralMoment(0, 2, matrix);
		return mc02 / mc00;
	}

	/**
	 * Normalized Central Moment
	 * @param p
	 * @param q
	 * @param matrix the pixel map
	 * @return Normalized Central Moment n_pq
	 */
	public static double getNormalizedCentralMoment(int p, int q, double[][] matrix) {
		double gama = ((p + q) / 2) + 1;
		double mpq = Util.getCentralMoment(p, q, matrix);
		double m00gama = Math.pow(Util.getCentralMoment(0, 0, matrix), gama);
		return mpq / m00gama;
	}
	
	/**
	 * Hu invariant moments
	 * @param matrix the pixel map
	 * @param n the Hu moment horder
	 * @return n-order Hu moment
	 */
	public static double getHuMoment (double[][] matrix,int n) {
		double result = 0.0;
		
		double
		n20 = Util.getNormalizedCentralMoment(2, 0, matrix),
		n02 = Util.getNormalizedCentralMoment(0, 2, matrix),
		n30 = Util.getNormalizedCentralMoment(3, 0, matrix),
		n12 = Util.getNormalizedCentralMoment(1, 2, matrix),
		n21 = Util.getNormalizedCentralMoment(2, 1, matrix),
		n03 = Util.getNormalizedCentralMoment(0, 3, matrix),
		n11 = Util.getNormalizedCentralMoment(1, 1, matrix);
		
		switch (n) {
		case 1:
			result = n20 + n02;
			break;
		case 2:
			result = Math.pow((n20 - 02), 2) + Math.pow(2 * n11, 2);
			break;
		case 3:
			result = Math.pow(n30 - (3 * (n12)), 2)
					+ Math.pow((3 * n21 - n03), 2);
			break;
		case 4:
			result = Math.pow((n30 + n12), 2) + Math.pow((n12 + n03), 2);
			break;
		case 5:
			result = (n30 - 3 * n12) * (n30 + n12)
					* (Math.pow((n30 + n12), 2) - 3 * Math.pow((n21 + n03), 2))
					+ (3 * n21 - n03) * (n21 + n03)
					* (3 * Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2));
			break;
		case 6:
			result = (n20 - n02)
					* (Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2))
					+ 4 * n11 * (n30 + n12) * (n21 + n03);
			break;
		case 7:
			result = (3 * n21 - n03) * (n30 + n12)
					* (Math.pow((n30 + n12), 2) - 3 * Math.pow((n21 + n03), 2))
					+ (n30 - 3 * n12) * (n21 + n03)
					* (3 * Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2));
			break;

		default:
			throw new IllegalArgumentException("Invalid number for Hu moment.");
		}
		return result;
	}

	/**
	 * Generic Distance
	 * @param mat1 First matrix input
	 * @param mat2 Second matrix input
	 * @param o Order of distance (2=Euclidian, 0=Minkowiski)
	 * @return double
	 */
	private static double calcDistance (Image target, Image test, double o) {
		double distance = 0.0f;
		
		double[][][] mat1 = target.getMatrix();
		double[][][] mat2 = test.getMatrix();
		
		int h = mat1.length;
		int w = mat1[0].length;
		int l = mat1[0][0].length;
		
		// Test pixel maps sizes
		if (mat2.length != h || mat2[0].length !=w || mat2[0][0].length != l) {
			throw new IllegalArgumentException("Input images cannot be compared due to different pixel map sizes.");
		}
		
		for (int k = 0; k < l; k++) {
			for (int j = 0; j < w; j++) {
				for (int i = 0; i < h; i++) {
					distance = distance + Math.abs(Math.pow(mat1[i][j][k] - mat2[i][j][k], o));
				}
			}
		}
		
		distance = Math.pow(distance, 1/o);
		
		return distance;
	}
	
	/**
	 * Calculates KNN
	 * @param data the database classes
	 * @param test the test input image
	 * @param n the n nearest neighbors
	 * @param o the order to be used in distance calculus
	 * @return the nearest neighbor
	 */
	public static KnnImageClass knn(KnnImageClass[] data, Image test, int n, double o) {
		ArrayList<KnnResult> distance = new ArrayList<KnnResult>();

		for (int classIndex = 0; classIndex < data.length; classIndex++) {
			Iterator<Image> classImagesIterator = data[classIndex].getImages()
					.iterator();
			while (classImagesIterator.hasNext()) {
				distance.add(new KnnResult(data[classIndex].getName(),
						calcDistance(classImagesIterator.next(), test, o)));
			}
		}

		Collections.sort(distance);

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
		KnnResult nearest = (KnnResult) nearestNeighborIterator.next();

		while (nearestNeighborIterator.hasNext()) {
			KnnResult next = (KnnResult) nearestNeighborIterator.next();
			if (nearest.compareTo(next) > 0) {
				nearest = next;
			}
		}

		for (int i = 0; i < data.length; i++) {
			KnnImageClass currentClass = data[i];
			if (currentClass.getName().equals(nearest.getName())) {
				return currentClass;
			}
		}

		return null;
	}
}