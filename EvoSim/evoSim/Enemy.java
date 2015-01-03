package evoSim;

import java.util.Random;

public abstract class Enemy {
	Hex current;
	Random rnd = new Random();
	int traj;
	double mainTrajProb;
	double leftTrajProb;
	double rightTrajProb;
	
	
	public Enemy(){
		
	}
	
	void randomTraj(){
		traj = rnd.nextInt(6) + 1;
	}
	
	void directMove(int dir){
		switch (dir) {
		case 1:
			if (current.ul != null) {
				current = current.ul;
				traj = 1;
				break;
			} else if (current.ul == null && current.ur != null) {
				current = current.ur;
				traj = 3;
				break;
			} else if (current.ul == null && current.ur == null
					&& current.bl != null) {
				current = current.bl;
				traj = 6;
				break;
			} else {
				current = current.br;
				traj = 4;
				break;
			}
		case 2:
			if (current.u != null) {
				current = current.u;
				traj = 2;
				break;
			} else {
				current = current.b;
				traj = 5;
				break;
			}
		case 3:
			if (current.ur != null) {
				current = current.ur;
				traj = 3;
				break;
			} else if (current.ur == null && current.ul != null) {
				current = current.ul;
				traj = 1;
				break;
			} else if (current.ur == null && current.ul == null
					&& current.br != null) {
				current = current.br;
				traj = 4;
				break;
			} else {
				current = current.bl;
				traj = 6;
				break;
			}
		case 4:
			if (current.br != null) {
				current = current.br;
				traj = 4;
				break;
			} else if (current.br == null && current.bl != null) {
				current = current.bl;
				traj = 6;
				break;
			} else if (current.br == null && current.ur != null) {
				current = current.ur;
				traj = 3;
				break;
			} else {
				current = current.ul;
				traj = 1;
				break;
			}
		case 5:
			if (current.b != null) {
				current = current.b;
				traj = 5;
				break;
			} else {
				current = current.u;
				traj = 2;
				break;
			}
		case 6:
			if (current.bl != null) {
				current = current.bl;
				traj = 6;
				break;
			} else if (current.bl == null && current.br != null) {
				current = current.br;
				traj = 4;
				break;
			} else if (current.bl == null && current.br == null
					&& current.ul != null) {
				current = current.ul;
				traj = 1;
				break;
			} else {
				current = current.ur;
				traj = 3;
				break;
			}
		}
	}
	
	int getIdPlusTraj(){
		return (this.current.id + this.traj);
	}
}
