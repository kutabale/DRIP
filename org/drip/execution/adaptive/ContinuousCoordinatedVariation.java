
package org.drip.execution.adaptive;

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
 * ContinuousCoordinatedVariation implements the Continuous HJB-based Single Step Optimal Cost Trajectory
 *  using the Coordinated Variation Version of the Stochastic Volatility and the Transaction Function arising
 *  from the Realization of the Market State Variable as described in the "Trading Time" Model. The
 *  References are:
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
 * 		Financial Studies & (4) 631-651.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ContinuousCoordinatedVariation {
	private double _dblExecutionTime = java.lang.Double.NaN;
	private double _dblTimeIncrement = java.lang.Double.NaN;
	private org.drip.execution.adaptive.NonDimensionalCostEvolver _ndce = null;

	/**
	 * ContinuousCoordinatedVariation Constructor
	 * 
	 * @param dblExecutionTime The Execution Time
	 * @param dblTimeIncrement The Evolution Time Increment
	 * @param ndce The Non Dimensional Cost Evolver
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ContinuousCoordinatedVariation (
		final double dblExecutionTime,
		final double dblTimeIncrement,
		final org.drip.execution.adaptive.NonDimensionalCostEvolver ndce)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblExecutionTime = dblExecutionTime) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblTimeIncrement = dblTimeIncrement) || 0. >=
				_dblTimeIncrement || null == (_ndce = ndce))
			throw new java.lang.Exception ("ContinuousCoordinatedVariation Constructor => Invalid Inputs");

		if (_dblTimeIncrement > _dblExecutionTime) _dblTimeIncrement = _dblExecutionTime;
	}

	/**
	 * Retrieve the Non Dimensional Cost Evolver
	 * 
	 * @return The Non Dimensional Cost Evolver
	 */

	public org.drip.execution.adaptive.NonDimensionalCostEvolver evolver()
	{
		return _ndce;
	}

	/**
	 * Retrieve the Execution Time
	 * 
	 * @return The Execution Time
	 */

	public double executionTime()
	{
		return _dblExecutionTime;
	}

	/**
	 * Retrieve the Evolution Time Increment
	 * 
	 * @return The Evolution Time Increment
	 */

	public double timeIncrement()
	{
		return _dblTimeIncrement;
	}

	public org.drip.execution.adaptive.NonDimensionalCost[] generate (
		final double dblInitialMarketState)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblInitialMarketState)) return null;

		org.drip.quant.stochastic.OrnsteinUhlenbeckProcess oup = _ndce.ornsteinUnlenbeckProcess();

		int iNumTimeNode = (int) (_dblExecutionTime / _dblTimeIncrement);
		double[] adblNonDimensionalTradeRate = new double[iNumTimeNode + 1];
		double[] adblNonDimensionalHoldings = new double[iNumTimeNode + 1];
		double[] adblMarketState = new double[iNumTimeNode + 1];
		adblMarketState[0] = dblInitialMarketState;
		adblNonDimensionalHoldings[0] = 1.;
		adblNonDimensionalTradeRate[0] = 0.;
		org.drip.execution.adaptive.NonDimensionalCost[] aNDC = new
			org.drip.execution.adaptive.NonDimensionalCost[iNumTimeNode + 1];

		if (null == (aNDC[0] = org.drip.execution.adaptive.NonDimensionalCost.Zero())) return null;

		for (int i = 0; i < iNumTimeNode; ++i) {
			org.drip.quant.stochastic.GenericIncrement gi = oup.increment (adblMarketState[i],
				_dblTimeIncrement);

			if (null == gi) return null;

			adblMarketState[i + 1] = adblMarketState[i] + gi.deterministic() + gi.stochastic();
		}

		for (int i = 1; i < iNumTimeNode; ++i) {
			if (null == (aNDC[i] = _ndce.evolve (aNDC[i - 1], adblMarketState[i], (iNumTimeNode - i) *
				_dblTimeIncrement, _dblTimeIncrement)))
				return null;

			adblNonDimensionalTradeRate[i] = aNDC[i].nonDimensionalTradeRate();
		}

		return aNDC;
	}
}
