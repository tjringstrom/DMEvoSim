package evoSim;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	TreeNode parent;
	TreeNode left = null;
	TreeNode mid = null;
	TreeNode right = null;
	List<TreeNode> children = new ArrayList<TreeNode>();
	Hex hex;
	int directionFromParent;
	TreeChildPos position;
	boolean vistited = false;
	
	public TreeNode(TreeNode p, Hex h, int direction, TreeChildPos pos){
		parent = p;
		hex = h;
		directionFromParent = direction;
		position = pos;
	}
}
