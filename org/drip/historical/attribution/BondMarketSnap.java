
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
 * BondMarketSnap contains the Metrics Snapshot associated with the relevant Manifest Measures for the given
 *  Bond Position.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BondMarketSnap extends org.drip.historical.attribution.PositionMarketSnap {

	/**
	 * BondMarketSnap Constructor
	 * 
	 * @param dtSnap The Snapshot Date
	 * @param dblMarketValue The Snapshot Market Value
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BondMarketSnap (
		final org.drip.analytics.date.JulianDate dtSnap,
		final double dblMarketValue)
		throws java.lang.Exception
	{
		super (dtSnap, dblMarketValue);
	}

	/**
	 * Set the Yield Level and Position Sensitivity
	 * 
	 * @param dblYield The Yield Level
	 * @param dblYieldSensitivity The Position Yield Sensitivity
	 * @param dblYieldRollDown The Position Yield Roll Down
	 * 
	 * @return TRUE - The Yield Level and the Position Sensitivity successfully set
	 */

	public boolean setYieldMarketFactor (
		final double dblYield,
		final double dblYieldSensitivity,
		final double dblYieldRollDown)
	{
		return addManifestMeasureSnap ("Yield", dblYield, dblYieldSensitivity, dblYieldRollDown);
	}
}
