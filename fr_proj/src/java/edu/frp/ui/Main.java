package edu.frp.ui;

import java.io.IOException;

import edu.frp.exception.UnsupportedImageTypeException;
import edu.frp.util.Image;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Image i = new Image("C:\\Users\\Saulo\\Documents\\Faculdade\\Visão Computacional\\bases\\s40.pgm");
			i.write("C:\\Users\\Saulo\\Pictures\\naosalvo.jpg", Image.JPEG);
			i.getLayerMatrix(Image.RED_LAYER);
			String result = i.isColored() ? "colorida" : "cinza";
			System.out.println(result);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
