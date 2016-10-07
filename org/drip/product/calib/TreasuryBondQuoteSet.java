
package org.drip.product.calib;

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
 * TreasuryBondQuoteSet extends the ProductQuoteSet by implementing the Calibration Parameters for the
 *  Treasury Bond Component. Currently it exposes the Yield.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TreasuryBondQuoteSet extends org.drip.product.calib.ProductQuoteSet {

	/**
	 * TreasuryBondQuoteSet Constructor
	 * 
	 * @param aLSS Array of Latent State Specification
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public TreasuryBondQuoteSet (
		final org.drip.state.representation.LatentStateSpecification[] aLSS)
		throws java.lang.Exception
	{
		super (aLSS);
	}

	/**
	 * Set the Yield
	 * 
	 * @param dblYield The Yield
	 * 
	 * @return TRUE - The Yield successfully set
	 */

	public boolean setYield (
		final double dblYield)
	{
		return set ("Yield", dblYield);
	}

	/**
	 * Indicate if the Yield Field exists
	 * 
	 * @return TRUE - Yield Field Exists
	 */

	public boolean containsYield()
	{
		return contains ("Yield");
	}

	/**
	 * Retrieve the Yield
	 * 
	 * @return Yield
	 * 
	 * @throws java.lang.Exception Thrown if the Yield Field does not exist
	 */

	public double yield()
		throws java.lang.Exception
	{
		return get ("Yield");
	}
}
