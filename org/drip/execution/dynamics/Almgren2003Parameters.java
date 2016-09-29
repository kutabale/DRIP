
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
 * Almgren2003Parameters uses the Specific Temporary/Permanent Market Impact Displacement and Volatility
 * 	Functions specified in Almgren (2003). The References are:
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

public class Almgren2003Parameters extends org.drip.execution.dynamics.LinearPermanentExpectationParameters {

	/**
	 * Almgren2003Parameters Constructor
	 * 
	 * @param dblMarketCoreDrift The Market Core Drift
	 * @param dblMarketCoreVolatility The Market Core Volatility
	 * @param dblMarketCoreSerialCorrelation The Market Core Serial Correlation
	 * @param tflPermanent The Linear Permanent Market Impact Expectation Function
	 * @param tfpTemporary The Power Temporary Market Impact Expectation Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public Almgren2003Parameters (
		final double dblMarketCoreDrift,
		final double dblMarketCoreVolatility,
		final double dblMarketCoreSerialCorrelation,
		final org.drip.execution.impact.TransactionFunctionLinear tflPermanent,
		final org.drip.execution.impact.TransactionFunctionPower tfpTemporary)
		throws java.lang.Exception
	{
		super (dblMarketCoreDrift, dblMarketCoreVolatility, dblMarketCoreSerialCorrelation, tflPermanent,
			tfpTemporary);
	}

	/**
	 * Retrieve the Power Temporary Market Impact Expectation Function
	 * 
	 * @return The Power Temporary Market Impact Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunctionPower powerTemporaryExpectation()
	{
		return (org.drip.execution.impact.TransactionFunctionPower) temporaryExpectation();
	}
}
