package edu.frp.util.pca;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import Jama.Matrix;

import edu.frp.util.Image;
import edu.frp.util.Util;
import edu.frp.util.knn.Knn;
import edu.frp.util.knn.KnnClass;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class Recognizer {

	private static Recognizer INSTANCE = null;

	private Matrix tm = null;
	private ArrayList<KnnClass> data = null;

	public static Recognizer getInstance() {
		if (INSTANCE == null) {
			return new Recognizer();
		} else {
			return INSTANCE;
		}
	}

	private Recognizer() {
	}

	/**
	 * Recognize Images
	 * @param biArray
	 * @param pcaMatrixPath
	 * @param k
	 * @param maxDissimilarity
	 * @return String[]
	 */
	@SuppressWarnings("unchecked")
	public String[] recognize(BufferedImage[] biArray, int k,
			double maxDissimilarity, String pcaFilesPath) {
		
		if(this.tm == null){
			this.tm = PcaTransformMatrixLoader.getMatrix(pcaFilesPath + File.separator + "pca.matrix");
			Object o = Util.readFromFile(pcaFilesPath + File.separator + "knn.data");
			if (o.getClass().isInstance(this.data)){
				this.data = (ArrayList<KnnClass>) o;
			}
		}
		
		String[] names = new String[biArray.length];

		for (int count = 0; count < biArray.length; count ++) {
			BufferedImage bi = biArray[count];
			Image tempImage = new Image(bi);
			EigenFace eigenFace = new EigenFace(tempImage, this.tm);
			Knn knnClassfier = new Knn(this.data, eigenFace.getEfMatrix(), 1, 2);

			KnnClass result = knnClassfier.getKnn(k, maxDissimilarity);
			if (result == null) {
				names[count] = "Unknown";
			} else {
				names[count] = result.getName();
			}
		}

		return names;
	}
}
