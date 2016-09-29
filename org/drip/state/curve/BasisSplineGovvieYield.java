
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
 * BasisSplineGovvieYield manages the Basis Spline Latent State, using the Basis as the State Response
 *  Representation, for the Govvie Curve with Yield Quantification Metric.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BasisSplineGovvieYield extends org.drip.state.govvie.GovvieCurve {
	private org.drip.spline.grid.Span _span = null;

	/**
	 * BasisSplineGovvieYield Constructor
	 * 
	 * @param strTreasuryCode Treasury Code
	 * @param strCurrency Currency
	 * @param span Govvie Curve Span
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BasisSplineGovvieYield (
		final java.lang.String strTreasuryCode,
		final java.lang.String strCurrency,
		final org.drip.spline.grid.Span span)
		throws java.lang.Exception
	{
		super ((int) span.left(), strTreasuryCode, strCurrency);

		_span = span;
	}

	@Override public double yield (
		final int iDate)
		throws java.lang.Exception
	{
		double dblSpanLeft = _span.left();

		if (iDate <= dblSpanLeft) return _span.calcResponseValue (dblSpanLeft);

		double dblSpanRight = _span.right();

		if (iDate >= dblSpanRight) return _span.calcResponseValue (dblSpanRight);

		return _span.calcResponseValue (iDate);
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDForwardDManifestMeasure (
		final java.lang.String strManifestMeasure,
		final int iDate)
	{
		return _span.jackDResponseDManifestMeasure (strManifestMeasure, iDate, 1);
	}
}
