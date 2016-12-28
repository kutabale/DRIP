
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
 * NonDimensionalCost1D contains the Level, the Gradient, and the Jacobian of the HJB Non-dimensional Cost
 * 	Value Function to the 1D Market State. The References are:
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

public class NonDimensionalCost1D {
	private double _dblRealization = java.lang.Double.NaN;
	private double _dblRealizationGradient = java.lang.Double.NaN;
	private double _dblRealizationJacobian = java.lang.Double.NaN;
	private double _dblNonDimensionalTradeRate = java.lang.Double.NaN;

	/**
	 * Generate a Zero Sensitivity 1D Non-dimensional Cost Instance
	 * 
	 * @return The Zero Sensitivity 1D Non-dimensional Cost Instance
	 */

	public static final NonDimensionalCost1D Zero()
	{
		try {
			return new NonDimensionalCost1D (0., 0., 0., 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a Linear Trading 1D Non Dimensional Cost Instance
	 * 
	 * @param dblMarketStateExponentiation The Exponentiated Market State
	 * @param dblNonDimensionalTime The Non Dimensional Time
	 * 
	 * @return The Linear Trading 1D Non Dimensional Cost Instance
	 */

	public static final NonDimensionalCost1D LinearThreshold (
		final double dblMarketStateExponentiation,
		final double dblNonDimensionalTime)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblMarketStateExponentiation) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblNonDimensionalTime) || 0. >= dblNonDimensionalTime)
			return null;

		double dblNonDimensionalUrgency = 1. / dblNonDimensionalTime;
		double dblNonDimensionalCostThreshold = dblMarketStateExponentiation * dblNonDimensionalUrgency;

		try {
			return new NonDimensionalCost1D (dblNonDimensionalCostThreshold, dblNonDimensionalCostThreshold,
				dblNonDimensionalCostThreshold, dblNonDimensionalUrgency);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate a Euler Enhanced Linear Trading 1D Non Dimensional Cost Instance
	 * 
	 * @param dblMarketState The Market State
	 * @param dblNonDimensionalCost The Non Dimensional Cost
	 * @param dblNonDimensionalCostCross The Non Dimensional Cost Cross Term
	 * 
	 * @return The Euler Enhanced Linear Trading 1D Non Dimensional Cost Instance
	 */

	public static final NonDimensionalCost1D EulerEnhancedLinearThreshold (
		final double dblMarketState,
		final double dblNonDimensionalCost,
		final double dblNonDimensionalCostCross)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblMarketState) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblNonDimensionalCost) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblNonDimensionalCostCross))
			return null;

		try {
			return new NonDimensionalCost1D (dblNonDimensionalCost, dblNonDimensionalCost +
				dblNonDimensionalCostCross, dblNonDimensionalCost + 2. * dblNonDimensionalCostCross,
					java.lang.Math.exp (-dblMarketState) * dblNonDimensionalCost);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * NonDimensionalCost1D Constructor
	 * 
	 * @param dblRealization The Non dimensional Value Function Realization
	 * @param dblRealizationGradient The Non dimensional Value Function Realization Gradient
	 * @param dblRealizationJacobian The Non dimensional Value Function Realization Jacobian
	 * @param dblNonDimensionalTradeRate The Non-dimensional Trade Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NonDimensionalCost1D (
		final double dblRealization,
		final double dblRealizationGradient,
		final double dblRealizationJacobian,
		final double dblNonDimensionalTradeRate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblRealization = dblRealization) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblRealizationGradient = dblRealizationGradient) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblRealizationJacobian = dblRealizationJacobian)
					|| !org.drip.quant.common.NumberUtil.IsValid (_dblNonDimensionalTradeRate =
						dblNonDimensionalTradeRate))
			throw new java.lang.Exception ("NonDimensionalCost1D Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Realized Non-dimensional Value
	 * 
	 * @return The Realized Non-dimensional Value
	 */

	public double realization()
	{
		return _dblRealization;
	}

	/**
	 * Retrieve the Realized Non-dimensional Value Gradient
	 * 
	 * @return The Realized Non-dimensional Value Gradient
	 */

	public double realizationGradient()
	{
		return _dblRealizationGradient;
	}

	/**
	 * Retrieve the Realized Non-dimensional Value Jacobian
	 * 
	 * @return The Realized Non-dimensional Value Jacobian
	 */

	public double realizationJacobian()
	{
		return _dblRealizationJacobian;
	}

	/**
	 * Retrieve the Non-dimensional Trade Rate
	 * 
	 * @return The Non-dimensional Trade Rate
	 */

	public double nonDimensionalTradeRate()
	{
		return _dblNonDimensionalTradeRate;
	}
}
