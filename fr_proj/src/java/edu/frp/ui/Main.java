package edu.frp.ui;

import java.io.IOException;

import edu.frp.exception.UnsupportedImageTypeException;
import edu.frp.image.Image;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Image i = new Image("C:\\Users\\Saulo\\Pictures\\altair.png");
			i.write("C:\\Users\\Saulo\\Pictures\\naosalvo.jpg", Image.JPEG);
			i.getLayerMatrix(Image.RED_LAYER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
