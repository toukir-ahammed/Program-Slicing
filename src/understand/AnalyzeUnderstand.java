package understand;

import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import com.scitools.understand.Understand;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AnalyzeUnderstand {
	public AnalyzeUnderstand() {
	}

	public static CFG reloadCFG(String CFGFile, String methodName) {
		List<String> lines = FileToLines.fileToLines(CFGFile, 1);
		CFG cfg = null;
		for (String line : lines) {
			String[] splits = line.split("\t", -1);
			String cfgString = splits[7];
			if (splits[1].equals(methodName)) {
				cfg = new CFG(cfgString);
			}
				
		}
		return cfg;
	}

	public static Collection<VariableUsage> reloadVariableUsage(String variableUsageFile) {

		List<String> lines = FileToLines.fileToLines(variableUsageFile, 1);
		HashMap<Integer, VariableUsage> variableUsages = new HashMap<>();

		for (String line : lines) {
			String[] splits = line.split("\t");
			int nodeID = Integer.parseInt(splits[0]);
			String methodName = splits[6];

			if (variableUsages.get(Integer.valueOf(nodeID)) == null) {
				variableUsages.put(Integer.valueOf(nodeID), new VariableUsage(splits[1]));
			}

			VariableUsage vu = variableUsages.get(Integer.valueOf(nodeID));
			vu.setNodeID(nodeID);
			// vu.setMethodID(methodID);
			vu.setMethodName(methodName);
			String variableType = splits[3];
			vu.setVariableType(variableType);

			int refLineNum = Integer.parseInt(splits[4]);
			String usageType = splits[5];
			if (usageType.equals("define")) {
				vu.getDefLines().add(Integer.valueOf(refLineNum));
			} else if (usageType.equals("set")) {
				vu.getSetLines().add(Integer.valueOf(refLineNum));
			} else if (usageType.equals("use")) {
				vu.getUseLines().add(Integer.valueOf(refLineNum));
			} else
				System.err.println("unknown usage type for the variable info:" + line);
		}
		return variableUsages.values();
	}

	public static void extractCFG(String filename, String saveFile) throws Exception {
		if (new File(saveFile).exists())
			return;
		Database database = Understand.open(filename);
		Entity[] entities = database.ents("Java Method Constructor Member, Java Method Member");
		int IOFlushNum = 10000;
		List<String> lines = new ArrayList<String>();

		lines.add("method ID \t method name \t params \t source file \t start line \t end line \t method kind \t CFG");

		for (Entity entity : entities)
			if ((entity.library() == null) || (entity.library().equals(""))) {

				Reference[] refs = entity.refs("Definein", "", false);
				if (refs.length != 0) {

					String file = EntityUtil.getFile(entity);
					int startLine = EntityUtil.getLineStart(entity);
					int endLine = EntityUtil.getLineEnd(entity);

					lines.add(entity.id() + "\t" + entity.longname(true) + "\t" + entity.parameters() + "\t" + file
							+ "\t" + startLine + "\t" + endLine + "\t" + entity.kind().name() + "\t"
							+ entity.freetext("CGraph"));
					if (lines.size() >= IOFlushNum) {
						WriteLinesToFile.writeLinesToFile(lines, saveFile);
						lines.clear();
					}
				}
			}
		if (!lines.isEmpty()) {
			WriteLinesToFile.writeLinesToFile(lines, saveFile);
		}
		database.close();
	}

	public static void extractVariableUse(String filename, String saveFile) throws Exception {
		if (new File(saveFile).exists())
			return;
		Database database = Understand.open(filename);
		Entity[] entities = database.ents("Java Method Constructor Member, Java Method Member");

		List<String> lines = new ArrayList<String>();

		lines.add(
				"variable ID \t variable name \t variable type \t reference kind (param or variable) \t reference line \t define or set or use? \t method name \t source file \t start line \t end line");

		int IOFlushNum = 10000;

		for (Entity entity : entities)
			if ((entity.library() == null) || (entity.library().equals(""))) {

				Reference[] refs = entity.refs("Definein", "", false);
				if (refs.length != 0) {

					String file = EntityUtil.getFile(entity);
					int startLine = EntityUtil.getLineStart(entity);
					int endLine = EntityUtil.getLineEnd(entity);
					String methodInfo = entity.longname(true) + "\t" + file + "\t" + startLine + "\t" + endLine;

					Reference[] parameterRefs = entity.refs("Define", "Parameter", false);
					for (Reference paramRef : parameterRefs) {
						String variableName = paramRef.ent().name();
						String variableType = paramRef.ent().type();
						int refLine = paramRef.line();
						lines.add(paramRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t" + "param"
								+ "\t" + refLine + "\t" + "define" + "\t" + methodInfo);
					}

					parameterRefs = entity.refs("Set", "Parameter", false);
					for (Reference paramRef : parameterRefs) {
						String variableName = paramRef.ent().name();
						String variableType = paramRef.ent().type();
						int refLine = paramRef.line();
						lines.add(paramRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t" + "param"
								+ "\t" + refLine + "\t" + "set" + "\t" + methodInfo);
					}

					parameterRefs = entity.refs("Use", "Parameter", false);
					String variableName;
					for (Reference paramRef : parameterRefs) {
						variableName = paramRef.ent().name();
						String variableType = paramRef.ent().type();
						int refLine = paramRef.line();
						lines.add(paramRef.ent().id() + "\t" + variableName + "\t" + variableType + "\t" + "param"
								+ "\t" + refLine + "\t" + "use" + "\t" + methodInfo);
					}

					Reference[] variableRefs = entity.refs("Define", "Variable", false);
					for (Reference varRef : variableRefs) {
						String variableName1 = varRef.ent().name();
						String variableType = varRef.ent().type();
						int refLine = varRef.line();
						lines.add(varRef.ent().id() + "\t" + variableName1 + "\t" + variableType + "\t" + "variable"
								+ "\t" + refLine + "\t" + "define" + "\t" + methodInfo);
					}

					variableRefs = entity.refs("Set", "Variable", false);
					for (Reference varRef : variableRefs) {
						String variableName1 = varRef.ent().name();
						String variableType = varRef.ent().type();
						int refLine = varRef.line();
						lines.add(varRef.ent().id() + "\t" + variableName1 + "\t" + variableType + "\t" + "variable"
								+ "\t" + refLine + "\t" + "set" + "\t" + methodInfo);
					}

					variableRefs = entity.refs("Use", "Variable", false);
					for (Reference varRef : variableRefs) {
						String variableName1 = varRef.ent().name();
						String variableType = varRef.ent().type();
						int refLine = varRef.line();
						lines.add(varRef.ent().id() + "\t" + variableName1 + "\t" + variableType + "\t" + "variable"
								+ "\t" + refLine + "\t" + "use" + "\t" + methodInfo);
					}

					if (lines.size() >= IOFlushNum) {
						WriteLinesToFile.appendLinesToFile(lines, saveFile);
						lines.clear();
					}
				}
			}
		if (!lines.isEmpty()) {
			WriteLinesToFile.appendLinesToFile(lines, saveFile);
		}
		database.close();
	}
}
