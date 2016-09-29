
package org.drip.historical.attribution;

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
 * PositionManifestMeasureSnap contains the Metrics Snapshot associated with a Specified Manifest Measure
 *  for a given Position.
 *
 * @author Lakshmi Krishnamurthy
 */

public class PositionManifestMeasureSnap {
	private double _dblRollDown = java.lang.Double.NaN;
	private double _dblRealization = java.lang.Double.NaN;
	private double _dblSensitivity = java.lang.Double.NaN;

	/**
	 * PositionManifestMeasureSnap Constructor
	 * 
	 * @param dblRealization Manifest Measure Instance Realization
	 * @param dblSensitivity First-Order Sensitivity of the Position to the Manifest Measure
	 * @param dblRollDown Manifest Measure Roll Down
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public PositionManifestMeasureSnap (
		final double dblRealization,
		final double dblSensitivity,
		final double dblRollDown)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblRealization = dblRealization) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblSensitivity = dblSensitivity) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblRollDown = dblRollDown))
			throw new java.lang.Exception ("PositionManifestMeasureSnap ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Realized Manifest Measure Value
	 * 
	 * @return The Manifest Measure Realization
	 */

	public double realization()
	{
		return _dblRealization;
	}

	/**
	 * Retrieve the Manifest Measure Sensitivity
	 * 
	 * @return The Manifest Measure Sensitivity
	 */

	public double sensitivity()
	{
		return _dblSensitivity;
	}

	/**
	 * Retrieve the Manifest Measure Roll Down
	 * 
	 * @return The Manifest Measure Roll Down
	 */

	public double rollDown()
	{
		return _dblRollDown;
	}
}
