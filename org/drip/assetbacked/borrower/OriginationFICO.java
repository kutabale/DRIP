
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
 * OriginationFICO contains the Borrower's FICO Score at a given Loan's Origination.
 *
 * @author Lakshmi Krishnamurthy
 */

public class OriginationFICO {
	private double _dblFICO = java.lang.Double.NaN;

	/**
	 * OriginationFICO Constructor
	 * 
	 * @param dblFICO The Borrower's FICO Score at Origination
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public OriginationFICO (
		final double dblFICO)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblFICO = dblFICO))
			throw new java.lang.Exception ("OriginationFICO Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Borrower's FICO Score at Origination
	 * 
	 * @return The Borrower's FICO Score at Origination
	 */

	public double score()
	{
		return _dblFICO;
	}
}
