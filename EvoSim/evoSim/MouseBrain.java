package evoSim;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import org.la4j.vector.dense.BasicVector;

public class MouseBrain extends Globals{
	
	int maxHealth = 2000;
	int generation = 0;
	int currentCurrency = 0;
	double cheeseAlpha; //gene for biasing in direction of cheese
	double cheeseDistBias; //gene
	double timeStepBias; //gene
	double timeStepBiasLoc; //gene
	private ArrayList<double[][]> timeStepHeatMaps; //still need to do this!
	private HashMap<Integer, Set<AbstractMap.SimpleEntry<Integer,Integer>>> locationSet; //corresponds to hexID : x/y location in heatMap //probably not needed
	public HeatMap heatMap;
	int maxTimeStep = 10;
	BasicVector ulRiskSamp;
	BasicVector uRiskSamp;
	BasicVector urRiskSamp;
	BasicVector brRiskSamp;
	BasicVector bRiskSamp;
	BasicVector blRiskSamp;
	
	Hex current;
	int health = this.maxHealth;
	BufferedImage imgHunter = null;{ //FIX??
		
	try {
		imgHunter = ImageIO.read(new File("mouse000.jpg"));
	} catch(Exception ex){}
	}
	
	//movement
	boolean hunterMoved = true;
	boolean q = false;
	boolean w = false;
	boolean e = false;
	boolean a = false;
	boolean s = false;
	boolean d = false;
	
	MouseBrain(double cDirBias, double cDistBias, double tsBias, int gen){
		System.out.println("mousebrain");
		this.cheeseAlpha = cDirBias;
		this.cheeseDistBias = cDistBias;
		this.timeStepBias = tsBias;
		this.generation = gen;
	}
	
	//remember map edge boundaries 
	//want loop to control which heatMap time step I am sampling from so we don't have
	//to load different maps into memory over and over
	
	private BasicVector multTimeStepBias(BasicVector riskSample, BasicVector tsbv){
		for (int i = 0; i < riskSample.length(); i++) {
			riskSample.set(i, Math.pow(riskSample.get(i), tsbv.get(i)));
		}
		return riskSample;
	}
	
