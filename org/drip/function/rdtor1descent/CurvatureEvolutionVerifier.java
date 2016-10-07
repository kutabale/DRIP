
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
 * CurvatuveEvolutionVerifier implements the Armijo Criterion used for the Inexact Line Search Increment
 *  Generation to ascertain that the Gradient of the Function has reduced sufficiently. The References are:
 * 
 * 	- Wolfe, P. (1969): Convergence Conditions for Ascent Methods, SIAM Review 11 (2) 226-235.
 * 
 * 	- Wolfe, P. (1971): Convergence Conditions for Ascent Methods; II: Some Corrections, SIAM Review 13 (2)
 * 		185-188.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CurvatureEvolutionVerifier extends org.drip.function.rdtor1descent.LineEvolutionVerifier {

	/**
	 * The Nocedal-Wright Curvature Parameter
	 */

	public static final double NOCEDAL_WRIGHT_CURVATURE_PARAMETER = 0.9;

	private boolean _bStrongCurvatureCriterion = false;
	private double _dblCurvatureParameter = java.lang.Double.NaN;

	/**
	 * Construct the Nocedal-Wright Curvature Evolution Verifier
	 * 
	 * @param bStrongCurvatureCriterion TRUE - Apply the Strong Curvature Criterion
	 * 
	 * @return The Nocedal-Wright Curvature Evolution Verifier Instance
	 */

	public static final CurvatureEvolutionVerifier NocedalWrightStandard (
		final boolean bStrongCurvatureCriterion)
	{
		try {
			return new CurvatureEvolutionVerifier (NOCEDAL_WRIGHT_CURVATURE_PARAMETER,
				bStrongCurvatureCriterion);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * CurvatureEvolutionVerifier Constructor
	 * 
	 * @param dblCurvatureParameter The Curvature Parameter
	 * @param bStrongCurvatureCriterion TRUE - Apply the Strong Curvature Criterion
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CurvatureEvolutionVerifier (
		final double dblCurvatureParameter,
		final boolean bStrongCurvatureCriterion)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblCurvatureParameter = dblCurvatureParameter))
			throw new java.lang.Exception ("CurvatureEvolutionVerifier Constructor => Invalid Inputs");

		_bStrongCurvatureCriterion = bStrongCurvatureCriterion;
	}

	/**
	 * Retrieve the Curvature Parameter
	 * 
	 * @return The Curvature Parameter
	 */

	public double curvatureParameter()
	{
		return _dblCurvatureParameter;
	}

	/**
	 * Retrieve Whether of not the "Strong" Curvature Criterion needs to be met
	 * 
	 * @return TRUE - The "Strong" Curvature Criterion needs to be met
	 */

	public boolean strongCriterion()
	{
		return _bStrongCurvatureCriterion;
	}

	@Override public org.drip.function.rdtor1descent.LineEvolutionVerifierMetrics metrics (
		final org.drip.function.definition.UnitVector uvTargetDirection,
		final double[] adblCurrentVariate,
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double dblStepLength)
	{
		try {
			return null == funcRdToR1 ? null : new
				org.drip.function.rdtor1descent.CurvatureEvolutionVerifierMetrics (_dblCurvatureParameter,
					_bStrongCurvatureCriterion, uvTargetDirection, adblCurrentVariate, dblStepLength,
						funcRdToR1.jacobian (adblCurrentVariate), funcRdToR1.jacobian (NextVariate
							(uvTargetDirection, adblCurrentVariate, dblStepLength)));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
