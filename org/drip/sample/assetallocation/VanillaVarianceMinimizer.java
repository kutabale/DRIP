
package org.drip.sample.assetallocation;

import org.drip.feed.loader.*;
import org.drip.measure.statistics.MultivariateMoments;
import org.drip.portfolioconstruction.allocator.*;
import org.drip.portfolioconstruction.asset.*;
import org.drip.portfolioconstruction.params.AssetUniverseStatisticalProperties;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * VanillaVarianceMinimizer demonstrates the Construction of an Optimal Portfolio using the Variance
 *  Minimizing Allocator with only the Fully Invested Constraint.
 *
 * @author Lakshmi Krishnamurthy
 */

public class VanillaVarianceMinimizer {

	static final void RiskTolerancePortfolio (
		final String[] astrAsset,
		final AssetUniverseStatisticalProperties ausp,
		final double dblRiskTolerance)
		throws Exception
	{
		OptimizationOutput opf = new QuadraticMeanVarianceOptimizer().allocate (
			new PortfolioConstructionParameters (
				astrAsset,
				CustomRiskUtilitySettings.VarianceMinimizer(),
				new PortfolioEqualityConstraintSettings (
					PortfolioEqualityConstraintSettings.FULLY_INVESTED_CONSTRAINT,
					Double.NaN
				)
			),
			ausp
		);

		AssetComponent[] aAC = opf.optimalPortfolio().assets();

		System.out.println ("\n\n\t|---------------||");

		for (AssetComponent ac : aAC)
			System.out.println ("\t| " + ac.id() + " | " + FormatUtil.FormatDouble (ac.amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|---------------||");

		System.out.println ("\t|---------------------------------------||");

		System.out.println ("\t| Portfolio Notional           : " + FormatUtil.FormatDouble (opf.optimalPortfolio().notional(), 1, 3, 1.) + " ||");

		System.out.println ("\t| Portfolio Expected Return    : " + FormatUtil.FormatDouble (opf.optimalMetrics().excessReturnsMean(), 1, 2, 100.) + "% ||");

		System.out.println ("\t| Portfolio Standard Deviation : " + FormatUtil.FormatDouble (opf.optimalMetrics().excessReturnsStandardDeviation(), 1, 2, 100.) + "% ||");

		System.out.println ("\t|---------------------------------------||\n");
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strSeriesLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\MeanVarianceOptimizer\\FormattedSeries1.csv";

		CSVGrid csvGrid = CSVParser.NamedStringGrid (strSeriesLocation);

		String[] astrVariateHeader = csvGrid.headers();

		String[] astrAsset = new String[astrVariateHeader.length - 1];
		double[][] aadblVariateSample = new double[astrVariateHeader.length - 1][];

		for (int i = 0; i < astrAsset.length; ++i) {
			astrAsset[i] = astrVariateHeader[i + 1];

			aadblVariateSample[i] = csvGrid.doubleArrayAtColumn (i + 1);
		}

		AssetUniverseStatisticalProperties ausp = AssetUniverseStatisticalProperties.FromMultivariateMetrics (
			MultivariateMoments.Standard (
				astrAsset,
				aadblVariateSample
			)
		);

		RiskTolerancePortfolio (
			astrAsset,
			ausp,
			0.
		);
	}
}
