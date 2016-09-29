
package org.drip.sample.lvar;

import org.drip.execution.capture.LinearImpactTrajectoryEstimator;
import org.drip.execution.dynamics.LinearExpectationParameters;
import org.drip.execution.generator.OptimalTrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimizer.PowerVarianceObjectiveUtility;
import org.drip.execution.optimum.EfficientTradingTrajectory;
import org.drip.execution.strategy.*;
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
 * OptimalTrajectoryNoDrift generates the Trade/Holdings List of Optimal Execution Schedule based on the
 *  Evolution Walk Parameters specified according to the Liquidity VaR Optimal Objective Function, exclusive
 *  of Drift. The Generation follows a Numerical Optimizer Scheme. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk, 3 (2),
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Non-linear Impact Functions and Trading Enhanced Risk,
 * 		Applied Mathematical Finance, 10, 1-18.
 *
 * 	- Artzner, P., F. Delbaen, J. M. Eber, and D. Heath (1999): Coherent Measures of Risk, Mathematical
 * 		Finance, 9, 203-228.
 *
 * 	- Basak, S., and A. Shapiro (2001): Value-at-Risk Based Risk Management: Optimal Policies and Asset
 * 		Prices, Review of Financial Studies, 14, 371-405.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OptimalTrajectoryNoDrift {

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblSigma = 0.9487;
		double dblAlpha = 0.02;
		double dblEpsilon = 0.0625;
		double dblGamma = 2.5e-07;
		double dblEta = 2.5e-06;
		double dblConfidenceLevel = 0.90;

		double dblX = 1000000.;
		double dblT = 5.;
		int iN = 5;

		double dblLambdaV = R1UnivariateNormal.Standard().confidenceInterval (dblConfidenceLevel);

		TradingTrajectoryControl ttc = TradingTrajectoryControl.FixedInterval (
			new OrderSpecification (
				dblX,
				dblT
			),
			iN
		);

		LinearExpectationParameters lipe = new LinearExpectationParameters (
			0.00,
			dblSigma,
			0.,
			new TradeRateLinear (
				0.,
				dblGamma
			),
			new TradeRateLinear (
				dblEpsilon,
				dblEta
			)
		);

		EfficientTradingTrajectory ett = new OptimalTrajectoryScheme (
			ttc,
			lipe,
			PowerVarianceObjectiveUtility.LiquidityVaR (dblLambdaV)
		).generate();

		double[] adblExecutionTimeNode = ett.executionTimeNode();

		double[] adblTradeList = ett.tradeList();

		double[] adblHoldings = ett.holdings();

		LinearImpactTrajectoryEstimator lite = new LinearImpactTrajectoryEstimator (ett);

		R1UnivariateNormal r1un = lite.totalCostDistributionSynopsis (lipe);

		System.out.println ("\n\t|---------------------------------------------||");

		System.out.println ("\t| ALMGREN-CHRISS TRAJECTORY GENERATOR INPUTS  ||");

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\t| Initial Stock Price           : " + dblS0);

		System.out.println ("\t| Initial Holdings              : " + dblX);

		System.out.println ("\t| Liquidation Time              : " + dblT);

		System.out.println ("\t| Number of Time Periods        : " + iN);

		System.out.println ("\t| Daily Volume 5 million Shares : " + dblGamma);

		System.out.println ("\t| VaR Confidence Level          :" + FormatUtil.FormatDouble (dblConfidenceLevel, 2, 2, 100.) + "%");

		System.out.println ("\t| VaR Based Risk Aversion       : " + dblLambdaV);

		System.out.println ("\t|");

		System.out.println (
			"\t| Daily Volatility              : " +
			FormatUtil.FormatDouble (dblSigma, 1, 4, 1.)
		);

		System.out.println (
			"\t| Daily Returns                 : " +
			FormatUtil.FormatDouble (dblAlpha, 1, 4, 1.)
		);

		System.out.println ("\t| Temporary Impact Fixed Offset :  " + dblEpsilon);

		System.out.println ("\t| Eta                           :  " + dblEta);

		System.out.println ("\t| Gamma                         :  " + dblGamma);

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\n\t|-----------------------------||");

		System.out.println ("\t| Optimal Trading Trajectory  ||");

		System.out.println ("\t| ------- ------- ----------  ||");

		System.out.println ("\t|     L -> R:                 ||");

		System.out.println ("\t|        Time Node            ||");

		System.out.println ("\t|        Holdings             ||");

		System.out.println ("\t|        Trade Amount         ||");

		System.out.println ("\t|-----------------------------||");

		for (int i = 0; i <= iN; ++i) {
			if (i == 0)
				System.out.println (
					"\t|" + FormatUtil.FormatDouble (adblExecutionTimeNode[i], 1, 0, 1.) + " => " +
					FormatUtil.FormatDouble (adblHoldings[i], 7, 1, 1.) + " | " +
					FormatUtil.FormatDouble (0., 6, 1, 1.) + " ||"
				);
			else
				System.out.println (
					"\t|" + FormatUtil.FormatDouble (adblExecutionTimeNode[i], 1, 0, 1.) + " => " +
					FormatUtil.FormatDouble (adblHoldings[i], 7, 1, 1.) + " | " +
					FormatUtil.FormatDouble (adblTradeList[i - 1], 6, 1, 1.) + " ||"
				);
		}

		System.out.println ("\t|-----------------------------||");

		System.out.println ("\n\t|----------------------------------------------------------------||");

		System.out.println ("\t|  TRANSACTION COST RECONCILIATION: OPTIMAL vs. EXPLICIT LINEAR  ||");

		System.out.println ("\t|----------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation         : " +
			FormatUtil.FormatDouble (r1un.mean(), 7, 1, 1.) + " | " +
			FormatUtil.FormatDouble (ett.transactionCostExpectation(), 7, 1, 1.) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance (X 10^-06) : " +
			FormatUtil.FormatDouble (r1un.variance(), 7, 1, 1.e-06) + " | " +
			FormatUtil.FormatDouble (ett.transactionCostVariance(), 7, 1, 1.e-06) + " ||"
		);

		System.out.println ("\t|----------------------------------------------------------------||");
	}
}
