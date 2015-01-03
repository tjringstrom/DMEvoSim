package evoSim;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

import org.uncommons.maths.random.GaussianGenerator;

public class GameStateManager {
	int cutoff = 4; // must be even
	int currentGen = 0;
	int maxNumOfGens = 10;
	HeatMap hm;
	public Stack<GameState> states;
	int generationSize = 10;
	Random rnd = new Random();
	org.uncommons.maths.random.GaussianGenerator gRand = new GaussianGenerator(0,0.25,rnd);

	// first list = generations, second = logs
	public ArrayList<ArrayList<MouseLogEntry>> mouseLog;

	public ArrayList<MouseBrain> currentGeneration = new ArrayList<MouseBrain>();

	public MouseBrain getNextMouseBrain() {
		MouseBrain mb;
		if (!currentGeneration.isEmpty()) {
			mb = currentGeneration.get(0);
			currentGeneration.remove(0);
		} else {
			currentGeneration = this.getNewGeneration();
			this.currentGen++;
			this.mouseLog.add(new ArrayList<MouseLogEntry>());
			mb = currentGeneration.get(0);
			currentGeneration.remove(0);
		}
		return mb;
	}

	public boolean checkIfEndOfSim() {
		if (this.currentGeneration.size() > 0) {
			return false;
		} else if (this.currentGen < this.maxNumOfGens) {
			return false;
		} else {
			return true;
		}
	}

	public void printSimParameters(double numOfGen, int maxH){
		String text = "Sim_Parameters";
		PrintWriter printout;
		try {
			printout = new PrintWriter(text);
			printout.println("numOfGenerations: " + numOfGen + ", maxHealth: " + maxH + ", numOfGenerations: " + numOfGen);
			printout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printData(int generation) {

		String text = "Generation" + this.currentGen + ".txt";
		PrintWriter printout;
		try {
			printout = new PrintWriter(text);
			for (MouseLogEntry mle : this.mouseLog.get(this.currentGen)) {
				printout.println(mle.endHealth + ", " + mle.survived + ", "
						+ mle.cheeseDirBias + ", " + mle.cheeseDistBias + ", "
						+ mle.timeStepBias + ", " + mle.generation);
			}
			printout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void updateMouseLog(MouseLogEntry mle) {
		mouseLog.get(currentGen).add(mle);
	}

	public ArrayList<MouseBrain> getNewGeneration() {
		// gets best mice and
		ArrayList<MouseBrain> nextGen = new ArrayList<MouseBrain>();
		ArrayList<MouseLogEntry> gen = mouseLog.get(currentGen);
		Collections.sort(gen);
		Collections.reverse(gen);

		for (int i = 0; i < cutoff; i = i +1) {
			MouseLogEntry mle1 = gen.get(i);
//			MouseLogEntry mle2 = gen.get(i + 1);

			mle1.survived = true;
//			mle2.survived = true;

			double cDirBias = 0;
			double cDistBias = 0;
			double timeStepBias = 0;

			// recombination
			for (int j = 0; j < (this.generationSize / (this.cutoff)); j++) {
				cDirBias = mle1.cheeseDirBias;
				cDistBias = mle1.cheeseDistBias;
				timeStepBias = mle1.timeStepBias;
//				if (rnd.nextDouble() > 0.5) {
//					cDirBias = mle1.cheeseDirBias;
//				} else {
//					cDirBias = mle2.cheeseDirBias;
//				}
//				if (rnd.nextDouble() > 0.5) {
//					cDistBias = mle1.cheeseDistBias;
//				} else {
//					cDistBias = mle2.cheeseDistBias;
//				}
//				if (rnd.nextDouble() > 0.5) {
//					timeStepBias = mle1.timeStepBias;
//				} else {
//					timeStepBias = mle2.timeStepBias;
//				}

				// mutation
//				cDistBias +=  this.gRand.nextValue(); // +/- 2
				cDistBias += 0; // +/- 2
				if (cDistBias < 1) {
					cDistBias = 1;
				}
//				cDirBias += this.gRand.nextValue();
				cDirBias += 0;
				if (cDirBias < 1.0) {
					cDirBias = 1;
				}
//				timeStepBias += 0;
				 timeStepBias +=  this.gRand.nextValue();
				if (timeStepBias < 1) {
					timeStepBias = 1;
				}
				System.out.println("cDirBias " + cDirBias);
				System.out.println("cDistBias " + cDistBias);
				System.out.println("timeStepBias " + timeStepBias);
				MouseBrain newMB = new MouseBrain(cDirBias, cDistBias,
						timeStepBias, currentGen + 1);
				nextGen.add(newMB);
				this.printData(currentGen);
			}

		}
		return nextGen;

	}

	private void setFirstGeneration() {
		ArrayList<MouseBrain> list = new ArrayList<MouseBrain>();
		for (int i = 0; i < generationSize; i++) {
//			 double cheeseDistBias = 10 * rnd.nextDouble()+1;
			double cheeseDistBias = 1; // 1.2
//			 double cheeseDirBias = 1 * rnd.nextDouble()+1;
//			double cheeseDirBias = ((i*i)/4.0) + 1;
			double cheeseDirBias = 14;
			 double timeStepBias = 3.9;
//			double timeStepBias = (i*2) + 1;
			MouseBrain mb = new MouseBrain(cheeseDirBias, cheeseDistBias,
					timeStepBias, 0);
			mb.generation = 0;
			list.add(mb);
		}
		currentGeneration = list;
	}

	public GameStateManager() {
		mouseLog = new ArrayList<ArrayList<MouseLogEntry>>(20);
		mouseLog.add(new ArrayList<MouseLogEntry>(20));
		this.setFirstGeneration();
		states = new Stack<GameState>();
		states.push(new MenuState(this));
	}

	public void tick() {
		states.peek().tick();
	}

	public void draw(Graphics g) {
		states.peek().draw(g);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(int k) {
		states.peek().keyPressed(k);
		;
	}

	public void keyReleased(int k) {
		states.peek().keyReleased(k);
		;
	}

	// public ArrayList<Double> getMutatedMice(int generation){
	//
	//
	// }

}
