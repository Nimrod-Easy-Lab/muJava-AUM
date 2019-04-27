/*
 * MethodDeclaration.java 1.0
 *
 * Jun 20, 1997 by mich
 * Sep 29, 1997 by bv
 * Oct 10, 1997 by mich
 * Jul 29, 1998 by mich, Fixed makeCopy() which crushed.
 *
 * @see openjava.ptree.ParseTree
 * @version 1.0 last updated:  Oct 10, 1997
 * @author  Michiaki Tatsubori
 */
package openjava.ptree;

import java.util.Hashtable;

import openjava.ptree.util.ParseTreeVisitor;

/**
 * The MethodDeclaration class presents method declaration node
 * of parse tree.
 *
 * @see openjava.ptree.ParseTree
 * @see openjava.ptree.NonLeaf
 * @see openjava.ptree.FieldDeclaration
 */
public class MethodDeclaration extends NonLeaf implements MemberDeclaration {

	private Hashtable suffixes = null;
	private String genericsTypeParameters = "";

	/**
	 * Constructs new MethodDeclaration from its elements.
	 * 
	 * @param  modiflist  modifier list. If it has no modifier list
	 *                    then thes arg is set empty list.
	 * @param  typespec  returning type specifier 
	 * @param  methoddecl  method declarator
	 * @param  throwlist  throw type list. If there is no throws
	 *                    then this arg is set empty list
	 * @param  block  method block. if arg block is null, it means method
	 *                body with only semi colon such as methods in interface
	 */
	public MethodDeclaration(
		ModifierList modiflist,
		TypeName typespec,
		String name,
		ParameterList params,
		TypeName[] throwlist,
		StatementList block,
		TypeParameterList genericsTypeParameters) {
		super();
		if (params == null)
			params = new ParameterList();
		if (throwlist == null)
			throwlist = new TypeName[0];
		set(modiflist, genericsTypeParameters, typespec, name, params, throwlist, block);
		//set(modiflist, typespec, name, params, throwlist, block);
		if(genericsTypeParameters != null)
			this.genericsTypeParameters = genericsTypeParameters.toString();
	}

	/**
	 * Is needed for recursive copy.
	 */
	MethodDeclaration() {
		super();
	}

	/**
	 * Gets modifierlist of this method.
	 *
	 * @return  modifier list. Even if there is no modifiers, getModifiers
	 *          returns empty list
	 */
	public ModifierList getModifiers() {
		return (ModifierList) elementAt(0);
	}

	/**
	 * Sets modifierlist of this method.
	 *
	 * @param  modifs  modifier list to set
	 */
	public void setModifiers(ModifierList modifs) {
		setElementAt(modifs, 0);
	}

	/**
	 * Gets type specifier of this method.
	 *
	 * @return  type specifier node
	 */
	public TypeName getReturnType() {
		return (TypeName) elementAt(2);
	}

	/**
	 * Sets type specifier of this method.
	 *
	 * @param  tspec  type specifier to set
	 */
	public void setReturnType(TypeName tspec) {
		setElementAt(tspec, 2);
	}

	/**
	 * Gets name of this method.
	 *
	 * @return method declarator node
	 */
	public String getName() {
		return (String) elementAt(3);
	}

	/**
	 * Sets name of this method.
	 *
	 * @param  name  method's name
	 */
	public void setName(String name) {
		setElementAt(name, 3);
	}

	/**
	 * Gets parameter list of this method.
	 * Even if this method has no parameter, this returns
	 * an empty list of parameter.
	 *
	 * @return  method's name
	 */
	public ParameterList getParameters() {
		return (ParameterList) elementAt(4);
	}

	/**
	 * Sets parameter list of this method.
	 *
	 * @param  params  parameter list to set
	 */
	public void setParameters(ParameterList params) {
		setElementAt(params, 4);
	}

	/**
	 * Gets throw type name list of this method.
	 * Even if there is no throws, this returns an empty list.
	 *
	 * @return  class type list
	 */
	public TypeName[] getThrows() {
		return (TypeName[]) elementAt(5);
	}

	/**
	 * Sets throw type name list of this method.
	 *
	 * @param  class type list to set
	 */
	public void setThrows(TypeName[] thrwlist) {
		setElementAt(thrwlist, 5);
	}

	/**
	 * Gets body of this method.
	 * If the body is only semi colon such as the methods' body of interface,
	 * this returns null.
	 *
	 * @return  statement list
	 */
	public StatementList getBody() {
		return (StatementList) elementAt(6);
	}

	/**
	 * Sets body of this method.
	 *
	 * @param  stmts  statement list to set
	 */
	public void setBody(StatementList stmts) {
		setElementAt(stmts, 6);
	}
	
	/**
	 * Get TypeParameterList of this method.
	 * If the method does not any type parameters, it returns null.
	 *
	 * @return  TypeParameterList list
	 */
	public TypeParameterList getTypeParameterList() {
		return (TypeParameterList) elementAt(1);
	}

	/**
	 * Set TypeParameterList of this method.
	 *
	 * @param  tpl TypeParameterList
	 */
	public void setTypeParameterList(TypeParameterList tpl) {
		setElementAt(tpl, 1);
	}

	public void setSuffixes(Hashtable suffixes) {
		this.suffixes = suffixes;
	}

	public String getGenericsTypeParameters() {
		return this.genericsTypeParameters;
	}
	
	public void setGenericsTypeParameters(String genericsTypeParameters) {
		this.genericsTypeParameters = genericsTypeParameters;
	}

	public Hashtable getSuffixes() {
		return this.suffixes;
	}

	public void accept(ParseTreeVisitor v) throws ParseTreeException {
		v.visit(this);
	}
}
