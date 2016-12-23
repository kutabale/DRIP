
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
 * CoordinatedVariationTrajectoryGenerator implements the HJB-based Multi Step Optimal Trajectory Cost Step
 *  Evolver using the Coordinated Variation Version of the Stochastic Volatility and the Transaction Function
 *  arising from the Realization of the Market State Variable as described in the "Trading Time" Model. The
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

public class CoordinatedVariationTrajectoryGenerator {
	private double _dblInitialMarketState = java.lang.Double.NaN;
	private org.drip.execution.strategy.OrderSpecification _os = null;
	private org.drip.quant.stochastic.OrnsteinUhlenbeckProcess _oup = null;
	private org.drip.execution.tradingtime.CoordinatedVariation _cv = null;
	private org.drip.execution.risk.MeanVarianceObjectiveUtility _mvou = null;

	/**
	 * CoordinatedVariationTrajectoryGenerator Constructor
	 * 
	 * @param oup The Underlying Market State Ornstein-Uhlenbeck Evolver
	 * @param cv The Coordinated Variation Trading Time Parameters
	 * @param os The Execution Order Specification
	 * @param mvou The Mean Variance Objective Utility Instance
	 * @param dblInitialMarketState The Initial Market State
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CoordinatedVariationTrajectoryGenerator (
		final org.drip.quant.stochastic.OrnsteinUhlenbeckProcess oup,
		final org.drip.execution.tradingtime.CoordinatedVariation cv,
		final org.drip.execution.strategy.OrderSpecification os,
		final org.drip.execution.risk.MeanVarianceObjectiveUtility mvou,
		final double dblInitialMarketState)
		throws java.lang.Exception
	{
		if (null == (_oup = oup) || null == (_cv = cv) || null == (_os = os) || null == (_mvou = mvou) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblInitialMarketState = dblInitialMarketState))
			throw new java.lang.Exception
				("CoordinatedVariationTrajectoryGenerator Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Execution Order Specification
	 * 
	 * @return The Execution Order Specification
	 */

	public org.drip.execution.strategy.OrderSpecification orderSpecification()
	{
		return _os;
	}

	/**
	 * Retrieve the Mean Variance Objective Utility Instance
	 * 
	 * @return The Mean Variance Objective Utility Instance'
	 */

	public org.drip.execution.risk.MeanVarianceObjectiveUtility objectiveUtility()
	{
		return _mvou;
	}

	/**
	 * Retrieve the Initial Market State
	 * 
	 * @return The Initial Market State
	 */

	public double initialMarketState()
	{
		return _dblInitialMarketState;
	}

	/**
	 * Retrieve the Coordinated Variation Trading Time Parameters
	 * 
	 * @return The Coordinated Variation Trading Time Parameters
	 */

	public org.drip.execution.tradingtime.CoordinatedVariation tradingTimeParameters()
	{
		return _cv;
	}

	/**
	 * Retrieve the Underlying Market State Ornstein-Uhlenbeck Evolver
	 * 
	 * @return The Underlying Market State Ornstein-Uhlenbeck Evolver
	 */

	public org.drip.quant.stochastic.OrnsteinUhlenbeckProcess ornsteinUhlenbeckProcess()
	{
		return _oup;
	}

	/**
	 * Generate the Array of the trajectory Non-dimensional Cost Increments
	 * 
	 * @return The Array of the trajectory Non-dimensional Cost Increments
	 */

	public org.drip.execution.adaptive.CoordinatedVariationTrajectoryState[] generate()
	{
		double dblHoldings = _os.size();

		double dblExecutionTime = _os.maxExecutionTime();

		double dblMarketRelaxationTime = _oup.relaxationTime();

		double dblReferenceLiquidity = _cv.referenceLiquidity();

		double dblReferenceVolatility = _cv.referenceVolatility();

		double dblIntrinsicMeanMarketUrgency = java.lang.Math.sqrt (_mvou.riskAversion() *
			dblReferenceVolatility * dblReferenceVolatility / dblReferenceLiquidity);

		double dblMarketState = _dblInitialMarketState;
		org.drip.execution.adaptive.NonDimensionalCost ndc = null;
		org.drip.execution.adaptive.NonDimensionalCostEvolver ndce = null;
		double dblNonDimensionalTimeUnit = dblExecutionTime / dblMarketRelaxationTime;
		int iNumTimeUnit = (int) (dblNonDimensionalTimeUnit) + 1;
		double dblNonDimensionalTradeRateUnit = dblHoldings / dblMarketRelaxationTime;
		double dblNonDimensionalRiskAversion = dblIntrinsicMeanMarketUrgency * dblMarketRelaxationTime;
		double dblNonDimensionalCostUnit = dblReferenceLiquidity * dblHoldings * dblHoldings /
			dblMarketRelaxationTime;
		org.drip.execution.adaptive.CoordinatedVariationTrajectoryState[] aCVTS = new
			org.drip.execution.adaptive.CoordinatedVariationTrajectoryState[iNumTimeUnit];

		try {
			aCVTS[0] = new org.drip.execution.adaptive.CoordinatedVariationTrajectoryState (0., dblHoldings,
				0., 0., dblMarketState);

			ndc = new org.drip.execution.adaptive.NonDimensionalCost (dblMarketState, 0., 0., 0.);

			ndce = new org.drip.execution.adaptive.NonDimensionalCostEvolver (_oup.burstiness(),
				dblNonDimensionalRiskAversion);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 1; i < iNumTimeUnit; ++i) {
			org.drip.quant.stochastic.GenericIncrement giMarketState = _oup.increment (dblMarketState,
				dblNonDimensionalTimeUnit);

			if (null == giMarketState || null == (ndc = ndce.evolve (ndc, dblMarketState = dblMarketState +
				giMarketState.deterministic() + giMarketState.stochastic(), i, dblNonDimensionalTimeUnit)))
				return null;

			double dblTradeRate = dblHoldings / dblMarketRelaxationTime * ndc.nonDimensionalTradeRate();

			dblHoldings = dblHoldings + dblTradeRate * dblNonDimensionalTimeUnit;

			try {
				aCVTS[i] = new org.drip.execution.adaptive.CoordinatedVariationTrajectoryState (i *
					dblNonDimensionalTimeUnit, dblHoldings, dblTradeRate, dblReferenceLiquidity * dblHoldings
						* dblHoldings / dblMarketRelaxationTime * ndc.realization(), dblMarketState);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aCVTS;
	}
}
