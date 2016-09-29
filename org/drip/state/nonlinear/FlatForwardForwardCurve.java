
package org.drip.state.nonlinear;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * FlatForwardForwardCurve contains an implementation of the flat forward rate forward curve.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FlatForwardForwardCurve extends org.drip.state.forward.ForwardCurve {
	private double _dblFlatForwardRate = java.lang.Double.NaN;

	/**
	 * FlatForwardForwardCurve constructor
	 * 
	 * @param dtEpoch The Forward Curve Epoch Date
	 * @param fri The Floating Rate Index
	 * @param dblFlatForwardRate The Flat FOrward Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public FlatForwardForwardCurve (
		final org.drip.analytics.date.JulianDate dtEpoch,
		final org.drip.state.identifier.ForwardLabel fri,
		final double dblFlatForwardRate)
		throws java.lang.Exception
	{
		super (dtEpoch.julian(), fri);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblFlatForwardRate = dblFlatForwardRate))
			throw new java.lang.Exception ("FlatForwardForwardCurve ctr: Invalid Inputs");
	}

	@Override public double forward (
		final int iDate)
		throws java.lang.Exception
	{
		return _dblFlatForwardRate;
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDForwardDManifestMeasure (
		final java.lang.String strManifestMeasure,
		final int iDate)
	{
		return null;
	}
}
