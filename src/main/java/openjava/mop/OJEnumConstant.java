/*
 * OJField.java
 *
 * Jul 28, 1998 by mich
 */
package openjava.mop;

import java.lang.reflect.Field;
import java.util.Hashtable;

import openjava.ptree.EnumConstant;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.TypeName;


public class OJEnumConstant implements OJMember, Cloneable
{

    private OJEnumConstantImp substance;

    private static Hashtable table = new Hashtable();



    public OJEnumConstant( Environment env, OJClass declarer, EnumConstant d ) {
    	this.substance = new OJEnumConstantSourceCode( env, declarer, d );
    }


    public Signature signature() {
        return new Signature( this );
    }

    public OJClass getDeclaringClass() {
	return substance.getDeclaringClass();
    }
    
    public String getName() {
	return substance.getName();
    }

    public String getIdentifiableName() {
	return substance.getIdentifiableName();
    }

    public OJModifier getModifiers() {
	return substance.getModifiers();
    }

    public OJClass getType() {
	return substance.getType();
    }

    /**
     * Compares this field against the given object.
     * The algorithm is borrowed by java.lang.reflect.Field.equals().
     *
     * @see java.lang.reflect.Field#equals
     */
    public boolean equals( Object obj ) {
	if(obj != null && obj instanceof OJEnumConstant) {
	    OJEnumConstant other = (OJEnumConstant) obj;
	    return (getDeclaringClass() == other.getDeclaringClass())
		&& (getName().equals( other.getName() ))
		    && (getType() == other.getType());
	} else {
	    return false;
	}
    }

    /**
     * Computes a hashcode for this field.
     *
     * @return  hash code.
     */
    public int hashCode() {
	return toString().hashCode();
    }

    public String toString() {
	return substance.toString();
    }

    public Environment getEnvironment() {
        return new ClosedEnvironment( getDeclaringClass().getEnvironment() );
    }

    /**
     * Obtains the field value specified by this field object on
     * the given object.
     *
     * @exception IllegalArgumentException if this field is not
     * compiled yet.
     **/
    public Object get( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.get( obj );
    }

    public boolean getBoolean( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getBoolean( obj );
    }
    
    public byte getByte( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getByte( obj );
    }
    
    public char getChar( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getChar( obj );
    }

    public short getShort( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getShort( obj );
    }

    public int getInt( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getInt( obj );
    }

    public long getLong( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getLong( obj );
    }

    public float getFloat( Object obj )
  	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getFloat( obj );
    }
  
    public double getDouble( Object obj )
  	throws IllegalArgumentException, IllegalAccessException
    {
	return substance.getDouble( obj );
    }
  
    public void set( Object obj, Object value )
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.set( obj, value );
    }
  
    public void setBoolean( Object obj, boolean z)
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setBoolean( obj, z );
    }
  
    public void setByte( Object obj, byte b)
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setByte( obj, b );
    }
  
    public void setChar( Object obj, char c)
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setChar( obj, c );
    }
  
    public void setShort( Object obj, short s)
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setShort( obj, s );
    }
  
    public void setInt( Object obj, int i )
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setInt( obj, i );
    }
  
    public void setLong( Object obj, long l )
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setLong( obj, l );
    }
  
    public void setFloat( Object obj, float f )
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setFloat( obj, f );
    }
  
    public void setDouble( Object obj, double d )
  	throws IllegalArgumentException, IllegalAccessException
    {
	substance.setDouble( obj, d );
    }
  
    /* -- methods java.lang.reflect.Field does not supply. -- */
  
    public boolean isExecutable() {
	return substance.isExecutable();
    }

    public boolean isAlterable() {
	return substance.isAlterable();
    }

    public final Field getByteCode() throws CannotExecuteException {
        return substance.getByteCode();
    }

    public final EnumConstant getSourceCode()
        throws CannotAlterException
    {
        return substance.getSourceCode();
    }
    
    /**
     * Unfinished
     * @return
     */
    public OJEnumConstant getCopy() {
		try {
		    
		    if (substance instanceof OJEnumConstantByteCode) {
			java.lang.reflect.Field field = 
			    ((OJEnumConstantByteCode)substance).getByteCode () ;
			
			OJEnumConstant result = (OJEnumConstant)this.clone () ;
			// On remplace du ByteCode par du SourceCode
			EnumConstant fd = new EnumConstant (new ModifierList (field.getModifiers ()), field.getName (), null,null,TypeName.forOJClass (OJClass.forClass (field.getDeclaringClass ())).getName()) ;
			Environment env = substance.getDeclaringClass ().getEnvironment () ;
			result.substance =
			    new OJEnumConstantSourceCode (env,
						   substance.getDeclaringClass (),
						   fd) ;
			return result ;
		    } else if (substance instanceof OJEnumConstantSourceCode) {
			OJEnumConstant result = (OJEnumConstant)this.clone () ;
			result.substance = new OJEnumConstantSourceCode
			    (((OJEnumConstantSourceCode)this.substance).getEnvironment (),
			     this.substance.getDeclaringClass (),
			     (EnumConstant)this.substance.getSourceCode ().
			     makeRecursiveCopy ()) ;
			return result ;
		    }
		} catch (Exception e) {
		    System.err.println ("Failed to copy " + this + ": " + e) ;
		    e.printStackTrace () ;
		}
		return null ;
    }

    /* -- inner use only -- */

    public void setDeclaringClass( OJClass parent ) throws CannotAlterException {
        substance.setDeclaringClass( parent );
    }
  
    /* -- Translation (not overridable) -- */
  
    public final void setName( String name )
	throws CannotAlterException
    {
	substance.setName( name );
    }
  
    public final void setModifiers( int mods )
	throws CannotAlterException
    {
	substance.setModifiers( mods );
    }

    public final void setModifiers( OJModifier mods )
	throws CannotAlterException
    {
	setModifiers( mods.toModifier() );
    }
  
    public final void setType( OJClass type )
	throws CannotAlterException
    {
	substance.setType( type );
    }
  
}


