package test;

import java.util.HashSet;
import java.util.List;

import slicer.Criterion;
import slicer.Slicer;

public class ForwardSliceTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Slicer slicer = new Slicer();
		String filePath = "C:\\Users\\Toukir Ahammed\\eclipse-workspace\\SamplePrograms\\src\\Main.java";
		HashSet<String> variableset = new HashSet<>();
		variableset.add("i");
		Criterion criterion = new Criterion(11, variableset);
		try {
			List<Integer> slicedLines = slicer.getForwardSlice(filePath, criterion);
			
			System.out.println("Sliced Lines are:");
			
			for (Integer integer : slicedLines) {
				System.out.println("Satement: " + integer);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
