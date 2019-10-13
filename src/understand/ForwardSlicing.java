package understand;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ForwardSlicing extends Slicing {
	public ForwardSlicing(CFG cfg, Collection<VariableUsage> variableUsages) {
		super(cfg, variableUsages);
	}

	public HashSet<CFGNode> getSlicedNode(int line) {
		HashSet<CFGNode> sliceNodes = new HashSet<CFGNode>();

		List<CFGNode> startingRelevantNodes = cfg.getNodes(line);

		HashMap<CFGNode, HashSet<Integer>> nodeToAliveRelevantVariableSet = new HashMap<CFGNode, HashSet<Integer>>();

		HashSet<CFGNode> visitiedNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> visitingNodes = new HashSet<CFGNode>();
		HashSet<CFGNode> toVisitNodes = new HashSet<CFGNode>();
		toVisitNodes.addAll(startingRelevantNodes);

		boolean isUpdated = false;
		int numIteration = 0;

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

				useVariableNodes.removeAll(defineOrSetVariableNodes);
				HashSet<Integer> relevantAliveVariablesFromPredecessors = new HashSet<Integer>();
				for (CFGNode successor : node.getPredecessors()) {
					if (nodeToAliveRelevantVariableSet.get(successor) != null) {
						relevantAliveVariablesFromPredecessors
								.addAll((Collection<Integer>) nodeToAliveRelevantVariableSet.get(successor));
					}
				}

				if (relevantAliveVariablesFromPredecessors.size() == 0) {
					if (startingRelevantNodes.contains(node)) {
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, defineOrSetVariableNodes);
							if (node.getSuccessors().size() > 0)
								((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getSuccessors());
						} else if ((!((HashSet<?>) nodeToAliveRelevantVariableSet.get(node))
								.containsAll(defineOrSetVariableNodes)) && (defineOrSetVariableNodes.size() > 0)) {
							((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node))
									.addAll(defineOrSetVariableNodes);
							if (node.getSuccessors().size() > 0)
								((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(useVariableNodes);
							isUpdated = true;
							sliceNodes.add(node);
							potentialNodes.addAll(node.getSuccessors());
						}

					}

				} else {
					if ((useVariableNodes.size() != 0)
							&& (!Collections.disjoint(useVariableNodes, relevantAliveVariablesFromPredecessors))) {
						HashSet<Integer> refNodeSet = new HashSet<Integer>();
						refNodeSet.addAll(defineOrSetVariableNodes);
						refNodeSet.addAll(relevantAliveVariablesFromPredecessors);
						if (nodeToAliveRelevantVariableSet.get(node) == null) {
							nodeToAliveRelevantVariableSet.put(node, refNodeSet);
							if (node.getSuccessors().size() > 0)
								((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(useVariableNodes);
							isUpdated = true;
						} else if (!((HashSet<?>) nodeToAliveRelevantVariableSet.get(node)).containsAll(refNodeSet)) {
							((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(refNodeSet);
							if (node.getSuccessors().size() > 0)
								((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node)).addAll(useVariableNodes);
							isUpdated = true;
						}

						sliceNodes.add(node);

					} else {
						if ((defineOrSetVariableNodes.size() != 0) && (!Collections.disjoint(defineOrSetVariableNodes,
								relevantAliveVariablesFromPredecessors))) {
							relevantAliveVariablesFromPredecessors.removeAll(defineOrSetVariableNodes);
						}
						if ((useVariableNodes.size() == 0)
								|| (Collections.disjoint(useVariableNodes, relevantAliveVariablesFromPredecessors))) {
							if (nodeToAliveRelevantVariableSet.get(node) == null) {
								nodeToAliveRelevantVariableSet.put(node, new HashSet<Integer>());
								((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node))
										.addAll(relevantAliveVariablesFromPredecessors);
								isUpdated = true;
							} else if (!((HashSet<?>) nodeToAliveRelevantVariableSet.get(node))
									.containsAll(relevantAliveVariablesFromPredecessors)) {
								((HashSet<Integer>) nodeToAliveRelevantVariableSet.get(node))
										.addAll(relevantAliveVariablesFromPredecessors);
								isUpdated = true;
							}
						}
					}

					potentialNodes.addAll(node.getSuccessors());
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
		}

		return sliceNodes;
	}
}
