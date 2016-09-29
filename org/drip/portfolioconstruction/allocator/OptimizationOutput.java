
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
 * OptimizationOutput holds the Output of an Optimal Portfolio Construction Run, i.e., the Optimal Asset
 *  Weights in the Portfolio and the related Portfolio Metrics.
 *
 * @author Lakshmi Krishnamurthy
 */

public class OptimizationOutput {
	private org.drip.portfolioconstruction.asset.Portfolio _pfOptimal = null;
	private org.drip.portfolioconstruction.asset.PortfolioMetrics _pmOptimal = null;

	/**
	 * Create an Instance of the Optimal Portfolio
	 * 
	 * @param aACOptimal The Array of the Optimal Asset Components
	 * @param ausp The AUSP Instance
	 * 
	 * @return The Instance of the Optimal Portfolio
	 */

	public static final OptimizationOutput Create (
		final org.drip.portfolioconstruction.asset.AssetComponent[] aACOptimal,
		final org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties ausp)
	{
		if (null == aACOptimal || null == ausp) return null;

		int iNumAsset = aACOptimal.length;

		if (0 == iNumAsset) return null;

		try {
			org.drip.portfolioconstruction.asset.Portfolio pfOptimal = new
				org.drip.portfolioconstruction.asset.Portfolio (aACOptimal);

			double dblPortfolioExcessReturnsMean = pfOptimal.expectedReturn (ausp);

			double dblPortfolioExcessReturnsVariance = pfOptimal.variance (ausp);

			double dblPortfolioExcessReturnsSigma = java.lang.Math.sqrt
				(dblPortfolioExcessReturnsVariance);

			double[] adblImpliedBeta = org.drip.quant.linearalgebra.Matrix.Product (ausp.covariance
				(pfOptimal.id()), pfOptimal.weights());

			if (null == adblImpliedBeta) return null;

			for (int i = 0; i < iNumAsset; ++i)
				adblImpliedBeta[i] = adblImpliedBeta[i] / dblPortfolioExcessReturnsVariance;

			return new org.drip.portfolioconstruction.allocator.OptimizationOutput (new
				org.drip.portfolioconstruction.asset.Portfolio (aACOptimal), new
					org.drip.portfolioconstruction.asset.PortfolioMetrics (dblPortfolioExcessReturnsMean,
						dblPortfolioExcessReturnsVariance, dblPortfolioExcessReturnsSigma,
							dblPortfolioExcessReturnsMean / dblPortfolioExcessReturnsSigma,
								adblImpliedBeta));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * OptimizationOutput Constructor
	 * 
	 * @param pfOptimal The Optimal Portfolio
	 * @param pmOptimal The Optimal Portfolio Metrics
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public OptimizationOutput (
		final org.drip.portfolioconstruction.asset.Portfolio pfOptimal,
		final org.drip.portfolioconstruction.asset.PortfolioMetrics pmOptimal)
		throws java.lang.Exception
	{
		if (null == (_pfOptimal = pfOptimal) || null == (_pmOptimal = pmOptimal))
			throw new java.lang.Exception ("OptimizationOutput Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Optimal Portfolio Metrics
	 * 
	 * @return The Optimal Portfolio Metrics
	 */

	public org.drip.portfolioconstruction.asset.PortfolioMetrics optimalMetrics()
	{
		return _pmOptimal;
	}

	/**
	 * Retrieve the Optimal Portfolio Instance
	 * 
	 * @return The Optimal Portfolio Instance
	 */

	public org.drip.portfolioconstruction.asset.Portfolio optimalPortfolio()
	{
		return _pfOptimal;
	}
}
