package mujava.op.util;

/**
 * • Mutation operator group: one of AOR, COR, EVR, LOR, LVR, ORU, ROR, SOR, or
 * STD. • Mutation operator: the specific program transformation that generates
 * a mutant (e.g., lhs < rhs ?−→lhs != rhs). • Node data type (basic): the
 * summary data type of the mutated node in the AST (e.g., int, boolean, class).
 * • Node data type (detailed): the detailed data type of the mutated node in
 * the AST—the data types of the operands and result (e.g., (int,int)int) for
 * mutated operators or methods; identical to node data type (basic) for other
 * mutations. • Node type (basic): the type of the mutated AST node (e.g.,
 * ‘binary operator’, ‘int literal’, ‘method call’). • AST context (basic): the
 * sequence of AST node types from the mutated node (inclusive) to the root node
 * of the AST. Figures 3 and 4 give an example. • AST context (detailed):
 * similar to AST context (basic), but nodes with multiple children are
 * annotated with the edge label that indicates the relationship (e.g., ‘for’
 * becomes ‘for:init’, ‘for:cond’, ‘for:inc’, or ‘for:body’). Figures 3 and 4
 * give an example. • AST statement context (basic): the sequence of AST node
 * types from the mutated node (inclusive) to the root node of the AST,
 * abstracted to include only those nodes that represent a top-level statement.
 * • AST statement context (detailed): similar to AST statement context (basic),
 * but nodes with multiple children are anno- tated with the edge label that
 * indicates the relationship. • Parent context (basic): the AST node type of
 * the immediate parent node of the mutated AST node. • Parent context
 * (detailed): the AST node type of the imme- diate parent node of the mutated
 * AST node plus the edge label that describes the relationship between the two
 * nodes (see AST context detailed). • Parent statement context (basic): similar
 * to Parent context (basic), but abstracted to include only those nodes that
 * represent a top-level statement. • Parent statement context (detailed):
 * similar to Parent context (detailed), but abstracted to include only those
 * nodes that represent a top-level statement. • Child node type: – Has literal:
 * indicates whether the mutated AST node has an immediate child node that is a
 * literal. – Has variable: indicates whether the mutated AST node has an
 * immediate child node that is a variable. – Has operator: indicates whether
 * the mutated AST node has an immediate child node that is an oper- ator.
 *
 * @author leofernandesmo
 */

// @Documented
// @Target(ElementType.TYPE)
// @Inherited
// @Retention(RetentionPolicy.RUNTIME)
// public @interface ContextInfo{
public class ContextInfo {
  // String stuStream() default "CSE";
  // String mutationOperatorGroup(); //one of AOR, COR, EVR, LOR, LVR, ORU,
  // ROR, SOR, or STD.
  // String mutationOperator(); //the specific program transformation that
  // generates a mutant (e.g., lhs < rhs ?−→lhs != rhs).
  // String nodeDataType(); //the summary data type of the mutated node in the
  // AST (e.g., int, boolean, class).
  // String astContext(); //the sequence of AST node types from the mutated
  // node (inclusive) to the root node of the AST.
  // String parentContext(); //the AST node type of the immediate parent node
  // of the mutated AST node.

  private String mutationOperatorGroup;
  private String mutationOperator;
  private String nodeDataType;
  private String astContext;
  private String parentContext;
  private String before;
  private String after;
  private int line;


  public int getLine() {
	return line;
  }

  public void setLine(int line) {
	this.line = line;
  }

  public String getBefore() {
	return before;
  }

  public void setBefore(String before) {
	this.before = before;
  }

  public String getAfter() {
	return after;
  }

  public void setAfter(String after) {
	this.after = after;
  }

  public String getMutationOperatorGroup() {
	return mutationOperatorGroup;
  }

  public void setMutationOperatorGroup(String mutationOperatorGroup) {
	this.mutationOperatorGroup = mutationOperatorGroup;
  }

  public String getMutationOperator() {
	return mutationOperator;
  }

  public void setMutationOperator(String mutationOperator) {
	this.mutationOperator = mutationOperator;
  }

  public String getNodeDataType() {
	return nodeDataType;
  }

  public void setNodeDataType(String nodeDataType) {
	this.nodeDataType = nodeDataType;
  }

  public String getAstContext() {
	return astContext;
  }

  public void setAstContext(String astContext) {
	this.astContext = astContext;
  }

  public String getParentContext() {
	return parentContext;
  }

  public void setParentContext(String parentContext) {
	this.parentContext = parentContext;
  }
}