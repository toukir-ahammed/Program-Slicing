package understand;

import java.util.ArrayList;
import java.util.List;

public class CFGNode {
	int nodeID;

	public CFGNode() {
	}

	public int hashCode() {
		// int prime = 31;
		int result = 1;
		result = 31 * result + nodeID;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CFGNode other = (CFGNode) obj;
		if (nodeID != other.nodeID)
			return false;
		return true;
	}

	int lineStart = -1;
	int lineEnd = -1;
	List<CFGNode> successors = new java.util.ArrayList<CFGNode>();
	List<CFGNode> predecessors = new java.util.ArrayList<CFGNode>();

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getLineStart() {
		return lineStart;
	}

	public void setLineStart(int lineStart) {
		this.lineStart = lineStart;
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(int lineEnd) {
		this.lineEnd = lineEnd;
	}

	public List<CFGNode> getSuccessors() {
		return successors;
	}

	public void addSucessors(List<CFGNode> nodes) {
		successors.addAll(nodes);
	}

	public void changeLines(int start, int end) {
		if (lineEnd <= start) {
			lineEnd = end;
		} else if (end <= lineStart) {
			lineStart = start;
		}
	}

	public List<Integer> getSucessorIds() {
		List<Integer> ids = new ArrayList<>();
		for (CFGNode node : successors)
			ids.add(Integer.valueOf(node.nodeID)); // nodeID changed to node.nodeID
		return ids;
	}

	public List<CFGNode> getPredecessors() {
		return predecessors;
	}
}
