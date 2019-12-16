package slicer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.activity.InvalidActivityException;

import understand.AnalyzeUnderstand;
import understand.CFG;
import understand.CFGNode;
import understand.FSlicing;
import understand.MySlicing;
import understand.Slicing;
import understand.UnderstandJavaSourceFiles;
import understand.VariableUsage;

public class Slicer {
	
	static {
		try {
			// System.load("C:\\Program Files\\SciTools\\bin\\pc-win64\\Java\\Understand.dll");
			System.loadLibrary("/lib/Understand");
			System.out.println("Native code library has been loaded successfully.\n");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}
	// filePath is absolute path
	private List<Integer> getSlice(String filePath, Criterion criterion, String type) throws Exception {
		File sourceFile = new File(filePath);

		if (sourceFile.exists()) {
			Path sourceFilePath = Files.createTempDirectory("programSlicerTemp");
			
			sourceFilePath.toFile().deleteOnExit();
			
			String sourceDir = sourceFilePath.toString();
			
			String timestamp = String.valueOf(new Date().getTime());

			String udbFile = sourceDir + File.separator + sourceFile.getName() + timestamp + ".udb";
			String useFile = sourceDir + File.separator + sourceFile.getName() + timestamp + ".use";
			String cfgFile = sourceDir + File.separator + sourceFile.getName() + timestamp + ".cfg";

			File indexFile = new File(udbFile);

			if (!indexFile.exists()) {
				UnderstandJavaSourceFiles.createAnalysisDB(sourceFile, udbFile);
			} else {
				throw new FileAlreadyExistsException(indexFile.getAbsolutePath());
			}

			indexFile = new File(useFile);

			if (!indexFile.exists()) {
				AnalyzeUnderstand.extractVariableUse(udbFile, useFile);
				
			} else {
				throw new FileAlreadyExistsException(indexFile.getAbsolutePath());
			}

			indexFile = new File(cfgFile);

			if (!indexFile.exists()) {
				AnalyzeUnderstand.extractCFG(udbFile, cfgFile);
			} else {
				throw new FileAlreadyExistsException(indexFile.getAbsolutePath());
			}


			String methodName = null;

			Collection<VariableUsage> variableUsages = AnalyzeUnderstand.reloadVariableUsage(useFile);
			for (VariableUsage variableUsage : variableUsages) {
				if (criterion.getVariableSet().contains(variableUsage.name)
						&& variableUsage.isRelevantLine(criterion.getLinenum())) {
					methodName = variableUsage.getMethodName();
				}
			}

			if (methodName != null) {
				CFG cfg = AnalyzeUnderstand.reloadCFG(cfgFile, methodName);
				
				Slicing slicing = null;
								
				if (type.equals("backward")) {
					slicing = new MySlicing(cfg, variableUsages);
					
				} else if (type.equals("forward")) {
					slicing = new FSlicing(cfg, variableUsages);
				}
				
				HashSet<CFGNode> cfgnodes = slicing.getSlicedNode(criterion.getLinenum());
				

				
				List<Integer> slicedLines = new ArrayList<>();
				for (CFGNode cfgNode : cfgnodes) {
					for (int i = cfgNode.getLineStart(); i <= cfgNode.getLineEnd(); i++) {
						slicedLines.add(i);
					}
				}

				Collections.sort(slicedLines);

				return slicedLines;
			} else {
				// TODO Handle Exception				
				throw new InvalidActivityException("Slicing criterion is not valid");
			}

		} else {
			throw new FileNotFoundException();
		}

	}
	
	public List<Integer> getBackwardSlice(String filePath, Criterion criterion) throws Exception {
		return getSlice(filePath, criterion, "backward");
	}

	public List<Integer> getForwardSlice(String filePath, Criterion criterion) throws Exception {
		return getSlice(filePath, criterion, "forward");
	}

}