	public int makeDecision(int currency, Hex current, Hex cheeseCur, HexMapPathTable hmpt, int d){
		this.getRiskSamples(currency, current);
		double[] list = new double[6];
		int smallest = 0;
		BasicVector tsBiasVec = this.getTimeStepBiasVec();
		BasicVector cheeseVec = this.getCheeseVect(cheeseCur, current, d);
		BasicVector distBiasVec = this.getDistBiasVec(cheeseCur, current, hmpt);
		System.out.println("tsBIASVECTOR: " + tsBiasVec.toString());
		System.out.println("cheeseVec: " + cheeseVec.toString());
		System.out.println("distBiasVector: " + distBiasVec.toString());
		
//		probably don't need this any more, effects risk samps equally as is multiplying by a const.		
//		list[0] = ulRiskSamp.hadamardProduct(tsBiasVec).sum() + 1;
//		list[1] = uRiskSamp.hadamardProduct(tsBiasVec).sum() + 1;
//		list[2] = urRiskSamp.hadamardProduct(tsBiasVec).sum() + 1;
//		list[3] = brRiskSamp.hadamardProduct(tsBiasVec).sum() + 1;
//		list[4] = bRiskSamp.hadamardProduct(tsBiasVec).sum() + 1;
//		list[5] = blRiskSamp.hadamardProduct(tsBiasVec).sum() + 1;
		
		list[0] = this.multTimeStepBias(ulRiskSamp, tsBiasVec).sum() + 1;
		list[1] = this.multTimeStepBias(uRiskSamp, tsBiasVec).sum() + 1;
		list[2] = this.multTimeStepBias(urRiskSamp, tsBiasVec).sum() + 1;
		list[3] = this.multTimeStepBias(brRiskSamp, tsBiasVec).sum() + 1;
		list[4] = this.multTimeStepBias(bRiskSamp, tsBiasVec).sum() + 1;
		list[5] = this.multTimeStepBias(blRiskSamp, tsBiasVec).sum() + 1;
		
		System.out.println("alert");
		
		BasicVector riskSampleVec = new BasicVector(list);
		System.out.println("risk vector with tsBias: " + riskSampleVec.toString());
		BasicVector output2 = (BasicVector) riskSampleVec.hadamardProduct(cheeseVec);
		System.out.println("cheeseBiasVec: " + output2.toString());
		
//		BasicVector output2 = (BasicVector) output.hadamardProduct(distBiasVec);
		System.out.println("final risk vector (after dist Bias): " + output2.toString());
		
		list = output2.toArray();
		
		//get the best direction
		for (int i = 0; i < list.length; i++) {
			System.out.print(list[i] + " ");
		}
		
		for (int i = 0; i < list.length; i++) {
			if (list[i] < list[smallest]) {
				smallest = i;
			}
		}
		System.out.println("Returning: " + smallest);
		return smallest;
	}
	
	
	//makes the vector that is used to bias the movement decision in the direction of the cheese.
	public BasicVector getCheeseVect(Hex cheeseCurrent, Hex current, int dist){
		//will need to modify to accommodate null move.
//		int dist = hmpt.getDistance(current, cheeseCurrent);

		
		BasicVector v = new BasicVector(6);
		int cheeseDir = this.getCheeseTraj(cheeseCurrent, current);
		
		if(cheeseDir == 0){
			double[] arr = {1,1,1,1,1,1};
			return new BasicVector(arr);
		}
		if(dist == 0){
			dist = super.hexcountx + super.hexcounty;
		}
		for (int i = 0; i <= v.length()/2; i++) {
			double val = ((1.0/dist) * (this.cheeseDistBias-1) + 1) * ((cheeseAlpha-1.0)/(v.length()/2))*i + 1;
			v.set(this.getTraj(cheeseDir, i)-1, val);
			v.set(this.getTraj(cheeseDir, 0-i)-1, val);
		}
		System.out.println("DISTANCE: " + dist);
		System.out.println("dist bias: " + (cheeseDistBias/dist)+1);
		System.out.println("cheeseBias before: " + v.toString());
		
		return v;
	}
	
	public BasicVector getTimeStepBiasVec(){
		BasicVector tsBiasVec = new BasicVector(this.currentCurrency);
		
		for (int i = tsBiasVec.length()-1; i >= 0; i--) {
			double val = ((timeStepBias-1)/(tsBiasVec.length()))*i + 1;
			tsBiasVec.set(tsBiasVec.length()-1-i, val);
		}
		
		return tsBiasVec;
	}
	
