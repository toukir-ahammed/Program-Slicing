package understand;

import java.io.File;

public class UnderstandJavaSourceFiles {
	public UnderstandJavaSourceFiles() {
	}

	public static void createAnalysisDB(File sourceFile, String analysisDBPath) throws Exception {
		String formattedProjectDBPath = new File(analysisDBPath).getAbsolutePath();

		System.out.println("Begin to analyze by understand tool");

		StringBuffer commandBuffer = new StringBuffer("und create -db ");
		commandBuffer.append(formattedProjectDBPath + " ");
		commandBuffer.append("-languages java ");
		ExecCommand command = new ExecCommand();
		System.out.println(commandBuffer.toString());
		command.execOneThread(commandBuffer.toString(), ".");

		String formattedSourceFileName = sourceFile.getAbsolutePath();
		commandBuffer = new StringBuffer("und -db ");
		commandBuffer.append(formattedProjectDBPath + " ");
		commandBuffer.append("add ");
		commandBuffer.append(formattedSourceFileName + " ");
		System.out.println(commandBuffer);
		command.execOneThread(commandBuffer.toString(), ".");

		commandBuffer = new StringBuffer("und -db ");
		commandBuffer.append(formattedProjectDBPath + " ");
		commandBuffer.append("analyze");
		command.execOneThread(commandBuffer.toString(), ".");
	}
}
