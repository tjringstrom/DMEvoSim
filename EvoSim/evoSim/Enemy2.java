package evoSim;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import evoSim.Enemy1.State;

public class Enemy2 extends Enemy {
	Random rnd = new Random();
	public enum State{WANDERING, HUNTING};
	State enemy2state = State.WANDERING;
	int fastPace = 1;
	int fullness = 0;
	
	LinkedList<Hex> shortestPathToHunter;
	LinkedList<Hex> shortestPathToE2;
	Enemy2 closestE2;
	Dijikstra enemy2Dijikstra = null;
	
	BufferedImage wanderingImg = null;{ //FIX??
		
		try {
			wanderingImg = ImageIO.read(new File("monkey.jpg"));
		} catch(Exception ex){}
		}
	BufferedImage huntingImg = null;{ //FIX??
		
		try {
			huntingImg = ImageIO.read(new File("redmonkey.jpg"));
		} catch(Exception ex){}
		}
	BufferedImage stateImg = wanderingImg;

	public Enemy2(Hex hex, int initialtraj, HexMap testMap){
		current = hex;
		traj = initialtraj;
		this.enemy2Dijikstra = new Dijikstra(testMap);
		this.mainTrajProb = 0.8;
		this.rightTrajProb = 0.1;
		this.leftTrajProb = 0.1;
	}
	
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
	
	void moveEnemy2(Hex hunterPos) {
		if(this.enemy2state.equals(State.HUNTING)){
			setTrajToHunter(hunterPos);
			this.stateImg = this.huntingImg;
		}
		if(this.enemy2state.equals(State.WANDERING)){
			this.stateImg = this.wanderingImg;
		}
		if(this.enemy2state == Enemy2.State.WANDERING){
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
					current.ul.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ul;
					traj = 1;
					break;
				} else if (current.ul == null && current.ur != null) {
					current.ur.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ur;
					traj = 3;
					break;
				} else if (current.ul == null && current.ur == null
						&& current.bl != null) {
					current.bl.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.bl;
					traj = 6;
					break;
				} else {
					current.br.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.br;
					traj = 4;
					break;
				}
			case 2:
				if (current.u != null) {
					current.u.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.u;
					traj = 2;
					break;
				} else {
					current.b.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.b;
					traj = 5;
					break;
				}
			case 3:
				if (current.ur != null) {
					current.ur.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ur;
					traj = 3;
					break;
				} else if (current.ur == null && current.ul != null) {
					current.ul.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ul;
					traj = 1;
					break;
				} else if (current.ur == null && current.ul == null
						&& current.br != null) {
					current.br.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.br;
					traj = 4;
					break;
				} else {
					current.bl.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.bl;
					traj = 6;
					break;
				}
			case 4:
				if (current.br != null) {
					current.br.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.br;
					traj = 4;
					break;
				} else if (current.br == null && current.bl != null) {
					current.bl.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.bl;
					traj = 6;
					break;
				} else if (current.br == null && current.ur != null) {
					current.ur.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ur;
					traj = 3;
					break;
				} else {
					current.ul.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ul;
					traj = 1;
					break;
				}
			case 5:
				if (current.b != null) {
					current.b.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.b;
					traj = 5;
					break;
				} else {
					current.u.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.u;
					traj = 2;
					break;
				}
			case 6:
				if (current.bl != null) {
					current.bl.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.bl;
					traj = 6;
					break;
				} else if (current.bl == null && current.br != null) {
					current.br.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.br;
					traj = 4;
					break;
				} else if (current.bl == null && current.br == null
						&& current.ul != null) {
					current.ul.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ul;
					traj = 1;
					break;
				} else {
					current.ur.hexEnemy2List.add(this);
					current.removeEnemy2FromList(this);
					current = current.ur;
					traj = 3;
					break;
				}
			}
			if(this.enemy2state == Enemy2.State.WANDERING){
				if(rnd.nextInt(10) < 2){
					if(rnd.nextInt(2) == 1){
						this.changeTraj(-1);
					}else{
						this.changeTraj(1);
					}
				}
			}

	}

}
