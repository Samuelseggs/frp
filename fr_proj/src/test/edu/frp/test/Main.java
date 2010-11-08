package edu.frp.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;
import edu.frp.util.EigenFace;
import edu.frp.util.Image;
import edu.frp.util.Util;
import edu.frp.util.knn.KnnClass;

public class Main {

	/**
	 * Main test
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//Image i = new Image("C:\\Users\\Saulo\\Documents\\Faculdade\\Visão Computacional\\bases\\texturas_color\\tex1.jpg");
			
			//Util.printM(Util.coMatrix(i, 8));
			
			//Util.cvMatrix(i).print(0, 4);
			
			if (args != null && args.length == 4) {
			
			System.out.println("Initializing database...");
			String baseDir = "C:\\Users\\Saulo\\Documents\\Faculdade\\Visão Computacional\\bases\\att_faces\\";
			int nComponents = Integer.parseInt(args[0]);
			int trainLength = Integer.parseInt(args[1]);
			//Image[][] imageMatrix = new Image[10][trainLength];
			ArrayList[] classesData = new ArrayList[10];
			KnnClass[] data = new KnnClass[10];
			for (int nClass = 0; nClass < 10; nClass++) {
				classesData[nClass] = new ArrayList<Matrix>();
				for (int nImage = 1; nImage < trainLength; nImage++) {
					Image image = new Image(baseDir + "s" + (nClass+1) + File.separator + nImage + ".jpg");
					//imageMatrix[nClass][nImage] = 
					EigenFace eFace = new EigenFace(image,nComponents);
					classesData[nClass].add(eFace.getEigMatrix());
				}
				data[nClass] = new KnnClass("s"+(nClass+1), classesData[nClass]);
			}
			
			String testImagePath = baseDir + "s" + args[2] + File.separator + args[3] + ".jpg";
			Image testImage = new Image(testImagePath);

			System.out.println("Testing for query image: " + testImagePath+".");
			EigenFace et = new EigenFace(testImage, nComponents);
			System.out.println("Class found: " + Util.knn(data, et.getEigMatrix(), 3, 2).getName());
			
			//System.out.println(Util.calcDistance(e1.getEigMatrix().getArray(), e2.getEigMatrix().getArray(), 2));
			
			} else {
				System.out.println("Usage: pca <nComponents> <trainingLength> <classNumber> <imageNumber>");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
