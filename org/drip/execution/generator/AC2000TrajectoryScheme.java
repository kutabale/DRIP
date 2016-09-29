
package org.drip.execution.generator;

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
 * AC2000TrajectoryScheme generates the Trade/Holdings List of Optimal Execution Schedule for the Equally
 *  Spaced Trading Intervals based on the No-Drift Linear Impact Evolution Walk Parameters specified. The
 *  References are:
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

public class AC2000TrajectoryScheme extends org.drip.execution.generator.OptimalTrajectoryScheme {

	private double KappaTau (
		final double dblKappaTildaSquared,
		final double dblTau)
	{
		double dblKappaTildaSquaredTauSquared = dblKappaTildaSquared * dblTau * dblTau;

		return java.lang.Math.log (0.5 * (2. + dblKappaTildaSquaredTauSquared + dblTau * java.lang.Math.sqrt
			(dblKappaTildaSquared * (dblKappaTildaSquaredTauSquared + 4.))));
	}

	/**
	 * Create the Standard AC2000TrajectoryScheme Instance
	 * 
	 * @param dblStartHoldings Trajectory Start Holdings
	 * @param dblFinishTime Trajectory Finish Time
	 * @param iNumInterval The Number of Fixed Intervals
	 * @param lep Linear Impact Price Walk Parameters
	 * @param dblRiskAversion The Risk Aversion Parameter
	 * 
	 * @return The AC2000TrajectoryScheme Instance
	 */

	public static final AC2000TrajectoryScheme Standard (
		final double dblStartHoldings,
		final double dblFinishTime,
		final int iNumInterval,
		final org.drip.execution.dynamics.LinearExpectationParameters lep,
		final double dblRiskAversion)
	{
		try {
			return new AC2000TrajectoryScheme
				(org.drip.execution.strategy.TradingTrajectoryControl.FixedInterval (new
					org.drip.execution.strategy.OrderSpecification (dblStartHoldings, dblFinishTime),
						iNumInterval), lep, new org.drip.execution.optimizer.MeanVarianceObjectiveUtility
							(dblRiskAversion));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private AC2000TrajectoryScheme (
		final org.drip.execution.strategy.TradingTrajectoryControl ttc,
		final org.drip.execution.dynamics.LinearExpectationParameters lpe,
		final org.drip.execution.optimizer.MeanVarianceObjectiveUtility mvou)
		throws java.lang.Exception
	{
		super (ttc, lpe, mvou);
	}

	@Override public org.drip.execution.optimum.EfficientTradingTrajectory generate()
	{
		org.drip.execution.strategy.TradingTrajectoryControl ttc = control();

		double[] adblTNode = ttc.executionTimeNodes();

		org.drip.execution.dynamics.LinearExpectationParameters lep =
			(org.drip.execution.dynamics.LinearExpectationParameters) priceWalkParameters();

		org.drip.execution.impact.TransactionFunctionLinear tflTemporaryExpectation =
			lep.linearTemporaryExpectation();

		double dblGamma = lep.linearPermanentExpectation().slope();

		double dblEta = tflTemporaryExpectation.slope();

		double dblSigma = lep.marketCoreVolatility();

		double dblX = ttc.startHoldings();

		int iNumNode = adblTNode.length;
		double dblXSquared = dblX * dblX;
		double dblSigmaSquared = dblSigma * dblSigma;
		double[] adblHoldings = new double[iNumNode];
		double[] adblTradeList = new double[iNumNode - 1];
		double dblTau = adblTNode[1] - adblTNode[0];
		double dblT = adblTNode[iNumNode - 1] - adblTNode[0];
		double dblEtaTilda = dblEta - 0.5 * dblGamma * dblTau;

		double dblKappaTildaSquared = ((org.drip.execution.optimizer.MeanVarianceObjectiveUtility)
			objectiveUtility()).riskAversion() * dblSigmaSquared / dblEtaTilda;

		double dblKappaTau = KappaTau (dblKappaTildaSquared, dblTau);

		double dblKappa = dblKappaTau / dblTau;
		double dblHalfKappaTau = 0.5 * dblKappaTau;
		double dblKappaT = dblKappa * dblT;

		double dblSinhKappaT = java.lang.Math.sinh (dblKappaT);

		double dblSinhKappaTau = java.lang.Math.sinh (dblKappaTau);

		double dblSinhHalfKappaTau = java.lang.Math.sinh (dblHalfKappaTau);

		double dblTSinhKappaTau = dblT * dblSinhKappaTau;
		double dblInverseSinhKappaT = 1. / dblSinhKappaT;
		double dblTrajectoryScaler = dblInverseSinhKappaT * dblX;
		double dblTradeListScaler = 2. * dblSinhHalfKappaTau * dblTrajectoryScaler;
		double dblReciprocalSinhKappaTSquared = dblInverseSinhKappaT * dblInverseSinhKappaT;

		for (int i = 0; i < iNumNode; ++i) {
			adblHoldings[i] = dblTrajectoryScaler * java.lang.Math.sinh (dblKappa * (dblT - adblTNode[i]));

			if (i < iNumNode - 1)
				adblTradeList[i] = -1. * dblTradeListScaler * java.lang.Math.cosh (dblKappa * (dblT - dblTau
					* (0.5 + i)));
		}

		try {
			return new org.drip.execution.optimum.AC2000TradingTrajectory (adblTNode, adblHoldings,
				adblTradeList, java.lang.Math.sqrt (dblKappaTildaSquared), dblKappa, 0.5 * dblGamma *
					dblXSquared + tflTemporaryExpectation.offset() * dblX + dblEtaTilda * dblXSquared *
						dblReciprocalSinhKappaTSquared * java.lang.Math.tanh (dblHalfKappaTau) * (dblTau *
							java.lang.Math.sinh (2. * dblKappaT) + 2. * dblTSinhKappaTau) / (2. * dblTau *
								dblTau), 0.5 * dblSigmaSquared * dblXSquared * dblReciprocalSinhKappaTSquared
									* (dblTau * dblSinhKappaT * java.lang.Math.cosh (dblKappa * (dblT -
										dblTau)) - dblTSinhKappaTau) / dblSinhKappaTau);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
