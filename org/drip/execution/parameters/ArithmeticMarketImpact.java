
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
 * ArithmeticLinearMarketImpact contains the Arithmetic Linear Market Impact Inputs used in the Construction
 *  of the Impact Parameters for the Almgren and Chriss (2000) Optimal Trajectory Generation Scheme. The
 *  References are:
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

public class ArithmeticMarketImpact {
	private double _dblPrice = java.lang.Double.NaN;
	private double _dblDailyVolume = java.lang.Double.NaN;
	private double _dblBidAskSpread = java.lang.Double.NaN;
	private double _dblPermanentImpactFactor = java.lang.Double.NaN;
	private double _dblTemporaryImpactFactor = java.lang.Double.NaN;

	/**
	 * Construct a Standard ArithmeticLinearMarketImpact Instance using Almgren-Chriss Market Impact Factors
	 * 
	 * @param dblPrice The Asset Price
	 * @param dblDailyVolume The Daily Volume
	 * @param dblBidAskSpread The Bid-Ask Spread
	 *  
	 * @return The Standard ArithmeticLinearMarketImpact Instance
	 */

	public static final ArithmeticMarketImpact AlmgrenChriss (
		final double dblPrice,
		final double dblDailyVolume,
		final double dblBidAskSpread)
	{
		try {
			return new ArithmeticMarketImpact (dblPrice, dblDailyVolume, dblBidAskSpread, 0.1, 0.01);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ArithmeticLinearMarketImpact Constructor
	 * 
	 * @param dblPrice The Asset Price
	 * @param dblDailyVolume The Daily Volume
	 * @param dblBidAskSpread The Bid-Ask Spread
	 * @param dblPermanentImpactFactor The Fraction of the Daily Volume that triggers One Bid-Ask of
	 *  Permanent Impact Cost
	 * @param dblTemporaryImpactFactor The Fraction of the Daily Volume that triggers One Bid-Ask of
	 *  Temporary Impact Cost
	 *  
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ArithmeticMarketImpact (
		final double dblPrice,
		final double dblDailyVolume,
		final double dblBidAskSpread,
		final double dblPermanentImpactFactor,
		final double dblTemporaryImpactFactor)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblPrice = dblPrice) || 0. >= _dblPrice ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblDailyVolume = dblDailyVolume) || 0. >=
				_dblDailyVolume || !org.drip.quant.common.NumberUtil.IsValid (_dblBidAskSpread =
					dblBidAskSpread) || !org.drip.quant.common.NumberUtil.IsValid (_dblPermanentImpactFactor
						= dblPermanentImpactFactor) || 0. >= _dblPermanentImpactFactor ||
							!org.drip.quant.common.NumberUtil.IsValid (_dblTemporaryImpactFactor =
								dblTemporaryImpactFactor) || 0. >= _dblTemporaryImpactFactor)
			throw new java.lang.Exception ("ArithmeticMarketImpact Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Asset Price
	 *  
	 * @return The Asset Price
	 */

	public double price()
	{
		return _dblPrice;
	}

	/**
	 * Retrieve the Bid-Ask Spread
	 * 
	 * @return The Bid-Ask Spread
	 */

	public double bidAskSpread()
	{
		return _dblBidAskSpread;
	}

	/**
	 * Retrieve the Daily Volume
	 * 
	 * @return The Daily Volume
	 */

	public double dailyVolume()
	{
		return _dblDailyVolume;
	}

	/**
	 * Retrieve the Fraction of the Daily Volume that triggers One Bid-Ask of Permanent Impact Cost
	 * 
	 * @return The Fraction of the Daily Volume that triggers One Bid-Ask of Permanent Impact Cost
	 */

	public double permanentImpactFactor()
	{
		return _dblPermanentImpactFactor;
	}

	/**
	 * Retrieve the Fraction of the Daily Volume that triggers One Bid-Ask of Temporary Impact Cost
	 * 
	 * @return The Fraction of the Daily Volume that triggers One Bid-Ask of Temporary Impact Cost
	 */

	public double temporaryImpactFactor()
	{
		return _dblTemporaryImpactFactor;
	}

	/**
	 * Retrieve the Permanent Impact Market Parameter Slope
	 * 
	 * @return The Permanent Impact Market Parameter Slope
	 */

	public double permanentSlope()
	{
		return _dblBidAskSpread / (_dblPermanentImpactFactor * _dblDailyVolume);
	}

	/**
	 * Retrieve the Temporary Impact Market Parameter Offset
	 * 
	 * @return The Temporary Impact Market Parameter Offset
	 */

	public double temporaryOffset()
	{
		return 0.5 * _dblBidAskSpread;
	}

	/**
	 * Retrieve the Temporary Impact Market Parameter Slope
	 * 
	 * @return The Temporary Impact Market Parameter Slope
	 */

	public double temporarySlope()
	{
		return _dblBidAskSpread / (_dblTemporaryImpactFactor * _dblDailyVolume);
	}
}
