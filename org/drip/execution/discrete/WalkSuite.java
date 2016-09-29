
package org.drip.execution.discrete;

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
 * WalkSuite holds the Walk Random Variables (e.g., Weiner Variates) that correspond to an Instance of Walk
 * 	attributable to different Factor Contributions inside of a Slice Increment. The References are:
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

public class WalkSuite {
	private double _dblPermanentImpact = java.lang.Double.NaN;
	private double _dblTemporaryImpact = java.lang.Double.NaN;
	private double _dblMarketCoreCurrent = java.lang.Double.NaN;
	private double _dblMarketCorePrevious = java.lang.Double.NaN;

	/**
	 * WalkSuite Constructor
	 * 
	 * @param dblMarketCorePrevious The Previous Market Core Walk Realization
	 * @param dblMarketCoreCurrent The Current Market Core Walk Realization
	 * @param dblPermanentImpact The Permanent Impact Walk Realization
	 * @param dblTemporaryImpact The Temporary Impact Walk Realization
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public WalkSuite (
		final double dblMarketCorePrevious,
		final double dblMarketCoreCurrent,
		final double dblPermanentImpact,
		final double dblTemporaryImpact)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblMarketCorePrevious = dblMarketCorePrevious) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblMarketCoreCurrent = dblMarketCoreCurrent) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblPermanentImpact = dblPermanentImpact) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblTemporaryImpact = dblTemporaryImpact))
			throw new java.lang.Exception ("WalkSuite Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Previous Instance of the Market Core Walk Wanderer
	 * 
	 * @return The Previous Instance of the Market Core Walk Wanderer
	 */

	public double marketCorePreviousWanderer()
	{
		return _dblMarketCorePrevious;
	}

	/**
	 * Retrieve the Current Instance of the Market Core Walk Wanderer
	 * 
	 * @return The Current Instance of the Market Core Walk Wanderer
	 */

	public double marketCoreCurrentWanderer()
	{
		return _dblMarketCoreCurrent;
	}

	/**
	 * Retrieve the Previous Instance of the Permanent Impact Walk Wanderer
	 * 
	 * @return The Previous Instance of the Permanent Impact Walk Wanderer
	 */

	public double permanentImpactWanderer()
	{
		return _dblPermanentImpact;
	}

	/**
	 * Retrieve the Previous Instance of the Temporary Impact Walk Wanderer
	 * 
	 * @return The Previous Instance of the Temporary Impact Walk Wanderer
	 */

	public double temporaryImpactWanderer()
	{
		return _dblTemporaryImpact;
	}
}
