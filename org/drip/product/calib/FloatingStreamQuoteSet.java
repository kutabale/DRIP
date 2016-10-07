
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
 * FloatingStreamQuoteSet extends the ProductQuoteSet by implementing the Calibration Parameters for the
 *  Floating Stream. Currently it exposes the PV and the Spread Quote Fields.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FloatingStreamQuoteSet extends org.drip.product.calib.ProductQuoteSet {

	/**
	 * FloatingStreamQuoteSet Constructor
	 * 
	 * @param aLSS Array of Latent State Specification
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public FloatingStreamQuoteSet (
		final org.drip.state.representation.LatentStateSpecification[] aLSS)
		throws java.lang.Exception
	{
		super (aLSS);
	}

	/**
	 * Set the PV
	 * 
	 * @param dblPV The PV
	 * 
	 * @return TRUE - PV successfully set
	 */

	public boolean setPV (
		final double dblPV)
	{
		return set ("PV", dblPV);
	}

	/**
	 * Indicate if the PV Field exists
	 * 
	 * @return TRUE - PV Field Exists
	 */

	public boolean containsPV()
	{
		return contains ("PV");
	}

	/**
	 * Retrieve the PV
	 * 
	 * @return The PV
	 * 
	 * @throws java.lang.Exception Thrown if the PV Field does not exist
	 */

	public double pv()
		throws java.lang.Exception
	{
		return get ("PV");
	}

	/**
	 * Set the Forward Rate
	 * 
	 * @param dblForwardRate The Forward Rate
	 * 
	 * @return TRUE - The Forward Rate successfully set
	 */

	public boolean setForwardRate (
		final double dblForwardRate)
	{
		return set ("ForwardRate", dblForwardRate);
	}

	/**
	 * Indicate if the Forward Rate Field exists
	 * 
	 * @return TRUE - Forward Rate Field Exists
	 */

	public boolean containsForwardRate()
	{
		return contains ("ForwardRate");
	}

	/**
	 * Retrieve the Forward Rate
	 * 
	 * @return The Forward Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Forward Rate Field does not exist
	 */

	public double forwardRate()
		throws java.lang.Exception
	{
		return get ("ForwardRate");
	}

	/**
	 * Set the Spread
	 * 
	 * @param dblSpread The Spread
	 * 
	 * @return TRUE - The Spread successfully set
	 */

	public boolean setSpread (
		final double dblSpread)
	{
		return set ("Spread", dblSpread);
	}

	/**
	 * Indicate if the Spread Field exists
	 * 
	 * @return TRUE - Spread Field Exists
	 */

	public boolean containsSpread()
	{
		return contains ("Spread");
	}

	/**
	 * Retrieve the Spread
	 * 
	 * @return The Spread
	 * 
	 * @throws java.lang.Exception Thrown if the Spread Field does not exist
	 */

	public double spread()
		throws java.lang.Exception
	{
		return get ("Spread");
	}
}
