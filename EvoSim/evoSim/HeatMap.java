package evoSim;

import java.util.AbstractMap;
import java.util.ArrayList;




import java.util.Set;

import org.la4j.factory.Factory;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.ujmp.core.*;
import org.la4j.matrix.*;
import org.ujmp.core.doublematrix.SparseDoubleMatrix2D;
import org.ujmp.core.matrix.SparseMatrix;
import org.ujmp.core.util.MathUtil;

public class HeatMap {

	int maxTimeStep = 10;
	HexMap map;
	boolean b = true;
	
	public Matrix laMatrix;
	public SparseMatrix sm;
//	public SparseMatrix input; // rows = t, cols = t+1
//	public SparseMatrix output; // rows = t, cols = t+1
//	public SparseMatrix master; // rows = t+1, cols = t
//	public SparseMatrix masterT; // rows = t, cols = t+1
//	ArrayList<SparseMatrix> timeStepList = new ArrayList<SparseMatrix>(sizeOfList);
	ArrayList<Matrix> timeStepList = new ArrayList<Matrix>(maxTimeStep); //list of map matrices at each time step
	ArrayList<SparseMatrix> smTimeStepList = new ArrayList<SparseMatrix>(maxTimeStep); //list of map matrices at each time step
	double[][] timeStepMap; // first bracket is timestep, second bracket is id, index 0 
	SparseMatrix mat;
	
	SparseMatrix tempSm;
	
	public HeatMap(HexMap testMap){
		map = testMap;
		
		// create new enemy to pass to the sparse matrix generator
		Enemy1 enemy1 = new Enemy1(null,0);
		SparseMatrixGenerator smgE1 = new SparseMatrixGenerator(testMap, enemy1);
		laMatrix = smgE1.la4jMat;
		sm = smgE1.sm;
//		master = smgE1.finalMat;
//		masterT = smgE1.finalTransMat;
		this.setTimeStepList();
		timeStepMap = new double[maxTimeStep][map.hexcountx * map.hexcounty];

		
		
	}
	
	//this function makes list of the timeStep Matracies, the for loop computes a matrix power iteratively and saves all intermediate matrices in a list.
	public void setTimeStepList(){
		Matrix temp = new org.la4j.matrix.dense.Basic2DMatrix(laMatrix.rows(), laMatrix.columns());
		temp = laMatrix.copy();
		SparseMatrix tempSm2 = SparseMatrix.factory.zeros(sm.getSize(0),sm.getSize(0));
		tempSm2 = sm;
		
		timeStepList.add(laMatrix);
		smTimeStepList.add(sm);
		for (int i = 1; i < maxTimeStep; i++) {
			double t = System.nanoTime();
			
			temp = temp.multiply(laMatrix);
			timeStepList.add(temp);

			double a = System.nanoTime() - t;
			System.out.println("iteration: " + i);
			System.out.println("dense time: " + a);
			
			// ujmp
//			double c = System.nanoTime();
//			
//			tempSm2 = (SparseMatrix) tempSm2.mtimes(sm);
//			smTimeStepList.add(tempSm2);
//			System.out.println("is it sparse?: " + tempSm2.isSparse());
//
//			double v = System.nanoTime() - c;
//			System.out.println("ujmp time: " + v);
			
			if(i == 1){
//				tempSm2.showGUI();
			}
		}
		
		
//		System.out.println(timeStepList.get(7).toString());
		
	}
	
	public void generate(int currency, ArrayList<Enemy1> e1List){
		double tt = System.nanoTime();
		for (int n = 1; n <= currency; n++) { //problem here, note that n starts at 1
			int s = map.hexList.size();
			for (int i = 0; i < s; i++) {
				map.hexList.get(i).localThreat = 0;
			}
			
			Matrix input = new org.la4j.matrix.dense.Basic2DMatrix(laMatrix.rows(), laMatrix.columns());
			SparseMatrix smInput = SparseMatrix.factory.zeros(sm.getSize(0),sm.getSize(0));
					
			// setting the input matrix that will act as a seed
			int e1ListSize = e1List.size();
			for (int i = 0; i < e1ListSize; i++) {
				int e1IdPlusTraj = e1List.get(i).getIdPlusTraj();
				int e1IdPlusTrajSM = e1List.get(i).getIdPlusTraj();
				
//				smInput.setAsD
				input.set(e1IdPlusTraj, e1IdPlusTraj, 1.0);
				smInput.setAsDouble(1, e1IdPlusTrajSM, e1IdPlusTrajSM);
			}
			if(b == true){
//				smInput.showGUI();
				b = false;
			}
			//everything should be good until here
			
			
			
//		System.out.println("temp after power: " + temp.toString());
			
			
//		CRSMatrix multipliedMatrix = (CRSMatrix) master.power(currency-1); //for conventional way
//		System.out.println("input1: " + input.toString());
			double t = System.nanoTime();
			input = input.multiply(this.timeStepList.get(n-1)); //Where the power is taken
			double a = System.nanoTime() - t;
			System.out.println("time: " + a);
			//not sure if it should be A*M or M*A. Ask Maria.
//		System.out.println("hello");
//		input = temp.matrixTimes(input); // Does the multiply function convert the sparse matrix to a dense matrix first?
			
//		System.out.println("input2: " + input.toString());
			input = input.transpose();
//		System.out.println("input3: " + input.toString());
			
//		System.out.println(input.toString());
			
			
			
			//collapse all S_t-1 hexes into S_t hex  //inother words collapse all t into t+1
			int rowSize = input.rows();
			Double[] hexProbArray = new Double[rowSize];
			
//		System.out.println("hexProbArraylength: " + hexProbArray.length);
			for (int i = 0; i < rowSize; i++) {
				double num = input.getRow(i).sum(); //power-sum with power of 1
				hexProbArray[i] = num;
			}
			
			//collapse all trajectories
			int size = hexProbArray.length;
			ArrayList<Double> condensedProbList = new ArrayList<Double>(size/7);
			for (int i = 0; i < size; i = i+7) {
				
				double total = hexProbArray[i] + hexProbArray[i+1] + hexProbArray[i+2] + hexProbArray[i+3] + 
						hexProbArray[i+4] + hexProbArray[i+5] + hexProbArray[i+6];
				map.hexList.get(i/7).localThreat = total;
				timeStepMap[n][i/7] = total; 
				condensedProbList.add(total);
			}
			
		}
		
//		System.out.println(condensedProbList.toString());
		System.out.println("total loop time: " + (System.nanoTime() - tt));
	}
	
	
}
