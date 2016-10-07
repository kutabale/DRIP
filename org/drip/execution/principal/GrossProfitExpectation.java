
package org.drip.execution.principal;

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
 * GrossProfitExpectation implements the R^1 To R^1 Univariate that computes the Explicit Profit of a
 *  Principal Execution given the Optimal Trajectory. The References are:
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
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class GrossProfitExpectation extends org.drip.function.definition.R1ToR1 {
	private double _dblNumExecutedUnit = java.lang.Double.NaN;
	private double _dblTransactionCost = java.lang.Double.NaN;

	/**
	 * GrossProfitExpectation Constructor
	 * 
	 * @param dblNumExecutedUnit The Number of Executed Units
	 * @param dblTransactionCost The Execution Transaction Cost
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public GrossProfitExpectation (
		final double dblNumExecutedUnit,
		final double dblTransactionCost)
		throws java.lang.Exception
	{
		super (null);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblNumExecutedUnit = dblNumExecutedUnit) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblTransactionCost = dblTransactionCost))
			throw new java.lang.Exception ("GrossProfitExpectation Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Execution Transaction Cost
	 * 
	 * @return The Execution Transaction Cost
	 */

	public double transactionCost()
	{
		return _dblTransactionCost;
	}

	/**
	 * Retrieve the Number of Executed Units
	 * 
	 * @return The Number of Executed Units
	 */

	public double numExecutedUnit()
	{
		return _dblNumExecutedUnit;
	}

	@Override public double evaluate (
		final double dblUnitDiscount)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblUnitDiscount))
			throw new java.lang.Exception ("GrossProfitExpectation::evaluate => Invalid Inputs");

		return dblUnitDiscount * _dblNumExecutedUnit - _dblTransactionCost;
	}
}
