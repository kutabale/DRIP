
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
 * LinearTemporaryImpact computes and holds the Optimal Trajectory using the Linear Temporary Impact Function
 *  for the given set of Inputs. It provides a Default Unconstrained Trajectory Implementation. The
 *  References are:
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

public class LinearTemporaryImpact {
	private double _dblSpotTime = java.lang.Double.NaN;
	private double _dblFinishTime = java.lang.Double.NaN;
	private double _dblSpotHoldings = java.lang.Double.NaN;
	private double _dblGrossPriceChange = java.lang.Double.NaN;
	private double _dblInstantaneousTradeRate = java.lang.Double.NaN;
	private org.drip.execution.bayesian.PriorConditionalCombiner _pcc = null;
	private org.drip.execution.strategy.ContinuousTradingTrajectory _ctt = null;
	private org.drip.execution.impact.TransactionFunctionLinear _tflTemporary = null;

	/**
	 * Generate an Unconstrained LinearTemporaryImpact Instance
	 * 
	 * @param dblSpotTime Spot Time
	 * @param dblFinishTime Finish Time
	 * @param dblSpotHoldings Spot Holdings
	 * @param pcc The Prior/Conditional Combiner
	 * @param dblGrossPriceChange The Gross Price Change
	 * @param tflTemporary The Temporary Linear Impact Function
	 * 
	 * @return The Unconstrained LinearTemporaryImpact Instance
	 */

