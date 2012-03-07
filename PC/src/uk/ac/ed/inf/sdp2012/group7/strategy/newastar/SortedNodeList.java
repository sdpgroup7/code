package astar;

import java.util.ArrayList;
import java.util.Collections;


class SortedNodeList {
	
	private ArrayList<Node> list = new ArrayList<Node>();
	
	public Node getFirst() {
		return list.get(0);
	}
	
	public void clear() {
		list.clear();
	}
	
	public void add(Node node) {
		list.add(node);
		Collections.sort(list);
	}
	
	public void remove(Node n) {
		list.remove(n);
	}
	
	public int size() {
		return list.size();
	}
	
	public boolean contains(Node n) {
		return list.contains(n);
	}
	
	public ArrayList<Node> getList(){
		return this.list;
	}
}