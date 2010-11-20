package edu.frp.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import Jama.Matrix;

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

	/**
	 * Build covariance matrix for matrix m
	 * 
	 * @param m
	 * @return
	 */
	public static Matrix cvMatrix(Matrix m) {
		int imageWidth = m.getColumnDimension();
		int imageHeight = m.getRowDimension();

		double[][] cvMatrix = new double[imageWidth][imageWidth];

		// Columns means
		double[] cMeans = new double[imageWidth];
		Arrays.fill(cMeans, 0d);

		for (int w = 0; w < imageWidth; w++) {
			for (int h = 0; h < imageHeight; h++) {
				cMeans[w] = cMeans[w] + m.get(h, w);
			}
			cMeans[w] = cMeans[w] / imageHeight;
		}

		// Covariance for each pair of variables x1 and x2
		double[][] pMapT = m.transpose().getArray();
		System.out.println(pMapT.length + " " + pMapT[0].length);
		DecimalFormat df = new DecimalFormat("0.00");

		logger.info("Calculating covariance matrix...");
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < imageHeight; i++) {
			logger.finest("" + df.format(i * 100d / (imageHeight - 1)) + "%");
			// double[] x1 = Arrays.copyOf(pMapT[i], newLength);
			// double mx1 = cMeans[i];
			for (int j = i; j < imageHeight; j++) {
				// double[] x2 = Arrays.copyOf(pMapT[j], newLength);
				// double mx2 = cMeans[j];
				double v = 0d;
				for (int k = 0; k < imageWidth; k++) {

					v = v
							+ ((pMapT[k][i] - cMeans[i])
									* (pMapT[k][j] - cMeans[j]) / (imageWidth - 1));
				}

				cvMatrix[i][j] = v;
				cvMatrix[j][i] = v;
			}
		}
		logger.info("Covariance matrix calculated in "
				+ (System.currentTimeMillis() - startTime) + "ms.");
		return new Matrix(cvMatrix);
	}

	/**
	 * Normalize matrix (Mean centered matrix)
	 * 
	 * @param dataMatrix
	 * @return Matrix
	 */
	public static Matrix normalizeMatrix(Matrix dataMatrix) {
		double sum = 0d;
		for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
			for (int j = 0; j < dataMatrix.getColumnDimension(); j++) {
				sum = sum + dataMatrix.get(i, j);
			}
		}
		// Mean value of matrix
		double mean = sum
				/ (dataMatrix.getRowDimension() * dataMatrix
						.getColumnDimension());

		// Mean centered image
		Matrix mCDataMatrix = dataMatrix.minus(new Matrix(dataMatrix
				.getRowDimension(), dataMatrix.getColumnDimension(), mean));

		return mCDataMatrix;
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

	public static Object readFromFile(String filePath) {
		Object obj = null;
		try {
			FileInputStream f_in = new FileInputStream(filePath);
			ObjectInputStream obj_in = new ObjectInputStream(f_in);
			obj = obj_in.readObject();

		} catch (FileNotFoundException e) {
			logger.severe("File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			logger
					.severe("Could not read from file.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			logger
					.severe("Could not find class for file.");
			e.printStackTrace();
		}

		return obj;
	}

	public static void saveToFile(Object obj, String outputFilePath) {
		try {
			FileOutputStream fOut = new FileOutputStream(outputFilePath);
			ObjectOutputStream obj_out = new ObjectOutputStream(fOut);
			obj_out.writeObject(obj);
		} catch (FileNotFoundException e) {
			logger.severe("File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe("Could not write file " + outputFilePath + ".");
			e.printStackTrace();
		}

	}
}