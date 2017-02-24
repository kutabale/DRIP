
package org.drip.sample.netting;

import org.drip.analytics.date.*;
import org.drip.measure.discretemarginal.SequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLogarithmic;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.collateral.GroupTrajectoryVertexNumeraire;
import org.drip.xva.netting.*;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * PortfolioPathAggregationDeterministic generates an Aggregation of the Portfolio Paths evolved using
 * 	Deterministic Market Parameters. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 *  
 *  - Li, B., and Y. Tang (2007): Quantitative Analysis, Derivatives Modeling, and Trading Strategies in the
 *  	Presence of Counter-party Credit Risk for the Fixed Income Market, World Scientific Publishing,
 *  	Singapore.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PortfolioPathAggregationDeterministic {

	private static final JumpDiffusionEdge[][] PortfolioRealization (
		final DiffusionEvolver mePortfolio,
		final double dblInitialAssetValue,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumStep,
		final int iNumSimulation)
		throws Exception
	{
		JumpDiffusionEdge[][] aaJDE = new JumpDiffusionEdge[iNumSimulation][];

		for (int i = 0; i < iNumSimulation; ++i)
			aaJDE[i] = mePortfolio.incrementSequence (
				new JumpDiffusionVertex (
					dblTime,
					dblInitialAssetValue,
					0.,
					false
				),
				UnitRandom.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
				dblTimeWidth
			);

		return aaJDE;
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iNumStep = 10;
		double dblTime = 5.;
		int iNumSimulation = 50000;
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblInitialAssetValue = 1.;
		double dblCollateralDrift = 0.01;
		double dblBankHazardRate = 0.015;
		double dblBankRecoveryRate = 0.40;
		double dblCounterPartyHazardRate = 0.030;
		double dblCounterPartyRecoveryRate = 0.30;

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep];
		double dblBankFundingSpread = dblBankHazardRate / (1. - dblBankRecoveryRate);
		GroupTrajectoryVertexNumeraire[] aGTVN = new GroupTrajectoryVertexNumeraire[iNumStep];

		JulianDate dtSpot = DateUtil.Today();

		DiffusionEvolver mePortfolio = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblAssetDrift,
				dblAssetVolatility
			)
		);

		JumpDiffusionEdge[][] aaJDE = PortfolioRealization (
			mePortfolio,
			dblInitialAssetValue,
			dblTime,
			dblTimeWidth,
			iNumStep,
			iNumSimulation
		);

		for (int i = 0; i < iNumStep; ++i) {
			adtVertex[i] = dtSpot.addMonths (6 * i + 6);

			aGTVN[i] = new GroupTrajectoryVertexNumeraire (
				Math.exp (0.5 * dblCollateralDrift * (i + 1)),
				Math.exp (-0.5 * dblBankHazardRate * (i + 1)),
				dblBankRecoveryRate,
				dblBankFundingSpread,
				Math.exp (-0.5 * dblCounterPartyHazardRate * (i + 1)),
				dblCounterPartyRecoveryRate
			);
		}

		GroupTrajectoryPathAggregator gtpa = GroupTrajectoryPathAggregator.Standard (
			adtVertex,
			aaJDE,
			aGTVN
		);

		JulianDate[] adtVertexNode = gtpa.vertexes();

		System.out.println();

		System.out.println ("\t|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|");

		String strDump = "\t|         DATE         =>" ;

		for (int i = 0; i < adtVertexNode.length; ++i)
			strDump = strDump + " " + adtVertexNode[i] + " |";

		System.out.println (strDump);

		System.out.println ("\t|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|");

		double[] adblEE = gtpa.collateralizedExposure();

		strDump = "\t|       EXPOSURE       =>   " + FormatUtil.FormatDouble (dblInitialAssetValue, 1, 4, 1.) + "   |";

		for (int j = 0; j < adblEE.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblEE[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblEPE = gtpa.collateralizedPositiveExposure();

		strDump = "\t|  POSITIVE EXPOSURE   =>   " + FormatUtil.FormatDouble (dblInitialAssetValue, 1, 4, 1.) + "   |";

		for (int j = 0; j < adblEPE.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblEPE[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblENE = gtpa.collateralizedNegativeExposure();

		strDump = "\t|  NEGATIVE EXPOSURE   =>   " + FormatUtil.FormatDouble (0., 1, 4, 1.) + "   |";

		for (int j = 0; j < adblENE.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblENE[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblEEPV = gtpa.collateralizedExposurePV();

		strDump = "\t|      EXPOSURE PV     =>   " + FormatUtil.FormatDouble (dblInitialAssetValue, 1, 4, 1.) + "   |";

		for (int j = 0; j < adblEEPV.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblEEPV[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblEPEPV = gtpa.collateralizedPositiveExposurePV();

		strDump = "\t| POSITIVE EXPOSURE PV =>   " + FormatUtil.FormatDouble (dblInitialAssetValue, 1, 4, 1.) + "   |";

		for (int j = 0; j < adblEPEPV.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblEPEPV[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblENEPV = gtpa.collateralizedNegativeExposurePV();

		strDump = "\t| NEGATIVE EXPOSURE PV =>   " + FormatUtil.FormatDouble (0., 1, 4, 1.) + "   |";

		for (int j = 0; j < adblENEPV.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblENEPV[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		System.out.println ("\t|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|");

		System.out.println();

		System.out.println ("\t||----------------||");

		System.out.println ("\t|| CVA => " + FormatUtil.FormatDouble (gtpa.cva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|| DVA => " + FormatUtil.FormatDouble (gtpa.dva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|| FVA => " + FormatUtil.FormatDouble (gtpa.fca(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||----------------||");

		System.out.println();
	}
}
