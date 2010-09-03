package edu.frp.image;

import java.util.logging.Logger;

/**
 * Image moments
 * @author Arlington
 * @author Saulo (scsm@ecomp.poli.br)<p>
 * {@link http://en.wikipedia.org/wiki/Image_moment}
 */
public class Moments {

	private static Logger logger = Logger.getLogger(Moments.class.getName());
	
	public static double getRawMoment(int p, int q, double[][] matrix) {
		double m = 0;
		logger.finest("Calculating raw moment for p="+p+" and q="+q);
		for (int i = 0, k = matrix.length; i < k; i++) {
			for (int j = 0, l = matrix[i].length; j < l; j++) {
				m += Math.pow(i, p) * Math.pow(j, q) * matrix[i][j];
			}
		}
		return m;
	}

	public static double getCentralMoment(int p, int q, double[][] img) {
		double mc = 0;
		double m00 = Moments.getRawMoment(0, 0, img);
		double m10 = Moments.getRawMoment(1, 0, img);
		double m01 = Moments.getRawMoment(0, 1, img);
		double x0 = m10 / m00;
		double y0 = m01 / m00;
		for (int i = 0, k = img.length; i < k; i++) {
			for (int j = 0, l = img[i].length; j < l; j++) {
				mc += Math.pow((i - x0), p) * Math.pow((j - y0), q) * img[i][j];
			}
		}
		return mc;
	}

	public static double getCovarianceXY(int p, int q, double[][] matrix) {
		double mc00 = Moments.getCentralMoment(0, 0, matrix);
		double mc11 = Moments.getCentralMoment(1, 1, matrix);
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
		double mc00 = Moments.getCentralMoment(0, 0, matrix);
		double mc20 = Moments.getCentralMoment(2, 0, matrix);
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
		double mc00 = Moments.getCentralMoment(0, 0, matrix);
		double mc02 = Moments.getCentralMoment(0, 2, matrix);
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
		double mpq = Moments.getCentralMoment(p, q, matrix);
		double m00gama = Math.pow(Moments.getCentralMoment(0, 0, matrix), gama);
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
		n20 = Moments.getNormalizedCentralMoment(2, 0, matrix),
		n02 = Moments.getNormalizedCentralMoment(0, 2, matrix),
		n30 = Moments.getNormalizedCentralMoment(3, 0, matrix),
		n12 = Moments.getNormalizedCentralMoment(1, 2, matrix),
		n21 = Moments.getNormalizedCentralMoment(2, 1, matrix),
		n03 = Moments.getNormalizedCentralMoment(0, 3, matrix),
		n11 = Moments.getNormalizedCentralMoment(1, 1, matrix);
		
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
}