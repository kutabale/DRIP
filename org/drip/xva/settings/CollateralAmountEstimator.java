
package org.drip.xva.settings;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * CollateralAmountEstimator estimates the Amount of Collateral that is to be Posted during a Single Run of a
 *  Collateral Group Valuation. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 *  
 *  - Li, B., and Y. Tang (2007): Quantitative Analysis, Derivatives Modeling, and Trading Strategies in the
 *  	Presence of Counter-party Credit Risk for the Fixed Income Market, World Scientific Publishing,
 *  	Singapore.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CollateralAmountEstimator {
	private double _dblCurrentBalance = java.lang.Double.NaN;
	private org.drip.xva.settings.CollateralGroup _cg = null;
	private org.drip.xva.settings.CounterPartyGroup _cpg = null;
	private org.drip.measure.continuousmarginal.BrokenDateBridge _bdb = null;

	/**
	 * CollateralAmountEstimator Constructor
	 * 
	 * @param cg The Collateral Group
	 * @param cpg The Counter Party Group
	 * @param bdb The Stochastic Value Broken Date Bridge Estimator
	 * @param dblCurrentBalance The Current Collateral Balance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CollateralAmountEstimator (
		final org.drip.xva.settings.CollateralGroup cg,
		final org.drip.xva.settings.CounterPartyGroup cpg,
		final org.drip.measure.continuousmarginal.BrokenDateBridge bdb,
		final double dblCurrentBalance)
		throws java.lang.Exception
	{
		if (null == (_cg = cg) || null == (_cpg = cpg) || null == (_bdb = bdb))
			throw new java.lang.Exception ("CollateralAmountEstimator Constructor => Invalid Inputs");

		_dblCurrentBalance = dblCurrentBalance;
	}

	/**
	 * Retrieve the Collateral Group
	 * 
	 * @return The Collateral Group
	 */

	public org.drip.xva.settings.CollateralGroup collateralGroup()
	{
		return _cg;
	}

	/**
	 * Retrieve the Counter Party Group
	 * 
	 * @return The Counter Party Group
	 */

	public org.drip.xva.settings.CounterPartyGroup counterPartyGroup()
	{
		return _cpg;
	}

	/**
	 * Retrieve the Stochastic Value Broken Date Bridge Estimator
	 * 
	 * @return The Stochastic Value Broken Date Bridge Estimator
	 */

	public org.drip.measure.continuousmarginal.BrokenDateBridge brokenDateBridge()
	{
		return _bdb;
	}

	/**
	 * Retrieve the Current Collateral Balance
	 * 
	 * @return The Current Collateral Balance
	 */

	public double currentCollateralBalance()
	{
		return _dblCurrentBalance;
	}

	/**
	 * Calculate the Margin Value at the Bank Default Window
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Margin Value at the Bank Default Window
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double bankWindowMarginValue (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		if (null == dtValue)
			throw new java.lang.Exception
				("CollateralAmountEstimator::bankWindowMarginValue => Invalid Inputs");

		org.drip.analytics.date.JulianDate dtMargin = dtValue.subtractDays (_cpg.bankDefaultWindow());

		if (null == dtMargin)
			throw new java.lang.Exception
				("CollateralAmountEstimator::bankWindowMarginValue => Invalid Inputs");

		return _bdb.interpolate (dtMargin.julian());
	}

	/**
	 * Calculate the Bank Collateral Threshold
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Bank Collateral Threshold
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double bankThreshold (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		org.drip.function.definition.R1ToR1 r1ToR1BankThreshold = _cg.bankThreshold();

		return null == r1ToR1BankThreshold ? 0. : r1ToR1BankThreshold.evaluate (dtValue.julian());
	}

	/**
	 * Calculate the Collateral Amount Required to be Posted by the Bank
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Collateral Amount Required to be Posted by the Bank
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double bankPostingRequirement (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		double dblBankPostingRequirement = bankWindowMarginValue (dtValue) - bankThreshold (dtValue);

		return 0. < dblBankPostingRequirement ? 0. : dblBankPostingRequirement;
	}

	/**
	 * Calculate the Margin Value at the Counter Party Default Window
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Margin Value at the Counter Party Default Window
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double counterPartyWindowMarginValue (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		if (null == dtValue)
			throw new java.lang.Exception
				("CollateralAmountEstimator::counterPartyWindowMarginValue => Invalid Inputs");

		org.drip.analytics.date.JulianDate dtMargin = dtValue.subtractDays
			(_cpg.counterPartyDefaultWindow());

		if (null == dtMargin)
			throw new java.lang.Exception
				("CollateralAmountEstimator::counterPartyWindowMarginValue => Invalid Inputs");

		return _bdb.interpolate (dtMargin.julian());
	}

	/**
	 * Calculate the Counter Party Collateral Threshold
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Counter Party Collateral Threshold
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double counterPartyThreshold (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		org.drip.function.definition.R1ToR1 r1ToR1CounterPartyThreshold = _cg.counterPartyThreshold();

		return null == r1ToR1CounterPartyThreshold ? 0. : r1ToR1CounterPartyThreshold.evaluate
			(dtValue.julian());
	}

	/**
	 * Calculate the Collateral Amount Required to be Posted by the Counter Party
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Collateral Amount Required to be Posted by the Counter Party
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double counterPartyPostingRequirement (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		double dblCounterPartyPostingRequirement = counterPartyWindowMarginValue (dtValue) -
			counterPartyThreshold (dtValue);

		return 0. > dblCounterPartyPostingRequirement ? 0. : dblCounterPartyPostingRequirement;
	}

	/**
	 * Calculate the Gross Collateral Amount Required to be Posted
	 * 
	 * @param dtValue The Valuation Date
	 * 
	 * @return The Gross Collateral Amount Required to be Posted
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double postingRequirement (
		final org.drip.analytics.date.JulianDate dtValue)
		throws java.lang.Exception
	{
		return org.drip.quant.common.NumberUtil.IsValid (_dblCurrentBalance) ? _dblCurrentBalance :
			bankPostingRequirement (dtValue) + counterPartyPostingRequirement (dtValue);
	}
}
