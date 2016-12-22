
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
 * NonDimensionalCostEvolver implements the HJB-based Single Step Optimal Trajectory Cost Step Evolver using
 *  the Coordinated Variation Version of the Stochastic Volatility and the Transaction Function arising from
 *  the Realization of the Market State Variable as described in the "Trading Time" Model. The References
 *  are:
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

public class NonDimensionalCostEvolver {
	private double _dblDimensionlessBurstiness = java.lang.Double.NaN;
	private double _dblDimensionlessRiskAversion = java.lang.Double.NaN;

	private double advance (
		final org.drip.execution.adaptive.NonDimensionalCost ndcInitial,
		final double dblMarketState)
	{
		double dblNondimensionalCost = ndcInitial.realization();

		return java.lang.Math.exp (-dblMarketState) * (_dblDimensionlessRiskAversion *
			_dblDimensionlessRiskAversion - dblNondimensionalCost * dblNondimensionalCost) + 0.5 *
				_dblDimensionlessBurstiness * _dblDimensionlessBurstiness * ndcInitial.realizationJacobian()
					- dblMarketState * ndcInitial.realizationGradient();
	}

	/**
	 * NonDimensionalCostEvolver Constructor
	 * 
	 * @param dblDimensionlessBurstiness The Non-dimensional Burstiness Parameter
	 * @param dblDimensionlessRiskAversion The Non-dimensional Risk Aversion Parameter
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NonDimensionalCostEvolver (
		final double dblDimensionlessBurstiness,
		final double dblDimensionlessRiskAversion)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDimensionlessBurstiness =
			dblDimensionlessBurstiness) || !org.drip.quant.common.NumberUtil.IsValid
				(_dblDimensionlessRiskAversion = dblDimensionlessRiskAversion))
			throw new java.lang.Exception ("NonDimensionalCostEvolver Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Non-dimensional Burstiness Parameter
	 * 
	 * @return The Non-dimensional Burstiness Parameter
	 */

	public double dimensionlessBurstiness()
	{
		return _dblDimensionlessBurstiness;
	}

	/**
	 * Retrieve the Non-dimensional Risk Aversion Parameter
	 * 
	 * @return The Non-dimensional Risk Aversion Parameter
	 */

	public double dimensionlessRiskAversion()
	{
		return _dblDimensionlessRiskAversion;
	}

	/**
	 * Evolve a Single Time Step of the Optimal Trajectory
	 * 
	 * @param ndcInitial The Initial Non-dimensional Cost Value Function
	 * @param dblMarketState The Non-dimensional Market State
	 * 
	 * @return The Post Evolved Non-dimensional Cost Value Function
	 */

	public org.drip.execution.adaptive.NonDimensionalCost evolve (
		final org.drip.execution.adaptive.NonDimensionalCost ndcInitial,
		final double dblMarketState)
	{
		if (null == ndcInitial || !org.drip.quant.common.NumberUtil.IsValid (dblMarketState)) return null;

		double dblMarketStateIncrement = 0.01 * dblMarketState;

		double dblCostAdvanceUp = advance (ndcInitial, dblMarketState + dblMarketStateIncrement);

		double dblCostAdvanceMid = advance (ndcInitial, dblMarketState);

		double dblCostAdvanceDown = advance (ndcInitial, dblMarketState - dblMarketStateIncrement);

		try {
			return new org.drip.execution.adaptive.NonDimensionalCost (dblCostAdvanceMid, 0.5 *
				(dblCostAdvanceUp - dblCostAdvanceDown) / dblMarketStateIncrement, (dblCostAdvanceUp +
					dblCostAdvanceDown - 2. * dblCostAdvanceMid) / dblMarketStateIncrement,
						java.lang.Math.exp (-dblMarketState) * dblCostAdvanceMid);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
