
package org.drip.sample.burgard2012;

import org.drip.analytics.date.*;
import org.drip.measure.discrete.SequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLinear;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.numeraire.MarketPath;
import org.drip.xva.numeraire.MarketVertex;
import org.drip.xva.strategy.FundingGroupPathAA2014;
import org.drip.xva.strategy.NettingGroupPathAA2014;
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
 * FixFloatVABank illustrates the Fix-Float Swap Valuation Adjustment Metrics Dependence on the Bank Spread
 *  using the Set of Netting Group Exposure Simulations. The References are:
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

public class FixFloatVABank {

	private static final double[][] ATMSwapRateOffsetRealization (
		final DiffusionEvolver deATMSwapRateOffset,
		final double dblATMSwapRateOffsetInitial,
		final double dblTime,
		final double dblTimeWidth,
		final int iNumStep,
		final int iNumSimulation)
		throws Exception
	{
		double[][] aablATMSwapRateOffset = new double[iNumSimulation][iNumStep + 1];

		for (int i = 0; i < iNumSimulation; ++i) {
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

			aablATMSwapRateOffset[i][0] = dblATMSwapRateOffsetInitial;

			for (int j = 1; j <= iNumStep; ++j)
				aablATMSwapRateOffset[i][j] = aJDE[j - 1].finish();
		}

		return aablATMSwapRateOffset;
	}

	public static final void VA (
		final double dblBankHazardRate)
		throws Exception
	{
		int iNumStep = 10;
		double dblTime = 5.;
		int iNumPath = 10000;
		double dblATMSwapRateOffsetDrift = 0.0;
		double dblATMSwapRateOffsetVolatility = 0.15;
		double dblATMSwapRateOffsetInitial = 0.;
		double dblCSADrift = 0.01;
		double dblBankRecoveryRate = 0.40;
		double dblCounterPartyHazardRate = 0.025;
		double dblCounterPartyRecoveryRate = 0.30;

		double dblTimeWidth = dblTime / iNumStep;
		JulianDate[] adtVertex = new JulianDate[iNumStep + 1];
		MarketVertex[] aVN = new MarketVertex[iNumStep + 1];
		CounterPartyGroupPath[] aCPGP = new CounterPartyGroupPath[iNumPath];
		double dblBankFundingSpread = dblBankHazardRate / (1. - dblBankRecoveryRate);

		JulianDate dtSpot = DateUtil.Today();

		double[][] aaablATMSwapRateOffset = ATMSwapRateOffsetRealization (
			new DiffusionEvolver (
				DiffusionEvaluatorLinear.Standard (
					dblATMSwapRateOffsetDrift,
					dblATMSwapRateOffsetVolatility
				)
			),
			dblATMSwapRateOffsetInitial,
			dblTime,
			dblTimeWidth,
			iNumStep,
			iNumPath
		);

		for (int i = 0; i <= iNumStep; ++i)
			aVN[i] = MarketVertex.Standard (
				adtVertex[i] = dtSpot.addMonths (6 * i),
				Math.exp (0.5 * dblCSADrift * i),
				Math.exp (-0.5 * dblBankHazardRate * i),
				dblBankRecoveryRate,
				dblBankFundingSpread,
				Math.exp (-0.5 * dblCounterPartyHazardRate * i),
				dblCounterPartyRecoveryRate
			);

		MarketPath np = new MarketPath (aVN);

		for (int i = 0; i < iNumPath; ++i) {
			CollateralGroupVertexVanilla[] aCGV = new CollateralGroupVertexVanilla[iNumStep + 1];

			for (int j = 0; j <= iNumStep; ++j)
				aCGV[j] = new CollateralGroupVertexVanilla (
					adtVertex[j],
					dblTimeWidth * (iNumStep - j) * aaablATMSwapRateOffset[i][j],
					0.,
					0.
				);

			CollateralGroupPath[] aCGP = new CollateralGroupPath[] {new CollateralGroupPath (aCGV)};

			aCPGP[i] = new CounterPartyGroupPath (
				new NettingGroupPathAA2014[] {
					new NettingGroupPathAA2014 (
						aCGP,
						np
					)
				},
				new FundingGroupPathAA2014[] {
					new FundingGroupPathAA2014 (
						aCGP,
						np
					)
				}
			);
		}

		CounterPartyGroupAggregator cpga = new CounterPartyGroupAggregator (aCPGP);

		System.out.println ("\t|| " +
			FormatUtil.FormatDouble (dblBankHazardRate, 3, 0, 10000.) + " bp => " +
			FormatUtil.FormatDouble (cpga.ucva(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.ftdcva(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.cva(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.cvacl(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.dva(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.fva(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.fda(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.fca(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.fba(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.sfva(), 1, 2, 100.) + "% | " +
			FormatUtil.FormatDouble (cpga.total(), 1, 2, 100.) + "% ||"
		);
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double[] adblBankHazardRate = new double[] {
			0.0025,
			0.0050,
			0.0075,
			0.0100,
			0.0125,
			0.0150,
			0.0175,
			0.0200,
			0.0225,
			0.0250,
			0.0275,
			0.0300
		};

		System.out.println();

		System.out.println ("\t||-------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||                                      VA DEPENDENCE ON BANK HAZARD RATE                                      ||");

		System.out.println ("\t||-------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||  Hazard =>  UCVA  | FTDCVA |   CVA  |  CVACL |   DVA  |   FVA  |   FDA  |   FCA  |   FBA  |  SFVA  |  Total ||");

		System.out.println ("\t||-------------------------------------------------------------------------------------------------------------||");

		for (double dblBankHazardRate : adblBankHazardRate)
			VA (dblBankHazardRate);

		System.out.println ("\t||-------------------------------------------------------------------------------------------------------------||");

		System.out.println();
	}
}
