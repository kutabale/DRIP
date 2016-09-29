
package org.drip.analytics.daycount;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * Copyright (C) 2011 Lakshmi Krishnamurthy
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
 * This class contains parameters to represent Act/Act day count. It exports the following functionality:
 * 	- Frequency/Start/End Date Fields
 *  - Serialization/De-serialization to and from Byte Arrays
 *
 * @author Lakshmi Krishnamurthy
 */

public class ActActDCParams {
	private int _iFreq = 0;
	private int _iNumDays = -1;

	/**
	 * Construct an ActActDCParams from the specified Frequency
	 * 
	 * @param iFreq The Frequency
	 * 
	 * @return The ActActDCParams Instance
	 */

	public static final ActActDCParams FromFrequency (
		final int iFreq)
	{
		try {
			return new ActActDCParams (iFreq, (int) (365.25 / iFreq));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Constructs an ActActDCParams instance from the corresponding parameters
	 * 
	 * @param iFreq Frequency
	 * @param iNumDays Number of Days in the Period
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public ActActDCParams (
		final int iFreq,
		final int iNumDays)
		throws java.lang.Exception
	{
		_iFreq = iFreq;

		if (0 >= (_iFreq = iFreq) || !org.drip.quant.common.NumberUtil.IsValid (_iNumDays = iNumDays) ||
			0 >= _iNumDays)
			throw new java.lang.Exception ("ActActDCParams ctr: Invalid inputs");
	}

	/**
	 * Retrieve the Frequency
	 * 
	 * @return The Frequency
	 */

	public int freq()
	{
		return _iFreq;
	}

	/**
	 * Number of Days in the Act/Act Period
	 * 
	 * @return The Number of Days in the Act/Act Period
	 */

	public int days()
	{
		return _iNumDays;
	}
}
