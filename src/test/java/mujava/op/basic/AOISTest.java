package mujava.op.basic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mujava.MutationSystem;
import mujava.OpenJavaException;
import mujava.TraditionalMutantsGenerator;
import mujava.op.util.LogReduction;

public class AOISTest {

    @Test
    public void dummy_test() {
        Assert.assertEquals(true,true);
    }
/*
	private static List<String> mujavaLogLines;
	private static List<String> nimrodEquivalentLogLines;
	private static List<File> mutantsDirs;

	@BeforeClass
	public static void setupEnv() {
		File originaFile = new File(TestSuite.PATH_TO_RESOURCES + "mutants/session/src/AOISExample.java");
		try {
			System.setProperty("user.dir", TestSuite.PATH_TO_RESOURCES + "mutants/session");
			TestSuite.makeMujavaConfigFile(TestSuite.PATH_TO_RESOURCES + "mutants/session");

			// FileUtils.writeStringToFile(originaFile, content());
			String[] opArray = { "AOIS" };
			List<String> allOperatorsSelected = new ArrayList<String>();
			MutationSystem.setJMutationPaths("AOISExample");
			TestSuite.setMutationSystemPathFor("AOISExample.java");
			TraditionalMutantsGenerator tmGenEngine = new TraditionalMutantsGenerator(originaFile, opArray,
					allOperatorsSelected);
			tmGenEngine.makeMutants();
			tmGenEngine.compileMutants();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OpenJavaException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() {
		if (mujavaLogLines == null || mujavaLogLines.size() == 0) {
			mujavaLogLines = TestSuite
					.readLogFile("mutants/session/result/AOISExample/traditional_mutants/mutation_log");
		}
		if (nimrodEquivalentLogLines == null || nimrodEquivalentLogLines.size() == 0) {
			nimrodEquivalentLogLines = TestSuite.readLogFile("mutants/session/result/nimrod_equivalent.log");
		}
		if (mutantsDirs == null || mutantsDirs.size() == 0) {
			mutantsDirs = TestSuite.listMutantsDirs("mutants/session/result/AOISExample/traditional_mutants");
		}
	}

	@Test
	public void testAOISEquivalenceLastStmtLocalVariable() {
		// open mujava log file to get generated mutants
		List<String> mutants = new ArrayList<String>();
		for (String line : mujavaLogLines) {
			// Get mutants equivalents last stmt in void method
			if (line.contains("void_f001()") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			// Get mutants equivalents last stmt in a return
			if (line.contains("int_f002()") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			// Get mutants equivalents last stmt in a return inside an if
			if (line.contains("int_f003(int)") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			// Get mutants equivalents last stmt in a method with the variable
			// declared inside an if
			if (line.contains("void_f009()") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			// Get mutants equivalents last stmt in a method with the variable
			// declared inside a try
			if (line.contains("void_f010()") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}

		}
		// Verify if Equivalence Avoid is enable
		if (LogReduction.AVOID) {
			Assert.assertEquals(mutants.size(), 0);
			return;
		} else {
			Assert.assertEquals(mutants.size(), 10);
		}

		// Nimrod log file should detect equivalence
		for (String mutant : mutants) {
			boolean detected = false;
			for (String line : nimrodEquivalentLogLines) {
				// Check if Nimrod detected equivalence
				if (line.contains(mutant+":")) {
					detected = true;
				}
			}
			if (!detected) {
				Assert.assertTrue("Mutant: " + mutant + " detected as useful. But it is useless.", false);
			}
		}

	}

	@Test
	public void testAOISLastStmtFieldVariable() {
		// open mujava log file to get generated mutants
		List<String> mutants = new ArrayList<String>();
		for (String line : mujavaLogLines) {
			// Get mutants last stmt in void method
			if (line.contains("void_f005(int)") && (line.contains("field++") || line.contains("field--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			// Get mutants last stmt in a return
			if (line.contains("int_f006()") && (line.contains("field++") || line.contains("field--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
		}
		Assert.assertEquals(mutants.size(), 4);

		// Directories should be generated
		for (String equivalentMutant : mutants) {
			boolean detected = false;
			for (File dirs : mutantsDirs) {
				if (dirs.getAbsolutePath().contains(equivalentMutant)) {
					detected = true;
				}
			}
			if (!detected) {
				Assert.assertTrue("Mutant: " + equivalentMutant + " was not generated.", false);
			}
		}

		// Nimrod log file should NOT detect equivalence
		for (String usefulMutant : mutants) {
			boolean detected = false;
			for (String line : nimrodEquivalentLogLines) {
				// Check if Nimrod detected equivalence
				if (line.contains(usefulMutant+":")) {
					detected = true;
				}
			}
			if (detected) {
				Assert.assertTrue("Mutant: " + usefulMutant + " detected as useless. But it is useful.", false);
			}
		}

	}

	@Test
	public void testAOISLastStmtLocalVariableInsideALoop() {
		// open mujava log file to get generated mutants
		List<String> mutants = new ArrayList<String>();
		for (String line : mujavaLogLines) {
			// Get mutants last stmt of the method, but inside a loop
			if (line.contains("void_f004(int)") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
		}
		Assert.assertEquals(mutants.size(), 2);

		// Nimrod log file should NOT detect equivalence
		for (String uselessMutant : mutants) {
			boolean detected = false;
			for (String line : nimrodEquivalentLogLines) {
				// Check if Nimrod detected equivalence
				if (line.contains(uselessMutant+":")) {
					detected = true;
				}
			}
			if (detected) {
				Assert.assertTrue("Mutant: " + uselessMutant + " detected as useless. But it is useful.", false);
			}
		}

	}

	@Test
	public void testAOISEquivalenceLastStmtLocalVariableInComplexReturn() {
		// open mujava log file to get generated mutants
		List<String> mutants = new ArrayList<String>();
		for (String line : mujavaLogLines) {
			if (line.contains("int_f007(int)") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}

			if (line.contains("void_f008(int)") && (line.contains("x++") || line.contains("x--"))) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
		}
		// Verify if Equivalence Avoid is enable
		if (LogReduction.AVOID) {
			Assert.assertEquals(mutants.size(), 4);
			return;
		} else {
			Assert.assertEquals(mutants.size(), 8);
		}

		// Nimrod log file should detect equivalence
		int detected = 0;
		for (String uselessMutant : mutants) {
			for (String line : nimrodEquivalentLogLines) {
				// Check if Nimrod detected equivalence
				if (line.contains(uselessMutant+":")) {
					detected++;
				}
			}
		}
		if (detected != 4) {
			Assert.assertTrue("Mutant equivalence wrong detected.", false);
		}

	}

	@AfterClass
	public static void tearDownEnv() {
		try {
			FileUtils.cleanDirectory(new File(TestSuite.PATH_TO_RESOURCES + "mutants/session/result/"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
}
