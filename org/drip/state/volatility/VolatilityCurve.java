
package org.drip.state.volatility;

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
 * VolatilityCurve exposes the Stub that implements the Latent State's Deterministic Volatility Term
 * 	Structure Curve - by Construction, this is expected to be non-local.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class VolatilityCurve extends org.drip.analytics.definition.NodeStructure {

	protected VolatilityCurve (
		final int iEpochDate,
		final org.drip.state.identifier.LatentStateLabel label,
		final java.lang.String strCurrency)
		throws java.lang.Exception
	{
		super (iEpochDate, label, strCurrency);
	}

	/**
	 * Compute the Deterministic Implied Volatility at the Date Node from the Volatility Term Structure
	 * 
	 * @param iDate The Date Node
	 * 
	 * @return The Deterministic Implied Volatility at the Date Node from the Volatility Term Structure
	 * 
	 * @throws java.lang.Exception Thrown if the Deterministic Implied Volatility cannot be computed
	 */

	public abstract double impliedVol (
		final int iDate)
		throws java.lang.Exception;

	/**
	 * Compute the Deterministic Implied Volatility at the Date Node from the Volatility Term Structure
	 * 
	 * @param dt The Date Node
	 * 
	 * @return The Deterministic Implied Volatility at the Date Node from the Volatility Term Structure
	 * 
	 * @throws java.lang.Exception Thrown if the Deterministic Implied Volatility cannot be computed
	 */

	public double impliedVol (
		final org.drip.analytics.date.JulianDate dt)
		throws java.lang.Exception
	{
		if (null == dt) throw new java.lang.Exception ("VolatilityCurve::impliedVol => Invalid Inputs!");

		return impliedVol (dt.julian());
	}

	/**
	 * Compute the Deterministic Implied Volatility at the Tenor from the Volatility Term Structure
	 * 
	 * @param strTenor The Date Node
	 * 
	 * @return The Deterministic Implied Volatility at the Tenor from the Volatility Term Structure
	 * 
	 * @throws java.lang.Exception Thrown if the Deterministic Implied Volatility cannot be computed
	 */

	public double impliedVol (
		final java.lang.String strTenor)
		throws java.lang.Exception
	{
		if (null == strTenor || strTenor.isEmpty())
			throw new java.lang.Exception ("VolatilityCurve::impliedVol => Invalid Inputs!");

		return impliedVol (epoch().addTenor (strTenor).julian());
	}

	/**
	 * Compute the Deterministic Implied Volatility at the Date Node from the Volatility Term Structure
	 * 
	 * @param iDate The Date Node
	 * 
	 * @return The Deterministic Implied Volatility at the Date Node from the Volatility Term Structure
	 * 
	 * @throws java.lang.Exception Thrown if the Deterministic Implied Volatility cannot be computed
	 */

	public abstract double vol (
		final int iDate)
		throws java.lang.Exception;
}
