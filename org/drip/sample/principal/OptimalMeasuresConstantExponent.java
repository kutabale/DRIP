
package org.drip.sample.principal;

import org.drip.execution.dynamics.Almgren2003Parameters;
import org.drip.execution.generator.Almgren2003PowerImpact;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.Almgren2003PowerImpactContinuous;
import org.drip.execution.parameters.*;
import org.drip.execution.principal.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * OptimalMeasuresConstantExponent demonstrates the Dependence Exponents on Liquidity, Trade Size, and
 *  Permanent Impact Adjusted Principal Discount for the Optimal Principal Horizon and the Optional
 *  Information Ratio. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 16 (6) 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OptimalMeasuresConstantExponent {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblDailyVolume = 1000000.;
		double dblBidAskSpread = 0.;
		double dblPermanentImpactFactor = 0.;
		double dblTemporaryImpactFactor = 0.01;
		double dblDailyVolumeExecutionFactor = 0.1;
		double dblDrift = 0.;
		double dblVolatility = 1.;
		double dblSerialCorrelation = 0.;
		double dblX = 100000.;
		double dblFinishTime = 1.;
		double dblLambda = 5.e-06;

		double[] adblK = new double[] {
			0.5,
			1.0,
			1.5,
			2.0,
			2.5,
			3.0,
			3.5,
			4.0,
			4.5,
			5.0,
			5.5,
			6.0,
			6.5,
			7.0,
			7.5,
			8.0,
			8.5,
			9.0,
			9.5
		};

		System.out.println();

		System.out.println ("\t|-----------------------------------------------------||");

		System.out.println ("\t|    Optimal Market Parameters Horizon Dependence     ||");

		System.out.println ("\t|-----------------------------------------------------||");

		for (double dblK : adblK) {
			PriceMarketImpactPower pmip = new PriceMarketImpactPower (
				new AssetTransactionSettings (
					dblS0,
					dblDailyVolume,
					dblBidAskSpread
				),
				dblPermanentImpactFactor,
				dblTemporaryImpactFactor,
				dblDailyVolumeExecutionFactor,
				dblK
			);

			Almgren2003Parameters a2003p = new Almgren2003Parameters (
				new ArithmeticPriceDynamicsSettings (
					dblDrift,
					dblVolatility,
					dblSerialCorrelation
				),
				(ParticipationRateLinear) pmip.permanentTransactionFunction(),
				(ParticipationRatePower) pmip.temporaryTransactionFunction()
			);

			Almgren2003Estimator a2003e = new Almgren2003Estimator (
				(Almgren2003PowerImpactContinuous) Almgren2003PowerImpact.Standard (
					dblX,
					dblFinishTime,
					a2003p,
					dblLambda
				).generate(),
				a2003p
			);

			OptimalMeasureDependence omd = a2003e.optimalMeasures().omdHorizon();

			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (dblK, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.constant(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.liquidityExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.blockSizeExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.volatilityExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.adjustedPrincipalDiscountExponent(), 1, 3, 1.) + " ||"
			);
		}

		System.out.println ("\t|-----------------------------------------------------||");
	}
}
