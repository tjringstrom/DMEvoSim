package evoSim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class MenuState extends GameState {

	private String[] options = { "Start", "Options", "Exit" };
	private int currentSelection = 0;

	public MenuState(GameStateManager gsm) {
		super(gsm);
	}

	public void init() {
	}

	public void tick() {

	}

	public void draw(Graphics g) {
		g.setColor(new Color(50, 0, 150));
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		for (int i = 0; i < options.length; i++) {
			if (i == currentSelection) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			}

			g.setFont(new Font("Helvetica", Font.BOLD, 30));
			g.drawString(options[i], GamePanel.WIDTH / 2 - 40, 50 + i * 30);
		}
	}

	public void keyPressed(int k) {
		if (k == KeyEvent.VK_DOWN) {
			currentSelection++;
			if (currentSelection >= options.length) {
				currentSelection = 0;
			}
		} else if (k == KeyEvent.VK_UP) {
			currentSelection--;
			if (currentSelection < 0) {
				currentSelection = options.length - 1;
			}
		}

		if (k == KeyEvent.VK_ENTER) {
			if (currentSelection == 0) {
				gsm.states.push(new SimState(gsm, gsm.getNextMouseBrain()));
			} else if (currentSelection == 1) {
				gsm.states.push(new MenuState(gsm));
			} else if (currentSelection == 2) {
				System.exit(0);
			}
		}
	}

	public void keyReleased(int k) {

	}

}
