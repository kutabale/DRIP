
package org.drip.measure.gaussian;

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
 * R1MultivariateNormal contains the Generalized Joint Multivariate R^1 Normal Distributions.
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1MultivariateNormal extends org.drip.measure.continuousjoint.R1Multivariate {
	private double[] _adblMean = null;
	private org.drip.measure.gaussian.Covariance _covariance = null;

	/**
	 * Construct a Standard R1MultivariateNormal Instance
	 * 
	 * @param meta The R^1 Multivariate Meta Headers
	 * @param adblMean Array of the Univariate Means
	 * @param aadblCovariance The Covariance Matrix
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public static final R1MultivariateNormal Standard (
		final org.drip.measure.continuousjoint.MultivariateMeta meta,
		final double[] adblMean,
		final double[][] aadblCovariance)
	{
		try {
			return new R1MultivariateNormal (meta, adblMean, new org.drip.measure.gaussian.Covariance
				(aadblCovariance));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Standard R1MultivariateNormal Instance
	 * 
	 * @param astrVariateID Array of Variate IDs
	 * @param adblMean Array of the Univariate Means
	 * @param aadblCovariance The Covariance Matrix
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public static final R1MultivariateNormal Standard (
		final java.lang.String[] astrVariateID,
		final double[] adblMean,
		final double[][] aadblCovariance)
	{
		try {
			return new R1MultivariateNormal (new org.drip.measure.continuousjoint.MultivariateMeta
				(astrVariateID), adblMean, new org.drip.measure.gaussian.Covariance (aadblCovariance));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * R1MultivariateNormal Constructor
	 * 
	 * @param meta The R^1 Multivariate Meta Headers
	 * @param adblMean Array of the Univariate Means
	 * @param covariance The Multivariate Covariance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public R1MultivariateNormal (
		final org.drip.measure.continuousjoint.MultivariateMeta meta,
		final double[] adblMean,
		final org.drip.measure.gaussian.Covariance covariance)
		throws java.lang.Exception
	{
		super (meta);

		if (null == (_adblMean = adblMean) || null == (_covariance = covariance))
			throw new java.lang.Exception ("R1MultivariateNormal Constructor => Invalid Inputs!");

		int iNumVariate = meta.numVariable();

		if (iNumVariate != _adblMean.length || iNumVariate != _covariance.numVariate() ||
			!org.drip.quant.common.NumberUtil.IsValid (_adblMean)) {
			System.out.println ("iNumVariate = " + iNumVariate);

			System.out.println ("_adblMean = " + _adblMean.length);

			throw new java.lang.Exception ("R1MultivariateNormal Constructor => Invalid Inputs!");
		}
	}

	/**
	 * Compute the Co-variance of the Distribution
	 * 
	 * @return The Co-variance of the Distribution
	 */

	public org.drip.measure.gaussian.Covariance covariance()
	{
		return _covariance;
	}

	@Override public double density (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		if (null == adblVariate || !org.drip.quant.common.NumberUtil.IsValid (adblVariate))
			throw new java.lang.Exception ("R1MultivariateNormal::density => Invalid Inputs");

		double dblDensity = 0.;
		int iNumVariate = _adblMean.length;
		double[] adblVariateOffset = new double[iNumVariate];

		if (iNumVariate != adblVariate.length)
			throw new java.lang.Exception ("R1MultivariateNormal Constructor => Invalid Inputs!");

		for (int i = 0; i < iNumVariate; ++i)
			adblVariateOffset[i] = adblVariate[i] - _adblMean[i];

		double[][] aadblPrecision = _covariance.precisionMatrix();

		for (int i = 0; i < iNumVariate; ++i) {
			for (int j = 0; j < iNumVariate; ++j)
				dblDensity = dblDensity + adblVariateOffset[i] * aadblPrecision[i][j] *
					adblVariateOffset[j];
		}

		return java.lang.Math.exp (dblDensity) * java.lang.Math.pow (2. * java.lang.Math.PI, -0.5 *
			iNumVariate);
	}

	@Override public double[] mean()
	{
		return _adblMean;
	}

	@Override public double[] variance()
	{
		return _covariance.variance();
	}
}
