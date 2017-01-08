
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
 * OrnsteinUhlenbeckProcess2D guides the Random Variable Evolution according to 2D Ornstein-Uhlenbeck Mean
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

public class OrnsteinUhlenbeckProcess2D implements org.drip.quant.stochastic.OrnsteinUhlenbeck {
	private double _dblCorrelation = java.lang.Double.NaN;
	private org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D _oupDerived = null;
	private org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D _oupReference = null;

	/**
	 * OrnsteinUhlenbeckProcess2D Constructor
	 * 
	 * @param oupReference The Reference 1D Ornstein-Uhlenbeck Process
	 * @param oupDerived The Derived 1D Ornstein-Uhlenbeck Process
	 * @param dblCorrelation The Correlation between the Two Ornstein-Uhlenbeck Processes
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public OrnsteinUhlenbeckProcess2D (
		final org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D oupReference,
		final org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D oupDerived,
		final double dblCorrelation)
		throws java.lang.Exception
	{
		if (null == (_oupReference = oupReference) || null == (_oupDerived = oupDerived) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCorrelation = dblCorrelation) || _dblCorrelation <
				-1. || _dblCorrelation > 1.)
			throw new java.lang.Exception ("OrnsteinUhlenbeckProcess2D Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Reference 1D Ornstein-Uhlenbeck Process
	 * 
	 * @return The Reference 1D Ornstein-Uhlenbeck Process
	 */

	public org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D reference()
	{
		return _oupReference;
	}

	/**
	 * Retrieve the Derived 1D Ornstein-Uhlenbeck Process
	 * 
	 * @return The Derived 1D Ornstein-Uhlenbeck Process
	 */

	public org.drip.quant.stochastic.OrnsteinUhlenbeckProcess1D derived()
	{
		return _oupDerived;
	}

	/**
	 * Retrieve the Correlation between the Ornstein-Uhlenbeck Processes
	 * 
	 * @return The Correlation between the Ornstein-Uhlenbeck Processes
	 */

	public double correlation()
	{
		return _dblCorrelation;
	}

	/**
	 * Generate the Adjacent Increment Set from the specified Ornstein Uhlenbeck Random Variate Pair
	 * 
	 * @param adblOrnsteinUhlenbeckVariate The Array of the Ornstein Uhlenbeck Random Variates
	 * @param adblRandomRealization The Array of the Random Stochastic Realization Variates
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent Ornstein Uhlenbeck Increment Set
	 */

	public org.drip.quant.stochastic.GenericIncrement[] increment (
		final double[] adblOrnsteinUhlenbeckVariate,
		final double[] adblRandomRealization,
		final double dblTimeIncrement)
	{
		if (null == adblOrnsteinUhlenbeckVariate || 2 != adblOrnsteinUhlenbeckVariate.length ||
			!org.drip.quant.common.NumberUtil.IsValid (adblOrnsteinUhlenbeckVariate) || null ==
				adblRandomRealization || 2 != adblRandomRealization.length ||
					!org.drip.quant.common.NumberUtil.IsValid (adblRandomRealization) ||
						!org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement) || 0. >= dblTimeIncrement)
			return null;

		double dblRelaxationTime0 = _oupReference.relaxationTime();

		double dblRelaxationTime1 = _oupDerived.relaxationTime();

		try {
			return new org.drip.quant.stochastic.GenericIncrement[] {
				new org.drip.quant.stochastic.GenericIncrement (
					-1. * adblOrnsteinUhlenbeckVariate[0] / dblRelaxationTime0 * dblTimeIncrement,
					_oupReference.burstiness() * adblRandomRealization[0] * java.lang.Math.sqrt (dblTimeIncrement / dblRelaxationTime0),
					adblRandomRealization[0]
				),
				new org.drip.quant.stochastic.GenericIncrement (
					-1. * adblOrnsteinUhlenbeckVariate[1] / dblRelaxationTime1 * dblTimeIncrement,
					_oupDerived.burstiness() * adblRandomRealization[1] * java.lang.Math.sqrt (dblTimeIncrement / dblRelaxationTime1),
					adblRandomRealization[1]
				)
			};
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Weiner Based Increment Sequence from the Current Ornstein Uhlenbeck Random Variate
	 * 
	 * @param adblOrnsteinUhlenbeckVariate The Current Ornstein Uhlenbeck Random Variate
	 * @param dblTimeIncrement The Time Increment
	 * 
	 * @return The Weiner Based Increment Sequence from the Current Ornstein Uhlenbeck Random Variate
	 */

	public org.drip.quant.stochastic.GenericIncrement[] weinerIncrement (
		final double[] adblOrnsteinUhlenbeckVariate,
		final double dblTimeIncrement)
	{
		try {
			double dblFirstWeiner = org.drip.measure.gaussian.NormalQuadrature.Random();

			return increment (adblOrnsteinUhlenbeckVariate, new double[] {dblFirstWeiner, dblFirstWeiner *
				_dblCorrelation + org.drip.measure.gaussian.NormalQuadrature.Random() * java.lang.Math.sqrt
					(1. - _dblCorrelation * _dblCorrelation)}, dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public double referenceRelaxationTime()
	{
		return _oupReference.relaxationTime();
	}

	@Override public double referenceBurstiness()
	{
		return _oupReference.burstiness();
	}

	@Override public double referenceMeanReversionLevel()
	{
		return _oupReference.meanReversionLevel();
	}
}
