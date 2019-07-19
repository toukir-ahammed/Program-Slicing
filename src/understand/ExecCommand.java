package understand;

import java.io.BufferedReader;

public class ExecCommand {
	public ExecCommand() {
	}

	public String execOneThread(String command, String workingpath) {
		StringBuffer result = new StringBuffer("");
		try {
			java.io.File dir = new java.io.File(workingpath);
			Process process = Runtime.getRuntime().exec(command, null, dir);
			BufferedReader stdInput = new BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new java.io.InputStreamReader(process.getErrorStream()));
			String line = null;
			while ((line = stdInput.readLine()) != null) {
				result.append(line + "\n");
			}

			while ((line = stdError.readLine()) != null) {
				System.out.println(line);
			}

			stdInput.close();
			stdError.close();
		} catch (Exception e) {
			System.err.println("Error:" + command);
			return null;
		}
		return result.toString();
	}

/*	public Pair<String, String> execOneThread(String[] commands, String workingpath) {
		Pair<String, String> result = null;
		try {
			java.io.File dir = new java.io.File(workingpath);

			Process process = Runtime.getRuntime().exec(commands, null, dir);

			ReadStream s1 = new ReadStream("stdin", process.getInputStream());
			ReadStream s2 = new ReadStream("stderr", process.getErrorStream());
			s1.start();
			s2.start();
			if (!process.waitFor(2L, java.util.concurrent.TimeUnit.MINUTES)) {
				result = new Pair("Timeout", "Timeout");
				s1.end();
				s2.end();
				process.destroy();
			} else {
				result = new Pair("OK", "OK");

			}

		} catch (Exception e) {

			e.printStackTrace();
			System.err.println("Error:" + e.getClass());
			return result;
		}
		ReadStream s2;
		ReadStream s1;
		return result;
	} */
}
