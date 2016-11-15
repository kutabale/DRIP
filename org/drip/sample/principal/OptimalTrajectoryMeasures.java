
package org.drip.sample.principal;

import org.drip.execution.dynamics.Almgren2003Parameters;
import org.drip.execution.generator.Almgren2003TrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.Almgren2003TradingTrajectory;
import org.drip.execution.parameters.*;
import org.drip.execution.principal.Almgren2003Estimator;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
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
 * OptimalTrajectoryMeasures demonstrates the Trade Scheduling using the Equity Market Impact Functions
 *  determined empirically by Almgren, Thum, Hauptmann, and Li (2005), using the Parameterization of Almgren
 *  (2003) for IBM. It generates the Transaction Cost/Principal Discount Measures from the Run The References
 *  are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OptimalTrajectoryMeasures {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblX = 100000.;
		double dblVolatility = 1.;
		double dblDailyVolume = 1000000.;
		double dblDailyVolumeExecutionFactor = 0.1;
		double dblTemporaryImpactFactor = 0.01;
		double dblT = 5.;
		int iNumInterval = 20;
		double dblLambda = 1.e-06;
		double dblK = 1.;
		double dblPrincipalDiscount = 0.15;

		ArithmeticPowerImpact apim = new ArithmeticPowerImpact (
			new AssetTransactionSettings (
				dblS0,
				dblDailyVolume,
				0.
			),
			dblTemporaryImpactFactor,
			dblDailyVolumeExecutionFactor,
			dblK
		);

		Almgren2003Parameters a2003p = new Almgren2003Parameters (
			0.,
			dblVolatility,
			0.,
			(ParticipationRateLinear) apim.permanentTransactionFunction(),
			(ParticipationRatePower) apim.temporaryTransactionFunction()
		);

		Almgren2003TrajectoryScheme a2003ts = Almgren2003TrajectoryScheme.Standard (
			dblX,
			dblT,
			iNumInterval,
			a2003p,
			dblLambda
		);

		Almgren2003TradingTrajectory a2003tt = (Almgren2003TradingTrajectory) a2003ts.generate();

		double[] adblExecutionTimeNode = a2003tt.executionTimeNode();

		double[] adblTradeList = a2003tt.tradeList();

		double[] adblHoldings = a2003tt.holdings();

		Almgren2003Estimator a2003e = new Almgren2003Estimator (
			a2003tt,
			a2003p
		);

		System.out.println();

		System.out.println ("\t|----------------------------------||");

		System.out.println ("\t| IBM ATHL 2005 Optimal Trajectory ||");

		System.out.println ("\t|----------------------------------||");

		System.out.println ("\t|     L -> R:                      ||");

		System.out.println ("\t|           - Execution Time Node  ||");

		System.out.println ("\t|           - Holdings Remaining   ||");

		System.out.println ("\t|           - Trade List Amount    ||");

		System.out.println ("\t|           - Holdings (%)         ||");

		System.out.println ("\t|----------------------------------||");

		for (int i = 1; i < adblExecutionTimeNode.length; ++i)
			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (adblExecutionTimeNode[i], 1, 2, 1.) + " | " +
				FormatUtil.FormatDouble (adblHoldings[i], 5, 0, 1.) + " | " + 
				FormatUtil.FormatDouble (adblTradeList[i - 1], 5, 0, 1.) + " | " + 
				FormatUtil.FormatDouble (adblHoldings[i] / dblX, 2, 1, 100.) + "% ||"
			);

		System.out.println ("\t|----------------------------------||");

		System.out.println();

		System.out.println ("\t|-----------------------------------------------------------------------||");

		System.out.println ("\t|       IBM ATHL 2005 Optimal Trajectory Transaction Cost Measures      ||");

		System.out.println ("\t|-----------------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation ( X 10^-03)                  : " +
			FormatUtil.FormatDouble (a2003tt.transactionCostExpectation(), 5, 2, 1.e-03) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance ( X 10^-06)                     : " +
			FormatUtil.FormatDouble (a2003tt.transactionCostVariance(), 5, 2, 1.e-06) + " ||"
		);

		System.out.println (
			"\t| Characteristic Time                                       : " +
			FormatUtil.FormatDouble (a2003tt.characteristicTime(), 5, 2, 1.) + " ||"
		);

		System.out.println (
			"\t| Efficient Frontier Hyperboloid Boundary Value ( X 10^-12) : " +
			FormatUtil.FormatDouble (a2003tt.hyperboloidBoundaryValue(), 5, 2, 1.e-12) + " ||"
		);

		System.out.println (
			"\t| Break-even Principal Discount (cents per unit)            : " +
			FormatUtil.FormatDouble (a2003e.breakevenPrincipalDiscount(), 5, 2, 100.) + " ||"
		);

		System.out.println (
			"\t| Gross Profit Expectation                                  : " +
			FormatUtil.FormatDouble (a2003e.principalMeasure (dblPrincipalDiscount).mean(), 5, 2, 1.) + " ||"
		);

		System.out.println (
			"\t| Gross Profit Variance ( X 10^-06)                         : " +
			FormatUtil.FormatDouble (a2003e.principalMeasure (dblPrincipalDiscount).variance(), 5, 2, 1.e-06) + " ||"
		);

		System.out.println (
			"\t| Gross Returns Expectation                                 : " +
			FormatUtil.FormatDouble (a2003e.horizonPrincipalMeasure (dblPrincipalDiscount).mean(), 5, 2, 1.) + " ||"
		);

		System.out.println (
			"\t| Gross Returns Variance ( X 10^-06)                        : " +
			FormatUtil.FormatDouble (a2003e.horizonPrincipalMeasure (dblPrincipalDiscount).variance(), 5, 2, 1.e-06) + " ||"
		);

		System.out.println (
			"\t| Information Ratio ( X 10^+03)                             : " +
			FormatUtil.FormatDouble (a2003e.informationRatio (dblPrincipalDiscount), 5, 2, 1.e+03) + " ||"
		);

		System.out.println (
			"\t| Optimal Information Ratio ( X 10^+03)                     : " +
			FormatUtil.FormatDouble (a2003e.optimalInformationRatio (dblPrincipalDiscount), 5, 2, 1.e+03) + " ||"
		);

		System.out.println (
			"\t| Optimal Information Ratio Horizon                         : " +
			FormatUtil.FormatDouble (a2003e.optimalInformationRatioHorizon (dblPrincipalDiscount), 5, 2, 1.) + " ||"
		);

		System.out.println ("\t|-----------------------------------------------------------------------||");
	}
}
