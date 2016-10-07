
package org.drip.execution.impact;

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
 * TransactionFunction exports the Temporary/Permanent Market Impact Displacement/Volatility Functional
 *  Dependence on the Trade Rate. The References are:
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

public abstract class TransactionFunction extends org.drip.function.definition.R1ToR1 {

	protected TransactionFunction()
	{
		super (null);
	}

	/**
	 * Regularize the Input Function using the specified Trade Inputs
	 * 
	 * @param dblTradeInterval The Trade Interval
	 * 
	 * @return The Regularize Input
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract double regularize (
		final double dblTradeInterval)
		throws java.lang.Exception;

	/**
	 * Modulate/Scale the Impact Output
	 * 
	 * @param dblTradeInterval The Trade Interval
	 * 
	 * @return The Modulated Output
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract double modulate (
		final double dblTradeInterval)
		throws java.lang.Exception;

	/**
	 * Evaluate the Impact Function at the specified Trade Parameters
	 * 
	 * @param dblTradeAmount The Trade Amount
	 * @param dblTradeInterval The Trade Interval
	 * 
	 * @return The Value of the Impact Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double evaluate (
		final double dblTradeAmount,
		final double dblTradeInterval)
		throws java.lang.Exception
	{
		return modulate (dblTradeInterval) * evaluate (dblTradeAmount * regularize (dblTradeInterval));
	}

	/**
	 * Compute the Sensitivity to the Left Holdings
	 * 
	 * @param dblTradeAmount The Trade Amount
	 * @param dblTradeInterval The Trade Interval
	 * @param iOrder Order of the Derivative
	 * 
	 * @return The Sensitivity to the Left Holdings of the Impact Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double leftHoldingsDerivative (
		final double dblTradeAmount,
		final double dblTradeInterval,
		final int iOrder)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblTradeAmount) || 0 >= iOrder)
			throw new java.lang.Exception ("TransactionFunction::leftHoldingsDerivative => Invalid Inputs");

		double dblRegularizedInput = regularize (dblTradeInterval);

		return modulate (dblTradeInterval) * java.lang.Math.pow (-1. * dblRegularizedInput, iOrder) *
			derivative (dblTradeAmount * dblRegularizedInput, iOrder);
	}

	/**
	 * Compute the Sensitivity to the Right Holdings
	 * 
	 * @param dblTradeAmount The Trade Amount
	 * @param dblTradeInterval The Trade Interval
	 * @param iOrder Order of the Derivative
	 * 
	 * @return The Sensitivity to the Right Holdings of the Impact Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double rightHoldingsDerivative (
		final double dblTradeAmount,
		final double dblTradeInterval,
		final int iOrder)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblTradeAmount) || 0 >= iOrder)
			throw new java.lang.Exception ("TransactionFunction::rightHoldingsDerivative => Invalid Inputs");

		double dblRegularizedInput = regularize (dblTradeInterval);

		return modulate (dblTradeInterval) * java.lang.Math.pow (dblRegularizedInput, iOrder) * derivative
			(dblTradeAmount * dblRegularizedInput, iOrder);
	}

	/**
	 * Compute the Second Order Sensitivity to the Left/Right Holdings
	 * 
	 * @param dblTradeAmount The Trade Amount
	 * @param dblTradeInterval The Trade Interval
	 * 
	 * @return The Second Order Sensitivity to the Left/Right Holdings of the Impact Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double crossHoldingsDerivative (
		final double dblTradeAmount,
		final double dblTradeInterval)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblTradeAmount))
			throw new java.lang.Exception ("TransactionFunction::crossHoldingsDerivative => Invalid Inputs");

		double dblRegularizedInput = regularize (dblTradeInterval);

		return -1. * modulate (dblTradeInterval) * dblRegularizedInput * dblRegularizedInput * derivative
			(dblTradeAmount * dblRegularizedInput, 2);
	}
}
