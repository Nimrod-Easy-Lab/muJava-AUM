package openjava.ptree;

import openjava.ptree.util.ParseTreeVisitor;

public class EnumConstant extends NonLeaf implements ParseTree{

	/**
	 * Allocates a new EnumDeclaration object.
	 *
	 */
	EnumConstant() {
		super();
	}

	/**
	 * An enumeration constant
	 * 
	 * The format is: (Modifier())* Identifier() [Arguments() ] [ClassBody()]
	 * 
	 * @param modifiers
	 * @param identifier
	 * @param exprList
	 * @param membDeclaList
	 */
	public EnumConstant(
		ModifierList modifiers, String identifier, ExpressionList exprList, MemberDeclarationList membDeclaList, String enumType) {
		
		super();
		set(modifiers, identifier, exprList, membDeclaList, enumType);
	}
	
	/**
     * Get the modifier list
     * 
     * @return there is no modifiers, getModifierList returns an empty list.
     */
    public ModifierList getModifiers() {
        return (ModifierList) elementAt(0);
    }
    
	/**
     * Set the modifier list
     */
    public void setModifiers(ModifierList modifiers) {
        setElementAt(modifiers,0);
    }
    
	/**
     * Get the name of an Enumeration Constant
     * 
     */
    public String getName() {
        return (String) elementAt(1);
    }
    
	/**
     * Set the name of an Enumeration Constant
     */
    public void setName(String identifier) {
        setElementAt(identifier,1);
    }
    
	/**
     * Get the arguments of an Enumeration Constant
     * 
     */
    public ExpressionList getArguments() {
        return (ExpressionList) elementAt(2);
    }
    
	/**
     * Get the class body of an Enumeration Constant
     * 
     */
    public MemberDeclarationList getClassBody() {
        return (MemberDeclarationList) elementAt(3);
    }
    
	/**
     * Get the type of an Enumeration Constant
     * 
     */
    public String getEnumType() {
        return (String) elementAt(4);
    }
    
	/**
     * Set the type of an Enumeration Constant
     */
    public void setEnumType(String enumType) {
        setElementAt(enumType,4);
    }

	
	@Override
	public void accept(ParseTreeVisitor visitor) throws ParseTreeException {
		visitor.visit(this);
		
	}

}
