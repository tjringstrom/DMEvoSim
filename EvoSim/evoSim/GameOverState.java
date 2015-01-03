package evoSim;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class GameOverState extends GameState {
	public GameOverState(GameStateManager gsm){
		super(gsm);
	}

	 
	public void init() {
		 
		
	}

	 
	public void tick() {
		 
		
	}

	 
	public void draw(Graphics g) {
//		g.setFont(new Font("Helvetica", Font.BOLD, 500));
//		g.drawString("DEAD", 20, 600);
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.drawString("DEAD", 710, 400);
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.drawString("Press 'n'", 700, 500);
		
	}

	 
	public void keyPressed(int k) {
		if (KeyEvent.VK_N == k) {
//			gsm.states.push(new SimState(gsm,2,2,2));
		}
		
	}

	 
	public void keyReleased(int k) {
		 
		
	}
}
