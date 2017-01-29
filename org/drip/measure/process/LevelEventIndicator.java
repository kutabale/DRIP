
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
 * LevelEventIndicator holds the Outcome of the Event Indicator Step Function Value.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LevelEventIndicator {
	private boolean _bOccurred = false;
	private double _dblDensity = java.lang.Double.NaN;
	private double _dblMagnitude = java.lang.Double.NaN;

	/**
	 * LevelEventIndicator Constructor
	 * 
	 * @param bOccurred TRUE - The Event Occurred
	 * @param dblDensity The Event Occurrence Probability Density
	 * @param dblMagnitude The Event Magnitude
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LevelEventIndicator (
		final boolean bOccurred,
		final double dblDensity,
		final double dblMagnitude)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDensity = dblDensity) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblMagnitude = dblMagnitude))
			throw new java.lang.Exception ("LevelEventIndicator Constructor => Invalid Inputs");

		_bOccurred = bOccurred;
	}

	/**
	 * Retrieve the "Event Occurred" Flag
	 * 
	 * @return The "Event Occurred" Flag
	 */

	public final boolean occurred()
	{
		return _bOccurred;
	}

	/**
	 * Retrieve the Event Occurrence Probability Density
	 * 
	 * @return The Event Occurrence Probability Density
	 */

	public final double density()
	{
		return _dblDensity;
	}

	/**
	 * Retrieve the Event Magnitude
	 * 
	 * @return The Event Magnitude
	 */

	public final double magnitude()
	{
		return _dblMagnitude;
	}
}
