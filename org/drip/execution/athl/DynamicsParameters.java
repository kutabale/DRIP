
package org.drip.execution.athl;

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
 * DynamicsParameters generates the Variants of the Market Dynamics Parameters constructed using the
 *  Methodologies presented in Almgren, Thum, Hauptmann, and Li (2005), using the Parameterization of Almgren
 *  (2003). The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DynamicsParameters {
	private org.drip.execution.parameters.AssetFlowSettings _afp = null;

	/**
	 * DynamicsParameters Constructor
	 * 
	 * @param afp The Asset Flow Parameters Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public DynamicsParameters (
		final org.drip.execution.parameters.AssetFlowSettings afp)
		throws java.lang.Exception
	{
		if (null == (_afp = afp))
			throw new java.lang.Exception ("DynamicsParameters Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Asset Flow Parameters Instance
	 * 
	 * @return The Asset Flow Parameters Instance
	 */

	public org.drip.execution.parameters.AssetFlowSettings assetFlowParameters()
	{
		return _afp;
	}

	/**
	 * Generate an Instance of the Almgren 2003 Dynamics Parameters
	 * 
	 * @return Instance of the Almgren 2003 Dynamics Parameters
	 */

	public org.drip.execution.dynamics.Almgren2003Parameters almgren2003()
	{
		try {
			return new org.drip.execution.dynamics.Almgren2003Parameters (0., _afp.dailyVolatility(), 0., new
				org.drip.execution.athl.PermanentImpactNoArbitrage (_afp), new
					org.drip.execution.athl.TemporaryImpact (_afp));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
