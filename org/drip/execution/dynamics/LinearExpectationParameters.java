
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
 * LinearExpectationParameters contains the Exogenous Parameters that determine the Dynamics of the Price
 *  Movements exhibited by an Asset owing to the Volatility and the Linear Market Impact Permanent/Temporary
 *  Factors. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 *
 * 	- Chan, L. K. C., and J. Lakonishak (1995): The Behavior of Stock Prices around Institutional Trades,
 * 		Journal of Finance, 50, 1147-1174.
 *
 * 	- Keim, D. B., and A. Madhavan (1997): Transaction Costs and Investment Style: An Inter-exchange
 * 		Analysis of Institutional Equity Trades, Journal of Financial Economics, 46, 265-292.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class LinearExpectationParameters extends
	org.drip.execution.dynamics.LinearPermanentExpectationParameters {

	/**
	 * LinearExpectationParameters Constructor
	 * 
	 * @param apds The Asset Price Dynamics Settings
	 * @param tflPermanentExpectation The Linear Permanent Market Impact Expectation Function
	 * @param tflTemporaryExpectation The Linear Temporary Market Impact Expectation Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LinearExpectationParameters (
		final org.drip.execution.parameters.ArithmeticPriceDynamicsSettings apds,
		final org.drip.execution.impact.TransactionFunctionLinear tflPermanentExpectation,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporaryExpectation)
		throws java.lang.Exception
	{
		super (apds, tflPermanentExpectation, tflTemporaryExpectation);
	}

	/**
	 * Retrieve the Linear Temporary Market Impact Expectation Function
	 * 
	 * @return The Linear Temporary Market Impact Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunctionLinear linearTemporaryExpectation()
	{
		return (org.drip.execution.impact.TransactionFunctionLinear) temporaryExpectation();
	}
}
