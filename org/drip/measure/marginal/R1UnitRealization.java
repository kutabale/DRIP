
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
 * R1UnitRealization holds the Continuous and the Jump R^1 Unit Edge Realizations.
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1UnitRealization {
	private double _dblJump = java.lang.Double.NaN;
	private double _dblContinuous = java.lang.Double.NaN;

	/**
	 * Generate a R^1 Continuous Uniform Realization
	 * 
	 * @return The R^1 Continuous Uniform Realization
	 */

	public static final R1UnitRealization ContinuousUniform()
	{
		try {
			return new R1UnitRealization (java.lang.Math.random(), 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a R^1 Continuous Gaussian Realization
	 * 
	 * @return The R^1 Continuous Gaussian Realization
	 */

	public static final R1UnitRealization ContinuousGaussian()
	{
		try {
			return new R1UnitRealization (org.drip.measure.gaussian.NormalQuadrature.Random(), 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a R^1 Jump Uniform Realization
	 * 
	 * @return The R^1 Jump Uniform Realization
	 */

	public static final R1UnitRealization JumpUniform()
	{
		try {
			return new R1UnitRealization (0., java.lang.Math.random());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a R^1 Jump Gaussian Realization
	 * 
	 * @return The R^1 Jump Gaussian Realization
	 */

	public static final R1UnitRealization JumpGaussian()
	{
		try {
			return new R1UnitRealization (0., org.drip.measure.gaussian.NormalQuadrature.Random());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate an Array of R^1 Continuous Realizations
	 * 
	 * @param adblContinuousRealization The Array of Continuous Realizations
	 * 
	 * @return Array of R^1 Continuous Realizations
	 */

	public static final R1UnitRealization[] Continuous (
		final double[] adblContinuousRealization)
	{
		if (null == adblContinuousRealization) return null;

		int iSize = adblContinuousRealization.length;
		R1UnitRealization[] aR1UR = 0 == iSize ? null : new R1UnitRealization[iSize];

		for (int i = 0; i < iSize; ++i) {
			try {
				aR1UR[i] = new R1UnitRealization (adblContinuousRealization[i], 0.);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aR1UR;
	}

	/**
	 * Generate an Array of R^1 Jump Realizations
	 * 
	 * @param adblJumpRealization The Array of Jump Realizations
	 * 
	 * @return Array of R^1 Jump Realizations
	 */

	public static final R1UnitRealization[] Jump (
		final double[] adblJumpRealization)
	{
		if (null == adblJumpRealization) return null;

		int iSize = adblJumpRealization.length;
		R1UnitRealization[] aR1UR = 0 == iSize ? null : new R1UnitRealization[iSize];

		for (int i = 0; i < iSize; ++i) {
			try {
				aR1UR[i] = new R1UnitRealization (0., adblJumpRealization[i]);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aR1UR;
	}

	/**
	 * Generate an Array of R^1 Continuous/Jump Realizations
	 * 
	 * @param adblContinuousRealization The Array of Continuous Realizations
	 * @param adblJumpRealization The Array of Jump Realizations
	 * 
	 * @return Array of R^1 Continuous/Jump Realizations
	 */

	public static final R1UnitRealization[] ContinuousJump (
		final double[] adblContinuousRealization,
		final double[] adblJumpRealization)
	{
		if (null == adblJumpRealization || null == adblJumpRealization) return null;

		int iSize = adblContinuousRealization.length;
		R1UnitRealization[] aR1UR = 0 == iSize ? null : new R1UnitRealization[iSize];

		if (0 == iSize || iSize != adblJumpRealization.length) return null;

		for (int i = 0; i < iSize; ++i) {
			try {
				aR1UR[i] = new R1UnitRealization (adblContinuousRealization[i], adblJumpRealization[i]);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aR1UR;
	}

	/**
	 * R1UnitRealization Constructor
	 * 
	 * @param dblContinuous The Continuous Random Variable
	 * @param dblJump The Jump Random Variable
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public R1UnitRealization (
		final double dblContinuous,
		final double dblJump)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblContinuous = dblContinuous) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblJump = dblJump))
			throw new java.lang.Exception ("R1UnitRealization Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Continuous Random Variable
	 * 
	 * @return The Continuous Random Variable
	 */

	public double continuous()
	{
		return _dblContinuous;
	}

	/**
	 * Retrieve the Jump Random Variable
	 * 
	 * @return The Jump Random Variable
	 */

	public double jump()
	{
		return _dblJump;
	}
}
