package evoSim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import net.sf.javaml.clustering.mcl.SparseMatrix;

import org.la4j.matrix.sparse.CRSMatrix;
import org.uncommons.maths.random.GaussianGenerator;

public class SimState extends GameState {
	int timeStepsRemaining = 2500;
	boolean step = false;
	boolean stepOn = false;
	int timeVis = 1; //controls what timeStep is visulaized on the map
	int dist = 6;
	int numOfEnemies1 = 2;
	int numOfEnemies2 = 0;
	Random rnd2 = new Random();
	Random rnd = new Random();
	org.uncommons.maths.random.GaussianGenerator gRand = new GaussianGenerator(  // Normal Distribution used for enemy scent detection
			0.0, 1.7, rnd);
//	org.uncommons.maths.random.GaussianGenerator gRand2 = new GaussianGenerator(
//			0.0, 1.0, rnd);
	double i = gRand.nextValue();
	HexMap testMap = new HexMap(hexcountx, hexcounty);
	Cheese cheese = new Cheese(
			testMap.hexarray[testMap.hexcountx - 1][testMap.hexcounty - 1]);
	ArrayList<Enemy1> enemy1List;
	ArrayList<Enemy2> enemy2List;
	Dijikstra shortestPathHunter = new Dijikstra(testMap);
	ArrayList<Dijikstra> shortestPathListE2 = new ArrayList<Dijikstra>();
	HexMapPathTable pathTable = new HexMapPathTable(testMap);
	HeatMap heatMap = new HeatMap(testMap);
	MouseBrain mouseBrain;
	int adist;
	

	public SimState(GameStateManager gsm, MouseBrain mb) {
		// main functions of this constructor
		// 1. adds enemies
		// 2. generates heat map based on enemies
		// 3. initializes settings for mouse brain
		super(gsm); // calls the parent constructor i.e. GameState
		
		this.enemy1List = new ArrayList<Enemy1>();
		this.enemy2List = new ArrayList<Enemy2>();
		for (int i = 0; i < numOfEnemies1; i++) {
			enemy1List.add(new Enemy1(testMap.hexarray[rnd2
					.nextInt(hexcountx - 1)][rnd2.nextInt(hexcounty - 1)], rnd2
					.nextInt(6) + 1));
		} // adds type 1 enemies randomly
		for (int i = 0; i < numOfEnemies2; i++) {
			enemy2List.add(new Enemy2(testMap.hexarray[rnd2
					.nextInt(hexcountx - 1)][rnd2.nextInt(hexcounty - 1)], rnd2
					.nextInt(6) + 1, testMap));
		} // adds type 2 enemies randomly
		heatMap.generate(dist, enemy1List);
		super.gsm.hm = heatMap;
		System.out.println("enemy traj: " + enemy1List.get(0).traj);
//		mouseBrain = new MouseBrain(testMap.hexarray[0][0], heatMap, cDirBias, cDistBias, tsBias);
		mouseBrain = mb;
		mouseBrain.current = testMap.hexarray[0][0];
		mouseBrain.heatMap = heatMap;
		
		gsm.printSimParameters(timeStepsRemaining, mouseBrain.maxHealth);
		
//		System.out.println(heatMap.finalHeatMap.toString());
//		System.out.println("post-collapse sum: " + heatMap.finalHeatMap.sum());
//		System.out.println(super.finalSparseMat.finalMatrix.toString());

	}

	public void init() {

	}

