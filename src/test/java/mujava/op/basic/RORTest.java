package mujava.op.basic;

import mujava.AllMutantsGenerator;
import mujava.MutationSystem;
import mujava.OpenJavaException;
import mujava.RORMockMutationSystem;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class RORTest {


	@Test
	public void test_erule13() throws OpenJavaException {
		try {
			MutationSystem.setJMutationStructureFromFilePath("/home/pedro/Documents/" +
					"Shared/GitHub/muJava-AUM/mujava/examples/session3");
			MutationSystem.recordInheritanceRelation();

			File original = new File("/home/pedro/Documents/Shared/" +
					"GitHub/muJava-AUM/mujava/examples/session3");
			AllMutantsGenerator amg = new AllMutantsGenerator(original,
					new String[0],new String[]{"ROR"});
			amg.makeMutants();
			amg.compileMutants();
		} catch (FileNotFoundException e) {
			fail(e.getMessage());

		} catch (URISyntaxException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(true,true);
	}
}