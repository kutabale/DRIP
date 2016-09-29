
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
 * ExpectedBasicConsumption holds the Parameters required for estimating the Investor's Basic Consumption
 * 	Profile.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ExpectedBasicConsumption {
	private double _dblWorkingAgeConsumptionRate = java.lang.Double.NaN;
	private double _dblRetirementAgeConsumptionRate = java.lang.Double.NaN;

	/**
	 * ExpectedBasicConsumption Constructor
	 * 
	 * @param dblWorkingAgeConsumptionRate The Working Age Consumption Rate
	 * @param dblRetirementAgeConsumptionRate The Retirement Age Consumption Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ExpectedBasicConsumption (
		final double dblWorkingAgeConsumptionRate,
		final double dblRetirementAgeConsumptionRate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblWorkingAgeConsumptionRate =
			dblWorkingAgeConsumptionRate) || !org.drip.quant.common.NumberUtil.IsValid
				(_dblRetirementAgeConsumptionRate = dblRetirementAgeConsumptionRate))
			throw new java.lang.Exception ("ExpectedBasicConsumption Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Working Age Consumption Rate
	 * 
	 * @return The Working Age Consumption Rate
	 */

	public double workingAgeConsumptionRate()
	{
		return _dblWorkingAgeConsumptionRate;
	}

	/**
	 * Retrieve the Retirement Age Consumption Rate
	 * 
	 * @return The Retirement Age Consumption Rate
	 */

	public double retirementAgeConsumptionRate()
	{
		return _dblRetirementAgeConsumptionRate;
	}

	/**
	 * Compute the Expected Consumption Rate
	 * 
	 * @param dblAge The Age whose Investment Phase is needed
	 * @param ics The Investor's Time Cliff Settings Instance
	 * 
	 * @return The Expected Consumption Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Expected Consumption Rate cannot be computed
	 */

	public double rate (
		final double dblAge,
		final org.drip.portfolioconstruction.alm.InvestorCliffSettings ics)
		throws java.lang.Exception
	{
		if (null == ics) throw new java.lang.Exception ("ExpectedBasicConsumption::rate => Invalid Inputs");

		int iAgePhase = ics.phase (dblAge);

		if (org.drip.portfolioconstruction.alm.InvestorCliffSettings.DATE_PHASE_BEFORE_RETIREMENT ==
			iAgePhase)
			return _dblWorkingAgeConsumptionRate;

		if (org.drip.portfolioconstruction.alm.InvestorCliffSettings.DATE_PHASE_AFTER_RETIREMENT ==
			iAgePhase)
			return _dblRetirementAgeConsumptionRate;

		return 0.;
	}
}
