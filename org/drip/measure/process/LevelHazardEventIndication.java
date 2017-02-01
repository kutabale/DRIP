
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
 * LevelHazardEventIndication holds the Outcome of the Hazard Event Indication Evaluator.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LevelHazardEventIndication {
	private boolean _bOccurred = false;
	private double _dblHazardRate = java.lang.Double.NaN;
	private double _dblTerminalValue = java.lang.Double.NaN;
	private double _dblHazardIntegral = java.lang.Double.NaN;

	/**
	 * LevelHazardEventIndication Constructor
	 * 
	 * @param bOccurred TRUE - The Event Occurred
	 * @param dblHazardRate The Hazard Rate
	 * @param dblHazardIntegral The Level Hazard Integral
	 * @param dblTerminalValue The Event Terminal Value
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LevelHazardEventIndication (
		final boolean bOccurred,
		final double dblHazardRate,
		final double dblHazardIntegral,
		final double dblTerminalValue)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblHazardRate = dblHazardRate) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblHazardIntegral = dblHazardIntegral) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblTerminalValue = dblTerminalValue))
			throw new java.lang.Exception ("LevelHazardEventIndication Constructor => Invalid Inputs");

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

	public final double hazardRate()
	{
		return _dblHazardRate;
	}

	/**
	 * Retrieve the Event Occurrence Hazard Integral
	 * 
	 * @return The Event Occurrence Hazard Integral
	 */

	public final double hazardIntegral()
	{
		return _dblHazardIntegral;
	}

	/**
	 * Retrieve the Terminal Event Value
	 * 
	 * @return The Terminal Event Value
	 */

	public final double terminalValue()
	{
		return _dblTerminalValue;
	}
}
