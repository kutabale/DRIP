
package org.drip.execution.parameters;

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
 * ArithmeticPriceDynamicsSettings contains the Arithmetic Price Evolution Dynamics Parameters used in the
 *  Almgren and Chriss (2000) Optimal Trajectory Generation Scheme. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 *
 * 	- Chan, L. K. C., and J. Lakonishak (1995): The Behavior of Stock Prices around Institutional Trades,
 * 		Journal of Finance, 50, 1147-1174.
 *
 * 	- Keim, D. B., and A. Madhavan (1997): Transaction Costs and Investment Style: An Inter-exchange
 * 		Analysis of Institutional Equity Trades, Journal of Financial Economics, 46, 265-292.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ArithmeticPriceDynamicsSettings {
	private double _dblDrift = java.lang.Double.NaN;
	private double _dblVolatility = java.lang.Double.NaN;
	private double _dblSerialCorrelation = java.lang.Double.NaN;

	/**
	 * Construct the Asset Dynamics Settings from the Annual Returns Parameters
	 * 
	 * @param dblAnnualReturnsExpectation The Asset Annual Expected Returns
	 * @param dblAnnualReturnsVolatility The Asset Annual Returns Volatility
	 * @param dblSerialCorrelation The Asset Serial Correlation
	 * @param dblPrice The Asset Price
	 * 
	 * @return The Asset Dynamics Settings from the Annual Returns Parameters
	 */

	public static final ArithmeticPriceDynamicsSettings FromAnnualReturnsSettings (
		final double dblAnnualReturnsExpectation,
		final double dblAnnualReturnsVolatility,
		final double dblSerialCorrelation,
		final double dblPrice)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblAnnualReturnsExpectation) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblAnnualReturnsVolatility) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblPrice))
			return null;

		try {
			return new ArithmeticPriceDynamicsSettings (dblPrice * dblAnnualReturnsExpectation / 250.,
				dblPrice * dblAnnualReturnsVolatility / java.lang.Math.sqrt (250.), dblSerialCorrelation);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ArithmeticPriceDynamicsSettings Constructor
	 * 
	 * @param dblDrift The Asset Daily Arithmetic Drift
	 * @param dblVolatility The Asset Daily Normal Volatility
	 * @param dblSerialCorrelation The Asset Serial Correlation
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ArithmeticPriceDynamicsSettings (
		final double dblDrift,
		final double dblVolatility,
		final double dblSerialCorrelation)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDrift = dblDrift) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblVolatility = dblVolatility) || 0. >=
				_dblVolatility || !org.drip.quant.common.NumberUtil.IsValid (_dblSerialCorrelation =
					dblSerialCorrelation) || 1. < _dblSerialCorrelation || -1. > _dblSerialCorrelation)
			throw new java.lang.Exception ("ArithmeticPriceDynamicsSettings Constructor => Invalid Inputs!");
	}

	/**
	 * Retrieve the Asset Annual Logarithmic Drift
	 *  
	 * @return The Asset Annual Logarithmic Drift
	 */

	public double drift()
	{
		return _dblDrift;
	}

	/**
	 * Retrieve the Asset Annual Volatility
	 *  
	 * @return The Asset Annual Volatility
	 */

	public double volatility()
	{
		return _dblVolatility;
	}

	/**
	 * Retrieve the Asset Serial Correlation
	 *  
	 * @return The Asset Serial Correlation
	 */

	public double serialCorrelation()
	{
		return _dblSerialCorrelation;
	}
}
