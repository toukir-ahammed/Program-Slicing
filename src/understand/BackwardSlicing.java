package understand;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import main.MyMain;

public class BackwardSlicing extends Slicing {
	public BackwardSlicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		super(cfg, variableUsages);
	}

	public HashSet<CFGNode> getSlicedNode(int line) {
		List<CFGNode> startingRelevantNodes = cfg.getNodes(line);

		HashMap<CFGNode, HashSet<Integer>> nodeToAliveRelevantVariableSet = new HashMap<CFGNode, HashSet<Integer>>();

		HashSet<CFGNode> visitiedNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> visitingNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> toVisitNodes = new HashSet<CFGNode>();
		toVisitNodes.addAll(startingRelevantNodes);

		boolean isUpdated = false;
		int numIteration = 0;

		HashSet<CFGNode> sliceNodes = new HashSet<CFGNode>();

		while (toVisitNodes.size() != 0) {
			
			numIteration++;
			visitingNodes.addAll(toVisitNodes);
			toVisitNodes.clear();

			HashSet<CFGNode> potentialNodes = new HashSet<CFGNode>();

			for (CFGNode node : visitingNodes) {
				visitiedNodes.add(node);
				HashSet<Integer> useVariableNodes = getUseNodes(node.getLineStart(), node.getLineEnd(), variableUsages);
				HashSet<Integer> defineOrSetVariableNodes = getDefOrSetNodes(node.getLineStart(), node.getLineEnd(),
						variableUsages);
				
				
				defineOrSetVariableNodes.removeAll(useVariableNodes);
				useVariableNodes.removeAll(defineOrSetVariableNodes);

				HashSet<Integer> relevantAliveVariablesFromSuccessors = new HashSet<Integer>();
				for (CFGNode successor : node.getSuccessors()) {
					if (nodeToAliveRelevantVariableSet.get(successor) != null) {
						relevantAliveVariablesFromSuccessors
								.addAll((Collection<Integer>) nodeToAliveRelevantVariableSet.get(successor));
					}
				}

				if (relevantAliveVariablesFromSuccessors.size() == 0) {
					if (startingRelevantNodes.contains(node)) {
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getPredecessors());
						} else if ((!((HashSet<?>) nodeToAliveRelevantVariableSet.get(node))
								.containsAll(useVariableNodes)) && (useVariableNodes.size() > 0)) {
							((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getPredecessors());
						}

					}

				} else {
					if ((defineOrSetVariableNodes.size() != 0) && (!Collections.disjoint(defineOrSetVariableNodes,
							relevantAliveVariablesFromSuccessors))) {

						HashSet<Integer> refNodeSet = new HashSet<Integer>();
						refNodeSet.addAll(useVariableNodes);
						refNodeSet.addAll(relevantAliveVariablesFromSuccessors);
						refNodeSet.removeAll(defineOrSetVariableNodes);
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, refNodeSet);
							isUpdated = true;
						} else if (!((HashSet<?>) nodeToAliveRelevantVariableSet.get(node)).containsAll(refNodeSet)) {
							((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(refNodeSet);
							isUpdated = true;
						}

						sliceNodes.add(node);
					}

					if ((defineOrSetVariableNodes.size() == 0)
							|| (Collections.disjoint(defineOrSetVariableNodes, relevantAliveVariablesFromSuccessors))) {
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, new HashSet<Integer>());
							((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node))
									.addAll(relevantAliveVariablesFromSuccessors);
							isUpdated = true;
						} else if (!((HashSet<?>) nodeToAliveRelevantVariableSet.get(node))
								.containsAll(relevantAliveVariablesFromSuccessors)) {
							((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node))
									.addAll(relevantAliveVariablesFromSuccessors);
							isUpdated = true;
						}
					}

					potentialNodes.addAll(node.getPredecessors());
				}

				for (CFGNode potentialNode : potentialNodes) {
					if (!visitiedNodes.contains(potentialNode)) {
						toVisitNodes.add(potentialNode);
					} else if (isUpdated) {
						toVisitNodes.add(potentialNode);
					}
				}

				isUpdated = false;
			}
			visitingNodes.clear();
			// System.err.println("--------------------------------------------------------");
			// for (Iterator iterator = potentialNodes.iterator(); iterator.hasNext();) {
			// CFGNode cfgNode = (CFGNode) iterator.next();
			// System.out.println(cfgNode.getLineStart() + "-->" + cfgNode.getLineEnd());
			//
			// }
		}

		return sliceNodes;
	}
}
