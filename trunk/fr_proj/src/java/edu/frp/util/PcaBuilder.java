package edu.frp.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import Jama.Matrix;
import edu.frp.util.knn.KnnClass;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class PcaBuilder {

	private static Logger logger = Logger.getLogger(PcaBuilder.class.getName());
	private static String CLASS_FOLDER_PREFIX = "class_";

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
					classesNames[nClass] = cDir.getName().replace(CLASS_FOLDER_PREFIX, "");
					File[] images = cDir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".jpg");
						}
					});
					
					classesImagesMap.put(nClass, new ArrayList<Object>());
					
					for (File image : images) {
						Image tempImage;
						try {
							tempImage = new Image(image.getAbsolutePath());
							length = tempImage.getWidth()
									* tempImage.getHeight();
							classesImagesMap.get(nClass).add(tempImage);
							imagesArray.add(tempImage);
						} catch (IOException e) {
							logger.severe("Could not load image "+image.getAbsolutePath()+".");
							e.printStackTrace();
						}
					}
				}
				
				// Reunite all images
				
				int nComponents = Integer.parseInt(args[nargs++]);
				String outputDirPath = args[nargs++];
				PcaTransformMatrix tm = new PcaTransformMatrix(imagesArray,
						length, nComponents, outputDirPath + File.separator + "pca.matrix", false);

				ArrayList<KnnClass> data = new ArrayList<KnnClass>();
				Map<Integer, ArrayList<Matrix>> eigenFaces = new HashMap<Integer, ArrayList<Matrix>>();

				for (int nClass = 0; nClass < totalClasses; nClass++) {
					eigenFaces.put(nClass, new ArrayList<Matrix>());
					
					for(Object image : classesImagesMap.get(nClass)){
						Matrix eigenFaceMatrix = new EigenFace((Image)image, tm).getEfMatrix();
						eigenFaces.get(nClass).add(eigenFaceMatrix);
					}
					data.add(new KnnClass(classesNames[nClass], eigenFaces
							.get(nClass)));
				}
				Util.saveToFile(data, outputDirPath + File.separator + "knn.data");
			}

		} else {
			logger.warning("Wrong usage of PcaBuilder. It should match \"buildpca <inputDir> <nComponents> <outputDir>\"");
		}
	}

}
