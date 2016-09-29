
package org.drip.state.govvie;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * YieldEstimator is the Interface that exposes the Computation of the Yield of a specified Issue.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface YieldEstimator {

	/**
	 * Calculate the Yield to the given Date
	 * 
	 * @param iDate Date
	 * 
	 * @return The Yield
	 * 
	 * @throws java.lang.Exception Thrown if the Yield cannot be calculated
	 */

	public abstract double yield (
		final int iDate)
		throws java.lang.Exception;

	/**
	 * Calculate the Yield to the given Date
	 * 
	 * @param dt Date
	 * 
	 * @return The Yield
	 * 
	 * @throws java.lang.Exception Thrown if the Yield cannot be calculated
	 */

	public abstract double yield (
		final org.drip.analytics.date.JulianDate dt)
		throws java.lang.Exception;

	/**
	 * Calculate the Yield to the Tenor implied by the given Date
	 * 
	 * @param strTenor The Tenor
	 * 
	 * @return The Yield
	 * 
	 * @throws java.lang.Exception Thrown if the Yield cannot be calculated
	 */

	public abstract double yield (
		final java.lang.String strTenor)
		throws java.lang.Exception;

	/**
	 * Calculate the Discount Factor to the given Date Using the specified DCF
	 * 
	 * @param iDate Date
	 * @param dblDCF The Day Count Fraction
	 * 
	 * @return Discount Factor
	 * 
	 * @throws java.lang.Exception Thrown if the Discount Factor cannot be calculated
	 */

	public abstract double yieldDF (
		final int iDate,
		final double dblDCF)
		throws java.lang.Exception;
}
