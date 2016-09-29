
package org.drip.portfolioconstruction.bayesian;

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
 * ProjectionImpliedConfidenceOutput holds the Results of the Idzorek 2005 Black Litterman Intuitive
 *  Projection Confidence Level Estimation Run. The References are:
 *  
 *  - He. G., and R. Litterman (1999): The Intuition behind the Black-Litterman Model Portfolios, Goldman
 *  	Sachs Asset Management
 *  
 *  - Idzorek, T. (2005): A Step-by-Step Guide to the Black-Litterman Model: Incorporating User-Specified
 *  	Confidence Levels, Ibbotson Associates, Chicago
 *
 * @author Lakshmi Krishnamurthy
 */

public class ProjectionImpliedConfidenceOutput {
	private double[] _adblUnadjustedWeight = null;
	private org.drip.portfolioconstruction.bayesian.BlackLittermanOutput _bloFullConfidence = null;
	private org.drip.portfolioconstruction.bayesian.BlackLittermanCustomConfidenceOutput _blcco = null;

	/**
	 * ProjectionImpliedConfidenceOutput Constructor
	 * 
	 * @param adblUnadjustedWeight Array of the Unadjusted Weights
	 * @param blcco The Custom Confidence Black Litterman Run Output
	 * @param bloFullConfidence The Full Confidence Black Litterman Run Output
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ProjectionImpliedConfidenceOutput (
		final double[] adblUnadjustedWeight,
		final org.drip.portfolioconstruction.bayesian.BlackLittermanCustomConfidenceOutput blcco,
		final org.drip.portfolioconstruction.bayesian.BlackLittermanOutput bloFullConfidence)
		throws java.lang.Exception
	{
		if (null == (_adblUnadjustedWeight = adblUnadjustedWeight) || 0 == _adblUnadjustedWeight.length ||
			null == (_blcco = blcco) || null == (_bloFullConfidence = bloFullConfidence))
			throw new java.lang.Exception
				("ProjectionImpliedConfidenceOutput Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Array of the Unadjusted Equilibrium Weights
	 * 
	 * @return The Array of the Unadjusted Equilibrium Weights
	 */

	public double[] unadjustedWeights()
	{
		return _adblUnadjustedWeight;
	}

	/**
	 * Retrieve the Custom Projection Confidence Black Litterman Run Output
	 * 
	 * @return The Custom Projection Confidence Black Litterman Run Output
	 */

	public org.drip.portfolioconstruction.bayesian.BlackLittermanCustomConfidenceOutput
		customConfidenceOutput()
	{
		return _blcco;
	}

	/**
	 * Retrieve the Full Projection Confidence Black Litterman Run Output
	 * 
	 * @return The Full Projection Confidence Black Litterman Run Output
	 */

	public org.drip.portfolioconstruction.bayesian.BlackLittermanOutput fullConfidenceOutput()
	{
		return _bloFullConfidence;
	}

	/**
	 * Retrieve the Custom Projection Induced Equilibrium Asset Deviation Array
	 * 
	 * @return The Custom Projection Induced Equilibrium Asset Deviation Array
	 */

	public double[] customProjectionConfidenceDeviation()
	{
		return _blcco.allocationAdjustmentTilt();
	}

	/**
	 * Retrieve the Custom Projection Induced Equilibrium Asset Weight Array
	 * 
	 * @return The Custom Projection Induced Equilibrium Asset Weight Array
	 */

	public double[] customProjectionConfidenceWeight()
	{
		return _blcco.adjustedMetrics().optimalPortfolio().weights();
	}

	/**
	 * Retrieve the Full Projection Induced Equilibrium Asset Deviation Array
	 * 
	 * @return The Full Projection Induced Equilibrium Asset Deviation Array
	 */

	public double[] fullProjectionConfidenceDeviation()
	{
		return _bloFullConfidence.allocationAdjustmentTilt();
	}

	/**
	 * Retrieve the Full Projection Induced Equilibrium Asset Weight Array
	 * 
	 * @return The Full Projection Induced Equilibrium Asset Weight Array
	 */

	public double[] fullProjectionConfidenceWeight()
	{
		return _bloFullConfidence.adjustedMetrics().optimalPortfolio().weights();
	}

	/**
	 * Compute the Array of the Custom Projection Induced Confidence Level
	 * 
	 * @return The Array of the Custom Projection Induced Confidence Level
	 */

	public double[] level()
	{
		int iNumAsset = _adblUnadjustedWeight.length;
		double[] adblImpliedConfidenceLevel = new double[iNumAsset];

		double[] adblCustomProjectionConfidenceDeviation = _blcco.allocationAdjustmentTilt();

		double[] adblFullProjectionConfidenceDeviation = _bloFullConfidence.allocationAdjustmentTilt();

		for (int i = 0; i < iNumAsset; ++i)
			adblImpliedConfidenceLevel[i] = adblCustomProjectionConfidenceDeviation[i] /
				adblFullProjectionConfidenceDeviation[i];

		return adblImpliedConfidenceLevel;
	}
}