	public void tick() {
		
		if(timeStepsRemaining == 0){
			super.gsm.states.remove(0); // ??? what does this do?
		}
		//  (1) hunter moves first, (2) then enemy, (3) update numbers, (4) generate heatMap for new enemy locations
		
		if ((mouseBrain.hunterMoved == true && this.stepOn == false) || (this.mouseBrain.hunterMoved == true && this.stepOn == true && this.step == true)) {
			mouseBrain.hunterMoved = false;
//			for (Enemy2 e2 : enemy2List){
//				e2.enemy2Dijikstra.run(e2.current);
//			}
			
//			mouseBrain.moveHunter();
			mouseBrain.moveHunterAuto(mouseBrain.makeDecision(dist, mouseBrain.current, cheese.current, pathTable, adist)); // (1) hunter moves
			//  Weiwen's comment: perhaps efficient coding can play a role in mouse's movement (above).
			double t = System.nanoTime();
			this.shortestPathHunter.run(this.mouseBrain.current);
			adist = (this.shortestPathHunter.getPath(cheese.current).size() - 1);
			System.out.println("DijikstraTime: " + (System.nanoTime() - t));
			System.out.println("DISTANCE TO CHEESE: " + adist);
			this.moveAndUpdateEnemy1();    // (2) then enemy moves
			this.moveAndUpdateEnemy2(); //not currently in game
			this.cheeseCheck();
			this.dealDamage();   // (3) update numbers
			decreaseHunger();
//			if(this.timeStepsRemaining % 6 == 0){
//				mouseBrain.health--;				
//			}
			System.out.println(this.pathTable.getDistance(mouseBrain.current, this.enemy1List.get(0).current)); // (4) generate heatmap
			heatMap.generate(dist, enemy1List);
			step = false;
			timeStepsRemaining--;
			System.out.println("DISTANCE TO CHEESE: " + this.shortestPathHunter.getPath(cheese.current).size());
		}
		
		if (this.mouseBrain.health == 0 || this.timeStepsRemaining == 0) {
			MouseLogEntry mle = mouseBrain.getMouseLogEntry();
			super.gsm.updateMouseLog(mle);
			gsm.states.pop();
			if(super.gsm.checkIfEndOfSim() == false){
				gsm.states.push(new SimState(super.gsm, super.gsm.getNextMouseBrain()));				
			} else {
				super.gsm.printData(super.gsm.currentGen);				
			}
		}
	}

	private void cheeseCheck() {
		if (mouseBrain.current.equals(cheese.current)) { // i.e. if mouse and cheese are at the same location
			mouseBrain.gainHealth(4);                    // ... then mouse gains health
			if (this.mouseBrain.current
					.equals(testMap.hexarray[testMap.hexcountx - 1][testMap.hexcounty - 1])) {
				this.cheese.current = testMap.hexarray[0][0];
				return;
			} else if (cheese.current.equals(testMap.hexarray[0][0])) {
				cheese.current = testMap.hexarray[testMap.hexcountx - 1][testMap.hexcounty - 1]; // to check: this moves cheese to the other end of the board?
				return;
			}
		}
	}

	// only move enemies when hunter moved
	private void moveAndUpdateEnemy1() {
		//  Weiwen's comment: perhaps efficient coding can play a role in enemy's movement. 
		for(Enemy1 enemy : enemy1List){
			if(enemy.fullness == 0){
				enemy.fastPace++;
				//extra movement every 6 moves
				if(enemy.fastPace % 6 == 0 && enemy.enemy1state.equals(Enemy1.State.HUNTING)){
					enemy.moveEnemy1(mouseBrain.current);
				}
				if(enemy.fullness == 0){ // ??? why do you need to have this if condition?
					enemy.moveEnemy1(mouseBrain.current);
				}
				
				LinkedList<Hex> enemyToHunterPath = shortestPathHunter.getPath(enemy.current);
				
				//turn hunting on with Gaussian probability
				if(Math.abs(gRand.nextValue()) >enemyToHunterPath.size()){
					enemy.enemy1state = Enemy1.State.HUNTING;
				}
				
				//turn hunting off with fixed probability (10%)
				if(enemy.enemy1state == Enemy1.State.HUNTING && Math.abs(rnd.nextInt(10)) < 1){
					enemy.enemy1state = Enemy1.State.WANDERING;
				}
			}
			else if (enemy.fullness > 0){
				this.decreaseHunger();
			}
			
		}
	}

