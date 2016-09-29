
package org.drip.sample.semidefinite;

import org.drip.function.definition.RdToR1;
import org.drip.function.rdtor1.*;
import org.drip.function.rdtor1descent.LineStepEvolutionControl;
import org.drip.function.rdtor1solver.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * TwoVariateConstrainedVariance demonstrates the Application of the Interior Point Method for minimizing
 * 	the Variance Across Two Variates under the Normalization Constraint.
 *
 * @author Lakshmi Krishnamurthy
 */

public class TwoVariateConstrainedVariance {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double[][] aadblCovarianceMatrix = new double[][] {
			{0.09, 0.12},
			{0.12, 0.04}
		};

		double[] adblEqualityConstraint = new double[] {
			1.,
			1.
		};

		double dblEqualityConstraintConstant = -1.;
		int iObjectiveDimension = aadblCovarianceMatrix.length;

		RdToR1[] aEqualityConstraintRdToR1 = new LinearMultivariate[] {
			new LinearMultivariate (
				adblEqualityConstraint,
				dblEqualityConstraintConstant
			)
		};

		int iNumEqualityConstraint = aEqualityConstraintRdToR1.length;

		LinearBoundMultivariate lmbConstraint1 = new LinearBoundMultivariate (
			true,
			0,
			2 + iNumEqualityConstraint,
			0.65
		);

		LinearBoundMultivariate lmbConstraint2 = new LinearBoundMultivariate (
			true,
			1,
			2 + iNumEqualityConstraint,
			0.65
		);

		LinearBoundMultivariate lmbConstraint3 = new LinearBoundMultivariate (
			false,
			0,
			2 + iNumEqualityConstraint,
			0.15
		);

		LinearBoundMultivariate lmbConstraint4 = new LinearBoundMultivariate (
			false,
			1,
			2 + iNumEqualityConstraint,
			0.15
		);

		RdToR1[] aRdToR1InequalityConstraint = new RdToR1[] {
			lmbConstraint1,
			lmbConstraint2,
			lmbConstraint3,
			lmbConstraint4
		};

		double dblBarrierStrength = 1.;

		CovarianceEllipsoidMultivariate ce = new CovarianceEllipsoidMultivariate (aadblCovarianceMatrix);

		LagrangianMultivariate ceec = new LagrangianMultivariate (
			ce,
			aEqualityConstraintRdToR1
		);

		double[] adblStartingVariate = ObjectiveConstraintVariateSet.Uniform (
			iObjectiveDimension,
			1
		);

		InteriorPointBarrierControl ipbc = InteriorPointBarrierControl.Standard();

		BarrierFixedPointFinder ifpm = new BarrierFixedPointFinder (
			ceec,
			aRdToR1InequalityConstraint,
			ipbc,
			LineStepEvolutionControl.NocedalWrightStrongWolfe (false)
		);

		VariateInequalityConstraintMultiplier vcmt = ifpm.solve (adblStartingVariate);

		System.out.println ("\n\n\t|----------------------------------------------------||");

		System.out.println (
			"\t| OPTIMAL VARIATES => " + FormatUtil.FormatDouble (vcmt.variates()[0], 1, 5, 1.) +
			" | " + FormatUtil.FormatDouble (vcmt.variates()[1], 1, 5, 1.) +
			" | " + FormatUtil.FormatDouble (ceec.evaluate (vcmt.variates()), 1, 5, 1.) + " ||"
		);

		System.out.println ("\t|----------------------------------------------------||\n\n");

		int iStepDown = 20;

		double[] adblConstraintMultiplier = new double[aRdToR1InequalityConstraint.length];

		for (int i = 0; i < aRdToR1InequalityConstraint.length; ++i)
			adblConstraintMultiplier[i] = dblBarrierStrength / aRdToR1InequalityConstraint[i].evaluate (adblStartingVariate);

		vcmt = new VariateInequalityConstraintMultiplier (
			false,
			adblStartingVariate,
			adblConstraintMultiplier
		);

		ConvergenceControl cc = new ConvergenceControl (
			ConvergenceControl.OBJECTIVE_FUNCTION_SEQUENCE_CONVERGENCE,
			5.0e-02,
			1.0e-06,
			70
		);

		System.out.println ("\t|-------------------------------------------------||");

		System.out.println ("\t|    BARRIER    =>      VARIATES       | VARIANCE ||");

		System.out.println ("\t|-------------------------------------------------||");

		while (--iStepDown > 0) {
			InteriorFixedPointFinder bfpf = new InteriorFixedPointFinder (
				ceec,
				aRdToR1InequalityConstraint,
				LineStepEvolutionControl.NocedalWrightStrongWolfe (false),
				cc,
				dblBarrierStrength
			);

			vcmt = bfpf.find (vcmt);

			adblStartingVariate = vcmt.variates();

			System.out.println (
				"\t| " + FormatUtil.FormatDouble (dblBarrierStrength, 1, 10, 1.) +
				" => " + FormatUtil.FormatDouble (vcmt.variates()[0], 1, 5, 1.) +
				" | " + FormatUtil.FormatDouble (vcmt.variates()[1], 1, 5, 1.) +
				" | " + FormatUtil.FormatDouble (ceec.evaluate (vcmt.variates()), 1, 5, 1.) + " ||"
			);

			dblBarrierStrength *= 0.5;
		}

		System.out.println ("\t|-------------------------------------------------||\n\n");
	}
}
