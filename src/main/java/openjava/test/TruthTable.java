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

  

package openjava.test;

import java.util.BitSet;

/**
	*
	*in order to dispaly table as textbook, the row number is changed 
 * @author wxu2
 *
 */
public class TruthTable {
	
	private int rows;
	private int vars;
	
	private BitSet[] bits;	
	
	public TruthTable (int vars)
	{
		this.vars=vars;
		this.rows=(int)Math.pow(2, vars);
		bits=new BitSet[rows];		
	}
	
	public static void main(String[] args)
	{
		TruthTable table=new TruthTable(3);
		for( int i=0;i<8;i++)
		{
			BitSet bit=table.getRow(i);
			for(int j=0;j<3;j++)
				System.out.print(">"+j+":"+bit.get(j));
			System.out.println();
		}
	}
	
	public BitSet getRow(int row)
	{	
		if(row>=rows)
			return null;
		
		row=rows-row-1;
		if(bits[row]==null)
		{
			bits[row]=new BitSet();
			String bitString=Integer.toBinaryString(row);
			int start=vars-bitString.length();			
			for(int i=0;i<bitString.length();i++)
			{				
				if(bitString.charAt(i)=='1')
				{
					bits[row].set(start+i, true);//.flip(start+i);
				}
			}
		}		
		return bits[row];
	}
	
	public int getRowNum()
	{
		return rows;
	}
	
	public boolean getRowValue(int row)
	{
		return bits[rows-row-1].get(vars);
	}
	
	public boolean getValue(int row, int col)
	{
		return bits[rows-row-1].get(col);
	}
	
	public int getVarNum()
	{
		return vars;
	}
	
	public void setRowValue(int row, boolean value)
	{
		bits[rows-row-1].set(vars, value);
	}
	
	public String toString()
	{
		String result="";
		for(int i=0;i<rows;i++)
		{
			result+=i+" ";
			for( int j=0;j<vars+1;j++)
			{
				result+=printTF(bits[i].get(j))+" ";
			}
			
			result+="\n";
		}
		
		return result;
	}
	
	public static String printTF(boolean value)
	{
		if(value)
			return "T";
		else
			return "F";
	}

	/**
	 * @return the bits
	 */
	public BitSet[] getBits() {
		return bits;
	}

	/**
	 * @param bits the bits to set
	 */
	public void setBits(BitSet[] bits) {
		this.bits = bits;
	}
	
}
