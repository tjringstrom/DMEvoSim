package evoSim;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.uncommons.maths.random.GaussianGenerator;

public class Enemy1 extends Enemy {
	Random rnd = new Random();
	org.uncommons.maths.random.GaussianGenerator gRand = new GaussianGenerator(0.0,1.0, rnd);
	int fullness = 0;
	int fastPace = 1;
	boolean scent = false;
	public enum State{WANDERING, HUNTING};
	State enemy1state = State.WANDERING;

	
	//trajectory key 1=ul 2=u 3=ur 4=br 5=b 6=bl
	
	BufferedImage wanderingImg = null;{ //FIX??
		try {
			wanderingImg = ImageIO.read(new File("greenskull.jpg"));
		} catch(Exception ex){}
		}
	BufferedImage huntingImg = null;{ //FIX??
		
		try {
			huntingImg = ImageIO.read(new File("redskull.jpg"));
		} catch(Exception ex){}
		}
	BufferedImage stateImg = wanderingImg;



	public Enemy1(Hex currentpos, int initialTraj) {
		traj = initialTraj;
		current = currentpos;
		this.mainTrajProb = 0.9;
		this.rightTrajProb = 0.05;
		this.leftTrajProb = 0.05;
	}
	
	// change trajectory by adding positive number to turn right, negative to
	// turn left
	int changeTraj(int turnNumber) {

		traj += turnNumber;
		if (traj > 6) {
			traj -= 6;
		}
		if (traj < 1) {
			traj += 6;
		}
		return traj;
	}

	void setTrajToHunter(Hex hunterPos){
		double angle = Math.atan2((hunterPos.xCoor - current.xCoor),
				(hunterPos.yCoor - current.yCoor));
		angle = Math.toDegrees(angle);
		
		if((hunterPos.xCoor - current.xCoor) == 0 && (hunterPos.yCoor - current.yCoor) == 0){
			traj = 0;
		}
		else if(angle >= 90.0 && angle < 150.0){
			traj = 3;
		}
		else if((angle >= 150.0 && angle <= 180.0) || (angle > -180.0 && angle <= -150.0)){
			traj = 2;
		}
		else if(angle >= -150.0 && angle < -90.0){
			traj = 1;
		}
		else if(angle >= -90.0 && angle < -30.0){
			traj = 6;
		}
		else if(angle >= -30.0 && angle < 30.0){
			traj = 5;
		}
		else if(angle >= 30.0 && angle < 90.0){
			traj = 4;
		}	
	}
	
	void moveEnemy1(Hex hunterPos) {
		if(this.enemy1state.equals(State.HUNTING)){
			setTrajToHunter(hunterPos);
			this.stateImg = this.huntingImg;
		}
		if(this.enemy1state.equals(State.WANDERING)){
			this.stateImg = this.wanderingImg;
		}
		if(this.enemy1state == Enemy1.State.WANDERING){
			double rndNum = rnd.nextDouble();
			if(rndNum < this.leftTrajProb){
				this.changeTraj(-1);
			} else if((rndNum = rndNum - this.leftTrajProb) < this.rightTrajProb){
				this.changeTraj(1);
			}
		}
			switch (traj) {
			case 1:
				if (current.ul != null) {
					current.ul.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ul;
					traj = 1;
					break;
				} else if (current.ul == null && current.ur != null) {
					current.ur.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ur;
					traj = 3;
					break;
				} else if (current.ul == null && current.ur == null
						&& current.bl != null) {
					current.bl.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.bl;
					traj = 6;
					break;
				} else {
					current.br.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.br;
					traj = 4;
					break;
				}
			case 2:
				if (current.u != null) {
					current.u.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.u;
					traj = 2;
					break;
				} else {
					current.b.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.b;
					traj = 5;
					break;
				}
			case 3:
				if (current.ur != null) {
					current.ur.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ur;
					traj = 3;
					break;
				} else if (current.ur == null && current.ul != null) {
					current.ul.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ul;
					traj = 1;
					break;
				} else if (current.ur == null && current.ul == null
						&& current.br != null) {
					current.br.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.br;
					traj = 4;
					break;
				} else {
					current.bl.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.bl;
					traj = 6;
					break;
				}
			case 4:
				if (current.br != null) {
					current.br.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.br;
					traj = 4;
					break;
				} else if (current.br == null && current.bl != null) {
					current.bl.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.bl;
					traj = 6;
					break;
				} else if (current.br == null && current.ur != null) {
					current.ur.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ur;
					traj = 3;
					break;
				} else {
					current.ul.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ul;
					traj = 1;
					break;
				}
			case 5:
				if (current.b != null) {
					current.b.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.b;
					traj = 5;
					break;
				} else {
					current.u.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.u;
					traj = 2;
					break;
				}
			case 6:
				if (current.bl != null) {
					current.bl.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.bl;
					traj = 6;
					break;
				} else if (current.bl == null && current.br != null) {
					current.br.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.br;
					traj = 4;
					break;
				} else if (current.bl == null && current.br == null
						&& current.ul != null) {
					current.ul.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ul;
					traj = 1;
					break;
				} else {
					current.ur.hexEnemy1List.add(this);
					current.removeEnemy1FromList(this);
					current = current.ur;
					traj = 3;
					break;
				}
			}
			
	}
	
	
}
