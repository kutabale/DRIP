
package org.drip.portfolioconstruction.allocator;

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
 * MeanVarianceOptimizer exposes Portfolio Construction using Mean Variance Optimization Techniques.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class MeanVarianceOptimizer {

	protected abstract org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters
		constrainedPCP (
			final org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters pcpDesign,
			final double dblReturnsConstraint);

	/**
	 * Allocate the Long-Only Maximum Returns Portfolio
	 * 
	 * @param pcp The Portfolio Construction Parameters
	 * @param ausp The Asset Universe Statistical Properties Instance
	 * 
	 * @return The Long-Only Maximum Returns Portfolio
	 */

	public abstract org.drip.portfolioconstruction.allocator.OptimizationOutput longOnlyMaximumReturnsAllocate
		(final org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters pcp,
		final org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties ausp);

	/**
	 * Allocate the Global Minimum Variance Portfolio without any Returns Constraints in the Parameters
	 * 
	 * @param pcp The Portfolio Construction Parameters
	 * @param ausp The Asset Universe Statistical Properties Instance
	 * 
	 * @return The Global Minimum Variance Portfolio
	 */

	public abstract org.drip.portfolioconstruction.allocator.OptimizationOutput globalMinimumVarianceAllocate (
		final org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters pcp,
		final org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties ausp);

	/**
	 * Allocate the Optimal Portfolio Weights given the Portfolio Construction Parameters
	 * 
	 * @param pcp The Portfolio Construction Parameters
	 * @param ausp The Asset Universe Statistical Properties Instance
	 * 
	 * @return The Optimal Portfolio
	 */

	public abstract org.drip.portfolioconstruction.allocator.OptimizationOutput allocate (
		final org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters pcp,
		final org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties ausp);

	/**
	 * Generate the Efficient Frontier given the Portfolio Construction Parameters
	 * 
	 * @param pcp The Portfolio Construction Parameters
	 * @param ausp The Asset Universe Statistical Properties Instance
	 * @param iFrontierSampleUnits The Number of Frontier Sample Units
	 * 
	 * @return The Efficient Frontier
	 */

	public org.drip.portfolioconstruction.mpt.MarkovitzBullet efficientFrontier (
		final org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters pcp,
		final org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties ausp,
		final int iFrontierSampleUnits)
	{
		if (0 >= iFrontierSampleUnits) return null;

		org.drip.portfolioconstruction.allocator.OptimizationOutput opGlobalMinimumVariance =
			globalMinimumVarianceAllocate (pcp, ausp);

		if (null == opGlobalMinimumVariance) return null;

		org.drip.portfolioconstruction.allocator.OptimizationOutput opLongOnlyMaximumReturns =
			longOnlyMaximumReturnsAllocate (pcp, ausp);

		if (null == opLongOnlyMaximumReturns) return null;

		double dblReturnsGlobalMinimumVariance =
			opGlobalMinimumVariance.optimalMetrics().excessReturnsMean();

		double dblReturnsLongOnlyMaximumReturns =
			opLongOnlyMaximumReturns.optimalMetrics().excessReturnsMean();

		double dblReturnsConstraintGridWidth = (dblReturnsLongOnlyMaximumReturns -
			dblReturnsGlobalMinimumVariance) / iFrontierSampleUnits;
		double dblReturnsConstraint = dblReturnsGlobalMinimumVariance + dblReturnsConstraintGridWidth;
		org.drip.portfolioconstruction.mpt.MarkovitzBullet mb = null;

		try {
			mb = new org.drip.portfolioconstruction.mpt.MarkovitzBullet (opGlobalMinimumVariance,
				opLongOnlyMaximumReturns);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		while (dblReturnsConstraint <= dblReturnsLongOnlyMaximumReturns) {
			try {
				mb.addOptimalPortfolio (allocate (constrainedPCP (pcp, dblReturnsConstraint), ausp));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			dblReturnsConstraint += dblReturnsConstraintGridWidth;
		}

		return mb;
	}
}
