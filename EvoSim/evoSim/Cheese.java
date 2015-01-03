package evoSim;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Cheese {
	Hex current;
	
	public Cheese(Hex hex){
		current = hex;
	}
	
BufferedImage cheeseImg = null;{ //FIX??
		
		try {
			cheeseImg = ImageIO.read(new File("cheese.jpg"));
		} catch(Exception ex){}
		}
}
