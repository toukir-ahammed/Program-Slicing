package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;

import understand.AnalyzeUnderstand;
import understand.CFG;
import understand.CFGNode;
import understand.FileToLines;
import understand.MySlicing;
import understand.Slicing;
import understand.UnderstandJavaSourceFiles;
import understand.VariableUsage;
import understand.WriteLinesToFile;

public class MyMain {
	static {
		try {
			System.load("C:\\Program Files\\SciTools\\bin\\pc-win64\\Java\\Understand.dll");
			//System.loadLibrary("Understand");
			System.out.println("Native code library has been loaded successfully.\n");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File curDir = new File(System.getProperty("user.dir"));
		fileChooser.setCurrentDirectory(curDir);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle("Select a file to slice");
		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File sourceFile = fileChooser.getSelectedFile();
			String sourceDir = sourceFile.getParent();

			System.out.println("The following file is selected:");
			System.out.println(sourceFile);

			String udbFile = sourceDir + File.separator + sourceFile.getName() + ".udb";
			String useFile = sourceDir + File.separator + sourceFile.getName() + ".use";
			String cfgFile = sourceDir + File.separator + sourceFile.getName() + ".cfg";

			File indexFile = new File(udbFile);

			if (!indexFile.exists()) {
				try {
					UnderstandJavaSourceFiles.createAnalysisDB(sourceFile, udbFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Filename already exists. Exiting...");
			}
			indexFile = new File(cfgFile);
			try {
				AnalyzeUnderstand.extractCFG(udbFile, cfgFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			indexFile = new File(useFile);
			try {
				AnalyzeUnderstand.extractVariableUse(udbFile, useFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<String> lines = FileToLines.fileToLines(sourceFile.getAbsolutePath());
			
			List<String> slicedLines = new ArrayList<String>();

			CFG cfg = AnalyzeUnderstand.reloadCFG(cfgFile);
			Collection<VariableUsage> variableUsages = AnalyzeUnderstand.reloadVariableUsage(useFile);
			Slicing backwardSlicing = new MySlicing(cfg, variableUsages);
			
			HashSet<CFGNode> cfgnodes = backwardSlicing.getSlicedNode(11);

			for (Iterator<CFGNode> iterator2 = cfgnodes.iterator(); iterator2.hasNext();) {
				CFGNode cfgNode = (CFGNode) iterator2.next();
//				System.out.println("Backward Slice result");
//				System.out.println("Start: " + cfgNode.getLineStart());
//				System.out.println("End: " + cfgNode.getLineEnd());
				
				for (int i = cfgNode.getLineStart(); i <= cfgNode.getLineEnd(); i++) {
					slicedLines.add(lines.get(i-1));
				}

			}
			
			String slicedFile = sourceDir + File.separator + "sliced"+ sourceFile.getName();
			WriteLinesToFile.writeLinesToFile(slicedLines, slicedFile);

		} else {
			System.out.println("No file is choosen. Exiting...");

		}

	}
}
