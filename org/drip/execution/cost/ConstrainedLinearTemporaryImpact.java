
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
 * ConstrainedLinearTemporaryImpact computes and holds the Optimal Trajectory under Trading Rate Sign
 *  Constraints using Linear Temporary Impact Function for the given set of Inputs. The References are:
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

public class ConstrainedLinearTemporaryImpact extends org.drip.execution.cost.LinearTemporaryImpact {
	private double _dblCriticalDrift = java.lang.Double.NaN;
	private double _dblTradeStartTime = java.lang.Double.NaN;
	private double _dblTradeFinishTime = java.lang.Double.NaN;

	/**
	 * Generate a ConstrainedLinearTemporaryImpact Instance
	 * 
	 * @param dblSpotTime Spot Time
	 * @param dblFinishTime Finish Time
	 * @param dblSpotHoldings Spot Holdings
	 * @param pcc The Prior/Conditional Combiner
	 * @param dblGrossPriceChange The Gross Price Change
	 * @param tflTemporary The Temporary Linear Impact Function
	 * 
	 * @return The ConstrainedLinearTemporaryImpact Instance
	 */

	public static final ConstrainedLinearTemporaryImpact Standard (
		final double dblSpotTime,
		final double dblFinishTime,
		final double dblSpotHoldings,
		final org.drip.execution.bayesian.PriorConditionalCombiner pcc,
		final double dblGrossPriceChange,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporary)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotTime) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblFinishTime) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblSpotHoldings) || null == pcc || null ==
					tflTemporary)
			return null;

		final double dblLiquidityCoefficient = tflTemporary.slope();

		org.drip.measure.gaussian.R1UnivariateNormal r1unPosterior = pcc.posteriorDriftDistribution
			(dblGrossPriceChange);

		if (null == r1unPosterior) return null;

		final double dblDriftEstimate = r1unPosterior.mean();

		double dblTradeStartTime = dblSpotTime;
		double dblTradeFinishTime = dblFinishTime;
		double dblHorizon = dblFinishTime - dblSpotTime;
		double dblInstantaneousTradeRate = java.lang.Double.NaN;
		double dblCriticalDrift = 4. * dblLiquidityCoefficient * dblSpotHoldings / (dblHorizon * dblHorizon);

		if (dblDriftEstimate > dblCriticalDrift) {
			dblTradeStartTime = dblSpotTime;

			dblInstantaneousTradeRate = java.lang.Math.sqrt (dblDriftEstimate * dblSpotHoldings /
				dblLiquidityCoefficient);

			dblTradeFinishTime = dblSpotTime + java.lang.Math.sqrt (4. * dblLiquidityCoefficient *
				dblSpotHoldings / dblDriftEstimate);
		} else if (dblDriftEstimate < -1. * dblCriticalDrift) {
			dblTradeFinishTime = dblFinishTime - java.lang.Math.sqrt (-4. * dblLiquidityCoefficient *
				dblSpotHoldings / dblDriftEstimate);

			dblInstantaneousTradeRate = 0.;
			dblTradeFinishTime = dblFinishTime;
		} else
			dblInstantaneousTradeRate = dblSpotHoldings / dblHorizon + dblDriftEstimate * dblHorizon /
				dblLiquidityCoefficient;

		final double dblt = dblTradeStartTime;
		final double dblT = dblTradeFinishTime;

		org.drip.function.definition.R1ToR1 r1ToR1Holdings = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblTau)
				throws java.lang.Exception
			{
				if (!org.drip.quant.common.NumberUtil.IsValid (dblTau))
					throw new java.lang.Exception
						("ConstrainedLinearTemporaryImpact::Holdings::evaluate => Invalid Inputs");

				if (dblTau <= dblt) return dblSpotHoldings;

				if (dblTau >= dblT) return 0.;

				return (dblT - dblTau) * (dblSpotHoldings / (dblT - dblt) - 0.25 * dblDriftEstimate * (dblTau
					- dblt) / dblLiquidityCoefficient);
			}
		};

		try {
			return new ConstrainedLinearTemporaryImpact (dblSpotTime, dblFinishTime, dblSpotHoldings, pcc,
				dblGrossPriceChange, tflTemporary, r1ToR1Holdings, dblInstantaneousTradeRate,
					dblCriticalDrift, dblTradeStartTime, dblTradeFinishTime);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected ConstrainedLinearTemporaryImpact (
		final double dblSpotTime,
		final double dblFinishTime,
		final double dblSpotHoldings,
		final org.drip.execution.bayesian.PriorConditionalCombiner pcc,
		final double dblGrossPriceChange,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporary,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings,
		final double dblInstantaneousTradeRate,
		final double dblCriticalDrift,
		final double dblTradeStartTime,
		final double dblTradeFinishTime)
		throws java.lang.Exception
	{
		super (dblSpotTime, dblFinishTime, dblSpotHoldings, pcc, dblGrossPriceChange, tflTemporary,
			r1ToR1Holdings, dblInstantaneousTradeRate);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblCriticalDrift = dblCriticalDrift) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblTradeStartTime = dblTradeStartTime) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblTradeFinishTime = dblTradeFinishTime))
			throw new java.lang.Exception ("ConstrainedLinearTemporaryImpact Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Critical Drift
	 * 
	 * @return The Critical Drift
	 */

	public double criticalDrift()
	{
		return _dblCriticalDrift;
	}

	/**
	 * Retrieve the Trade Start Time
	 * 
	 * @return The Trade Start Time
	 */

	public double tradeStartTime()
	{
		return _dblTradeStartTime;
	}

	/**
	 * Retrieve the Trade Finish Time
	 * 
	 * @return The Trade Finish Time
	 */

	public double tradeFinishTime()
	{
		return _dblTradeFinishTime;
	}
}
