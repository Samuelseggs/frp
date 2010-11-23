package edu.frp.util.pca;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import Jama.Matrix;
import edu.frp.util.Image;
import edu.frp.util.Util;
import edu.frp.util.knn.KnnClass;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class PcaUpdater {

    private static Logger logger = Logger.getLogger(PcaUpdater.class.getName());
    private static String CLASS_FOLDER_PREFIX = "class_";
    private static String PCA_FILENAME = "pca.matrix";
    private static String KNN_FILENAME = "knn.data";

    public static void main(String[] args) {
	if (args != null && args.length == 3) {
	    int totalClasses = 0;
	    int nargs = 0;
	    String inputDirPath = args[nargs++];
	    File inputDir = new File(inputDirPath);
	    if (inputDir.exists()) {
		Map<Integer, ArrayList<Object>> classesImagesMap = new HashMap<Integer, ArrayList<Object>>();
		File[] classesDir = inputDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
			return name.startsWith(CLASS_FOLDER_PREFIX);
		    }
		});
		// Number of classes
		totalClasses = classesDir.length;

		String[] classesNames = new String[totalClasses];
		int length = 0;
		ArrayList<Object> imagesArray = new ArrayList<Object>();
		for (int nClass = 0; nClass < totalClasses; nClass++) {
		    File cDir = classesDir[nClass];
		    classesNames[nClass] = cDir.getName().replace(
			    CLASS_FOLDER_PREFIX, "");
		    File[] images = cDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
			    return name.toLowerCase().endsWith(".jpg");
			}
		    });

		    classesImagesMap.put(nClass, new ArrayList<Object>());
		    Image tempImage = null;
		    for (File image : images) {
			try {
			    tempImage = new Image(image.getAbsolutePath());

			    classesImagesMap.get(nClass).add(tempImage);
			    imagesArray.add(tempImage);
			} catch (IOException e) {
			    logger.severe("Could not load image "
				    + image.getAbsolutePath() + ".");
			    e.printStackTrace();
			}
		    }
		    length = tempImage.getWidth() * tempImage.getHeight();
		}

		int nComponents = Integer.parseInt(args[nargs++]);
		String outputDirPath = args[nargs++];
		
		// PCA Matrix
		Matrix tm = PcaTransformMatrixLoader.getMatrix(
			imagesArray, length, nComponents, outputDirPath
				+ File.separator + PCA_FILENAME, false);

		ArrayList<KnnClass> data = new ArrayList<KnnClass>();
		Map<Integer, ArrayList<Matrix>> eigenFaces = new HashMap<Integer, ArrayList<Matrix>>();

		for (int nClass = 0; nClass < totalClasses; nClass++) {
		    eigenFaces.put(nClass, new ArrayList<Matrix>());

		    for (Object image : classesImagesMap.get(nClass)) {
			
			// Calculate eigenface for Image given the PCA matrix tm
			Matrix eigenFaceMatrix = new EigenFace((Image) image,
				tm).getEfMatrix();
			
			// Add new eigenface to a class
			eigenFaces.get(nClass).add(eigenFaceMatrix);
		    }
		    // Add class to data
		    data.add(new KnnClass(classesNames[nClass], eigenFaces
			    .get(nClass)));
		}
		
		// Save KNN data to file
		Util.saveToFile(data, outputDirPath + File.separator
			+ KNN_FILENAME);
	    }

	} else {
	    logger
		    .warning("Wrong usage of PcaBuilder. It should match "
			    + "\"java -jar buildpca <inputDir> <nComponents> <outputDir>\"");
	}
    }

}
