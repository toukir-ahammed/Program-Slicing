package understand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MySlicing extends Slicing {

	public MySlicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		super(cfg, variableUsages);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashSet<CFGNode> getSlicedNode(int line) {

		// Assume single criterion node
		CFGNode criterionNode = cfg.getNodes(line).get(0);
		HashMap<CFGNode, HashSet<Integer>> R0C = new HashMap<>();

		R0C.put(criterionNode, getUseNodes(criterionNode.lineStart, criterionNode.lineEnd, variableUsages));
		
		// Definitions are not considered		
		// R0C.get(criterionNode).addAll(getDefOrSetNodes(criterionNode.lineStart, criterionNode.lineEnd, variableUsages));

		List<CFGNode> toVisitNodes = criterionNode.getPredecessors();
		
		int iteration = 0;
		
		HashSet<CFGNode> sliceNodes = new HashSet<CFGNode>();
		sliceNodes.add(criterionNode);

		while (toVisitNodes.size() != 0) {
			if (iteration == 20) break;
			iteration++;
			List<CFGNode> potentialNodes = new ArrayList<CFGNode>();
			for (CFGNode node : toVisitNodes) {
				
				HashSet<Integer> DEF = getDefOrSetNodes(node.lineStart, node.lineEnd, variableUsages);
				HashSet<Integer> REF = getUseNodes(node.lineStart, node.lineEnd, variableUsages);
				
				HashSet<Integer> relevantAliveVariablesFromSuccessors = new HashSet<Integer>();
				for (CFGNode successor : node.getSuccessors()) {
					if (R0C.get(successor) != null) {
						relevantAliveVariablesFromSuccessors.addAll(R0C.get(successor));
					}
				}

				if (!Collections.disjoint(DEF, relevantAliveVariablesFromSuccessors)) {

					if (R0C.get(node) == null) {
						R0C.put(node, REF);
					} else {
						R0C.get(node).addAll(REF);
					}
					
					sliceNodes.add(node);

				}
				
				if (R0C.get(node) == null) {
					relevantAliveVariablesFromSuccessors.removeAll(DEF);
					R0C.put(node, relevantAliveVariablesFromSuccessors);
				} else {
					relevantAliveVariablesFromSuccessors.removeAll(DEF);
					R0C.get(node).addAll(relevantAliveVariablesFromSuccessors);
				}
				
				potentialNodes.addAll(node.getPredecessors());
				

			}
			
			toVisitNodes.clear();
			toVisitNodes.addAll(potentialNodes);
		}

		return sliceNodes;
	}

}