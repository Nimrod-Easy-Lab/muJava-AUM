/*
 * MethodCall.java 1.0
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

import openjava.mop.Environment;
import openjava.mop.NoSuchMemberException;
import openjava.mop.OJClass;
import openjava.mop.OJField;
import openjava.mop.OJMethod;
import openjava.mop.Signature;
import openjava.ptree.util.ParseTreeVisitor;

/**
 * The <code>MethodCall</code> class represents
 * a method call expression.
 *
 * @see openjava.ptree.NonLeaf
 * @see openjava.ptree.Expression
 */
public class MethodCall extends NonLeaf implements Expression {

	/**
	 * Allocates a new method call expression object.
	 *
	 * @param  expr  the expression which indicates an object or
	 *               a class. This may be null for invocation on 'this'.
	 * @param  name  the method name.
	 * @param  args  the arguments for this method.
	 */
	public MethodCall(Expression expr, String name, ExpressionList args) {
		super();
		if (args == null)
			args = new ExpressionList();
		set(expr, null, name, args);
	}

	/**
	 * Allocates a new method call expression for 'this'.
	 * This is equivalent to:
	 * <pre>
	 *     new MethodCall( (Expression) null, name, args )
	 * </pre>
	 *
	 * @param  name  the method name.
	 * @param  args  the arguments for this method.
	 */
	public MethodCall(String name, ExpressionList args) {
		this((Expression) null, name, args);
	}

	/**
	 * Allocates a new method call expression object.
	 *
	 * @param  name  the method name.
	 * @param  args  the arguments for this method.
	 */
	public MethodCall(TypeName type, String name, ExpressionList args) {
		super();
		if (args == null)
			args = new ExpressionList();
		set(null, type, name, args);
	}

	public MethodCall(OJClass clazz, String name, ExpressionList args) {
		this(TypeName.forOJClass(clazz), name, args);
	}

	MethodCall() {
		super();
	}

	/**
	 * Gets the expression accessed.
	 *
	 * @return  the expression accessed.
	 */
	public Expression getReferenceExpr() {
		return (Expression) elementAt(0);
	}

	/**
	 * Sets the expression accessed.
	 *
	 * @param  expr  the expression accessed.
	 */
	public void setReferenceExpr(Expression expr) {
		setElementAt(expr, 0);
		setElementAt(null, 1);
	}

	public TypeName getReferenceType() {
		return (TypeName) elementAt(1);
	}

	public void setReferenceType(TypeName type) {
		setElementAt(null, 0);
		setElementAt(type, 1);
	}

	/**
	 * Gets the method name.
	 *
	 * @return  the method name.
	 */
	public String getName() {
		return (String) elementAt(2);
	}

	/**
	 * Sets the method name.
	 *
	 * @param  name  the method name.
	 */
	public void setName(String name) {
		setElementAt(name, 2);
	}

	/**
	 * Gets the arguments for this method.
	 *
	 * @return  the expression list as the arguments.
	 */
	public ExpressionList getArguments() {
		return (ExpressionList) elementAt(3);
	}

	/**
	 * Sets the arguments for this method.
	 *
	 * @param  exprs  the expression list as the arguments.
	 */
	public void setArguments(ExpressionList exprs) {
		if (exprs == null)
			exprs = new ExpressionList();
		setElementAt(exprs, 3);
	}

	public void accept(ParseTreeVisitor v) throws ParseTreeException {
		v.visit(this);
	}

	public OJClass getType(Environment env) throws Exception {
		//System.out.println("openjava.ptree.MethodCall: " + env.currentClassName());
		OJClass selftype = env.lookupClass(env.currentClassName());
		//System.out.println("openjava.ptree.MethodCall: selftype" + selftype);
		Expression refexpr = getReferenceExpr();
		//System.out.println("Expression: " + refexpr);
		//System.out.println("getReferenceType: " + getReferenceType());
		String name = getName();
		//System.out.println("getName(): " + getName());
		
		OJClass reftype = null;

		if (refexpr != null) {
			reftype = refexpr.getType(env);
			//System.out.println("reftype: " + reftype);
		} else {
			TypeName refname = getReferenceType();
			if (refname != null) {
				String qname = env.toQualifiedName(refname.toString());
				reftype = env.lookupClass(qname);
			}
		}

		ExpressionList args = getArguments();
		OJClass[] argtypes = new OJClass[args.size()];
		for (int i = 0; i < argtypes.length; ++i) {
			argtypes[i] = args.get(i).getType(env);
		}

		OJMethod method = null;

		if (reftype != null)
			method = pickupMethod(reftype, name, argtypes);
		//System.out.println("method: " + method);
		if (method != null)
			return method.getReturnType();

		/* try to consult this class and outer classes */
		if (reftype == null) {
			OJClass declaring = selftype;
			while (declaring != null) {
				method = pickupMethod(declaring, name, argtypes);
				if (method != null)
					return method.getReturnType();

				/* consult innerclasses */
				OJClass[] inners = declaring.getDeclaredClasses();
				for (int i = 0; i < inners.length; ++i) {
					method = pickupMethod(inners[i], name, argtypes);
					if (method != null)
						return method.getReturnType();
				}

				declaring = declaring.getDeclaringClass();
			}
			reftype = selftype;
		}

		/**
		 * Added for static imported fields or packages
		 */
		System.out.println("MethodCall method: " + this.getReferenceType() + "; " + this.getName());
		if(this.getReferenceType() != null && this.getName() != null){
			method = new OJMethod(env, reftype, this);
			System.out.println("MethodCall getType method: " + (method != null) + "; " + method.getReturnType());
			if(method != null)
				return method.getReturnType();
		}
		
		Signature sign = new Signature(name, argtypes);
		NoSuchMemberException e = new NoSuchMemberException(sign.toString());
		method = reftype.resolveException(e, name, argtypes);
		return method.getReturnType();
	}

	private static OJMethod pickupMethod(
		OJClass reftype,
		String name,
		OJClass[] argtypes) {
		try {
			return reftype.getAcceptableMethod(name, argtypes, reftype);
		} catch (NoSuchMemberException e) {
			return null;
		}
	}

}
