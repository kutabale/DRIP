
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
 * R1Evolver implements the Functionality that guides the Single Factor R^1 Random Process Variable
 *  Evolution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1Evolver {
	private org.drip.measure.process.EventIndicator _ei = null;
	private org.drip.measure.process.LocalDeterministicEvolutionFunction _ldevDrift = null;
	private org.drip.measure.process.LocalDeterministicEvolutionFunction _ldevVolatility = null;

	/**
	 * R1Evolver Constructor
	 * 
	 * @param ldevDrift The LDEV Drift Function of the Marginal Process
	 * @param ldevVolatility The LDEV Volatility Function of the Marginal Process Continuous Component
	 * @param ei The Point Event Indicator Function Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public R1Evolver (
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility,
		final org.drip.measure.process.EventIndicator ei)
		throws java.lang.Exception
	{
		_ei = ei;
		_ldevVolatility = ldevVolatility;

		if (null == (_ldevDrift = ldevDrift) || (null == _ldevVolatility && null == _ei))
			throw new java.lang.Exception ("R1Evolver Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the LDEV Drift Function of the Marginal Process
	 * 
	 * @return The LDEV Drift Function of the Marginal Process
	 */

	public org.drip.measure.process.LocalDeterministicEvolutionFunction driftLDEV()
	{
		return _ldevDrift;
	}

	/**
	 * Retrieve the LDEV Volatility Function of the Marginal Process Continuous Component
	 * 
	 * @return The LDEV Volatility Function of the Marginal Process Continuous Component
	 */

	public org.drip.measure.process.LocalDeterministicEvolutionFunction volatilityLDEV()
	{
		return _ldevVolatility;
	}

	/**
	 * Retrieve the Point Event Indicator Instance
	 * 
	 * @return The Point Event Indicator Instance
	 */

	public org.drip.measure.process.EventIndicator eventIndicatorFunction()
	{
		return _ei;
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

	public org.drip.measure.marginal.R1LevelRealization increment (
		final org.drip.measure.marginal.R1Snap r1s,
		final org.drip.measure.marginal.R1UnitRealization r1ur,
		final double dblTimeIncrement)
	{
		if (null == r1s || null == r1ur || !org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement))
			return null;

		try {
			if (r1s.terminationReached())
				return new org.drip.measure.marginal.R1LevelRealization (r1s.value(), 0., 0., 0., null, 0.);

			double dblJumpRandomUnitRealization = r1ur.jump();

			org.drip.measure.process.LevelEventIndicator lei = null;

			double dblContinuousRandomUnitRealization = r1ur.continuous();

			if (null != _ei) {
				double dblPreviousValue = r1s.value();

				double dblEventDensity = _ei.densityLDEV().value (r1s);

				double dblEventMagnitude = _ei.magnitudeLDEV().value (r1s);

				boolean bEventOccurrence = (dblEventDensity * dblTimeIncrement) >=
					dblJumpRandomUnitRealization;

				if (bEventOccurrence && _ei.isJumpTerminal())
					return new org.drip.measure.marginal.R1LevelRealization (dblPreviousValue, 0., 0., 0.,
						new org.drip.measure.process.LevelEventIndicator (bEventOccurrence, dblEventDensity,
							dblEventMagnitude - dblPreviousValue), dblJumpRandomUnitRealization);

				lei = new org.drip.measure.process.LevelEventIndicator (bEventOccurrence, dblEventDensity,
					bEventOccurrence ? dblEventMagnitude : 0.);
			}

			return new org.drip.measure.marginal.R1LevelRealization (r1s.value(), _ldevDrift.value (r1s) *
				dblTimeIncrement, null == _ldevVolatility ? 0. : _ldevVolatility.value (r1s) *
					dblContinuousRandomUnitRealization * java.lang.Math.sqrt (java.lang.Math.abs
						(dblTimeIncrement)), dblContinuousRandomUnitRealization, lei,
							dblJumpRandomUnitRealization);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Array of Adjacent Increments from the specified Random Variate Array
	 * 
	 * @param r1s The Random Variate Marginal Snap
	 * @param r1ur The R^1 Unit Realization
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Array of Adjacent Increment
	 */

	public org.drip.measure.marginal.R1LevelRealization[] incrementSequence (
		final org.drip.measure.marginal.R1Snap r1s,
		final org.drip.measure.marginal.R1UnitRealization[] aR1UR,
		final double dblTimeIncrement)
	{
		if (null == aR1UR) return null;

		int iNumTimeStep = aR1UR.length;
		org.drip.measure.marginal.R1Snap r1sLoop = r1s;
		org.drip.measure.marginal.R1LevelRealization[] aLR = 0 == iNumTimeStep ? null : new
			org.drip.measure.marginal.R1LevelRealization[iNumTimeStep];

		if (0 == iNumTimeStep) return null;

		for (int i = 0; i < iNumTimeStep; ++i) {
			if (null == (aLR[i] = increment (r1sLoop, aR1UR[i], dblTimeIncrement))) return null;

			try {
				r1sLoop = new org.drip.measure.marginal.R1Snap (r1sLoop.time() + dblTimeIncrement,
					aLR[i].finish(), false);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aLR;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and a Weiner Driver
	 * 
	 * @param r1s The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.marginal.R1LevelRealization weinerIncrement (
		final org.drip.measure.marginal.R1Snap r1s,
		final double dblTimeIncrement)
	{
		try {
			return increment (r1s, org.drip.measure.marginal.R1UnitRealization.ContinuousGaussian(),
				dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and a Jump Driver
	 * 
	 * @param r1s The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.marginal.R1LevelRealization jumpIncrement (
		final org.drip.measure.marginal.R1Snap r1s,
		final double dblTimeIncrement)
	{
		return increment (r1s, org.drip.measure.marginal.R1UnitRealization.JumpUniform(), dblTimeIncrement);
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and Jump/Weiner Drivers
	 * 
	 * @param r1s The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.marginal.R1LevelRealization jumpWeinerIncrement (
		final org.drip.measure.marginal.R1Snap r1s,
		final double dblTimeIncrement)
	{
		try {
			return increment (r1s, new org.drip.measure.marginal.R1UnitRealization
				(org.drip.measure.gaussian.NormalQuadrature.Random(), java.lang.Math.random()),
					dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and Weiner/Jump Drivers
	 * 
	 * @param r1s The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.marginal.R1LevelRealization weinerJumpIncrement (
		final org.drip.measure.marginal.R1Snap r1s,
		final double dblTimeIncrement)
	{
		try {
			return increment (r1s, new org.drip.measure.marginal.R1UnitRealization
				(org.drip.measure.gaussian.NormalQuadrature.Random(), java.lang.Math.random()),
					dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
