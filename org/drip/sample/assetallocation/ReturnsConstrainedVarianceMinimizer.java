
package org.drip.sample.assetallocation;

import org.drip.feed.loader.*;
import org.drip.function.rdtor1descent.LineStepEvolutionControl;
import org.drip.function.rdtor1solver.InteriorPointBarrierControl;
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
 * ReturnsConstrainedVarianceMinimizer demonstrates the Construction of an Optimal Portfolio using the
 *  Variance Minimizing Allocator with Weight Normalization Constraints and Design Returns Constraints.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ReturnsConstrainedVarianceMinimizer {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strSeriesLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\MeanVarianceOptimizer\\FormattedSeries1.csv";

		CSVGrid csvGrid = CSVParser.NamedStringGrid (strSeriesLocation);

		String[] astrVariateHeader = csvGrid.headers();

		double dblDesignReturn = 0.0026;
		double dblAssetLowerBound = 0.05;
		double dblAssetUpperBound = 0.65;
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

		double[][] aadblCovarianceMatrix = ausp.covariance (astrAsset);

		System.out.println ("\n\n\t|------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|                                  CROSS ASSET COVARIANCE MATRIX                                 ||");

		System.out.println ("\t|------------------------------------------------------------------------------------------------||");

		String strHeader = "\t|     |";

		for (int i = 0; i < astrAsset.length; ++i)
			strHeader += "    " + astrAsset[i] + "     |";

		System.out.println (strHeader + "|");

		System.out.println ("\t|------------------------------------------------------------------------------------------------||");

		for (int i = 0; i < astrAsset.length; ++i) {
			String strDump = "\t| " + astrAsset[i] + " ";

			for (int j = 0; j < astrAsset.length; ++j)
				strDump += "|" + FormatUtil.FormatDouble (aadblCovarianceMatrix[i][j], 1, 8, 1.) + " ";

			System.out.println (strDump + "||");
		}

		System.out.println ("\t|------------------------------------------------------------------------------------------------||\n\n");

		System.out.println ("\t|------------------||");

		System.out.println ("\t|   ASSET BOUNDS   ||");

		System.out.println ("\t|------------------||");

		for (int i = 0; i < astrAsset.length; ++i)
			System.out.println (
				"\t| " + astrAsset[i] + " | " +
				FormatUtil.FormatDouble (dblAssetLowerBound, 1, 0, 100.) + "% | " +
				FormatUtil.FormatDouble (dblAssetUpperBound, 2, 0, 100.) + "% ||"
			);

		System.out.println ("\t|------------------||\n\n");

		InteriorPointBarrierControl ipbc = InteriorPointBarrierControl.Standard();

		System.out.println ("\t|--------------------------------------------||");

		System.out.println ("\t|  INTERIOR POINT METHOD BARRIER PARAMETERS  ||");

		System.out.println ("\t|--------------------------------------------||");

		System.out.println ("\t|    Barrier Decay Velocity        : " + 1. / ipbc.decayVelocity());

		System.out.println ("\t|    Barrier Decay Steps           : " + ipbc.numDecaySteps());

		System.out.println ("\t|    Initial Barrier Strength      : " + ipbc.initialStrength());

		System.out.println ("\t|    Barrier Convergence Tolerance : " + ipbc.relativeTolerance());

		System.out.println ("\t|--------------------------------------------||\n\n");

		ConstrainedMeanVarianceOptimizer cmva = new ConstrainedMeanVarianceOptimizer (
			ipbc,
			LineStepEvolutionControl.NocedalWrightStrongWolfe (false)
		);

		BoundedPortfolioConstructionParameters pdp = new BoundedPortfolioConstructionParameters (
			astrAsset,
			CustomRiskUtilitySettings.VarianceMinimizer(),
			new PortfolioEqualityConstraintSettings (
				PortfolioEqualityConstraintSettings.FULLY_INVESTED_CONSTRAINT | PortfolioEqualityConstraintSettings.RETURNS_CONSTRAINT,
				dblDesignReturn
			)
		);

		for (int i = 0; i < astrAsset.length; ++i)
			pdp.addBound (
				astrAsset[i],
				dblAssetLowerBound,
				dblAssetUpperBound
			);

		OptimizationOutput pf = cmva.allocate (
			pdp,
			ausp
		);

		AssetComponent[] aAC = pf.optimalPortfolio().assets();

		System.out.println ("\t|---------------||");

		System.out.println ("\t| ASSET WEIGHTS ||");

		System.out.println ("\t|---------------||");

		for (AssetComponent ac : aAC)
			System.out.println ("\t| " + ac.id() + " | " + FormatUtil.FormatDouble (ac.amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|---------------||\n\n");

		System.out.println ("\t|-----------------------------------------||");

		System.out.println ("\t| Portfolio Notional           : " + FormatUtil.FormatDouble (pf.optimalPortfolio().notional(), 1, 4, 1.) + "  ||");

		System.out.println ("\t| Portfolio Design Return      : " + FormatUtil.FormatDouble (dblDesignReturn, 1, 4, 100.) + "% ||");

		System.out.println ("\t| Portfolio Expected Return    : " + FormatUtil.FormatDouble (pf.optimalMetrics().excessReturnsMean(), 1, 4, 100.) + "% ||");

		System.out.println ("\t| Portfolio Standard Deviation : " + FormatUtil.FormatDouble (pf.optimalMetrics().excessReturnsStandardDeviation(), 1, 4, 100.) + "% ||");

		System.out.println ("\t|-----------------------------------------||\n");
	}
}
