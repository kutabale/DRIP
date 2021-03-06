
package org.drip.sample.burgard2013;

import org.drip.analytics.date.*;
import org.drip.measure.bridge.BrokenDateInterpolatorLinearT;
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
import org.drip.xva.cpty.*;
import org.drip.xva.definition.*;
import org.drip.xva.hypothecation.*;
import org.drip.xva.set.*;
import org.drip.xva.strategy.*;
import org.drip.xva.universe.*;

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
 * PerfectReplicationCollateralizedFundingStochastic examines the Basel BCBS 2012 OTC Accounting Impact to a
 *  Portfolio of 10 Swaps resulting from the Addition of a New Swap - Comparison via both FVA/FDA and FCA/FBA
 *  Schemes. Simulation is carried out under the following Criteria using one of the Generalized Burgard
 *  Kjaer (2013) Scheme.
 *  
 *    - Collateralization Status - Collateralized
 *    - Aggregation Unit         - Funding Group
 *    - Added Swap Type          - Positive Upfront Swap (Payable)
 *    - Market Dynamics          - Stochastic (Dynamic Market Evolution)
 *    - Funding Strategy         - Perfect Replication
 *  
 * The References are:
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

public class PerfectReplicationCollateralizedFundingStochastic {

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
		double[] adblTimeWidth = new double[iNumStep];

		for (int i = 0; i < iNumStep; ++i)
			adblTimeWidth[i] = dblTimeWidth;