/**
 * The abstract class <code>OJFieldImp</code> provides an interface to
 * an implementation of OJField.
 */
abstract class OJEnumConstantImp
{
    public abstract String toString();
    abstract OJClass getDeclaringClass();
    abstract String getName();
    abstract String getIdentifiableName();
    abstract OJModifier getModifiers();
    abstract OJClass getType();

    abstract Object get( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract boolean getBoolean( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract byte getByte( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract char getChar( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract short getShort( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract int getInt( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract long getLong( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract float getFloat( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract double getDouble( Object obj )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void set( Object obj, Object value )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setBoolean( Object obj, boolean z )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setByte( Object obj, byte b )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setChar( Object obj, char c )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setShort( Object obj, short s )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setInt( Object obj, int i )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setLong( Object obj, long l )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setFloat( Object obj, float f )
	throws IllegalArgumentException, IllegalAccessException;
    abstract void setDouble( Object obj, double d )
	throws IllegalArgumentException, IllegalAccessException;
    
    /* -- methods java.lang.reflect.Field does not supply. -- */
  
    abstract boolean isExecutable();
    abstract boolean isAlterable();
    abstract Field getByteCode() throws CannotExecuteException;
    abstract EnumConstant getSourceCode() throws CannotAlterException;

    /* -- inner use only -- */

    abstract void setDeclaringClass( OJClass parent )
	throws CannotAlterException;
  
    /* -- Translation (not overridable) -- */
  
    abstract void setName( String name )
	throws CannotAlterException;
    abstract void setModifiers( int mods )
	throws CannotAlterException;
    abstract void setType( OJClass type )
	throws CannotAlterException;
  
}


class OJEnumConstantByteCode extends OJEnumConstantImp
{

    private Field javaField;

    OJEnumConstantByteCode( Field java_enumConstant ) {
    	this.javaField = java_enumConstant;
    }

    public String toString() {
	return javaField.toString();
    }

    OJClass getDeclaringClass() {
	return OJClass.forClass( javaField.getDeclaringClass() );
    }
    
    String getName() {
	return javaField.getName();
    }

    String getIdentifiableName() {
	/***********/
	return getDeclaringClass().getName() + "." + getName();
    }

    OJModifier getModifiers() {
	return OJModifier.forModifier( javaField.getModifiers() );
    }

    OJClass getType() {
	return OJClass.forClass( javaField.getType() );
    }

    Object get( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.get( obj );
    }

    boolean getBoolean( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getBoolean( obj );
    }
    
    byte getByte( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getByte( obj );
    }
    
    char getChar( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getChar( obj );
    }

    short getShort( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getShort( obj );
    }

    int getInt( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getInt( obj );
    }

    long getLong( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getLong( obj );
    }

    float getFloat( Object obj )
  	throws IllegalArgumentException, IllegalAccessException
    {
  	  return javaField.getFloat( obj );
    }
  
    double getDouble( Object obj )
  	throws IllegalArgumentException, IllegalAccessException
    {
	return javaField.getDouble( obj );
    }
  
    void set( Object obj, Object value)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.set( obj, value);
    }
  
    void setBoolean( Object obj, boolean z)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setBoolean( obj, z);
    }
  
    void setByte( Object obj, byte b)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setByte( obj, b);
    }
  
    void setChar( Object obj, char c)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setChar( obj, c);
    }
  
    void setShort( Object obj, short s)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setShort( obj, s);
    }
  
    void setInt( Object obj, int i)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setInt( obj, i);
    }
  
    void setLong( Object obj, long l)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setLong( obj, l);
    }
  
    void setFloat( Object obj, float f)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setFloat( obj, f);
    }
  
    void setDouble( Object obj, double d)
  	throws IllegalArgumentException, IllegalAccessException
    {
	javaField.setDouble( obj, d);
    }
    
    /* -- methods java.lang.reflect.Field does not supply. -- */
  
