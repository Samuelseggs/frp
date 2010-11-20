package edu.frp.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 * @author Arlington (abr@ecomp.poli.br)
 */
public class PcaTransformMatrix {
	private static Logger logger = Logger.getLogger(PcaTransformMatrix.class
			.getName());

	private static final long serialVersionUID = 3627512864882477843L;
	private Matrix pCMatrix;
	private double[][] linearData;

	/**
	 * @return the pCMatrix
	 */
	public Matrix getTransformMatrix() {
		return pCMatrix;
	}

	public PcaTransformMatrix(String filePath) {
		// Object obj = null;
		// try {
		//
		// FileInputStream f_in = new FileInputStream(filePath);
		// ObjectInputStream obj_in = new ObjectInputStream(f_in);
		// obj = obj_in.readObject();
		//
		// } catch (FileNotFoundException e) {
		// logger.severe("PCA transformation matrix not found.");
		// e.printStackTrace();
		// } catch (IOException e) {
		// logger
		// .severe("Could not read from PCA transformation matrix file.");
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// logger
		// .severe("Could not find class for PCA transformation matrix file.");
		// e.printStackTrace();
		// }
		//
		// if (obj instanceof Matrix) {
		// this.pCMatrix = (Matrix) obj;
		// } else {
		// logger.severe("Failed to load transformation matrix from file.");
		// }
		this.pCMatrix = (Matrix) Util.readFromFile(filePath);
	}

	/**
	 * Class constructor
	 * 
	 * @param imagesArray
	 * @param length
	 * @param nComponents
	 * @param outputFilePath
	 */
	public PcaTransformMatrix(ArrayList<Object> imagesArray, int length,
			int nComponents, String outputFilePath, boolean overwrite) {
		this.linearData = new double[imagesArray.size()][length];

		File outputFile = new File(outputFilePath);
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}

		if (outputFile.exists() && !overwrite) {
			logger.severe("PCA matrix file already exists.");
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
				extractPrincipalComponents(nComponents, normalizedData);
				this.pCMatrix = normalizedData;
			}

			Util.saveToFile(this.pCMatrix, outputFilePath);
		}
	}

	/**
	 * Extract Principal components
	 * 
	 * @param nComponents
	 *            number of components to extract
	 * @param mCDataMatrix
	 *            normalized matrix
	 * @return Matrix
	 */
	private void extractPrincipalComponents(int nComponents, Matrix mCDataMatrix) {
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

		this.pCMatrix = new Matrix(principalComponents);
		logger.info("Principal components extracted in "
				+ (System.currentTimeMillis() - startTime) + "ms");
	}
}