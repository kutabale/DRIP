
package org.drip.product.params;

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
 * CTDEntry implements the Bond Futures CTD Entry Details.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CTDEntry {
	private org.drip.product.definition.Bond _bond = null;
	private double _dblForwardPrice = java.lang.Double.NaN;
	private double _dblConversionFactor = java.lang.Double.NaN;

	/**
	 * CTDEntry Constructor
	 * 
	 * @param bond The Futures CTD Bond
	 * @param dblConversionFactor The CTD Conversion Factor
	 * @param dblForwardPrice The CTD Forward Price
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CTDEntry (
		final org.drip.product.definition.Bond bond,
		final double dblConversionFactor,
		final double dblForwardPrice)
		throws java.lang.Exception
	{
		if (null == (_bond = bond) || !org.drip.quant.common.NumberUtil.IsValid (_dblConversionFactor =
			dblConversionFactor) || !org.drip.quant.common.NumberUtil.IsValid (_dblForwardPrice =
				dblForwardPrice))
			throw new java.lang.Exception ("CTDEntry Constructor => Invalid Inputs");
	}

	/**
	 * 
	 * Retrieve the CTD Bond Instance
	 * 
	 * @return The CTD Bond Instance
	 */

	public org.drip.product.definition.Bond bond()
	{
		return _bond;
	}

	/**
	 * Retrieve the CTD Conversion Factor
	 * 
	 * @return The CTD Conversion Factor
	 */

	public double conversionFactor()
	{
		return _dblConversionFactor;
	}

	/**
	 * Retrieve the CTD Forward Price
	 * 
	 * @return The CTD Forward Price
	 */

	public double forwardPrice()
	{
		return _dblForwardPrice;
	}
}
