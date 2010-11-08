package edu.frp.util;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class Histogram {

	private int[][] frequencyValues;

	private int[][] cdf;
	private int[] cdfMin;
	private int nPixels;

	private boolean isColored;

	public Histogram(Image image) {
		frequencyValues = new int[3][256];

		nPixels = image.getWidth() * image.getHeight();

		isColored = image.isColored();

		double[][] redMatrix = image.getLayerMatrix(Image.RED_LAYER);
		double[][] greenMatrix = image.getLayerMatrix(Image.GREEN_LAYER);
		double[][] blueMatrix = image.getLayerMatrix(Image.BLUE_LAYER);

		int width = image.getWidth();
		int height = image.getHeight();

		int value = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				value = (int) Math.abs(255 * redMatrix[i][j]);
				frequencyValues[0][value] = frequencyValues[0][value] + 1;

				value = (int) Math.abs(255 * greenMatrix[i][j]);
				frequencyValues[1][value] = frequencyValues[1][value] + 1;

				value = (int) Math.abs(255 * blueMatrix[i][j]);
				frequencyValues[2][value] = frequencyValues[2][value] + 1;
			}
		}
	}

	/**
	 * Histogram for requested layer <i> <li>Image.RED_LAYER <li>
	 * Image.BLUE_LAYER <li>Image.GREEN_LAYER </i>
	 * 
	 * @param layerNum
	 * @return int[]
	 */
	public int[] getHistogram(int layerNum) {
		return frequencyValues[layerNum];
	}

	/**
	 * Returns color frequency on given layer
	 * 
	 * @param layerNum
	 * @param colorValue
	 * @return int
	 */
	public int getColorFrequency(int layerNum, int colorValue) {
		return frequencyValues[layerNum][colorValue];
	}

	public int getEqualizedValue(int v, int l) {
		if (cdf == null) {
			buildCdf();
		}
		return Math
				.round(((cdf[l][v] - cdfMin[l]) / (nPixels) - cdfMin[l]) * 255);
	}

	/**
	 * Build the Cumulative Distribution Function (Cumulative Histogram) used by
	 * histogram equalization
	 */
	private void buildCdf() {
		cdf = new int[3][256];
		cdfMin = new int[3];

		int nlayers = 1;
		cdfMin[0] = nPixels;
		if (isColored) {
			nlayers = 3;
			cdfMin[1] = nPixels;
			cdfMin[2] = nPixels;
		}

		int[] total = new int[3];
		for (int i = 0; i < 256; i++) {
			for (int l = 0; l < nlayers; l++) {
				total[l] = 0;
				for (int j = 0; j <= i; j++) {
					total[l] += frequencyValues[l][j];
				}
				cdf[l][i] = total[l];

				if (total[l] < cdfMin[l]) {
					cdfMin[l] = total[l];
				}
			}
		}
	}

	/**
	 * Get equalized correspondent equalized histogram
	 * 
	 * @return Equalized Histogram
	 */
	public Histogram getEqualizedHistogram() {
		Histogram equalizedHistogram = this;
		int[][] newFrequencyValues = new int[3][256];

		int nlayers = 1;
		if (isColored) {
			nlayers = 3;
		}

		for (int l = 0; l < nlayers; l++) {
			for (int i = 0; i < 256; i++) {
				newFrequencyValues[0][i] = this.getEqualizedValue(
						frequencyValues[0][i], 0);
			}
		}

		equalizedHistogram.setFrequencyValues(newFrequencyValues);

		return equalizedHistogram;

	}

	/**
	 * @return the frequencyValues
	 */
	public int[][] getFrequencyValues() {
		return frequencyValues;
	}

	/**
	 * @param frequencyValues
	 *            the frequencyValues to set
	 */
	public void setFrequencyValues(int[][] frequencyValues) {
		this.frequencyValues = frequencyValues;
	}
}
