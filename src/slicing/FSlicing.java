package slicing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import utils.*;

public class FSlicing extends Slicing{

	public FSlicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		super(cfg, variableUsages);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashSet<CFGNode> getSlicedNode(int line, HashSet<String> variableSet) {
		// Assume single criterion node
				CFGNode criterionNode = cfg.getNodes(line).get(0);
				HashMap<CFGNode, HashSet<Integer>> R0C = new HashMap<>();

				for (String name : variableSet) {
					R0C.put(criterionNode, getRelevantNodes(criterionNode.getLineStart(), criterionNode.getLineEnd(),name, variableUsages));
				}
				
				// R0C.put(criterionNode, getDefOrSetNodes(criterionNode.getLineStart(), criterionNode.getLineEnd(), variableUsages));
				
				// Definitions are not considered		
				// R0C.get(criterionNode).addAll(getDefOrSetNodes(criterionNode.lineStart, criterionNode.lineEnd, variableUsages));

				List<CFGNode> toVisitNodes = criterionNode.getSuccessors();
				
				int iteration = 0;
				
				HashSet<CFGNode> sliceNodes = new HashSet<CFGNode>();
				sliceNodes.add(criterionNode);

				while (toVisitNodes.size() != 0) {
					if (iteration == 20) break;
					iteration++;
					List<CFGNode> potentialNodes = new ArrayList<CFGNode>();
					for (CFGNode node : toVisitNodes) {
						
						HashSet<Integer> DEF = getDefOrSetNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
						HashSet<Integer> REF = getUseNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
						
						HashSet<Integer> relevantAliveVariablesFromPredecessors = new HashSet<Integer>();
						for (CFGNode predecessor : node.getPredecessors()) {
							if (R0C.get(predecessor) != null) {
								relevantAliveVariablesFromPredecessors.addAll(R0C.get(predecessor));
							}
						}

						if (!Collections.disjoint(REF, relevantAliveVariablesFromPredecessors)) {

							if (R0C.get(node) == null) {
								R0C.put(node, DEF);
							} else {
								R0C.get(node).addAll(DEF);
							}
							
							sliceNodes.add(node);

						}
						
						if (R0C.get(node) == null) {
							// relevantAliveVariablesFromPredecessors.removeAll(REF);
							R0C.put(node, relevantAliveVariablesFromPredecessors);
						} else {
							// relevantAliveVariablesFromPredecessors.removeAll(REF);
							R0C.get(node).addAll(relevantAliveVariablesFromPredecessors);
						}
						
						potentialNodes.addAll(node.getSuccessors());
						

					}
					
					toVisitNodes.clear();
					toVisitNodes.addAll(potentialNodes);
				}
		return sliceNodes;
	}

}
