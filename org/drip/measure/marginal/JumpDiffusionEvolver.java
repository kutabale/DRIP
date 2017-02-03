
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
 * JumpDiffusionEvolver implements the Functionality that guides the Single Factor R^1 Jump Diffusion Random
 *  Process Variable Evolution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class JumpDiffusionEvolver extends org.drip.measure.marginal.DiffusionEvolver {
	private org.drip.measure.process.HazardJumpIndicationEvaluator _heie = null;

	/**
	 * JumpDiffusionEvolver Constructor
	 * 
	 * @param ldevDrift The LDEV Drift Function of the Marginal Process
	 * @param ldevVolatility The LDEV Volatility Function of the Marginal Process Continuous Component
	 * @param heie The Hazard Point Event Indicator Function Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public JumpDiffusionEvolver (
		final org.drip.measure.process.LocalDeterministicEvaluator ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvaluator ldevVolatility,
		final org.drip.measure.process.HazardJumpIndicationEvaluator heie)
		throws java.lang.Exception
	{
		super (ldevDrift, ldevVolatility);

		if (null == (_heie = heie))
			throw new java.lang.Exception ("JumpDiffusionEvolver Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Hazard Point Event Indicator Instance
	 * 
	 * @return The Hazard Point Event Indicator Instance
	 */

	public org.drip.measure.process.HazardJumpIndicationEvaluator eventIndicationEvaluator()
	{
		return _heie;
	}

	@Override protected org.drip.measure.realization.JumpIndicationEdge eventIndicationDAG (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final org.drip.measure.realization.JumpDiffusionUnit jdu,
		final double dblTimeIncrement)
	{
		double dblHazardRate = _heie.hazardRate();

		double dblLevelHazardIntegral = dblHazardRate * dblTimeIncrement;

		boolean bEventOccurred = java.lang.Math.exp (-1. * (jdv.cumulativeHazardIntegral() +
			dblLevelHazardIntegral)) <= jdu.jump();

		try {
			return new org.drip.measure.realization.JumpIndicationEdge (bEventOccurred, dblHazardRate,
				dblLevelHazardIntegral, bEventOccurred ? _heie.magnitudeEvaluator().value (jdv) : 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.measure.realization.JumpDiffusionEdge increment (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final org.drip.measure.realization.JumpDiffusionUnit jdu,
		final double dblTimeIncrement)
	{
		if (null == jdv || null == jdu || !org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement))
			return null;

		double dblPreviousValue = jdv.value();

		double dblHazardRate = _heie.hazardRate();

		double dblJumpRandomUnitRealization = jdu.jump();

		double dblLevelHazardIntegral = dblHazardRate * dblTimeIncrement;

		boolean bEventOccurred = java.lang.Math.exp (-1. * (jdv.cumulativeHazardIntegral() +
			dblLevelHazardIntegral)) <= dblJumpRandomUnitRealization;

		try {
			return bEventOccurred ? new org.drip.measure.realization.JumpDiffusionEdge (dblPreviousValue, 0.,
				0., new org.drip.measure.realization.JumpIndicationEdge (bEventOccurred, dblHazardRate,
					dblLevelHazardIntegral, _heie.magnitudeEvaluator().value (jdv) - dblPreviousValue), new
						org.drip.measure.realization.JumpDiffusionUnit (0., dblJumpRandomUnitRealization)) :
							super.increment (jdv, jdu, dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
