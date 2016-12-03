
package org.drip.sample.almgren2003;

import org.drip.execution.capture.*;
import org.drip.execution.dynamics.TradingEnhancedVolatilityParameters;
import org.drip.execution.generator.Almgren2003ConstantTradingEnhanced;
import org.drip.execution.impact.ParticipationRateLinear;
import org.drip.execution.optimum.EfficientTradingTrajectoryContinuous;
import org.drip.execution.strategy.DiscreteTradingTrajectory;
import org.drip.function.definition.R1ToR1;
import org.drip.measure.gaussian.R1UnivariateNormal;
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
 * ConstantTradingEnhancedVolatility demonstrates the Generation of the Optimal Trading Trajectory under the
 * 	Condition of Constant Trading Enhanced Volatility. The References are:
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

public class ConstantTradingEnhancedVolatility {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblEta = 5.e-06;
		double dblAlpha = 1.;
		double dblSigma = 1.;
		double dblLambda = 1.e-05;
		double dblX = 100000.;
		double dblT = 5.;
		int iNumInterval = 50000;

		TradingEnhancedVolatilityParameters tevp = new TradingEnhancedVolatilityParameters (
			dblSigma,
			ParticipationRateLinear.SlopeOnly (
				dblEta
			),
			new ParticipationRateLinear (
				dblAlpha,
				0.
			)
		);

		Almgren2003ConstantTradingEnhanced ctes = Almgren2003ConstantTradingEnhanced.Standard (
			dblX,
			dblT,
			tevp,
			dblLambda
		);

		EfficientTradingTrajectoryContinuous ectt = (EfficientTradingTrajectoryContinuous) ctes.generate();

		R1ToR1 r1ToR1Holdings = ectt.holdings();

		double[] adblHoldings = new double[iNumInterval];
		double[] adblExecutionTime = new double[iNumInterval];

		for (int i = 1; i <= iNumInterval; ++i) {
			adblExecutionTime[i - 1] = dblT * i / iNumInterval;

			adblHoldings[i - 1] = r1ToR1Holdings.evaluate (adblExecutionTime[i - 1]);
		}

		DiscreteTradingTrajectory dtt = DiscreteTradingTrajectory.Standard (
			adblExecutionTime,
			adblHoldings
		);

		TrajectoryShortfallEstimator tse = new TrajectoryShortfallEstimator (dtt);

		R1UnivariateNormal r1un = tse.totalCostDistributionSynopsis (tevp);

		double[] adblTradeList = dtt.tradeList();

		for (int i = 1; i < adblExecutionTime.length; ++i) {
			System.out.println ("\t| " +
				FormatUtil.FormatDouble (adblExecutionTime[i], 1, 4, 1.) + " => " +
				FormatUtil.FormatDouble (adblHoldings[i] / dblX, 2, 4, 100.) + "% | " +
				FormatUtil.FormatDouble (adblTradeList[i - 1] / dblX, 1, 4, 100.) + "% ||"
			);
		}

		System.out.println ("\t|---------------------------------||");

		System.out.println ("\n\t|--------------------------------------------------------------||");

		System.out.println ("\t|  TRANSACTION COST RECONCILIATION: EXPLICIT vs. ALMGREN 2003  ||");

		System.out.println ("\t|--------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation         : " +
			FormatUtil.FormatDouble (r1un.mean(), 6, 1, 1.) + " | " +
			FormatUtil.FormatDouble (ectt.transactionCostExpectation(), 6, 1, 1.) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance (X 10^-06) : " +
			FormatUtil.FormatDouble (r1un.variance(), 6, 1, 1.e-06) + " | " +
			FormatUtil.FormatDouble (ectt.transactionCostVariance(), 6, 1, 1.e-06) + " ||"
		);

		System.out.println ("\t|--------------------------------------------------------------||");
	}
}
