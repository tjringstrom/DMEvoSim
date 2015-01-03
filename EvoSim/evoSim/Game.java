package evoSim;
import java.awt.BorderLayout;
import javax.swing.*;

public class Game {

	public static void main(String[] args){
		JFrame frame = new JFrame("Sim");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.add(new GamePanel(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}
	//test
}
