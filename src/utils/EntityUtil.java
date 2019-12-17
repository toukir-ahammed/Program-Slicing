package utils;

import com.scitools.understand.Entity;

public class EntityUtil {
	public EntityUtil() {
	}

	public static int getLineStart(Entity entity) {
		com.scitools.understand.Reference[] refs = entity.refs("Definein", "", false);
		if (refs.length == 0) {
			return -1;
		}
		int startLine = refs[0].line();
		return startLine;
	}

	public static int getLineEnd(Entity entity) {
		String cfgString = entity.freetext("CGraph");
		int lineEnd = -1;
		if ((cfgString != null) && (!cfgString.equals("null"))) {
			CFG cfg = new CFG(cfgString);
			if (cfg != null) {
				for (CFGNode node : cfg.getAllNodes()) {
					if (node.getLineEnd() > lineEnd) {
						lineEnd = node.getLineEnd();
					}
				}
			}
		}
		com.scitools.understand.Reference[] refs = entity.refs("Definein", "", false);
		if (refs.length == 0) {
			return -1;
		}
		int startLine = refs[0].line();
		int endLine = startLine + entity.metric("CountLineCode").intValue() - 1;
		if (endLine < startLine) {
			endLine = startLine;
		}

		if (endLine > lineEnd) {
			lineEnd = endLine;
		}

		return lineEnd;
	}

	public static String getFile(Entity entity) {
		com.scitools.understand.Reference[] refs = entity.refs("Definein", "", false);
		if (refs.length == 0) {
			return "";
		}
		String file = "";
		if (refs.length > 0) {
			file = refs[0].file().longname(false);
		}
		return file;
	}
}
