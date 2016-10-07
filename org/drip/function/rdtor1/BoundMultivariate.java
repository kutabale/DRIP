
package org.drip.function.rdtor1;

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
 * BoundMultivariate Interface implements R^d To R^1 Bounds.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface BoundMultivariate {

	/**
	 * Retrieve the Bound Type Indicator Flag
	 * 
	 * @return TRUE - Bound is Upper Type
	 */

	public abstract boolean isUpper();

	/**
	 * Retrieve the Bound Variate Index
	 * 
	 * @return The Bound Variate Index
	 */

	public abstract int boundVariateIndex();

	/**
	 * Retrieve the Bound Value
	 * 
	 * @return The Bound Value
	 */

	public abstract double boundValue();

	/**
	 * Indicate if the Specified Bound has been violated by the Variate
	 * 
	 * @param dblVariate The Variate
	 * 
	 * @return TRUE - The Bound has been violated
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public abstract boolean violated (
		final double dblVariate)
		throws java.lang.Exception;
}
