
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
 * LinearTemporaryImpactTrajectory computes and holds the Optimal Trajectory using the Linear Temporary
 *  Impact Function for the given set of Inputs. It provides a Default Unconstrained Trajectory
 *  Implementation. The References are:
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

public class LinearTemporaryImpactTrajectory {
	private double _dblSpotTime = java.lang.Double.NaN;
	private double _dblFinishTime = java.lang.Double.NaN;
	private double _dblSpotHoldings = java.lang.Double.NaN;
	private double _dblDriftEstimate = java.lang.Double.NaN;
	private double _dblInstantaneousTradeRate = java.lang.Double.NaN;
	private org.drip.function.definition.R1ToR1 _r1ToR1Holdings = null;
	private org.drip.execution.impact.TransactionFunctionLinear _tflTemporary = null;

	/**
	 * Generate an Unconstrained LinearTemporaryImpactTrajectory Instance
	 * 
	 * @param dblSpotTime Spot Time
	 * @param dblFinishTime Finish Time
	 * @param dblSpotHoldings Spot Holdings
	 * @param dblDriftEstimate Drift Estimate
	 * @param tflTemporary The Temporary Linear Impact Function
	 * 
	 * @return The Unconstrained LinearTemporaryImpactTrajectory Instance
	 */

	public static final LinearTemporaryImpactTrajectory Unconstrained (
		final double dblSpotTime,
		final double dblFinishTime,
		final double dblSpotHoldings,
		final double dblDriftEstimate,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporary)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotTime) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblFinishTime) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblSpotHoldings) ||
					!org.drip.quant.common.NumberUtil.IsValid (dblDriftEstimate) || null == tflTemporary)
			return null;

		final double dblHorizon = dblFinishTime - dblSpotTime;

		final double dblScaledDrift = 0.25 * dblDriftEstimate / tflTemporary.slope();

		org.drip.function.definition.R1ToR1 r1ToR1Holdings = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblTau)
				throws java.lang.Exception
			{
				if (0. >= dblHorizon) return 0.;

				if (!org.drip.quant.common.NumberUtil.IsValid (dblTau))
					throw new java.lang.Exception
						("LinearTemporaryImpactTrajectory::Holdings::evaluate => Invalid Inputs");

				if (dblTau <= dblSpotTime) return dblSpotHoldings;

				if (dblTau >= dblFinishTime) return 0.;

				return (dblFinishTime - dblTau) * (dblSpotHoldings / (dblFinishTime - dblSpotTime) -
					dblScaledDrift * (dblTau - dblSpotTime));
			}
		};

		try {
			return new LinearTemporaryImpactTrajectory (dblSpotTime, dblFinishTime, dblSpotHoldings,
				dblDriftEstimate, tflTemporary, r1ToR1Holdings, 0 >= dblHorizon ? 0. : dblSpotHoldings /
					dblHorizon + dblScaledDrift * dblHorizon);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected LinearTemporaryImpactTrajectory (
		final double dblSpotTime,
		final double dblFinishTime,
		final double dblSpotHoldings,
		final double dblDriftEstimate,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporary,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings,
		final double dblInstantaneousTradeRate)
		throws java.lang.Exception
	{
		if (null == (_r1ToR1Holdings = r1ToR1Holdings) || !org.drip.quant.common.NumberUtil.IsValid
			(_dblInstantaneousTradeRate = dblInstantaneousTradeRate))
			throw new java.lang.Exception ("LinearTemporaryImpactTrajectory Constructor => Invalid Inputs");
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

	/**
	 * Retrieve the Holdings Function
	 * 
	 * @return The Holdings Function
	 */

	public org.drip.function.definition.R1ToR1 holdingsFunction()
	{
		return _r1ToR1Holdings;
	}

	/**
	 * Retrieve the Instantaneous Trade Rate
	 * 
	 * @return The Instantaneous Trade Rate
	 */

	public double instantaneousTradeRate()
	{
		return _dblInstantaneousTradeRate;
	}
}
