
package org.drip.dynamics.evolution;

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
 * LSQMCurveUpdate contains the Snapshot and the Increment of the Evolving Curve Latent State Quantification
 *  Metrics.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LSQMCurveUpdate {
	private int _iFinalDate = java.lang.Integer.MIN_VALUE;
	private int _iInitialDate = java.lang.Integer.MIN_VALUE;
	private org.drip.dynamics.evolution.LSQMCurveSnapshot _snapshot = null;
	private org.drip.dynamics.evolution.LSQMCurveIncrement _increment = null;

	/**
	 * LSQMCurveUpdate Constructor
	 * 
	 * @param iInitialDate The Initial Date
	 * @param iFinalDate The Final Date
	 * @param snapshot The LSQM Curve Snapshot
	 * @param increment The LSQM Curve Increment
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public LSQMCurveUpdate (
		final int iInitialDate,
		final int iFinalDate,
		final org.drip.dynamics.evolution.LSQMCurveSnapshot snapshot,
		final org.drip.dynamics.evolution.LSQMCurveIncrement increment)
		throws java.lang.Exception
	{
		if (null == (_snapshot = snapshot) || (_iFinalDate = iFinalDate) < (_iInitialDate = iInitialDate))
			throw new java.lang.Exception ("LSQMCurveUpdate ctr: Invalid Inputs");

		_increment = increment;
	}

	/**
	 * Retrieve the Initial Date
	 * 
	 * @return The Initial Date
	 */

	public int initialDate()
	{
		return _iInitialDate;
	}

	/**
	 * Retrieve the Final Date
	 * 
	 * @return The Final Date
	 */

	public int finalDate()
	{
		return _iFinalDate;
	}

	/**
	 * Retrieve the LSQM Curve Snapshot
	 * 
	 * @return The LSQM Curve Snapshot
	 */

	public org.drip.dynamics.evolution.LSQMCurveSnapshot snapshot()
	{
		return _snapshot;
	}

	/**
	 * Retrieve the LSQM Curve Increment
	 * 
	 * @return The LSQM Curve Increment
	 */

	public org.drip.dynamics.evolution.LSQMCurveIncrement increment()
	{
		return _increment;
	}
}
