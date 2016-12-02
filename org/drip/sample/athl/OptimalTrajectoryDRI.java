
package org.drip.sample.athl;

import org.drip.execution.athl.DynamicsParameters;
import org.drip.execution.dynamics.Almgren2003Parameters;
import org.drip.execution.generator.Almgren2003TrajectoryScheme;
import org.drip.execution.optimum.Almgren2003PowerImpactContinuous;
import org.drip.execution.parameters.AssetFlowSettings;
import org.drip.execution.strategy.DiscreteTradingTrajectory;
import org.drip.function.definition.R1ToR1;
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
 * OptimalTrajectoryDRI demonstrates the Trade Scheduling using the Equity Market Impact Functions determined
 *  empirically by Almgren, Thum, Hauptmann, and Li (2005), using the Parameterization of Almgren (2003) for
 *  DRI. The References are:
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

public class OptimalTrajectoryDRI {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strAssetName = "DRI";
		double dblAverageDailyVolume = 1929000.;
		double dblSharesOutstanding = 168000000.;
		double dblDailyVolatility = 2.26;

		double dblTradeSize = 192900.;
		double dblTradeTime = 1.;
		int iNumInterval = 20;

		double dblRiskAversion = 1.e-02;

		AssetFlowSettings afs = new AssetFlowSettings (
			strAssetName,
			dblAverageDailyVolume,
			dblSharesOutstanding,
			dblDailyVolatility
		);

		Almgren2003Parameters a2003p = new DynamicsParameters (afs).almgren2003();

		Almgren2003TrajectoryScheme a2003ts = Almgren2003TrajectoryScheme.Standard (
			dblTradeSize,
			dblTradeTime,
			a2003p,
			dblRiskAversion
		);

		Almgren2003PowerImpactContinuous a2003tt = (Almgren2003PowerImpactContinuous) a2003ts.generate();

		R1ToR1 r1ToR1Holdings = a2003tt.holdings();

		double[] adblHoldings = new double[iNumInterval];
		double[] adblExecutionTime = new double[iNumInterval];

		for (int i = 1; i <= iNumInterval; ++i) {
			adblExecutionTime[i - 1] = dblTradeTime * i / iNumInterval;

			adblHoldings[i - 1] = r1ToR1Holdings.evaluate (adblExecutionTime[i - 1]);
		}

		DiscreteTradingTrajectory dtt = DiscreteTradingTrajectory.Standard (
			adblExecutionTime,
			adblHoldings
		);

		double[] adblTradeList = dtt.tradeList();

		System.out.println();

		System.out.println ("\t|-----------------------------------||");

		System.out.println ("\t| DRI ATHL 2005 Optimal Trajectory  ||");

		System.out.println ("\t|-----------------------------------||");

		System.out.println ("\t|       L -> R:                     ||");

		System.out.println ("\t|             - Execution Time Node ||");

		System.out.println ("\t|             - Holdings Remaining  ||");

		System.out.println ("\t|             - Trade List Amount   ||");

		System.out.println ("\t|             - Holdings (%)        ||");

		System.out.println ("\t|-----------------------------------||");

		for (int i = 1; i < adblExecutionTime.length; ++i)
			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (adblExecutionTime[i], 1, 2, 1.) + " | " +
				FormatUtil.FormatDouble (adblHoldings[i], 6, 0, 1.) + " | " + 
				FormatUtil.FormatDouble (adblTradeList[i - 1], 5, 0, 1.) + " | " + 
				FormatUtil.FormatDouble (adblHoldings[i] / dblTradeSize, 2, 1, 100.) + "% ||"
			);

		System.out.println ("\t|-----------------------------------||");

		System.out.println();

		System.out.println ("\t|---------------------------------------------------------------------||");

		System.out.println ("\t|      DRI ATHL 2005 Optimal Trajectory Transaction Cost Measures     ||");

		System.out.println ("\t|---------------------------------------------------------------------||");

		System.out.println (
			"\t| Transaction Cost Expectation ( X 10^-06)                  : " +
			FormatUtil.FormatDouble (a2003tt.transactionCostExpectation(), 3, 2, 1.e-06) + " ||"
		);

		System.out.println (
			"\t| Transaction Cost Variance ( X 10^-09)                     : " +
			FormatUtil.FormatDouble (a2003tt.transactionCostVariance(), 3, 2, 1.e-09) + " ||"
		);

		System.out.println (
			"\t| Characteristic Time                                       : " +
			FormatUtil.FormatDouble (a2003tt.characteristicTime(), 3, 2, 1.) + " ||"
		);

		System.out.println (
			"\t| Efficient Frontier Hyperboloid Boundary Value ( X 10^-12) : " +
			FormatUtil.FormatDouble (a2003tt.hyperboloidBoundaryValue(), 3, 2, 1.e-12) + " ||"
		);

		System.out.println ("\t|---------------------------------------------------------------------||");
	}
}
