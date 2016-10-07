
package org.drip.product.calib;

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
 * VolatilityProductQuoteSet implements the Calibratable Volatility Product Quote Shell.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class VolatilityProductQuoteSet extends org.drip.product.calib.ProductQuoteSet {

	/**
	 * Volatility Product Quote Set Constructor
	 * 
	 * @param aLSS Array of Latent State Specifications
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public VolatilityProductQuoteSet (
		final org.drip.state.representation.LatentStateSpecification[] aLSS)
		throws java.lang.Exception
	{
		super (aLSS);
	}

	/**
	 * Set the PV of an Option on the Product
	 * 
	 * @param dblOptionPV The PV of an Option on the Product
	 * 
	 * @return TRUE - PV of an Option on the Product successfully set
	 */

	public boolean setOptionPV (
		final double dblOptionPV)
	{
		return set ("OptionPV", dblOptionPV);
	}

	/**
	 * Indicate if the PV of an Option on the Product Field exists
	 * 
	 * @return TRUE - PV of an Option on the Product Field Exists
	 */

	public boolean containsOptionPV()
	{
		return contains ("OptionPV");
	}

	/**
	 * Retrieve the PV of an Option on the Product
	 * 
	 * @return The PV of an Option on the Product
	 * 
	 * @throws java.lang.Exception Thrown if the PV of an Option on the Product Field does not exist
	 */

	public double optionPV()
		throws java.lang.Exception
	{
		if (!containsOptionPV())
			throw new java.lang.Exception ("ProductQuoteSet::optionPV - Does not contain Option PV");

		return get ("OptionPV");
	}
}
