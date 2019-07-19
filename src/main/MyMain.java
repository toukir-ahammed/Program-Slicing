package main;

import java.io.File;


import javax.swing.JFileChooser;

import understand.AnalyzeUnderstand;
import understand.UnderstandJavaSourceFiles;


public class MyMain {
	static {
		try {
			System.load("C:\\Program Files\\SciTools\\bin\\pc-win64\\Java\\Understand.dll");
			System.out.println("Native code library has been loaded successfully.\n");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
			
		} else {
			System.out.println("No file is choosen. Exiting...");

		}
		
	}
}
