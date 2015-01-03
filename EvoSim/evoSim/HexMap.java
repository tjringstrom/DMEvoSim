package evoSim;

import java.util.List;
import java.util.ArrayList;

import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;

public class HexMap extends Globals{
	Hex[][] hexarray;
	public List<Hex> hexList = new ArrayList<Hex>(); //list by id??
	public List<Edge> edgeList = new ArrayList<Edge>();
	public Matrix hexToIDMat;
	int hexcountx;
	int hexcounty;
	double angle = 60.00;
	double radius = super.radius;
	double vertspace = super.radius * Math.sin(Math.toRadians(60.0));
	
	
	public HexMap(int xDimension, int yDimension) {
		hexcountx = xDimension;
		hexcounty = yDimension;
		hexToIDMat = new org.la4j.matrix.dense.Basic2DMatrix(xDimension, yDimension);
		this.hexarray = new Hex[hexcountx][hexcounty];
		int k = 0;
		for (int i = 0; i < hexcountx; i++) {
			for (int j = 0; j < hexcounty; j++) {
				if (i % 2 == 0) {
					//only difference between if and else is specifying x and y coordinates
					hexarray[i][j] = new Hex(
							(this.radius + (1.5 * radius) * i),
							(this.vertspace + (2 * vertspace) * j),i,j,k*7); //multiplying by 7 for each direction, useful for making HashMap with id.
					this.hexList.add(hexarray[i][j]);
					hexToIDMat.set(i, j, k);
					k++;
				} else {
					hexarray[i][j] = new Hex(
							(this.radius + (1.5 * radius) * i),
							(2 * this.vertspace + (2 * vertspace) * j),i,j,k*7); 
					this.hexList.add(hexarray[i][j]);
					hexToIDMat.set(i, j, k);
					k++;
				}
			}
		}

		// links nodes together
		for (int i = 0; i < hexcountx; i++) {
			for (int j = 0; j < hexcounty; j++) {
				// connect hexes
				// upper left connection i == odd
				if (i - 1 >= 0 && i % 2 == 1) {
					hexarray[i][j].ul = hexarray[i - 1][j];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i - 1][j],0.0,1));
				}
				// upper left connection i == even
				if (i - 1 >= 0 && i % 2 == 0 && j - 1 >= 0) {
					hexarray[i][j].ul = hexarray[i - 1][j - 1];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i - 1][j - 1],0.0,1));
				}
				// upper connection
				if (j - 1 >= 0) {
					hexarray[i][j].u = hexarray[i][j - 1];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i][j - 1],0.0,1));
				}
				// upper right connection i == odd
				if (i + 1 <= hexcountx - 1 && i % 2 == 1) {
					hexarray[i][j].ur = hexarray[i + 1][j];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i + 1][j],0.0,1));
				}
				// upper right connection i == even
				if (i + 1 <= hexcountx - 1 && i % 2 == 0 && j - 1 >= 0) {
					hexarray[i][j].ur = hexarray[i + 1][j - 1];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i + 1][j - 1],0.0,1));
				}
				// bottom right connection i == odd
				if (i + 1 <= hexcountx - 1 && j + 1 <= hexcounty - 1
						&& i % 2 == 1) {
					hexarray[i][j].br = hexarray[i + 1][j + 1];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i + 1][j + 1],0.0,1));
				}
				// bottom right connection i == even
				if (i + 1 <= hexcountx - 1 && j <= hexcounty - 1 && i % 2 == 0) {
					hexarray[i][j].br = hexarray[i + 1][j];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i + 1][j],0.0,1));
				}
				// bottom connection
				if (j + 1 <= hexcounty - 1) {
					hexarray[i][j].b = hexarray[i][j + 1];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i][j + 1],0.0,1));
				}
				// bottom left connection i == odd
				if (i - 1 >= 0 && j + 1 <= hexcounty - 1 && i % 2 == 1) {
					hexarray[i][j].bl = hexarray[i - 1][j + 1];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i - 1][j + 1],0.0,1));
				}
				// bottom left connection i == even
				if (i - 1 >= 0 && j <= hexcounty - 1 && i % 2 == 0) {
					hexarray[i][j].bl = hexarray[i - 1][j];
					this.edgeList.add(new Edge(this.hexarray[i][j],this.hexarray[i - 1][j],0.0,1));
				}
				
			}
		}
		
		for (Hex h : this.hexList) {
			h.setRadialNbrLists();
			h.setCircleNbrList();
		}
	}
	
	
}
