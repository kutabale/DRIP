
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
 * ArithmeticPriceEvolutionParameters contains the Exogenous Parameters that determine the Dynamics of the
 *  Arithmetic Price Movements exhibited by an Asset owing to the Volatility and the Market Impact Factors.
 *  The References are:
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

public class ArithmeticPriceEvolutionParameters {
	private double _dblMarketCoreDrift = java.lang.Double.NaN;
	private double _dblMarketCoreVolatility = java.lang.Double.NaN;
	private double _dblMarketCoreSerialCorrelation = java.lang.Double.NaN;
	private org.drip.execution.impact.TransactionFunction _tfPermanentVolatility = null;
	private org.drip.execution.impact.TransactionFunction _tfTemporaryVolatility = null;
	private org.drip.execution.impact.TransactionFunction _tfPermanentExpectation = null;
	private org.drip.execution.impact.TransactionFunction _tfTemporaryExpectation = null;

	/**
	 * ArithmeticPriceEvolutionParameters Constructor
	 * 
	 * @param dblMarketCoreDrift The Market Core Drift
	 * @param dblMarketCoreVolatility The Market Core Volatility
	 * @param dblMarketCoreSerialCorrelation The Market Core Serial Correlation
	 * @param tfPermanentExpectation The Permanent Market Impact Expectation Function
	 * @param tfTemporaryExpectation The Temporary Market Impact Expectation Function
	 * @param tfPermanentVolatility The Permanent Market Impact Volatility Function
	 * @param tfTemporaryVolatility The Temporary Market Impact Volatility Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ArithmeticPriceEvolutionParameters (
		final double dblMarketCoreDrift,
		final double dblMarketCoreVolatility,
		final double dblMarketCoreSerialCorrelation,
		final org.drip.execution.impact.TransactionFunction tfPermanentExpectation,
		final org.drip.execution.impact.TransactionFunction tfTemporaryExpectation,
		final org.drip.execution.impact.TransactionFunction tfPermanentVolatility,
		final org.drip.execution.impact.TransactionFunction tfTemporaryVolatility)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblMarketCoreDrift = dblMarketCoreDrift) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblMarketCoreVolatility = dblMarketCoreVolatility) ||
				0. >= _dblMarketCoreVolatility || !org.drip.quant.common.NumberUtil.IsValid
					(_dblMarketCoreSerialCorrelation = dblMarketCoreSerialCorrelation) || 1. <
						_dblMarketCoreSerialCorrelation || -1. > _dblMarketCoreSerialCorrelation || null ==
							(_tfPermanentExpectation = tfPermanentExpectation) || null ==
								(_tfTemporaryExpectation = tfTemporaryExpectation) || null ==
									(_tfPermanentVolatility = tfPermanentVolatility) || null ==
										(_tfTemporaryVolatility = tfTemporaryVolatility))
			throw new java.lang.Exception
				("ArithmeticPriceEvolutionParameters Constructor => Invalid Inputs!");
	}

	/**
	 * Retrieve the Market Core Drift of the Arithmetic Dynamics
	 * 
	 * @return The Market Core Drift of the Arithmetic Dynamics
	 */

	public double marketCoreDrift()
	{
		return _dblMarketCoreDrift;
	}

	/**
	 * Retrieve the Market Core Volatility of the Arithmetic Dynamics
	 * 
	 * @return The Market Core Volatility of the Arithmetic Dynamics
	 */

	public double marketCoreVolatility()
	{
		return _dblMarketCoreVolatility;
	}

	/**
	 * Retrieve the Market Core Serial Correlation
	 *  
	 * @return The Market Core Serial Correlation
	 */

	public double marketCoreSerialCorrelation()
	{
		return _dblMarketCoreSerialCorrelation;
	}

	/**
	 * Retrieve the Permanent Market Impact Expectation Function
	 * 
	 * @return The Permanent Market Impact Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunction permanentExpectation()
	{
		return _tfPermanentExpectation;
	}

	/**
	 * Retrieve the Temporary Market Impact Expectation Function
	 * 
	 * @return The Temporary Market Impact Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunction temporaryExpectation()
	{
		return _tfTemporaryExpectation;
	}

	/**
	 * Retrieve the Permanent Market Impact Volatility Function
	 * 
	 * @return The Permanent Market Impact Volatility Function
	 */

	public org.drip.execution.impact.TransactionFunction permanentVolatility()
	{
		return _tfPermanentVolatility;
	}

	/**
	 * Retrieve the Temporary Market Impact Volatility Function
	 * 
	 * @return The Temporary Market Impact Volatility Function
	 */

	public org.drip.execution.impact.TransactionFunction temporaryVolatility()
	{
		return _tfTemporaryVolatility;
	}
}
