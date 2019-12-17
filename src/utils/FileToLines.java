package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileToLines {
	public FileToLines() {
	}

	public static List<String> fileToLines(String filename) {
		File file = new File(filename);
		List<String> lines = new java.util.LinkedList<String>();
		if (!file.exists())
			return lines;
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new java.io.FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static List<String> fileToLines(String filename, int startLine) {
		List<String> lines = fileToLines(filename);
		return lines.subList(startLine, lines.size());
	}

	public static String fileToString(String filename) {
		File file = new File(filename);
		String content = "";
		String line = "";
		if (!file.exists())
			return content;
		try {
			BufferedReader in = new BufferedReader(new java.io.FileReader(filename));
			while ((line = in.readLine()) != null) {
				content = content + line + "\n";
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String fileToStringNewLineBreak(String filename) {
		File file = new File(filename);
		String content = "";
		String line = "";
		if (!file.exists())
			return content;
		try {
			BufferedReader in = new BufferedReader(new java.io.FileReader(filename));
			while ((line = in.readLine()) != null) {
				content = content + line + "\r\n";
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
