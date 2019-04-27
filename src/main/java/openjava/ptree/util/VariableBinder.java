/*
 * VariableBinder.java
 *
 * comments here.
 *
 * @author   Michiaki Tatsubori
 * @version  %VERSION% %DATE%
 * @see      java.lang.Object
 *
 * COPYRIGHT 1998 by Michiaki Tatsubori, ALL RIGHTS RESERVED.
 */
package openjava.ptree.util;

import java.io.File;

import openjava.mop.Environment;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.Toolbox;
import openjava.ptree.EnumConstant;
import openjava.ptree.EnumConstantList;
import openjava.ptree.EnumDeclaration;
import openjava.ptree.ForStatement;
import openjava.ptree.MemberDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.TypeName;
import openjava.ptree.TypeParameter;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;
import openjava.tools.DebugOut;
import openjava.tools.parser.Parser;

/**
 * The class <code>VariableBinder</code>
 * <p>
 * For example
 * <pre>
 * </pre>
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: VariableBinder.java,v 1.2 2003/02/19 02:55:00 tatsubori Exp $
 * @see java.lang.Object
 */
public class VariableBinder extends ScopeHandler {

	public VariableBinder(Environment env) {
		super(env);
	}

	public Statement evaluateDown(VariableDeclaration ptree)
		throws ParseTreeException {

		super.evaluateDown(ptree);
		bindLocalVariable(ptree, getEnvironment());

		return ptree;
	}
	
	public Statement evaluateDown(ForStatement ptree)
		throws ParseTreeException {
		
		super.evaluateDown(ptree);
		TypeName tspec = ptree.getInitDeclType();

		if (tspec == null)
			return ptree;
		
		VariableDeclarator[] vdecls = ptree.getInitDecls();
		if(vdecls != null)
			bindForInit(tspec, vdecls, getEnvironment());
		else{
			//if this is an enhanced for statement, there is no variable declarator
			String identifier = ptree.getIdentifier();
			bindName(getEnvironment(), Toolbox.nameToJavaClassName(tspec.toString()), identifier);
		}

		return ptree;
	}

	public Parameter evaluateDown(Parameter ptree) throws ParseTreeException {
		super.evaluateDown(ptree);

		bindParameter(ptree, getEnvironment());

		return ptree;
	}

	private static void bindLocalVariable(
		VariableDeclaration var_decl,
		Environment env) {
		String type = var_decl.getTypeSpecifier().toString();
		String name = var_decl.getVariable();
		
		bindName(env, type, name);
	}

	private static void bindForInit(
		TypeName tspec,
		VariableDeclarator[] vdecls,
		Environment env) {
		//If the for statement is a enhanced one, variable declarator will be null
		//so the statements below will only be executed for the traditional for statements
		if(vdecls != null){
			for (int i = 0; i < vdecls.length; ++i) {
				String type = tspec.toString() + vdecls[i].dimensionString();
				String name = vdecls[i].getVariable();
				bindName(env, type, name);
			}
		}
	}

	private static void bindParameter(Parameter param, Environment env) {
		String type = "";
		String name = param.getVariable();
		if(param.isVarargs() == false){
			type = param.getTypeSpecifier().toString();			
		}
		else{
			type = param.getTypeSpecifier().toString() + "[]";
		}
		//System.out.println("VariableBinder: Parameter " + type + " " + name);
		
		bindName(env, type, name);
	}
	public TypeParameter evaluateDown(TypeParameter ptree) throws ParseTreeException {
		super.evaluateDown(ptree);
		
		String identifier = ptree.getName();
		if(ptree.getTypeBound() != ""){			
			String[] types = ptree.getTypeBound().split("&");

			//System.out.println("identifier: " + identifier);
			//System.out.println("type: " + type);
			for(String type: types)
				record(getEnvironment(), type, identifier);
		}
		else{
			OJClass OBJECT = OJClass.forClass(Object.class);

			//getEnvironment().record(identifier, OBJECT);
			getEnvironment().recordGenerics(identifier, OBJECT);

			//System.out.println("env: " + getEnvironment().toString());
		}
		return ptree;
	}
	
	private static void record(Environment env, String type, String name) {
		
		String qtypename = env.toQualifiedName(type);
		//System.out.println("qtypename: " + type);
		try {
			OJClass clazz = env.lookupClass(qtypename);
			if (clazz == null)
				clazz = OJClass.forName(qtypename);
			//System.out.println("OJClass: " + name + " " + clazz);
			env.record(name, clazz);
			//System.out.println("env: " + env.toString());
			DebugOut.println("record\t" + name + "\t: " + qtypename);
		} catch (OJClassNotFoundException e) {
			System.err.println(
				"VariableBinder.record() "
					+ e.toString()
					+ " : "
					+ qtypename);
			System.err.println(env);
		}
	}
	
	private static void bindName(Environment env, String type, String name) {
		//System.out.println("before bindName: " + name+ ": " + type);
		String qtypename = env.toQualifiedName(type);
		//System.out.println("bindName: " + name+ ": " + qtypename);
		try {
			OJClass clazz = env.lookupClass(qtypename);
			//System.out.println("OJClass: clazz==null" + clazz==null );
			if (clazz == null)
				clazz = OJClass.forName(qtypename, env);
			//System.out.println("OJClass: " + name + " " + clazz);
			env.bindVariable(name, clazz);
			//System.out.println("VariableBinder: " + env.toString());
			DebugOut.println("binds variable\t" + name + "\t: " + qtypename);
		} catch (OJClassNotFoundException e) {
			System.err.println(
				"VariableBinder.bindName() "
					+ e.toString()
					+ " : "
					+ qtypename);
			System.err.println(env);
		}
	}

}
