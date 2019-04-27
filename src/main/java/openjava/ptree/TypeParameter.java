/*
 * TypeName.java 1.0
 *
 * This interface is made to type ptree-node into the type
 * specifier in the body of class.
 *
 * Jun 20, 1997 by mich
 * Sep 29, 1997 by bv
 * Oct 11, 1997 by mich
 * Dec 27, 1998 by mich	
 *
 * @see openjava.ptree.ParseTree
 * @version 1.0 last updated:  Oct 11, 1997
 * @author  Michiaki Tatsubori
 */
package openjava.ptree;

import openjava.ptree.util.ParseTreeVisitor;

/**
 * The <code>TypeName</code> class represents a type specifier
 * node of parse tree.
 *
 * @see openjava.ptree.ParseTree
 * @see openjava.ptree.NonLeaf
 */
public class TypeParameter extends NonLeaf implements ParseTree{

	public TypeParameter(String identifier, String typeBounds) {
		super();
		set(identifier, typeBounds);	
	}

	TypeParameter() {
		super();
	}

	/**
	 * Get the identifier of this type parameter.
	 *
	 * @return  the type name.
	 */
	public String getName() {
		return (String) elementAt(0);
	}	

	/**
	 * Set the identifier of this type parameter.
	 *
	 * @param name the type name to set.
	 */
	public void setName(String name) {
		setElementAt(name, 0);
	}
	
	/**
	 * get the type bound of this type parameter
	 * 
	 * @return
	 */
	public String getTypeBound(){
		return (String) elementAt(1);
	}
	
	/**
	 * set the type bound of this type parameter
	 * 
	 * @param typeBounds
	 */
	public void setTypeBounds(String typeBounds){
		setElementAt(typeBounds, 1);
	}
	
	public String toString(){
		if(getTypeBound() == "")
			return getName();
		else
			return getName() + " extends " + getTypeBound();
	}

	public void accept(ParseTreeVisitor v) throws ParseTreeException {
		v.visit(this);
	}

}
