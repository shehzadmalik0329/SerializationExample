package com.java.binarysearchtree;

public class BinarySearchTree {
	
	Node root;
	
	public void add(int value) {
		root = addRecursive(root, value);
	}
	
	private Node addRecursive(Node current, int value) {
		if(current == null) {
			return new Node(value);
		}
		
		// left side
		if(value < current.value) {
			current.left = addRecursive(current.left,value);
		}
		//right side
		else if(value > current.value) {
			current.right = addRecursive(current.right,value);
		}
		else {
			return current;
		}
		
		return current;
	}

}
