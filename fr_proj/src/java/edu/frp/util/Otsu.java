package edu.frp.util;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class Otsu {

	private static int LEFT,WEIGHT = 0;
	private static int RIGHT,VALUE = 1;

	/**
	 * Automatic threshold value for histogram
	 * 
	 * @param h
	 * @return double
	 */
	public static double threshold(Histogram h) {
		double minVariance = Double.MAX_VALUE;
		int bestTh = 1;
		for (int currentTh = bestTh; currentTh < 256; currentTh++) {
			double[] leftFreqs = findFrequencies(h, currentTh, LEFT);
			double[] rightFreqs = findFrequencies(h, currentTh, RIGHT);

			double leftMean = leftFreqs[VALUE] / leftFreqs[WEIGHT];
			double rightMean = rightFreqs[VALUE] / rightFreqs[WEIGHT];

			double leftVariance = wVariance(h, currentTh, leftMean, LEFT);
			double rightVariance = wVariance(h, currentTh, rightMean, RIGHT);

			double currentVariance = bVariance(leftFreqs[WEIGHT], rightFreqs[WEIGHT],
					leftVariance, rightVariance);
			
			// Minimum variance between classes
			if (currentVariance < minVariance) {
				minVariance = currentVariance;
				bestTh = currentTh;
			}
		}
		return (double) (bestTh / 255D);
	}

	/**
	 * 
	 * @param h
	 * @param threshold
	 * @return
	 */
	public static double findMaxValue(Histogram h, int threshold) {
		return findFrequencies(h, 255, LEFT)[WEIGHT];
	}

	
	public static double[] findFrequencies(Histogram h, int currentTh,
			int classId) {
		double wf = 0f;
		double vf = 0f;

		int start = 0;
		int end = currentTh;

		if (classId == RIGHT) {
			start = currentTh;
			end = 256;
		}

		for (int i = start; i < end; i++) {
			wf += h.getColorFrequency(0, i);
			vf += i * h.getColorFrequency(0, i);
		}

		double result[] = new double[2];
		result[WEIGHT] = wf;
		result[VALUE] = vf;
		return result;
	}

	/**
	 * Within class variance
	 * @param h
	 * @param currentTh
	 * @param mean
	 * @param side
	 * @return
	 */
	public static double wVariance(Histogram h, int currentTh, double mean,
			int side) {
		int start = 0;
		int end = currentTh;
		if (side == RIGHT) {
			start = currentTh;
			end = 256;
		}

		double result = 0f;
		double weightFreq = 0f;
		for (int i = start; i < end; i++) {
			double wVariance = Math.pow(((double) i) - mean, 2);
			result += wVariance * (double) h.getColorFrequency(0, i);
			weightFreq += (double) h.getColorFrequency(0, i);
		}
		
		return result/weightFreq;
	}
	
	/**
	 * Variance between classes
	 * @param wf1
	 * @param wf2
	 * @param var1
	 * @param var2
	 * @return
	 */
	public static double bVariance(double wf1, double wf2, double var1,
			double var2) {
		double w1 = wf1 / (wf1 + wf2);
		double w2 = wf2 / (wf1 + wf2);

		return (w1 * var1 + w2 * var2);
	}
}
