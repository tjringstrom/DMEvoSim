package evoSim;

import java.util.HashMap;
import java.util.Map;





import org.la4j.matrix.*;
import org.la4j.matrix.sparse.CRSMatrix;
import org.ujmp.core.calculation.Calculation.Ret;
//import net.sf.javaml.clustering.mcl.*;
//import org.ujmp.*;
//import org.ujmp.core.Matrix;
//import org.ujmp.core.MatrixFactory;
import org.ujmp.core.matrix.SparseMatrix;

public class SparseMatrixGenerator {
	// I might have made a mistake here in the constructor.  I'm putting entries into a matrix where the columns represent  
	// the x coordinates on the hexmap and the rows represent the y coordinates.  I think I have to make the matrix so that 
	// each row/col corresponds to a single hex plus a direction.

	
	//This class simply takes in a HexMap and an enemy
	
//	final CRSMatrix finalMatrix;
//	final SparseMatrix finalMat; // rows = t+1, cols = t
//	final SparseMatrix finalTransMat; // cols = t+1, rows = t
	final Matrix la4jMat; //rows = t, cols = t+1
	public SparseMatrix sm;

	public SparseMatrixGenerator(HexMap map, Enemy enemy) {
		HexMap blankMap = new HexMap(map.hexcountx, map.hexcounty); //creates a blank map with no hunters to avoid affecting the enemy movement state
		int size = (map.hexcountx * map.hexcounty * 7);
		sm = SparseMatrix.factory.zeros(size,size);
//		SparseMatrix mat = new SparseMatrix(size, size);
		
		double[][] dmat = new double[size][size];
//		dmat[0][1] = 0.7;
		
//		System.out.println(testmat.toStringDense());
//		mat.set(0, 1, 0.8);
//		System.out.println(mat.toStringDense());
	
		for (int i = 0; i < map.hexcountx; i++) {
			for (int j = 0; j < map.hexcounty; j++) {
				for (int k = 0; k < 7; k++) {
					Hex current = blankMap.hexarray[i][j];
//					System.out.println(current.id);
					
					// a new enemy is put in every possible hex and orientation on the map and then is asked to move to each possible
					// hex, the index of the new hex is then used for the index of the movement probability matrix and the
					// appropriate probability is assigned.  IMPORTANT: the matrix reutrned is organized so that the rows correspond to
					// t+1 and the columns correspond to t.
					enemy.current = current;
					enemy.traj = k;
					
					enemy.directMove(k);
					Hex tempMid = enemy.current;
					int midIndex = (tempMid.id + enemy.traj);
					
					enemy.current = current;
					
					enemy.directMove(this.changeDirection(k, -1));
					Hex tempLeft = enemy.current;
					int leftIndex = (tempLeft.id + enemy.traj);
					
					enemy.current = current;
					
					enemy.directMove(this.changeDirection(k, 1));
					Hex tempRight = enemy.current;
					int rightIndex = (tempRight.id + enemy.traj);
					

					
					
					dmat[midIndex][current.id + k] = dmat[midIndex][current.id + k] + enemy.mainTrajProb;
					dmat[leftIndex][current.id + k] = dmat[leftIndex][current.id + k] + enemy.leftTrajProb;
					dmat[rightIndex][current.id + k] = dmat[rightIndex][current.id + k] + enemy.rightTrajProb;
					
					sm.setAsDouble( (sm.getAsDouble(midIndex, current.id + k) + enemy.mainTrajProb) , midIndex, current.id + k);
					sm.setAsDouble( (sm.getAsDouble(leftIndex, current.id + k) + enemy.leftTrajProb) , leftIndex, current.id + k);
					sm.setAsDouble( (sm.getAsDouble(rightIndex, current.id + k) + enemy.rightTrajProb) , rightIndex, current.id + k);
					
					//first argument = t+1, second = t  might need to be transposed
//					mat.set(midIndex, current.id + k, mat.get(midIndex, current.id + k) + enemy.mainTrajProb);
//					mat.set(leftIndex, current.id + k, mat.get(leftIndex, current.id + k) + enemy.leftTrajProb);
//					mat.set(rightIndex, current.id + k, mat.get(rightIndex, current.id + k) + enemy.rightTrajProb);
					
//					matrix.set(midIndex, current.id + k, matrix.get(midIndex, current.id + k) + enemy.mainTrajProb);
//					matrix.set(leftIndex, current.id + k, matrix.get(leftIndex, current.id + k) + enemy.leftTrajProb);
//					matrix.set(rightIndex, current.id + k, matrix.get(rightIndex, current.id + k) + enemy.rightTrajProb);
				}
			}
		}
//		SparseMatrix mat = new SparseMatrix(dmat);
		sm = (SparseMatrix) sm.transpose();
		Matrix matrix = new org.la4j.matrix.dense.Basic2DMatrix(dmat);
		matrix = matrix.transpose();
		this.la4jMat = matrix;
//		finalMat = mat;

//		mat = mat.transpose(); // switching dimensions to be (t x t+1).  Still square matrix.
//		finalTransMat = mat;
//		sm.showGUI();
		System.out.println("sm size: " + sm.getSize(0));
		
		SparseMatrix rowSM = SparseMatrix.factory.zeros(sm.getSize(0), sm.getSize(0));
		System.out.println("sum vector?: " + sm.sum(Ret.LINK, 0, false));
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
}
