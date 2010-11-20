package edu.frp.test;

import java.io.File;
import java.io.IOException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Jama.Matrix;
import edu.frp.util.EigenFace;
import edu.frp.util.Image;
import edu.frp.util.Knn;
import edu.frp.util.PcaTransformMatrix;
import edu.frp.util.Util;
import edu.frp.util.knn.KnnClass;

public class Main {

	/**
	 * Main test
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			if (args != null && args.length == 5) {
			if(true){
				//int totalClasses = 40;
				//int argn=0;
				//System.out.println("Initializing database...");
				//String baseDir = "C:\\Users\\Saulo\\Documents\\Faculdade\\Visão Computacional\\bases\\att_faces\\";
				//int nComponents = Integer.parseInt(args[argn++]);
				//int trainLength = Integer.parseInt(args[argn++]);

				//ArrayList<Object> imagesArray = new ArrayList<Object>();
				ArrayList<KnnClass> data = new ArrayList<KnnClass>();

				// Transformation Matrix
//				System.out.println("Criando matriz de transformação...");
//				PcaTransformMatrix tm = null;
//				int length = 0;
//				for (int nClass = 0; nClass < totalClasses; nClass++) {
//					for (int nImage = 0; nImage < trainLength; nImage++) {
//						Image image = new Image(baseDir + "s" + (nClass + 1)
//								+ File.separator + (nImage + 1) + ".jpg");
//						length = image.getWidth() * image.getHeight();
//						imagesArray.add(image);
//					}
//				}
				// tm = new PcaTransformMatrix(imagesArray, length, nComponents,
				// "C:\\test\\pca200.matrix", false);
//				tm = new PcaTransformMatrix("C:\\test\\pca.matrix");
//				System.out.println("Matriz de transformação criada.");

				// Preparing data for KNN
//				Map<Integer, ArrayList<Matrix>> eigenFaces = new HashMap<Integer, ArrayList<Matrix>>();
//				for (int nClass = 0; nClass < totalClasses; nClass++) {
//					eigenFaces.put(nClass, new ArrayList<Matrix>());
//					for (int nImage = 0; nImage < imagesArray.size(); nImage++) {
//						Image image = (Image) imagesArray.get(nImage);
//						Matrix eigenFaceMatrix = new EigenFace(image, tm)
//								.getEfMatrix();
//						eigenFaces.get(nClass).add(eigenFaceMatrix);
//					}
//					data.add(new KnnClass("s" + (nClass + 1), eigenFaces
//							.get(nClass)));
//				}
//				Util.saveToFile(data, "C:\\test\\base.dat");

				 data = (ArrayList<KnnClass>)
				 Util.readFromFile("C:\\test\\out\\knn.data");

//				String testImagePath = baseDir + "s" + args[argn++] + File.separator
//						+ args[argn++] + ".jpg";
				
				String testImagePath = "C:\\test\\query2.jpg";
				Image testImage = new Image(testImagePath);

				System.out.println("Testing for query image: " + testImagePath
						+ ".");

				PcaTransformMatrix tm = new PcaTransformMatrix("C:\\test\\out\\pca.matrix");
				
				EigenFace query = new EigenFace(testImage, tm);

				Knn knnClassfier = new Knn(data, query.getEfMatrix(), 1, 2);
				System.out.println("Class found: " + knnClassfier.getKnn(1, 100D).getName());

			} else {
				System.out
						.println("Usage: java -jar pca.jar Base(<nComponents> <trainingLength>) Query(<classNumber> <imageNumber>)");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
