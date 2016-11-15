
package org.drip.sample.almgrenchriss;

import org.drip.execution.dynamics.LinearExpectationParameters;
import org.drip.execution.generator.AC2000TrajectorySchemeWithDrift;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.AC2000TradingTrajectoryWithDrift;
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
 * TrajectoryComparisonWithDrift compares different Optimal Trading Trajectories computed in accordance with
 *  the Specification of Almgren and Chriss (2000) for a Set of Risk Aversion Parameters, inclusive of the
 *  Asset Drift. The References are:
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

public class TrajectoryComparisonWithDrift {

	private static final void DisplayTrajectory (
		final AC2000TradingTrajectoryWithDrift tt,
		final double dblLambda,
		final double dblX)
		throws Exception
	{
		double[] adblHoldings = tt.holdings();

		String strHoldings = "\t| [LAMBDA = " + FormatUtil.FormatDouble (dblLambda, 1, 3, dblX) + "]";

		for (int i = 0; i < adblHoldings.length; ++i)
			strHoldings = strHoldings + " | " + FormatUtil.FormatDouble (adblHoldings[i] / dblX, 2, 3, 100.);

		System.out.println (strHoldings + " ||");
	}

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
		double[] adblLambdaU = {
			0.001e-06,
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

		AssetDynamicsSettings amc = AssetDynamicsSettings.FromAnnualReturnsSettings (
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

		LinearExpectationParameters lipe = new LinearExpectationParameters (
			dblAlpha,
			dblSigma,
			0.,
			prlPermanent,
			prlTemporary
		);

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

		System.out.println ("\n\t|--------------------------------------------------------------------------------||");

		System.out.println ("\t|             OPTIMAL TRADING TRAJECTORY FOR DIFFERENT RISK AVERSION             ||");

		System.out.println ("\t|--------------------------------------------------------------------------------||");

		for (double dblLambdaU : adblLambdaU)
			DisplayTrajectory (
				(AC2000TradingTrajectoryWithDrift) AC2000TrajectorySchemeWithDrift.Standard (
					dblX,
					dblT,
					iN,
					lipe,
					dblLambdaU
				).generate(),
				dblLambdaU,
				dblX
			);

		System.out.println ("\t|--------------------------------------------------------------------------------||");
	}
}
