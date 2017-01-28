
package org.drip.measure.marginal;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * R1LevelRealization implements the Deterministic and the Stochastic Components of a R^1 Marginal Random
 *	Increment as well the Original Marginal Random Variate. The References are:
 * 
 * 	- Almgren, R. F., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3
 * 		(2) 5-39.
 *
 * 	- Almgren, R. F. (2009): Optimal Trading in a Dynamic Market
 * 		https://www.math.nyu.edu/financial_mathematics/content/02_financial/2009-2.pdf.
 *
 * 	- Almgren, R. F. (2012): Optimal Trading with Stochastic Liquidity and Volatility, SIAM Journal of
 * 		Financial Mathematics  3 (1) 163-181.
 * 
 * 	- Geman, H., D. B. Madan, and M. Yor (2001): Time Changes for Levy Processes, Mathematical Finance 11 (1)
 * 		79-96.
 * 
 * 	- Jones, C. M., G. Kaul, and M. L. Lipson (1994): Transactions, Volume, and Volatility, Review of
 * 		Financial Studies 7 (4) 631-651.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class R1LevelRealization {
	private double _dblStart = java.lang.Double.NaN;
	private double _dblJumpWander = java.lang.Double.NaN;
	private double _dblDeterministic = java.lang.Double.NaN;
	private double _dblContinuousWander = java.lang.Double.NaN;
	private double _dblContinuousStochastic = java.lang.Double.NaN;
	private org.drip.measure.process.EventIndicator _eiJumpStochastic = null;

	/**
	 * R1LevelRealization Constructor
	 * 
	 * @param dblStart The Starting Random Variable Realization
	 * @param dblDeterministic The Deterministic Increment Component
	 * @param dblContinuousStochastic The Continuous Stochastic Increment Component
	 * @param dblContinuousWander The Continuous Random Wander Realization
	 * @param eiJumpStochastic The Jump Stochastic Event Indicator
	 * @param dblJumpWander The Jump Random Wander Realization
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public R1LevelRealization (
		final double dblStart,
		final double dblDeterministic,
		final double dblContinuousStochastic,
		final double dblContinuousWander,
		final org.drip.measure.process.EventIndicator eiJumpStochastic,
		final double dblJumpWander)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblStart = dblStart) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblDeterministic = dblDeterministic) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblContinuousStochastic =
					dblContinuousStochastic) || !org.drip.quant.common.NumberUtil.IsValid
						(_dblContinuousWander = dblContinuousWander) ||
							!org.drip.quant.common.NumberUtil.IsValid (_dblJumpWander = dblJumpWander))
			throw new java.lang.Exception ("R1LevelRealization Constructor => Invalid Inputs");

		_eiJumpStochastic = eiJumpStochastic;
	}

	/**
	 * Retrieve the Starting Random Realization
	 * 
	 * @return The Starting Random Realization
	 */

	public double start()
	{
		return _dblStart;
	}

	/**
	 * Retrieve the Deterministic Increment Component
	 * 
	 * @return The Deterministic Increment Component
	 */

	public double deterministic()
	{
		return _dblDeterministic;
	}

	/**
	 * Retrieve the Continuous Stochastic Increment Component
	 * 
	 * @return The Continuous Stochastic Increment Component
	 */

	public double continuousStochastic()
	{
		return _dblContinuousStochastic;
	}

	/**
	 * Retrieve the Continuous Random Wander Realization
	 * 
	 * @return The Continuous Random Wander Realization
	 */

	public double continuousWander()
	{
		return _dblContinuousWander;
	}

	/**
	 * Retrieve the Jump Stochastic Increment Component
	 * 
	 * @return The Jump Stochastic Increment Component
	 */

	public double jumpStochastic()
	{
		return null == _eiJumpStochastic ? 0. : _eiJumpStochastic.magnitude();
	}

	/**
	 * Retrieve the Jump Random Wander Realization
	 * 
	 * @return The Jump Random Wander Realization
	 */

	public double jumpWander()
	{
		return _dblJumpWander;
	}

	/**
	 * Retrieve the Gross Increment
	 * 
	 * @return The Gross Increment
	 */

	public double grossChange()
	{
		return _dblDeterministic + _dblContinuousStochastic + jumpStochastic();
	}

	/**
	 * Retrieve the Next Random Realization
	 * 
	 * @return The Next Random Realization
	 */

	public double finish()
	{
		return _dblStart + _dblDeterministic + _dblContinuousStochastic + jumpStochastic();
	}

	/**
	 * Retrieve the Event Indicator Edge Instance
	 * 
	 * @return The Event Indicator Edge Instance
	 */

	public org.drip.measure.process.EventIndicator jumpStochasticEventIndicator()
	{
		return _eiJumpStochastic;
	}
}
