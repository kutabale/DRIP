
package org.drip.sample.rdtor1;

import org.drip.function.rdtor1.*;
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
 * UnconstrainedCovarianceEllipsoid demonstrates the Construction and Usage of a Co-variance Ellipsoid.
 *
 * @author Lakshmi Krishnamurthy
 */

public class UnconstrainedCovarianceEllipsoid {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double[][] aadblCovarianceMatrix = new double[][] {
			{0.09, 0.12},
			{0.12, 0.04}
		};

		CovarianceEllipsoidMultivariate ce = new CovarianceEllipsoidMultivariate (aadblCovarianceMatrix);

		double[][] aadblVariate = {
			{0.0, 1.0},
			{0.1, 0.9},
			{0.2, 0.8},
			{0.3, 0.7},
			{0.4, 0.6},
			{0.5, 0.5},
			{0.6, 0.4},
			{0.7, 0.3},
			{0.8, 0.2},
			{0.9, 0.1},
			{1.0, 0.0},
		};

		System.out.println ("\n\n\t|------------------------||");

		System.out.println ("\t|       POINT VALUE      ||");

		System.out.println ("\t|------------------------||");

		for (double[] adblVariate : aadblVariate)
			System.out.println (
				"\t|  [" + adblVariate[0] +
				" | " + adblVariate[1] +
				"] = " + FormatUtil.FormatDouble (ce.evaluate (adblVariate), 1, 4, 1.) + " ||"
			);

		System.out.println ("\t|------------------------||");

		System.out.println ("\n\n\t|-----------------------------------||");

		System.out.println ("\t|             JACOBIAN              ||");

		System.out.println ("\t|-----------------------------------||");

		for (double[] adblVariate : aadblVariate) {
			String strJacobian = "";

			double[] adblJacobian = ce.jacobian (adblVariate);

			for (double dblJacobian : adblJacobian)
				strJacobian += FormatUtil.FormatDouble (dblJacobian, 1, 4, 1.) + ",";

			System.out.println (
				"\t|  [" + adblVariate[0] +
				" | " + adblVariate[1] +
				"] = {" + strJacobian + "} ||"
			);
		}

		System.out.println ("\t|-----------------------------------||");

		double[][] aadblHessian = ce.hessian (
			new double[] {
				0.20,
				0.80
			}
		);

		System.out.println ("\n\n\t|--------------------||");

		System.out.println ("\t|      HESSIAN       ||");

		System.out.println ("\t|--------------------||");

		for (double[] adblHessian : aadblHessian) {
			String strHessian = "";

			for (double dblHessian : adblHessian)
				strHessian += FormatUtil.FormatDouble (dblHessian, 1, 4, 1.) + ",";

			System.out.println ("\t| [" + strHessian + "] ||");
		}

		System.out.println ("\t|--------------------||");
	}
}
