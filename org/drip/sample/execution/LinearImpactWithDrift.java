
package org.drip.sample.execution;

import org.drip.execution.capture.LinearImpactTrajectoryEstimator;
import org.drip.execution.dynamics.LinearExpectationParameters;
import org.drip.execution.generator.OptimalTrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimizer.MeanVarianceObjectiveUtility;
import org.drip.execution.optimum.EfficientTradingTrajectory;
import org.drip.execution.parameters.*;
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
 * LinearImpactWithDrift generates the Trade/Holdings List of Optimal Execution Schedule based on the
 *  Evolution Walk Parameters specified. The Generation follows a Numerical Optimizer Scheme, as opposed to
 *  the Almgren-Chriss Closed Form; it includes the Impact of Drift. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 *
 * 	- Chan, L. K. C., and J. Lakonishak (1995): The Behavior of Stock Prices around Institutional Trades,
 * 		Journal of Finance, 50, 1147-1174.
 *
 * 	- Keim, D. B., and A. Madhavan (1997): Transaction Costs and Investment Style: An Inter-exchange
 * 		Analysis of Institutional Equity Trades, Journal of Financial Economics, 46, 265-292.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class LinearImpactWithDrift {

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblX = 1000000.;
		double dblT = 5.;
		int iN = 5;
		double dblAnnualVolatility = 0.30;
		double dblAnnualReturns = 0.10;
		double dblBidAsk = 0.125;
		double dblDailyVolume = 5.e06;
		double dblDailyVolumePermanentImpact = 0.1;
		double dblDailyVolumeTemporaryImpact = 0.01;
		double dblLambdaU = 1.e-06;

		ArithmeticPriceDynamicsSettings amc = ArithmeticPriceDynamicsSettings.FromAnnualReturnsSettings (
			dblAnnualReturns,
			dblAnnualVolatility,
			0.,
			dblS0
		);

		double dblAlpha = amc.drift();

		double dblSigma = amc.volatility();

		PriceMarketImpactLinear ami = new PriceMarketImpactLinear (
			new AssetTransactionSettings (
				dblS0,
				dblDailyVolume,
				dblBidAsk
			),
			dblDailyVolumePermanentImpact,
			dblDailyVolumeTemporaryImpact
		);

		ParticipationRateLinear prlPermanent = (ParticipationRateLinear) ami.permanentTransactionFunction();

		ParticipationRateLinear prlTemporary = (ParticipationRateLinear) ami.temporaryTransactionFunction();

		TradingTrajectoryControl ttc = TradingTrajectoryControl.FixedInterval (
			new OrderSpecification (
				dblX,
				dblT
			),
			iN
		);

		LinearExpectationParameters lipe = new LinearExpectationParameters (
			new ArithmeticPriceDynamicsSettings (
				dblAlpha,
				dblSigma,
				0.
			),
			prlPermanent,
			prlTemporary
		);

		EfficientTradingTrajectory ett = new OptimalTrajectoryScheme (
			ttc,
			lipe,
			new MeanVarianceObjectiveUtility (dblLambdaU)
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

		System.out.println ("\t| Annual Volatility             :" + FormatUtil.FormatDouble (dblAnnualVolatility, 1, 0, 100.) + "%");

		System.out.println ("\t| Annual Growth                 :" + FormatUtil.FormatDouble (dblAnnualReturns, 1, 0, 100.) + "%");

		System.out.println ("\t| Bid-Ask Spread                : " + dblBidAsk);

		System.out.println ("\t| Daily Volume                  : " + dblDailyVolume);

		System.out.println ("\t| Daily Volume Temporary Impact : " + dblDailyVolumeTemporaryImpact);

		System.out.println ("\t| Daily Volume Permanent Impact : " + dblDailyVolumePermanentImpact);

		System.out.println ("\t| Daily Volume 5 million Shares : " + prlPermanent.slope());

		System.out.println ("\t| Static Holdings 11,000 Shares : " + dblLambdaU);

		System.out.println ("\t|");

		System.out.println (
			"\t| Daily Volatility              : " +
			FormatUtil.FormatDouble (dblSigma, 1, 4, 1.)
		);

		System.out.println (
			"\t| Daily Returns                 : " +
			FormatUtil.FormatDouble (dblAlpha, 1, 4, 1.)
		);

		System.out.println ("\t| Temporary Impact Fixed Offset :  " + prlTemporary.offset());

		System.out.println ("\t| Eta                           :  " + prlTemporary.slope());

		System.out.println ("\t| Gamma                         :  " + prlPermanent.slope());

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

		System.out.println ("\n\t|--------------------------------------------------------------||");

		System.out.println ("\t| TRANSACTION COST RECONCILIATION: OPTIMAL vs. EXPLICIT LINEAR ||");

		System.out.println ("\t|--------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation         : " +
			FormatUtil.FormatDouble (r1un.mean(), 6, 1, 1.) + " | " +
			FormatUtil.FormatDouble (ett.transactionCostExpectation(), 6, 1, 1.) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance (X 10^-06) : " +
			FormatUtil.FormatDouble (r1un.variance(), 6, 1, 1.e-06) + " | " +
			FormatUtil.FormatDouble (ett.transactionCostVariance(), 6, 1, 1.e-06) + " ||"
		);

		System.out.println ("\t|--------------------------------------------------------------||");
	}
}
