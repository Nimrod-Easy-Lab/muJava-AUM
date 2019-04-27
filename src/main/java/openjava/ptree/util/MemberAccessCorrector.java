/*
 * MemberAccessCorrector.java
 *
 * Firstly, the parser generates a temporal parse tree.
 * This class correct them.
 * <p>
 *
 * All the continuous field access are stored in a single Variable ptree
 * object.
 * [p.p.f.f].f  [p.f].m()  ([] a single Variable object)
 * FieldAccess := Variable name
 * MemberAccess := Variable name "(" .. ")"
 *
 * @author   Michiaki Tatsubori
 * @version  %VERSION% %DATE%
 * @see      java.lang.Object
 *
 * COPYRIGHT 1998 by Michiaki Tatsubori, ALL RIGHTS RESERVED.
 */
package openjava.ptree.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

import openjava.mop.AnonymousClassEnvironment;
import openjava.mop.Environment;
import openjava.mop.FileEnvironment;
import openjava.mop.GlobalEnvironment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.OJField;
import openjava.ptree.Expression;
import openjava.ptree.FieldAccess;
import openjava.ptree.MethodCall;
import openjava.ptree.ParseTreeException;
import openjava.ptree.TypeName;
import openjava.ptree.Variable;
import openjava.tools.DebugOut;

/**
 * The class <code>MemberAccessCorrector</code>
 * <p>
 * For example
 * <pre>
 * </pre>
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: MemberAccessCorrector.java,v 1.2 2003/02/19 02:55:00 tatsubori Exp $
 * @see java.lang.Object
 */
public class MemberAccessCorrector extends VariableBinder {

	private String errorState = null;

	public MemberAccessCorrector(Environment env) {
		super(env);
	}

	public String getErrorState() {
		return errorState;
	}

	public Expression evaluateDown(FieldAccess ptree)
		throws ParseTreeException {
		super.evaluateDown(ptree);

		if (ptree.getReferenceType() != null)
			return ptree;
		//System.out.println("MemberAccessCorrector FieldAccess: ptree " + ptree);
		Expression ref = ptree.getReferenceExpr();
		String field_name = ptree.getName();
		//System.out.println("MemberAccessCorrector FieldAccess: ref " + ref + "; field_name: "+ field_name + "; VARIABLE: " +(ref instanceof Variable));
		if (ref == null) {
			if (isVariable(field_name)) {
				/* this is a variable. */
				DebugOut.println("MC variable - " + field_name);
				return new Variable(field_name);
			} else if (isField(field_name)) {
				/* this is a field access */
				DebugOut.println("MC field access - " + field_name);
			} else if (getEnvironment().getImportedClasses() != null || getEnvironment().getImportedPackages() != null){
				/* if the FieldAccess is not null, a static field from imported classes or packages is used */
				FieldAccess fa = returnStaticFieldAccess(field_name);
				if(fa != null)
					ptree.setReferenceType(fa.getReferenceType());
				//System.out.println("MemberAccessCorrector FieldAccess: ptree getReferenceType: " + ptree.getReferenceType());
			} else {
				/* unknown variable or field */
				System.err.println("unknown field or variable : " + field_name);
				System.err.println(getEnvironment());
			}
		} else if (ref instanceof Variable) {
			FieldAccess fa = name2fieldaccess(ref.toString(), field_name);
			TypeName typename = fa.getReferenceType();
			Expression refexpr = fa.getReferenceExpr();
			//System.out.println("MemberAccessCorrector FieldAccess: refexpr " + refexpr + "; typename: "+ typename);
			if (typename != null) {
				ptree.setReferenceType(typename);
			} else {
				ptree.setReferenceExpr(refexpr);
			}
		}

		return ptree;
	}

