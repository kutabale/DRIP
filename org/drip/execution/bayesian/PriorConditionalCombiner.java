
package org.drip.execution.bayesian;

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
 * PriorConditionalCombiner holds the Distributions associated with the Prior Drift and the Conditional Price
 *  Distributions. It uses them to generate the resulting Joint, Posterior, and MAP Implied Posterior
 *  Distributions. The References are:
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

public class PriorConditionalCombiner {
	private org.drip.execution.bayesian.PriorDriftDistribution _pdd = null;
	private org.drip.execution.bayesian.ConditionalPriceDistribution _cpd = null;

	/**
	 * PriorConditionalCombiner Constructor
	 * 
	 * @param pdd The Prior Drift Distribution Instance
	 * @param cpd The Conditional Price Distribution Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public PriorConditionalCombiner (
		final org.drip.execution.bayesian.PriorDriftDistribution pdd,
		final org.drip.execution.bayesian.ConditionalPriceDistribution cpd)
		throws java.lang.Exception
	{
		if (null == (_pdd = pdd) || null == (_cpd = cpd))
			throw new java.lang.Exception ("PriorConditionalCombiner Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Prior Drift Distribution Instance
	 * 
	 * @return The Prior Drift Distribution Instance
	 */

	public org.drip.execution.bayesian.PriorDriftDistribution prior()
	{
		return _pdd;
	}

	/**
	 * Retrieve the Conditional Price Distribution Instance
	 * 
	 * @return The Conditional Price Distribution Instance
	 */

	public org.drip.execution.bayesian.ConditionalPriceDistribution conditional()
	{
		return _cpd;
	}

	/**
	 * Generate the Joint Price Distribution
	 * 
	 * @return The Joint Price Distribution
	 */

	public org.drip.measure.gaussian.R1UnivariateNormal jointPriceDistribution()
	{
		double dblTime = _cpd.time();

		try {
			return new org.drip.measure.gaussian.R1UnivariateNormal (_pdd.expectation() * dblTime,
				_pdd.variance() * dblTime * dblTime +_cpd.variance());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Posterior Drift Distribution
	 * 
	 * @param dblDeltaS The Price Change (Final - Initial)
	 * 
	 * @return The Posterior Drift Distribution
	 */

	public org.drip.measure.gaussian.R1UnivariateNormal posteriorDriftDistribution (
		final double dblDeltaS)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDeltaS)) return null;

		double dblT = _cpd.time();

		double dblNuSquared = _pdd.variance();

		double dblSigmaSquared = _cpd.variance() / dblT;

		double dblPrecisionSquared = 1. / (dblSigmaSquared + dblNuSquared * dblT);

		try {
			return new org.drip.measure.gaussian.R1UnivariateNormal ((_pdd.expectation() * dblSigmaSquared +
				dblNuSquared * dblDeltaS) * dblPrecisionSquared, dblSigmaSquared * dblNuSquared *
					dblPrecisionSquared);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
