
package org.drip.sample.netting;

import org.drip.analytics.date.*;
import org.drip.measure.discrete.SequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLogarithmic;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.trajectory.*;

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
 * PortfolioGroupRun demonstrates a Set of Netting Group Exposure Simulations. The References are:
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

public class PortfolioGroupSimulation {

	private static final double[][] CollateralPortfolioValueRealization (
		final DiffusionEvolver deCollateralPortfolioValue,
		final double dblCollateralPortfolioValueInitial,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumStep,
		final int iNumPath)
		throws Exception
	{
		double[][] aablCollateralPortfolioValue = new double[iNumPath][iNumStep + 1];

		for (int i = 0; i < iNumPath; ++i) {
			JumpDiffusionEdge[] aJDE = deCollateralPortfolioValue.incrementSequence (
				new JumpDiffusionVertex (
					dblTime,
					dblCollateralPortfolioValueInitial,
					0.,
					false
				),
				UnitRandom.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
				dblTimeWidth
			);

			aablCollateralPortfolioValue[i][0] = dblCollateralPortfolioValueInitial;

			for (int j = 1; j <= iNumStep; ++j)
				aablCollateralPortfolioValue[i][j] = aJDE[j - 1].finish();
		}

		return aablCollateralPortfolioValue;
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iNumStep = 10;
		double dblTime = 5.;
		int iNumPath = 10000;
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblCollateralPortfolioValueInitial = 1.;
		double dblCSADrift = 0.01;
		double dblBankHazardRate = 0.015;
		double dblBankRecoveryRate = 0.40;
		double dblCounterPartyHazardRate = 0.030;
		double dblCounterPartyRecoveryRate = 0.30;

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep + 1];
		NumeraireVertex[] aNV = new NumeraireVertex[iNumStep + 1];
		CounterPartyGroupPath[] aCPGP = new CounterPartyGroupPath[iNumPath];
		double dblBankFundingSpread = dblBankHazardRate / (1. - dblBankRecoveryRate);

		JulianDate dtSpot = DateUtil.Today();

		DiffusionEvolver deCollateralPortfolioValue = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblAssetDrift,
				dblAssetVolatility
			)
		);

		double[][] aadblCollateralPortfolioValue = CollateralPortfolioValueRealization (
			deCollateralPortfolioValue,
			dblCollateralPortfolioValueInitial,
			dblTime,
			dblTimeWidth,
			iNumStep,
			iNumPath
		);

		for (int i = 0; i <= iNumStep; ++i)
			aNV[i] = NumeraireVertex.Standard (
				adtVertex[i] = dtSpot.addMonths (6 * i),
				Math.exp (0.5 * dblCSADrift * i),
				Math.exp (-0.5 * dblBankHazardRate * i),
				dblBankRecoveryRate,
				dblBankFundingSpread,
				Math.exp (-0.5 * dblCounterPartyHazardRate * i),
				dblCounterPartyRecoveryRate
			);

		NumerairePath np = new NumerairePath (aNV);

		for (int i = 0; i < iNumPath; ++i) {
			CollateralGroupVertex[] aCGV = new CollateralGroupVertex[iNumStep + 1];

			for (int j = 0; j <= iNumStep; ++j) {
				aCGV[j] = new CollateralGroupVertex (
					adtVertex[j],
					aadblCollateralPortfolioValue[i][j],
					0.,
					0.
				);
			}

			CollateralGroupPath[] aCGP = new CollateralGroupPath[] {new CollateralGroupPath (aCGV)};

			aCPGP[i] = new CounterPartyGroupPath (
				new NettingGroupPath[] {
					new NettingGroupPath (
						aCGP,
						np
					)
				},
				new FundingGroupPath[] {
					new FundingGroupPath (
						aCGP,
						np
					)
				}
			);
		}

		CounterPartyGroupAggregator cpga = new CounterPartyGroupAggregator (aCPGP);

		JulianDate[] adtVertexNode = cpga.anchors();

		System.out.println();

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|");

		String strDump = "\t|         DATE         =>" ;

		for (int i = 0; i < adtVertexNode.length; ++i)
			strDump = strDump + " " + adtVertexNode[i] + " |";

		System.out.println (strDump);

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|");

		double[] adblExposure = cpga.collateralizedExposure();

		strDump = "\t|       EXPOSURE       =>";

		for (int j = 0; j < adblExposure.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblExposure[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblPositiveExposure = cpga.collateralizedPositiveExposure();

		strDump = "\t|  POSITIVE EXPOSURE   =>";

		for (int j = 0; j < adblPositiveExposure.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblPositiveExposure[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblNegativeExposure = cpga.collateralizedNegativeExposure();

		strDump = "\t|  NEGATIVE EXPOSURE   =>";

		for (int j = 0; j < adblNegativeExposure.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblNegativeExposure[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblExposurePV = cpga.collateralizedExposurePV();

		strDump = "\t|      EXPOSURE PV     =>";

		for (int j = 0; j < adblExposurePV.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblExposurePV[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblPositiveExposurePV = cpga.collateralizedPositiveExposurePV();

		strDump = "\t| POSITIVE EXPOSURE PV =>";

		for (int j = 0; j < adblPositiveExposurePV.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblPositiveExposurePV[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		double[] adblNegativeExposurePV = cpga.collateralizedNegativeExposurePV();

		strDump = "\t| NEGATIVE EXPOSURE PV =>";

		for (int j = 0; j < adblNegativeExposurePV.length; ++j)
			strDump = strDump + "   " + FormatUtil.FormatDouble (adblNegativeExposurePV[j], 1, 4, 1.) + "   |";

		System.out.println (strDump);

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|");

		System.out.println();

		System.out.println ("\t||-------------------||");

		System.out.println ("\t||  UCVA  => " + FormatUtil.FormatDouble (cpga.ucva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|| FTDCVA => " + FormatUtil.FormatDouble (cpga.ftdcva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  CVA   => " + FormatUtil.FormatDouble (cpga.cva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  CVACL => " + FormatUtil.FormatDouble (cpga.cvacl(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  DVA   => " + FormatUtil.FormatDouble (cpga.dva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FVA   => " + FormatUtil.FormatDouble (cpga.fva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FDA   => " + FormatUtil.FormatDouble (cpga.fda(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FCA   => " + FormatUtil.FormatDouble (cpga.fca(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FBA   => " + FormatUtil.FormatDouble (cpga.fba(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  SFVA  => " + FormatUtil.FormatDouble (cpga.sfva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||-------------------||");
	}
}
