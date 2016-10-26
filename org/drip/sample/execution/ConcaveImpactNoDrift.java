
package org.drip.sample.execution;

import org.drip.execution.dynamics.Almgren2003Parameters;
import org.drip.execution.generator.OptimalTrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimizer.MeanVarianceObjectiveUtility;
import org.drip.execution.optimum.EfficientTradingTrajectory;
import org.drip.execution.parameters.*;
import org.drip.execution.strategy.*;
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
 * ConcaveImpactNoDrift generates the Trade/Holdings List of Optimal Execution Schedule based on the Concave
 * 	Power Law Evolution Walk Parameters specified. The Generation follows a Numerical Optimizer Scheme, as
 *  opposed to the Closed Form; it also excludes the Impact of Drift. The References are:
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

public class ConcaveImpactNoDrift {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblDailyVolume = 1000000.;
		double dblBidAskSpread = 0.;
		double dblPermanentImpactFactor = 0.1;
		double dblTemporaryImpactFactor = 0.01;
		double dblK = 0.5;
		double dblDailyVolumeExecutionFactor = 0.1;
		double dblDrift = 0.;
		double dblVolatility = 1.;
		double dblX = 100000.;
		double dblFinishTime = 0.04;
		int iNumInterval = 2;
		double dblLambdaU = 3.e-03;

		ArithmeticPowerMarketImpact apim = new ArithmeticPowerMarketImpact (
			dblS0,
			dblDailyVolume,
			dblBidAskSpread,
			dblPermanentImpactFactor,
			dblTemporaryImpactFactor,
			dblDailyVolumeExecutionFactor,
			dblK
		);

		Almgren2003Parameters a2003pe = new Almgren2003Parameters (
			dblDrift,
			dblVolatility,
			0.,
			new TradeRateLinear (
				0.,
				0.
			),
			new TradeRatePower (
				apim.temporaryImpactConstant(),
				dblK
			)
		);

		TradingTrajectoryControl ttc = TradingTrajectoryControl.FixedInterval (
			new OrderSpecification (
				dblX,
				dblFinishTime
			),
			iNumInterval
		);

		EfficientTradingTrajectory ett = new OptimalTrajectoryScheme (
			ttc,
			a2003pe,
			new MeanVarianceObjectiveUtility (dblLambdaU)
		).generate();

		double[] adblExecutionTimeNode = ett.executionTimeNode();

		double[] adblTradeList = ett.tradeList();

		double[] adblHoldings = ett.holdings();

		System.out.println ("\n\t|-----------------------------||");

		System.out.println ("\t| Optimal Trading Trajectory  ||");

		System.out.println ("\t| ------- ------- ----------  ||");

		System.out.println ("\t|     L -> R:                 ||");

		System.out.println ("\t|        Time Node            ||");

		System.out.println ("\t|        Holdings             ||");

		System.out.println ("\t|        Trade Amount         ||");

		System.out.println ("\t|-----------------------------||");

		for (int i = 0; i <= iNumInterval; ++i) {
			if (i == 0)
				System.out.println (
					"\t|" + FormatUtil.FormatDouble (adblExecutionTimeNode[i], 1, 3, 1.) + " =>" +
					FormatUtil.FormatDouble (adblHoldings[i], 7, 1, 1.) + " | " +
					FormatUtil.FormatDouble (0., 6, 1, 1.) + " ||"
				);
			else
				System.out.println (
					"\t|" + FormatUtil.FormatDouble (adblExecutionTimeNode[i], 1, 3, 1.) + " =>" +
					FormatUtil.FormatDouble (adblHoldings[i], 7, 1, 1.) + " | " +
					FormatUtil.FormatDouble (adblTradeList[i - 1], 6, 1, 1.) + " ||"
				);
		}

		System.out.println ("\t|-----------------------------||");
	}
}
