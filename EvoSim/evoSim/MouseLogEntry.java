package evoSim;

public class MouseLogEntry implements java.lang.Comparable<MouseLogEntry> {
	int generation;
	double healthAverage;
	int endHealth;
	int cheeseCount;
	double cheeseDirBias;
	double cheeseDistBias;
	double timeStepBias;
	MouseLogEntry parent;
	MouseLogEntry child;
	boolean survived;
	
	MouseLogEntry(){
		
	}
	
	MouseLogEntry(int generation, double healthAverage, int endHealth, int cheeseCount, double cDirBias, double cDistBias, double tsb, boolean survived){
		this.generation = generation;
		this.healthAverage = healthAverage;
		this.endHealth = endHealth;
		this.cheeseCount = cheeseCount;
		this.cheeseDirBias = cDirBias;
		this.cheeseDistBias = cDistBias;
		this.timeStepBias = tsb;
		this.survived = survived;
	}

	@Override
	public int compareTo(MouseLogEntry o) {
		return Integer.compare(this.endHealth, o.endHealth);
	}
	
	
	
	
}