	private void moveAndUpdateEnemy2() {
		// finds for each enemy2 the distance to the nearest other enemy2
		for(Enemy2 enemy : enemy2List){
			enemy.shortestPathToHunter = shortestPathHunter.getPath(enemy.current);
			
			enemy.shortestPathToE2 = null;
			for(Enemy2 otherEnemy : enemy2List){
				if(!enemy.equals(otherEnemy)){
					if(enemy.shortestPathToE2 == null){
						enemy.shortestPathToE2 = enemy.enemy2Dijikstra.getPath(otherEnemy.current);
						enemy.closestE2 = otherEnemy;
					}
					else if (enemy.shortestPathToE2.size() > enemy.enemy2Dijikstra.getPath(otherEnemy.current).size()){
						enemy.shortestPathToE2 = enemy.enemy2Dijikstra.getPath(otherEnemy.current);
						enemy.closestE2 = otherEnemy;
					}
				}
			}	
		}
		
		// move enemy2 and then update state
		for(Enemy2 enemy : enemy2List){
			enemy.fastPace++;
			if(enemy.fastPace % 3 == 0 && enemy.enemy2state.equals(Enemy2.State.HUNTING)){
				enemy.moveEnemy2(mouseBrain.current);
			}
			if(enemy.fullness == 0){
				enemy.moveEnemy2(mouseBrain.current);
			}
			
			if(enemy.shortestPathToE2.size() < 10 && 
					enemy.closestE2.shortestPathToHunter.size() < 10 &&
					enemy.shortestPathToHunter.size() < 10){
				enemy.enemy2state = Enemy2.State.HUNTING;
				enemy.closestE2.enemy2state = Enemy2.State.HUNTING;
			}
		}

	}

	// deal damage to hunter if on same space as enemies, kills hunter, adds new
	// hunter randomly to map
	public void dealDamage() {
		if (!mouseBrain.current.hexEnemy1List.isEmpty()) {
			for (int i = 0; i < mouseBrain.current.hexEnemy1List.size(); i++) {
				if (mouseBrain.current.hexEnemy1List.get(i).fullness == 0) {
					mouseBrain.hit(9);
					mouseBrain.current.hexEnemy1List.get(i).fullness = 10;
				}
			}
		}
		if (!mouseBrain.current.hexEnemy2List.isEmpty()) {
			for (int i = 0; i < mouseBrain.current.hexEnemy2List.size(); i++) {
				if (mouseBrain.current.hexEnemy2List.get(i).fullness == 0) {
					mouseBrain.hit(35);
				}
			}
		}
	}

	public void decreaseHunger() {
		for (int i = 0; i < this.enemy1List.size(); i++) {
			if (enemy1List.get(i).fullness > 0) {
				enemy1List.get(i).traj = 0;
				enemy1List.get(i).fullness--;
				enemy1List.get(i).enemy1state = Enemy1.State.WANDERING;
				// enemy1List.get(i).Enemy1state = Enemy1.State.HUNTING;
				if (enemy1List.get(i).fullness == 0) {
					enemy1List.get(i).randomTraj();
				}
			}
		}
	}

	public void setAllEnemiesWandering() {
		for (int i = 0; i < this.enemy1List.size(); i++) {
			this.enemy1List.get(i).enemy1state = Enemy1.State.WANDERING;
		}
		for (int i = 0; i < this.enemy2List.size(); i++) {
			this.enemy2List.get(i).enemy2state = Enemy2.State.WANDERING;
		}
	}

	public void setAllEnemiesHunting() {
		for (int i = 0; i < this.enemy1List.size(); i++) {
			this.enemy1List.get(i).enemy1state = Enemy1.State.HUNTING;
			this.enemy1List.get(i).traj = rnd2.nextInt(6) + 1;
		}
		for (int i = 0; i < this.enemy2List.size(); i++) {
			this.enemy2List.get(i).enemy2state = Enemy2.State.HUNTING;
			this.enemy2List.get(i).traj = rnd2.nextInt(6) + 1;
		}
	}

	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g2.setStroke(new BasicStroke(1));
		g.setColor(Color.RED);

		// draw hexagons
		for (int i = 0; i < testMap.hexcountx; i++) {
			for (int j = 0; j < testMap.hexcounty; j++) {
				g.setColor(new Color((int)(255 - (heatMap.timeStepMap[timeVis][(int)testMap.hexToIDMat.get(i, j)]*2) * 255 /2
						)));
				g.fillPolygon(testMap.hexarray[i][j].hexShapeX,
						testMap.hexarray[i][j].hexShapeY, 6);
				g.drawPolygon(testMap.hexarray[i][j].hexShapeX,
						testMap.hexarray[i][j].hexShapeY, 6);
				g.setColor(Color.red);
				g.drawString(Integer.toString(testMap.hexarray[i][j].id),
						(int)(testMap.hexarray[i][j].xCoor-this.radius/2.9), (int)(testMap.hexarray[i][j].yCoor-this.radius/2.9));
			}
		}

