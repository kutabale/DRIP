
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
 * NonDimensionalCostEvolver2D implements the 2D HJB-based Single Step Optimal Trajectory Cost Step Evolver
 *  using the 2D Coordinated Variation Version of the Stochastic Volatility and the Transaction Function
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

public class NonDimensionalCostEvolver2D {
	private org.drip.quant.stochastic.OrnsteinUhlenbeckProcess2D _oup2D = null;

	private double advance (
		final org.drip.execution.adaptive.NonDimensionalCost2D ndc2D,
		final double dblLiquidityMarketState,
		final double dblVolatilityMarketState,
		final double dblNonDimensionalRiskAversion)
	{
		org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D oup1DVolatility = _oup2D.secondProcess();

		org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D oup1DLiquidity = _oup2D.firstProcess();

		double dblMu = oup1DLiquidity.relaxationTime() / oup1DVolatility.relaxationTime();

		double dblVolatilityBurstiness = oup1DVolatility.burstiness();

		double dblLiquidityBurstiness = oup1DLiquidity.burstiness();

		double dblNonDimensionalCost = ndc2D.realization();

		return
			dblNonDimensionalRiskAversion * dblNonDimensionalRiskAversion * java.lang.Math.exp (2. *
				dblVolatilityMarketState) -
			dblNonDimensionalCost * dblNonDimensionalCost * java.lang.Math.exp (-dblLiquidityMarketState) +
			_oup2D.correlation() + java.lang.Math.sqrt (dblMu) * dblLiquidityBurstiness *
				dblVolatilityBurstiness * ndc2D.realizationLiquidityVolatilityGradient() +
			0.5 * dblLiquidityBurstiness * dblLiquidityBurstiness * ndc2D.realizationLiquidityJacobian() +
			0.5 * dblMu * dblVolatilityBurstiness * dblVolatilityBurstiness *
				ndc2D.realizationVolatilityJacobian() -
			dblLiquidityMarketState * ndc2D.realizationLiquidityGradient() -
			dblMu * dblVolatilityMarketState * ndc2D.realizationVolatilityGradient();
	}

	/**
	 * Retrieve the Underlying 2D Ornstein-Unlenbeck Process
	 * 
	 * @return The Underlying 2D Ornstein-Unlenbeck Process
	 */

	public org.drip.quant.stochastic.OrnsteinUhlenbeckProcess2D ornsteinUnlenbeckProcess()
	{
		return _oup2D;
	}

	/**
	 * Generate the 2D Non Dimensional Cost Increment
	 * 
	 * @param ndc2D The 2D Non Dimensional Cost
	 * @param dblLiquidityMarketState The Liquidity Market State
	 * @param dblVolatilityMarketState The Volatility Market State
	 * @param dblNonDimensionalRiskAversion The Non-dimensional Risk Aversion Parameter
	 * @param dblNonDimensionalTime The Non Dimensional Time Node
	 * @param dblNonDimensionalTimeIncrement The Non Dimensional Time Increment
	 * 
	 * @return The 2D Non Dimensional Cost Increment
	 */

	public org.drip.execution.adaptive.NonDimensionalCost2D evolve (
		final org.drip.execution.adaptive.NonDimensionalCost2D ndc2D,
		final double dblLiquidityMarketState,
		final double dblVolatilityMarketState,
		final double dblNonDimensionalRiskAversion,
		final double dblNonDimensionalTime,
		final double dblNonDimensionalTimeIncrement)
	{
		if (null == ndc2D || !org.drip.quant.common.NumberUtil.IsValid (dblLiquidityMarketState) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblVolatilityMarketState) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblNonDimensionalRiskAversion) ||
					!org.drip.quant.common.NumberUtil.IsValid (dblNonDimensionalTime) ||
						!org.drip.quant.common.NumberUtil.IsValid (dblNonDimensionalTimeIncrement))
			return null;

		double dblLiquidityMarketStateIncrement = 0.01 * dblLiquidityMarketState;
		double dblVolatilityMarketStateIncrement = 0.01 * dblVolatilityMarketState;

		double dblCostIncrementMid = advance (ndc2D, dblLiquidityMarketState, dblVolatilityMarketState,
			dblNonDimensionalRiskAversion) * dblNonDimensionalTimeIncrement;

		double dblCostIncrementLiquidityUp = advance (ndc2D, dblLiquidityMarketState +
			dblLiquidityMarketStateIncrement, dblVolatilityMarketState, dblNonDimensionalRiskAversion) *
				dblNonDimensionalTimeIncrement;

		double dblCostIncrementLiquidityDown = advance (ndc2D, dblLiquidityMarketState -
			dblLiquidityMarketStateIncrement, dblVolatilityMarketState, dblNonDimensionalRiskAversion) *
				dblNonDimensionalTimeIncrement;

		double dblCostIncrementVolatilityUp = advance (ndc2D, dblLiquidityMarketState,
			dblVolatilityMarketState + dblVolatilityMarketStateIncrement, dblNonDimensionalRiskAversion) *
				dblNonDimensionalTimeIncrement;

		double dblCostIncrementVolatilityDown = advance (ndc2D, dblLiquidityMarketState,
			dblVolatilityMarketState - dblVolatilityMarketStateIncrement, dblNonDimensionalRiskAversion) *
				dblNonDimensionalTimeIncrement;

		double dblCostIncrementCrossUp = advance (ndc2D, dblLiquidityMarketState +
			dblLiquidityMarketStateIncrement, dblVolatilityMarketState + dblVolatilityMarketStateIncrement,
				dblNonDimensionalRiskAversion) * dblNonDimensionalTimeIncrement;

		double dblCostIncrementCrossDown = advance (ndc2D, dblLiquidityMarketState -
			dblLiquidityMarketStateIncrement, dblVolatilityMarketState - dblVolatilityMarketStateIncrement,
				dblNonDimensionalRiskAversion) * dblNonDimensionalTimeIncrement;

		double dblNonDimensionalCost = ndc2D.realization() + dblCostIncrementMid;

		try {
			return new org.drip.execution.adaptive.NonDimensionalCost2D (
				dblNonDimensionalCost,
				0.5 * (dblCostIncrementLiquidityUp - dblCostIncrementLiquidityDown) /
					dblLiquidityMarketStateIncrement,
				(dblCostIncrementLiquidityUp + dblCostIncrementLiquidityDown - 2. * dblCostIncrementMid) /
					(dblLiquidityMarketStateIncrement * dblLiquidityMarketStateIncrement),
				0.5 * (dblCostIncrementVolatilityUp - dblCostIncrementVolatilityDown) /
					dblVolatilityMarketStateIncrement,
				(dblCostIncrementVolatilityUp + dblCostIncrementVolatilityDown - 2. * dblCostIncrementMid) /
					(dblVolatilityMarketStateIncrement * dblVolatilityMarketStateIncrement),
				0.25 * (dblCostIncrementCrossUp - dblCostIncrementCrossDown) /
					(dblLiquidityMarketStateIncrement * dblVolatilityMarketStateIncrement),
				dblNonDimensionalCost * java.lang.Math.exp (-dblLiquidityMarketState));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
