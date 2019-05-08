package mujava.cli;

import mujava.AllMutantsGenerator;
import mujava.MutationSystem;
import mujava.OpenJavaException;
import mujava.op.util.ExpressionAnalyzer;
import mujava.op.util.CodeChangeLog;
import mujava.util.Debug;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Command line execution definitions
 *
 * @author Pedro Pinheiro
 */
public class CLIExecution {
  /**
   * All default class mutation operators
   */
  private static String[] DefaultClassMutantsOperators = new String[]{"IHD", "IHI", "IOD", "OMR", "OMD", "JDC", "AMC",
	  "ISD", "IOP", "IPC", "PNC", "PMD", "PPD", "PRV", "PCI", "PCC", "PCD", "JSD", "JSI", "JTD",
	  "JTI", "JID", "OAN", "EOA", "EOC", "EAM", "EMM"};

  /**
   * All default traditional mutation operators
   */
  private static String[] DefaultTraditionalMutantsOperators = new String[]{"AORB", "AORS", "AODU"
	  , "AODS", "AOIU", "AOIS", "ROR", "COR", "COD", "COI", "SOR", "LOR", "LOI", "LOD", "ASRS", "SDL",
	  "VDL", "CDL", "ODL"};

  private boolean isValidTraditionalMutantOperator(String operator) {
	boolean contains = false;
	for (String str : DefaultTraditionalMutantsOperators) {
	  if (str.equals(operator)) {
		contains = true;
		break;
	  }
	}
	return contains;
  }

  private boolean isValidClassMutantOperator(String operator) {
	boolean contains = false;
	for (String str : DefaultClassMutantsOperators) {
	  if (str.equals(operator)) {
		contains = true;
		break;
	  }
	}
	return contains;
  }

  /**
   * Selected traditional mutation operators. Defaults to all operators.
   */
  @Option(name = "-co", handler = StringArrayOptionHandler.class, usage = "Set class mutants operators.")
  private String[] classMutantsOperators = DefaultClassMutantsOperators;

  /**
   * Selected traditional mutation operators. Defaults to all operators.
   */
  @Option(name = "-to", handler = StringArrayOptionHandler.class, usage = "Set traditional mutants operators.")
  private String[] traditionalMutantsOperators = DefaultTraditionalMutantsOperators;

  /**
   * Input sessions
   */
  @Option(name = "-i", handler = StringArrayOptionHandler.class, usage = "Comma separated list of input sessions.")
  private List<String> input = new ArrayList<>();

  /**
   * Whether nocompile or not. Defaults to true.
   */
  @Option(name = "-noc", handler = BooleanOptionHandler.class, usage = "Do not compile.")
  private boolean nocompile;

  @Argument
  private List<String> arguments = new ArrayList<>();


  private CLIExecution() {
  }

  private void doMutation(java.io.File source) throws OpenJavaException {
	AllMutantsGenerator amg = new AllMutantsGenerator(source, classMutantsOperators,
		traditionalMutantsOperators);
	amg.makeMutants();
	if (!nocompile) amg.compileMutants();
  }

  /**
   * Evaluates whether input string corresponds to a Java file or a folder and processes accordingly.
   *
   * @param input String representing a path to a Java file or a folder containing source code structured by
   *              supported format
   * @throws Exception When the string provided is not a path to a Java file or folder, or file/folder is not readable.
   */
  private void processInput(String input) throws Exception {
	char[] sanitizedPath = new char[input.length()];
	for (int i = 0; i < input.length(); ++i) {
	  if (input.charAt(i) == '\\') sanitizedPath[i] = '/';
	  else sanitizedPath[i] = input.charAt(i);
	}
	input = new String(sanitizedPath);
	if (input.contains(".java")) {
	  String before_src = input.substring(0, input.indexOf("/src/"));
	  String classname = Paths.get(input).getFileName().toString();
	  classname = classname.replace(".java", "");
	  configureForProjectFolder(before_src);
	  configureForFile(before_src, classname);
	  System.out.println("Generating mutants for " + classname + ".");
	  System.out.println("Session is" + before_src + ".");
	  java.io.File source = new java.io.File(input);
	  doMutation(source);
	} else if (Files.isDirectory(Paths.get(input))) {
	  processInputAsDirectory(input);
	} else {
	  System.out.println("Invalid input. Try -h for help.");
	}
  }

  private void processInputAsDirectory(String session_path) throws IOException {
	String source_folder = session_path + "/src/";
	try (DirectoryStream<Path> stream = java.nio.file.Files.newDirectoryStream(Paths.get(source_folder), "*.java")) {
	  for (Path entry : stream)
		processInput(entry.toString());

	} catch (DirectoryIteratorException ex) {
	  throw ex.getCause();
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  private static void configureForProjectFolder(String path) throws Exception {
	Debug.setDebugLevel(Debug.DETAILED_LEVEL);
	ExpressionAnalyzer.DbgLevel = ExpressionAnalyzer.DebugLevel.NONE;
	MutationSystem.setJMutationStructure(path);

	CodeChangeLog.openLogFile();
  }

  private void configureForFile(String path, String classname) throws Exception {
	MutationSystem.ORIGINAL_PATH = path + "/result/" + classname + "/original";
	MutationSystem.CLASS_NAME = classname;
	MutationSystem.TRADITIONAL_MUTANT_PATH = path + "/result/" + classname + "/traditional_mutants";
	MutationSystem.CLASS_MUTANT_PATH = path + "/result/" + classname + "/class_mutants";
	MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
	MutationSystem.recordInheritanceRelation();
  }

  private void doMain(String[] args) throws Exception {
	CmdLineParser cmdLineParser = new CmdLineParser(this);
	try {
	  cmdLineParser.parseArgument(args);
	  if (arguments.isEmpty()) throw new CmdLineException(cmdLineParser, "No argument given");
	} catch (CmdLineException e) {
	  // if there's a problem in the command line,
	  // you'll get this exception. this will report
	  // an error message.
	  System.err.println(e.getMessage());
	  System.err.println("java SampleMain [options...] arguments...");
	  // print the list of available options
	  cmdLineParser.printUsage(System.err);
	  System.err.println();
//	  System.err.println("  Example: java mujava.cli.CLI"+cmdLineParser.printExample(cmdLineParser.));
	}
	System.out.println("Traditional mutant operators selected: " + traditionalMutantsOperators.toString() + ".");
	System.out.println("Class mutant operators selected: " + classMutantsOperators.toString() + ".");
	for (String i : input)
	  processInput(i);
  }

  public static void main(String[] args) throws Exception {
	new CLIExecution().doMain(args);
  }
}

