
package org.drip.sample.xva;

import org.drip.analytics.date.*;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
import org.drip.measure.discrete.SequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLinear;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.numeraire.MarketPath;
import org.drip.xva.numeraire.MarketVertex;
import org.drip.xva.settings.*;
import org.drip.xva.strategy.AlbaneseAndersenFunding;
import org.drip.xva.strategy.AlbaneseAndersenNetting;
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
 * CollateralizedCollateralGroup illustrates the Sample Run of a Single Partially Collateralized Collateral
 *  Group under Non-Zero Bank/Counter Party Threshold with several Fix-Float Swaps. The References are:
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

public class CollateralizedCollateralGroup {

	private static final double[] ATMSwapRateOffsetRealization (
		final DiffusionEvolver deATMSwapRateOffset,
		final double dblATMSwapRateOffsetInitial,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumStep)
		throws Exception
	{
		double[] adblATMSwapRateOffset = new double[iNumStep + 1];
		adblATMSwapRateOffset[0] = dblATMSwapRateOffsetInitial;

		JumpDiffusionEdge[] aJDE = deATMSwapRateOffset.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				dblATMSwapRateOffsetInitial,
				0.,
				false
			),
			UnitRandom.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
			dblTimeWidth
		);

		for (int j = 1; j <= iNumStep; ++j)
			adblATMSwapRateOffset[j] = aJDE[j - 1].finish();

		return adblATMSwapRateOffset;
	}

	private static final double[] SwapPortfolioValueRealization (
		final DiffusionEvolver deATMSwapRate,
		final double dblATMSwapRateStart,
		final int iNumStep,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumSwap)
		throws Exception
	{
		double[] adblSwapPortfolioValueRealization = new double[iNumStep + 1];

		for (int i = 0; i < iNumStep; ++i)
			adblSwapPortfolioValueRealization[i] = 0.;

		for (int i = 0; i < iNumSwap; ++i) {
			double[] adblATMSwapRateOffsetRealization = ATMSwapRateOffsetRealization (
				deATMSwapRate,
				dblATMSwapRateStart,
				dblTime,
				dblTimeWidth,
				iNumStep
			);

			for (int j = 0; j <= iNumStep; ++j)
				adblSwapPortfolioValueRealization[j] += dblTimeWidth * (iNumStep - j) * adblATMSwapRateOffsetRealization[j];
		}

		return adblSwapPortfolioValueRealization;
	}

	private static final double[][] SwapPortfolioValueRealization (
		final DiffusionEvolver deATMSwapRate,
		final double dblSwapPortfolioValueStart,
		final int iNumStep,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumSwap,
		final int iNumSimulation)
		throws Exception
	{
		double[][] aadblSwapPortfolioValueRealization = new double[iNumSimulation][];

		for (int i = 0; i < iNumSimulation; ++i)
			aadblSwapPortfolioValueRealization[i] = SwapPortfolioValueRealization (
				deATMSwapRate,
				dblSwapPortfolioValueStart,
				iNumStep,
				dblTime,
				dblTimeWidth,
				iNumSwap
			);

		return aadblSwapPortfolioValueRealization;
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iNumStep = 10;
		int iNumSwap = 10;
		double dblTime = 5.;
		int iNumPath = 10000;
		double dblATMSwapRateStart = 0.;
		double dblATMSwapRateDrift = 0.0;
		double dblATMSwapRateVolatility = 0.25;
		double dblCSADrift = 0.01;
		double dblBankHazardRate = 0.015;
		double dblBankRecoveryRate = 0.40;
		double dblCounterPartyHazardRate = 0.030;
		double dblCounterPartyRecoveryRate = 0.30;
		double dblBankThreshold = -0.1;
		double dblCounterPartyThreshold = 0.1;

		JulianDate dtSpot = DateUtil.Today();

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep + 1];
		MarketVertex[] aNV = new MarketVertex[iNumStep + 1];
		CounterPartyGroupPath[] aCPGP = new CounterPartyGroupPath[iNumPath];
		double dblBankFundingSpread = dblBankHazardRate / (1. - dblBankRecoveryRate);

		CollateralGroupSpecification cgs = CollateralGroupSpecification.FixedThreshold (
			"FIXEDTHRESHOLD",
			dblCounterPartyThreshold,
			dblBankThreshold
		);

		CounterPartyGroupSpecification cpgs = CounterPartyGroupSpecification.Standard ("CPGROUP");

		double[][] aadblSwapPortfolioValueRealization = SwapPortfolioValueRealization (
			new DiffusionEvolver (
				DiffusionEvaluatorLinear.Standard (
					dblATMSwapRateDrift,
					dblATMSwapRateVolatility
				)
			),
			dblATMSwapRateStart,
			iNumStep,
			dblTime,
			dblTimeWidth,
			iNumSwap,
			iNumPath
		);

		for (int i = 0; i <= iNumStep; ++i)
			aNV[i] = MarketVertex.Standard (
				adtVertex[i] = dtSpot.addMonths (6 * i),
				Math.exp (0.5 * dblCSADrift * i),
				Math.exp (-0.5 * dblBankHazardRate * i),
				dblBankRecoveryRate,
				dblBankFundingSpread,
				Math.exp (-0.5 * dblCounterPartyHazardRate * i),
				dblCounterPartyRecoveryRate
			);

		MarketPath np = new MarketPath (aNV);

		for (int i = 0; i < iNumPath; ++i) {
			JulianDate dtStart = dtSpot;
			double dblValueStart = dblTime * dblATMSwapRateStart;
			CollateralGroupVertex[] aCGV = new CollateralGroupVertex[iNumStep + 1];

			for (int j = 0; j <= iNumStep; ++j) {
				double dblCollateralBalance = 0.;
				JulianDate dtEnd = adtVertex[j];
				double dblValueEnd = aadblSwapPortfolioValueRealization[i][j];

				if (0 != j) {
					CollateralAmountEstimator cae = new CollateralAmountEstimator (
						cgs,
						cpgs,
						new BrokenDateInterpolatorLinearT (
							dtStart.julian(),
							dtEnd.julian(),
							dblValueStart,
							dblValueEnd
						),
						Double.NaN
					);

					dblCollateralBalance = cae.postingRequirement (dtEnd);
				}

				aCGV[j] = new CollateralGroupVertex (
					adtVertex[j],
					aadblSwapPortfolioValueRealization[i][j],
					0.,
					dblCollateralBalance
				);

				dtStart = dtEnd;
				dblValueStart = dblValueEnd;
			}

			CollateralGroupPath[] aCGP = new CollateralGroupPath[] {new CollateralGroupPath (aCGV)};

			aCPGP[i] = new CounterPartyGroupPath (
				new AlbaneseAndersenNetting[] {
					new AlbaneseAndersenNetting (
						aCGP,
						np
					)
				},
				new AlbaneseAndersenFunding[] {
					new AlbaneseAndersenFunding (
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
