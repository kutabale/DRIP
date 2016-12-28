
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
 * NonDimensionalCost2D contains the Level, the Gradient, and the Jacobian of the HJB Non-dimensional Cost
 * 	Value Function to the 2D Market State. The References are:
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

public class NonDimensionalCost2D {
	private double _dblRealization = java.lang.Double.NaN;
	private double _dblNonDimensionalTradeRate = java.lang.Double.NaN;
	private double _dblRealizationLiquidityGradient = java.lang.Double.NaN;
	private double _dblRealizationLiquidityJacobian = java.lang.Double.NaN;
	private double _dblRealizationVolatilityGradient = java.lang.Double.NaN;
	private double _dblRealizationVolatilityJacobian = java.lang.Double.NaN;
	private double _dblRealizationLiquidityVolatilityGradient = java.lang.Double.NaN;

	/**
	 * Generate a Zero Sensitivity 2D Non-dimensional Cost Instance
	 * 
	 * @return The Zero Sensitivity 2D Non-dimensional Cost Instance
	 */

	public static final NonDimensionalCost2D Zero()
	{
		try {
			return new NonDimensionalCost2D (0., 0., 0., 0., 0., 0., 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * NonDimensionalCost2D Constructor
	 * 
	 * @param dblRealization The Realized Non-dimensional Value
	 * @param dblNonDimensionalTradeRate The Non-dimensional Trade Rate
	 * @param dblRealizationLiquidityGradient The Realized Non-dimensional Value Liquidity Gradient
	 * @param dblRealizationLiquidityJacobian The Realized Non-dimensional Value Liquidity Jacobian
	 * @param dblRealizationVolatilityGradient The Realized Non-dimensional Value Volatility Gradient
	 * @param dblRealizationVolatilityJacobian The Realized Non-dimensional Value Volatility Jacobian
	 * @param dblRealizationLiquidityVolatilityGradient The Realized Non-dimensional Value
	 * 		Liquidity/Volatility Gradient
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NonDimensionalCost2D (
		final double dblRealization,
		final double dblRealizationLiquidityGradient,
		final double dblRealizationLiquidityJacobian,
		final double dblRealizationVolatilityGradient,
		final double dblRealizationVolatilityJacobian,
		final double dblRealizationLiquidityVolatilityGradient,
		final double dblNonDimensionalTradeRate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblRealization = dblRealization) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblNonDimensionalTradeRate =
				dblNonDimensionalTradeRate) || !org.drip.quant.common.NumberUtil.IsValid
					(_dblRealizationLiquidityGradient = dblRealizationLiquidityGradient) ||
						!org.drip.quant.common.NumberUtil.IsValid (_dblRealizationLiquidityJacobian =
							dblRealizationLiquidityJacobian) || !org.drip.quant.common.NumberUtil.IsValid
								(_dblRealizationVolatilityGradient = dblRealizationVolatilityGradient) ||
									!org.drip.quant.common.NumberUtil.IsValid
										(_dblRealizationVolatilityJacobian =
											dblRealizationVolatilityJacobian) ||
												!org.drip.quant.common.NumberUtil.IsValid
													(_dblRealizationLiquidityVolatilityGradient =
														dblRealizationLiquidityVolatilityGradient))
			throw new java.lang.Exception ("NonDimensionalCost2D Constructor => Invalid Inputs");
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
	 * Retrieve the Realized Non-dimensional Value Liquidity Gradient
	 * 
	 * @return The Realized Non-dimensional Value Liquidity Gradient
	 */

	public double realizationLiquidityGradient()
	{
		return _dblRealizationLiquidityGradient;
	}

	/**
	 * Retrieve the Realized Non-dimensional Value Liquidity Jacobian
	 * 
	 * @return The Realized Non-dimensional Value Liquidity Jacobian
	 */

	public double realizationLiquidityJacobian()
	{
		return _dblRealizationLiquidityJacobian;
	}

	/**
	 * Retrieve the Realized Non-dimensional Value Volatility Gradient
	 * 
	 * @return The Realized Non-dimensional Value Volatility Gradient
	 */

	public double realizationVolatilityGradient()
	{
		return _dblRealizationVolatilityGradient;
	}

	/**
	 * Retrieve the Realized Non-dimensional Value Volatility Jacobian
	 * 
	 * @return The Realized Non-dimensional Value Volatility Jacobian
	 */

	public double realizationVolatilityJacobian()
	{
		return _dblRealizationVolatilityJacobian;
	}

	/**
	 * Retrieve the Realized Non-dimensional Value Liquidity/Volatility Gradient
	 * 
	 * @return The Realized Non-dimensional Value Liquidity/Volatility Gradient
	 */

	public double realizationLiquidityVolatilityGradient()
	{
		return _dblRealizationLiquidityVolatilityGradient;
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
