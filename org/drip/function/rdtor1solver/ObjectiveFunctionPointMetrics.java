
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
 * ObjectiveFunctionPointMetrics holds the R^d Point Base and Sensitivity Metrics of the Objective Function.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ObjectiveFunctionPointMetrics {
	private double[] _adblJacobian = null;
	private double[][] _aadblHessian = null;

	/**
	 * ObjectiveFunctionPointMetrics Constructor
	 * 
	 * @param adblJacobian The Jacobian Array
	 * @param aadblHessian The Hessian Matrix
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are Invalid
	 */

	public ObjectiveFunctionPointMetrics (
		final double[] adblJacobian,
		final double[][] aadblHessian)
		throws java.lang.Exception
	{
		if (null == (_adblJacobian = adblJacobian) || null == (_aadblHessian = aadblHessian))
			throw new java.lang.Exception ("ObjectiveFunctionPointMetrics Constructor => Invalid Inputs");

		int iDimension = _adblJacobian.length;

		if (0 == iDimension || iDimension != _aadblHessian.length)
			throw new java.lang.Exception ("ObjectiveFunctionPointMetrics Constructor => Invalid Inputs");

		for (int i = 0; i < iDimension; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (adblJacobian[i]) || null == _aadblHessian[i] ||
				iDimension != _aadblHessian[i].length || !org.drip.quant.common.NumberUtil.IsValid
					(_aadblHessian[i]))
				throw new java.lang.Exception
					("ObjectiveFunctionPointMetrics Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Dimension
	 * 
	 * @return The Dimension
	 */

	public int dimension()
	{
		return _adblJacobian.length;
	}

	/**
	 * Retrieve the Jacobian Array
	 * 
	 * @return The Jacobian Array
	 */

	public double[] jacobian()
	{
		return _adblJacobian;
	}

	/**
	 * Retrieve the Hessian Matrix
	 * 
	 * @return The Hessian Matrix
	 */

	public double[][] hessian()
	{
		return _aadblHessian;
	}
}
