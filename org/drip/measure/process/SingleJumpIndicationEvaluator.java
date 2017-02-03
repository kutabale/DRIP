
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
 * SingleJumpIndicationEvaluator implements the Single Point Jump Event Indication Evaluator that guides the
 *  One Factor Jump Random Process Variable Evolution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class SingleJumpIndicationEvaluator {
	private org.drip.measure.process.LocalDeterministicEvaluator _ldeDensity = null;
	private org.drip.measure.process.LocalDeterministicEvaluator _ldeMagnitude = null;

	/**
	 * SingleJumpIndicationEvaluator Constructor
	 * 
	 * @param ldeDensity The LDE Event Density Evaluator of the Marginal Process Jump Component
	 * @param ldeMagnitude The LDE Event Magnitude Evaluator of the Marginal Process Jump Component
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public SingleJumpIndicationEvaluator (
		final org.drip.measure.process.LocalDeterministicEvaluator ldeDensity,
		final org.drip.measure.process.LocalDeterministicEvaluator ldeMagnitude)
		throws java.lang.Exception
	{
		if (null == (_ldeDensity = ldeDensity) || null == (_ldeMagnitude = ldeMagnitude))
			throw new java.lang.Exception ("SingleJumpIndicationEvaluator Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the LDEV Event Density Evaluator of the Marginal Process Jump Component
	 * 
	 * @return The LDEV Event Density Evaluator of the Marginal Process Jump Component
	 */

	public org.drip.measure.process.LocalDeterministicEvaluator densityEvaluator()
	{
		return _ldeDensity;
	}

	/**
	 * Retrieve the LDE Event Magnitude Function of the Marginal Process Jump Component
	 * 
	 * @return The LDE Event Magnitude Function of the Marginal Process Jump Component
	 */

	public org.drip.measure.process.LocalDeterministicEvaluator magnitudeEvaluator()
	{
		return _ldeMagnitude;
	}
}
