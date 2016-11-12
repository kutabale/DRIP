
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
 * LinearPermanentExpectationParameters implements a Permanent Market Impact Function where the Price Change
 *  scales linearly with the Trade Rate. The References are:
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

public class LinearPermanentExpectationParameters extends
	org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters {

	/**
	 * LinearPermanentExpectationParameters Constructor
	 * 
	 * @param dblMarketCoreDrift The Market Core Drift
	 * @param dblMarketCoreVolatility The Market Core Volatility
	 * @param dblMarketCoreSerialCorrelation The Market Core Serial Correlation
	 * @param tflPermanentExpectation The Linear Permanent Expectation Market Impact Function
	 * @param tfTemporaryExpectation The Temporary Expectation Market Impact Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LinearPermanentExpectationParameters (
		final double dblMarketCoreDrift,
		final double dblMarketCoreVolatility,
		final double dblMarketCoreSerialCorrelation,
		final org.drip.execution.impact.TransactionFunctionLinear tflPermanentExpectation,
		final org.drip.execution.impact.TransactionFunction tfTemporaryExpectation)
		throws java.lang.Exception
	{
		super (dblMarketCoreDrift, dblMarketCoreVolatility, dblMarketCoreSerialCorrelation,
			tflPermanentExpectation, tfTemporaryExpectation,
				org.drip.execution.impact.ParticipationRateLinear.NoImpact(),
					org.drip.execution.impact.ParticipationRateLinear.NoImpact());
	}

	/**
	 * Retrieve the Linear Permanent Market Impact Expectation Function
	 * 
	 * @return The Linear Permanent Market Impact Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunctionLinear linearPermanentExpectation()
	{
		return (org.drip.execution.impact.TransactionFunctionLinear) permanentExpectation();
	}
}
