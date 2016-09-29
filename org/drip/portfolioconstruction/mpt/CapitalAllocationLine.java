
package org.drip.portfolioconstruction.mpt;

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
 * CapitalAllocationLine implements the Efficient Half-line created from the Combination of the Risk Free
 * 	Asset and the Tangency Point of the CAPM Market Portfolio.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CapitalAllocationLine {
	private double _dblRiskFreeRate = java.lang.Double.NaN;
	private org.drip.portfolioconstruction.asset.PortfolioMetrics _pmTangency = null;

	/**
	 * CapitalAllocationLine Constructor
	 * 
	 * @param dblRiskFreeRate The Risk Free Rate
	 * @param pmTangency The Tangency Portfolio Metrics
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CapitalAllocationLine (
		final double dblRiskFreeRate,
		final org.drip.portfolioconstruction.asset.PortfolioMetrics pmTangency)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblRiskFreeRate = dblRiskFreeRate) || null ==
			(_pmTangency = pmTangency))
			throw new java.lang.Exception ("CapitalAllocationLine Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Risk-Free Rate
	 * 
	 * @return The Risk-Free Rate
	 */

	public double riskFreeRate()
	{
		return _dblRiskFreeRate;
	}

	/**
	 * Retrieve the Tangency Portfolio Metrics
	 * 
	 * @return The Tangency Portfolio Metrics
	 */

	public org.drip.portfolioconstruction.asset.PortfolioMetrics tangencyPortfolioMetrics()
	{
		return _pmTangency;
	}

	/**
	 * Calculate the Combination Portfolio's Expected Returns from the corresponding Standard Deviation
	 * 
	 * @param dblCombinationPortfolioStandardDeviation The Combination Portfolio's Standard Deviation
	 * 
	 * @return The Combination Portfolio's Expected Returns
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double combinationPortfolioExpectedReturn (
		final double dblCombinationPortfolioStandardDeviation)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCombinationPortfolioStandardDeviation))
			throw new java.lang.Exception
				("CapitalAllocationLine::combinationPortfolioExpectedReturn => Invalid Inputs");

		return _dblRiskFreeRate + dblCombinationPortfolioStandardDeviation * (_pmTangency.excessReturnsMean()
			- _dblRiskFreeRate) / _pmTangency.excessReturnsStandardDeviation();
	}

	/**
	 * Compute the Combination Portfolio's Standard Deviation
	 * 
	 * @param dblCombinationPortfolioExpectedReturn The Expected Returns of the Combination Portfolio
	 * 
	 * @return The Combination Portfolio's Standard Deviation
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double combinationPortfolioStandardDeviation (
		final double dblCombinationPortfolioExpectedReturn)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCombinationPortfolioExpectedReturn))
			throw new java.lang.Exception
				("CapitalAllocationLine::combinationPortfolioStandardDeviation => Invalid Inputs");

		return (dblCombinationPortfolioExpectedReturn - _dblRiskFreeRate) / (_pmTangency.excessReturnsMean()
			- _dblRiskFreeRate) * _pmTangency.excessReturnsStandardDeviation();
	}
}