		JumpDiffusionEdge[] aJDE = deNumeraireValue.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				dblNumeraireValueInitial,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				adblRandom
			),
			dblTimeWidth
		);

		for (int j = 1; j <= iNumStep; ++j)
			adblNumeraireValue[j] = aJDE[j - 1].finish();

		return adblNumeraireValue;
	}

	private static final double[] VertexNumeraireRealization (
		final DiffusionEvolver deNumeraireValue,
		final double dblNumeraireValueInitial,
		final double dblTime,
		final double dblTimeWidth,
		final double[] adblRandom,
		final int iNumStep)
		throws Exception
	{
		double[] adblNumeraireValue = new double[iNumStep + 1];
		double[] adblTimeWidth = new double[iNumStep];

		for (int i = 0; i < iNumStep; ++i)
			adblTimeWidth[i] = dblTimeWidth;

		JumpDiffusionVertex[] aJDV = deNumeraireValue.vertexSequenceReverse (
			new JumpDiffusionVertex (
				dblTime,
				dblNumeraireValueInitial,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				adblRandom
			),
			adblTimeWidth
		);

		for (int j = 0; j <= iNumStep; ++j)
			adblNumeraireValue[j] = aJDV[j].value();

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
		double[] adblTimeWidth = new double[iNumStep];

		for (int i = 0; i < iNumStep; ++i)
			adblTimeWidth[i] = dblTimeWidth;

		JumpDiffusionEdge[] aJDE = deATMSwapRateOffset.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				dblATMSwapRateOffsetInitial,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				adblRandom
			),
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
		double dblOvernightNumeraireDrift = 0.004;
		double dblOvernightNumeraireVolatility = 0.02;
		double dblOvernightNumeraireInitial = 1.;
		double dblCSADrift = 0.01;
		double dblCSAVolatility = 0.05;
		double dblCSAInitial = 1.;
		double dblBankHazardRateDrift = 0.002;
		double dblBankHazardRateVolatility = 0.20;
		double dblBankHazardRateInitial = 0.015;
		double dblBankSeniorRecoveryRateDrift = 0.002;
		double dblBankSeniorRecoveryRateVolatility = 0.02;
		double dblBankSeniorRecoveryRateInitial = 0.40;
		double dblBankSubordinateRecoveryRateDrift = 0.001;
		double dblBankSubordinateRecoveryRateVolatility = 0.01;
		double dblBankSubordinateRecoveryRateInitial = 0.15;
		double dblCounterPartyHazardRateDrift = 0.002;
		double dblCounterPartyHazardRateVolatility = 0.30;
		double dblCounterPartyHazardRateInitial = 0.030;
		double dblCounterPartyRecoveryRateDrift = 0.002;
		double dblCounterPartyRecoveryRateVolatility = 0.02;
		double dblCounterPartyRecoveryRateInitial = 0.30;
		double dblBankSeniorFundingSpreadDrift = 0.00002;
		double dblBankSeniorFundingSpreadVolatility = 0.002;
		double dblBankSubordinateFundingSpreadDrift = 0.00001;
		double dblBankSubordinateFundingSpreadVolatility = 0.001;
		double dblCounterPartyFundingSpreadDrift = 0.000022;
		double dblCounterPartyFundingSpreadVolatility = 0.0022;
		double dblBankThreshold = -0.1;
		double dblCounterPartyThreshold = 0.1;

		double[][] aadblCorrelation = new double[][] {
			{1.00,  0.00,  0.03,  0.07,  0.04,  0.05,  0.00,  0.08,  0.00,  0.00,  0.00},  // PORTFOLIO
			{0.00,  1.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00},  // OVERNIGHT
			{0.03,  0.00,  1.00,  0.26,  0.33,  0.21,  0.00,  0.35,  0.13,  0.00,  0.00},  // CSA
			{0.07,  0.00,  0.26,  1.00,  0.45, -0.17,  0.00,  0.07,  0.77,  0.00,  0.00},  // BANK HAZARD
			{0.04,  0.00,  0.33,  0.45,  1.00, -0.22,  0.00, -0.54,  0.58,  0.00,  0.00},  // COUNTER PARTY HAZARD
			{0.05,  0.00,  0.21, -0.17, -0.22,  1.00,  0.00,  0.47, -0.23,  0.00,  0.00},  // BANK SENIOR RECOVERY
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00,  0.00,  0.00,  0.00,  0.00},  // BANK SUBORDINATE RECOVERY
			{0.08,  0.00,  0.35,  0.07, -0.54,  0.47,  0.00,  1.00,  0.01,  0.00,  0.00},  // COUNTER PARTY RECOVERY
			{0.00,  0.00,  0.13,  0.77,  0.58, -0.23,  0.00,  0.01,  1.00,  0.00,  0.00},  // BANK SENIOR FUNDING SPREAD
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00,  0.00},  // BANK SUBORDINATE FUNDING SPREAD
			{0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  0.00,  1.00}   // COUNTER PARTY FUNDING SPREAD
		};

		CollateralGroupSpecification cgs = CollateralGroupSpecification.FixedThreshold (
			"FIXEDTHRESHOLD",
			dblCounterPartyThreshold,
			dblBankThreshold
		);

		CounterPartyGroupSpecification cpgs = CounterPartyGroupSpecification.Standard ("CPGROUP");

		JulianDate dtSpot = DateUtil.Today();

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep + 1];
		double[][] aadblPortfolio1Value = new double[iNumPath][iNumStep + 1];
		double[][] aadblPortfolio2Value = new double[iNumPath][iNumStep + 1];
		MonoPathExposureAdjustment[] aMPEAGround = new MonoPathExposureAdjustment[iNumPath];
		MonoPathExposureAdjustment[] aMPEAExtended = new MonoPathExposureAdjustment[iNumPath];
		double dblBankSeniorFundingSpreadInitial = dblBankHazardRateInitial / (1. - dblBankSeniorRecoveryRateInitial);
		double dblBankSubordinateFundingSpreadInitial = dblBankHazardRateInitial / (1. - dblBankSubordinateRecoveryRateInitial);
		double dblCounterPartyFundingSpreadInitial = dblCounterPartyHazardRateInitial / (1. - dblCounterPartyRecoveryRateInitial);

		DiffusionEvolver deATMSwapRateOffset = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				dblATMSwapRateOffsetDrift,
				dblATMSwapRateOffsetVolatility
			)
		);

		DiffusionEvolver deOvernightNumeraire = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblOvernightNumeraireDrift,
				dblOvernightNumeraireVolatility
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

		DiffusionEvolver deBankSeniorRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankSeniorRecoveryRateDrift,
				dblBankSeniorRecoveryRateVolatility
			)
		);

		DiffusionEvolver deBankSubordinateRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankSubordinateRecoveryRateDrift,
				dblBankSubordinateRecoveryRateVolatility
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

		DiffusionEvolver deBankSeniorFundingSpread = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				dblBankSeniorFundingSpreadDrift,
				dblBankSeniorFundingSpreadVolatility
			)
		);

		DiffusionEvolver deBankSubordinateFundingSpread = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				dblBankSubordinateFundingSpreadDrift,
				dblBankSubordinateFundingSpreadVolatility
			)
		);

		DiffusionEvolver deCounterPartyFundingSpread = new DiffusionEvolver (
			DiffusionEvaluatorLinear.Standard (
				dblCounterPartyFundingSpreadDrift,
				dblCounterPartyFundingSpreadVolatility
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

			double[] adblOvernightNumeraire = VertexNumeraireRealization (
				deOvernightNumeraire,
				dblOvernightNumeraireInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[1],
				iNumStep
			);

			double[] adblCSA = VertexNumeraireRealization (
				deCSA,
				dblCSAInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[2],
				iNumStep
			);

			double[] adblBankHazardRate = NumeraireValueRealization (
				deBankHazardRate,
				dblBankHazardRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[3],
				iNumStep
			);

			double[] adblCounterPartyHazardRate = NumeraireValueRealization (
				deCounterPartyHazardRate,
				dblCounterPartyHazardRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[4],
				iNumStep
			);

			double[] adblBankSeniorRecoveryRate = NumeraireValueRealization (
				deBankSeniorRecoveryRate,
				dblBankSeniorRecoveryRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[5],
				iNumStep
			);

			double[] adblBankSubordinateRecoveryRate = NumeraireValueRealization (
				deBankSubordinateRecoveryRate,
				dblBankSubordinateRecoveryRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[6],
				iNumStep
			);

			double[] adblCounterPartyRecoveryRate = NumeraireValueRealization (
				deCounterPartyRecoveryRate,
				dblCounterPartyRecoveryRateInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[7],
				iNumStep
			);

			double[] adblBankSeniorFundingSpread = NumeraireValueRealization (
				deBankSeniorFundingSpread,
				dblBankSeniorFundingSpreadInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[8],
				iNumStep
			);

			double[] adblBankSubordinateFundingSpread = NumeraireValueRealization (
				deBankSubordinateFundingSpread,
				dblBankSubordinateFundingSpreadInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[9],
				iNumStep
			);

			double[] adblCounterPartyFundingSpread = NumeraireValueRealization (
				deCounterPartyFundingSpread,
				dblCounterPartyFundingSpreadInitial,
				dblTime,
				dblTimeWidth,
				aadblNumeraire[10],
				iNumStep
			);

			JulianDate dtStart = dtSpot;
			MarketVertex[] aMV = new MarketVertex [iNumStep + 1];
			double dblValueStart1 = dblTime * dblATMSwapRateOffsetStart1;
			double dblValueStart2 = dblTime * dblATMSwapRateOffsetStart2;
			CollateralGroupVertex[] aCGV1 = new CollateralGroupVertex[iNumStep + 1];
			CollateralGroupVertex[] aCGV2 = new CollateralGroupVertex[iNumStep + 1];

			for (int j = 0; j <= iNumStep; ++j) {
				JulianDate dtEnd = (adtVertex[j] = dtSpot.addMonths (6 * j + 6));

				double dblCollateralBalance1 = 0.;
				double dblCollateralBalance2 = 0.;
				double dblValueEnd1 = aadblPortfolio1Value[i][j];
				double dblValueEnd2 = aadblPortfolio2Value[i][j];

				CloseOutGeneral cog = new CloseOutBilateral (
					adblBankSeniorRecoveryRate[j],
					adblCounterPartyRecoveryRate[j]
				);

				aMV[j] = new MarketVertex (
					adtVertex[j] = dtSpot.addMonths (6 * j),
					Double.NaN,
					dblOvernightNumeraireDrift,
					new NumeraireMarketVertex (
						adblOvernightNumeraire[0],
						adblOvernightNumeraire[j]
					),
					dblCSADrift,
					new NumeraireMarketVertex (
						adblCSA[0],
						adblCSA[j]
					),
					new EntityMarketVertex (
						Math.exp (-0.5 * adblBankHazardRate[j] * j),
						adblBankHazardRate[j],
						adblBankSeniorRecoveryRate[j],
						adblBankSeniorFundingSpread[j],
						new NumeraireMarketVertex (
							Math.exp (-0.5 * adblBankHazardRate[j] * (1. - adblBankSeniorRecoveryRate[j]) * iNumStep),
							Math.exp (-0.5 * adblBankHazardRate[j] * (1. - adblBankSeniorRecoveryRate[j]) * (iNumStep - j))
						),
						adblBankSubordinateRecoveryRate[j],
						adblBankSubordinateFundingSpread[j],
						new NumeraireMarketVertex (
							Math.exp (-0.5 * adblBankHazardRate[j] * (1. - adblBankSubordinateRecoveryRate[j]) * iNumStep),
							Math.exp (-0.5 * adblBankHazardRate[j] * (1. - adblBankSubordinateRecoveryRate[j]) * (iNumStep - j))
						)
					),
					new EntityMarketVertex (
						Math.exp (-0.5 * adblCounterPartyHazardRate[j] * j),
						adblCounterPartyHazardRate[j],
						adblCounterPartyRecoveryRate[j],
						adblCounterPartyFundingSpread[j],
						new NumeraireMarketVertex (
							Math.exp (-0.5 * adblCounterPartyHazardRate[j] * (1. - adblCounterPartyRecoveryRate[j]) * iNumStep),
							Math.exp (-0.5 * adblCounterPartyHazardRate[j] * (1. - adblCounterPartyRecoveryRate[j]) * (iNumStep - j))
						),
						Double.NaN,
						Double.NaN,
						null
					)
				);

				if (0 != j) {
					CollateralAmountEstimator hae1 = new CollateralAmountEstimator (
						cgs,
						cpgs,
						new BrokenDateInterpolatorLinearT (
							dtStart.julian(),
							dtEnd.julian(),
							dblValueStart1,
							dblValueEnd1
						),
						Double.NaN
					);

					dblCollateralBalance1 = hae1.postingRequirement (dtEnd);

					CollateralAmountEstimator hae2 = new CollateralAmountEstimator (
						cgs,
						cpgs,
						new BrokenDateInterpolatorLinearT (
							dtStart.julian(),
							dtEnd.julian(),
							dblValueStart2,
							dblValueEnd2
						),
						Double.NaN
					);

					dblCollateralBalance2 = hae2.postingRequirement (dtEnd);

					aCGV1[j] = BurgardKjaerVertexBuilder.HedgeErrorDualBond (
						adtVertex[j],
						aadblPortfolio1Value[i][j],
						0.,
						dblCollateralBalance1,
						0.,
						new MarketEdge (
							aMV[j - 1],
							aMV[j]
						),
						cog
					);

					aCGV2[j] = BurgardKjaerVertexBuilder.HedgeErrorDualBond (
						adtVertex[j],
						aadblPortfolio2Value[i][j],
						0.,
						dblCollateralBalance2,
						0.,
						new MarketEdge (
							aMV[j - 1],
							aMV[j]
						),
						cog
					);
				} else {
					aCGV1[j] = BurgardKjaerVertexBuilder.Initial (
						adtVertex[j],
						aadblPortfolio1Value[i][0],
						aMV[j],
						cog
					);

					aCGV2[j] = BurgardKjaerVertexBuilder.Initial (
						adtVertex[j],
						aadblPortfolio2Value[i][0],
						aMV[j],
						cog
					);
				}
			}

			MarketPath mp = new MarketPath (aMV);

			CollateralGroupPath[] aHGPGround = new CollateralGroupPath[] {
				new CollateralGroupPath (aCGV1)
			};

			aMPEAGround[i] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenNettingGroupPath[] {
					new AlbaneseAndersenNettingGroupPath (
						aHGPGround,
						mp
					)
				},
				new AlbaneseAndersenFundingGroupPath[] {
					new AlbaneseAndersenFundingGroupPath (
						aHGPGround,
						mp
					)
				}
			);

			CollateralGroupPath[] aHGPExtended = new CollateralGroupPath[] {
				new CollateralGroupPath (aCGV1),
				new CollateralGroupPath (aCGV2)
			};

			aMPEAExtended[i] = new MonoPathExposureAdjustment (
				new AlbaneseAndersenNettingGroupPath[] {
					new AlbaneseAndersenNettingGroupPath (
						aHGPExtended,
						mp
					)
				},
				new AlbaneseAndersenFundingGroupPath[] {
					new AlbaneseAndersenFundingGroupPath (
						aHGPExtended,
						mp
					)
				}
			);
		}

		return new ExposureAdjustmentAggregator[] {
			new ExposureAdjustmentAggregator (aMPEAGround),
			new ExposureAdjustmentAggregator (aMPEAExtended)
		};
	}

	private static final void CPGDDump (
		final String strHeader,
		final ExposureAdjustmentDigest ead)
		throws Exception
	{
		System.out.println();

		UnivariateDiscreteThin udtUCOLVA = ead.ucolva();

		UnivariateDiscreteThin udtFTDCOLVA = ead.ftdcolva();

		UnivariateDiscreteThin udtUCVA = ead.ucva();

		UnivariateDiscreteThin udtFTDCVA = ead.ftdcva();

		UnivariateDiscreteThin udtCVACL = ead.cvacl();

		UnivariateDiscreteThin udtCVA = ead.cva();

		UnivariateDiscreteThin udtDVA = ead.dva();

		UnivariateDiscreteThin udtFVA = ead.fva();

		UnivariateDiscreteThin udtFDA = ead.fda();

		UnivariateDiscreteThin udtFCA = ead.fca();

		UnivariateDiscreteThin udtFBA = ead.fba();

		UnivariateDiscreteThin udtSFVA = ead.sfva();

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (strHeader);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||  OODLE  => UCOLVA  | FTDCOLVA |  UCVA   | FTDCVA  |  CVACL  |   CVA   |   DVA   |   FVA   |   FDA   |   FCA   |   FBA   |   SFVA  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| Average => " +
			FormatUtil.FormatDouble (udtUCOLVA.average(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (udtFTDCOLVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtUCVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtDVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.average(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.average(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.average(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t|| Minimum => " +
			FormatUtil.FormatDouble (udtUCOLVA.minimum(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (udtFTDCOLVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtUCVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtDVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.minimum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.minimum(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.minimum(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t|| Maximum => " +
			FormatUtil.FormatDouble (udtUCOLVA.maximum(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (udtFTDCOLVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtUCVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtDVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.maximum(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.maximum(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.maximum(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t||  Error  => " +
			FormatUtil.FormatDouble (udtUCOLVA.error(), 2, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (udtFTDCOLVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtUCVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFTDCVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVACL.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtCVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtDVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFVA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFDA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFCA.error(), 2, 2, 1.) + "  | " +
			FormatUtil.FormatDouble (udtFBA.error(), 2, 2, 1.) + "  | " + 
			FormatUtil.FormatDouble (udtSFVA.error(), 2, 2, 1.) + "  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);
	}

	private static final void CPGDDiffDump (
		final String strHeader,
		final ExposureAdjustmentDigest eadGround,
		final ExposureAdjustmentDigest eadExpanded)
		throws Exception
	{
		System.out.println();

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (strHeader);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t||  OODLE  => UCOLVA  | FTDCOLVA |  UCVA   | FTDCVA  |  CVACL  |   CVA   |   DVA   |   FVA   |   FDA   |   FCA   |   FBA   |   SFVA  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);

		System.out.println (
			"\t|| Average => " +
			FormatUtil.FormatDouble (eadExpanded.ucolva().average() - eadGround.ucolva().average(), 3, 1, 10000.) + "  |  " +
			FormatUtil.FormatDouble (eadExpanded.ftdcolva().average() - eadGround.ftdcolva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.ucva().average() - eadGround.ucva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.ftdcva().average() - eadGround.ftdcva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.cvacl().average() - eadGround.cvacl().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.cva().average() - eadGround.cva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.dva().average() - eadGround.dva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.fva().average() - eadGround.fva().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.fda().average() - eadGround.fda().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.fca().average() - eadGround.fca().average(), 3, 1, 10000.) + "  | " +
			FormatUtil.FormatDouble (eadExpanded.fba().average() - eadGround.fba().average(), 3, 1, 10000.) + "  | " + 
			FormatUtil.FormatDouble (eadExpanded.sfva().average() - eadGround.sfva().average(), 3, 1, 10000.) + "  ||"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------||"
		);
	}

	private static final void BaselAccountingMetrics (
		final String strHeader,
		final ExposureAdjustmentAggregator eaaGround,
		final ExposureAdjustmentAggregator eaaExpanded)
		throws Exception
	{
		OTCAccountingModus oasFCAFBA = new OTCAccountingModusFCAFBA (eaaGround);

		OTCAccountingModus oasFVAFDA = new OTCAccountingModusFVAFDA (eaaGround);

		OTCAccountingPolicy oapFCAFBA = oasFCAFBA.feePolicy (eaaExpanded);

		OTCAccountingPolicy oapFVAFDA = oasFVAFDA.feePolicy (eaaExpanded);

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

		ExposureAdjustmentAggregator eaaGround = aCPGA[0];
		ExposureAdjustmentAggregator eaaExtended = aCPGA[1];

		ExposureAdjustmentDigest eadGround = eaaGround.digest();

		ExposureAdjustmentDigest eadExtended = eaaExtended.digest();

		CPGDDump (
			"\t||                                                  GROUND BOOK ADJUSTMENT METRICS                                                   ||",
			eadGround
		);

		CPGDDump (
			"\t||                                                 EXTENDED BOOK ADJUSTMENT METRICS                                                  ||",
			eadExtended
		);

		CPGDDiffDump (
			"\t||                                             TRADE INCREMENT ADJUSTMENT METRICS (bp)                                               ||",
			eadGround,
			eadExtended
		);

		BaselAccountingMetrics (
			"\t||           ALBANESE & ANDERSEN (2015) BCBS OTC ACCOUNTING            ||",
			eaaGround,
			eaaExtended
		);
	}
}
