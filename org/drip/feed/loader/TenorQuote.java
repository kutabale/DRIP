
package org.drip.feed.loader;

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
 * TenorQuote holds the Instrument Tenor and Closing Quote.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TenorQuote {
	private java.lang.String _strTenor = "";
	private double _dblQuote = java.lang.Double.NaN;

	/**
	 * TenorQuote Constructor
	 * 
	 * @param strTenor The Closing Tenor
	 * @param dblQuote The Closing Quote
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TenorQuote (
		final java.lang.String strTenor,
		final double dblQuote)
		throws java.lang.Exception
	{
		if (null == (_strTenor = strTenor) || _strTenor.isEmpty() ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblQuote = dblQuote))
			throw new java.lang.Exception ("TenorQuote Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Closing Tenor
	 * 
	 * @return The Closing Tenor
	 */

	public java.lang.String tenor()
	{
		return _strTenor;
	}

	/**
	 * Retrieve the Closing Quote
	 * 
	 * @return The Closing Quote
	 */

	public double quote()
	{
		return _dblQuote;
	}

	@Override public java.lang.String toString()
	{
		return _strTenor + "=" + _dblQuote;
	}
}
