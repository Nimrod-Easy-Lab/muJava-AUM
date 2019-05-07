package mujava.util.drule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class DRuleUtils {
  static Map<String, List<MutationInfo>> selectedOperators = new HashMap<>();
  static Semaphore sem = new Semaphore(1, true);


  /**
   * Checks whether a certain mutation was done with some operator
   * and then removes it from memory
   *
   * @param operator  a String corresponding the operator intended to lookup
   * @param operation the kind of mutation to lookup
   */
  public boolean consumeOperation(String operator, MutationInfo operation) {
	boolean r = false;
	try {
	  synchronized (this) {
		sem.acquire();
		r = selectedOperators.containsKey(operator) && selectedOperators.get(operator).contains(operation);
		if (r) {
		  selectedOperators.get(operator).remove(operation);
		  selectedOperators.remove(operator);
		}
		sem.release();
	  }
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
	return r;
  }

  /**
   * Inserts a certain mutation that was done with some operator
   * and then removes it from memory
   *
   * @param operator  a String corresponding the operator intended to lookup
   * @param operation the kind of mutation to lookup
   */
  public boolean insertMutation(String operator, MutationInfo operation) {
	boolean r = false;
	try {
	  synchronized (this) {
		sem.acquire();
		if (!selectedOperators.containsKey(operator)) selectedOperators.put(operator, new ArrayList<>());
		if (!selectedOperators.get(operator).contains(operation)) {
		  selectedOperators.get(operator).add(operation);
		  r = true;
		}
		sem.release();
	  }
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
	return r;
  }
}
