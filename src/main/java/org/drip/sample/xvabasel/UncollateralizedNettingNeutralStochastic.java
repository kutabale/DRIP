
package org.drip.sample.xvabasel;

import org.drip.analytics.date.*;
import org.drip.measure.crng.RandomNumberGenerator;
import org.drip.measure.discrete.CorrelatedPathVertexDimension;
import org.drip.measure.dynamics.*;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.measure.statistics.UnivariateDiscreteThin;
import org.drip.quant.common.FormatUtil;
import org.drip.quant.linearalgebra.Matrix;
import org.drip.service.env.EnvManager;
import org.drip.xva.basel.*;
import org.drip.xva.collateral.*;
import org.drip.xva.cpty.*;
import org.drip.xva.numeraire.*;
import org.drip.xva.strategy.*;

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
 * UncollateralizedNettingNeutralStochastic examines the Basel BCBS 2012 OTC Accounting Impact to a
 *  Portfolio of 10 Swaps resulting from the Addition of a New Swap - Comparison via both FVA/FDA and FCA/FBA
 *  Schemes. Simulation is carried out under the following Criteria:
 *  
 *    - Collateralization Status - Uncollateralized
 *    - Aggregation Unit         - Netting Group
 *    - Added Swap Type          - Zero Upfront Par Swap (Neutral)
 *    - Market Dynamics          - Fully Stochastic (Correlated Market Evolution)
 *  
 *  
 *  The References are:
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

