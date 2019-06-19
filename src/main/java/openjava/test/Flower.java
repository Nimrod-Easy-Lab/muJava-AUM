/**
 * Copyright (C) 2015  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

 /**
 * <p>Description: </p>
 * @author Jeff Offutt and Yu-Seung Ma
 * @version 1.0
  */  

package openjava.test;

public final class Flower {

	public static void main(String[] arguments) {

		int tulip = 2;
		assert tulip != 3 && tulip > 0 : "list variable is null or empty";
		int days = 5;
		assert days % 7 == 6 : days;

		assert hasValidState() : "Construction failed - not valid state.";

		int fLength = 2;
		int oldLength = 3;
		assert fLength > oldLength;

		int originalState = 2;
		assert (originalState = 3) != 5;

		assert (isValidSpecies(2) == isValidLength(fLength));

		int result = 5;
		assert result > 0 : result;

	}

	private static boolean isValidSpecies(int i) {

		return false;
	}

	private static boolean isValidLength(int fLength) {

		return false;
	}

	private static boolean hasValidState() {
		return false;
	}
}