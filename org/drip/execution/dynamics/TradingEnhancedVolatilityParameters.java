
package org.drip.execution.dynamics;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
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
 * TradingEnhancedVolatilityParameters uses the Linear Temporary Drift/Volatility Impact Functions specified
 *  for the Trading-Enhanced Risk Scenario in Almgren (2003). The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 97-102.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TradingEnhancedVolatilityParameters extends
	org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters {

	/**
	 * TradingEnhancedVolatilityParameters Constructor
	 * 
	 * @param dblPriceVolatility The Daily Price Volatility Parameter
	 * @param tflTemporaryExpectation The Linear Temporary Market Impact Expectation Function
	 * @param tflTemporaryVolatility The Linear Temporary Market Impact Volatility Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TradingEnhancedVolatilityParameters (
		final double dblPriceVolatility,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporaryExpectation,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporaryVolatility)
		throws java.lang.Exception
	{
		super (new org.drip.execution.parameters.ArithmeticPriceDynamicsSettings (0., dblPriceVolatility,
			0.), org.drip.execution.impact.ParticipationRateLinear.NoImpact(), tflTemporaryExpectation,
				org.drip.execution.impact.ParticipationRateLinear.NoImpact(), tflTemporaryVolatility);
	}

	/**
	 * Retrieve the Linear Temporary Expectation Function
	 * 
	 * @return The Linear Temporary Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunctionLinear linearTemporaryExpectation()
	{
		return (org.drip.execution.impact.TransactionFunctionLinear) temporaryExpectation();
	}

	/**
	 * Retrieve the Linear Temporary Volatility Function
	 * 
	 * @return The Linear Temporary Volatility Function
	 */

	public org.drip.execution.impact.TransactionFunctionLinear linearTemporaryVolatility()
	{
		return (org.drip.execution.impact.TransactionFunctionLinear) temporaryVolatility();
	}
}
