package understand;

import java.io.FileWriter;

public class WriteLinesToFile
{
  public WriteLinesToFile() {}
  
  public static void writeLinesToFile(java.util.List<String> lines, String file) {
    try {
      FileWriter writer = new FileWriter(new java.io.File(file));
      for (String line : lines) {
        writer.write(line + "\n");
      }
      writer.close();
    } catch (Exception e) {
      System.err.println("error happens when writing lines to file " + file);
      e.printStackTrace();
    }
  }
  
  public static void appendLinesToFile(java.util.List<String> lines, String file) {
    try {
      FileWriter writer = new FileWriter(new java.io.File(file), true);
      for (String line : lines) {
        writer.write(line + "\n");
      }
      writer.close();
    } catch (Exception e) {
      System.err.println("error happens when writing lines to file " + file);
      e.printStackTrace();
    }
  }
  
  public static void writeToFiles(String content, String file) {
    try {
      FileWriter writer = new FileWriter(new java.io.File(file));
      writer.write(content);
      writer.close();
    } catch (Exception e) {
      System.err.println("error happens when writing lines to file " + file);
      e.printStackTrace();
    }
  }
}
