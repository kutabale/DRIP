
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
 * NonDimensionalCostEvolver1D implements the 1D HJB-based Single Step Optimal Trajectory Cost Step Evolver
 *  using the 1D Coordinated Variation Version of the Stochastic Volatility and the Transaction Function
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

public class NonDimensionalCostEvolver1D {
	private static final double SINGULAR_URGENCY_THRESHOLD = 50.;

	private boolean _bAsymptoticEnhancedEulerCorrection = false;
	private org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D _oup = null;
	private double _dblAsymptoticEulerUrgencyThreshold = java.lang.Double.NaN;

	/**
	 * Construct a Standard NonDimensionalCostEvolver1D Instance
	 * 
	 * @param oup The Underlying Ornstein-Unlenbeck Process
	 * 
	 * @return The Standard NonDimensionalCostEvolver1D Instance
	 */

	public static final NonDimensionalCostEvolver1D Standard (
		final org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D oup)
	{
		try {
			return new NonDimensionalCostEvolver1D (oup, SINGULAR_URGENCY_THRESHOLD, true);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private double advance (
		final org.drip.execution.adaptive.NonDimensionalCost1D ndcInitial,
		final double dblMarketState,
		final double dblNonDimensionalRiskAversion)
	{
		double dblBurstiness = _oup.burstiness();

		double dblNonDimensionalCost = ndcInitial.realization();

		return java.lang.Math.exp (-dblMarketState) * (dblNonDimensionalRiskAversion *
			dblNonDimensionalRiskAversion - dblNonDimensionalCost * dblNonDimensionalCost) + 0.5 *
				dblBurstiness * dblBurstiness * ndcInitial.realizationJacobian() - dblMarketState *
					ndcInitial.realizationGradient();
	}

	/**
	 * NonDimensionalCostEvolver1D Constructor
	 * 
	 * @param oup The Underlying Ornstein-Unlenbeck Process
	 * @param bAsymptoticEnhancedEulerCorrection Asymptotic Enhanced Euler Correction Application Flag
	 * @param dblAsymptoticEulerUrgencyThreshold The Asymptotic Euler Urgency Threshold
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NonDimensionalCostEvolver1D (
		final org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D oup,
		final double dblAsymptoticEulerUrgencyThreshold,
		final boolean bAsymptoticEnhancedEulerCorrection)
		throws java.lang.Exception
	{
		if (null == (_oup = oup) || !org.drip.quant.common.NumberUtil.IsValid
			(_dblAsymptoticEulerUrgencyThreshold = dblAsymptoticEulerUrgencyThreshold))
			throw new java.lang.Exception ("NonDimensionalCostEvolver1D Constructor => Invalid Inputs");

		_bAsymptoticEnhancedEulerCorrection = bAsymptoticEnhancedEulerCorrection;
	}

	/**
	 * Retrieve the Asymptotic Enhanced Euler Correction Application Flag
	 * 
	 * @return The Asymptotic Enhanced Euler Correction Application Flag
	 */

	public boolean asymptoticEnhancedEulerCorrection()
	{
		return _bAsymptoticEnhancedEulerCorrection;
	}

	/**
	 * Retrieve the Asymptotic Euler Urgency Threshold
	 * 
	 * @return The Asymptotic Euler Urgency Threshold
	 */

	public double asymptoticEulerUrgencyThreshold()
	{
		return _dblAsymptoticEulerUrgencyThreshold;
	}

	/**
	 * Retrieve the Underlying Ornstein-Unlenbeck Process
	 * 
	 * @return The Underlying Ornstein-Unlenbeck Process
	 */

	public org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D ornsteinUnlenbeckProcess()
	{
		return _oup;
	}

	/**
	 * Evolve a Single Time Step of the Optimal Trajectory
	 * 
	 * @param ndcInitial The Initial Non-dimensional Cost Value Function
	 * @param ms The Market State
	 * @param dblNonDimensionalRiskAversion The Non-dimensional Risk Aversion Parameter
	 * @param dblNonDimensionalTime The Non Dimensional Time Node
	 * @param dblNonDimensionalTimeIncrement The Non Dimensional Time Increment
	 * 
	 * @return The Post Evolved Non-dimensional Cost Value Function
	 */

	public org.drip.execution.adaptive.NonDimensionalCost1D evolve (
		final org.drip.execution.adaptive.NonDimensionalCost1D ndcInitial,
		final org.drip.execution.latent.MarketState ms,
		final double dblNonDimensionalRiskAversion,
		final double dblNonDimensionalTime,
		final double dblNonDimensionalTimeIncrement)
	{
		if (null == ndcInitial || null == ms || !org.drip.quant.common.NumberUtil.IsValid
			(dblNonDimensionalRiskAversion) || !org.drip.quant.common.NumberUtil.IsValid
				(dblNonDimensionalTime) || !org.drip.quant.common.NumberUtil.IsValid
					(dblNonDimensionalTimeIncrement))
			return null;

		double dblMarketState = ms.liquidity();

		double dblMarketStateIncrement = 0.01 * dblMarketState;

		double dblMarketStateExponentiation = java.lang.Math.exp (dblMarketState);

		if (_dblAsymptoticEulerUrgencyThreshold * dblNonDimensionalTime < 1.) {
			if (!_bAsymptoticEnhancedEulerCorrection)
				return org.drip.execution.adaptive.NonDimensionalCost1D.LinearThreshold
					(dblMarketStateExponentiation, dblNonDimensionalTime);

			double dblBurstiness = _oup.burstiness();

			double dblNonDimensionalCostCross = -0.5 * dblMarketState * dblMarketStateExponentiation;

			return org.drip.execution.adaptive.NonDimensionalCost1D.EulerEnhancedLinearThreshold
				(dblMarketState, ((1. / dblNonDimensionalTimeIncrement) + 0.25 * dblBurstiness *
					dblBurstiness) * java.lang.Math.exp (dblMarketState) + dblNonDimensionalCostCross,
						dblNonDimensionalCostCross);
		}

		double dblCostIncrementMid = advance (ndcInitial, dblMarketState, dblNonDimensionalRiskAversion) *
			dblNonDimensionalTimeIncrement;

		double dblCostIncrementUp = advance (ndcInitial, dblMarketState + dblMarketStateIncrement,
			dblNonDimensionalRiskAversion) * dblNonDimensionalTimeIncrement;

		double dblCostIncrementDown = advance (ndcInitial, dblMarketState - dblMarketStateIncrement,
			dblNonDimensionalRiskAversion) * dblNonDimensionalTimeIncrement;

		double dblCost = ndcInitial.realization() + dblCostIncrementMid;

		try {
			return new org.drip.execution.adaptive.NonDimensionalCost1D (dblCost, 0.5 * (dblCostIncrementUp -
				dblCostIncrementDown) / dblMarketStateIncrement, (dblCostIncrementUp + dblCostIncrementDown -
					2. * dblCostIncrementMid) / (dblMarketStateIncrement * dblMarketStateIncrement),
						dblCost / dblMarketStateExponentiation);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
