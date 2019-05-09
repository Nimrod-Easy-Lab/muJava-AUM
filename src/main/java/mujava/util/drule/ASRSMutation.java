package mujava.util.drule;

import mujava.op.basic.ASRS;
import openjava.ptree.AssignmentExpression;

public class ASRSMutation implements MutationInfo{
  String original;
  String mutated;
  String classname;
  //Potential threat: trusting OJ's .equals() implementation
  AssignmentExpression subject;
  public ASRSMutation (String original, String mutated, AssignmentExpression subject, String classname) {
    this.original = original;
    this.mutated = mutated;
    this.classname = classname;
    this.subject = subject;
  }

  @Override
  public boolean equals(MutationInfo other) {
    if (other instanceof ASRSMutation) {
      ASRSMutation asrsMutationOther = (ASRSMutation) other;
      return (asrsMutationOther.original.equals(this.original)) && (asrsMutationOther.mutated.equals(this.mutated))
          && (asrsMutationOther.classname.equals(this.classname)) && (this.subject.equals(asrsMutationOther.subject));
    } else {
      return false;
    }
  }
}
