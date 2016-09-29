
package org.drip.sample.almgrenchriss;

import org.drip.execution.dynamics.LinearExpectationParameters;
import org.drip.execution.generator.AC2000TrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.AC2000TradingTrajectory;
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
 * EfficientFrontierNoDrift constructs the Efficient Frontier over a Sequence of Risk Aversion Parameters for
 *  Optimal Trading Trajectories computed in accordance with the Specification of Almgren and Chriss (2000),
 *  and calculates the corresponding Execution Half Life and the Trajectory Penalty without regard to the
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

public class EfficientFrontierNoDrift {

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblX = 1000000.;
		double dblT = 5.;
		int iN = 5;
		double dblSigma = 0.95;
		double dblAlpha = 0.02;
		double dblEpsilon = 0.0625;
		double dblGamma = 2.5e-07;
		double dblEta = 2.5e-06;
		double[] adblLambdaShortEndU = {
			0.001e-06,
			0.002e-06,
			0.003e-06,
			0.004e-06,
			0.005e-06,
			0.006e-06,
			0.007e-06,
			0.008e-06,
			0.009e-06
		};
		double[] adblLambdaLongEndU = {
			0.250e-06,
			0.500e-06,
			0.750e-06,
			1.000e-06,
			1.250e-06,
			1.500e-06,
			1.750e-06,
			2.000e-06,
			2.250e-06,
			2.500e-06,
			2.750e-06,
			3.000e-06,
			3.250e-06,
			3.500e-06,
			3.750e-06,
			4.000e-06
		};

		LinearExpectationParameters lipe = new LinearExpectationParameters (
			dblAlpha,
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

		System.out.println ("\n\t|---------------------------------------------||");

		System.out.println ("\t| ALMGREN-CHRISS TRAJECTORY GENERATOR INPUTS  ||");

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\t| Initial Stock Price           : " + dblS0);

		System.out.println ("\t| Initial Holdings              : " + dblX);

		System.out.println ("\t| Liquidation Time              : " + dblT);

		System.out.println ("\t| Number of Time Periods        : " + iN);

		System.out.println ("\t| 30% Annual Volatility         : " + dblSigma);

		System.out.println ("\t| 10% Annual Growth             : " + dblAlpha);

		System.out.println ("\t| Bid-Ask Spread = 1/8          : " + dblEpsilon);

		System.out.println ("\t| Daily Volume 5 million Shares : " + dblGamma);

		System.out.println ("\t| Impact at 1% of Market        : " + dblEta);

		System.out.println ("\t|---------------------------------------------||");

		System.out.println ("\n\t|------------------------------------------------------------||");

		System.out.println ("\t|      SHORT END COST DISTRIBUTION, PENALTY, AND DECAY       ||");

		System.out.println ("\t|------------------------------------------------------------||");

		System.out.println ("\t|       LAMBDA      |   MEAN  | SIGMA^2 | PENALTY | HALFLIFE ||");

		System.out.println ("\t|------------------------------------------------------------||");

		for (double dblLambda : adblLambdaShortEndU) {
			AC2000TradingTrajectory tt = (AC2000TradingTrajectory) AC2000TrajectoryScheme.Standard (
				dblX,
				dblT,
				iN,
				lipe,
				dblLambda
			).generate();
	
			String strHoldings = "\t| [LAMBDA = " + FormatUtil.FormatDouble (dblLambda, 1, 3, dblX) + "]";

			double dblTransactionCostExpectation = tt.transactionCostExpectation();

			double dblTransactionCostVariance = tt.transactionCostVariance();

			double dblTransactionCostPenalty = dblTransactionCostExpectation + dblLambda * dblTransactionCostVariance;

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (dblTransactionCostExpectation / dblX, 1, 4, 1.);

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (dblTransactionCostVariance / dblX / dblX, 1, 4, 1.);

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (dblTransactionCostPenalty / dblX, 1, 4, 1.);

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (tt.halfLife(), 2, 2, 1.);

			System.out.println (strHoldings + "   ||");
		}

		System.out.println ("\t|------------------------------------------------------------||");

		System.out.println ("\n\t|------------------------------------------------------------||");

		System.out.println ("\t|       LONG END COST DISTRIBUTION, PENALTY, AND DECAY       ||");

		System.out.println ("\t|------------------------------------------------------------||");

		System.out.println ("\t|       LAMBDA      |   MEAN  | SIGMA^2 | PENALTY | HALFLIFE ||");

		System.out.println ("\t|------------------------------------------------------------||");

		for (double dblLambda : adblLambdaLongEndU) {
			AC2000TradingTrajectory tt = (AC2000TradingTrajectory) AC2000TrajectoryScheme.Standard (
				dblX,
				dblT,
				iN,
				lipe,
				dblLambda
			).generate();
	
			String strHoldings = "\t| [LAMBDA = " + FormatUtil.FormatDouble (dblLambda, 1, 3, dblX) + "]";

			double dblTransactionCostExpectation = tt.transactionCostExpectation();

			double dblTransactionCostVariance = tt.transactionCostVariance();

			double dblTransactionCostPenalty = dblTransactionCostExpectation + dblLambda * dblTransactionCostVariance;

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (dblTransactionCostExpectation / dblX, 1, 4, 1.);

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (dblTransactionCostVariance / dblX / dblX, 1, 4, 1.);

			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (dblTransactionCostPenalty / dblX, 1, 4, 1.);

			strHoldings = strHoldings + " |  " + FormatUtil.FormatDouble (tt.halfLife(), 1, 2, 1.);

			System.out.println (strHoldings + "   ||");
		}

		System.out.println ("\t|------------------------------------------------------------||");
	}
}