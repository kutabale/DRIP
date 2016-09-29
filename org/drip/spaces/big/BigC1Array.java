
package org.drip.spaces.big;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * BigC1Array contains the Functionality to Process and Manipulate the Character Array backing the Big
 * 	String.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BigC1Array {
	private int _iLength = -1;
	private char[] _ach = null;

	private int WrapIndex (
		final int iIndex)
	{
		if (iIndex >= _iLength) return iIndex - _iLength;

		if (iIndex < 0) return iIndex + _iLength;

		return iIndex;
	}

	/**
	 * BigC1Array Constructor
	 * 
	 * @param ach Character Array
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BigC1Array (
		final char[] ach)
		throws java.lang.Exception
	{
		if (null == (_ach = ach) || 0 == (_iLength = _ach.length))
			throw new java.lang.Exception ("BigC1Array ctr => Invalid Inputs");
	}

	/**
	 * Retrieve the Character Array
	 * 
	 * @return The Character Array
	 */

	public char[] charArray()
	{
		return _ach;
	}

	/**
	 * Translate the String at around the Pivot Index using the String Block
	 * 
	 * @param iPivotIndex The Pivot Index
	 * @param iBlockSize The Block Size
	 * 
	 * @return TRUE => The Translation succeeded
	 */

	public boolean translateAtPivot (
		final int iPivotIndex,
		final int iBlockSize)
	{
		if (0 >= iPivotIndex || 0 >= iBlockSize) return false;

		int iLength = _ach.length;
		char[] achTemp = new char[iBlockSize];

		if (iPivotIndex >= iLength || iBlockSize >= iLength - iPivotIndex - 1) return false;

		for (int i = iPivotIndex - iBlockSize; i < iPivotIndex; ++i)
			achTemp[i - iPivotIndex + iBlockSize] = _ach[i];

		for (int i = 0; i < iLength - iBlockSize; ++i)
			_ach[WrapIndex (iPivotIndex - iBlockSize + i)] = _ach[WrapIndex (iPivotIndex + i)];

		for (int i = 0; i < iBlockSize; ++i)
			_ach[WrapIndex (iPivotIndex - 2 * iBlockSize + i)] = achTemp[i];

		return true;
	}
}
