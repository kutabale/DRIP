
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
 * OriginalPrincipal contains the Origination Loan Principal.
 *
 * @author Lakshmi Krishnamurthy
 */

public class OriginalPrincipal {
	private double _dblAmount = java.lang.Double.NaN;

	/**
	 * OriginalPrincipal Constructor
	 * 
	 * @param dblAmount The Original Principal Amount
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public OriginalPrincipal (
		final double dblAmount)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblAmount = dblAmount))
			throw new java.lang.Exception ("OriginalPrincipal Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Original Principal Amount
	 * 
	 * @return The Original Principal Amount
	 */

	public double amount()
	{
		return _dblAmount;
	}
}
