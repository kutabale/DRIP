
package org.drip.function.rdtor1descent;

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
 * LineEvolutionVerifier implements the Step Length Verification Criterion used for the Inexact Line Search
 *  Increment Generation. The References are:
 * 
 * 	- Armijo, L. (1966): Minimization of Functions having Lipschitz-Continuous First Partial Derivatives,
 * 		Pacific Journal of Mathematics 16 (1) 1-3.
 * 
 * 	- Wolfe, P. (1969): Convergence Conditions for Ascent Methods, SIAM Review 11 (2) 226-235.
 * 
 * 	- Wolfe, P. (1971): Convergence Conditions for Ascent Methods; II: Some Corrections, SIAM Review 13 (2)
 * 		185-188.
 * 
 * 	- Nocedal, J., and S. Wright (1999): Numerical Optimization, Wiley.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class LineEvolutionVerifier {

	protected static final double[] NextVariate (
		final org.drip.function.definition.UnitVector uvTargetDirection,
		final double[] adblCurrentVariate,
		final double dblStepLength)
	{
		if (null == adblCurrentVariate || !org.drip.quant.common.NumberUtil.IsValid (dblStepLength))
			return null;

		int iDimension = adblCurrentVariate.length;
		double[] adblNextVariate = 0 == iDimension ? null : new double[iDimension];

		if (null == adblNextVariate || null == uvTargetDirection) return null;

		double[] adblTargetDirection = uvTargetDirection.component();

		if (null == adblTargetDirection || iDimension != adblTargetDirection.length) return null;

		for (int i = 0; i < iDimension; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (adblCurrentVariate[i]) ||
				!org.drip.quant.common.NumberUtil.IsValid (adblTargetDirection[i]))
				return null;

			adblNextVariate[i] = adblCurrentVariate[i] + dblStepLength * adblTargetDirection[i];
		}

		return adblNextVariate;
	}

	/**
	 * Verify if the specified Inputs satisfy the Criterion
	 * 
	 * @param uvTargetDirection The Target Direction Unit Vector
	 * @param adblCurrentVariate The Current Variate
	 * @param funcRdToR1 The R^d -> R^1 Function
	 * @param dblStepLength The Incremental Step Length
	 * 
	 * @return TRUE => The Specified Inputs satisfy the Criterion
	 * 
	 * @throws java.lang.Exception Thrown if the Verification cannot be performed
	 */

	public boolean verify (
		final org.drip.function.definition.UnitVector uvTargetDirection,
		final double[] adblCurrentVariate,
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double dblStepLength)
		throws java.lang.Exception
	{
		org.drip.function.rdtor1descent.LineEvolutionVerifierMetrics levm = metrics (uvTargetDirection,
			adblCurrentVariate, funcRdToR1, dblStepLength);

		if (null == levm) throw new java.lang.Exception ("LineEvolutionVerifier::verify => Cannot Verify");

		return levm.verify();
	}

	/**
	 * Generate the Verifier Metrics for the Specified Inputs
	 * 
	 * @param uvTargetDirection The Target Direction Unit Vector
	 * @param adblCurrentVariate The Current Variate
	 * @param funcRdToR1 The R^d -> R^1 Function
	 * @param dblStepLength The Incremental Step Length
	 * 
	 * @return The Verifier Metrics
	 */

	public abstract org.drip.function.rdtor1descent.LineEvolutionVerifierMetrics metrics (
		final org.drip.function.definition.UnitVector uvTargetDirection,
		final double[] adblCurrentVariate,
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double dblStepLength);
}
