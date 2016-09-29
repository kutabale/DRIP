
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
 * TradingTrajectoryControl holds the Time Trajectory Control Settings of a Trading Block that is to be
 *  executed over Time. The References are:
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

public class TradingTrajectoryControl {
	private double[] _adblExecutionTimeNode = null;
	private double _dblStartHoldings = java.lang.Double.NaN;

	/**
	 * Create a TradingTrajectoryControl from Fixed Intervals
	 * 
	 * @param os The Order Specification
	 * @param iNumInterval The Number of Fixed Intervals
	 * 
	 * @return The TradingTrajectoryControl from Fixed Intervals
	 */

	public static final TradingTrajectoryControl FixedInterval (
		final org.drip.execution.strategy.OrderSpecification os,
		final int iNumInterval)
	{
		if (null == os || 0 >= iNumInterval) return null;

		double dblFinishTime = os.executionTime();

		double dblTimeInterval = dblFinishTime / iNumInterval;
		double[] adblExecutionTimeNode = new double[iNumInterval + 1];
		adblExecutionTimeNode[iNumInterval] = dblFinishTime;
		adblExecutionTimeNode[0] = 0.;

		for (int i = 1; i < iNumInterval; ++i)
			adblExecutionTimeNode[i] = i * dblTimeInterval;

		try {
			return new TradingTrajectoryControl (os.size(), adblExecutionTimeNode);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a Single Interval TradingTrajectoryControl Instance from the Order Specification
	 * 
	 * @param os The Order Specification
	 * 
	 * @return The Single Interval TradingTrajectoryControl Instance from the Order Specification
	 */

	public static final TradingTrajectoryControl SingleInterval (
		final org.drip.execution.strategy.OrderSpecification os)
	{
		return FixedInterval (os, 1);
	}

	/**
	 * TradingTrajectoryControl Constructor
	 * 
	 * @param dblStartHoldings Trajectory Start Holdings
	 * @param adblExecutionTimeNode Array of the Trajectory Time Snapshots
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TradingTrajectoryControl (
		final double dblStartHoldings,
		final double[] adblExecutionTimeNode)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblStartHoldings = dblStartHoldings) || null ==
			(_adblExecutionTimeNode = adblExecutionTimeNode))
			throw new java.lang.Exception ("TradingTrajectoryControl Constructor => Invalid Inputs!");

		int iNumExecutionTimeNode = _adblExecutionTimeNode.length;

		if (1 >= iNumExecutionTimeNode || !org.drip.quant.common.NumberUtil.IsValid
			(_adblExecutionTimeNode[0]))
			throw new java.lang.Exception ("TradingTrajectoryControl Constructor => Invalid Inputs!");

		for (int i = 1; i < iNumExecutionTimeNode; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblExecutionTimeNode[i]) ||
				_adblExecutionTimeNode[i - 1] >= _adblExecutionTimeNode[i])
				throw new java.lang.Exception ("TradingTrajectoryControl Constructor => Invalid Inputs!");
		}
	}

	/**
	 * Retrieve the Finish Time of the Trading Trajectory
	 * 
	 * @return The Finish Time of the Trading Trajectory
	 */

	public double finishTime()
	{
		return _adblExecutionTimeNode[_adblExecutionTimeNode.length - 1];
	}

	/**
	 * Retrieve the Array containing the Execution Time Nodes
	 * 
	 * @return The Array containing the Execution Time Nodes
	 */

	public double[] executionTimeNodes()
	{
		return _adblExecutionTimeNode;
	}

	/**
	 * Retrieve the Initial Holdings, i.e., the Starting Number of Units to the Executed
	 * 
	 * @return The Initial Holdings, i.e., the Starting Number of Units to the Executed
	 */

	public double startHoldings()
	{
		return _dblStartHoldings;
	}

	/**
	 * Generate the Order Specification corresponding to the Trajectory Control
	 * 
	 * @return The Order Specificaton Instance
	 */

	public org.drip.execution.strategy.OrderSpecification order()
	{
		try {
			return new org.drip.execution.strategy.OrderSpecification (_dblStartHoldings, finishTime());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
