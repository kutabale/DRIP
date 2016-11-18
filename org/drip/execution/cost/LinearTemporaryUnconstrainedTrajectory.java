
package org.drip.execution.cost;

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
 * LinearTemporaryUnconstrainedTrajectory computes and holds the Unconstrained Optimal Trajectory using
 *  Linear Temporary Impact Function for the given set of Inputs. The References are:
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets 1
 * 		1-50.
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Brunnermeier, L. K., and L. H. Pedersen (2005): Predatory Trading, Journal of Finance 60 (4) 1825-1863.
 *
 * 	- Almgren, R., and J. Lorenz (2006): Bayesian Adaptive Trading with a Daily Cycle, Journal of Trading 1
 * 		(4) 38-46.
 * 
 * 	- Kissell, R., and R. Malamut (2007): Algorithmic Decision Making Framework, Journal of Trading 1 (1)
 * 		12-21.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class LinearTemporaryUnconstrainedTrajectory {
	private double _dblSpotTime = java.lang.Double.NaN;
	private double _dblFinishTime = java.lang.Double.NaN;
	private double _dblSpotHoldings = java.lang.Double.NaN;
	private double _dblDriftEstimate = java.lang.Double.NaN;
	private org.drip.execution.impact.TransactionFunctionLinear _tflTemporary = null;

	/**
	 * LinearTemporaryUnconstrainedTrajectory Constructor
	 * 
	 * @param dblSpotTime The Spot Time
	 * @param dblFinishTime The Finish Time
	 * @param dblSpotHoldings The Spot Holdings
	 * @param dblDriftEstimate The Drift Estimate
	 * @param tflTemporary The Linear Temporary Market Impact Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LinearTemporaryUnconstrainedTrajectory (
		final double dblSpotTime,
		final double dblFinishTime,
		final double dblSpotHoldings,
		final double dblDriftEstimate,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporary)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblSpotTime = dblSpotTime) || 0. > _dblSpotTime ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblFinishTime = dblFinishTime) || _dblFinishTime <=
				_dblSpotTime || !org.drip.quant.common.NumberUtil.IsValid (_dblSpotHoldings = dblSpotTime) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblDriftEstimate = dblDriftEstimate) || null
						== (_tflTemporary = tflTemporary))
			throw new java.lang.Exception
				("LinearTemporaryUnconstrainedTrajectory Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Spot Time
	 * 
	 * @return The Spot Time
	 */

	public double spotTime()
	{
		return _dblSpotTime;
	}

	/**
	 * Retrieve the Finish Time
	 * 
	 * @return The Finish Time
	 */

	public double finishTime()
	{
		return _dblFinishTime;
	}

	/**
	 * Retrieve the Spot Holdings
	 * 
	 * @return The Spot Holdings
	 */

	public double spotHoldings()
	{
		return _dblSpotHoldings;
	}

	/**
	 * Retrieve the Drift Estimate
	 * 
	 * @return The Drift Estimate
	 */

	public double driftEstimate()
	{
		return _dblDriftEstimate;
	}

	/**
	 * Retrieve the Linear Temporary Market Impact Function
	 * 
	 * @return The Linear Temporary Market Impact Function
	 */

	public org.drip.execution.impact.TransactionFunctionLinear linearTemporaryImpact()
	{
		return _tflTemporary;
	}
}
