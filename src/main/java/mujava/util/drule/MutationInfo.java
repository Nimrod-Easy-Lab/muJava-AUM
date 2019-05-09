package mujava.util.drule;

public class MutationInfo {
  public enum DRule {
    RULE43,
	RULE49,
	RULE52,
	RULE56,
	RULE57,
	RULE66,
	RULE69,
	RULE70
  }

  DRule rule;
  DRuleUtils.MOperator operator;
  String classname;

  public MutationInfo(DRuleUtils.MOperator operator, DRule rule, String classname) {

  }
  public boolean equals(MutationInfo other) {
    return this.rule.equals(other.rule) && this.operator.equals(other.operator)
		&& this.classname.equals(other.classname);
  }
}
