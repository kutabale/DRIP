
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
	private org.drip.execution.strategy.OrderSpecification _os = null;
	private org.drip.execution.tradingtime.CoordinatedVariation _cv = null;
	private org.drip.execution.adaptive.NonDimensionalCostEvolver _ndce = null;

	/**
	 * ContinuousCoordinatedVariation Constructor
	 * 
	 * @param os The Order Specification
	 * @param cv The Coordinated Variation Instance
	 * @param ndce The Non Dimensional Cost Evolver
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ContinuousCoordinatedVariation (
		final org.drip.execution.strategy.OrderSpecification os,
		final org.drip.execution.tradingtime.CoordinatedVariation cv,
		final org.drip.execution.adaptive.NonDimensionalCostEvolver ndce)
		throws java.lang.Exception
	{
		if (null == (_os = os) || null == (_cv = cv) || null == (_ndce = ndce))
			throw new java.lang.Exception ("ContinuousCoordinatedVariation Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Order Specification
	 * 
	 * @return The Order Specification
	 */

	public org.drip.execution.strategy.OrderSpecification orderSpecification()
	{
		return _os;
	}

	/**
	 * Retrieve the Coordinated Variation Instance
	 * 
	 * @return The Coordinated Variation Instance
	 */

	public org.drip.execution.tradingtime.CoordinatedVariation coordinatedVariationConstraint()
	{
		return _cv;
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
	 * Generate the Continuous Coordinated Variation Dynamic Trajectory
	 * 
	 * @param adblMarketState Array of Realized Market States
	 * 
	 * @return The Continuous Coordinated Variation Dynamic Trajectory
	 */

	public org.drip.execution.adaptive.ContinuousCoordinatedVariationDynamic generate (
		final double[] adblMarketState)
	{
		if (null == adblMarketState || !org.drip.quant.common.NumberUtil.IsValid (adblMarketState))
			return null;

		int iNumTimeNode = adblMarketState.length;

		if (1 >= iNumTimeNode) return null;

		double dblExecutionSize = _os.size();

		double dblTimeIncrement = _os.maxExecutionTime() / (iNumTimeNode - 1);

		double dblNonDimensionalCost = _cv.referenceLiquidity() * dblExecutionSize * dblExecutionSize /
			_ndce.ornsteinUnlenbeckProcess().relaxationTime();

		double[] adblNonDimensionalHoldings = new double[iNumTimeNode];
		double[] adblNonDimensionalAdjustedTradeRate = new double[iNumTimeNode];
		adblNonDimensionalHoldings[0] = 1.;
		adblNonDimensionalAdjustedTradeRate[0] = 0.;
		org.drip.execution.adaptive.NonDimensionalCost[] aNDC = new
			org.drip.execution.adaptive.NonDimensionalCost[iNumTimeNode];

		if (null == (aNDC[0] = org.drip.execution.adaptive.NonDimensionalCost.Zero())) return null;

		for (int i = 1; i < iNumTimeNode; ++i) {
			if (null == (aNDC[i] = _ndce.evolve (aNDC[i - 1], adblMarketState[i], (iNumTimeNode - i) *
				dblTimeIncrement, dblTimeIncrement)))
				return null;

			adblNonDimensionalAdjustedTradeRate[i] = aNDC[i].nonDimensionalTradeRate();

			if (adblNonDimensionalHoldings[i - 1] > 0.)
				adblNonDimensionalHoldings[i] = adblNonDimensionalHoldings[i - 1] -
					aNDC[i].nonDimensionalTradeRate() * dblTimeIncrement;

			if (adblNonDimensionalHoldings[i] <= 0.) {
				adblNonDimensionalHoldings[i] = 0.;
				adblNonDimensionalAdjustedTradeRate[i] = 0.;
			}
		}

		try {
			return new org.drip.execution.adaptive.ContinuousCoordinatedVariationDynamic (dblExecutionSize,
				aNDC, adblNonDimensionalHoldings, adblNonDimensionalAdjustedTradeRate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
