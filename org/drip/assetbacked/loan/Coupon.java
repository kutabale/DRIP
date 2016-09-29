
package org.drip.assetbacked.loan;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * Coupon contains the current Loan Annualized Coupon Rate and Frequency
 *
 * @author Lakshmi Krishnamurthy
 */

public class Coupon {
	private int _iFrequency = -1;
	private double _dblRate = java.lang.Double.NaN;

	/**
	 * Coupon Constructor
	 * 
	 * @param dblRate Loan Coupon Rate
	 * @param iFrequency Loan Coupon Frequency
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public Coupon (
		final double dblRate,
		final int iFrequency)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblRate = dblRate) || 0 >= (_iFrequency =
			iFrequency))
			throw new java.lang.Exception ("Coupon Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Loan Coupon Frequency
	 * 
	 * @return The Loan Coupon Frequency
	 */

	public int frequency()
	{
		return _iFrequency;
	}

	/**
	 * Retrieve the Loan Coupon Rate
	 * 
	 * @return The Loan Coupon Rate
	 */

	public double rate()
	{
		return _dblRate;
	}
}
