package mujava;

import mujava.op.basic.ROR;
import mujava.op.util.BetterCodeChangeLog;
import mujava.op.util.MutantCodeWriter;
import mujava.util.Debug;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.ParseTreeException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class RORMockMutationSystem extends AllMutantsGenerator {
	static String[] mm = new String[0];
	static String[] cOP = {"ROR"};
	public RORMockMutationSystem(File f) {
		super(f, mm,cOP);
		Debug.setDebugLevel(2);
		String class_name = f.getName();
		class_name = class_name.substring(0,class_name.indexOf('.'));
		MutationSystem.CLASS_NAME = class_name;
	}

	@Override
	public boolean makeMutants() throws OpenJavaException {
		Debug.print("-------------------------------------------------------\n");
		Debug.print("* Generating parse tree. \n");

		generateParseTree();
		Debug.print("..done. \n");
		// System.out.println("0");
		Debug.print("* Initializing parse tree. \n");
		initParseTree();
		Debug.print("..done. \n");
		// System.out.println("1");
		Debug.print("* Generating Mutants \n");
		genMutants();
		//arrangeOriginal
		ClassDeclarationList cdecls = comp_unit.getClassDeclarations();
		for (int j = 0; j < cdecls.size(); ++j) {
			ClassDeclaration cdecl = cdecls.get(j);
			File outfile = null;
			try {
				UUID uuid_filename = UUID.randomUUID();
				outfile =  File.createTempFile("ROR"+ uuid_filename.toString(), ".java");
				FileWriter fout = new FileWriter(outfile);
				PrintWriter out = new PrintWriter(System.out);
				PrintWriter pfout = new PrintWriter(fout);
				MutantCodeWriter stdoutwriter = new MutantCodeWriter(out);
				MutantCodeWriter writer = new MutantCodeWriter(pfout);
				writer.setClassName(cdecl.getName());
				stdoutwriter.setClassName(cdecl.getName());
				comp_unit.accept(writer);
				comp_unit.accept(stdoutwriter);
				out.flush();
				out.close();
			} catch (IOException e) {
				System.err.println("fails to create " + outfile);
			} catch (ParseTreeException e) {
				System.err.println("errors during printing " + outfile);
				e.printStackTrace();
			}
		}
		Debug.print("..done.\n");
		Debug.flush();
		return true;
	}

	@Override
	void genMutants()
	{
		if (comp_unit == null)
		{
			System.err.println(original_file + " is skipped.");
		}
		ClassDeclarationList cdecls = comp_unit.getClassDeclarations();
		if (cdecls == null || cdecls.size() == 0)
			return;

		Debug.println("* Generating traditional mutants");
		MutationSystem.clearPreviousTraditionalMutants();
		MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;
		BetterCodeChangeLog.openLogFile("ROR");
		for(int j=0; j<cdecls.size(); ++j)
		{
			ClassDeclaration cdecl = cdecls.get(j);
			String name = cdecl.getName();
			if (cdecl.getName().equals(MutationSystem.CLASS_NAME))
			{
				try
				{
					mujava.op.util.Mutator mutant_op;
					if (hasOperator(traditionalOp, "ROR"))
					{
						Debug.println("  Applying ROR ... ... ");
						mutant_op = new ROR(file_env, cdecl, comp_unit);
						comp_unit.accept(mutant_op);
					}
				} catch (ParseTreeException e)
				{
					System.err.println( "Exception, during generating traditional mutants for the class "
							+ MutationSystem.CLASS_NAME);
					e.printStackTrace();
				}
			}
		}

		BetterCodeChangeLog.closeLogFile();
	}
}
