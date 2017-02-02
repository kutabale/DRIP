
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
 * ContinuousEvolverLogarithmic guides the Continuous/Jump Random Variable Evolution according to R^1
 *  Logarithmic Process.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ContinuousEvolverLogarithmic extends org.drip.measure.marginal.ContinuousEvolver {
	private double _dblDrift = java.lang.Double.NaN;
	private double _dblVolatility = java.lang.Double.NaN;

	/**
	 * Generate a Standard Instance of ContinuousEvolverLogarithmic
	 * 
	 * @param dblDrift The Drift
	 * @param dblVolatility The Volatility
	 * 
	 * @return The Standard Instance of ContinuousEvolverLogarithmic
	 */

	public static final ContinuousEvolverLogarithmic Standard (
		final double dblDrift,
		final double dblVolatility)
	{
		try {
			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.realization.JumpDiffusionVertex r1s)
					throws java.lang.Exception
				{
					if (null == r1s)
						throw new java.lang.Exception
							("ContinuousEvolverLogarithmic::DriftLDEV::value => Invalid Inputs");

					return r1s.value() * dblDrift;
				}
			};

			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.realization.JumpDiffusionVertex r1s)
					throws java.lang.Exception
				{
					if (null == r1s)
						throw new java.lang.Exception
							("ContinuousEvolverLogarithmic::VolatilityLDEV::value => Invalid Inputs");

					return r1s.value() * dblVolatility;
				}
			};

			return new ContinuousEvolverLogarithmic (dblDrift, dblVolatility, ldevDrift, ldevVolatility);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ContinuousEvolverLogarithmic (
		final double dblDrift,
		final double dblVolatility,
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility)
		throws java.lang.Exception
	{
		super (ldevDrift, ldevVolatility);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDrift = dblDrift) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblVolatility = dblVolatility))
			throw new java.lang.Exception ("ContinuousEvolverLogarithmic Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Drift
	 * 
	 * @return The Drift
	 */

	public double drift()
	{
		return _dblDrift;
	}

	/**
	 * Retrieve the Volatility
	 * 
	 * @return The Volatility
	 */

	public double volatility()
	{
		return _dblVolatility;
	}
}
