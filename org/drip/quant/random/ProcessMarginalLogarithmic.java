
package org.drip.quant.random;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * ProcessMarginalLogarithmic guides the Random Variable Evolution according to 1D Logarithmic Process.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ProcessMarginalLogarithmic extends org.drip.quant.random.ProcessMarginal {
	private double _dblDrift = java.lang.Double.NaN;
	private double _dblVolatility = java.lang.Double.NaN;

	/**
	 * ProcessMarginalLogarithmic Constructor
	 * 
	 * @param dblDrift The Drift
	 * @param dblVolatility The Volatility
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ProcessMarginalLogarithmic (
		final double dblDrift,
		final double dblVolatility)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDrift = dblDrift) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblVolatility = dblVolatility))
			throw new java.lang.Exception ("ProcessMarginalLogarithmic Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Drift of the Log Process
	 * 
	 * @return Drift of the Log Process
	 */

	public double drift()
	{
		return _dblDrift;
	}

	/**
	 * Retrieve the Volatility of the Log Process
	 * 
	 * @return Volatility of the Log Process
	 */

	public double volatility()
	{
		return _dblVolatility;
	}

	@Override public org.drip.quant.random.GenericIncrement increment (
		final double dblRandomVariate,
		final double dblRandomUnitRealization,
		final double dblTimeIncrement)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblRandomVariate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblRandomUnitRealization) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement) || 0. >= dblTimeIncrement)
			return null;

		try {
			return new org.drip.quant.random.GenericIncrement (dblRandomVariate * _dblDrift *
				dblTimeIncrement, dblRandomVariate * _dblVolatility * dblRandomUnitRealization *
					java.lang.Math.sqrt (dblTimeIncrement), dblRandomUnitRealization);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
