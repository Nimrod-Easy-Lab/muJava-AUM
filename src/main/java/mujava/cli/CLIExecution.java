package mujava.cli;

import mujava.AllMutantsGenerator;
import mujava.MutationSystem;
import mujava.OpenJavaException;
import mujava.op.util.ExpressionAnalyzer;
import mujava.util.Debug;
import mujava.util.drule.DRuleUtils;
import mujava.util.drule.MutationInfo;
import org.kohsuke.args4j.*;
import org.kohsuke.args4j.spi.*;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Command line execution definitions
 *
 * @author Pedro Pinheiro
 */
public class CLIExecution {

  /**
   * All default class mutation operators
   */
  static String[] DefaultClassMutantsOperators = new String[]{"IHD", "IHI", "IOD", "OMR", "OMD", "JDC", "AMC",
	  "ISD", "IOP", "IPC", "PNC", "PMD", "PPD", "PRV", "PCI", "PCC", "PCD", "JSD", "JSI", "JTD",
	  "JTI", "JID", "OAN", "EOA", "EOC", "EAM", "EMM"};

  /**
   * All default traditional mutation operators
   */
  static String[] DefaultTraditionalMutantsOperators = new String[]{"AORB", "AORS", "AODU"
	  , "AODS", "AOIU", "AOIS", "ROR", "COR", "COD", "COI", "SOR", "LOR", "LOI", "LOD", "ASRS", "SDL",
	  "VDL", "CDL", "ODL"};

  /**
   * Selected traditional mutation operators. Defaults to all operators.
   */
  @Option(name = "-co", handler = StringArrayOptionHandler.class, usage = "Set class mutants operators.")
  String[] classMutantsOperators = DefaultClassMutantsOperators;

  /**
   * Selected traditional mutation operators. Defaults to all operators.
   */
  @Option(name = "-to", handler = StringArrayOptionHandler.class, usage = "Set traditional mutants operators.")
  String[] traditionalMutantsOperators = DefaultTraditionalMutantsOperators;

  /**
   * Whether nocompile or not. Defaults to true.
   */
  @Option(name = "-noc", handler = BooleanOptionHandler.class, usage = "Do not compile.")
  boolean nocompile;

  @Argument(handler = StringArrayOptionHandler.class)
  String[] arguments = null;


  CLIExecution() {
  }

  void doMutation(java.io.File source) throws OpenJavaException {
	AllMutantsGenerator amg = new AllMutantsGenerator(source, classMutantsOperators,
		traditionalMutantsOperators);
	amg.makeMutants();
	if (!nocompile) amg.compileMutants();
  }

  /**
   * Evaluates whether input string corresponds to a Java file or a folder and processes accordingly.
   *
   * @param input File representing a path to a Java file or a folder containing source code
   * @throws Exception When the string provided is not a path to a Java file or folder, or file/folder is not readable.
   */
  void processInput(File input) throws Exception {
	if (input.isDirectory()) {
	  for (File a : Objects.requireNonNull(input.listFiles())) {
		  processInput(a);
	  }
	}
	else if (input.getName().endsWith(".java")) {
	  String temp = input.getAbsolutePath().replace('\\', '/');
	  temp = temp.substring(0, temp.indexOf("/src/"));
	  Path source_path = Paths.get(temp);
	  configureForProjectFolder(source_path.toString().replace('\\', '/'));
	  MutationSystem.recordInheritanceRelation();
	  setMutationSystemPathFor(input.getAbsolutePath().replace('\\', '/'));
	  doMutation(input);
	}
  }

  static void configureForProjectFolder(String path) throws Exception {
	Debug.setDebugLevel(Debug.DETAILED_LEVEL);
	ExpressionAnalyzer.DbgLevel = ExpressionAnalyzer.DebugLevel.NONE;
	MutationSystem.setJMutationStructure(path);
  }

