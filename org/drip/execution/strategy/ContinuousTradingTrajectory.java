
package org.drip.execution.strategy;

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
 * ContinuousTradingTrajectory holds the Continuous Trajectory of a Trading Block that is to be executed over
 *  the Specified Horizon. The References are:
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

public class ContinuousTradingTrajectory implements org.drip.execution.strategy.TradingTrajectory {
	private double _dblExecutionTime = java.lang.Double.NaN;
	private org.drip.function.definition.R1ToR1 _r1ToR1Holdings = null;
	private org.drip.function.definition.R1ToR1 _r1ToR1TradeRate = null;

	/**
	 * Construct a Standard Instance of ContinuousTradingTrajectory
	 * 
	 * @param dblExecutionTime The Execution Time
	 * @param r1ToR1Holdings The Holdings Function
	 * 
	 * @return Standard Instance of ContinuousTradingTrajectory
	 */

	public static final ContinuousTradingTrajectory Standard (
		final double dblExecutionTime,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings)
	{
		if (null == r1ToR1Holdings) return null;

		org.drip.function.definition.R1ToR1 r1ToR1TradeRate = new org.drip.function.definition.R1ToR1 (null)
		{
			@Override public double evaluate (
				final double dblVariate)
				throws java.lang.Exception
			{
				return r1ToR1Holdings.derivative (dblVariate, 1);
			}
		};

		try {
			return new ContinuousTradingTrajectory (dblExecutionTime, r1ToR1Holdings, r1ToR1TradeRate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ContinuousTradingTrajectory Constructor
	 * 
	 * @param dblExecutionTime The Execution Time
	 * @param r1ToR1Holdings The Holdings Function
	 * @param r1ToR1TradeRate The Trade Rate Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ContinuousTradingTrajectory (
		final double dblExecutionTime,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings,
		final org.drip.function.definition.R1ToR1 r1ToR1TradeRate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblExecutionTime = dblExecutionTime) || null ==
			(_r1ToR1Holdings = r1ToR1Holdings) || null == (_r1ToR1TradeRate = r1ToR1TradeRate))
			throw new java.lang.Exception ("ContinuousTradingTrajectory Constructor => Invalid Inputs");
	}

	@Override public double tradeSize()
	{
		try {
			return _r1ToR1Holdings.evaluate (0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return 0.;
	}

	@Override public double executedBlockSize()
	{
		try {
			return _r1ToR1Holdings.evaluate (0.) - _r1ToR1Holdings.evaluate (_dblExecutionTime);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return 0.;
	}

	@Override public double executionTime()
	{
		return _dblExecutionTime;
	}

	/**
	 * Retrieve the Holdings Function
	 * 
	 * @return The Holdings Function
	 */

	public org.drip.function.definition.R1ToR1 holdings()
	{
		return _r1ToR1Holdings;
	}

	/**
	 * Retrieve the Trade Rate Function
	 * 
	 * @return The Trade Rate Function
	 */

	public org.drip.function.definition.R1ToR1 tradeRate()
	{
		return _r1ToR1TradeRate;
	}
}
