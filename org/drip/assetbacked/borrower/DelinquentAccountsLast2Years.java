
package org.drip.assetbacked.borrower;

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
 * DelinquentAccountsLast2Years contains the Total Number of Borrower Delinquent Accounts over the Last Two
 *  Years
 *
 * @author Lakshmi Krishnamurthy
 */

public class DelinquentAccountsLast2Years {
	private int _iCount = -1;

	/**
	 * DelinquentAccountsLast2Years Constructor
	 * 
	 * @param iCount Count of the Delinquent Borrower Accounts over the last Two Years
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public DelinquentAccountsLast2Years (
		final int iCount)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_iCount = iCount))
			throw new java.lang.Exception ("DelinquentAccountsLast2Years Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Count of the Delinquent Borrower Accounts over the last Two Years
	 * 
	 * @return The Count of the Delinquent Borrower Accounts over the last Two Years
	 */

	public int count()
	{
		return _iCount;
	}
}
