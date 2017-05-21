
package org.drip.sample.netting;

import org.drip.analytics.date.*;
import org.drip.measure.discrete.SequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLogarithmic;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.collateral.HypothecationGroupPath;
import org.drip.xva.collateral.HypothecationGroupVertexRegular;
import org.drip.xva.cpty.*;
import org.drip.xva.numeraire.MarketPath;
import org.drip.xva.numeraire.MarketVertex;
import org.drip.xva.strategy.FundingGroupPathAA2014;
import org.drip.xva.strategy.NettingGroupPathAA2014;

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
			UnitRandomEdge.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
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
		MarketVertex[] aNV = new MarketVertex[iNumStep + 1];
		HypothecationGroupVertexRegular[] aCGV1 = new HypothecationGroupVertexRegular[iNumStep + 1];
		HypothecationGroupVertexRegular[] aCGV2 = new HypothecationGroupVertexRegular[iNumStep + 1];
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
			aNV[i] = MarketVertex.Standard (
				adtVertex[i] = dtSpot.addMonths (6 * i),
				Math.exp (0.5 * dblCSADrift * i),
				Math.exp (-0.5 * dblBankHazardRate * i),
				dblBankRecoveryRate,
				dblBankFundingSpread,
				Math.exp (-0.5 * dblCounterPartyHazardRate * i),
				dblCounterPartyRecoveryRate
			);

			aCGV1[i] = new HypothecationGroupVertexRegular (
				adtVertex[i],
				adblAssetValuePath1[i],
				0.,
				0.
			);

			aCGV2[i] = new HypothecationGroupVertexRegular (
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
				FormatUtil.FormatDouble (aNV[i].overnightPolicyIndex(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].bankSurvival(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].bankRecovery(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].bankFundingSpread(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].counterPartySurvival(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aNV[i].counterPartyRecovery(), 1, 6, 1.) + " ||"
			);
		}

		MarketPath np = new MarketPath (aNV);

		HypothecationGroupPath[] aCGP1 = new HypothecationGroupPath[] {new HypothecationGroupPath (aCGV1)};

		HypothecationGroupPath[] aCGP2 = new HypothecationGroupPath[] {new HypothecationGroupPath (aCGV2)};

		NettingGroupPathAA2014 ngp1 = new NettingGroupPathAA2014 (
			aCGP1,
			np
		);

		FundingGroupPathAA2014 fgp1 = new FundingGroupPathAA2014 (
			aCGP1,
			np
		);

		NettingGroupPathAA2014 ngp2 = new NettingGroupPathAA2014 (
			aCGP2,
			np
		);

		FundingGroupPathAA2014 fgp2 = new FundingGroupPathAA2014 (
			aCGP2,
			np
		);

		double[] adblPeriodUnilateralCreditAdjustment1 = ngp1.periodUnilateralCreditAdjustment();

		double[] adblPeriodBilateralCreditAdjustment1 = ngp1.periodBilateralCreditAdjustment();

		double[] adblPeriodCreditAdjustment1 = ngp1.periodCreditAdjustment();

		double[] adblPeriodContraLiabilityCreditAdjustment1 = ngp1.periodContraLiabilityCreditAdjustment();

		double[] adblPeriodUnilateralCreditAdjustment2 = ngp2.periodUnilateralCreditAdjustment();

		double[] adblPeriodBilateralCreditAdjustment2 = ngp2.periodBilateralCreditAdjustment();

		double[] adblPeriodCreditAdjustment2 = ngp2.periodCreditAdjustment();

		double[] adblPeriodContraLiabilityCreditAdjustment2 = ngp2.periodContraLiabilityCreditAdjustment();

		System.out.println ("\t|--------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|               PERIOD UNILATERAL CREDIT, BILATERAL CREDIT, CREDIT, & CONTRA LIABILITY CREDIT VALUATION ADJUSTMENTS               ||");

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|    - Forward Period                                                                                                             ||");

		System.out.println ("\t|    - Path #1 Period Unilateral Credit Adjustments                                                                               ||");

		System.out.println ("\t|    - Path #1 Period Bilateral Credit Adjustments                                                                                ||");

		System.out.println ("\t|    - Path #1 Period Credit Adjustments                                                                                          ||");

		System.out.println ("\t|    - Path #1 Period Contra-Liability Credit Adjustments                                                                         ||");

		System.out.println ("\t|    - Path #2 Period Unilateral Credit Adjustments                                                                               ||");

		System.out.println ("\t|    - Path #2 Period Bilateral Credit Adjustments                                                                                ||");

		System.out.println ("\t|    - Path #2 Period Credit Adjustments                                                                                          ||");

		System.out.println ("\t|    - Path #2 Period Contra-Liability Credit Adjustments                                                                         ||");

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------||");

		for (int i = 1; i <= iNumStep; ++i) {
			System.out.println ("\t| [" +
				adtVertex[i - 1] + " -> " + adtVertex[i] + "] => " +
				FormatUtil.FormatDouble (adblPeriodUnilateralCreditAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodBilateralCreditAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodCreditAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodContraLiabilityCreditAdjustment1[i - 1], 1, 6, 1.) + " ||| " +
				FormatUtil.FormatDouble (adblPeriodUnilateralCreditAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodBilateralCreditAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodCreditAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodContraLiabilityCreditAdjustment2[i - 1], 1, 6, 1.) + " ||"
			);
		}

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();

		double[] adblPeriodDebtAdjustment1 = ngp1.periodDebtAdjustment();

		double[] adblPeriodFundingValueAdjustment1 = fgp1.periodFundingValueAdjustment();

		double[] adblPeriodFundingDebtAdjustment1 = fgp1.periodFundingDebtAdjustment();

		double[] adblPeriodFundingCostAdjustment1 = fgp1.periodFundingCostAdjustment();

		double[] adblPeriodFundingBenefitAdjustment1 = fgp1.periodFundingBenefitAdjustment();

		double[] adblPeriodSymmetricFundingValueAdjustment1 = fgp1.periodSymmetricFundingValueAdjustment();

		double[] adblPeriodDebtAdjustment2 = ngp2.periodDebtAdjustment();

		double[] adblPeriodFundingValueAdjustment2 = fgp2.periodFundingValueAdjustment();

		double[] adblPeriodFundingDebtAdjustment2 = fgp2.periodFundingDebtAdjustment();

		double[] adblPeriodFundingCostAdjustment2 = fgp2.periodFundingCostAdjustment();

		double[] adblPeriodFundingBenefitAdjustment2 = fgp2.periodFundingBenefitAdjustment();

		double[] adblPeriodSymmetricFundingValueAdjustment2 = fgp2.periodSymmetricFundingValueAdjustment();

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|                            DEBT VALUATION, FUNDING VALUATION, FUNDING DEBT, FUNDING COST, FUNDING BENEFIT, & SYMMETRIC FUNDING VALUATION ADJUSTMENTS                             ||");

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t|  L -> R:                                                                                                                                                                         ||");

		System.out.println ("\t|          - Path #1 Debt Valuation Adjustment                                                                                                                                     ||");

		System.out.println ("\t|          - Path #1 Funding Valuation Adjustment                                                                                                                                  ||");

		System.out.println ("\t|          - Path #1 Funding Debt Adjustment                                                                                                                                       ||");

		System.out.println ("\t|          - Path #1 Funding Cost Adjustment                                                                                                                                       ||");

		System.out.println ("\t|          - Path #1 Funding Benefit Adjustment                                                                                                                                    ||");

		System.out.println ("\t|          - Path #1 Symmatric Funding Valuation Adjustment                                                                                                                        ||");

		System.out.println ("\t|          - Path #2 Debt Valuation Adjustment                                                                                                                                     ||");

		System.out.println ("\t|          - Path #2 Funding Valuation Adjustment                                                                                                                                  ||");

		System.out.println ("\t|          - Path #2 Funding Debt Adjustment                                                                                                                                       ||");

		System.out.println ("\t|          - Path #2 Funding Cost Adjustment                                                                                                                                       ||");

		System.out.println ("\t|          - Path #2 Funding Benefit Adjustment                                                                                                                                    ||");

		System.out.println ("\t|          - Path #2 Symmatric Funding Valuation Adjustment                                                                                                                        ||");

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		for (int i = 1; i <= iNumStep; ++i) {
			System.out.println ("\t| [" +
				adtVertex[i - 1] + " -> " + adtVertex[i] + "] => " +
				FormatUtil.FormatDouble (adblPeriodDebtAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingValueAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingDebtAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingCostAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingBenefitAdjustment1[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodSymmetricFundingValueAdjustment1[i - 1], 1, 6, 1.) + " || " +
				FormatUtil.FormatDouble (adblPeriodDebtAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingValueAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingDebtAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingCostAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodFundingBenefitAdjustment2[i - 1], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblPeriodSymmetricFundingValueAdjustment2[i - 1], 1, 6, 1.) + " || "
			);
		}

		System.out.println ("\t|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();

		ExposureAdjustmentAggregator cpga = new ExposureAdjustmentAggregator (
			new MonoPathExposureAdjustment[] {
				new MonoPathExposureAdjustment (
					new NettingGroupPathAA2014[] {ngp1},
					new FundingGroupPathAA2014[] {fgp1}
				),
				new MonoPathExposureAdjustment (
					new NettingGroupPathAA2014[] {ngp2},
					new FundingGroupPathAA2014[] {fgp2}
				)
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

		System.out.println ("\t||-------------------||");

		System.out.println ("\t||  UCVA  => " + FormatUtil.FormatDouble (cpga.ucva().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t|| FTDCVA => " + FormatUtil.FormatDouble (cpga.ftdcva().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  CVA   => " + FormatUtil.FormatDouble (cpga.cva().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  CVACL => " + FormatUtil.FormatDouble (cpga.cvacl().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  DVA   => " + FormatUtil.FormatDouble (cpga.dva().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FVA   => " + FormatUtil.FormatDouble (cpga.fva().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FDA   => " + FormatUtil.FormatDouble (cpga.fda().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FCA   => " + FormatUtil.FormatDouble (cpga.fca().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  FBA   => " + FormatUtil.FormatDouble (cpga.fba().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||  SFVA  => " + FormatUtil.FormatDouble (cpga.sfva().amount(), 2, 2, 100.) + "% ||");

		System.out.println ("\t||-------------------||");
	}
}
