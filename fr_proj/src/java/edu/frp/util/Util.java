package edu.frp.util;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import Jama.Matrix;
import edu.frp.util.knn.KnnClass;
import edu.frp.util.knn.KnnResult;

/**
 * Util
 * 
 * @author Saulo (scsm@ecomp.poli.br)
 *         <p>
 *         {@link http://en.wikipedia.org/wiki/Image_moment}
 */
public class Util {

	private static Logger logger = Logger.getLogger(Util.class.getName());

	/**
	 * Calculate Raw Moment
	 * 
	 * @param p
	 * @param q
	 * @param matrix
	 * @return The Raw Moment
	 */
	public static double calcRawMoment(int p, int q, double[][] matrix) {
		double m = 0;
		logger.finest("Calculating raw moment for p=" + p + " and q=" + q);
		for (int i = 0, k = matrix.length; i < k; i++) {
			for (int j = 0, l = matrix[i].length; j < l; j++) {
				m += Math.pow(i, p) * Math.pow(j, q) * matrix[i][j];
			}
		}
		return m;
	}

	/**
	 * Calculate Central Moment
	 * 
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
	 * 
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
	 * 
	 * @param p
	 * @param q
	 * @param matrix
	 *            containing pixel map for one layer
	 * @return
	 */
	public static double getVarianceX(int p, int q, double[][] matrix) {
		double mc00 = Util.getCentralMoment(0, 0, matrix);
		double mc20 = Util.getCentralMoment(2, 0, matrix);
		return mc20 / mc00;
	}

	/**
	 * Returns the variance in y-direction
	 * 
	 * @param p
	 * @param q
	 * @param matrix
	 *            containing pixel map for one layer
	 * @return
	 */
	public static double getVarianceY(int p, int q, double[][] matrix) {
		double mc00 = Util.getCentralMoment(0, 0, matrix);
		double mc02 = Util.getCentralMoment(0, 2, matrix);
		return mc02 / mc00;
	}

	/**
	 * Normalized Central Moment
	 * 
	 * @param p
	 * @param q
	 * @param matrix
	 *            the pixel map
	 * @return Normalized Central Moment n_pq
	 */
	public static double getNormalizedCentralMoment(int p, int q,
			double[][] matrix) {
		double gama = ((p + q) / 2) + 1;
		double mpq = Util.getCentralMoment(p, q, matrix);
		double m00gama = Math.pow(Util.getCentralMoment(0, 0, matrix), gama);
		return mpq / m00gama;
	}

