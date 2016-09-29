
package org.drip.portfolioconstruction.alm;

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
 * ExpectedNonFinancialIncome holds the Parameters required for estimating the Investor's Non-Financial
 *  Income Profile.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ExpectedNonFinancialIncome {
	private double _dblIncomeReplacementRate = java.lang.Double.NaN;

	/**
	 * ExpectedNonFinancialIncome Constructor
	 * 
	 * @param dblIncomeReplacementRate The Pension Based Income Replacement Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ExpectedNonFinancialIncome (
		final double dblIncomeReplacementRate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblIncomeReplacementRate = dblIncomeReplacementRate))
			throw new java.lang.Exception ("ExpectedNonFinancialIncome Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Retirement Age Income Replacement Rate
	 * 
	 * @return The Retirement Age Income Replacement Rate
	 */

	public double incomeReplacementRate()
	{
		return _dblIncomeReplacementRate;
	}

	/**
	 * Compute the Retirement Age Income Replacement Rate
	 * 
	 * @param dblAge The Age whose Investment Phase is needed
	 * @param ics The Investor's Time Cliff Settings Instance
	 * 
	 * @return The Retirement Age Income Replacement Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Retirement Age Income Replacement Rate cannot be computed
	 */

	public double rate (
		final double dblAge,
		final org.drip.portfolioconstruction.alm.InvestorCliffSettings ics)
		throws java.lang.Exception
	{
		if (null == ics)
			throw new java.lang.Exception ("ExpectedNonFinancialIncome::rate => Invalid Inputs");

		int iAgePhase = ics.phase (dblAge);

		if (org.drip.portfolioconstruction.alm.InvestorCliffSettings.DATE_PHASE_BEFORE_RETIREMENT ==
			iAgePhase)
			return 1.;

		if (org.drip.portfolioconstruction.alm.InvestorCliffSettings.DATE_PHASE_AFTER_RETIREMENT ==
			iAgePhase)
			return _dblIncomeReplacementRate;

		return 0.;
	}
}