	public static final LinearTemporaryImpact Unconstrained (
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

		final double dblHorizon = dblFinishTime - dblSpotTime;

		org.drip.measure.gaussian.R1UnivariateNormal r1unPosterior = pcc.posteriorDriftDistribution
			(dblGrossPriceChange);

		if (null == r1unPosterior) return null;

		final double dblScaledDrift = 0.25 * r1unPosterior.mean() / tflTemporary.slope();

		org.drip.function.definition.R1ToR1 r1ToR1Holdings = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblTau)
				throws java.lang.Exception
			{
				if (0. >= dblHorizon) return 0.;

				if (!org.drip.quant.common.NumberUtil.IsValid (dblTau))
					throw new java.lang.Exception
						("LinearTemporaryImpact::Holdings::evaluate => Invalid Inputs");

				if (dblTau <= dblSpotTime) return dblSpotHoldings;

				if (dblTau >= dblFinishTime) return 0.;

				return (dblFinishTime - dblTau) * (dblSpotHoldings / (dblFinishTime - dblSpotTime) -
					dblScaledDrift * (dblTau - dblSpotTime));
			}
		};

		try {
			return new LinearTemporaryImpact (dblSpotTime, dblFinishTime, dblSpotHoldings, pcc,
				dblGrossPriceChange, tflTemporary, r1ToR1Holdings, 0 >= dblHorizon ? 0. : dblSpotHoldings /
					dblHorizon + dblScaledDrift * dblHorizon);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected LinearTemporaryImpact (
		final double dblSpotTime,
		final double dblFinishTime,
		final double dblSpotHoldings,
		final org.drip.execution.bayesian.PriorConditionalCombiner pcc,
		final double dblGrossPriceChange,
		final org.drip.execution.impact.TransactionFunctionLinear tflTemporary,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings,
		final double dblInstantaneousTradeRate)
		throws java.lang.Exception
	{
		if (null == (_ctt = org.drip.execution.strategy.ContinuousTradingTrajectory.Standard ((_dblFinishTime
			= dblFinishTime) - (_dblSpotTime = dblSpotTime), r1ToR1Holdings)) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblSpotHoldings = dblSpotHoldings) || null ==
					(_pcc = pcc) || !org.drip.quant.common.NumberUtil.IsValid (_dblGrossPriceChange =
						dblGrossPriceChange) || null == (_tflTemporary = tflTemporary) ||
							!org.drip.quant.common.NumberUtil.IsValid (_dblInstantaneousTradeRate =
								dblInstantaneousTradeRate))
			throw new java.lang.Exception ("LinearTemporaryImpact Constructor => Invalid Inputs");
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
	 * Retrieve the Prior/Conditional Distributions Combiner
	 * 
	 * @return The Prior/Conditional Distributions Combiner
	 */

	public org.drip.execution.bayesian.PriorConditionalCombiner combiner()
	{
		return _pcc;
	}

	/**
	 * Retrieve the Gross Price Change
	 * 
	 * @return The Gross Price Change
	 */

	public double grossPriceChange()
	{
		return _dblGrossPriceChange;
	}

	/**
	 * Retrieve the Drift Estimate
	 * 
	 * @return The Drift Estimate
	 */

	public double driftEstimate()
	{
		return _pcc.posteriorDriftDistribution (_dblGrossPriceChange).mean();
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
	 * Retrieve the Holdings Trajectory
	 * 
	 * @return The Holdings Trajectory
	 */

	public org.drip.execution.strategy.ContinuousTradingTrajectory trajectory()
	{
		return _ctt;
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

	/**
	 * Estimate of the Transaction Cost
	 * 
	 * @return The Transaction Cost Estimate
	 */

	public double transactionCost()
	{
		double dblLiquidityCoefficient = _tflTemporary.slope();

		double dblDriftEstimate = _pcc.posteriorDriftDistribution (_dblGrossPriceChange).mean();

		double dblExecutionTime = _dblFinishTime - _dblSpotTime;
		return _dblSpotHoldings * _dblSpotHoldings * dblLiquidityCoefficient / dblExecutionTime + 0.5 *
			_dblSpotHoldings * dblDriftEstimate * dblExecutionTime - dblExecutionTime * dblExecutionTime *
				dblExecutionTime * dblDriftEstimate * dblDriftEstimate / (48. * dblLiquidityCoefficient);
	}

	/**
	 * Estimate the Gain available from the Bayesian Drift
	 * 
	 * @param pcc The Prior Conditional Combiner Instance
	 * 
	 * @return The Gain available from the Bayesian Drift
	 * 
	 * @throws java.lang.Exception Thrown if the Gain cannot be estimated
	 */

	public double transactionCostGain (
		final org.drip.execution.bayesian.PriorConditionalCombiner pcc)
		throws java.lang.Exception
	{
		if (null == pcc)
			throw new java.lang.Exception
				("ConstrainedLinearTemporaryImpact::transactionCostGain => Invalid Inputs");

		double dblHorizon = finishTime() - spotTime();

		double dblDriftConfidence = pcc.prior().confidence();

		double dblPriceVolatility = pcc.conditional().priceVolatility();

		final double dblRho = dblPriceVolatility * dblPriceVolatility / (dblDriftConfidence *
			dblDriftConfidence * dblHorizon);

		org.drip.function.definition.R1ToR1 r1ToR1Quadrature = new org.drip.function.definition.R1ToR1 (null)
		{
			@Override public double evaluate (
				final double dblDelta)
				throws java.lang.Exception
			{
				if (!org.drip.quant.common.NumberUtil.IsValid (dblDelta))
					throw new java.lang.Exception
						("ConstrainedLinearTemporaryImpact::transactionCostGain::r1ToR1Quadrature::evaluate => Invalid Inputs");

				double dblRemainingTime = 1. - dblDelta;
				double dblDimensionlessTime = dblDelta + dblRho;
				return dblRemainingTime * dblRemainingTime * dblRemainingTime / (dblDimensionlessTime *
					dblDimensionlessTime);
			}
		};

		return dblPriceVolatility * dblPriceVolatility * dblHorizon * dblHorizon / (48. *
			linearTemporaryImpact().slope()) * org.drip.quant.calculus.R1ToR1Integrator.LinearQuadrature
				(r1ToR1Quadrature, 0., 1.);
	}
}
