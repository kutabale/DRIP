
package org.drip.execution.capture;

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
 * LinearImpactTrajectoryEstimator estimates the Price/Cost Distribution associated with the Trading
 *  Trajectory generated using the Linear Market Impact Evolution Parameters. The References are:
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

public class LinearImpactTrajectoryEstimator extends org.drip.execution.capture.TrajectoryShortfallEstimator
{

	/**
	 * LinearImpactTrajectoryEstimator Constructor
	 * 
	 * @param tt The Trading Trajectory Instance
	 *  
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LinearImpactTrajectoryEstimator (
		final org.drip.execution.strategy.TradingTrajectory tt)
		throws java.lang.Exception
	{
		super (tt);
	}

	@Override public org.drip.measure.gaussian.R1UnivariateNormal totalCostDistributionSynopsis (
		final org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters apep)
	{
		if (null == apep) return null;

		org.drip.execution.dynamics.LinearExpectationParameters lep =
			(org.drip.execution.dynamics.LinearExpectationParameters) apep;

		double dblPermanentLinearImpactParameter = lep.linearPermanentExpectation().slope();

		org.drip.execution.impact.TransactionFunctionLinear tflTemporaryExpectation =
			lep.linearTemporaryExpectation();

		double dblTemporaryLinearImpactParameter = tflTemporaryExpectation.slope();

		double dblTemporaryOffsetImpactParameter = tflTemporaryExpectation.offset();

		org.drip.execution.strategy.TradingTrajectory tt = trajectory();

		double[] adblExecutionTimeNode = tt.executionTimeNode();

		org.drip.execution.parameters.ArithmeticPriceDynamicsSettings apds =
			lep.arithmeticPriceDynamicsSettings();

		double dblMarketCoreVolatility = apds.volatility();

		double dblMarketCoreDrift = apds.drift();

		double[] adblHoldings = tt.holdings();

		int iNumTimeNode = adblHoldings.length;
		double dblUnscaledTotalCostVariance = 0.;
		double dblTotalCostMean = 0.5 * dblPermanentLinearImpactParameter * adblHoldings[0] *
			adblHoldings[0];

		for (int i = 1; i < iNumTimeNode; ++i) {
			double dblHoldingsIncrement = adblHoldings[i] - adblHoldings[i - 1];
			double dblTimeInterval = adblExecutionTimeNode[i] - adblExecutionTimeNode[i - 1];
			dblUnscaledTotalCostVariance = dblUnscaledTotalCostVariance + dblTimeInterval * adblHoldings[i] *
				adblHoldings[i];

			dblTotalCostMean = dblTotalCostMean + dblTemporaryOffsetImpactParameter * java.lang.Math.abs
				(dblHoldingsIncrement) + (dblTemporaryLinearImpactParameter - 0.5 *
					dblPermanentLinearImpactParameter * dblTimeInterval) / dblTimeInterval *
						dblHoldingsIncrement * dblHoldingsIncrement - dblMarketCoreDrift * dblTimeInterval *
							adblHoldings[i];
		}

		try {
			return new org.drip.measure.gaussian.R1UnivariateNormal (dblTotalCostMean,
				dblMarketCoreVolatility * java.lang.Math.sqrt (dblUnscaledTotalCostVariance));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
