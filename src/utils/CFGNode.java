package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CFGNode {
	int nodeID;
	HashMap<Integer, String> kindMap;

	public CFGNode() {
		mapKind();

	}

	private void mapKind() {
		kindMap = new HashMap<>();
		kindMap.put(0, "do-while");
		kindMap.put(1, "end-do-while");

		kindMap.put(5, "if");
		kindMap.put(6, "elsif");
		kindMap.put(7, "else");
		kindMap.put(8, "end-if");
		kindMap.put(9, "loop");
		kindMap.put(10, "while-for");
		kindMap.put(11, "while");
		kindMap.put(12, "for");
		kindMap.put(15, "end-loop");
		kindMap.put(30, "return");
		kindMap.put(32, "break");
		kindMap.put(33, "continue");
		kindMap.put(35, "passive");
		kindMap.put(36, "passive-implicit");
		kindMap.put(37, "java-block-begin");
		kindMap.put(38, "end-java-block");

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

	private int kind = -1;

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

	public int getKind() {
		return kind;
	}
	public String getKindString() {
		return kindMap.get(kind);
	}

	public void setKind(int kind) {
		this.kind = kind;
	}
}
