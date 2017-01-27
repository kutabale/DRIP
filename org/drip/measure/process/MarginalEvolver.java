
package org.drip.measure.process;

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
 * MarginalEvolver implements the Functionality that guides the Single Factor Random Process Variable
 *  Evolution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class MarginalEvolver {
	private org.drip.measure.process.PointEvent _pe = null;
	private org.drip.measure.process.LocalDeterministicEvolutionFunction _ldevDrift = null;
	private org.drip.measure.process.LocalDeterministicEvolutionFunction _ldevVolatility = null;

	/**
	 * MarginalEvolver Constructor
	 * 
	 * @param ldevDrift The LDEV Drift Function of the Marginal Process
	 * @param ldevVolatility The LDEV Volatility Function of the Marginal Process Continuous Component
	 * @param pe The Point Event Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MarginalEvolver (
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility,
		final org.drip.measure.process.PointEvent pe)
		throws java.lang.Exception
	{
		_pe = pe;
		_ldevVolatility = ldevVolatility;

		if (null == (_ldevDrift = ldevDrift) || (null == _ldevVolatility && null == _pe))
			throw new java.lang.Exception ("MarginalEvolver Constructor => Invalid Inputs");
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
	 * Retrieve the Point Event Instance
	 * 
	 * @return The Point Event Instance
	 */

	public org.drip.measure.process.PointEvent pointEvent()
	{
		return _pe;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate
	 * 
	 * @param ms The Random Variate Marginal Snap
	 * @param dblContinuousRandomUnitRealization The Continuous Random Stochastic Realization Variate Unit
	 * @param dblJumpRandomUnitRealization The Jump Random Stochastic Realization Variate Unit
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.process.MarginalLevelRealization increment (
		final org.drip.measure.process.MarginalSnap ms,
		final double dblContinuousRandomUnitRealization,
		final double dblJumpRandomUnitRealization,
		final double dblTimeIncrement)
	{
		if (null == ms || !org.drip.quant.common.NumberUtil.IsValid (dblContinuousRandomUnitRealization) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblJumpRandomUnitRealization) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement) || 0. >= dblTimeIncrement)
			return null;

		try {
			double dblJumpIntensityMagnitude = 0.;

			if (null != _pe)
				dblJumpIntensityMagnitude = _pe.densityLDEV().value (ms) >= dblJumpRandomUnitRealization ?
					_pe.magnitudeLDEV().value (ms) : 0.;

			return new org.drip.measure.process.MarginalLevelRealization (ms.value(), _ldevDrift.value (ms) *
				dblTimeIncrement, null == _ldevVolatility ? 0. : _ldevVolatility.value (ms) *
					dblContinuousRandomUnitRealization * java.lang.Math.sqrt (dblTimeIncrement),
						dblContinuousRandomUnitRealization, dblJumpIntensityMagnitude,
							dblJumpRandomUnitRealization);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Array of Adjacent Increments from the specified Random Variate Array
	 * 
	 * @param ms The Random Variate Marginal Snap
	 * @param adblContinuousRandomUnitRealization Array of Continuous Random Stochastic Realization Variate
	 * 		Units
	 * @param adblJumpRandomUnitRealization Array of Jump Random Stochastic Realization Variate Units
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Array of Adjacent Increment
	 */

	public org.drip.measure.process.MarginalLevelRealization[] incrementSequence (
		final org.drip.measure.process.MarginalSnap ms,
		final double[] adblContinuousRandomUnitRealization,
		final double[] adblJumpRandomUnitRealization,
		final double dblTimeIncrement)
	{
		if (null == adblContinuousRandomUnitRealization || null == adblJumpRandomUnitRealization)
			return null;

		org.drip.measure.process.MarginalSnap msLoop = ms;
		int iNumTimeStep = adblJumpRandomUnitRealization.length;
		org.drip.measure.process.MarginalLevelRealization[] aLR = 0 == iNumTimeStep ? null : new
			org.drip.measure.process.MarginalLevelRealization[iNumTimeStep];

		if (0 == iNumTimeStep || adblContinuousRandomUnitRealization.length != iNumTimeStep) return null;

		for (int i = 0; i < iNumTimeStep; ++i) {
			if (null == (aLR[i] = increment (msLoop, adblContinuousRandomUnitRealization[i],
				adblJumpRandomUnitRealization[i], dblTimeIncrement)))
				return null;

			try {
				msLoop = new org.drip.measure.process.MarginalSnap (msLoop.time() + dblTimeIncrement,
					aLR[i].finish());
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
	 * @param ms The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.process.MarginalLevelRealization weinerIncrement (
		final org.drip.measure.process.MarginalSnap ms,
		final double dblTimeIncrement)
	{
		try {
			return increment (ms, org.drip.measure.gaussian.NormalQuadrature.Random(), 0., dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and a Jump Driver
	 * 
	 * @param ms The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.process.MarginalLevelRealization jumpIncrement (
		final org.drip.measure.process.MarginalSnap ms,
		final double dblTimeIncrement)
	{
		return increment (ms, 0., java.lang.Math.random(), dblTimeIncrement);
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and Jump/Weiner Drivers
	 * 
	 * @param ms The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.process.MarginalLevelRealization jumpWeinerIncrement (
		final org.drip.measure.process.MarginalSnap ms,
		final double dblTimeIncrement)
	{
		try {
			return increment (ms, org.drip.measure.gaussian.NormalQuadrature.Random(),
				java.lang.Math.random(), dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
