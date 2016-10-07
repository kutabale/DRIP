
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
 * LineStepEvolutionControl contains the Parameters required to compute the Valid a Line Step. The References
 *  are:
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

public class LineStepEvolutionControl {
	private int _iNumReductionStep = -1;
	private double _dblReductionFactor = java.lang.Double.NaN;
	private org.drip.function.rdtor1descent.LineEvolutionVerifier _lev = null;

	/**
	 * Retrieve the Nocedal-Wright-Armijo Verifier Based Standard LineStepEvolutionControl Instance
	 * 
	 * @param bMaximizerCheck TRUE - Perform a Check for the Function Maxima
	 * 
	 * @return The Nocedal-Wright-Armijo Verifier Based Standard LineStepEvolutionControl Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightArmijo (
		final boolean bMaximizerCheck)
	{
		try {
			return new LineStepEvolutionControl
				(org.drip.function.rdtor1descent.ArmijoEvolutionVerifier.NocedalWrightStandard
					(bMaximizerCheck), 0.75, 1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Weak Curvature Verifier Based Standard LineStepEvolutionControl Instance
	 * 
	 * @return The Nocedal-Wright-Weak Curvature Verifier Based Standard LineStepEvolutionControl Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightWeakCurvature()
	{
		try {
			return new LineStepEvolutionControl
				(org.drip.function.rdtor1descent.CurvatureEvolutionVerifier.NocedalWrightStandard (false),
					0.75, 1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Strong Curvature Verifier Based Standard LineStepEvolutionControl Instance
	 * 
	 * @return The Nocedal-Wright-Strong Curvature Verifier Based Standard LineStepEvolutionControl Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightStrongCurvature()
	{
		try {
			return new LineStepEvolutionControl
				(org.drip.function.rdtor1descent.CurvatureEvolutionVerifier.NocedalWrightStandard (true),
					0.75, 1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Weak Wolfe Verifier Based Standard LineStepEvolutionControl Instance
	 * 
	 * @param bMaximizerCheck TRUE - Perform a Check for the Function Maxima
	 * 
	 * @return The Nocedal-Wright-Weak Wolfe Verifier Based Standard LineStepEvolutionControl Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightWeakWolfe (
		final boolean bMaximizerCheck)
	{
		try {
			return new LineStepEvolutionControl
				(org.drip.function.rdtor1descent.WolfeEvolutionVerifier.NocedalWrightStandard
					(bMaximizerCheck, false), 0.75, 1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Strong Wolfe Verifier Based Standard LineStepEvolutionControl Instance
	 * 
	 * @param bMaximizerCheck TRUE - Perform a Check for the Function Maxima
	 * 
	 * @return The Nocedal-Wright-Strong Wolfe Verifier Based Standard LineStepEvolutionControl Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightStrongWolfe (
		final boolean bMaximizerCheck)
	{
		try {
			return new LineStepEvolutionControl
				(org.drip.function.rdtor1descent.WolfeEvolutionVerifier.NocedalWrightStandard
					(bMaximizerCheck, true), 0.75, 1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * LineStepEvolutionControl Constructor
	 * 
	 * @param lev The Lie Evolution Verifier Instance
	 * @param dblReductionFactor The Per-Step Reduction Factor
	 * @param iNumReductionStep Number of Reduction Steps
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are Invalid
	 */

	public LineStepEvolutionControl (
		final org.drip.function.rdtor1descent.LineEvolutionVerifier lev,
		final double dblReductionFactor,
		final int iNumReductionStep)
		throws java.lang.Exception
	{
		if (null == (_lev = lev) || !org.drip.quant.common.NumberUtil.IsValid (_dblReductionFactor =
			dblReductionFactor) || 1. <= _dblReductionFactor || 0 >= (_iNumReductionStep =
				iNumReductionStep))
			throw new java.lang.Exception ("LineStepEvolutionControl Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Line Evolution Verifier Instance
	 * 
	 * @return The Line Evolution Verifier Instance
	 */

	public org.drip.function.rdtor1descent.LineEvolutionVerifier lineEvolutionVerifier()
	{
		return _lev;
	}

	/**
	 * Retrieve the Reduction Factor per Step
	 * 
	 * @return The Reduction Factor per Step
	 */

	public double reductionFactor()
	{
		return _dblReductionFactor;
	}

	/**
	 * Retrieve the Number of Reduction Steps
	 * 
	 * @return The Number of Reduction Steps
	 */

	public int reductionSteps()
	{
		return _iNumReductionStep;
	}
}