		g.setColor(Color.YELLOW);
		// draw shortest path connecting Enemy2s
		
//			LinkedList<Hex> list = this.shortestPathHunter.getPath(cheese.current);
//					for (int j = 0; j < list.size(); j++) {
//						g.drawOval(
//								(int) (list.get(j).xCoor - radius / 2),
//								(int) (list.get(j).yCoor - radius / 2),
//								(int) radius, (int) radius);
//					}
				
			
		
		
//		if (this.enemy2List != null) {
//			for (int i = 0; i < this.enemy2List.size(); i++) {
//				if (this.enemy2List.get(i).shortestPathToE2 != null) {
//					for (int j = 0; j < this.enemy2List.get(i).shortestPathToE2
//							.size(); j++) {
//						g.drawOval(
//								(int) (this.enemy2List.get(i).shortestPathToE2
//										.get(j).xCoor - radius / 2),
//								(int) (this.enemy2List.get(i).shortestPathToE2
//										.get(j).yCoor - radius / 2),
//								(int) radius, (int) radius);
//					}
//				}
//			}
//		}

		// draw objects
		g.drawImage(mouseBrain.imgHunter,
				(int) (mouseBrain.current.xCoor - radius / 1.8),
				(int) (mouseBrain.current.yCoor - radius / 1.8),
				(int) (radius * 1.25), (int) (radius * 1.25), null);
		g.drawImage(cheese.cheeseImg,
				(int) (cheese.current.xCoor - radius / 1.8),
				(int) (cheese.current.yCoor - radius / 1.8),
				(int) (radius * 1.25), (int) (radius * 1.25), null);
		int s = this.enemy1List.size();
		for (int i = 0; i < s; i++) {
			g.drawImage(enemy1List.get(i).stateImg,
					(int) (enemy1List.get(i).current.xCoor - radius / 1.8),
					(int) (enemy1List.get(i).current.yCoor - radius / 1.8),
					(int) (radius * 1.25), (int) (radius * 1.25), null);
		}
		int m = this.enemy2List.size();
		for (int i = 0; i < m; i++) {
			g.drawImage(enemy2List.get(i).stateImg,
					(int) (enemy2List.get(i).current.xCoor - radius / 1.8),
					(int) (enemy2List.get(i).current.yCoor - radius / 1.8),
					(int) (radius * 1.25), (int) (radius * 1.25), null);
		}

		// draw health bar
		int x = (int) testMap.hexarray[this.hexcountx - 1][2].xCoor + 2 * (int)super.radius;
		int y = (int) testMap.hexarray[this.hexcountx - 1][2].yCoor;
		int width = 10;
		int height = 100;
		g2.setStroke(new BasicStroke(2));
		g.drawRect(x, y, width, height);
		g.setColor(Color.black);
		g.drawLine(x + width / 2, y + height, x + width / 2, y + height
				- height * mouseBrain.health / mouseBrain.maxHealth);
		g.drawString("Health: " + mouseBrain.health + "/" + mouseBrain.maxHealth , x - 15, y - 10);
		
		//display genes and biasing vectors
		int val = (int)this.testMap.hexarray[this.hexcountx-1][this.hexcounty-1].yCoor + 2* (int) super.radius;
		g.drawString("Cheese Direction Bias: " + mouseBrain.cheeseAlpha + " : " + mouseBrain.getCheeseVect(this.cheese.current, mouseBrain.current, adist), 20, val + 14);
		g.drawString("Cheese Distance Bias: " + mouseBrain.cheeseDistBias + " : " + mouseBrain.getDistBiasVec(this.cheese.current, mouseBrain.current, pathTable).toString(), 20, val + 2*14);
		g.drawString("Time Step Bias: " + mouseBrain.timeStepBias + " : " + mouseBrain.getTimeStepBiasVec().toString(), 20, val + 3*14);
		g.drawString("Moves Remaining: " + this.timeStepsRemaining, 20, val + 4*14);
		g.drawString("Generation of Mouse: " + mouseBrain.generation, 20, val + 5*14);
		g.drawString("Size of Mouse Log: " + super.gsm.mouseLog.size(), 20, val + 6*14); //should correspond to number of generations
		g.drawString("Size of Current Gen List in Mouse Log: " + super.gsm.mouseLog.get(super.gsm.currentGen).size(), 20, val + 7*14); //mouse number of current generation
		g.drawString("Distance From Cheese: " + pathTable.getDistance(mouseBrain.current, cheese.current), 20, val + 8*14);
		g.drawString("Distance From Cheese (dijikstra): " + adist, 20, val + 9*14);
		g.drawString("Enemy 1-1 Fullness: " + this.enemy1List.get(0).fullness, 20, val + 10*14);
		g.drawString("Enemy 1-2 Fullness: " + this.enemy1List.get(1).fullness, 20, val + 11*14);
		