	/**
	 * Hu invariant moments
	 * 
	 * @param matrix
	 *            the pixel map
	 * @param n
	 *            the Hu moment horder
	 * @return n-order Hu moment
	 */
	public static double getHuMoment(double[][] matrix, int n) {
		double result = 0.0;

		double n20 = Util.getNormalizedCentralMoment(2, 0, matrix), n02 = Util
				.getNormalizedCentralMoment(0, 2, matrix), n30 = Util
				.getNormalizedCentralMoment(3, 0, matrix), n12 = Util
				.getNormalizedCentralMoment(1, 2, matrix), n21 = Util
				.getNormalizedCentralMoment(2, 1, matrix), n03 = Util
				.getNormalizedCentralMoment(0, 3, matrix), n11 = Util
				.getNormalizedCentralMoment(1, 1, matrix);

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
					* (Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2)) + 4
					* n11 * (n30 + n12) * (n21 + n03);
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
	 * 
	 * @param mat1
	 *            the first matrix input
	 * @param mat2
	 *            the second matrix input
	 * @param o
	 *            the Order of distance (2=Euclidian, 0=Minkowiski)
	 * @return double
	 */
	public static double calcDistance(double[][] mat1, double[][] mat2, double o) {
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

	/**
	 * Calculates KNN
	 * 
	 * @param data
	 *            the database classes
	 * @param test
	 *            the test input image
	 * @param n
	 *            the n nearest neighbors
	 * @param o
	 *            the order to be used in distance calculus
	 * @return the nearest neighbor
	 */
	public static KnnClass knn(KnnClass[] data, Matrix test, int n, double o) {
		logger.info("Calculating " + n + " nearest neighbours");
		long startTime = System.currentTimeMillis();
		ArrayList<KnnResult> distance = new ArrayList<KnnResult>();

		for (int classIndex = 0; classIndex < data.length; classIndex++) {
			Iterator<Object> classIterator = data[classIndex].getObjects()
					.iterator();
			while (classIterator.hasNext()) {
				distance.add(new KnnResult(data[classIndex].getName(),
						calcDistance(
								((Matrix) classIterator.next()).getArray(),
								test.getArray(), o)));
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
		KnnResult nearest = (KnnResult) nearestNeighborIterator.next().getKey();

		while (nearestNeighborIterator.hasNext()) {
			KnnResult next = (KnnResult) nearestNeighborIterator.next()
					.getKey();
			if (nearest.compareTo(next) > 0) {
				nearest = next;
			}
		}

		for (int i = 0; i < data.length; i++) {
			KnnClass currentClass = data[i];
			if (currentClass.getName().equals(nearest.getName())) {
				long finalTime = System.currentTimeMillis() - startTime;
				logger.info("Class found in " + finalTime + "ms.");
				return currentClass;
			}
		}
		long finalTime = System.currentTimeMillis() - startTime;
		logger.info("No class found after " + finalTime + "ms.");
		return null;
	}

	/**
	 * Normalizes matrix to 8 values
	 * 
	 * @param matrix
	 *            the matrix to be normalized (double [0.0,1.0])
	 * @param normFactor
	 *            the normalization factor (256/samples)
	 * @return the normalized matrix (int [0,7])
	 */
	private static int[][] calcNormalizedValues(double[][] mat, int normFactor) {
		int[][] normMat = new int[mat.length][mat[0].length];

		for (int j = 0; j < mat[0].length; j++) {
			for (int i = 0; i < mat.length; i++) {
				normMat[i][j] = (int) Math.abs(255 * mat[i][j]) / normFactor;
			}
		}

		return normMat;
	}

	/**
	 * Calculate co-ocourrence matrix
	 * 
	 * @param image
	 *            the image
	 * @param size
	 *            the size of the desired co-ocourrence matrix
	 * @return the co-ocourrence matrix
	 */
	public static int[][] coMatrix(Image image, int size) {
		if (image.isColored()) {
			// throw new
			// IllegalArgumentException("Cannot calculate co-ocourrence matrix for colored images.");
		}

		int[][] result = new int[size][size];
		int[][] mat = Util.calcNormalizedValues(image.getLayerMatrix(0),
				(256 / size));
		int x, y;

		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length - 1; j++) {
				x = mat[i][j];
				y = mat[i][j + 1];
				result[x][y] = result[x][y] + 1;
			}
		}
		return result;
	}

	public static Matrix cvMatrix(Matrix m) {
		int imageWidth = m.getColumnDimension();
		int imageHeight = m.getRowDimension();

		double[][] cvMatrix = new double[imageWidth][imageWidth];

		// Columns means
		double[] cMeans = new double[imageWidth];
		Arrays.fill(cMeans, 0d);

		// Test
		// double[][] pMap = new double[3][3];
		// pMap[0][0]=1; pMap[0][1]=2; pMap[0][2]=3;
		// pMap[1][0]=4; pMap[1][1]=5; pMap[1][2]=6;
		// pMap[2][0]=7; pMap[2][1]=8; pMap[2][2]=9;

		for (int w = 0; w < imageWidth; w++) {
			for (int h = 0; h < imageHeight; h++) {
				cMeans[w] = cMeans[w] + m.get(h, w);
			}
			cMeans[w] = cMeans[w] / imageHeight;
		}

		// Covariance for each pair of variables x1 and x2
		double[][] pMapT = m.transpose().getArray();
		int newLength = imageWidth;
		for (int i = 0; i < imageWidth; i++) {
			double[] x1 = Arrays.copyOf(pMapT[i], newLength);
			double mx1 = cMeans[i];
			for (int j = i; j < imageWidth; j++) {
				double[] x2 = Arrays.copyOf(pMapT[j], newLength);
				double mx2 = cMeans[j];
				double v = 0d;
				for (int k = 0; k < newLength; k++) {
					v = v + ((x1[k] - mx1) * (x2[k] - mx2) / (imageHeight - 1));
				}

				cvMatrix[i][j] = v;
				cvMatrix[j][i] = v;
			}
		}
		return new Matrix(cvMatrix);
	}

	/**
	 * Method used to print a matrix
	 * 
	 * @param matrix
	 */
	public static void printM(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (j == 0) {
					System.out.print("" + matrix[i][j]);
				} else {
					System.out.print("\t" + matrix[i][j]);
				}
			}
			System.out.println("");
		}
	}

	/**
	 * Method used to print a matrix
	 * 
	 * @param matrix
	 */
	public static void printM(double[][] matrix) {
		DecimalFormat df = new DecimalFormat("0.0000000000000");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (j == 0) {
					System.out.print("" + df.format(matrix[i][j]));
				} else {
					System.out.print("\t" + df.format(matrix[i][j]));
				}
			}
			System.out.println("");
		}
	}

	/**
	 * Convert image to grayscale
	 * 
	 * @param i
	 * @return Image
	 */
	public Image toGrayscale(Image i) {
		i.setType(BufferedImage.TYPE_BYTE_GRAY);
		double[][][] matrix = i.getMatrix();
		double[][][] newMatrix = new double[3][i.getHeight()][i.getWidth()];
		double meanValue = 0D;

		for (int w = 0; w < i.getWidth(); w++) {
			for (int h = 0; h < i.getHeight(); h++) {
				meanValue = (matrix[0][h][w] + matrix[1][h][w] + matrix[2][h][w]) / 3D;
				newMatrix[0][h][w] = meanValue;
				newMatrix[1][h][w] = meanValue;
				newMatrix[2][h][w] = meanValue;
			}
		}

		i.setMatrix(newMatrix);

		return i;
	}

	/**
	 * Binarize image with given threshold
	 * 
	 * @param i
	 * @param threshold
	 * @return Image
	 */
	public Image binarize(Image i, int threshold) {
		if (i.isColored()) {
			i = this.toGrayscale(i);
		}
		i.setType(BufferedImage.TYPE_BYTE_BINARY);

		double[][][] matrix = i.getMatrix();

		for (int w = 0; w < i.getWidth(); w++) {
			for (int h = 0; h < i.getHeight(); h++) {
				if (matrix[0][h][w] < threshold) {
					matrix[0][h][w] = 0D;
					matrix[1][h][w] = 0D;
					matrix[2][h][w] = 0D;
				} else {
					matrix[0][h][w] = 1D;
					matrix[1][h][w] = 1D;
					matrix[2][h][w] = 1D;
				}
			}
		}
		i.setMatrix(matrix);
		return i;
	}

	/**
	 * Negative image
	 * 
	 * @param i
	 * @return Image
	 */
	public Image negative(Image i) {
		double[][][] matrix = i.getMatrix();
		for (int l = 0; l < 3; l++) {
			for (int w = 0; w < i.getWidth(); w++) {
				for (int h = 0; h < i.getHeight(); h++) {
					matrix[l][h][w] = 1 - matrix[l][h][w];
				}
			}
		}
		i.setMatrix(matrix);
		return i;
	}
}