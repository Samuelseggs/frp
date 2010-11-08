package edu.frp.util;

import java.util.Arrays;
import java.util.logging.Logger;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class EigenFace {
	private static Logger logger = Logger.getLogger(EigenFace.class.getName());
	private Matrix eigMatrix;

	public EigenFace(Image image, int nComponents) {
		logger.info("Building eigenface...");
		long startTime = System.currentTimeMillis();

		double[][] data = image.getLayerMatrix(0);
		Matrix dataMatrix = new Matrix(data);
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

		EigenvalueDecomposition eigs = Util.cvMatrix(mCDataMatrix).eig();

		double[] eValues = eigs.getRealEigenvalues();
		double[] eValuesSorted = eValues;
		Arrays.sort(eValues);

		// Get principal components indexes
		int count = 0;
		if (nComponents > eValuesSorted.length) {
			nComponents = eValuesSorted.length;
		}
		int[] pCIndexes = new int[nComponents];
		for (int i = eValuesSorted.length - 1; i > (eValuesSorted.length - nComponents); i--) {
			pCIndexes[count] = Arrays.binarySearch(eValues, eValuesSorted[i]);
			count++;
		}

		Matrix eVectors = eigs.getV();
		double[][] eVectorsT = eVectors.transpose().getArray();
		int newLength = eVectorsT[0].length;
		double[][] principalComponents = new double[pCIndexes.length][newLength];
		count = 0;
		for (int i : pCIndexes) {
			principalComponents[count] = Arrays.copyOf(eVectorsT[i], newLength);
			count++;
		}
		Matrix pCMatrix = new Matrix(principalComponents);

		this.eigMatrix = pCMatrix.times(mCDataMatrix.transpose());
		long finalTime = (System.currentTimeMillis() - startTime);
		logger.info("Eigenface built in " + finalTime + "ms.");
	}

	/**
	 * @return the eigMatrix
	 */
	public Matrix getEigMatrix() {
		return eigMatrix;
	}
}
