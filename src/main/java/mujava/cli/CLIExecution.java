package mujava.cli;

import mujava.AllMutantsGenerator;
import mujava.MutationSystem;
import mujava.op.basic.ExpressionAnalyzer;
import mujava.op.util.CodeChangeLog;
import mujava.util.Debug;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

  /**
   * Selected traditional mutation operators
   */
  @Option(name = "-co", handler = StringArrayOptionHandler.class, usage = "Set class mutants operators.")
  private String[] classMutantsOperators = DefaultClassMutantsOperators;

  /**
   * Selected traditional mutation operators
   */
  @Option(name = "-to", handler = StringArrayOptionHandler.class, usage = "Set traditional mutants operators.")
  private String[] traditionalMutantsOperators = DefaultTraditionalMutantsOperators;

  /**
   * Input sessions
   */
  @Option(name = "-i", handler = StringArrayOptionHandler.class, usage = "Comma separated list of input sessions.")
  private List<String> inputSessions = new ArrayList<>();

  /**
   * Whether compile or not
   */
  @Option(name = "-c", handler = BooleanOptionHandler.class, usage = "Compile or not.")
  private boolean compile = true;

  @Argument
  private List<String> arguments = new ArrayList<>();


  private CLIExecution() {
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

	  // print option sample. This is useful some time
//            System.err.println("  Example: java SampleMain"+cmdLineParser.printExample(ALL));
	}

	for (String session : inputSessions) {
	  configureForProjectFolder(session);
	  File sourceFolder = new File(MutationSystem.SRC_PATH);
	  File[] files_in_sourceFolder = sourceFolder.listFiles();

	  if (files_in_sourceFolder == null) throw new Exception("No source files found.");
	  if (files_in_sourceFolder.length == 0) throw new Exception("No source files found.");

	  for (File source : files_in_sourceFolder) {
		if (source.getName().endsWith(".java")) {
		  configureForFile(session, source.getName().replace(".java", ""));
		  AllMutantsGenerator amg = new AllMutantsGenerator(source, classMutantsOperators,
			  traditionalMutantsOperators);
		  amg.makeMutants();
		  if (compile) amg.compileMutants();
		}
	  }
	}
  }

  private static void configureForProjectFolder(String path) throws Exception {
	char[] sanitizedPath = new char[path.length()];
	for (int i = 0; i < path.length(); ++i) {
	  if (path.charAt(i) == '\\') sanitizedPath[i] = '/';
	  else sanitizedPath[i] = path.charAt(i);
	}
	path = new String(sanitizedPath);
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

  public static void main(String[] args) throws Exception {
	new CLIExecution().doMain(args);
  }
}

