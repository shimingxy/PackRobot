package com.packrobot.firefly;

import com.hansky.apps.firefly.base.Node;

public class FireflyNode {
	String nodePath;
	Node node;
	public FireflyNode() {
		// TODO Auto-generated constructor stub
	}
	
	public FireflyNode(String nodePath, Node node) {
		super();
		this.nodePath = nodePath;
		this.node = node;
	}

	public String getNodePath() {
		return nodePath;
	}
	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}

}
