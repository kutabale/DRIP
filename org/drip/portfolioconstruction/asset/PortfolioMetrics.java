
package org.drip.portfolioconstruction.asset;

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
 * PortfolioMetrics holds the Expected Portfolio Returns and the Standard Deviation.
 *
 * @author Lakshmi Krishnamurthy
 */

public class PortfolioMetrics {
	private double[] _adblImpliedBeta = null;
	private double _dblSharpeRatio = java.lang.Double.NaN;
	private double _dblExcessReturnsMean = java.lang.Double.NaN;
	private double _dblExcessReturnsVariance = java.lang.Double.NaN;
	private double _dblExcessReturnsStandardDeviation = java.lang.Double.NaN;

	/**
	 * PortfolioMetrics Constructor
	 * 
	 * @param dblExcessReturnsMean The Expected Portfolio Excess Returns Mean
	 * @param dblExcessReturnsVariance The Portfolio Excess Returns Variance
	 * @param dblExcessReturnsStandardDeviation The Excess Returns Portfolio Standard Deviation
	 * @param dblSharpeRatio Portfolio Sharpe Ratio
	 * @param adblImpliedBeta Portfolio Implied Beta Vector
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public PortfolioMetrics (
		final double dblExcessReturnsMean,
		final double dblExcessReturnsVariance,
		final double dblExcessReturnsStandardDeviation,
		final double dblSharpeRatio,
		final double[] adblImpliedBeta)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblExcessReturnsMean = dblExcessReturnsMean) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblExcessReturnsVariance = dblExcessReturnsVariance)
				|| !org.drip.quant.common.NumberUtil.IsValid (_dblExcessReturnsStandardDeviation =
					dblExcessReturnsStandardDeviation)|| !org.drip.quant.common.NumberUtil.IsValid
						(_dblSharpeRatio = dblSharpeRatio) || null == (_adblImpliedBeta = adblImpliedBeta) ||
							0 == _adblImpliedBeta.length)
			throw new java.lang.Exception ("PortfolioMetrics Constructor => Invalid Inputs!");
	}

	/**
	 * Retrieve the Portfolio Expected Excess Returns
	 * 
	 * @return The Portfolio Expected Excess Returns
	 */

	public double excessReturnsMean()
	{
		return _dblExcessReturnsMean;
	}

	/**
	 * Retrieve the Portfolio Excess Returns Variance
	 * 
	 * @return The Portfolio Excess Returns Variance
	 */

	public double excessReturnsVariance()
	{
		return _dblExcessReturnsVariance;
	}

	/**
	 * Retrieve the Portfolio Excess Returns Standard Deviation
	 * 
	 * @return The Portfolio Excess Returns Standard Deviation
	 */

	public double excessReturnsStandardDeviation()
	{
		return _dblExcessReturnsStandardDeviation;
	}

	/**
	 * Retrieve the Portfolio Sharpe Ratio
	 * 
	 * @return The Portfolio Sharpe Ratio
	 */

	public double sharpeRatio()
	{
		return _dblSharpeRatio;
	}

	/**
	 * Retrieve the Portfolio Implied Beta Vector
	 * 
	 * @return The Portfolio Implied Beta Vector
	 */

	public double[] impliedBeta()
	{
		return _adblImpliedBeta;
	}
}
