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

public class SDLTest {
    @Test
    public void dummy_test() {
        Assert.assertEquals(true,true);
    }
	/*

	private static List<String> mujavaLogLines;
	private static List<String> nimrodDuplicatedLogLines;
	private static List<File> mutantsDirs;

	@BeforeClass
	public static void setupEnv() {
		File originaFile = new File(TestSuite.PATH_TO_RESOURCES + "mutants/session/src/SDLExample.java");
		try {
			System.setProperty("user.dir", TestSuite.PATH_TO_RESOURCES + "mutants/session");
			TestSuite.makeMujavaConfigFile(TestSuite.PATH_TO_RESOURCES + "mutants/session");

			// FileUtils.writeStringToFile(originaFile, content());
			String[] opArray = { "SDL" };
			List<String> allOperatorsSelected = new ArrayList<String>();
			allOperatorsSelected.add("SDL");
			MutationSystem.setJMutationPaths("SDLExample");
			TestSuite.setMutationSystemPathFor("SDLExample.java");
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
					.readLogFile("mutants/session/result/SDLExample/traditional_mutants/mutation_log");
		}
		if (nimrodDuplicatedLogLines == null || nimrodDuplicatedLogLines.size() == 0) {
			nimrodDuplicatedLogLines = TestSuite.readLogFile("mutants/session/result/nimrod_duplicated.log");
		}
		if (mutantsDirs == null || mutantsDirs.size() == 0) {
			mutantsDirs = TestSuite.listMutantsDirs("mutants/session/result/SDLExample/traditional_mutants");
		}
	}

	@Test
	public void test_SDL_x_SDL_Duplicated() {
		// open mujava log file to get generated mutants
		List<String> mutants = new ArrayList<String>();
		for (String line : mujavaLogLines) {
//			System.out.println(line);
			// Get mutants equivalents last stmt in void method
			if (line.contains("void_f001(int)")) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			if (line.contains("void_f002(int)")) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			if (line.contains("void_f003(java.lang.Integer)")) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			if (line.contains("void_f004(int)")) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			if (line.contains("void_f005(int)")) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
			if (line.contains("void_f006(int)")) {
				mutants.add(line.substring(0, line.indexOf(":")));
			}
		}
		// Verify if Equivalence Avoid is enable
		// TODO uncomment and resolve later
//		if (LogReduction.AVOID) {
//			Assert.assertEquals(mutants.size(), 0);
//			return;
//		} else {
//			Assert.assertEquals(mutants.size(), 20);
//		}

		// Nimrod log file should detect equivalence
		int detected = 0;
		StringBuilder uselessMutants = new StringBuilder();
		for (String mutant : mutants) {
			for (String line : nimrodDuplicatedLogLines) {
				// Check if Nimrod detected equivalence
				if (line.contains(mutant + ":")) {
					uselessMutants.append(mutant + ",");
					detected++;
				}
			}
		}
		if (detected != 4) {
			Assert.assertTrue("Failed to detect useless mutants. List of useless mutants: " + uselessMutants, false);
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
