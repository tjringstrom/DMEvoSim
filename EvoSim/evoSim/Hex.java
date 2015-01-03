package evoSim;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Hex extends Globals{
	//How should I compress many of these attributes to save space?
	int maxCNL = 6;
	private int MAX_DISTANCE = 10000;
	int id;
	int xId;
	int yId;
	double xCoor;
	double yCoor;
	int hashMapKey;
//	float accumThreat = 0f;
//	float trueThreat = 0f;
//	int enemy1Distance = 0;
//	int cheeseDistance = 0;
	boolean shortestPath = false;
	double localThreat = 0;
//	double trueTreat = 0;
//  Enemy1 hexEnemy1 = null;
//  Cheese hexCheese = null;
	int[] hexShapeX = new int[6];
	int[] hexShapeY = new int[6];
	MouseBrain mouseBrain;
	ArrayList<Enemy1> hexEnemy1List = new ArrayList<Enemy1>();
	ArrayList<Enemy2> hexEnemy2List = new ArrayList<Enemy2>();
	public Set<AbstractMap.SimpleEntry<Integer,Integer>> radiusSet; //corresponds to x/y coordinates in HexMap hexarray
	int maxTimeStep = 20;
	
	Hex u = null;
	Hex ur = null;
	Hex br = null;
	Hex b = null;
	Hex bl = null;
	Hex ul = null;
	
	int[][] radialNbrIDs = new int[maxTimeStep][6]; // [timestep][direction] index 0
	Hex[][] radialNbrHexes = new Hex[maxTimeStep][6];
	ArrayList<ArrayList<Integer>> circleNbrList = new ArrayList<ArrayList<Integer>>(maxCNL); // index 0
	
	public Hex getRadialNbr(int time, int dir){
		return radialNbrHexes[time][dir];
	}
	
	public int getRadialID(int time, int dir){
		return radialNbrIDs[time][dir];
	}
	void removeEnemy1FromList(Enemy1 enemy) {
		for (int i = 0; i < hexEnemy1List.size(); i++) {
			if (hexEnemy1List.get(i).equals(enemy)) {
				hexEnemy1List.remove(i);
			}
		}
	}
	void removeEnemy2FromList(Enemy2 enemy) {
		for (int i = 0; i < hexEnemy2List.size(); i++) {
			if (hexEnemy2List.get(i).equals(enemy)) {
				hexEnemy2List.remove(i);
			}
		}
	}

	Hex(double x, double y, int xIndex, int yIndex, int identification) {
		id = identification;
		xId = xIndex;
		yId = yIndex;
		xCoor = x;
		yCoor = y;
		double radius = super.radius;
//		enemy1Distance = MAX_DISTANCE; 
		for (int i = 0; i < 6; i++) {
			hexShapeX[i] = (int) (xCoor + (radius * Math.cos(Math.toRadians(i
					* 60.0))));
			hexShapeY[i] = (int) (yCoor + (radius * Math.sin(Math.toRadians(i
					* 60.0))));
		}
		

	}
	
	//gets neighbor from traj number
	Hex getNbr(int num){
		num = ((num - 1) % 6) + 1;
		switch(num){
		case 1:
			return this.ul;
		case 2:
			return this.u;
		case 3:
			return this.ur;
		case 4:
			return this.br;
		case 5:
			return this.b;
		case 6:
			return this.bl;
		}
		return null;
	}
	
	public void setCircleNbrList(){
		int a = 0;
		for (int i = 0; i < maxCNL; i++) {
			this.circleNbrList.add(i, this.getRiskGroupCircle(i));
			a++;
		}

	}
	
	
	ArrayList<Hex> getNbrList(){
		ArrayList<Hex> nbrList = new ArrayList<Hex>(6);
		
		if(ul != null){
			nbrList.add(ul);
		}
		if(u != null){
			nbrList.add(u);
		}
		if(ur != null){
			nbrList.add(ur);
		}
		if(br != null){
			nbrList.add(br);
		}
		if(b != null){
			nbrList.add(b);
		}
		if(bl != null){
			nbrList.add(bl);
		}
		
		return nbrList;
	}
	
	
	
	// this basically radiates outward in the 6 directions from the hex and records 
	// the ids of the neighbors which can be used in the sampling algorithms
	// caution: even though traj starts at 1 for ul, in the array, ul corresponds to index 0, bl = 5. 
	// and time step 1 is at index 0 for the first bracket [i]
	
	//for this.radialNbrIDs[i][0], the fist bracket starts at 0 and is the first hex outward, not home hex, ul = 0,
	public void setRadialNbrLists(){ 
		Hex ulCurrent = this;
		Hex uCurrent = this;
		Hex urCurrent = this;
		Hex brCurrent = this;
		Hex bCurrent = this;
		Hex blCurrent = this;
		for (int i = 0; i < 10; i++) {
			if(ulCurrent.ul != null){
				ulCurrent = ulCurrent.ul;
			}
			this.radialNbrIDs[i][0] = ulCurrent.id;
			this.radialNbrHexes[i][0] = ulCurrent;
			
			if(uCurrent.u != null){
				uCurrent = uCurrent.u;
			}
			this.radialNbrIDs[i][1] = uCurrent.id;
			this.radialNbrHexes[i][1] = uCurrent;
			
			if(urCurrent.ur != null){
				urCurrent = urCurrent.ur;
			}
			this.radialNbrIDs[i][2] = urCurrent.id;
			this.radialNbrHexes[i][2] = urCurrent;
			
			if(brCurrent.br != null){
				brCurrent = brCurrent.br;
			}
			this.radialNbrIDs[i][3] = brCurrent.id;
			this.radialNbrHexes[i][3] = brCurrent;
			
			if(bCurrent.b != null){
				bCurrent = bCurrent.b;
			}
			this.radialNbrIDs[i][4] = bCurrent.id;
			this.radialNbrHexes[i][4] = bCurrent;
			
			if(blCurrent.bl != null){
				blCurrent = blCurrent.bl;
			}
			this.radialNbrIDs[i][5] = blCurrent.id;
			this.radialNbrHexes[i][5] = blCurrent;
		}
	}
	
	//This obtains a sampling patch the resembles a diamond with one of the corners being the starting hex, and the size is determined by distance
	public ArrayList<Integer> getRiskGroupDiamond(int dir, int distance){
		ArrayList<Integer> list = new ArrayList<Integer>();
		int midNum = dir;
		int leftNum = this.getTraj(dir, 1);
		int rightNum = this.getTraj(dir, -1);
		
		Hex current;
		list.add(this.id);
		Queue<Hex> q = new LinkedList<Hex>();
		q.add(this.getNbr(dir));
		
		while (list.size() < distance*distance) { //must fix this.  Won't work for edges
			current = q.poll();
			if(current.getNbr(leftNum) != null && !list.contains(current.getNbr(leftNum).id)){
				q.add(current.getNbr(leftNum));
				list.add(current.getNbr(leftNum).id);
			}
			if(current.getNbr(midNum) != null && !list.contains(current.getNbr(midNum).id)){
				q.add(current.getNbr(midNum));
				list.add(current.getNbr(midNum).id);
			}
			if(current.getNbr(rightNum) != null && !list.contains(current.getNbr(rightNum).id)){
				q.add(current.getNbr(rightNum));
				list.add(current.getNbr(rightNum).id);
			}
		}
		return list;
	}
	
	//Also gets a the ids of a group of hexes but in the shape of a circle with the center being *this*.
	public ArrayList<Integer> getRiskGroupCircle(int distance){
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		Hex current;
		list.add(this.id);
		LinkedList<Hex> q1 = new LinkedList<Hex>();
		LinkedList<Hex> q2 = new LinkedList<Hex>();
		q1.add(this);

		for (int i = 0; i < distance; i++) {
			
			q2 = (LinkedList<Hex>)q1.clone();
			q1.clear();
			while(!q2.isEmpty()) {
				current = q2.poll();
				for (Hex h : current.getNbrList()) {
					if (!list.contains(h.id)) {
						list.add(h.id);
						q1.add(h);
					}
				}
			}
		}
		
		return list;
	}
	
	
	Hex directMove(int dir){
		switch (dir) {
		case 0: return this;
		case 1:
			if (this.ul != null) {
				return this.ul;
			} else if (this.ul == null && this.ur != null) {
				return this.ur;
			} else if (this.ul == null && this.ur == null
					&& this.bl != null) {
				return this.bl;
			} else {
				return this.br;
			}
		case 2:
			if (this.u != null) {
				return this.u;
			} else {
				return this.b;
			}
		case 3:
			if (this.ur != null) {
				return this.ur;
			} else if (this.ur == null && this.ul != null) {
				return this.ul;
			} else if (this.ur == null && this.ul == null
					&& this.br != null) {
				return this.br;
			} else {
				return this.bl;
			}
		case 4:
			if (this.br != null) {
				return this.br;
			} else if (this.br == null && this.bl != null) {
				return this.bl;
			} else if (this.br == null && this.ur != null) {
				return this.ur;
			} else {
				return this.ul;
			}
		case 5:
			if (this.b != null) {
				return this.b;
			} else {
				return this.u;
			}
		case 6:
			if (this.bl != null) {
				return this.bl;
			} else if (this.bl == null && this.br != null) {
				return this.br;
			} else if (this.bl == null && this.br == null
					&& this.ul != null) {
				return this.ul;
			} else {
				return this.ur;
			}
		}
		return this;
	}
	
	int getTraj(int initial, int turnNumber) { // used for creating Cheese Vector with values at proper indexes.

		initial += turnNumber;
		if (initial > 6) {
			initial -= 6;
		}
		if (initial < 1) {
			initial += 6;
		}
		return initial;
	}
	
}

