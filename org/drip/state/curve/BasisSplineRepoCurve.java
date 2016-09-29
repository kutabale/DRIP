
package org.drip.state.curve;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
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
 * BasisSplineRepoCurve manages the Basis Latent State, using the Repo as the State Response Representation.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BasisSplineRepoCurve extends org.drip.state.repo.RepoCurve {
	private org.drip.spline.grid.Span _span = null;

	/**
	 * BasisSplineRepoCurve constructor
	 * 
	 * @param comp The Underlying Repo Component
	 * @param span The Span over which the Basis Representation is valid
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BasisSplineRepoCurve (
		final org.drip.product.definition.Component comp,
		final org.drip.spline.grid.Span span)
		throws java.lang.Exception
	{
		super ((int) span.left(), comp);

		_span = span;
	}

	@Override public double repo (
		final int iDate)
		throws java.lang.Exception
	{
		if (iDate < _span.left() || iDate >= component().maturityDate().julian())
			throw new java.lang.Exception ("BasisSplineRepoCurve::repo => Invalid Input");

		double dblSpanRight = _span.right();

		if (iDate >= dblSpanRight) return _span.calcResponseValue (dblSpanRight);

		return _span.calcResponseValue (iDate);
	}
}
