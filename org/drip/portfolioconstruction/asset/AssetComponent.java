
package org.drip.portfolioconstruction.asset;

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
 * AssetComponent holds the Amount of an Asset given by the corresponding ID.
 *
 * @author Lakshmi Krishnamurthy
 */

public class AssetComponent {
	private java.lang.String _strID = "";
	private double _dblAmount = java.lang.Double.NaN;

	/**
	 * AssetComponent Constructor
	 * 
	 * @param strID Asset ID
	 * @param dblAmount Asset Amount
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public AssetComponent (
		final java.lang.String strID,
		final double dblAmount)
		throws java.lang.Exception
	{
		if (null == (_strID = strID) || _strID.isEmpty() || !org.drip.quant.common.NumberUtil.IsValid
			(_dblAmount = dblAmount))
			throw new java.lang.Exception ("AssetComponent Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Asset ID
	 * 
	 * @return The Asset ID
	 */

	public java.lang.String id()
	{
		return _strID;
	}

	/**
	 * Retrieve the Asset Amount
	 * 
	 * @return The Asset Amount
	 */

	public double amount()
	{
		return _dblAmount;
	}
}
