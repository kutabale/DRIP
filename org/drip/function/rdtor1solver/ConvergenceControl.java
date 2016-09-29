
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
 * ConvergenceControl contains the R^d -> R^1 Convergence Control/Tuning Parameters.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ConvergenceControl {

	/**
	 * Solve Using the Convergence of the Objective Function Realization
	 */

	public static final int OBJECTIVE_FUNCTION_SEQUENCE_CONVERGENCE = 1;

	/**
	 * Solve Using the Convergence of the Variate/Constraint Multiplier Tuple Realization
	 */

	public static final int VARIATE_CONSTRAINT_SEQUENCE_CONVERGENCE = 2;

	private int _iNumFinderSteps = -1;
	private double _dblAbsoluteTolerance = java.lang.Double.NaN;
	private double _dblRelativeTolerance = java.lang.Double.NaN;
	private int _iConvergenceType = VARIATE_CONSTRAINT_SEQUENCE_CONVERGENCE;

	/**
	 * Construct a Standard ConvergenceControl Instance
	 * 
	 * @return The Standard ConvergenceControl Instance
	 */

	public static ConvergenceControl Standard()
	{
		try {
			return new ConvergenceControl (VARIATE_CONSTRAINT_SEQUENCE_CONVERGENCE, 5.0e-02, 1.0e-06, 70);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ConvergenceControl Constructor
	 * 
	 * @param iConvergenceType The Convergence Type
	 * @param dblRelativeTolerance The Objective Function Relative Tolerance
	 * @param dblAbsoluteTolerance The Objective Function Absolute Tolerance
	 * @param iNumFinderSteps The Number of the Fixed Point Finder Steps
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ConvergenceControl (
		final int iConvergenceType,
		final double dblRelativeTolerance,
		final double dblAbsoluteTolerance,
		final int iNumFinderSteps)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblRelativeTolerance = dblRelativeTolerance) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblAbsoluteTolerance = dblAbsoluteTolerance) || 1 >
				(_iNumFinderSteps = iNumFinderSteps))
			throw new java.lang.Exception ("ConvergenceControl Constructor => Invalid Inputs");

		_iConvergenceType = iConvergenceType;
	}

	/**
	 * Retrieve the Convergence Type
	 * 
	 * @return The Convergence Type
	 */

	public int convergenceType()
	{
		return _iConvergenceType;
	}

	/**
	 * Retrieve the Number of Finder Steps
	 * 
	 * @return The Number of Finder Steps
	 */

	public int numFinderSteps()
	{
		return _iNumFinderSteps;
	}

	/**
	 * Retrieve the Relative Tolerance
	 * 
	 * @return The Relative Tolerance
	 */

	public double relativeTolerance()
	{
		return _dblRelativeTolerance;
	}

	/**
	 * Retrieve the Absolute Tolerance
	 * 
	 * @return The Absolute Tolerance
	 */

	public double absoluteTolerance()
	{
		return _dblAbsoluteTolerance;
	}
}
