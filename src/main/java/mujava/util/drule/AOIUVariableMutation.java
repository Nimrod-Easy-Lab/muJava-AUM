package mujava.util.drule;

import openjava.ptree.Variable;

public class AOIUVariableMutation implements MutationInfo {
  Variable subject;
  String classname;

  public AOIUVariableMutation(Variable subject, String classname) {
	this.subject = subject;
	this.classname = classname;
  }

  @Override
  public boolean equals(MutationInfo other) {
    if (other instanceof AOIUVariableMutation) {
      AOIUVariableMutation aoiuVariableMutation = (AOIUVariableMutation) other;
      return this.subject.equals(aoiuVariableMutation.subject) && this.classname.equals(aoiuVariableMutation.classname);
	} else {
	  return false;
	}
  }
}
