package mujava.op.basic;

import org.junit.Assert;
import org.junit.Test;

//@RunWith(Suite.class)
//@SuiteClasses({AOISTest.class, SDLTest.class, CODTest.class, LOITest.class, ODLTest.class})
public class TestSuite {
    @Test
    private void dummy_test() {
        Assert.assertEquals(true,true);
    }
	/*
	
	public static final String PATH_TO_RESOURCES = "src/test/resources";

    @BeforeClass
    public static void setUp() {
        System.out.println("setting up");
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("tearing down");
    }

	static void setMutationSystemPathFor(String file_name) {
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
	
			String mutant_dir_path = MutationSystem.MUTANT_HOME + "/" + temp;
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
			MutationSystem.DIR_NAME = temp;
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	static void makeMujavaConfigFile(String output) throws IOException {
		File file = new File("src/test/resources/mujava.config");
		FileWriter fw = new FileWriter(file);
		fw.write("MuJava_HOME=" + output);
		fw.close();
	
		file = new File("src/test/resources/mujavaCLI.config");
		fw = new FileWriter(file);
		fw.write("MuJava_HOME=" + output);
		fw.close();
	}

	public static List<String> readLogFile(String path) {
		// open mujava log file
		try {
			File file = new File(PATH_TO_RESOURCES + path);
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			// for (String line : lines) {
			// System.out.println(line);
			// }
			return lines;
		} catch (IOException e) {
			Assert.assertTrue(false);
			return new ArrayList<String>();
		}
	}

	public static List<File> listMutantsDirs(String path) {
		// Confirmar que o mutante foi gerado fazendo um LS na pasta
		File dir = new File(PATH_TO_RESOURCES + path);
		String[] extensions = new String[] { "class" };
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		// for (File file : files) {
		// System.out.println("folder: " + file.getParentFile().getName());
		// }
		return files;
	}

	
*/
}