    final boolean isExecutable() {
        return true;
    }

    final boolean isAlterable() {
        return false;
    }

    final Field getByteCode() throws CannotExecuteException {
	return javaField;
    }

    final EnumConstant getSourceCode() throws CannotAlterException {
	throw new CannotAlterException( "getSourceCode()" );
    }

    /* -- inner use only --  */

    void setDeclaringClass( OJClass parent ) throws CannotAlterException {
        throw new CannotAlterException( "setDeclaringClass()" );
    }
  
    /* -- Translation (not overridable) --  */
  
    final void setName( String name ) throws CannotAlterException {
	throw new CannotAlterException( "setName()" );
    }
  
    final void setModifiers( int mods ) throws CannotAlterException {
	throw new CannotAlterException( "setModifiers()" );
    }
  
    final void setType( OJClass type ) throws CannotAlterException {
	throw new CannotAlterException( "setType()" );
    }
  
}


class OJEnumConstantSourceCode extends OJEnumConstantImp
{

    private static int idCounter = 0;
    private int id;

    private OJClass			declarer;
    private EnumConstant	definition;
    private Environment		env;

    OJEnumConstantSourceCode( Environment env, OJClass declarer,
    		EnumConstant ptree )
    {
	this.declarer = declarer;
	this.definition = ptree;
	this.env = env;
	this.id = idCounter++;
    }

    public String toString() {
	OJClass declarer = getDeclaringClass();
	String declarername = (declarer == null) ? "*" : declarer.getName();

	StringBuffer buf = new StringBuffer();
        String modif = getModifiers().toString();
        if (! modif.equals( "" )) {
            buf.append( modif );
            buf.append( " " );
        }
	buf.append( getType().getName() );
        buf.append( " " );
        buf.append( declarername );
        buf.append( "." );
        buf.append( getName() );
	return buf.toString();
    }

    OJClass getDeclaringClass() {
	return declarer;
    }
    
    String getName() {
	return definition.getName();
    }

    String getIdentifiableName() {
	OJClass declarer = getDeclaringClass();
	String declarername;
	if (declarer == null) {
	    declarername = "*" + id;
	} else {
	    declarername = declarer.getName();
	}
	return declarername + "." + getName();
    }

    OJModifier getModifiers() {
	return OJModifier.forParseTree( definition.getModifiers() );
    }

    OJClass getType() {
    	String type_name = definition.getEnumType().toString();
    	return Toolbox.forNameAnyway( env, type_name );
    }

    /**
     * Obtains the field value specified by this field object on
     * the given object.
     *
     * @exception IllegalArgumentException if this field is not
     * compiled yet.
     **/
    Object get( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "get()" );
    }

    boolean getBoolean( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getBoolean()" );
    }
    
    byte getByte( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getByte()" );
    }
    
    char getChar( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getChar()" );
    }

    short getShort( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getShort()" );
    }

    int getInt( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getInt()" );
    }

    long getLong( Object obj )
	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getLong()" );
    }

    float getFloat( Object obj )
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getFloat()" );
    }
  
    double getDouble( Object obj )
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "getDouble()" );
    }
  
    void set( Object obj, Object value)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "set()" );
    }
  
    void setBoolean( Object obj, boolean z)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setBoolean()" );
    }
  
    void setByte( Object obj, byte b)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setByte()" );
    }
  
    void setChar( Object obj, char c)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setChar()" );
    }
  
    void setShort( Object obj, short s)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setShort()" );
    }
  
    void setInt( Object obj, int i)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setInt()" );
    }
  
    void setLong( Object obj, long l)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setLong()" );
    }
  
    void setFloat( Object obj, float f)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setFloat()" );
    }
  
    void setDouble( Object obj, double d)
  	throws IllegalArgumentException, IllegalAccessException
    {
	throw new IllegalArgumentException( "setDouble()" );
    }
    
    /* -- methods java.lang.reflect.Field does not supply. -- */
  
    boolean isExecutable() {
        return false;
    }

    boolean isAlterable() {
        return true;
    }

    final Field getByteCode() throws CannotExecuteException {
	throw new CannotExecuteException( "getByteCode()" );
    }

    final EnumConstant getSourceCode()
	throws CannotAlterException
    {
	return definition;
    }

    Environment getEnvironment ()
    {
	return this.env ;
    }

    final void setSourceCode (EnumConstant definition) {
	this.definition = definition ;
    }
  
    /* -- inner use only --  */

    void setDeclaringClass( OJClass parent ) throws CannotAlterException {
        this.declarer = parent;
    }
  
    /* -- Translation (not overridable) -- */
  
    final void setName( String name ) throws CannotAlterException {
        definition.setName( name );
    }
  
    final void setModifiers( int mods ) throws CannotAlterException {
    	definition.setModifiers( new ModifierList( mods ) );
    }
  
    final void setType( OJClass type ) throws CannotAlterException {
    	definition.setEnumType( TypeName.forOJClass( type ).getName() );
    }
  
}

