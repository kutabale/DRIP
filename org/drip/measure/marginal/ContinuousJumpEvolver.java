
package org.drip.measure.marginal;

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
 * ContinuousJumpEvolver implements the Functionality that guides the Single Factor R^1 Continuous Jump
 *  Random Process Variable Evolution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ContinuousJumpEvolver extends org.drip.measure.marginal.ContinuousEvolver {
	private org.drip.measure.process.HazardEventIndicationEvaluator _heie = null;

	/**
	 * ContinuousJumpEvolver Constructor
	 * 
	 * @param ldevDrift The LDEV Drift Function of the Marginal Process
	 * @param ldevVolatility The LDEV Volatility Function of the Marginal Process Continuous Component
	 * @param heie The Hazard Point Event Indicator Function Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ContinuousJumpEvolver (
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility,
		final org.drip.measure.process.HazardEventIndicationEvaluator heie)
		throws java.lang.Exception
	{
		super (ldevDrift, ldevVolatility);

		if (null == (_heie = heie))
			throw new java.lang.Exception ("ContinuousJumpEvolver Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Hazard Point Event Indicator Instance
	 * 
	 * @return The Hazard Point Event Indicator Instance
	 */

	public org.drip.measure.process.HazardEventIndicationEvaluator eventIndicationEvaluator()
	{
		return _heie;
	}

	protected org.drip.measure.process.LevelHazardEventIndication hazardEventIndication (
		final org.drip.measure.realization.JumpDiffusionVertex r1s,
		final org.drip.measure.realization.JumpDiffusionUnit r1ur,
		final double dblTimeIncrement)
	{
		double dblHazardRate = _heie.hazardRate();

		double dblJumpRandomUnitRealization = r1ur.jump();

		double dblLevelHazardIntegral = dblHazardRate * dblTimeIncrement;

		boolean bEventOccurred = java.lang.Math.exp (-1. * (r1s.cumulativeHazardIntegral() +
			dblLevelHazardIntegral)) <= dblJumpRandomUnitRealization;

		try {
			return new org.drip.measure.process.LevelHazardEventIndication (bEventOccurred, dblHazardRate,
				dblLevelHazardIntegral, bEventOccurred ? _heie.magnitudeLDEV().value (r1s) : 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate
	 * 
	 * @param r1s The Random Variate Marginal Snap
	 * @param r1ur The R^1 Unit Realization
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.realization.JumpDiffusionLevel increment (
		final org.drip.measure.realization.JumpDiffusionVertex r1s,
		final org.drip.measure.realization.JumpDiffusionUnit r1ur,
		final double dblTimeIncrement)
	{
		if (null == r1s || null == r1ur || !org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement))
			return null;

		double dblPreviousValue = r1s.value();

		double dblHazardRate = _heie.hazardRate();

		double dblJumpRandomUnitRealization = r1ur.jump();

		double dblLevelHazardIntegral = dblHazardRate * dblTimeIncrement;

		boolean bEventOccurred = java.lang.Math.exp (-1. * (r1s.cumulativeHazardIntegral() +
			dblLevelHazardIntegral)) <= dblJumpRandomUnitRealization;

		try {
			return bEventOccurred && _heie.isJumpTerminal() ? new
				org.drip.measure.realization.JumpDiffusionLevel (dblPreviousValue, 0., 0., new
					org.drip.measure.process.LevelHazardEventIndication (bEventOccurred, dblHazardRate,
						dblLevelHazardIntegral, _heie.magnitudeLDEV().value (r1s) - dblPreviousValue), new
							org.drip.measure.realization.JumpDiffusionUnit (0.,
								dblJumpRandomUnitRealization)) : super.increment (r1s, r1ur,
									dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
