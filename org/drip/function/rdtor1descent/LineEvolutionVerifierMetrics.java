
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
 * LineEvolutionVerifierMetrics implements the Step Length Verification Criterion used for the Inexact Line
 *  Search Increment Generation. The References are:
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

public abstract class LineEvolutionVerifierMetrics {
	private double[] _adblCurrentVariate = null;
	private double _dblStepLength = java.lang.Double.NaN;
	private double[] _adblCurrentVariateFunctionJacobian = null;
	private org.drip.function.definition.UnitVector _uvTargetDirection = null;

	protected LineEvolutionVerifierMetrics (
		final org.drip.function.definition.UnitVector uvTargetDirection,
		final double[] adblCurrentVariate,
		final double dblStepLength,
		final double[] adblCurrentVariateFunctionJacobian)
		throws java.lang.Exception
	{
		if (null == (_uvTargetDirection = uvTargetDirection) || null == (_adblCurrentVariate =
			adblCurrentVariate) || !org.drip.quant.common.NumberUtil.IsValid (_dblStepLength = dblStepLength)
				|| null == (_adblCurrentVariateFunctionJacobian = adblCurrentVariateFunctionJacobian) ||
					_adblCurrentVariate.length != _adblCurrentVariateFunctionJacobian.length)
			throw new java.lang.Exception ("LineEvolutionVerifierMetrics Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Current Variate Array
	 * 
	 * @return The Current Variate Array
	 */

	public double[] currentVariate()
	{
		return _adblCurrentVariate;
	}

	/**
	 * Retrieve the Target Direction Unit Vector
	 * 
	 * @return The Target Direction Unit Vector
	 */

	public org.drip.function.definition.UnitVector targetDirection()
	{
		return _uvTargetDirection;
	}

	/**
	 * Retrieve the Step Length
	 * 
	 * @return The Step Length
	 */

	public double stepLength()
	{
		return _dblStepLength;
	}

	/**
	 * Retrieve the Function Jacobian at the Current Variate
	 * 
	 * @return The Function Jacobian at the Current Variate
	 */

	public double[] currentVariateFunctionJacobian()
	{
		return _adblCurrentVariateFunctionJacobian;
	}

	@Override public java.lang.String toString()
	{
		double[] adblDirection = _uvTargetDirection.component();

		java.lang.String strDump = "\t[";
		int iNumVariate = _adblCurrentVariate.length;

		for (int i = 0; i < iNumVariate; ++i)
			strDump = strDump + org.drip.quant.common.FormatUtil.FormatDouble (_adblCurrentVariate[i], 2, 3,
				1.) + " |";

		strDump = strDump + "]" + org.drip.quant.common.FormatUtil.FormatDouble (_dblStepLength, 1, 3, 1.) +
			" || {";

		for (int i = 0; i < iNumVariate; ++i)
			strDump = strDump + org.drip.quant.common.FormatUtil.FormatDouble (adblDirection[i], 1, 2, 1.) +
				" |";

		return strDump + " }";
	}

	/**
	 * Indicate if the Evolution Criterion has been met
	 * 
	 * @return TRUE - The Evolution Criterion has been met
	 */

	public abstract boolean verify();
}
