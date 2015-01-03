package evoSim;

import java.util.HashMap;
import java.util.LinkedList;

public class HexMapPathTable {
	// first int is hex id, second int is shortest distance
	private HashMap<Integer, Integer> evenColumnPathHM = new HashMap<Integer, Integer>(); 
	private HashMap<Integer, Integer> oddColumnPathHM = new HashMap<Integer, Integer>();
	private HexMap map;

	HexMapPathTable(HexMap testMap) {

		HexMap expandedMap = new HexMap(testMap.hexcountx + 1,
				testMap.hexcounty+1); // expandedMap used for issues related to
									// counting lengths of oddColumn paths
		this.map = expandedMap;
		Dijikstra d = new Dijikstra(map);
		d.run(map.hexarray[0][0]);
		for (int i = 0; i < map.hexcountx; i++) {
			for (int j = 0; j < map.hexcounty; j++) {
				if (!(i == 0 && j == 0)) {
					int temp = d.getPath(map.hexarray[i][j]).size();
					evenColumnPathHM.put(map.hexarray[i][j].id, temp);
				}

			}
		}
		d.run(map.hexarray[1][0]);
		for (int i = 1; i < map.hexcountx; i++) {
			for (int j = 0; j < map.hexcounty; j++) {
				if (!(i == 1 && j == 0)) { // condition to avoid computing
											// distance to same hex
					int temp = d.getPath(map.hexarray[i][j]).size();
					oddColumnPathHM.put(map.hexarray[i][j].id, temp);
				}
			}
		}
	}

	public int getDistance(Hex start, Hex end) {
		int xDiff = Math.abs(end.xId - start.xId);
		int yDiff = Math.abs(end.yId - start.yId);
		int temp;
		if (xDiff == 0 && yDiff == 0) {
			return 0;
		}
		if (start.xId % 2 == 0) {
			if (xDiff % 2 == 1 && end.yId - start.yId < 0) {
				temp = evenColumnPathHM.get(map.hexarray[xDiff][yDiff - 1].id) - 1;
			} else {
				temp = evenColumnPathHM.get(map.hexarray[xDiff][yDiff].id) - 1;
			}
		} else {
			temp = oddColumnPathHM.get(map.hexarray[xDiff + 1][yDiff].id) - 1;
		}
		return temp;
	}

	public int getDistance(int startX, int startY, int endX, int endY) {
		int xDiff = Math.abs(endX - startX);
		int yDiff = Math.abs(endY - startY);
		int temp;
		if (xDiff == 0 && yDiff == 0) {
			return 0;
		}
		if (startX % 2 == 0) {
			if (xDiff % 2 == 1 && endY - startY < 0) {
				temp = evenColumnPathHM.get(map.hexarray[xDiff][yDiff - 1].id) - 1;
			} else {
				temp = evenColumnPathHM.get(map.hexarray[xDiff][yDiff].id) - 1;
			}
		} else {
			temp = oddColumnPathHM.get(map.hexarray[xDiff + 1][yDiff].id);
		}
		return temp;
	}
}
