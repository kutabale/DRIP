
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
 * ArithmeticLinearImpact contains the Arithmetic Linear Market Impact Inputs used in the Construction of the
 *  Impact Parameters for the Almgren and Chriss (2000) Optimal Trajectory Generation Scheme. The References
 *  are:
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

public class ArithmeticLinearImpact {
	private double _dblPermanentImpactFactor = java.lang.Double.NaN;
	private double _dblTemporaryImpactFactor = java.lang.Double.NaN;
	private org.drip.execution.parameters.AssetTransactionSettings _ats = null;

	/**
	 * Construct a Standard ArithmeticLinearImpact Instance
	 * 
	 * @param dblPrice The Asset Price
	 * @param dblDailyVolume The Daily Volume
	 * @param dblBidAskSpread The Bid-Ask Spread
	 *  
	 * @return The Standard ArithmeticLinearImpact Instance
	 */

	public static final ArithmeticLinearImpact AlmgrenChriss (
		final double dblPrice,
		final double dblDailyVolume,
		final double dblBidAskSpread)
	{
		try {
			return new ArithmeticLinearImpact (new org.drip.execution.parameters.AssetTransactionSettings
				(dblPrice, dblDailyVolume, dblBidAskSpread), 0.1, 0.01);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ArithmeticLinearImpact Constructor
	 * 
	 * @param ats The Asset Transaction Settings Instance
	 * @param dblPermanentImpactFactor The Fraction of the Daily Volume that triggers One Bid-Ask of
	 *  Permanent Impact Cost
	 * @param dblTemporaryImpactFactor The Fraction of the Daily Volume that triggers One Bid-Ask of
	 *  Temporary Impact Cost
	 *  
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ArithmeticLinearImpact (
		final org.drip.execution.parameters.AssetTransactionSettings ats,
		final double dblPermanentImpactFactor,
		final double dblTemporaryImpactFactor)
		throws java.lang.Exception
	{
		if (null == (_ats = ats) || !org.drip.quant.common.NumberUtil.IsValid (_dblPermanentImpactFactor =
			dblPermanentImpactFactor) || 0. >= _dblPermanentImpactFactor ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblTemporaryImpactFactor =
					dblTemporaryImpactFactor) || 0. >= _dblTemporaryImpactFactor)
			throw new java.lang.Exception ("ArithmeticLinearImpact Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the AssetTransactionSettings Instance
	 * 
	 * @return The AssetTransactionSettings Instance
	 */

	public org.drip.execution.parameters.AssetTransactionSettings ats()
	{
		return _ats;
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
	 * Generate the Permanent Impact Transaction Function
	 * 
	 * @return The Permanent Impact Transaction Function
	 */

	public org.drip.execution.impact.TransactionFunction permanentTransactionFunction()
	{
		org.drip.execution.parameters.AssetTransactionSettings ats = ats();

		try {
			return new org.drip.execution.impact.ParticipationRateLinear (0., ats.bidAskSpread() /
				(_dblPermanentImpactFactor * ats.backgroundVolume()));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Temporary Impact Transaction Function
	 * 
	 * @return The Temporary Impact Transaction Function
	 */

	public org.drip.execution.impact.TransactionFunction temporaryTransactionFunction()
	{
		org.drip.execution.parameters.AssetTransactionSettings ats = ats();

		double dblBidAskSpread = ats.bidAskSpread();

		try {
			return new org.drip.execution.impact.ParticipationRateLinear (0.5 * dblBidAskSpread,
				dblBidAskSpread / (_dblTemporaryImpactFactor * ats.backgroundVolume()));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
