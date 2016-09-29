
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
 * OptimalTrajectoryScheme generates the Trade/Holdings List of Optimal Execution Schedule based on the Trade
 *  Trajectory Control, the Price Walk Parameters, and the Objective Utility Function. The References are:
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

public class OptimalTrajectoryScheme {
	private org.drip.execution.optimizer.ObjectiveUtility _ou = null;
	private org.drip.execution.strategy.TradingTrajectoryControl _ttc = null;
	private org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters _apep = null;

	private double[] completeHoldings (
		final double[] adblInnerHoldings)
	{
		if (null == adblInnerHoldings) return null;

		int iNumCompleteHoldings = adblInnerHoldings.length + 2;
		double[] adblCompleteHoldings = new double[iNumCompleteHoldings];

		for (int i = 0; i < iNumCompleteHoldings; ++i) {
			if (0 == i)
				adblCompleteHoldings[i] = _ttc.startHoldings();
			else if (iNumCompleteHoldings - 1 == i)
				adblCompleteHoldings[i] = 0.;
			else
				adblCompleteHoldings[i] = adblInnerHoldings[i - 1];
		}

		return adblCompleteHoldings;
	}

	private org.drip.execution.sensitivity.ControlNodesGreek objectiveSensitivity (
		final double[] adblInnerHoldings)
	{
		org.drip.execution.capture.TrajectoryShortfallEstimator tse = null;

		try {
			tse = new org.drip.execution.capture.TrajectoryShortfallEstimator
				(org.drip.execution.strategy.TradingTrajectory.Standard (_ttc.executionTimeNodes(),
					completeHoldings (adblInnerHoldings)));
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		org.drip.execution.sensitivity.ControlNodesGreek cngExpectation = tse.expectationContribution
			(_apep);

		org.drip.execution.sensitivity.ControlNodesGreek cngVariance = tse.varianceContribution (_apep);

		if (null == cngExpectation || null == cngVariance) return null;

		return _ou.sensitivity ((org.drip.execution.sensitivity.TrajectoryControlNodesGreek) cngExpectation,
			(org.drip.execution.sensitivity.TrajectoryControlNodesGreek) cngVariance);
	}

	private org.drip.function.definition.RdToR1 optimizerRdToR1()
	{
		return new org.drip.function.definition.RdToR1 (null) {
			@Override public int dimension()
			{
				return _ttc.executionTimeNodes().length - 2;
			}

			@Override public double evaluate (
				final double[] adblInnerHoldings)
				throws java.lang.Exception
			{
				org.drip.execution.sensitivity.ControlNodesGreek cngObjectiveUtility = objectiveSensitivity
					(adblInnerHoldings);

				if (null == cngObjectiveUtility)
					throw new java.lang.Exception
						("OptimalTrajectoryScheme::optimizerRdToR1::evaluate => Invalid Inputs");

				return cngObjectiveUtility.value();
			}

			@Override public double[] jacobian (
				final double[] adblInnerHoldings)
			{
				org.drip.execution.sensitivity.ControlNodesGreek cngObjectiveUtility = objectiveSensitivity
					(adblInnerHoldings);

				return null == cngObjectiveUtility ? null : cngObjectiveUtility.jacobian();
			}

			@Override public double[][] hessian (
				final double[] adblInnerHoldings)
			{
				org.drip.execution.sensitivity.ControlNodesGreek cngObjectiveUtility = objectiveSensitivity
					(adblInnerHoldings);

				return null == cngObjectiveUtility ? null : cngObjectiveUtility.hessian();
			}
		};
	}

	/**
	 * OptimalTrajectoryScheme Constructor
	 * 
	 * @param ttc The Discrete Trading Trajectory Control Parameters
	 * @param apep The Arithmetic Price Walk Parameters
	 * @param ou The Optimizer Objective Utility Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are not valid
	 */

	public OptimalTrajectoryScheme (
		final org.drip.execution.strategy.TradingTrajectoryControl ttc,
		final org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters apep,
		final org.drip.execution.optimizer.ObjectiveUtility ou)
		throws java.lang.Exception
	{
		if (null == (_ttc = ttc) || null == (_apep = apep) || null == (_ou = ou))
			throw new java.lang.Exception ("OptimalTrajectoryScheme Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Optimizer Objective Utility Function
	 * 
	 * @return The Optimizer Objective Utility Function
	 */

	public org.drip.execution.optimizer.ObjectiveUtility objectiveUtility()
	{
		return _ou;
	}

	/**
	 * Retrieve the Discrete Trajectory Control Settings
	 * 
	 * @return The Discrete Trajectory Control Settings
	 */

	public org.drip.execution.strategy.TradingTrajectoryControl control()
	{
		return _ttc;
	}

	/**
	 * Retrieve the Asset Arithmetic Price Walk Evolution Parameters
	 * 
	 * @return The Asset Arithmetic Price Walk Evolution Parameters
	 */

	public org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters priceWalkParameters()
	{
		return _apep;
	}

	/**
	 * Invoke the Optimizer, and generate/return the Optimal Trading Trajectory Instance
	 * 
	 * @return The Optimal Trading Trajectory Instance
	 */

	public org.drip.execution.optimum.EfficientTradingTrajectory generate()
	{
		double[] adblExecutionTimeNode = _ttc.executionTimeNodes();

		org.drip.execution.strategy.TradingTrajectory tt =
			org.drip.execution.strategy.TradingTrajectory.Linear (adblExecutionTimeNode,
				_ttc.startHoldings(), 0.);

		if (null == tt) return null;

		org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier vicm = null;

		try {
			if (null == (vicm = new org.drip.function.rdtor1solver.NewtonFixedPointFinder (optimizerRdToR1(),
				null, org.drip.function.rdtor1solver.ConvergenceControl.Standard()).convergeVariate (new
					org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier (false,
						tt.innerHoldings(), null))))
				return null;
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		return org.drip.execution.optimum.EfficientTradingTrajectory.Standard (adblExecutionTimeNode,
			completeHoldings (vicm.variates()), _apep);
	}
}
