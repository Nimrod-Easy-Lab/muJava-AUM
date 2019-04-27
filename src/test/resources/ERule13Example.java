import java.util.Collection;
import java.util.Map;
import java.util.Set;

class ERule13Example {

	public	ERule13Example (){}
	private class GenericContainerMock {
		public int length() {
			return 6;
		}
		public int size() {
			return length();
		}
	}

	void defaultMethodCall() {
	}
	void m_rule_applicable_gtstring_straightforward() {
		String string = "string";
		if (string.length() > 0) {
			defaultMethodCall();
		}
	}

	void m_rule_applicable_gtstring_reverseorder() {
		String string = "string";
		if (0 < string.length()) {
			defaultMethodCall();
		}
	}

	void m_rule_applicable_eqstring_reverseorder() {
		String string = "string";
		if (0 == string.length()) {
			defaultMethodCall();
		}
	}

	void m_rule_applicable_eqstring_straightforward() {
		String string = "string";
		if (string.length() == 0) {
			defaultMethodCall();
		}
	}

	void m_rule_not_applicable_neqstring_straightforward() {
		String string = "string";
		if (string.length() != 0) {
			defaultMethodCall();
		}
	}

	void m_rule_not_applicable_neqstring_reverseorder() {
		String string = "string";
		if (0 != string.length()) {
			defaultMethodCall();
		}
	}
	// Arrays
	void m_rule_applicable_gtarray_straightforward() {
		int[] array = {1,2,3};
		if (array.length > 0) {
			defaultMethodCall();
		}
	}

	void m_rule_applicable_gtarray_reverseorder() {
		int[] array = {1,2,3};
		if (0 < array.length) {
			defaultMethodCall();
		}
	}

	void m_rule_applicable_eqarray_reverseorder() {
		int[] array = {1,2,3};
		if (0 == array.length) {
			defaultMethodCall();
		}
	}

	void m_rule_applicable_eqarray_straightforward() {
		int[] array = {1,2,3};
		if (array.length == 0) {
			defaultMethodCall();
		}
	}

	void m_rule_not_applicable_neqarray_straightforward() {
		int[] array = {1,2,3};
		if (array.length != 0) {
			defaultMethodCall();
		}
	}

	void m_rule_not_applicable_neqarray_reverseorder() {
		int[] array = {1,2,3};
		if (0 != array.length) {
			defaultMethodCall();
		}
	}
	// Not applicable in any case
	void m_rule_not_applicable_1() {
		String string = "string";
		if (string.length() > 1) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_2() {
		String string = "string";
		if (string.length() != 1) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_3() {
		String string = "string";
		if (string.length() < 6) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_4() {
		String string = "string";
		if (string.length() < 7) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_5() {
		String string = "string";
		if (string.length() < 7) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_6() {
		int[] array = {1,2,3,4,5,6};
		if (array.length > 1) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_7() {
		int[] array = {1,2,3,4,5,6};
		if (array.length != 1) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_8() {
		int[] array = {1,2,3,4,5,6};
		if (array.length < 6) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_9() {
		int[] array = {1,2,3,4,5,6};
		if (array.length < 7) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable_10() {
		int[] array = {1,2,3,4,5,6};
		if (array.length < 7) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable11() {
		GenericContainerMock gcm = new GenericContainerMock();
		if (gcm.length() > 0) {
			defaultMethodCall();
		}
	}
	void m_rule_not_applicable12() {
		GenericContainerMock gcm = new GenericContainerMock();
		if (gcm.size() > 0) {
			defaultMethodCall();
		}
	}
}