	public Expression evaluateDown(MethodCall ptree)
		throws ParseTreeException {
		super.evaluateDown(ptree);
		//System.out.println("MemberAccessCorrector MethodCall: ptree " + ptree + "; name: " + ptree.getName() + "; type: " + ptree.getReferenceType() + "; " );
		if (ptree.getReferenceType() != null){
			return ptree;
		}

		Expression ref = ptree.getReferenceExpr();
		//System.out.println("MemberAccessCorrector MethodCall: ref instanceof Variable " + (ref instanceof Variable));
		if (ref == null || !(ref instanceof Variable)){
			/**
			 * check if this is a static imported method
			 */
			if(getEnvironment().getImportedClasses() != null || getEnvironment().getImportedPackages() != null){
				MethodCall mc = returnStaticMethodCall(ptree.getName(), ptree);
				if(mc != null)
					ptree.setReferenceType(mc.getReferenceType());
				//System.out.println("MemberAccessCorrector MethodCall: ptree getReferenceType: " + ptree.getReferenceType());
			}
			return ptree;
		}
		String method_name = ptree.getName();
		
		if (ref instanceof Variable) {
			FieldAccess fa = name2fieldaccess(ref.toString(), method_name);
			TypeName typename = fa.getReferenceType();
			Expression refexpr = fa.getReferenceExpr();
			//System.out.println("MemberAccessCorrector MethodCall: typename " + typename + "; refexpr: " + refexpr);
			if (typename != null) {
				ptree.setReferenceType(typename);
			} else {
				ptree.setReferenceExpr(refexpr);
			}
		}

				
		return ptree;
	}

	private FieldAccess name2fieldaccess(String names, String field) {
		Expression result_expr;
		String first = getFirst(names);
		String rest = getRest(names);

		if (isVariable(first)) {
			/* this is a variable  */
			DebugOut.println("MC variable - " + first);
			result_expr = new Variable(first);
		} else if (isField(first)) {
			/* this is a field  */
			DebugOut.println("MC field - " + first);
			result_expr = new FieldAccess((Variable) null, first);
		} else {
			/* this is a class */
			while (rest != null && !isClass(first)) {
				first = first + "." + getFirst(rest);
				rest = getRest(rest);
			}
			while (isClass(first + "." + getFirst(rest))) {
				first = first + "." + getFirst(rest);
				rest = getRest(rest);
			}
			if (isClass(first)) {
				DebugOut.println("MC class - " + first);
			} else 
			{
				System.err.println("unknown class : " + first);
			}

			TypeName type = new TypeName(first);
			if (rest == null) {
				/* ref is a typename */
				return new FieldAccess(type, field);
			}
			first = getFirst(rest);
			rest = getRest(rest);
			result_expr = new FieldAccess(type, first);
		}

		/* remainder is field access */
		while (rest != null) {
			first = getFirst(rest);
			rest = getRest(rest);
			result_expr = new FieldAccess(result_expr, first);
		}

		return new FieldAccess(result_expr, field);
	}

	private boolean isVariable(String name) {
		Environment env = getEnvironment();
		OJClass bindedtype = env.lookupBind(name);
		//System.out.println("MemberAccessCorrecotr: name to read: " + name + "; return: " + bindedtype);
		//System.out.println("MemberAccessCorrecotr: " + env.toString());
		return (bindedtype != null);
	}

	private boolean isField(String name) {
		Environment env = getEnvironment();
		String qcname = env.toQualifiedName(env.currentClassName());
		//System.out.println("MemberAccessCoorector isField: env.currentClassName " + env.currentClassName() + " "+ (env instanceof AnonymousClassEnvironment));
		
		if(qcname.indexOf("anonymous class") >= 0){
			Environment envCopy = getEnvironment();
			if(envCopy instanceof AnonymousClassEnvironment){
				boolean result = ((AnonymousClassEnvironment)envCopy).isField(name);
				if(result == true)
					return true;
			}
			
			do{
				Environment tempEnv = envCopy.getParentEnvironment();
				
				if(tempEnv instanceof AnonymousClassEnvironment){
					boolean result = ((AnonymousClassEnvironment)tempEnv).isField(name);
					if(result == true)
						return true;
				}
				envCopy = tempEnv;
			}while(!(envCopy instanceof FileEnvironment));
			
		}
		
		OJClass declarer = env.lookupClass(qcname);
		OJField field = null;
		while (declarer != null && field == null) {
			try {
				field = declarer.getField(name, declarer);
			} 
			catch (NoSuchMemberException e) {
			}
			declarer = declarer.getDeclaringClass();
		}
		return (field != null);
	}
	
