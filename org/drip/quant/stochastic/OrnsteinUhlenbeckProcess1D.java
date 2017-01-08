
package org.drip.quant.stochastic;

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
 * OrnsteinUhlenbeckProcess1D guides the Random Variable Evolution according to 1D Ornstein-Uhlenbeck Mean
 *  Reverting Process. The References are:
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
 * 		Financial Studies 7 (4) 631-651.
 * 
 * 	- Walia, N. (2006): Optimal Trading - Dynamic Stock Liquidation Strategies, Senior Thesis, Princeton
 * 		University.
 *
 * @author Lakshmi Krishnamurthy
 */

public class OrnsteinUhlenbeckProcess1D implements org.drip.quant.stochastic.OrnsteinUhlenbeck {
	private double _dblBurstiness = java.lang.Double.NaN;
	private double _dblRelaxationTime = java.lang.Double.NaN;
	private double _dblMeanReversionLevel = java.lang.Double.NaN;

	/**
	 * Construct a Zero-Mean Instance of OrnsteinUhlenbeckProcess1D
	 * 
	 * @param dblBurstiness The Burstiness Parameter
	 * @param dblRelaxationTime The Relaxation Time
	 * 
	 * @return The Zero-Mean Instance of OrnsteinUhlenbeckProcess1D
	 */

	public static final OrnsteinUhlenbeckProcess1D ZeroMean (
		final double dblBurstiness,
		final double dblRelaxationTime)
	{
		try {
			return new OrnsteinUhlenbeckProcess1D (0., dblBurstiness, dblRelaxationTime);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * OrnsteinUhlenbeckProcess1D Constructor
	 * 
	 * @param dblMeanReversionLevel The Mean Reversion Level
	 * @param dblBurstiness The Burstiness Parameter
	 * @param dblRelaxationTime The Relaxation Time
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public OrnsteinUhlenbeckProcess1D (
		final double dblMeanReversionLevel,
		final double dblBurstiness,
		final double dblRelaxationTime)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblMeanReversionLevel = dblMeanReversionLevel) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblBurstiness = dblBurstiness) || 0. >=
				_dblBurstiness || !org.drip.quant.common.NumberUtil.IsValid (_dblRelaxationTime =
					dblRelaxationTime) || 0. >= _dblRelaxationTime)
			throw new java.lang.Exception ("OrnsteinUhlenbeckProcess1D Constructor - Invalid Inputs");
	}

	/**
	 * Retrieve the Mean Reversion Level
	 * 
	 * @return The Mean Reversion Level
	 */

	public double meanReversionLevel()
	{
		return _dblMeanReversionLevel;
	}

	/**
	 * Retrieve the Burstiness Parameter
	 * 
	 * @return The Burstiness Parameter
	 */

	public double burstiness()
	{
		return _dblBurstiness;
	}

	/**
	 * Retrieve the Relaxation Time
	 * 
	 * @return The Relaxation Time
	 */

	public double relaxationTime()
	{
		return _dblRelaxationTime;
	}

	/**
	 * Generate the Adjacent Increment from the specified Ornstein Uhlenbeck Random Variate
	 * 
	 * @param dblOrnsteinUhlenbeckVariate The Ornstein Uhlenbeck Random Variate
	 * @param dblRandomRealization The Random Stochastic Realization Variate
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Ornstein Uhlenbeck Increment
	 */

	public org.drip.quant.stochastic.GenericIncrement increment (
		final double dblOrnsteinUhlenbeckVariate,
		final double dblRandomRealization,
		final double dblTimeIncrement)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblOrnsteinUhlenbeckVariate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblRandomRealization) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement) || 0. >= dblTimeIncrement)
			return null;

		try {
			return new org.drip.quant.stochastic.GenericIncrement (-1. * dblOrnsteinUhlenbeckVariate /
				_dblRelaxationTime * dblTimeIncrement, _dblBurstiness * dblRandomRealization *
					java.lang.Math.sqrt (dblTimeIncrement / _dblRelaxationTime), dblRandomRealization);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent Increment from the specified Ornstein Uhlenbeck Variate and a Weiner Driver
	 * 
	 * @param dblOrnsteinUhlenbeckVariate The Ornstein Uhlenbeck Random Variate
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Ornstein Uhlenbeck Increment
	 */

	public org.drip.quant.stochastic.GenericIncrement weinerIncrement (
		final double dblOrnsteinUhlenbeckVariate,
		final double dblTimeIncrement)
	{
		try {
			return increment (dblOrnsteinUhlenbeckVariate,
				org.drip.measure.gaussian.NormalQuadrature.Random(), dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public double referenceRelaxationTime()
	{
		return relaxationTime();
	}

	@Override public double referenceBurstiness()
	{
		return burstiness();
	}

	@Override public double referenceMeanReversionLevel()
	{
		return meanReversionLevel();
	}
}