	public BasicVector getDistBiasVec(Hex cheeseCurrent, Hex current, HexMapPathTable hmpt){
		int dist = hmpt.getDistance(current, cheeseCurrent);
		int cheeseDir = this.getCheeseTraj(cheeseCurrent, current);
		double distBias = cheeseDistBias;
		BasicVector distBiasVec = new BasicVector(6);
		
		for (int i = 0; i <= distBiasVec.length()/2; i++) {
			double val2 = ((distBias-1)/(distBiasVec.length()/2))*i*dist + 1;
			double val = ((distBias-1)/(distBiasVec.length()/2))*i + 1;
			distBiasVec.set(this.getTraj(cheeseDir, i)-1, val2);
			distBiasVec.set(this.getTraj(cheeseDir, 0-i)-1, val2);
		}
		
		return distBiasVec;
	}
	
	
	public int getCheeseTraj(Hex cheeseCurrent, Hex current){
		int traj = 0;
		double angle = Math.atan2((cheeseCurrent.xCoor - current.xCoor),
				(cheeseCurrent.yCoor - current.yCoor));
		angle = Math.toDegrees(angle);
		if((cheeseCurrent.xCoor - current.xCoor) == 0 && (cheeseCurrent.yCoor - current.yCoor) == 0){
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
		return traj;
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
	
	public MouseLogEntry getMouseLogEntry(){
		MouseLogEntry mle = new MouseLogEntry();
		mle.generation = generation;
		mle.endHealth = health;
		mle.cheeseDirBias = this.cheeseAlpha;
		mle.cheeseDistBias = this.cheeseDistBias;
		mle.timeStepBias = this.timeStepBias;
		return mle;
	}
	
	
	public void getRiskSamples(int currency, Hex current){
		// problems: if the mouse doesn't sample from an area surrounding a hex t time steps away, 
		// it might miss crucial highly probable hexes that the enemy could be in that are adjacent.  This will make a big
		// difference because it is possible to be in line with an enemy's trajectory but be an odd number of hexes away
		// and still miss the risk when getRiskSample is called.
		this.currentCurrency = currency;
		
		ulRiskSamp = new BasicVector(currency);
		uRiskSamp = new BasicVector(currency);
		urRiskSamp = new BasicVector(currency);
		brRiskSamp = new BasicVector(currency);
		bRiskSamp = new BasicVector(currency);
		blRiskSamp = new BasicVector(currency);
		
		boolean searchul = true;
		boolean searchu = true;
		boolean searchur = true;
		boolean searchbl = true;
		boolean searchb = true;
		boolean searchbr = true;
		
		for (int i = 0; i < currency; i++) { //index i controls time
			int radius = 2;
			System.out.println(i);
			System.out.println("a: " + current.radialNbrIDs[i][3]);
			System.out.println(current.circleNbrList.size());
			System.out.println("b: " + heatMap.timeStepMap[i].length);
			System.out.println("mouse current: " + current.id);
			System.out.println("nbr UL radial: " + current.radialNbrIDs[0][0] + " " + current.radialNbrIDs[1][0] + " " + current.radialNbrIDs[2][0]);
			System.out.println("Current ID: " + current.id);
			if(i == 0){
				System.out.println("asdf: " + getGroupAveRisk(current.getRadialNbr(i, 3), radius, i));
			}
			//it is i+1 because the heatmap starts at t=1, that is, there is not 0 time step
			if (searchul) {
				ulRiskSamp.set(i, getGroupAveRisk(current.getRadialNbr(i, 0), radius, i+1));
			} else {
				ulRiskSamp.set(i, 999);
			}
			if (searchu) {
				uRiskSamp.set(i, getGroupAveRisk(current.getRadialNbr(i, 1), radius, i+1));
			} else {
				uRiskSamp.set(i, 999);
			}
			if (searchur) {
				urRiskSamp.set(i, getGroupAveRisk(current.getRadialNbr(i, 2), radius, i+1));
			} else {
				urRiskSamp.set(i, 999);
			}
			if (searchbr) {
				brRiskSamp.set(i, getGroupAveRisk(current.getRadialNbr(i, 3), radius, i+1));
			} else {
				brRiskSamp.set(i, 999);
			}
			if (searchb) {
				bRiskSamp.set(i, getGroupAveRisk(current.getRadialNbr(i, 4), radius, i+1));
			} else {
				bRiskSamp.set(i, 999);
			}
			if (searchbl) {
				blRiskSamp.set(i, getGroupAveRisk(current.getRadialNbr(i, 5), radius, i+1));
			} else {
				blRiskSamp.set(i, 999);
			}
			
			if (i == 0) {
				double avgRisk = (blRiskSamp.get(i) + bRiskSamp.get(i) + brRiskSamp.get(i) + ulRiskSamp.get(i) + urRiskSamp.get(i) + uRiskSamp.get(i))/6.0;
				// Question to Tom: Is there a significant chance that Risk samples in all directions will be exactly equal?
				// Because if so, everything will be equal to the average, and the mouse will search in all three directions.
				if (blRiskSamp.get(i) > avgRisk) {
					searchbl = false;
				}
				if (bRiskSamp.get(i) > avgRisk) {
					searchb = false;
				}
				if (brRiskSamp.get(i) > avgRisk) {
					searchbr = false;
				}
				if (ulRiskSamp.get(i) > avgRisk) {
					searchul = false;
				}
				if (uRiskSamp.get(i) > avgRisk) {
					searchu = false;
				}
				if (urRiskSamp.get(i) > avgRisk) {
					searchur = false;
				}
			}

			
//			ulRiskSamp.set(i, heatMap.timeStepMap[i+1][current.radialNbrIDs[i][0]/7]*100);
//			uRiskSamp.set(i, heatMap.timeStepMap[i+1][current.radialNbrIDs[i][1]/7]*100);
//			urRiskSamp.set(i, heatMap.timeStepMap[i+1][current.radialNbrIDs[i][2]/7]*100);
//			brRiskSamp.set(i, heatMap.timeStepMap[i+1][current.radialNbrIDs[i][3]/7]*100);
//			bRiskSamp.set(i, heatMap.timeStepMap[i+1][current.radialNbrIDs[i][4]/7]*100);
//			blRiskSamp.set(i, heatMap.timeStepMap[i+1][current.radialNbrIDs[i][5]/7]*100);
		}
		System.out.println("ul risk: " + ulRiskSamp.toString());
		System.out.println("u risk: " + uRiskSamp.toString());
		System.out.println("ur risk: " + urRiskSamp.toString());
		System.out.println("br risk: " + brRiskSamp.toString());
		System.out.println("b risk: " + bRiskSamp.toString());
		System.out.println("bl risk: " + blRiskSamp.toString());
		
	}
	
	private double getGroupAveRisk(Hex h, int radius, int timeStep){
		double out = 0;
//		System.out.println("size of CNL: " + h.circleNbrList.size());
//		System.out.println("Sample Hex ID: " + h.id);
//		System.out.println("Circle Nbr Set: " + h.circleNbrList.get(radius));
		for (Integer id : h.circleNbrList.get(radius)) {
			out += heatMap.timeStepMap[timeStep][id/7]*100; //rNIDs[timestep][direction]
		}
//		out /= h.circleNbrList.size();
		out /= 1 + 6 * radius;
		System.out.println("out: " + out);
		return out;
	}
	
	public double getHexGroupRisk(int[] group){
		double total = 0;
		for (int h : group) {
			total += h;
		}
		total /= group.length;
		return total;
	}
	
	public void hit(int damage){
		health -= damage;
		if(health<0){
			health = 0;
		}
	}
	
	public void gainHealth(int amount){
		this.health += amount;
		if(this.health > maxHealth){
			this.health = maxHealth;
		}
	}
	
	public void moveHunterAuto(int move){ //controls the hunters ability to move automatically wrt probability samples.  moveHunter() just executes the move
		move++;
		System.out.println("Mouse at: " + this.current.id + "  MOUSE DECISION: " + move);
		switch (move) {
		case 0:
			this.hunterMoved = true; //no move
		case 1:
			if(this.current.ul != null){
				this.current = this.current.ul;
			}
			this.hunterMoved = true;
			break;
		case 2:
			if(this.current.u != null){
				this.current = this.current.u;
			}
			this.hunterMoved = true;
			break;
		case 3:
			if(this.current.ur != null){
				this.current = this.current.ur;
			}
			this.hunterMoved = true;
			break;
		case 4:
			if(this.current.br != null){
				this.current = this.current.br;
			}
			this.hunterMoved = true;
			break;
		case 5:
			if(this.current.b != null){
				this.current = this.current.b;
			}
			this.hunterMoved = true;
			break;
		case 6:
			if(this.current.bl != null){
				this.current = this.current.bl;
			}
			this.hunterMoved = true;
			break;

		default:
			this.hunterMoved = false;
			break;
		}
	}
	
	public void moveHunter (){//probably should have used switch 
		if(q == true && this.current.ul != null){
			clearControlsExcept('q');
			this.current = this.current.ul;
			q = false;
			this.hunterMoved = false;
		}
		else if(w == true && this.current.u != null){
			clearControlsExcept('w');
			this.current = this.current.u;
			w = false;
			this.hunterMoved = false;
		}
		else if(e == true && this.current.ur != null){
			clearControlsExcept('e');
			this.current = this.current.ur;
			e = false;
			this.hunterMoved = false;
		}
		else if(d == true && this.current.br != null){
			clearControlsExcept('d');
			this.current = this.current.br;
			d = false;
			this.hunterMoved = false;
		}
		else if(s == true && this.current.b != null){
			clearControlsExcept('s');
			this.current = this.current.b;
			s = false;
			this.hunterMoved = false;
		}
		else if(a == true && this.current.bl != null){
			clearControlsExcept('a');
			this.current = this.current.bl;
			a = false;
			this.hunterMoved = false;
		}
	}
	
	public void clearControlsExcept(char direction){
 		q = false;
		w = false;
		e = false;
		d = false;
		s = false;
		a = false;
		if(direction == 'q'){
			q = true;
		}
		if(direction == 'w'){
			w = true;
		}
		if(direction == 'e'){
			e = true;
		}
		if(direction == 'd'){
			d = true;
		}
		if(direction == 's'){
			s = true;
		}
		if(direction == 'a'){
			a = true;
		}
	}

	
	void moveHunter(int key) {
		if (key == KeyEvent.VK_D && current.br != null) {
			current.mouseBrain = null;
			current.br.mouseBrain = this;
			current = current.br;
		}
		if (key == KeyEvent.VK_S && current.b != null) {
			current.mouseBrain = null;
			current.b.mouseBrain = this;
			current = current.b;
		}
		if (key == KeyEvent.VK_A && current.bl != null) {
			current.mouseBrain = null;
			current.bl.mouseBrain = this;
			current = current.bl;
		}
		if (key == KeyEvent.VK_Q && current.ul != null) {
			current.mouseBrain = null;
			current.ul.mouseBrain = this;
			current = current.ul;
		}
		if (key == KeyEvent.VK_W && current.u != null) {
			current.mouseBrain = null;
			current.u.mouseBrain = this;
			current = current.u;
		}
		if (key == KeyEvent.VK_E && current.ur != null) {
			current.mouseBrain = null;
			current.ur.mouseBrain = this;
			current = current.ur;
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// stuff below could be deleted
	
	
	
	public Hex getNextMove(int lookaheadDistance, Hex initialHex){
		double totalUL = 0;
		double totalU = 0;
		double totalUR = 0;
		double totalBR = 0;
		double totalB = 0;
		double totalBL = 0;
		Hex currentUL = initialHex;
		Hex currentU = initialHex;
		Hex currentUR = initialHex;
		Hex currentBR = initialHex;
		Hex currentB = initialHex;
		Hex currentBL = initialHex;
		double[][] currentTimeHeatMap;
		for (int time = 0; time < lookaheadDistance; time++) {
			currentTimeHeatMap = timeStepHeatMaps.get(time);
			currentUL = currentUL.directMove(1);
			currentU = currentU.directMove(2);
			currentUR = currentUR.directMove(3);
			currentBR = currentBR.directMove(4);
			currentB = currentB.directMove(5);
			currentBL = currentBL.directMove(6);
			totalUL += this.getRadiusTotal(currentUL, currentTimeHeatMap);
			totalU += this.getRadiusTotal(currentU, currentTimeHeatMap);
			totalUR += this.getRadiusTotal(currentUR, currentTimeHeatMap);
			totalBR += this.getRadiusTotal(currentBR, currentTimeHeatMap);
			totalB += this.getRadiusTotal(currentB, currentTimeHeatMap);
			totalBL += this.getRadiusTotal(currentBL, currentTimeHeatMap);
		}
		ArrayList<Double> list = new ArrayList<Double>(6);
		list.add(totalUL);
		list.add(totalU);
		list.add(totalUR);
		list.add(totalBR);
		list.add(totalB);
		list.add(totalBL);
		
		Collections.sort(list);
		
		return null;
	}
	
	double getRadiusTotal(Hex mainHex, double[][] heatMap){
		double total = 0;
		for (AbstractMap.SimpleEntry<Integer,Integer> intPair : locationSet.get(mainHex.id)) {
			total += heatMap[intPair.getKey()][intPair.getValue()];
		}
		return total;
	}
	
}
