package evoSim;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.la4j.matrix.sparse.CRSMatrix;
import net.sf.javaml.clustering.mcl.*;

public class SparseMatGen2 {
	HashMap<Integer, CRSMatrix> matrixHM;
	HashMap<Integer, MouseBrain> nextMoveHM; // key is id+k, int[] is the id+k of mid/left/right moves

	SparseMatGen2(HexMap map, Enemy enemy) {
		int size = map.hexcountx * map.hexcounty * 7;
		matrixHM = new HashMap<Integer, CRSMatrix>(size * 2);
		nextMoveHM = new HashMap<Integer, MouseBrain>(size * 2);
		//body of HexMap
//		for (int i = 1; i < map.hexcountx-1; i++) {
//			for (int j = 1; j < map.hexcounty-1; j++) {
//				for (int k = 1; k < 7; k++) { //possibility of using k = 0 for no movement?
//					CRSMatrix matrix = new CRSMatrix(size, size);
//					Hex current = map.hexarray[i][j];
//					
//					int currentIndex = (current.id) + k;
//					int prevIndex = this.getPrevMove(current, k).id;
//					int counterClockTraj = this.changeDirection(k, -1);
//					int clockwiseTraj = this.changeDirection(k, 1);
//					
//					matrix.set(prevIndex + k, currentIndex, enemy.mainTrajProb);
//					matrix.set(prevIndex + counterClockTraj, currentIndex, enemy.rightTrajProb);
//					matrix.set(prevIndex + clockwiseTraj, currentIndex, enemy.leftTrajProb);
//					
//					matrixHM.put(current.id, matrix);
//				}	
//			}
//		}
		for (int i = 0; i < map.hexcountx; i++) {
			for (int j = 0; j < map.hexcounty; j++) {
				for (int k = 0; k < 7; k++){
					ArrayList<Map.Entry<Integer, Double>> moveIndex = this.getPrevMoveIndex(map.hexarray[i][j], k);
				}
			}
		}
		//top edge of HexMap except corners
		//this might not work because there are exceptions at xIndex = 1 and yIndex = 1
		for (int i = 1; i < map.hexcountx-1; i++) {
			for (int k = 0; k < 7; k++) {
				
				if(i % 2 == 0){
					if(k >= 1 && k <= 3){
						
					}
				}
				
				if(i % 2 == 1){
					
				}
				
			}
			
		}
		
		for (int i = 0; i < 2; i++) {
			for (int j = 1; j < map.hexcountx-1; j++) {
				for (int k = 0; k < 7; k++) {
					CRSMatrix matrix = new CRSMatrix(size, size);
					Hex current = map.hexarray[i][j];
					
					if(true){
						
					}
				}
			}
			
		}
	}
	

	public HashMap<Integer, CRSMatrix> getHashMap() {
		return this.matrixHM;
	}
	
	public HashMap<Integer, MouseBrain> getNextMoveHM() {
		return this.nextMoveHM;
	}

	private int changeDirection(int direction, int turnNumber) {
		if (direction == 0) {
			return 0;
		}
		direction += turnNumber;
		if (direction > 6) {
			direction -= 6;
		} else if (direction < 1) {
			direction += 6;
		}
		return direction;
	}
	
	private Hex getNextMove(Hex current, int traj) {
		switch (traj) {
		case 0:
			return current;
		case 1:
			if (current.ul != null) {
				return current.ul;
			} else if (current.ul == null && current.ur != null) {
				return current.ur;
			} else if (current.ul == null && current.ur == null
					&& current.bl != null) {
				return current.bl;
			} else {
				return current.br;

			}
		case 2:
			if (current.u != null) {
				return current.u;
			} else {
				return current.b;
			}
		case 3:
			if (current.ur != null) {
				return current.ur;
			} else if (current.ur == null && current.ul != null) {
				return current.ul;
			} else if (current.ur == null && current.ul == null
					&& current.br != null) {
				return current.br;
			} else {
				return current.bl;
			}
		case 4:
			if (current.br != null) {
				return current.br;
			} else if (current.br == null && current.bl != null) {
				return current.bl;
			} else if (current.br == null && current.ur != null) {
				return current.ur;
			} else {
				return current.ul;
			}
		case 5:
			if (current.b != null) {
				return current.b;
			} else {
				return current.u;
			}
		case 6:
			if (current.bl != null) {
				return current.bl;
			} else if (current.bl == null && current.br != null) {
				return current.br;
			} else if (current.bl == null && current.br == null
					&& current.ul != null) {
				return current.ul;
			} else {
				return current.ur;
			}
		}
		return null;
	}
	
	private ArrayList<Map.Entry<Integer, Double>> getPrevMoveIndex(Hex hex, int traj){
		//here i want to return a list of all of the hexes plus traj that could have been the predecessor of the passed Hex parameter + traj
		//the integer in the map.entry is the hex.id+traj and the Double is the corresponding probability of movement.
		ArrayList<Map.Entry<Integer, Double>> indexList = new ArrayList<Map.Entry<Integer, Double>>(6);
		ArrayList<Hex> nbrs = hex.getNbrList();
		Enemy1 enemy = new Enemy1(null, 0);
		
		//problem here, it is not guaranteed that the enemy travels in the direction you expect
		for (Hex neighbor : nbrs) {
			for (int i = 0; i < 7; i++) {
				enemy.current = neighbor;
				enemy.traj = i;
				enemy.directMove(this.changeDirection(enemy.traj, -1));
				if(enemy.current.equals(hex) && enemy.traj == traj){
					
				}
				enemy.current = neighbor;
				enemy.traj = i;
				enemy.directMove(enemy.traj);
			}
		}
		
		
		indexList.trimToSize();
		enemy.current = null;
		return indexList;
	}

	private Hex getPrevMove(Hex current, int traj) {
		switch (traj) {
		case 0:
			return current;
		case 1:
			if (current.br != null) {
				return current.br;
			} else {
				break;
			}
		case 2:
			if (current.b != null) {
				return current.b;
			} else {
				break;
			}
		case 3:
			if (current.bl != null) {
				return current.bl;
			} else {
				break;
			}
		case 4:
			if (current.ul != null) {
				return current.ul;
			} else {
				break;
			}
		case 5:
			if (current.u != null) {
				return current.u;
			} else {
				break;
			}
		case 6:
			if (current.ur != null) {
				return current.ur;
			} else {
				break;
			}
		}
		return null;
	}
}
