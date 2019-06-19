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
// $ANTLR 2.7.6 (2005-12-22): "expr.g" -> "ExprParser.java"$

package openjava.test;

public interface ExprParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int SEM = 4;
	int EQUAL = 5;
	int EOR = 6;
	int IMPL = 7;
	int OR = 8;
	int AND = 9;
	int NOT = 10;
	int ID = 11;
	int LPAREN = 12;
	int RPAREN = 13;
	int WS = 14;
}
