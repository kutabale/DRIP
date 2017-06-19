
package org.drip.xva.strategy;

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
 * BurgardKjaerExposure holds the Vertex Realizations off the specific Exposures in each Simulation Step as
 * 	laid out in the Burgard Kjaer (2013) Scheme. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2011): PDE Representations of Derivatives with Bilateral Counter Party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2011): In the Balance, Risk, 22 (11) 72-75.
 *  
 *  - Burgard, C., and M. Kjaer (2013): Funding Costs and Funding Strategies, Risk, 24 (12) 82-87.
 *  
 *  - Hull, J., and A. White (2012): The FVA Debate, Risk, 23 (7) 83-85.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class BurgardKjaerExposure {
	private double _dblDebt = java.lang.Double.NaN;
	private double _dblCredit = java.lang.Double.NaN;
	private double _dblHedgeError = java.lang.Double.NaN;
	private double _dblCollateralAmount = java.lang.Double.NaN;

	/**
	 * Generate a Standard Instance of BurgardKjaerExposure
	 * 
	 * @param dblUncollateralizedExposure The Uncollateralized Exposure
	 * @param cog The Close-out General Instance
	 * @param dblHedgeError The Hedge Error
	 * @param dblCollateralAmount The Collateral Amount
	 * 
	 * @return The Standard Instance of BurgardKjaerExposure
	 */

	public static final BurgardKjaerExposure Standard (
		final double dblUncollateralizedExposure,
		final org.drip.xva.definition.CloseOutGeneral cog,
		final double dblHedgeError,
		final double dblCollateralAmount)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblUncollateralizedExposure) || null == cog)
			return null;

		try {
			return new BurgardKjaerExposure (
				dblUncollateralizedExposure - cog.counterPartyDefault (dblUncollateralizedExposure,
					dblCollateralAmount),
				dblUncollateralizedExposure - cog.bankDefault (dblUncollateralizedExposure,
					dblCollateralAmount),
				dblHedgeError,
				dblCollateralAmount
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * BurgardKjaerExposure Constructor
	 * 
	 * @param dblCredit The Counter Party Credit Exposure
	 * @param dblDebt The Bank Debt Exposure
	 * @param dblHedgeError The Hedge Error
	 * @param dblCollateralAmount The Collateral Amount
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BurgardKjaerExposure (
		final double dblCredit,
		final double dblDebt,
		final double dblHedgeError,
		final double dblCollateralAmount)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblCredit = dblCredit) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblDebt = dblDebt) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblHedgeError = dblHedgeError) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralAmount = dblCollateralAmount))
			throw new java.lang.Exception ("BurgardKjaerExposure Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Counter Party Credit Exposure
	 * 
	 * @return The Counter Party Credit Exposure
	 */

	public double credit()
	{
		return _dblCredit;
	}

	/**
	 * Retrieve the Bank Debt Exposure
	 * 
	 * @return The Bank Debt Exposure
	 */

	public double debt()
	{
		return _dblDebt;
	}

	/**
	 * Retrieve the Hedge Error
	 * 
	 * @return The Hedge Error
	 */

	public double hedgeError()
	{
		return _dblHedgeError;
	}

	/**
	 * Retrieve the Collateral Amount
	 * 
	 * @return The Collateral Amount
	 */

	public double collateralAmount()
	{
		return _dblCollateralAmount;
	}
}
