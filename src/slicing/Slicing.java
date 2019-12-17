package slicing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import utils.*;


public abstract class Slicing {
	public CFG cfg;
	public Collection<VariableUsage> variableUsages;
	public HashMap<CFGNode, HashSet<CFGNode>> influence;

	public Slicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		this.cfg = cfg;
		this.variableUsages = variableUsages;
		calculateControlDependence();
		System.out.println();
	}
	
	private void calcIfDependency(CFGNode parent, CFGNode sucessor) {
		
		if (sucessor.getKindString().equals("end-if")) {
			return;			
		}
		
		if (sucessor.getKindString().equals("passive")) {
			if (influence.get(parent) != null) {
				HashSet<CFGNode> tempSet = influence.get(parent);
				tempSet.add(sucessor);
				influence.get(parent).addAll(tempSet);
			} else {
				HashSet<CFGNode> tempSet = new HashSet<>();
				tempSet.add(sucessor);
				influence.put(parent, tempSet);
			}
		}
		
		for (CFGNode tempNode : sucessor.getSuccessors()) {
			calcIfDependency(parent, tempNode);
			
		}
		
	}
	
private void calcWhileDependency(CFGNode parent, CFGNode sucessor) {
		
		if (sucessor.getKindString().equals("end-loop")) {
			return;			
		}
		
		if (sucessor.getKindString().equals("passive")) {
			if (influence.get(parent) != null) {
				HashSet<CFGNode> tempSet = influence.get(parent);
				tempSet.add(sucessor);
				influence.get(parent).addAll(tempSet);
			} else {
				HashSet<CFGNode> tempSet = new HashSet<>();
				tempSet.add(sucessor);
				influence.put(parent, tempSet);
			}
		}
		
		for (CFGNode tempNode : sucessor.getSuccessors()) {
			calcWhileDependency(parent, tempNode);
			
		}
		
	}

	private void calculateControlDependence() {
		
		influence = new HashMap<>();
		// TODO Auto-generated method stub
		for (CFGNode cfgNode : cfg.getAllNodes()) {
			if (cfgNode.getKindString().equals("if")) {
				for (CFGNode successor : cfgNode.getSuccessors()) {
					if (successor.getKindString().equals("passive") || successor.getKindString().equals("else") ) {
						calcIfDependency(cfgNode, successor);
					}
				}
									
							
			} else if (cfgNode.getKindString().equals("elsif")) {
				for (CFGNode successor : cfgNode.getSuccessors()) {
					if (successor.getKindString().equals("passive")) {
						calcIfDependency(cfgNode, successor);
					}
				}
				
			} else if (cfgNode.getKindString().equals("while-for")) {
				for (CFGNode successor : cfgNode.getSuccessors()) {
					if (successor.getKindString().equals("passive")) {
						calcWhileDependency(cfgNode, successor);
					}
				}
				
			}
		}
		
	}
	
	public CFGNode getInfluencedBy(CFGNode cfgNode) {
		for (CFGNode node : influence.keySet()) {
			HashSet<CFGNode> set = influence.get(node);
			if (set.contains(cfgNode)) {
				return node;
			}
		}
		return null;
	}

	protected HashSet<Integer> getUseNodes(int lineStart, int lineEnd, Collection<VariableUsage> variableUsages) {
		if (lineEnd < lineStart) {
			lineEnd = lineStart;
		}
		HashSet<Integer> nodeIds = new HashSet<Integer>();
		for (int lineNum = lineStart; lineNum <= lineEnd; lineNum++) {
			for (VariableUsage vu : variableUsages) {
				if (vu.isUseLine(lineNum)) {
					nodeIds.add(Integer.valueOf(vu.getNodeID()));
				}
			}
		}
		return nodeIds;
	}
	
	protected HashSet<Integer> getRelevantNodes(int lineStart, int lineEnd, String name, Collection<VariableUsage> variableUsages) {
		if (lineEnd < lineStart) {
			lineEnd = lineStart;
		}
		HashSet<Integer> nodeIds = new HashSet<Integer>();
		for (int lineNum = lineStart; lineNum <= lineEnd; lineNum++) {
			for (VariableUsage vu : variableUsages) {
				if (vu.isUseLine(lineNum) || vu.isDefineLine(lineNum) && vu.name.equals(name)) {
					nodeIds.add(Integer.valueOf(vu.getNodeID()));
				}
			}
		}
		return nodeIds;
	}

	protected HashSet<Integer> getDefOrSetNodes(int lineStart, int lineEnd, Collection<VariableUsage> variableUsages) {
		if (lineEnd < lineStart) {
			lineEnd = lineStart;
		}
		HashSet<Integer> nodeIds = new HashSet<Integer>();
		for (int lineNum = lineStart; lineNum <= lineEnd; lineNum++) {
			for (VariableUsage vu : variableUsages) {
				if ((vu.isDefineLine(lineNum)) || (vu.isSetLine(lineNum))) {
					nodeIds.add(Integer.valueOf(vu.getNodeID()));
				}
			}
		}
		return nodeIds;
	}

	public abstract HashSet<CFGNode> getSlicedNode(int line, HashSet<String> variableset);
}
