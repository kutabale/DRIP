
package org.drip.optimization.kkt;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * OptimizationFramework holds the Non Linear Objective Function and the Collection of Equality and the
 *  Inequality Constraints that correspond to the Optimization Setup. The References are:
 * 
 * 	- Boyd, S., and L. van den Berghe (2009): Convex Optimization, Cambridge University Press, Cambridge UK.
 * 
 * 	- Eustaquio, R., E. Karas, and A. Ribeiro (2008): Constraint Qualification for Nonlinear Programming,
 * 		Technical Report, Federal University of Parana.
 * 
 * 	- Karush, A. (1939): Minima of Functions of Several Variables with Inequalities as Side Constraints,
 * 		M. Sc., University of Chicago, Chicago IL.
 * 
 * 	- Kuhn, H. W., and A. W. Tucker (1951): Nonlinear Programming, Proceedings of the Second Berkeley
 * 		Symposium, University of California, Berkeley CA 481-492.
 * 
 * 	- Ruszczynski, A. (2006): Nonlinear Optimization, Princeton University Press, Princeton NJ.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OptimizationFramework {
	private org.drip.function.definition.RdToR1 _rdToR1Objective = null;
	private org.drip.function.definition.RdToR1[] _aRdToR1EqualityConstraint = null;
	private org.drip.function.definition.RdToR1[] _aRdToR1InequalityConstraint = null;

	/**
	 * OptimizationFramework Constructor
	 * 
	 * @param rdToR1Objective The R^d -> R^1 Objective Function
	 * @param aRdToR1EqualityConstraint The Array of R^d -> R^1 Equality Constraint Functions
	 * @param aRdToR1InequalityConstraint The Array of R^d -> R^1 Inequality Constraint Functions
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public OptimizationFramework (
		final org.drip.function.definition.RdToR1 rdToR1Objective,
		final org.drip.function.definition.RdToR1[] aRdToR1EqualityConstraint,
		final org.drip.function.definition.RdToR1[] aRdToR1InequalityConstraint)
		throws java.lang.Exception
	{
		if (null == (_rdToR1Objective = rdToR1Objective))
			throw new java.lang.Exception ("OptimizationFramework Constructor => Invalid Inputs");

		int iObjectiveDimension = _rdToR1Objective.dimension();

		if (null != (_aRdToR1EqualityConstraint = aRdToR1EqualityConstraint)) {
			int iNumEqualityConstraint = _aRdToR1EqualityConstraint.length;

			for (int i = 0; i < iNumEqualityConstraint; ++i) {
				if (null == _aRdToR1EqualityConstraint[i] || _aRdToR1EqualityConstraint[i].dimension() !=
					iObjectiveDimension)
					throw new java.lang.Exception ("OptimizationFramework Constructor => Invalid Inputs");
			}
		}

		if (null != (_aRdToR1InequalityConstraint = aRdToR1InequalityConstraint)) {
			int iNumInequalityConstraint = _aRdToR1InequalityConstraint.length;

			for (int i = 0; i < iNumInequalityConstraint; ++i) {
				if (null == _aRdToR1InequalityConstraint[i] || _aRdToR1InequalityConstraint[i].dimension() !=
					iObjectiveDimension)
					throw new java.lang.Exception ("OptimizationFramework Constructor => Invalid Inputs");
			}
		}
	}

	/**
	 * Retrieve the R^d -> R^1 Objective Function
	 * 
	 * @return The R^d -> R^1 Objective Function
	 */

	public org.drip.function.definition.RdToR1 objectiveFunction()
	{
		return _rdToR1Objective;
	}

	/**
	 * Retrieve the Array of R^d -> R^1 Equality Constraint Functions
	 * 
	 * @return The Array of R^d -> R^1 Equality Constraint Functions
	 */

	public org.drip.function.definition.RdToR1[] equalityConstraint()
	{
		return _aRdToR1EqualityConstraint;
	}

	/**
	 * Retrieve the Array of R^d -> R^1 Inequality Constraint Functions
	 * 
	 * @return The Array of R^d -> R^1 Inequality Constraint Functions
	 */

	public org.drip.function.definition.RdToR1[] inequalityConstraint()
	{
		return _aRdToR1InequalityConstraint;
	}

	/**
	 * Retrieve the Number of Equality Constraints
	 * 
	 * @return The Number of Equality Constraints
	 */

	public int numEqualityConstraint()
	{
		return null == _aRdToR1EqualityConstraint ? 0 : _aRdToR1EqualityConstraint.length;
	}

	/**
	 * Retrieve the Number of Inequality Constraints
	 * 
	 * @return The Number of Inequality Constraints
	 */

	public int numInequalityConstraint()
	{
		return null == _aRdToR1InequalityConstraint ? 0 : _aRdToR1InequalityConstraint.length;
	}

	/**
	 * Indicate if the Optimizer Framework is Lagrangian
	 * 
	 * @return TRUE - The Optimizer Framework is Lagrangian
	 */

	public boolean isLagrangian()
	{
		return 0 == numInequalityConstraint() && 0 != numEqualityConstraint();
	}

	/**
	 * Indicate if the Optimizer Framework is Unconstrained
	 * 
	 * @return TRUE - The Optimizer Framework is Unconstrained
	 */

	public boolean isUnconstrained()
	{
		return 0 == numInequalityConstraint() && 0 == numEqualityConstraint();
	}

	/**
	 * Indicate if the specified KKT Multiplier Suite is compatible with the Optimization Framework
	 * 
	 * @param kktMultiplier The specified KKT Multipliers
	 * 
	 * @return TRUE - The specified KKT Multiplier Suite is compatible with the Optimization Framework
	 */

	public boolean isCompatible (
		final org.drip.optimization.kkt.Multipliers kktMultiplier)
	{
		int iNumEqualityConstraint = numEqualityConstraint();

		int iNumInequalityConstraint = numInequalityConstraint();

		int iNumEqualityKKTMultiplier = null == kktMultiplier ? 0 : kktMultiplier.numEqualityCoefficients();

		if (iNumEqualityConstraint != iNumEqualityKKTMultiplier) return false;

		int iNumInequalityKKTMultiplier = null == kktMultiplier ? 0 :
			kktMultiplier.numInequalityCoefficients();

		return iNumInequalityConstraint == iNumInequalityKKTMultiplier;
	}

	/**
	 * Check the Candidate Point for Primal Feasibility
	 * 
	 * @param adblVariate The Candidate R^d Variate
	 * 
	 * @return TRUE - The Candidate Point has passed the Primal Feasibility Test
	 * 
	 * @throws java.lang.Exception Thrown if the Input in Invalid
	 */

	public boolean primalFeasibilityCheck (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		int iNumEqualityConstraint = numEqualityConstraint();

		int iNumInequalityConstraint = numInequalityConstraint();

		for (int i = 0; i < iNumEqualityConstraint; ++i) {
			if (0. != _aRdToR1EqualityConstraint[i].evaluate (adblVariate)) return false;
		}

		for (int i = 0; i < iNumInequalityConstraint; ++i) {
			if (0. < _aRdToR1InequalityConstraint[i].evaluate (adblVariate)) return false;
		}

		return true;
	}

	/**
	 * Check for Complementary Slackness across the Inequality Constraints
	 * 
	 * @param kktMultiplier The specified KKT Multipliers
	 * @param adblVariate The Candidate R^d Variate
	 * 
	 * @return TRUE - The Complementary Slackness Test passed
	 * 
	 * @throws java.lang.Exception Thrown if the Input in Invalid
	 */

	public boolean complementarySlacknessCheck (
		final org.drip.optimization.kkt.Multipliers kktMultiplier,
		final double[] adblVariate)
		throws java.lang.Exception
	{
		if (!isCompatible (kktMultiplier))
			throw new java.lang.Exception
				("OptimizationFramework::complementarySlacknessCheck => Invalid Inputs");

		int iNumInequalityConstraint = numInequalityConstraint();

		double[] adblInequalityConstraintCoefficient = null == kktMultiplier ? null :
			kktMultiplier.inequalityConstraintCoefficient();

		for (int i = 0; i < iNumInequalityConstraint; ++i) {
			if (0. != _aRdToR1InequalityConstraint[i].evaluate (adblVariate) *
				adblInequalityConstraintCoefficient[i])
				return false;
		}

		return true;
	}

	/**
	 * Active Constraint Set Rank Computation
	 * 
	 * @param adblVariate The Candidate R^d Variate
	 * 
	 * @return The Active Constraint Set Rank
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public int activeConstraintRank (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		int iNumEqualityConstraint = numEqualityConstraint();

		int iNumInequalityConstraint = numInequalityConstraint();

		java.util.List<double[]> lsJacobian = new java.util.ArrayList<double[]>();

		for (int i = 0; i < iNumEqualityConstraint; ++i) {
			double[] adblJacobian = _aRdToR1EqualityConstraint[i].jacobian (adblVariate);

			if (null == adblJacobian)
				throw new java.lang.Exception
					("OptimizationFramework::activeConstraintRank => Cannot Compute");

			lsJacobian.add (adblJacobian);
		}

		for (int i = 0; i < iNumInequalityConstraint; ++i) {
			if (0. == _aRdToR1InequalityConstraint[i].evaluate (adblVariate)) {
				double[] adblJacobian = _aRdToR1InequalityConstraint[i].jacobian (adblVariate);

				if (null == adblJacobian)
					throw new java.lang.Exception
						("OptimizationFramework::activeConstraintRank => Cannot Compute");

				lsJacobian.add (adblJacobian);
			}
		}

		int iNumJacobian = lsJacobian.size();

		double[][] aadblJacobian = new double[iNumJacobian][];

		for (int i = 0; i < iNumJacobian; ++i)
			aadblJacobian[i] = lsJacobian.get (i);

		return org.drip.quant.linearalgebra.Matrix.Rank (aadblJacobian);
	}

	/**
	 * Compare the Active Constraint Set Rank at the specified against the specified Rank
	 * 
	 * @param adblVariate The Candidate R^d Variate
	 * @param iRank The specified Rank
	 * 
	 * @return TRUE - Active Constraint Set Rank matches the specified Rank
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public boolean activeConstraintRankComparison (
		final double[] adblVariate,
		final int iRank)
		throws java.lang.Exception
	{
		return activeConstraintRank (adblVariate) == iRank;
	}

	/**
	 * Active Constraint Set Linear Dependence Check
	 * 
	 * @param adblVariate The Candidate R^d Variate
	 * @param bPositiveLinearDependenceCheck TRUE - Perform an Additional Positive Dependence Check
	 * 
	 * @return TRUE - Active Constraint Set Linear Dependence Check is satisfied
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public boolean activeConstraintLinearDependence (
		final double[] adblVariate,
		final boolean bPositiveLinearDependenceCheck)
		throws java.lang.Exception
	{
		int iNumEqualityConstraint = numEqualityConstraint();

		int iNumInequalityConstraint = numInequalityConstraint();

		int iNumConstraint = iNumEqualityConstraint + iNumInequalityConstraint;
		double[][] aadblJacobian = new double[iNumConstraint][];

		for (int i = 0; i < iNumEqualityConstraint; ++i) {
			if (null == (aadblJacobian[i] = _aRdToR1EqualityConstraint[i].jacobian (adblVariate)))
				return false;
		}

		for (int i = iNumEqualityConstraint; i < iNumConstraint; ++i) {
			if (0. == _aRdToR1InequalityConstraint[i - iNumEqualityConstraint].evaluate (adblVariate)) {
				if (null == (aadblJacobian[i] =
					_aRdToR1InequalityConstraint[i - iNumEqualityConstraint].jacobian (adblVariate)))
					return false;
			} else
				aadblJacobian[i] = null;
		}

		for (int i = 0; i < iNumConstraint; ++i) {
			if (null != aadblJacobian[i]) {
				for (int j = i + 1; j < iNumConstraint; ++j) {
					if (null != aadblJacobian[j] && 0. != org.drip.quant.linearalgebra.Matrix.DotProduct
						(aadblJacobian[i], aadblJacobian[j]))
						return false;
				}
			}
		}

		if (bPositiveLinearDependenceCheck) {
			for (int i = 0; i < iNumConstraint; ++i) {
				if (null != aadblJacobian[i] || 0. == org.drip.quant.linearalgebra.Matrix.Modulus
					(aadblJacobian[i]))
					return false;
			}
		}

		return true;
	}

	/**
	 * Check for Linearity Constraint Qualification
	 * 
	 * @return TRUE - Linearity Constraint Qualification is satisfied
	 */

	public boolean isLCQ()
	{
		int iNumEqualityConstraint = numEqualityConstraint();

		int iNumInequalityConstraint = numInequalityConstraint();

		for (int i = 0; i < iNumEqualityConstraint; ++i) {
			if (!(_aRdToR1EqualityConstraint[i] instanceof org.drip.function.rdtor1.LinearMultivariate))
				return false;
		}

		for (int i = 0; i < iNumInequalityConstraint; ++i) {
			if (!(_aRdToR1InequalityConstraint[i] instanceof org.drip.function.rdtor1.LinearMultivariate))
				return false;
		}

		return true;
	}

	/**
	 * Check for Linearity Independent Constraint Qualification
	 * 
	 * @param adblVariate The Candidate R^d Variate
	 * 
	 * @return TRUE - Linearity Independent Constraint Qualification is satisfied
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public boolean isLICQ (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		return activeConstraintLinearDependence (adblVariate, false);
	}

	/**
	 * Check for Mangasarian Fromovitz Constraint Qualification
	 * 
	 * @param adblVariate The Candidate R^d Variate
	 * 
	 * @return TRUE - The Mangasarian Fromovitz Constraint Qualification is satisfied
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public boolean isMFCQ (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		return activeConstraintLinearDependence (adblVariate, true);
	}
}
