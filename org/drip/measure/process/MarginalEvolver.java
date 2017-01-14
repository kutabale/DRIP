
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
	private org.drip.measure.process.LocalDeterministicEvolutionFunction _ldevDrift = null;
	private org.drip.measure.process.LocalDeterministicEvolutionFunction _ldevVolatility = null;

	/**
	 * MarginalEvolver Constructor
	 * 
	 * @param ldevDrift The LDEV Drift Function of the Marginal Process
	 * @param ldevVolatility The LDEV Volatility Function of the Marginal Process
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MarginalEvolver (
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility)
		throws java.lang.Exception
	{
		if (null == (_ldevDrift = ldevDrift) || null == (_ldevVolatility = ldevVolatility))
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
	 * Retrieve the LDEV Volatility Function of the Marginal Process
	 * 
	 * @return The LDEV Volatility Function of the Marginal Process
	 */

	public org.drip.measure.process.LocalDeterministicEvolutionFunction volatilityLDEV()
	{
		return _ldevVolatility;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate
	 * 
	 * @param ms The Random Variate Marginal Snap
	 * @param dblRandomUnitRealization The Random Stochastic Realization Variate Unit
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.process.RealizedIncrement increment (
		final org.drip.measure.process.MarginalSnap ms,
		final double dblRandomUnitRealization,
		final double dblTimeIncrement)
	{
		if (null == ms || !org.drip.quant.common.NumberUtil.IsValid (dblRandomUnitRealization) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement) || 0. >= dblTimeIncrement)
			return null;

		try {
			return new org.drip.measure.process.RealizedIncrement (ms.value(), _ldevDrift.value (ms) *
				dblTimeIncrement, _ldevVolatility.value (ms) * dblRandomUnitRealization * java.lang.Math.sqrt
					(dblTimeIncrement), dblRandomUnitRealization);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent Increment from the specified Random Variate and a Weiner Driver
	 * 
	 * @param ms The Random Variate Marginal Snap
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Increment
	 */

	public org.drip.measure.process.RealizedIncrement weinerIncrement (
		final org.drip.measure.process.MarginalSnap ms,
		final double dblTimeIncrement)
	{
		try {
			return increment (ms, org.drip.measure.gaussian.NormalQuadrature.Random(), dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
