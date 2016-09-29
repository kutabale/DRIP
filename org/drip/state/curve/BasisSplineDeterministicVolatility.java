
package org.drip.state.curve;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * BasisSplineDeterministicVolatility extends the BasisSplineTermStructure for the specific case of the
 * 	Implementation of the Deterministic Volatility Term Structure.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BasisSplineDeterministicVolatility extends org.drip.state.volatility.VolatilityCurve {
	private org.drip.spline.grid.Span _spanImpliedVolatility = null;

	/**
	 * BasisSplineDeterministicVolatility Constructor
	 * 
	 * @param iEpochDate The Epoch Date
	 * @param label Latent State Label
	 * @param strCurrency The Currency
	 * @param spanImpliedVolatility The Implied Volatility Span
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BasisSplineDeterministicVolatility (
		final int iEpochDate,
		final org.drip.state.identifier.CustomLabel label,
		final java.lang.String strCurrency,
		final org.drip.spline.grid.Span spanImpliedVolatility)
		throws java.lang.Exception
	{
		super (iEpochDate, label, strCurrency);

		if (null == (_spanImpliedVolatility = spanImpliedVolatility))
			throw new java.lang.Exception ("BasisSplineDeterministicVolatility ctr: Invalid Inputs");
	}

	@Override public double impliedVol (
		final int iDate)
		throws java.lang.Exception
	{
		double dblSpanLeft = _spanImpliedVolatility.left();

		if (dblSpanLeft >= iDate) return _spanImpliedVolatility.calcResponseValue (dblSpanLeft);

		double dblSpanRight = _spanImpliedVolatility.right();

		if (dblSpanRight <= iDate) return _spanImpliedVolatility.calcResponseValue (dblSpanRight);

		return _spanImpliedVolatility.calcResponseValue (iDate);
	}

	@Override public double node (
		final int iDate)
		throws java.lang.Exception
	{
		double dblImpliedVol = impliedVol (iDate);

		return java.lang.Math.sqrt (dblImpliedVol * dblImpliedVol + 2. * dblImpliedVol * (iDate -
			epoch().julian()) / 365.25 * _spanImpliedVolatility.calcResponseValueDerivative (iDate, 1));
	}

	@Override public double vol (
		final int iDate)
		throws java.lang.Exception
	{
		return node (iDate);
	}

	@Override public double nodeDerivative (
		final int iDate,
		final int iOrder)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (iDate))
			throw new java.lang.Exception
				("BasisSplineDeterministicVolatility::nodeDerivative => Invalid Inputs");

		org.drip.function.definition.R1ToR1 au = new org.drip.function.definition.R1ToR1
			(null) {
			@Override public double evaluate (
				double dblX)
				throws java.lang.Exception
			{
				return node ((int) dblX);
			}
		};

		return au.derivative (iDate, iOrder);
	}
}
