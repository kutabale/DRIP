
package org.drip.sample.xva;

import org.drip.analytics.date.*;
import org.drip.measure.discrete.SequenceGenerator;
import org.drip.measure.dynamics.*;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.quant.linearalgebra.Matrix;
import org.drip.service.env.EnvManager;
import org.drip.xva.numeraire.MarketPath;
import org.drip.xva.numeraire.MarketVertex;
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
 * UncollateralizedCollateralGroupCorrelated illustrates the Sample Run of a Single Uncollateralized
 *  Collateral Group with several Fix-Float Swaps, and with built in Factor Correlations across the
 *  Numeraires. The References are:
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

public class UncollateralizedCollateralGroupCorrelated {

	private static final double[] NumeraireValueRealization (
		final DiffusionEvolver deNumeraireValue,
		final double dblNumeraireValueInitial,
		final double dblTime,
		final double dblTimeWidth,
		final double[] adblRandom,
		final int iNumStep)
		throws Exception
	{
		double[] adblNumeraireValue = new double[iNumStep + 1];
		adblNumeraireValue[0] = dblNumeraireValueInitial;

		JumpDiffusionEdge[] aJDE = deNumeraireValue.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				dblNumeraireValueInitial,
				0.,
				false
			),
			UnitRandom.Diffusion (adblRandom),
			dblTimeWidth
		);

		for (int j = 1; j <= iNumStep; ++j)
			adblNumeraireValue[j] = aJDE[j - 1].finish();

		return adblNumeraireValue;
	}

	private static final double[] ATMSwapRateOffsetRealization (
		final DiffusionEvolver deATMSwapRateOffset,
		final double dblATMSwapRateOffsetInitial,
		final double[] adblRandom,
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
			UnitRandom.Diffusion (adblRandom),
			dblTimeWidth
		);

		for (int j = 1; j <= iNumStep; ++j)
			adblATMSwapRateOffset[j] = aJDE[j - 1].finish();

		return adblATMSwapRateOffset;
	}

	private static final double[] SwapPortfolioValueRealization (
		final DiffusionEvolver deATMSwapRate,
		final double dblATMSwapRateStart,
		final double[] adblRandom,
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
				adblRandom,
				dblTime,
				dblTimeWidth,
				iNumStep
			);

			for (int j = 0; j <= iNumStep; ++j)
				adblSwapPortfolioValueRealization[j] += dblTimeWidth * (iNumStep - j) * adblATMSwapRateOffsetRealization[j];
		}

		return adblSwapPortfolioValueRealization;
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
		double dblATMSwapRateOffsetDrift = 0.0;
		double dblATMSwapRateOffsetVolatility = 0.25;
		double dblATMSwapRateOffsetStart = 0.;
		double dblCSADrift = 0.01;
		double dblCSAVolatility = 0.05;
		double dblCSAInitial = 1.;
		double dblBankHazardRateDrift = 0.002;
		double dblBankHazardRateVolatility = 0.20;
		double dblBankHazardRateInitial = 0.015;
		double dblBankRecoveryRateDrift = 0.002;
		double dblBankRecoveryRateVolatility = 0.02;
		double dblBankRecoveryRateInitial = 0.40;
		double dblCounterPartyHazardRateDrift = 0.002;
		double dblCounterPartyHazardRateVolatility = 0.30;
		double dblCounterPartyHazardRateInitial = 0.030;
		double dblCounterPartyRecoveryRateDrift = 0.002;
		double dblCounterPartyRecoveryRateVolatility = 0.02;
		double dblCounterPartyRecoveryRateInitial = 0.30;
		double dblBankFundingSpreadDrift = 0.00002;
		double dblBankFundingSpreadVolatility = 0.002;

		double[][] aadblCorrelation = new double[][] {
			{1.00, 0.03,  0.07,  0.04,  0.05,  0.08,  0.00},  // PORTFOLIO
			{0.03, 1.00,  0.26,  0.33,  0.21,  0.35,  0.13},  // CSA
			{0.07, 0.26,  1.00,  0.45, -0.17,  0.07,  0.77},  // BANK HAZARD
			{0.04, 0.33,  0.45,  1.00, -0.22, -0.54,  0.58},  // COUNTER PARTY HAZARD
			{0.05, 0.21, -0.17, -0.22,  1.00,  0.47, -0.23},  // BANK RECOVERY
			{0.08, 0.35,  0.07, -0.54,  0.47,  1.00,  0.01},  // COUNTER PARTY RECOVERY
			{0.00, 0.13,  0.77,  0.58, -0.23,  0.01,  1.00}   // BANK FUNDING SPREAD
		};

		JulianDate dtSpot = DateUtil.Today();

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep + 1];
		CounterPartyGroupPath[] aCPGP = new CounterPartyGroupPath[iNumPath];
		double[][] aadblPortfolioValue = new double[iNumPath][iNumStep + 1];
		double[][] aadblCollateralBalance = new double[iNumPath][iNumStep + 1];
		double dblBankFundingSpreadInitial = dblBankHazardRateInitial / (1. - dblBankRecoveryRateInitial);

		DiffusionEvolver deATMSwapRateOffset = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				dblATMSwapRateOffsetDrift,
				dblATMSwapRateOffsetVolatility
			)
		);

		DiffusionEvolver deCSA = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblCSADrift,
				dblCSAVolatility
			)
		);

		DiffusionEvolver deBankHazardRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankHazardRateDrift,
				dblBankHazardRateVolatility
			)
		);

		DiffusionEvolver deCounterPartyHazardRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblCounterPartyHazardRateDrift,
				dblCounterPartyHazardRateVolatility
			)
		);

		DiffusionEvolver deBankRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankRecoveryRateDrift,
				dblBankRecoveryRateVolatility
			)
		);

		DiffusionEvolver deCounterPartyRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblCounterPartyRecoveryRateDrift,
				dblCounterPartyRecoveryRateVolatility
			)
		);

		DiffusionEvolver deBankFundingSpread = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				dblBankFundingSpreadDrift,
				dblBankFundingSpreadVolatility
			)
		);

		for (int i = 0; i < iNumPath; ++i) {
			double[][] aadblNumeraire = Matrix.Transpose (
				SequenceGenerator.GaussianJoint (
					iNumStep,
					aadblCorrelation
				)
			);

			aadblPortfolioValue[i] = SwapPortfolioValueRealization (
				deATMSwapRateOffset,
				dblATMSwapRateOffsetStart,
				aadblNumeraire[0],
				iNumStep,
				dblTime,
				dblTimeWidth,
				iNumSwap
			);

			double[] adblCSA = NumeraireValueRealization (
				deCSA,
				dblCSAInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[1],
				iNumStep
			);

			double[] adblBankHazardRate = NumeraireValueRealization (
				deBankHazardRate,
				dblBankHazardRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[2],
				iNumStep
			);

			double[] adblCounterPartyHazardRate = NumeraireValueRealization (
				deCounterPartyHazardRate,
				dblCounterPartyHazardRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[3],
				iNumStep
			);

			double[] adblBankRecoveryRate = NumeraireValueRealization (
				deBankRecoveryRate,
				dblBankRecoveryRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[4],
				iNumStep
			);

			double[] adblCounterPartyRecoveryRate = NumeraireValueRealization (
				deCounterPartyRecoveryRate,
				dblCounterPartyRecoveryRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[5],
				iNumStep
			);

			double[] adblBankFundingSpread = NumeraireValueRealization (
				deBankFundingSpread,
				dblBankFundingSpreadInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[6],
				iNumStep
			);

			MarketVertex[] aNV = new MarketVertex [iNumStep + 1];
			CollateralGroupVertex[] aCGV = new CollateralGroupVertex[iNumStep + 1];

			for (int j = 0; j <= iNumStep; ++j) {
				aNV[j] = MarketVertex.Standard (
					adtVertex[j] = dtSpot.addMonths (6 * j + 6),
					adblCSA[j],
					Math.exp (-0.5 * adblBankHazardRate[j] * (j + 1)),
					adblBankRecoveryRate[j],
					adblBankFundingSpread[j],
					Math.exp (-0.5 * adblCounterPartyHazardRate[j] * (j + 1)),
					adblCounterPartyRecoveryRate[j]
				);

				aadblCollateralBalance[i][j] = 0.;

				aCGV[j] = new CollateralGroupVertex (
					adtVertex[j],
					aadblPortfolioValue[i][j],
					0.,
					0.
				);
			}

			MarketPath np = new MarketPath (aNV);

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
