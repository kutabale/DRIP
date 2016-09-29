
package org.drip.portfolioconstruction.params;

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
 * AssetStatisticalProperties holds the Statistical Properties of a given Asset.
 *
 * @author Lakshmi Krishnamurthy
 */

public class AssetStatisticalProperties {
	private java.lang.String _strID = "";
	private java.lang.String _strName = "";
	private double _dblVariance = java.lang.Double.NaN;
	private double _dblExpectedReturn = java.lang.Double.NaN;

	/**
	 * AssetStatisticalProperties Constructor
	 * 
	 * @param strName Name of the Asset
	 * @param strID ID of the Asset
	 * @param dblExpectedReturn Expected Return of the Asset
	 * @param dblVariance Variance of the Assert
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public AssetStatisticalProperties (
		final java.lang.String strName,
		final java.lang.String strID,
		final double dblExpectedReturn,
		final double dblVariance)
		throws java.lang.Exception
	{
		if (null == (_strName = strName) || _strName.isEmpty() || null == (_strID = strID) ||
			_strID.isEmpty() || !org.drip.quant.common.NumberUtil.IsValid (_dblExpectedReturn =
				dblExpectedReturn) || !org.drip.quant.common.NumberUtil.IsValid (_dblVariance = dblVariance))
			throw new java.lang.Exception ("AssetStatisticalProperties Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Name of the Asset
	 * 
	 * @return Name of the Asset
	 */

	public java.lang.String name()
	{
		return _strName;
	}

	/**
	 * Retrieve the ID of the Asset
	 * 
	 * @return ID of the Asset
	 */

	public java.lang.String id()
	{
		return _strID;
	}

	/**
	 * Retrieve the Expected Returns of the Asset
	 * 
	 * @return Expected Returns of the Asset
	 */

	public double expectedReturn()
	{
		return _dblExpectedReturn;
	}

	/**
	 * Retrieve the Variance of the Asset
	 * 
	 * @return Variance of the Asset
	 */

	public double variance()
	{
		return _dblVariance;
	}
}
