
package org.drip.quant.random;

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
 * ProcessMarginalLogarithmic guides the Random Variable Evolution according to 1D Logarithmic Process.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ProcessMarginalLogarithmic extends org.drip.quant.random.ProcessMarginal {
	private double _dblDrift = java.lang.Double.NaN;
	private double _dblVolatility = java.lang.Double.NaN;
	private org.drip.quant.random.LocalDeterministicEvolutionFunction _ldevDrift = null;
	private org.drip.quant.random.LocalDeterministicEvolutionFunction _ldevVolatility = null;

	/**
	 * Generate a Standard Instance of ProcessMarginalLogarithmic
	 * 
	 * @param dblDrift The Drift
	 * @param dblVolatility The Volatility
	 * 
	 * @return The Standard Instance of ProcessMarginalLogarithmic
	 */

	public static final ProcessMarginalLogarithmic Standard (
		final double dblDrift,
		final double dblVolatility)
	{
		try {
			org.drip.quant.random.LocalDeterministicEvolutionFunction ldevDrift = new
				org.drip.quant.random.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.quant.random.MarginalSnap ms)
					throws java.lang.Exception
				{
					if (null == ms)
						throw new java.lang.Exception
							("ProcessMarginalLogarithmic::DriftLDEV::value => Invalid Inputs");

					return ms.value() * dblDrift;
				}
			};

			org.drip.quant.random.LocalDeterministicEvolutionFunction ldevVolatility = new
				org.drip.quant.random.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.quant.random.MarginalSnap ms)
					throws java.lang.Exception
				{
					if (null == ms)
						throw new java.lang.Exception
							("ProcessMarginalLogarithmic::VolatilityLDEV::value => Invalis Inputs");

					return ms.value() * dblVolatility;
				}
			};

			return new ProcessMarginalLogarithmic (dblDrift, dblVolatility, ldevDrift, ldevVolatility);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ProcessMarginalLogarithmic (
		final double dblDrift,
		final double dblVolatility,
		final org.drip.quant.random.LocalDeterministicEvolutionFunction ldevDrift,
		final org.drip.quant.random.LocalDeterministicEvolutionFunction ldevVolatility)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDrift = dblDrift) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblVolatility = dblVolatility) || null == (_ldevDrift
				= ldevDrift) || null == (_ldevVolatility = ldevVolatility))
			throw new java.lang.Exception ("ProcessMarginalLogarithmic Constructor => Invalid Inputs");
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

	/**
	 * Retrieve the LDEV Drift Function of the Log Process
	 * 
	 * @return The LDEV Drift Function of the Log Process
	 */

	public org.drip.quant.random.LocalDeterministicEvolutionFunction driftLDEV()
	{
		return _ldevDrift;
	}

	/**
	 * Retrieve the LDEV Volatility Function of the Log Process
	 * 
	 * @return The LDEV Volatility Function of the Log Process
	 */

	public org.drip.quant.random.LocalDeterministicEvolutionFunction volatilityLDEV()
	{
		return _ldevVolatility;
	}

	@Override public org.drip.quant.random.GenericIncrement increment (
		final org.drip.quant.random.MarginalSnap ms,
		final double dblRandomUnitRealization,
		final double dblTimeIncrement)
	{
		if (null == ms || !org.drip.quant.common.NumberUtil.IsValid (dblRandomUnitRealization) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement) || 0. >= dblTimeIncrement)
			return null;

		try {
			return new org.drip.quant.random.GenericIncrement (_ldevDrift.value (ms) * dblTimeIncrement,
				_ldevVolatility.value (ms) * dblRandomUnitRealization * java.lang.Math.sqrt
					(dblTimeIncrement), dblRandomUnitRealization);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
