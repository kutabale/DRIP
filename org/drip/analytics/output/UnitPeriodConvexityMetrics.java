
package org.drip.analytics.output;

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
 * UnitPeriodMetrics holds the results of a unit composable period convexity metrics estimate output.
 *
 * @author Lakshmi Krishnamurthy
 */

public class UnitPeriodConvexityMetrics {
	private int _iEndDate = java.lang.Integer.MIN_VALUE;
	private int _iStartDate = java.lang.Integer.MIN_VALUE;
	private org.drip.analytics.output.ConvexityAdjustment _convAdj = null;

	/**
	 * UnitPeriodConvexityMetrics constructor
	 * 
	 * @param iStartDate Metric Period Start Date
	 * @param iEndDate Metric Period End Date
	 * @param convAdj Coupon Period Convexity Adjustment
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public UnitPeriodConvexityMetrics (
		final int iStartDate,
		final int iEndDate,
		final org.drip.analytics.output.ConvexityAdjustment convAdj)
		throws java.lang.Exception
	{
		if ((_iEndDate = iEndDate) < (_iStartDate = iStartDate) || null == (_convAdj = convAdj))
			throw new java.lang.Exception ("UnitPeriodConvexityMetrics ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Start Date
	 * 
	 * @return The Start Date
	 */

	public int startDate()
	{
		return _iStartDate;
	}

	/**
	 * Retrieve the End Date
	 * 
	 * @return The End Date
	 */

	public int endDate()
	{
		return _iEndDate;
	}

	/**
	 * Retrieve the Convexity Adjustment
	 * 
	 * @return The Convexity Adjustment
	 */

	public org.drip.analytics.output.ConvexityAdjustment convAdj()
	{
		return _convAdj;
	}
}
