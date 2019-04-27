/*
 * ClassDeclaration.java 1.0
 *
 *
 * Jun 20, 1997 by mich
 * Sep 29, 1997 by bv
 * Oct 10, 1997 by mich
 *
 * @see openjava.ptree.ParseTree
 * @version 1.0 last updated:  Oct 10, 1997
 * @author  Michiaki Tatsubori
 */
package openjava.ptree;

import java.util.Hashtable;

import openjava.ptree.util.ParseTreeVisitor;

/**
 * The ClassDeclaration class presents class declaraton node of parse tree.
 * 
 * @see openjava.ptree.ParseTree
 * @see openjava.ptree.NonLeaf
 * @see openjava.ptree.TypeDeclaration
 */
public class ClassDeclaration extends NonLeaf implements Statement, MemberDeclaration, ParseTree {
    private String[] metaclazz = null;
    private Hashtable suffixes = null;
    private boolean _isInterface = false;
    private boolean _isEnum = false;

    /**
     * Constructs ClassDeclaration from its elements.
     * 
     * @param modiflist
     *            modifier list
     * @param name
     *            class name
     * @param zuper
     *            arg zuper is null means class decl has no super class
     * @param interfacelist
     *            if class decl has no implemants, arg interfacelist should be
     *            set an empty list
     * @param fieldlist
     *            field declaration list (body of new class)
     */
    public ClassDeclaration(ModifierList modiflist, String name, TypeParameterList tpl, TypeName[] baseclasses, TypeName[] ifaces,
            MemberDeclarationList fieldlist) {
        this(modiflist, name, tpl, baseclasses, ifaces, fieldlist, true);
    }

    public ClassDeclaration(ModifierList modiflist, String name, TypeParameterList tpl, TypeName[] baseclasses, TypeName[] ifaces,
            MemberDeclarationList fieldlist, boolean is_class) {
        super();
        baseclasses = (baseclasses == null) ? new TypeName[0] : baseclasses;
        ifaces = (ifaces == null) ? new TypeName[0] : ifaces;
        set(modiflist, name, tpl, baseclasses, ifaces, fieldlist);
        this._isInterface = (!is_class);
    }
    
    /**
     * A class constructor for an enumeration declaration
     * @param enumDeclaration
     */
    public ClassDeclaration(MemberDeclaration enumDeclaration){
    	super();
    	set_isEnum(true);
    	TypeName[] ifaces = (((EnumDeclaration )enumDeclaration).getImplementsList() == null) ? new TypeName[0] : ((EnumDeclaration )enumDeclaration).getImplementsList();
    	set(((EnumDeclaration )enumDeclaration).getModifiers(), ((EnumDeclaration )enumDeclaration).getName(), null, new TypeName[0], ifaces, ((EnumDeclaration )enumDeclaration).getClassBodayDeclaration(), false, ((EnumDeclaration )enumDeclaration).getEnumConstantList());
    }
    
    
    public boolean isInterface() {
        return this._isInterface;
    }

    public void beInterface(boolean isInterface) {
        this._isInterface = isInterface;
    }

    /**
     * Gets modifier list
     * 
     * @return there is no modifiers, getModifierList returns an empty list.
     */
    public ModifierList getModifiers() {
        return (ModifierList) elementAt(0);
    }

    /**
     * Sets modifier list
     * 
     * @param modifs
     *            modifiers to set
     */
    public void setModifiers(ModifierList modifs) {
        setElementAt(modifs, 0);
    }

    /**
     * Gets the class name.
     * 
     * @return class name
     */
    public String getName() {
        return (String) elementAt(1);
    }

    /**
     * Sets a class name in extends clause.
     * 
     * @param name
     *            name to set
     */
    public void setName(String name) {
        setElementAt(name, 1);
    }
    
    /**
     * Get the type paramters
     * 
     * @return
     */
    public TypeParameterList getTypeParameters(){
    	return (TypeParameterList) elementAt(2);
    }
    
    public void setTypeParameters(TypeParameterList tpl){
    	setElementAt(tpl, 2);
    }

    /**
     * Gets the classes in 'extends' clause. This causes
     * 
     * @return if class decl has no extends, this returns null otherwise returns
     *         the name of the super class.
     */
    public TypeName[] getBaseclasses() {
        return (TypeName[]) elementAt(3);
    }

    /**
     * Gets base classes in 'extends' clause.
     * 
     * @return if class decl has no extends, this returns null otherwise returns
     *         the name of the super class.
     */
    public TypeName getBaseclass() {
        TypeName[] bases = getBaseclasses();
        if (bases.length == 0)
            return null;
        return bases[0];
    }

    /**
     * Sets super class name
     * 
     * @param ctypes
     *            class types to set
     */
    public void setBaseclasses(TypeName[] ctypes) {
        setElementAt(ctypes, 3);
    }

    /**
     * Sets super class name
     * 
     * @param ctype
     *            class type to set
     */
    public void setBaseclass(TypeName ctype) {
        setElementAt(new TypeName[] { ctype }, 3);
    }

    /**
     * Gets interface name list
     * 
     * @return there is no implemented class, getInterfaceList returns an empty
     *         list
     */
    public TypeName[] getInterfaces() {
        return (TypeName[]) elementAt(4);
    }

    /**
     * Sets interface name list
     * 
     * @param ctlist
     *            class type list to set
     */
    public void setInterfaces(TypeName[] ctlist) {
        setElementAt(ctlist, 4);
    }

    /**
     * Gets class body
     * 
     * @return return an field declaration list as this class body.
     */
    public MemberDeclarationList getBody() {
        return (MemberDeclarationList) elementAt(5);
    }

    /**
     * Sets class body
     * 
     * @param body
     *            member declaration list to set as this class body.
     */
    public void setBody(MemberDeclarationList mdlist) {
       
    	setElementAt(mdlist, 5);
    }

	/**
     * Get the list of Enumeration Constants
     * 
     */
    public EnumConstantList getEnumConstants() {
        return (EnumConstantList) elementAt(7);
    }
    
	/**
     * Set the list of Enumeration Constants
     * 
     */
    public void setEnumConstants(EnumConstantList ecl) {
    	setElementAt(ecl, 7);
    }
    
    public void setSuffixes(Hashtable suffixes) {
        this.suffixes = suffixes;
    }

    public Hashtable getSuffixes() {
        return this.suffixes;
    }

    public void setMetaclass(String metaclazz) {
        this.metaclazz = new String[] { metaclazz };
    }

    public void setMetaclass(String[] metaclazz) {
        this.metaclazz = metaclazz;
    }

    public String getMetaclass() {
        if (metaclazz == null || metaclazz.length == 0)
            return null;
        return this.metaclazz[0];
    }

    public void accept(ParseTreeVisitor v) throws ParseTreeException {
        v.visit(this);
    }

	public void set_isEnum(boolean _isEnum) {
		this._isEnum = _isEnum;
	}

	public boolean isEnumeration() {
		return _isEnum;
	}
}
