
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
 * OptimalMeasuresDiscountDependence demonstrates the Dependence of the Optimal Principal Measures on the
 * 	Discount. The References are:
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

public class OptimalMeasuresDiscountDependence {

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

		double[] adblPrincipalDiscount = new double[] {
			0.12,
			0.13,
			0.14,
			0.15,
			0.16,
			0.18,
			0.21,
			0.24,
			0.27,
			0.30,
			0.33,
			0.36,
			0.39,
			0.42,
			0.45,
			0.50,
			0.55,
			0.60,
			0.66,
			0.72,
			0.78,
			0.84,
			0.91,
			0.98
		};

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

		Almgren2003Estimator a2003e = new Almgren2003Estimator (
			a2003tt,
			a2003p
		);

		System.out.println();

		System.out.println ("\t|------------------------------------------------------------------||");

		System.out.println ("\t|         OPTIMAL MEASURES PRINCIPAL DISCOUNT DEPENDENCE           ||");

		System.out.println ("\t|------------------------------------------------------------------||");

		System.out.println ("\t|    L -> R:                                                       ||");

		System.out.println ("\t|            - Principal Discount                                  ||");

		System.out.println ("\t|            - Gross Profit Expectation                            ||");

		System.out.println ("\t|            - Gross Profit Standard Deviation                     ||");

		System.out.println ("\t|            - Gross Returns Expectation                           ||");

		System.out.println ("\t|            - Gross Returns Standard Deviation                    ||");

		System.out.println ("\t|            - Information Ratio                                   ||");

		System.out.println ("\t|            - Optimal Information Ratio                           ||");

		System.out.println ("\t|            - Optimal Information Ratio Horizon                   ||");

		System.out.println ("\t|------------------------------------------------------------------||");

		for (double dblPrincipalDiscount : adblPrincipalDiscount)
			System.out.println (
				"\t|" +
				FormatUtil.FormatDouble (dblPrincipalDiscount, 1, 2, 1.) + " |" +
				FormatUtil.FormatDouble (a2003e.principalMeasure (dblPrincipalDiscount).mean(), 5, 0, 1.) + " |" +
				FormatUtil.FormatDouble (Math.sqrt (a2003e.principalMeasure (dblPrincipalDiscount).variance()), 6, 0, 1.) + " |" +
				FormatUtil.FormatDouble (a2003e.horizonPrincipalMeasure (dblPrincipalDiscount).mean(), 5, 0, 1.) + " |" +
				FormatUtil.FormatDouble (Math.sqrt (a2003e.horizonPrincipalMeasure (dblPrincipalDiscount).variance()), 5, 0, 1.) + " |" +
				FormatUtil.FormatDouble (a2003e.informationRatio (dblPrincipalDiscount), 1, 4, 1.) + " |" +
				FormatUtil.FormatDouble (a2003e.optimalInformationRatio (dblPrincipalDiscount), 1, 4, 1.) + " |" +
				FormatUtil.FormatDouble (a2003e.optimalInformationRatioHorizon (dblPrincipalDiscount), 1, 4, 1.) + " ||"
			);

		System.out.println ("\t|------------------------------------------------------------------||");
	}
}
