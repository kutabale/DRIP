
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
 * AssetBounds holds the Upper/Lower Bounds on an Asset.
 *
 * @author Lakshmi Krishnamurthy
 */

public class AssetBounds {
	private double _dblLower = java.lang.Double.NaN;
	private double _dblUpper = java.lang.Double.NaN;

	/**
	 * AssetBounds Constructor
	 * 
	 * @param dblLower The Asset Lower Bound
	 * @param dblUpper The Asset Upper Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public AssetBounds (
		final double dblLower,
		final double dblUpper)
		throws java.lang.Exception
	{
		_dblLower = dblLower;
		_dblUpper = dblUpper;

		if (org.drip.quant.common.NumberUtil.IsValid (_dblLower) && org.drip.quant.common.NumberUtil.IsValid
			(_dblUpper) && _dblLower >= _dblUpper)
			throw new java.lang.Exception ("AssetBounds Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Lower Bound
	 * 
	 * @return The Lower Bound
	 */

	public double lower()
	{
		return _dblLower;
	}

	/**
	 * Retrieve the Upper Bound
	 * 
	 * @return The Upper Bound
	 */

	public double upper()
	{
		return _dblUpper;
	}

	/**
	 * Retrieve a Viable Feasible Starting Point
	 * 
	 * @return A Viable Feasible Starting Point
	 */

	public double feasibleStart()
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblLower) &&
			!org.drip.quant.common.NumberUtil.IsValid (_dblUpper))
			return 0.;

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblLower)) return 0.5 * _dblUpper;

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblUpper)) return 2.0 * _dblLower;

		return 0.5 * (_dblLower + _dblUpper);
	}

	/**
	 * Localize the Variate Value to within the Bounds
	 * 
	 * @param dblVariate The Variate Value
	 * 
	 * @return The Localized Variate Value
	 * 
	 * @throws java.lang.Exception Thrown if the Input is Invalid
	 */

	public double localize (
		final double dblVariate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblVariate))
			throw new java.lang.Exception ("AssetBounds::localize => Invalid Inputs");

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblLower) &&
			!org.drip.quant.common.NumberUtil.IsValid (_dblUpper))
			return dblVariate;

		if (org.drip.quant.common.NumberUtil.IsValid (_dblLower) && dblVariate < _dblLower) return _dblLower;

		if (org.drip.quant.common.NumberUtil.IsValid (_dblUpper) && dblVariate > _dblUpper) return _dblUpper;

		return dblVariate;
	}
}