	private FieldAccess returnStaticFieldAccess(String name)  {
		FieldAccess fa = null;
		Environment env = getEnvironment();
		Vector importedClasses = env.getImportedClasses();
		for(int i = 0; i < importedClasses.size();i++){
			String s = (String)importedClasses.get(i);
			if(s.indexOf("static") >= 0){				
				//System.out.println("MemberAccessCoorector classname: getStaticClass " + getStaticClass(s));
				fa = findImportedField(getStaticClass(s), name);
				if(fa != null)
					return fa;
			}
		}
		
		Vector importedPackages = env.getImportedPackages();
		for(int i = 0; i < importedPackages.size();i++){
			String s = (String)importedPackages.get(i);			
			if(s.indexOf("static") >= 0){			
				//System.out.println("MemberAccessCoorector : importedPackages " + s);
				return findImportedField(getStaticPackage(s), name);
			}
		}
		return fa;
	}
	
	private MethodCall returnStaticMethodCall(String name, MethodCall ptree)  {
		MethodCall mc = null;
		Environment env = getEnvironment();
		Vector importedClasses = env.getImportedClasses();
		for(int i = 0; i < importedClasses.size();i++){
			String s = (String)importedClasses.get(i);
			if(s.indexOf("static") >= 0){				
				//System.out.println("MemberAccessCoorector classname: getStaticClass " + getStaticClass(s));
				mc = findImportedMethodCall(getStaticClass(s), name,  ptree);
				if(mc != null)
					return mc;
			}
		}
		
		Vector importedPackages = env.getImportedPackages();
		for(int i = 0; i < importedPackages.size();i++){
			String s = (String)importedPackages.get(i);			
			if(s.indexOf("static") >= 0){			
				//System.out.println("MemberAccessCoorector : importedPackages " + s);
				mc = findImportedMethodCall(getStaticPackage(s), name, ptree);
				if(mc != null)
					return mc;
			}
		}
		return mc;
	}
	
	private String getStaticClass(String className){
		String[] s = className.split(" ");	
		int index = s[1].lastIndexOf(".");
		return s[1].substring(0, index);
	}
	
	private String getStaticPackage(String className){
		String[] s = className.split(" ");	
		return s[1];
	}
	
	private FieldAccess findImportedField(String referencedType, String name){
		FieldAccess fa = null;
		Class c = null;
		try {
			c = Class.forName(referencedType);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(c != null){
			Field[] fields = c.getFields();
			for(Field f: fields){
				//System.out.println("MemberAccessCoorector classname: " + f.getName() + (f.getName().equals(name)));
				if(f.getName().equals(name)){
					fa = new FieldAccess(new TypeName(referencedType), name);
					return fa;
				}
				
			}
		}
		return fa;
	}
	
	private MethodCall findImportedMethodCall(String referencedType, String name, MethodCall ptree){
		//System.out.println("MemberAccessCoorector findImportedMethodCall: " + referencedType + "; "+ name);
		MethodCall mc = null;
		Class c = null;
		try {
			c = Class.forName(referencedType);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("MemberAccessCoorector findImportedMethodCall: c != null" + (c != null));
		if(c != null){
			Method[] methods = c.getMethods();
			for(Method m: methods){
				//System.out.println("MemberAccessCoorector method: " + m.getName() + (m.getName().equals(name)));
				if(m.getName().equals(name)){
					mc = new MethodCall(new TypeName(referencedType), name, ptree.getArguments());
					return mc;
				}
				
			}
		}
		return mc;
	}

	private boolean isClass(String name) {
		Environment env = getEnvironment();
				
		String qname = env.toQualifiedName(name);
		//System.out.println("MemberAccessCorrector: isClass: " + name + "; " + qname);
		try {
			OJClass.forName(qname);
			return true;
		} catch (OJClassNotFoundException e) {
		}
		OJClass clazz = env.lookupClass(qname);
		
		return (clazz != null);
	}

	private static final String getFirst(String qname) {
		if (qname == null)
			return null;
		int dot = qname.indexOf('.');
		if (dot == -1)
			return qname;
		return qname.substring(0, dot);
	}

	private static final String getRest(String qname) {
		if (qname == null)
			return null;
		int dot = qname.indexOf('.');
		if (dot == -1)
			return null;
		return qname.substring(dot + 1);
	}

}
