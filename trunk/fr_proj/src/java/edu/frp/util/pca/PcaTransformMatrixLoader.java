package edu.frp.util.pca;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import edu.frp.util.Image;
import edu.frp.util.Util;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 * @author Arlington (abr@ecomp.poli.br)
 */
public class PcaTransformMatrixLoader {
	private static Logger logger = Logger.getLogger(PcaTransformMatrixLoader.class
			.getName());

	private static final long serialVersionUID = 3627512864882477843L;

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static Matrix getMatrix(String filePath) {
		return (Matrix) Util.readFromFile(filePath);
	}

	/**
	 * 
	 * @param imagesArray
	 * @param length
	 * @param nComponents
	 * @param outputFilePath
	 * @param overwrite
	 * @return
	 */
	public static Matrix getMatrix(ArrayList<Object> imagesArray, int length,
			int nComponents, String outputFilePath, boolean overwrite) {
		double[][] linearData = new double[imagesArray.size()][length];

		File outputFile = new File(outputFilePath);
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}

		Matrix pcaMatrix = null;
		if (outputFile.exists() && !overwrite) {
			logger.severe("PCA matrix file already exists.");
			return getMatrix(outputFilePath);
		} else {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				logger
						.severe("Could not create file to store PCA transformation matrix.");
				e.printStackTrace();
			}

			if (imagesArray != null && imagesArray.size() > 0) {

				for (int i = 0; i < imagesArray.size(); i++) {
					linearData[i] = new Matrix(((Image) imagesArray.get(i))
							.getLayerMatrix(0)).getRowPackedCopy();
				}
				Matrix normalizedData = Util.normalizeMatrix(new Matrix(
						linearData));
				
				pcaMatrix = extractPrincipalComponents(nComponents, normalizedData);
			}

			Util.saveToFile(pcaMatrix, outputFilePath);
			return pcaMatrix;
		}
	}

	/**
	 * 
	 * @param nComponents
	 * @param mCDataMatrix
	 * @return
	 */
	private static Matrix extractPrincipalComponents(int nComponents, Matrix mCDataMatrix) {
		// Eigenvalues decomposition
		logger.info("Extracting principal components...");
		long startTime = System.currentTimeMillis();

		logger.info("Starting eigenvalue decomposition...");
		EigenvalueDecomposition eigs = Util.cvMatrix(mCDataMatrix).eig();

		logger.info("Finished eigenvalue decomposition in"
				+ (System.currentTimeMillis() - startTime) + "ms.");
		double[] eValues = eigs.getRealEigenvalues();
		double[] eValuesSorted = eValues;
		for (int i = 0; i < eValuesSorted.length; i++) {
			eValuesSorted[i] = Math.abs(eValuesSorted[i]);
		}
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
		double[][] eVectorsT = (eVectors.transpose()).getArray();
		int newLength = eVectorsT[0].length;
		double[][] principalComponents = new double[pCIndexes.length][newLength];
		count = 0;

		for (int i : pCIndexes) {
			principalComponents[count] = Arrays.copyOf(eVectorsT[i], newLength);
			count++;
		}

		
		logger.info("Principal components extracted in "
				+ (System.currentTimeMillis() - startTime) + "ms");
		return new Matrix(principalComponents);
	}
}