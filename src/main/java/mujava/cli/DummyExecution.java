package mujava.cli;

import mujava.AllMutantsGenerator;
import mujava.MutationSystem;
import mujava.op.basic.ExpressionAnalyzer;
import mujava.op.util.CodeChangeLog;
import mujava.util.Debug;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class DummyExecution {
	/**
	 * Set path to folder containing code that will be mutated
	 * @param path
	 */
	private static void configure(String path, String classname) throws Exception {
		char[] sanitizedPath = new char[path.length()];
		for (int i = 0; i < path.length(); ++i) {
			if (path.charAt(i) == '\\') sanitizedPath[i] = '/';
			else sanitizedPath[i] = path.charAt(i);
		}
		path = new String(sanitizedPath);
		Debug.setDebugLevel(Debug.DETAILED_LEVEL);
		ExpressionAnalyzer.DbgLevel = ExpressionAnalyzer.DebugLevel.NONE;
		MutationSystem.setJMutationStructureFromFilePath(path);
		MutationSystem.recordInheritanceRelation();
		MutationSystem.ORIGINAL_PATH = path + "/result/" + classname + "/original";
		MutationSystem.CLASS_NAME = classname;
		MutationSystem.TRADITIONAL_MUTANT_PATH =  path + "/result/"+ classname + "/traditional_mutants";
		MutationSystem.CLASS_MUTANT_PATH = path + "/result/"+ classname +"/class_mutants";
		MutationSystem.MUTANT_PATH =  MutationSystem.TRADITIONAL_MUTANT_PATH;
		CodeChangeLog.openLogFile();
	}

	public static void main(String[] args) {
		String path;
		String className = "Example";

		if ((args.length == 1) || (args.length == 2)) {
			path = args[0];
			if (args.length == 2) {
				className = args[1];
			}
		}  else {
			System.out.println("Invalid argument.");
			return;
		}
		try {
			configure(path, className);
			File original = new File(path + "/src/"+ className + ".java");
			AllMutantsGenerator amg = new AllMutantsGenerator(original, new String[0], new String[]{"ROR"});
			amg.makeMutants();
		}
		catch (FileNotFoundException e) {
			System.out.println("Error! " + path  + "\nFile not found!");
			e.printStackTrace();

		}
		catch (URISyntaxException e) {
			System.out.println("Error! Wrong URI!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("Error!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