		String text = "Next Mice (cDir gene values): ";
		for (MouseBrain mb : gsm.currentGeneration) {
			text = text + mb.cheeseAlpha + ", ";
		}
		g.drawString(text, 20, val + 12*14);
		
		text = "Next Mice (timeStepBias gene values): ";
		for (MouseBrain mb : gsm.currentGeneration) {
			text = text + mb.timeStepBias + ", ";
		}
		g.drawString(text, 20, val + 14*14);
		
		String text2 = "Winners From Previous Gen (cDir gene values): ";
		if(gsm.currentGen == 0){
			text2 += "N/A";
		} else {
			for (MouseLogEntry mle : gsm.mouseLog.get(gsm.currentGen - 1)) {
				if (mle.survived == true) {
					text2 = text2 + mle.cheeseDirBias + ", ";				
				}
			}			
		}
		g.drawString(text2, 20, val + 13*14);

	}

	public void keyPressed(int k) {
		if (KeyEvent.VK_X == k) {
			this.step = true;
		}
		if (KeyEvent.VK_Z == k) {
			if(this.stepOn == true){
				this.stepOn = false;
			} else{
				this.stepOn = true;
			}
			
		}
		if (KeyEvent.VK_P == k) {
			this.setAllEnemiesWandering();
		}
		if (KeyEvent.VK_L == k) {
			timeVis++;
		}
		if (KeyEvent.VK_K == k) {
			timeVis--;
		}
		if (KeyEvent.VK_O == k) {
			this.setAllEnemiesHunting();
		}
//		if (KeyEvent.VK_N == k) {
//			gsm.states.remove(0);
//			gsm.states.push(new SimState(gsm, new MouseBrain(2,2,2)));
//		}
		if (KeyEvent.VK_Q == k) {
			mouseBrain.q = true;
			this.mouseBrain.hunterMoved = true;

		}
		if (KeyEvent.VK_W == k) {
			mouseBrain.w = true;
			this.mouseBrain.hunterMoved = true;

		}
		if (KeyEvent.VK_E == k) {
			mouseBrain.e = true;
			this.mouseBrain.hunterMoved = true;

		}
		if (KeyEvent.VK_D == k) {
			mouseBrain.d = true;
			this.mouseBrain.hunterMoved = true;

		}
		if (KeyEvent.VK_S == k) {
			mouseBrain.s = true;
			this.mouseBrain.hunterMoved = true;

		}
		if (KeyEvent.VK_A == k) {
			mouseBrain.a = true;
			this.mouseBrain.hunterMoved = true;

		}
	}

	public void keyReleased(int k) {
//		if (KeyEvent.VK_Q == k) {
//			mouseBrain.q = false;
//			this.mouseBrain.hunterMoved = false;
//		}
//		if (KeyEvent.VK_W == k) {
//			mouseBrain.w = false;
//			this.mouseBrain.hunterMoved = false;
//		}
//		if (KeyEvent.VK_E == k) {
//			mouseBrain.e = false;
//			this.mouseBrain.hunterMoved = false;
//		}
//		if (KeyEvent.VK_D == k) {
//			mouseBrain.d = false;
//			this.mouseBrain.hunterMoved = false;
//		}
//		if (KeyEvent.VK_S == k) {
//			mouseBrain.s = false;
//			this.mouseBrain.hunterMoved = false;
//		}
//		if (KeyEvent.VK_A == k) {
//			mouseBrain.a = false;
//			this.mouseBrain.hunterMoved = false;
//		}
	}

}
