package edu.frp.util;

import java.util.logging.Logger;

import Jama.Matrix;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 * @author Arlington (abr@ecomp.poli.br)
 */
public class EigenFace {
	private static Logger logger = Logger.getLogger(EigenFace.class.getName());
	private Matrix efMatrix;

	/**
	 * Class constructor
	 * @param image Image
	 * @param tm Matrix
	 */
	public EigenFace(Image image, Matrix tm) {
		logger.info("Building eigenface...");
		long startTime = System.currentTimeMillis();

		double[][] linearData = new double[1][image.getWidth()*image.getHeight()];
		linearData[0] = new Matrix(image.getLayerMatrix(0)).getRowPackedCopy();

		Matrix linearDataMatrixT = new Matrix(linearData).transpose();
		Matrix transformMatrixT = tm;
		this.efMatrix = transformMatrixT.times(linearDataMatrixT);
		long finalTime = (System.currentTimeMillis() - startTime);
		logger.info("Eigenface built in " + finalTime + "ms.");
	}

	/**
	 * @return the efMatrix
	 */
	public Matrix getEfMatrix() {
		return efMatrix;
	}
}
