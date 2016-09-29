
package org.drip.spaces.iterator;

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
 * IterationHelper contains the Functionality that helps perform Checked Multidimensional Iterative Scans.
 *
 * @author Lakshmi Krishnamurthy
 */

public class IterationHelper {

	/**
	 * Scan through the Integer Array looking for a repeating Index
	 * 
	 * @param ai The Index Array
	 * 
	 * @return TRUE => A Repeating Index exists
	 */

	public static final boolean CheckForRepeatingIndex (
		final int[] ai)
	{
		if (null == ai) return false;

		int iCursorLength = ai.length;

		if (1 >= iCursorLength) return false;

		for (int i = 0; i < iCursorLength; ++i) {
			for (int j = i + 1; j < iCursorLength; ++j) {
				if (ai[i] == ai[j]) return true;
			}
		}

		return false;
	}

	/**
	 * Display the Contents of the Index Array
	 * 
	 * @param strPrefix The Dump Prefix
	 * @param ai The Index Array
	 */

	public static final void DumpIndexArray (
		final java.lang.String strPrefix,
		final int[] ai)
	{
		if (null == ai) return;

		int iNumIndex = ai.length;
		java.lang.String strIndexArray = strPrefix;

		if (0 >= iNumIndex) return;

		for (int i = 0; i < iNumIndex; ++i)
			strIndexArray += (0 == i ? "[" : ",") + ai[i];

		System.out.println (strIndexArray + "]");
	}

	/**
	 * Compose a String constructed from the specified Array Index
	 * 
	 * @param strMaster The Master String
	 * @param aiIndex The Index Array
	 * 
	 * @return The Composed String
	 */

	public static final java.lang.String ComposeFromIndex (
		final java.lang.String strMaster,
		final int[] aiIndex)
	{
		if (null == aiIndex) return null;

		int iNumIndex = aiIndex.length;
		java.lang.String strOffOfIndex = "";

		if (0 >= iNumIndex) return null;

		for (int i = 0; i < iNumIndex; ++i)
			strOffOfIndex += strMaster.charAt (aiIndex[i]);

		return strOffOfIndex;
	}
}
