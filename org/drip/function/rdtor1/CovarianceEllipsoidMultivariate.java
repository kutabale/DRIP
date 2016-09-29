
package org.drip.function.rdtor1;

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
 * CovarianceEllipsoidMultivariate implements an R^d -> R^1 Co-variance Estimate of the specified Distribution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CovarianceEllipsoidMultivariate extends org.drip.function.definition.RdToR1 {
	private double[][] _aadblCovarianceMatrix = null;

	/**
	 * CovarianceEllipsoidMultivariate Constructor
	 * 
	 * @param aadblCovarianceMatrix The Covariance Matrix
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CovarianceEllipsoidMultivariate (
		final double[][] aadblCovarianceMatrix)
		throws java.lang.Exception
	{
		super (null);

		if (null == (_aadblCovarianceMatrix = aadblCovarianceMatrix))
			throw new java.lang.Exception ("CovarianceEllipsoidMultivariate Constructor => Invalid Inputs");

		int iSize = _aadblCovarianceMatrix.length;

		if (0 == iSize)
			throw new java.lang.Exception ("CovarianceEllipsoidMultivariate Constructor => Invalid Inputs");

		for (int i = 0; i < iSize; ++i) {
			if (null == _aadblCovarianceMatrix[i] || iSize != _aadblCovarianceMatrix[i].length ||
				!org.drip.quant.common.NumberUtil.IsValid (_aadblCovarianceMatrix[i]))
				throw new java.lang.Exception
					("CovarianceEllipsoidMultivariate Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Input Variate Dimension
	 * 
	 * @return The Input Variate Dimension
	 */

	public int dimension()
	{
		return _aadblCovarianceMatrix.length;
	}

	/**
	 * Retrieve the Co-variance Matrix
	 * 
	 * @return The Co-variance Matrix
	 */

	public double[][] covariance()
	{
		return _aadblCovarianceMatrix;
	}

	@Override public double evaluate (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		if (null == adblVariate || !org.drip.quant.common.NumberUtil.IsValid (adblVariate))
			throw new java.lang.Exception ("CovarianceEllipsoidMultivariate::evaluate => Invalid Inputs");

		double dblCovariance = 0.;
		int iDimension = adblVariate.length;

		if (iDimension != dimension())
			throw new java.lang.Exception ("CovarianceEllipsoidMultivariate::evaluate => Invalid Inputs");

		for (int i = 0; i < iDimension; ++i) {
			for (int j = 0; j < iDimension; ++j)
				dblCovariance += adblVariate[i] * _aadblCovarianceMatrix[i][j] * adblVariate[j];
		}

		return dblCovariance;
	}

	@Override public double[] jacobian (
		final double[] adblVariate)
	{
		if (null == adblVariate || !org.drip.quant.common.NumberUtil.IsValid (adblVariate)) return null;

		int iDimension = adblVariate.length;
		double[] adblJacobian = new double[iDimension];

		if (iDimension != dimension()) return null;

		for (int i = 0; i < iDimension; ++i) {
			adblJacobian[i] = 0.;

			for (int j = 0; j < iDimension; ++j)
				adblJacobian[i] += 2. * _aadblCovarianceMatrix[i][j] * adblVariate[j];
		}

		return adblJacobian;
	}

	@Override public double[][] hessian (
		final double[] adblVariate)
	{
		int iDimension = dimension();

		double[][] aadblHessian = new double[iDimension][iDimension];

		for (int i = 0; i < iDimension; ++i) {
			for (int j = 0; j < iDimension; ++j)
				aadblHessian[i][j] += 2. * _aadblCovarianceMatrix[i][j];
		}

		return aadblHessian;
	}
}
