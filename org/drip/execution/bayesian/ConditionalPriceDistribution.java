
package org.drip.execution.bayesian;

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
 * ConditionalPriceDistribution holds the Price Distribution Conditional on a given Drift. The References
 *  are:
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets 1
 * 		1-50.
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Brunnermeier, L. K., and L. H. Pedersen (2005): Predatory Trading, Journal of Finance 60 (4) 1825-1863.
 *
 * 	- Almgren, R., and J. Lorenz (2006): Bayesian Adaptive Trading with a Daily Cycle, Journal of Trading 1
 * 		(4) 38-46.
 * 
 * 	- Kissell, R., and R. Malamut (2007): Algorithmic Decision Making Framework, Journal of Trading 1 (1)
 * 		12-21.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ConditionalPriceDistribution extends org.drip.measure.gaussian.R1UnivariateNormal {
	private double _dblTime = java.lang.Double.NaN;
	private double _dblPriceVolatility = java.lang.Double.NaN;
	private double _dblConditionalDrift = java.lang.Double.NaN;

	/**
	 * ConditionalPriceDistribution Constructor
	 * 
	 * @param dblConditionalDrift The Conditional Drift
	 * @param dblPriceVolatility The Price Volatility
	 * @param dblTime The Distribution Time Horizon
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ConditionalPriceDistribution (
		final double dblConditionalDrift,
		final double dblPriceVolatility,
		final double dblTime)
		throws java.lang.Exception
	{
		super (dblConditionalDrift * dblTime, dblPriceVolatility * java.lang.Math.sqrt (dblTime));

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblTime = dblTime) || 0. >= _dblTime ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblConditionalDrift = dblConditionalDrift) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblPriceVolatility = dblPriceVolatility))
			throw new java.lang.Exception ("ConditionalPriceDistribution Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Distribution Time Horizon
	 * 
	 * @return The Distribution Time Horizon
	 */

	public double time()
	{
		return _dblTime;
	}

	/**
	 * Retrieve the Distribution Price Volatility
	 * 
	 * @return The Distribution Price Volatility
	 */

	public double priceVolatility()
	{
		return _dblPriceVolatility;
	}

	/**
	 * Retrieve the Distribution Conditional Drift
	 * 
	 * @return The Distribution Conditional Drift
	 */

	public double conditionalDrift()
	{
		return _dblConditionalDrift;
	}
}
