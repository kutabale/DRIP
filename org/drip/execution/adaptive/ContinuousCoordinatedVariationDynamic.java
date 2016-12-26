
package org.drip.execution.adaptive;

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
 * ContinuousCoordinatedVariationDynamic implements the Continuous HJB-based Single Step Optimal Cost Dynamic
 *  Trajectory using the Coordinated Variation Version of the Stochastic Volatility and the Transaction
 *  Function arising from the Realization of the Market State Variable as described in the "Trading Time"
 *  Model. The References are:
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
 * 		Financial Studies & (4) 631-651.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ContinuousCoordinatedVariationDynamic {
	private double _dblOrderSize = java.lang.Double.NaN;
	private double[] _adblNonDimensionalHoldings = null;
	private double[] _adblNonDimensionalAdjustedTradeRate = null;
	private org.drip.execution.adaptive.NonDimensionalCost[] _aNDC = null;

	/**
	 * ContinuousCoordinatedVariationDynamic Constructor
	 * 
	 * @param dblOrderSize The Order Size
	 * @param aNDC The Array of the Non Dimensional Costs
	 * @param adblNonDimensionalHoldings The Array of the Non Dimensional Holdings
	 * @param adblNonDimensionalAdjustedTradeRate The Array of the Non Dimensional Adjusted Trade Rate
	 * 
	 * @throws java.lang.Exception Thrown if the the Inputs are Invalid
	 */

	public ContinuousCoordinatedVariationDynamic (
		final double dblOrderSize,
		final org.drip.execution.adaptive.NonDimensionalCost[] aNDC,
		final double[] adblNonDimensionalHoldings,
		final double[] adblNonDimensionalAdjustedTradeRate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblOrderSize = dblOrderSize) || null == (_aNDC =
			aNDC) || null == (_adblNonDimensionalHoldings = adblNonDimensionalHoldings) || null ==
				(_adblNonDimensionalAdjustedTradeRate = adblNonDimensionalAdjustedTradeRate) ||
					!org.drip.quant.common.NumberUtil.IsValid (_adblNonDimensionalHoldings) ||
						!org.drip.quant.common.NumberUtil.IsValid (_adblNonDimensionalAdjustedTradeRate))
			throw new java.lang.Exception
				("ContinuousCoordinatedVariationDynamic Constructor => Invalid Inputs");

		int iNumTimeNode = _adblNonDimensionalHoldings.length;

		if (0 == iNumTimeNode || iNumTimeNode != _adblNonDimensionalAdjustedTradeRate.length || iNumTimeNode
			!= _aNDC.length)
			throw new java.lang.Exception
				("ContinuousCoordinatedVariationDynamic Constructor => Invalid Inputs");

		for (int i = 0; i < iNumTimeNode; ++i) {
			if (null == _aNDC[i])
				throw new java.lang.Exception
					("ContinuousCoordinatedVariationDynamic Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Order Size
	 * 
	 * @return The Order Size
	 */

	public double orderSize()
	{
		return _dblOrderSize;
	}

	/**
	 * Retrieve the Array of the Non Dimensional Holdings
	 * 
	 * @return The Array of the Non Dimensional Holdings
	 */

	public double[] nonDimensionalHoldings()
	{
		return _adblNonDimensionalHoldings;
	}

	/**
	 * Retrieve the Array of the Non Dimensional Adjusted Trade Rate
	 * 
	 * @return The Array of the Non Dimensional Adjusted Trade Rate
	 */

	public double[] nonDimensionalAdjustedTradeRate()
	{
		return _adblNonDimensionalAdjustedTradeRate;
	}

	/**
	 * Retrieve the Array of the Non Dimensional Costs
	 * 
	 * @return The Array of the Non Dimensional Costs
	 */

	public org.drip.execution.adaptive.NonDimensionalCost[] nonDimensionalCost()
	{
		return _aNDC;
	}
}
