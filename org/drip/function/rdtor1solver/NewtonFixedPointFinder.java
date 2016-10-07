
package org.drip.function.rdtor1solver;

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
 * NewtonFixedPointFinder generates the Iterators for solving R^d To R^1 Convex/Non-Convex Functions Using
 *  the Multivariate Newton Method.
 *
 * @author Lakshmi Krishnamurthy
 */

public class NewtonFixedPointFinder extends org.drip.function.rdtor1solver.FixedRdFinder {

	/**
	 * NewtonFixedPointFinder Constructor
	 * 
	 * @param rdToR1ObjectiveFunction The Objective Function
	 * @param lsec The Line Step Evolution Control
	 * @param cc Convergence Control Parameters
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NewtonFixedPointFinder (
		final org.drip.function.definition.RdToR1 rdToR1ObjectiveFunction,
		final org.drip.function.rdtor1descent.LineStepEvolutionControl lsec,
		final org.drip.function.rdtor1solver.ConvergenceControl cc)
		throws java.lang.Exception
	{
		super (rdToR1ObjectiveFunction, lsec, cc);
	}

	@Override public org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier increment (
		final org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier vcmtCurrent)
	{
		if (null == vcmtCurrent) return null;

		double[] adblVariate = vcmtCurrent.variates();

		org.drip.function.definition.RdToR1 rdToR1ObjectiveFunction = objectiveFunction();

		double[] adblVariateIncrement = org.drip.quant.linearalgebra.Matrix.Product
			(org.drip.quant.linearalgebra.Matrix.InvertUsingGaussianElimination
				(rdToR1ObjectiveFunction.hessian (adblVariate)), rdToR1ObjectiveFunction.jacobian
					(adblVariate));

		if (null == adblVariateIncrement) return null;

		int iVariateDimension = adblVariateIncrement.length;

		for (int i = 0; i < iVariateDimension; ++i)
			adblVariateIncrement[i] = -1. * adblVariateIncrement[i];

		try {
			return new org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier (true,
				adblVariateIncrement, null);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier next (
		final org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier vcmtCurrent,
		final org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier vcmtIncrement,
		final double dblIncrementFraction)
	{
		return org.drip.function.rdtor1solver.VariateInequalityConstraintMultiplier.Add (vcmtCurrent,
			vcmtIncrement, dblIncrementFraction, null);
	}
}
