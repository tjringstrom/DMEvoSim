package evoSim;

import java.awt.Graphics;
import java.util.Map;

import org.la4j.matrix.sparse.CRSMatrix;

public abstract class GameState extends Globals {
	public GameStateManager gsm;
	
	
	protected GameState(GameStateManager gsm){
		this.gsm = gsm;
		init();
	}
	
	public abstract void init();
	public abstract void tick();
	public abstract void draw(Graphics g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);

}
