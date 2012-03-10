package astar;

import java.util.ArrayList;
import java.util.Collections;


class SortedNodeList extends ArrayList<Node>{
	
    @Override
	public boolean add(Node node) {
		boolean temp = super.add(node);
		Collections.sort(this);
                return temp;
	}

}