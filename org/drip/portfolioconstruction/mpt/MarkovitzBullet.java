
package org.drip.portfolioconstruction.mpt;

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
 * MarkovitzBullet holds the Portfolio Performance Metrics across a Variety of Return Constraints.
 *
 * @author Lakshmi Krishnamurthy
 */

public class MarkovitzBullet {
	private org.drip.portfolioconstruction.allocator.OptimizationOutput _opGlobalMinimumVariance = null;
	private org.drip.portfolioconstruction.allocator.OptimizationOutput _opLongOnlyMaximumReturns = null;

	private java.util.TreeMap<java.lang.Double, org.drip.portfolioconstruction.allocator.OptimizationOutput>
		_mapOptimalPortfolio = new java.util.TreeMap<java.lang.Double,
			org.drip.portfolioconstruction.allocator.OptimizationOutput>();

	/**
	 * MarkovitzBullet Constructor
	 * 
	 * @param opGlobalMinimumVariance The Global Minimum Variance Optimal Portfolio
	 * @param opLongOnlyMaximumReturns The Long Only Maximum Returns Optimal Portfolio
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MarkovitzBullet (
		final org.drip.portfolioconstruction.allocator.OptimizationOutput opGlobalMinimumVariance,
		final org.drip.portfolioconstruction.allocator.OptimizationOutput opLongOnlyMaximumReturns)
		throws java.lang.Exception
	{
		if (null == (_opGlobalMinimumVariance = opGlobalMinimumVariance) || null ==
			(_opLongOnlyMaximumReturns = opLongOnlyMaximumReturns))
			throw new java.lang.Exception ("MarkovitzBullet Constructor => Invalid inputs");

		_mapOptimalPortfolio.put (_opGlobalMinimumVariance.optimalMetrics().excessReturnsMean(),
			_opGlobalMinimumVariance);

		_mapOptimalPortfolio.put (_opLongOnlyMaximumReturns.optimalMetrics().excessReturnsMean(),
			_opLongOnlyMaximumReturns);
	}

	/**
	 * Retrieve the Global Minimum Variance Portfolio Metrics
	 * 
	 * @return The Global Minimum Variance Portfolio Metrics
	 */

	public org.drip.portfolioconstruction.allocator.OptimizationOutput globalMinimumVariance()
	{
		return _opGlobalMinimumVariance;
	}

	/**
	 * Retrieve the Long Only Maximum Returns Portfolio Metrics
	 * 
	 * @return The Long Only Maximum Returns Portfolio Metrics
	 */

	public org.drip.portfolioconstruction.allocator.OptimizationOutput longOnlyMaximumReturns()
	{
		return _opLongOnlyMaximumReturns;
	}

	/**
	 * Add a Returns Constrained Optimal Portfolio
	 * 
	 * @param op The Returns Constrained Optimal Portfolio
	 * 
	 * @return TRUE - The Returns Constrained Optimal Portfolio Successfully Added
	 */

	public boolean addOptimalPortfolio (
		final org.drip.portfolioconstruction.allocator.OptimizationOutput op)
	{
		if (null == op) return false;

		_mapOptimalPortfolio.put (op.optimalMetrics().excessReturnsMean(), op);

		return true;
	}

	/**
	 * Retrieve the Map of Optimal Portfolios
	 * 
	 * @return The Map of Optimal Portfolios
	 */

	public java.util.TreeMap<java.lang.Double, org.drip.portfolioconstruction.allocator.OptimizationOutput>
		optimalPortfolios()
	{
		return _mapOptimalPortfolio;
	}
}
