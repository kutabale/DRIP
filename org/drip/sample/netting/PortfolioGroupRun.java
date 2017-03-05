
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
 * PortfolioGroupRun demonstrates the Simulation Run of the Netting Group Exposure. The References are:
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

public class PortfolioGroupRun {

	private static final double[] AssetValueRealization (
		final DiffusionEvolver deAssetValue,
		final double dblAssetValueInitial,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumStep)
		throws Exception
	{
		double[] ablAssetValue = new double[iNumStep + 1];
		ablAssetValue[0] = dblAssetValueInitial;

		JumpDiffusionEdge[] aJDE = deAssetValue.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				dblAssetValueInitial,
				0.,
				false
			),
			UnitRandom.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
			dblTimeWidth
		);

		for (int j = 1; j <= iNumStep; ++j)
			ablAssetValue[j] = aJDE[j - 1].finish();

		return ablAssetValue;
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iNumStep = 10;
		double dblTime = 5.;
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblAssetValueInitial = 1.;
		double dblCSADrift = 0.01;
		double dblBankHazardRate = 0.015;
		double dblBankRecoveryRate = 0.40;
		double dblCounterPartyHazardRate = 0.030;
		double dblCounterPartyRecoveryRate = 0.30;

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep + 1];
		NumeraireVertex[] aNV = new NumeraireVertex[iNumStep + 1];
		CollateralGroupVertex[] aCGV1 = new CollateralGroupVertex[iNumStep + 1];
		CollateralGroupVertex[] aCGV2 = new CollateralGroupVertex[iNumStep + 1];
		double dblBankFundingSpread = dblBankHazardRate / (1. - dblBankRecoveryRate);

		JulianDate dtSpot = DateUtil.Today();

		DiffusionEvolver deAssetValue = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblAssetDrift,
				dblAssetVolatility
			)
		);

		double[] adblAssetValuePath1 = AssetValueRealization (
			deAssetValue,
			dblAssetValueInitial,
			dblTime,
			dblTimeWidth,
			iNumStep
		);

		double[] adblAssetValuePath2 = AssetValueRealization (
			deAssetValue,
			dblAssetValueInitial,
			dblTime,
			dblTimeWidth,
			iNumStep
		);

		System.out.println();

		System.out.println ("\t|--------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|                                                       PATH VERTEX EXPOSURES AND NUMERAIRE REALIZATIONS                                                       ||");

		System.out.println ("\t|--------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|    L -> R:                                                                                                                                                   ||");

		System.out.println ("\t|            - Path #1 Gross Exposure                                                                                                                          ||");

		System.out.println ("\t|            - Path #1 Positive Exposure                                                                                                                       ||");

		System.out.println ("\t|            - Path #1 Negative Exposure                                                                                                                       ||");

		System.out.println ("\t|            - Path #2 Gross Exposure                                                                                                                          ||");

		System.out.println ("\t|            - Path #2 Positive Exposure                                                                                                                       ||");

		System.out.println ("\t|            - Path #2 Negative Exposure                                                                                                                       ||");

		System.out.println ("\t|            - Collateral Numeraire                                                                                                                            ||");

		System.out.println ("\t|            - Bank Survival Probability                                                                                                                       ||");

		System.out.println ("\t|            - Bank Recovery Rate                                                                                                                              ||");

		System.out.println ("\t|            - Bank Funding Spread                                                                                                                             ||");

		System.out.println ("\t|            - Counter Party Survival Probability                                                                                                              ||");

		System.out.println ("\t|            - Counter Party Recovery Rate                                                                                                                     ||");

		System.out.println ("\t|--------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		for (int i = 0; i <= iNumStep; ++i) {
			aNV[i] = new NumeraireVertex (
				adtVertex[i] = dtSpot.addMonths (6 * i),
				Math.exp (0.5 * dblCSADrift * i),
				Math.exp (-0.5 * dblBankHazardRate * i),
				dblBankRecoveryRate,
				dblBankFundingSpread,
				Math.exp (-0.5 * dblCounterPartyHazardRate * i),
				dblCounterPartyRecoveryRate
			);

			aCGV1[i] = new CollateralGroupVertex (
				adtVertex[i],
				adblAssetValuePath1[i],
				0.,
				0.
			);

			aCGV2[i] = new CollateralGroupVertex (
				adtVertex[i],
				adblAssetValuePath2[i],
				0.,
				0.
			);

			System.out.println (
				"\t| " + adtVertex[i] + " => " +
				FormatUtil.FormatDouble (aCGV1[i].collateralizedExposure(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aCGV1[i].uncollateralizedExposure(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aCGV1[i].collateralBalance(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aCGV2[i].collateralizedExposure(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aCGV2[i].uncollateralizedExposure(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aCGV2[i].collateralBalance(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].csa(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].bankSurvival(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].bankRecovery(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].bankFundingSpread(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].counterPartySurvival(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].counterPartyRecovery(), 1, 6, 1.) + " ||"
			);
		}

		NumerairePath np = new NumerairePath (aNV);

		NettingGroupPath ngp1 = new NettingGroupPath (
			new CollateralGroupPath[] {new CollateralGroupPath (aCGV1)},
			np
		);

		NettingGroupPath ngp2 = new NettingGroupPath (
			new CollateralGroupPath[] {new CollateralGroupPath (aCGV2)},
			np
		);

		double[] adblPeriodCreditAdjustment1 = ngp1.periodCreditAdjustment();

		double[] adblPeriodDebtAdjustment1 = ngp1.periodDebtAdjustment();

		double[] adblPeriodFundingAdjustment1 = ngp1.periodFundingAdjustment();

		double[] adblPeriodCreditAdjustment2 = ngp2.periodCreditAdjustment();

		double[] adblPeriodDebtAdjustment2 = ngp2.periodDebtAdjustment();

		double[] adblPeriodFundingAdjustment2 = ngp2.periodFundingAdjustment();

		System.out.println ("\t|--------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|                          PERIOD CREDIT, DEBT, AND FUNDING VALUATION ADJUSTMENTS                          ||");

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|    - Forward Period                                                                                      ||");

		System.out.println ("\t|    - Path #1 Period Credit Adjustments                                                                   ||");

		System.out.println ("\t|    - Path #1 Period Debt Adjustments                                                                     ||");

		System.out.println ("\t|    - Path #1 Period Funding Adjustments                                                                  ||");

		System.out.println ("\t|    - Path #2 Period Credit Adjustments                                                                   ||");

		System.out.println ("\t|    - Path #2 Period Debt Adjustments                                                                     ||");

		System.out.println ("\t|    - Path #2 Period Funding Adjustments                                                                  ||");

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------||");

		for (int i = 1; i <= iNumStep; ++i) {
			System.out.println ("\t| [" +
				adtVertex[i - 1] + " -> " + adtVertex[i] + "] => " +
				FormatUtil.FormatDouble (adblPeriodCreditAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodDebtAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingAdjustment1[i - 1], 1, 6, 1.) + " || " +
				FormatUtil.FormatDouble (adblPeriodCreditAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodDebtAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingAdjustment2[i - 1], 1, 6, 1.) + " ||"
			);
		}

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------||");

		System.out.println();

		CounterPartyGroupAggregator cpga = new CounterPartyGroupAggregator (
			new CounterPartyGroupPath[] {
				new CounterPartyGroupPath (new NettingGroupPath[] {ngp1}),
				new CounterPartyGroupPath (new NettingGroupPath[] {ngp2})
			}
		);

		JulianDate[] adtVertexNode = cpga.anchors();

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

		System.out.println ("\t||----------------||");

		System.out.println ("\t|| CVA => " + FormatUtil.FormatDouble (cpga.cva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|| DVA => " + FormatUtil.FormatDouble (cpga.dva(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|| FVA => " + FormatUtil.FormatDouble (cpga.fca(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||----------------||");
	}
}
