package mujava.util.drule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DRuleUtils {
  public enum MOperator {
	AOIU,
	ASRS
  }

  ;
  static Semaphore sem = new Semaphore(1, true);
  static DRuleUtils instance = new DRuleUtils();
  static List<String> allOperatorsSelected = new ArrayList<>();
  static List<MutationInfo> mutationInfoList = new ArrayList<>();

  DRuleUtils() {

  }

  public List<String> getAllOperatorsSelected() {
   	return allOperatorsSelected;
  }

  public boolean addMutation(MutationInfo mutationInfo) {
	boolean ret = false;
	try {
	  synchronized (this) {
		sem.acquire();
		if (!mutationInfoList.contains(mutationInfo)) {
		  ret = true;
		  mutationInfoList.add(mutationInfo);
		}
		sem.release();
	  }
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
	return ret;
  }

  public boolean containsMutation(MutationInfo mutationInfo) {
	boolean ret = false;
	try {
	  synchronized (this) {
		sem.acquire();
		ret = mutationInfoList.contains(mutationInfo);
		sem.release();
	  }
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
	return ret;
  }

  public boolean isOperatorSelected(String op) {
	return allOperatorsSelected.contains(op);
  }

  public void setSelectedOperators(List<String> allOperatorsSelected) {
	this.allOperatorsSelected = allOperatorsSelected;
  }

  public static DRuleUtils access() {
	return instance;
  }
}
