
package org.drip.sample.almgrenchriss;

import org.drip.execution.capture.*;
import org.drip.execution.dynamics.LinearExpectationParameters;
import org.drip.execution.generator.AC2000TrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.AC2000TradingTrajectory;
import org.drip.execution.parameters.*;
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
 * OptimalTrajectoryNoDrift demonstrates the Generation of the Optimal Trading Trajectory in accordance with
 *  the Specification of Almgren and Chriss (2000) for the given Risk Aversion Parameter without the Asset
 *  Drift. The References are:
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

public class OptimalTrajectoryNoDrift {

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

		AssetReturnsSettings amc = new AssetReturnsSettings (
			dblAnnualReturns,
			dblAnnualVolatility,
			0.
		);

		double dblAlpha = amc.dailyArithmeticDrift (dblS0);

		double dblSigma = amc.dailyArithmeticVolatility (dblS0);

		ArithmeticLinearImpact ami = new ArithmeticLinearImpact (
			new AssetTransactionSettings (
				dblS0,
				dblDailyVolume,
				dblBidAsk
			),
			dblDailyVolumePermanentImpact,
			dblDailyVolumeTemporaryImpact
		);

		double dblEta = ami.temporarySlope();

		double dblGamma = ami.permanentSlope();

		double dblEpsilon = ami.temporaryOffset();

		LinearExpectationParameters lipe = new LinearExpectationParameters (
			0.,
			dblSigma,
			0.,
			new ParticipationRateLinear (
				0.,
				dblGamma
			),
			new ParticipationRateLinear (
				dblEpsilon,
				dblEta
			)
		);

		AC2000TrajectoryScheme acts = AC2000TrajectoryScheme.Standard (
			dblX,
			dblT,
			iN,
			lipe,
			dblLambdaU
		);

		AC2000TradingTrajectory tt = (AC2000TradingTrajectory) acts.generate();

		double[] adblExecutionTimeNode = tt.executionTimeNode();

		double[] adblTradeList = tt.tradeList();

		double[] adblHoldings = tt.holdings();

		LinearImpactTrajectoryEstimator lite = new LinearImpactTrajectoryEstimator (tt);

		TrajectoryShortfallAggregate tsa = lite.totalCostDistributionDetail (lipe);

		double[] adblIncrementalPermanentImpact = tsa.incrementalPermanentImpactExpectation();

		double[] adblIncrementalTemporaryImpact = tsa.incrementalTemporaryImpactExpectation();

		double[] adblCumulativePermanentImpact = tsa.cumulativePermanentImpactExpectation();

		double[] adblCumulativeTemporaryImpact = tsa.cumulativeTemporaryImpactExpectation();

		double[] adblIncrementalShortfallVariance = tsa.incrementalVariance();

		double[] adblCumulativeShortfallVariance = tsa.cumulativeVariance();

		double[] adblIncrementalShortfallMean = tsa.incrementalExpectation();

		double[] adblCumulativeShortfallMean = tsa.cumulativeExpectation();

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

		System.out.println ("\t| Daily Volume 5 million Shares : " + dblGamma);

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

		System.out.println ("\t| Temporary Impact Fixed Offset :  " + dblEpsilon);

		System.out.println ("\t| Eta                           :  " + dblEta);

		System.out.println ("\t| Gamma                         :  " + dblGamma);

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\n\t|-----------------------------||");

		System.out.println ("\t| AC2000 Optimal Trajectory   ||");

		System.out.println ("\t| ------ ------- ----------   ||");

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

		System.out.println ("\n\t|-----------------------------------------------------------||");

		System.out.println ("\t|               PERIOD LEVEL COST DISTRIBUTION              ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |          MEAN         |        VARIANCE       ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |    INCR   |    CUML   |    INCR   |    CUML   ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		for (int i = 0; i < adblIncrementalShortfallMean.length; ++i)
			System.out.println (
				"\t| PERIOD #" + (i + 1) + " | " +
				FormatUtil.FormatDouble (adblIncrementalShortfallMean[i], 6, 1, 1.) + " | " +
				FormatUtil.FormatDouble (adblCumulativeShortfallMean[i], 6, 1, 1.) + " | " +
				FormatUtil.FormatDouble (adblIncrementalShortfallVariance[i], 6, 1, 1.e-06) + " | " +
				FormatUtil.FormatDouble (adblCumulativeShortfallVariance[i], 6, 1, 1.e-06) + " ||"
			);

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\n\t|-----------------------------------------------------------||");

		System.out.println ("\t|            PERIOD LEVEL COST IMPACT CONTRIBUTION          ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |       PERMANENT       |       TEMPORARY       ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\t|  PERIOD   |    INCR   |    CUML   |    INCR   |    CUML   ||");

		System.out.println ("\t|-----------------------------------------------------------||");

		for (int i = 0; i < adblIncrementalPermanentImpact.length; ++i)
			System.out.println (
				"\t| PERIOD #" + (i + 1) + " | " +
				FormatUtil.FormatDouble (adblIncrementalPermanentImpact[i], 6, 1, 1.) + " | " +
				FormatUtil.FormatDouble (adblCumulativePermanentImpact[i], 6, 1, 1.) + " | " +
				FormatUtil.FormatDouble (adblIncrementalTemporaryImpact[i], 6, 1, 1.) + " | " +
				FormatUtil.FormatDouble (adblCumulativeTemporaryImpact[i], 6, 1, 1.) + " ||"
			);

		System.out.println ("\t|-----------------------------------------------------------||");

		System.out.println ("\n\t|--------------------------------------------------------------||");

		System.out.println ("\t| TRANSACTION COST RECONCILIATION: AC2000 vs. EXPLICIT LINEAR  ||");

		System.out.println ("\t|--------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation         : " +
			FormatUtil.FormatDouble (r1un.mean(), 6, 1, 1.) + " | " +
			FormatUtil.FormatDouble (tt.transactionCostExpectation(), 6, 1, 1.) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance (X 10^-06) : " +
			FormatUtil.FormatDouble (r1un.variance(), 6, 1, 1.e-06) + " | " +
			FormatUtil.FormatDouble (tt.transactionCostVariance(), 6, 1, 1.e-06) + " ||"
		);

		System.out.println ("\t|--------------------------------------------------------------||");

		System.out.println ("\n\t|-----------------------||");

		System.out.println ("\t|  AC2000 METRICS DUMP  ||");

		System.out.println ("\t|-----------------------||");

		System.out.println ("\t| Kappa       : " + FormatUtil.FormatDouble (tt.kappa(), 1, 4, 1.) + " ||");

		System.out.println ("\t| Kappa Tilda : " + FormatUtil.FormatDouble (tt.kappaTilda(), 1, 4, 1.) + " ||");

		System.out.println ("\t| Half Life   : " + FormatUtil.FormatDouble (tt.halfLife(), 1, 4, 1.) + " ||");

		System.out.println ("\t|-----------------------||");
	}
}
