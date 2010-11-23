package edu.frp.test;

import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;
import edu.frp.util.Image;
import edu.frp.util.Util;
import edu.frp.util.knn.Knn;
import edu.frp.util.knn.KnnClass;
import edu.frp.util.pca.EigenFace;
import edu.frp.util.pca.PcaTransformMatrixLoader;

public class Main {

	/**
	 * Main test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ArrayList<KnnClass> data = new ArrayList<KnnClass>();

		data = (ArrayList<KnnClass>) Util
				.readFromFile("C:\\test\\out\\knn.data");

		String testImagePath = "C:\\test\\query2.jpg";
		Image testImage;
		try {
			testImage = new Image(testImagePath);
			System.out.println("Testing for query image: " + testImagePath
					+ ".");

			Matrix tm = PcaTransformMatrixLoader
					.getMatrix("C:\\test\\out\\pca.matrix");

			EigenFace query = new EigenFace(testImage, tm);

			Knn knnClassfier = new Knn(data, query.getEfMatrix(), 1, 2);
			System.out.println("Class found: "
					+ knnClassfier.getKnn(1, 100D).getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
