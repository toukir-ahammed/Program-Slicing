package slicer;

import java.util.HashSet;

public class Criterion {
	
	private int linenum;
	private HashSet<String> variableSet;
	
	public Criterion(int linenum, HashSet<String> variableSet) {
		// TODO Auto-generated constructor stub
		this.setLinenum(linenum);
		this.setVariableSet(variableSet);
	}

	public int getLinenum() {
		return linenum;
	}

	public void setLinenum(int linenum) {
		this.linenum = linenum;
	}

	public HashSet<String> getVariableSet() {
		return variableSet;
	}

	public void setVariableSet(HashSet<String> variableSet) {
		this.variableSet = variableSet;
	}
	
	

}