  //Copied from MutantsGenPanel.java
  //Modified by Pedro Pinheiro
  void setMutationSystemPathFor(String file_name) {
	try {
	  String temp;
	  temp = file_name.substring(0, file_name.length() - ".java".length());
	  temp = temp.replace('/', '.');
	  temp = temp.replace('\\', '.');
	  int separator_index = temp.lastIndexOf(".");

	  if (separator_index >= 0) {
		MutationSystem.CLASS_NAME = temp.substring(separator_index + 1, temp.length());
	  } else {
		MutationSystem.CLASS_NAME = temp;
	  }

	  //Modified. Changed temp to CLASS_NAME. temp behaves strangely on Windows. Ill figure out the cause later.
	  String mutant_dir_path = MutationSystem.MUTANT_HOME + "/" + MutationSystem.CLASS_NAME;
	  File mutant_path = new File(mutant_dir_path);
	  mutant_path.mkdir();

	  String class_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.CM_DIR_NAME;
	  File class_mutant_path = new File(class_mutant_dir_path);
	  class_mutant_path.mkdir();

	  String traditional_mutant_dir_path = mutant_dir_path + "/" + MutationSystem.TM_DIR_NAME;
	  File traditional_mutant_path = new File(traditional_mutant_dir_path);
	  traditional_mutant_path.mkdir();

	  String original_dir_path = mutant_dir_path + "/" + MutationSystem.ORIGINAL_DIR_NAME;
	  File original_path = new File(original_dir_path);
	  original_path.mkdir();

	  MutationSystem.CLASS_MUTANT_PATH = class_mutant_dir_path;
	  MutationSystem.TRADITIONAL_MUTANT_PATH = traditional_mutant_dir_path;
	  MutationSystem.ORIGINAL_PATH = original_dir_path;
	  //Modified. Changed temp to file_name. temp behaves strangely on Windows. Ill figure out the cause later.
	  MutationSystem.DIR_NAME = file_name;
	} catch (Exception e) {
	  System.err.println(e);
	}
  }

  //Copied from MutantsGenPanel.java
  void deleteDirectory() {
	File originalDir = new File(
		MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME + "/" + MutationSystem.ORIGINAL_DIR_NAME);
	while (originalDir.delete()) { // do nothing?
	}

	File cmDir = new File(
		MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME + "/" + MutationSystem.CM_DIR_NAME);
	while (cmDir.delete()) { // do nothing?
	}

	File tmDir = new File(
		MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME + "/" + MutationSystem.TM_DIR_NAME);
	while (tmDir.delete()) { // do nothing?
	}

	File myHomeDir = new File(MutationSystem.MUTANT_HOME + "/" + MutationSystem.DIR_NAME);
	while (myHomeDir.delete()) { // do nothing?
	}
  }

  /**
   * Configures the mutation engine for file containing source code intended to be mutated.
   * @param path Path to the project/session containing the source
   * @param classname String representing the name of the Class (contained in source) that will be mutated
   * @throws Exception
   */
  void configureForFile(String path, String classname) throws Exception {
	MutationSystem.ORIGINAL_PATH = path + "/result/" + classname + "/original";
	MutationSystem.CLASS_NAME = classname;
	MutationSystem.TRADITIONAL_MUTANT_PATH = path + "/result/" + classname + "/traditional_mutants";
	MutationSystem.CLASS_MUTANT_PATH = path + "/result/" + classname + "/class_mutants";
	MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
	MutationSystem.recordInheritanceRelation();
  }

  public static String toString (String[] string_array) {
    StringBuilder acm = new StringBuilder();
    if (string_array != null) {
      for (String s : string_array) {
        acm.append(s);
	  }
	}
    return acm.toString();
  }

  static ArrayList<String> concatArrayString(String[]... varargs) {
    ArrayList<String> arrayStringToArrayList = new ArrayList<>();
    for(String[] astr : varargs) {
      if(astr != null)
		for (String str : astr)
		  if ((str != null) && str.length() > 0) arrayStringToArrayList.add(str);
	}
    return arrayStringToArrayList;
  }

  void doMain(String[] args) {
	CmdLineParser cmdLineParser = new CmdLineParser(this);
	try {
	  cmdLineParser.parseArgument(args);
	  if (arguments != null) {
		DRuleUtils.access().setSelectedOperators(concatArrayString(traditionalMutantsOperators, classMutantsOperators));
		System.out.println("Traditional mutant operators selected: " + toString(traditionalMutantsOperators) + ".");
		System.out.println("Class mutant operators selected: " + toString(classMutantsOperators) + ".");
		for (String i : arguments)
		  processInput(Paths.get(i).toFile());
	  } else throw new CmdLineException("Bad arguments provided.");
	} catch (Exception e) {
	  System.err.println(e.getMessage());
	  System.err.println("java mujava [options...] arguments...");
	  cmdLineParser.printUsage(System.err);
	  System.err.println();
	  deleteDirectory();
	}
  }

  public static void main(String[] args) {
	new CLIExecution().doMain(args);
  }
}

