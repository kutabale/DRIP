
package org.drip.execution.principal;

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
 * HorizonInformationRatioDependence holds the Dependence Constants/Exponents for the Optimal Information
 * 	Ratio and the corresponding Horizon. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 16 (6) 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class HorizonInformationRatioDependence {
	private org.drip.execution.principal.OptimalMeasureDependence _omdHorizon = null;
	private org.drip.execution.principal.OptimalMeasureDependence _omdInformationRatio = null;

	/**
	 * HorizonInformationRatioDependence Constructor
	 * 
	 * @param omdHorizon The Horizon Optimal Measure Dependence Instance
	 * @param omdInformationRatio The Information Ratio Optimal Measure Dependence Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public HorizonInformationRatioDependence (
		final org.drip.execution.principal.OptimalMeasureDependence omdHorizon,
		final org.drip.execution.principal.OptimalMeasureDependence omdInformationRatio)
		throws java.lang.Exception
	{
		if (null == (_omdHorizon = omdHorizon) || null == (_omdInformationRatio = omdInformationRatio))
			throw new java.lang.Exception ("HorizonInformationRatioDependence Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Optimal Measure Dependence for the Time Horizon
	 *  
	 * @return Optimal Measure Dependence for Time Horizon
	 */

	public org.drip.execution.principal.OptimalMeasureDependence omdHorizon()
	{
		return _omdHorizon;
	}

	/**
	 * Retrieve the Optimal Measure Dependence for the Time Horizon
	 *  
	 * @return Optimal Measure Dependence for Time Horizon
	 */

	public org.drip.execution.principal.OptimalMeasureDependence omdInformationRatio()
	{
		return _omdInformationRatio;
	}
}
