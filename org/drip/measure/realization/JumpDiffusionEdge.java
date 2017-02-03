
package org.drip.measure.realization;

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
 * JumpDiffusionEdge implements the Deterministic and the Stochastic Components of a R^1 Marginal Random
 *	Increment Edge as well the Original Marginal Random Variate. The References are:
 * 
 * 	- Almgren, R. F., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3
 * 		(2) 5-39.
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
 * @author Lakshmi Krishnamurthy
 */

public class JumpDiffusionEdge {
	private double _dblStart = java.lang.Double.NaN;
	private double _dblDeterministic = java.lang.Double.NaN;
	private double _dblDiffusionStochastic = java.lang.Double.NaN;
	private org.drip.measure.realization.JumpDiffusionUnit _jdu = null;
	private org.drip.measure.realization.JumpIndicationEdge _jie = null;

	/**
	 * JumpDiffusionEdge Constructor
	 * 
	 * @param dblStart The Starting Random Variable Realization
	 * @param dblDeterministic The Deterministic Increment Component
	 * @param dblDiffusionStochastic The Diffusion Stochastic Increment Component
	 * @param jie The Jump Stochastic Event Indication Edge
	 * @param jdu The Jump Diffusion Unit Realization
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public JumpDiffusionEdge (
		final double dblStart,
		final double dblDeterministic,
		final double dblDiffusionStochastic,
		final org.drip.measure.realization.JumpIndicationEdge jie,
		final org.drip.measure.realization.JumpDiffusionUnit jdu)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblStart = dblStart) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblDeterministic = dblDeterministic) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblDiffusionStochastic = dblDiffusionStochastic)
					|| null == (_jdu = jdu))
			throw new java.lang.Exception ("JumpDiffusionEdge Constructor => Invalid Inputs");

		_jie = jie;
	}

	/**
	 * Retrieve the Starting Random Realization
	 * 
	 * @return The Starting Random Realization
	 */

	public double start()
	{
		return _dblStart;
	}

	/**
	 * Retrieve the Deterministic Increment Component
	 * 
	 * @return The Deterministic Increment Component
	 */

	public double deterministic()
	{
		return _dblDeterministic;
	}

	/**
	 * Retrieve the Diffusion Stochastic Increment Component
	 * 
	 * @return The Diffusion Stochastic Increment Component
	 */

	public double diffusionStochastic()
	{
		return _dblDiffusionStochastic;
	}

	/**
	 * Retrieve the Diffusion Random Wander Realization
	 * 
	 * @return The Diffusion Random Wander Realization
	 */

	public double diffusionWander()
	{
		return _jdu.diffusion();
	}

	/**
	 * Retrieve the Jump Stochastic Increment Component
	 * 
	 * @return The Jump Stochastic Increment Component
	 */

	public double jumpStochastic()
	{
		return null == _jie ? 0. : _jie.terminalValue();
	}

	/**
	 * Retrieve the Jump Random Wander Realization
	 * 
	 * @return The Jump Random Wander Realization
	 */

	public double jumpWander()
	{
		return _jdu.jump();
	}

	/**
	 * Retrieve the Gross Increment
	 * 
	 * @return The Gross Increment
	 */

	public double grossChange()
	{
		return _dblDeterministic + _dblDiffusionStochastic + jumpStochastic();
	}

	/**
	 * Retrieve the Next Random Realization
	 * 
	 * @return The Next Random Realization
	 */

	public double finish()
	{
		return _dblStart + _dblDeterministic + _dblDiffusionStochastic + jumpStochastic();
	}

	/**
	 * Retrieve the Jump Event Indication Edge Instance
	 * 
	 * @return The Jump Event Indication Edge Instance
	 */

	public org.drip.measure.realization.JumpIndicationEdge jumpIndicationEdge()
	{
		return _jie;
	}
}
