package openjava.ptree;

import openjava.ptree.util.ParseTreeVisitor;

public class EnumDeclaration extends NonLeaf implements MemberDeclaration{

	/**
	 * Allocates a new EnumDeclaration object.
	 *
	 */
	EnumDeclaration() {
		super();
	}

	/**
	 * An enumeration declaration
	 * @param modifier
	 * @param identifier
	 * @param tn
	 * @param enumConstantsList
	 * @param mdl
	 */
	public EnumDeclaration(ModifierList modifiers, String identifier, TypeName [] tn, EnumConstantList enumConstantsList, MemberDeclarationList mdl) {
		super();
		set(modifiers, identifier, tn, enumConstantsList, mdl);
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
     * set the modifier list
     * 
     */
    public void setModifiers(ModifierList modifiers) {
    	setElementAt(modifiers, 0);
    }
    
	/**
     * Get the identifier of an Enumeration Constant
     * 
     */
    public String getName() {
        return (String) elementAt(1);
    }
    
	/**
     * Get the implementsList of an Enumeration Constant
     * 
     */
    public TypeName[] getImplementsList() {
        return (TypeName[]) elementAt(2);
    }
    
	/**
     * Get the list of Enumeration Constants
     * 
     */
    public EnumConstantList getEnumConstantList() {
        return (EnumConstantList) elementAt(3);
    }
    
	/**
     * Get the an inner class 
     * 
     */
    public MemberDeclarationList getClassBodayDeclaration() {
        return (MemberDeclarationList) elementAt(4);
    }
	
	@Override
	public void accept(ParseTreeVisitor visitor) throws ParseTreeException {
		visitor.visit(this);
		
	}

}