public class UncollateralizedNettingNeutralStochastic {

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
			UnitRandomEdge.Diffusion (adblRandom),
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
			UnitRandomEdge.Diffusion (adblRandom),
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
		final double dblTimeMaturity,
		final double dblSwapNotional)
		throws Exception
	{
		double[] adblSwapPortfolioValueRealization = new double[iNumStep + 1];
		int iMaturityStep = (int) (dblTimeMaturity / dblTimeWidth);

		for (int i = 0; i < iNumStep; ++i)
			adblSwapPortfolioValueRealization[i] = 0.;

		double[] adblATMSwapRateOffsetRealization = ATMSwapRateOffsetRealization (
			deATMSwapRate,
			dblATMSwapRateStart,
			adblRandom,
			dblTime,
			dblTimeWidth,
			iNumStep
		);

		for (int j = 0; j <= iNumStep; ++j)
			adblSwapPortfolioValueRealization[j] = j > iMaturityStep ? 0. :
				dblSwapNotional * dblTimeWidth * (iMaturityStep - j) * adblATMSwapRateOffsetRealization[j];

		return adblSwapPortfolioValueRealization;
	}

	private static final double[][] Path (
		final double[][] aadblCorrelation,
		final int iNumVertex)
		throws Exception
	{
		CorrelatedPathVertexDimension cpvd = new CorrelatedPathVertexDimension (
			new RandomNumberGenerator(),
			aadblCorrelation,
			iNumVertex,
			1,
			false,
			null
		);

		return cpvd.multiPathVertexRd()[0].flatform();
	}

	private static final ExposureAdjustmentAggregator[] Mix (
		final double dblTimeMaturity1,
		final double dblATMSwapRateOffsetStart1,
		final double dblSwapNotional1,
		final double dblTimeMaturity2,
		final double dblATMSwapRateOffsetStart2,
		final double dblSwapNotional2)
		throws Exception
	{
		int iNumStep = 10;
		int iNumPath = 100000;
		int iNumVertex = 10;
		double dblTime = 5.;
		double dblATMSwapRateOffsetDrift = 0.0;
		double dblATMSwapRateOffsetVolatility = 0.25;
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
		double[][] aadblPortfolio1Value = new double[iNumPath][iNumStep + 1];
		double[][] aadblPortfolio2Value = new double[iNumPath][iNumStep + 1];
		double[][] aadblCollateralBalance = new double[iNumPath][iNumStep + 1];
		PathExposureAdjustment[] aCPGPGround = new PathExposureAdjustment[iNumPath];
		PathExposureAdjustment[] aCPGPExtended = new PathExposureAdjustment[iNumPath];
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

		DiffusionEvolver deBankRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankRecoveryRateDrift,
				dblBankRecoveryRateVolatility
			)
		);

		DiffusionEvolver deCounterPartyHazardRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblCounterPartyHazardRateDrift,
				dblCounterPartyHazardRateVolatility
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
				Path (
					aadblCorrelation,
					iNumVertex
				)
			);

			aadblPortfolio1Value[i] = SwapPortfolioValueRealization (
				deATMSwapRateOffset,
				dblATMSwapRateOffsetStart1,
				aadblNumeraire[0],
				iNumVertex,
				dblTime,
				dblTimeWidth,
				dblTimeMaturity1,
				dblSwapNotional1
			);

			aadblPortfolio2Value[i] = SwapPortfolioValueRealization (
				deATMSwapRateOffset,
				dblATMSwapRateOffsetStart2,
				aadblNumeraire[0],
				iNumVertex,
				dblTime,
				dblTimeWidth,
				dblTimeMaturity2,
				dblSwapNotional2
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
			HypothecationGroupVertexRegular[] aCGV1 = new HypothecationGroupVertexRegular[iNumStep + 1];
			HypothecationGroupVertexRegular[] aCGV2 = new HypothecationGroupVertexRegular[iNumStep + 1];

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

				aCGV1[j] = new HypothecationGroupVertexRegular (
					adtVertex[j],
					aadblPortfolio1Value[i][j],
					0.,
					0.
				);

				aCGV2[j] = new HypothecationGroupVertexRegular (
					adtVertex[j],
					aadblPortfolio2Value[i][j],
					0.,
					0.
				);
			}

			MarketPath np = new MarketPath (aNV);

			HypothecationGroupPath[] aCGP1 = new HypothecationGroupPath[] {
				new HypothecationGroupPath (aCGV1)
			};

			aCPGPGround[i] = new PathExposureAdjustment (
				new NettingGroupPathAA2014[] {
					new NettingGroupPathAA2014 (
						aCGP1,
						np
					)
				},
				new FundingGroupPathAA2014[] {
					new FundingGroupPathAA2014 (
						aCGP1,
						np
					)
				}
			);

			HypothecationGroupPath[] aCGP2 = new HypothecationGroupPath[] {
				new HypothecationGroupPath (aCGV2)
			};

			aCPGPExtended[i] = new PathExposureAdjustment (
				new NettingGroupPathAA2014[] {
					new NettingGroupPathAA2014 (
						aCGP1,
						np
					),
					new NettingGroupPathAA2014 (
						aCGP2,
						np
					)
				},
				new FundingGroupPathAA2014[] {
					new FundingGroupPathAA2014 (
						aCGP1,
						np
					),
					new FundingGroupPathAA2014 (
						aCGP2,
						np
					)
				}
			);
		}

		return new ExposureAdjustmentAggregator[] {
			new ExposureAdjustmentAggregator (aCPGPGround),
			new ExposureAdjustmentAggregator (aCPGPExtended)
		};
	}

	private static final void CPGDDump (
		final String strHeader,
		final ExposureAdjustmentDigest cpgd)
		throws Exception
	{
		System.out.println();

		UnivariateDiscreteThin udtUCVA = cpgd.ucva();

		UnivariateDiscreteThin udtFTDCVA = cpgd.ftdcva();

		UnivariateDiscreteThin udtCVACL = cpgd.cvacl();

		UnivariateDiscreteThin udtCVA = cpgd.cva();

		UnivariateDiscreteThin udtFVA = cpgd.fva();

		UnivariateDiscreteThin udtFDA = cpgd.fda();

		UnivariateDiscreteThin udtFCA = cpgd.fca();

		UnivariateDiscreteThin udtFBA = cpgd.fba();

		UnivariateDiscreteThin udtSFVA = cpgd.sfva();

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);

		System.out.println (strHeader);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||  OODLE  =>  UCVA   | FTDCVA  |  CVACL  |   CVA   |   DVA   |   FDA   |   FCA   |   FBA   |   SFVA  ||"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| Average => " +
			FormatUtil.FormatDouble (udtUCVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.average(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.average(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t|| Minimum => " +
			FormatUtil.FormatDouble (udtUCVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.minimum(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.minimum(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t|| Maximum => " +
			FormatUtil.FormatDouble (udtUCVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.maximum(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.maximum(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t||  Error  => " +
			FormatUtil.FormatDouble (udtUCVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.error(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.error(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);
	}

	private static final void CPGDDiffDump (
		final String strHeader,
		final ExposureAdjustmentDigest cpgdGround,
		final ExposureAdjustmentDigest cpgdExpanded)
		throws Exception
	{
		System.out.println();

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);

		System.out.println (strHeader);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||  OODLE  =>  UCVA   | FTDCVA  |  CVACL  |   CVA   |   DVA   |   FDA   |   FCA   |   FBA   |   SFVA  ||"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| Average => " +
			FormatUtil.FormatDouble (cpgdExpanded.ucva().average() - cpgdGround.ucva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.ftdcva().average() - cpgdGround.ftdcva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.cvacl().average() - cpgdGround.cvacl().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.cva().average() - cpgdGround.cva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.fva().average() - cpgdGround.fva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.fda().average() - cpgdGround.fda().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.fca().average() - cpgdGround.fca().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (cpgdExpanded.fba().average() - cpgdGround.fba().average(), 3, 1, 10000.) + "  | " + 
			FormatUtil.FormatDouble (cpgdExpanded.sfva().average() - cpgdGround.sfva().average(), 3, 1, 10000.) + "  ||"
		);

		System.out.println (
			"\t||----------------------------------------------------------------------------------------------------||"
		);
	}

	private static final void BaselAccountingMetrics (
		final String strHeader,
		final ExposureAdjustmentAggregator cpgaGround,
		final ExposureAdjustmentAggregator cpgaExpanded)
		throws Exception
	{
		OTCAccountingScheme oasFCAFBA = new OTCAccountingSchemeFCAFBA (cpgaGround);

		OTCAccountingScheme oasFVAFDA = new OTCAccountingSchemeFVAFDA (cpgaGround);

		OTCAccountingPolicy oapFCAFBA = oasFCAFBA.feePolicy (cpgaExpanded);

		OTCAccountingPolicy oapFVAFDA = oasFVAFDA.feePolicy (cpgaExpanded);

		System.out.println();

		System.out.println (
			"\t||---------------------------------------------------------------------||"
		);

		System.out.println (strHeader);

		System.out.println (
			"\t||---------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| L -> R:                                                             ||"
		);

		System.out.println (
			"\t||         - Accounting Type (FCA/FBA vs. FVA/FDA)                     ||"
		);

		System.out.println (
			"\t||         - Contra Asset Adjustment                                   ||"
		);

		System.out.println (
			"\t||         - Contra Liability Adjustment                               ||"
		);

		System.out.println (
			"\t||         - FTP (Funding Transfer Pricing) (bp)                       ||"
		);

		System.out.println (
			"\t||         - CET1 (Common Equity Tier I) Change (bp)                   ||"
		);

		System.out.println (
			"\t||         - CL (Contra Liability) Change (bp)                         ||"
		);

		System.out.println (
			"\t||         - PFV (Porfolio Value) Change (Income) (bp)                 ||"
		);

		System.out.println (
			"\t||---------------------------------------------------------------------||"
		);

		System.out.println ("\t|| FCA/FBA Accounting => " +
			FormatUtil.FormatDouble (oasFCAFBA.contraAssetAdjustment(), 1, 4, 1.) + " | " +
			FormatUtil.FormatDouble (oasFCAFBA.contraLiabilityAdjustment(), 1, 4, 1.) + " | " +
			FormatUtil.FormatDouble (oapFCAFBA.fundingTransferPricing(), 3, 0, 10000.) + " | " +
			FormatUtil.FormatDouble (oapFCAFBA.cet1Change(), 3, 0, 10000.) + " | " +
			FormatUtil.FormatDouble (oapFCAFBA.contraLiabilityChange(), 3, 0, 10000.) + " | " +
			FormatUtil.FormatDouble (oapFCAFBA.portfolioValueChange(), 3, 0, 10000.) + " || "
		);

		System.out.println ("\t|| FVA/FDA Accounting => " +
			FormatUtil.FormatDouble (oasFVAFDA.contraAssetAdjustment(), 1, 4, 1.) + " | " +
			FormatUtil.FormatDouble (oasFVAFDA.contraLiabilityAdjustment(), 1, 4, 1.) + " | " +
			FormatUtil.FormatDouble (oapFVAFDA.fundingTransferPricing(), 3, 0, 10000.) + " | " +
			FormatUtil.FormatDouble (oapFVAFDA.cet1Change(), 3, 0, 10000.) + " | " +
			FormatUtil.FormatDouble (oapFVAFDA.contraLiabilityChange(), 3, 0, 10000.) + " | " +
			FormatUtil.FormatDouble (oapFVAFDA.portfolioValueChange(), 3, 0, 10000.) + " || "
		);

		System.out.println (
			"\t||---------------------------------------------------------------------||"
		);

		System.out.println();
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		ExposureAdjustmentAggregator[] aCPGA = Mix (
			5.,
			0.,
			100.,
			5.,
			0.,
			1.
		);

		ExposureAdjustmentAggregator cpgaGround = aCPGA[0];
		ExposureAdjustmentAggregator cpgaExtended = aCPGA[1];

		ExposureAdjustmentDigest cpgdGround = cpgaGround.digest();

		ExposureAdjustmentDigest cpgdExtended = cpgaExtended.digest();

		CPGDDump (
			"\t||                                   GROUND BOOK ADJUSTMENT METRICS                                   ||",
			cpgdGround
		);

		CPGDDump (
			"\t||                                  EXTENDED BOOK ADJUSTMENT METRICS                                  ||",
			cpgdExtended
		);

		CPGDDiffDump (
			"\t||                              TRADE INCREMENT ADJUSTMENT METRICS (bp)                               ||",
			cpgdGround,
			cpgdExtended
		);

		BaselAccountingMetrics (
			"\t||           ALBANESE & ANDERSEN (2015) BCBS OTC ACCOUNTING            ||",
			cpgaGround,
			cpgaExtended
		);
	}
}
