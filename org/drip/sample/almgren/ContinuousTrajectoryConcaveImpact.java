
package org.drip.sample.almgren;

import org.drip.execution.dynamics.Almgren2003Parameters;
import org.drip.execution.generator.Almgren2003TrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.Almgren2003TradingTrajectory;
import org.drip.execution.parameters.*;
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
 * ContinuousTrajectoryConcaveImpact reconciles the Characteristic Times of the Optimal Continuous Trading
 *  Trajectory resulting from the Application of the Almgren (2003) Scheme to a Concave Power Law Temporary
 *  Market Impact Function. The Power Exponent Considered here is k=0.5. The References are:
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
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ContinuousTrajectoryConcaveImpact {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblDailyVolume = 1000000.;
		double dblBidAskSpread = 0.;
		double dblTemporaryImpactFactor = 0.01;
		double dblK = 0.5;
		double dblGamma = 0.;
		double dblDailyVolumeExecutionFactor = 0.1;
		double dblDrift = 0.;
		double dblVolatility = 1.;
		double dblSerialCorrelation = 0.;
		double dblX = 100000.;
		double dblFinishTime = 1.;
		int iNumInterval = 13;

		double[] adblLambda = new double[] {
			1.e-03,
			1.e-04,
			1.e-05,
			1.e-06,
			1.e-07
		};

		double[][] aadblAlmgren2003Reconciler = new double[][] {
			{0.02, 221.,  11.},
			{0.09, 103.,  23.},
			{0.40,  48.,  49.},
			{1.84,  22., 105.},
			{8.55,  10., 226.}
		};

		ArithmeticPowerImpact apim = new ArithmeticPowerImpact (
			new AssetTransactionSettings (
				dblS0,
				dblDailyVolume,
				dblBidAskSpread
			),
			dblTemporaryImpactFactor,
			dblDailyVolumeExecutionFactor,
			dblK
		);

		Almgren2003Parameters a2003pe = new Almgren2003Parameters (
			dblDrift,
			dblVolatility,
			dblSerialCorrelation,
			new ParticipationRateLinear (
				0.,
				dblGamma
			),
			(ParticipationRatePower) apim.temporaryTransactionFunction()
		);

		System.out.println ("\n\t|-------------------------------------------||");

		System.out.println ("\t|                  COMPUTED                 ||");

		System.out.println ("\t|-------------------------------------------||");

		System.out.println ("\t| LAMBDAINV || T_STAR | COST_EXP | COST_STD ||");

		System.out.println ("\t|-------------------------------------------||");

		for (int i = 0; i < adblLambda.length; ++i) {
			Almgren2003TrajectoryScheme a2003ts = Almgren2003TrajectoryScheme.Standard (
				dblX,
				dblFinishTime,
				iNumInterval,
				a2003pe,
				adblLambda[i]
			);

			Almgren2003TradingTrajectory a2003tt = (Almgren2003TradingTrajectory) a2003ts.generate();

			System.out.println ("\t|  " +
				FormatUtil.FormatDouble (1. / adblLambda[i], 5, 0, 1.e-03) + "   || " +
				FormatUtil.FormatDouble (a2003tt.characteristicTime(), 1, 2, 1.) + "      " +
				FormatUtil.FormatDouble (a2003tt.transactionCostExpectation(), 3, 0, 1.e-03) + "       " +
				FormatUtil.FormatDouble (Math.sqrt (a2003tt.transactionCostVariance()), 3, 0, 1.e-03) + "   ||"
			);
		}

		System.out.println ("\t|-------------------------------------------||");

		System.out.println ("\n\t|-------------------------------------------||");

		System.out.println ("\t|               ALMGREN (2003)              ||");

		System.out.println ("\t|-------------------------------------------||");

		System.out.println ("\t| LAMBDAINV || T_STAR | COST_EXP | COST_STD ||");

		System.out.println ("\t|-------------------------------------------||");

		for (int i = 0; i < adblLambda.length; ++i)
			System.out.println ("\t|  " +
				FormatUtil.FormatDouble (1. / adblLambda[i], 5, 0, 1.e-03) + "   || " +
				FormatUtil.FormatDouble (aadblAlmgren2003Reconciler[i][0], 1, 2, 1.) + "      " +
				FormatUtil.FormatDouble (aadblAlmgren2003Reconciler[i][1], 3, 0, 1.) + "       " +
				FormatUtil.FormatDouble (aadblAlmgren2003Reconciler[i][2], 3, 0, 1.) + "   ||"
			);

		System.out.println ("\t|-------------------------------------------||");
	}
}
