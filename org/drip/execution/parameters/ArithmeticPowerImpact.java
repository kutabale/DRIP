
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
 * ArithmeticPowerImpact contains the Arithmetic Power Market Impact Inputs used in the Construction of the
 *  Impact Parameters for the Almgren and Chriss (2000) Optimal Trajectory Generation Scheme. The References
 *  are:
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
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ArithmeticPowerImpact extends org.drip.execution.parameters.ArithmeticLinearImpact {
	private double _dblTemporaryImpactExponent = java.lang.Double.NaN;
	private double _dblDailyVolumeExecutionFactor = java.lang.Double.NaN;

	/**
	 * ArithmeticPowerImpact Constructor
	 * 
	 * @param ats The Asset Transaction Settings Instance
	 * @param dblPermanentImpactFactor The Fraction of the Daily Volume that triggers One Bid-Ask of
	 *  Permanent Impact Cost
	 * @param dblTemporaryImpactFactor The Fraction of the Daily Volume that triggers One Bid-Ask of
	 *  Temporary Impact Cost
	 * @param dblDailyVolumeExecutionFactor The Daily Reference Execution Rate as a Proportion of the Daily
	 * 	Volume
	 * @param dblTemporaryImpactExponent The Temporary Impact Exponent
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ArithmeticPowerImpact (
		final org.drip.execution.parameters.AssetTransactionSettings ats,
		final double dblPermanentImpactFactor,
		final double dblTemporaryImpactFactor,
		final double dblDailyVolumeExecutionFactor,
		final double dblTemporaryImpactExponent)
		throws java.lang.Exception
	{
		super (ats, dblPermanentImpactFactor, dblTemporaryImpactFactor);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblDailyVolumeExecutionFactor =
			dblDailyVolumeExecutionFactor) || 0. >= _dblDailyVolumeExecutionFactor ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblTemporaryImpactExponent =
					dblTemporaryImpactExponent) || 0. >= _dblTemporaryImpactExponent)
			throw new java.lang.Exception ("ArithmeticPowerImpact Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Daily Reference Execution Rate as a Proportion of the Daily Volume
	 * 
	 * @return The Daily Reference Execution Rate as a Proportion of the Daily Volume
	 */

	public double dailyVolumeExecutionFactor()
	{
		return _dblDailyVolumeExecutionFactor;
	}

	/**
	 * Retrieve the Temporary Impact Constant
	 * 
	 * @return The Temporary Impact Constant
	 */

	public double temporaryImpactConstant()
	{
		org.drip.execution.parameters.AssetTransactionSettings ats = ats();

		return ats.price() * temporaryImpactFactor() / java.lang.Math.pow (ats.participationVolume() *
			_dblDailyVolumeExecutionFactor, _dblTemporaryImpactExponent);
	}

	/**
	 * Retrieve the Temporary Impact Power Exponent
	 * 
	 * @return The Temporary Impact Power Exponent
	 */

	public double temporaryImpactExponent()
	{
		return _dblTemporaryImpactExponent;
	}
}
