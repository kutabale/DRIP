
package org.drip.xva.universe;

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
 * TradeablesContainer describes the Economy with the following Traded Assets - the Default-free Zero Coupon
 *  Bond, the Default-able Zero Coupon Bank Bond, the Array of Default-able Zero Coupon Counter-party Bonds,
 *  and an Asset that follows Brownian Motion. The References are:
 *  
 *  - Albanese, C., and L. Andersen (2014): Accounting for OTC Derivatives: Funding Adjustments and the
 *  	Re-Hypothecation Option, eSSRN, https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2482955.
 *  
 *  - Burgard, C., and M. Kjaer (2013): Funding Costs, Funding Strategies, Risk, 23 (12) 82-87.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Burgard, C., and M. Kjaer (2017): Derivatives Funding, Netting, and Accounting, eSSRN,
 *  	https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2534011.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TradeablesContainer {
	private org.drip.xva.universe.Tradeable _tAsset = null;
	private org.drip.xva.universe.Tradeable _tZeroCouponBankBond = null;
	private org.drip.xva.universe.Tradeable _tZeroCouponCollateralBond = null;
	private org.drip.xva.universe.Tradeable[] _aTZeroCouponCounterPartyBond = null;
	private org.drip.xva.universe.Tradeable _tZeroCouponZeroRecoveryBankBond = null;

	/**
	 * TradeablesContainer Constructor
	 * 
	 * @param tAsset Trade-able Asset
	 * @param tZeroCouponCollateralBond Zero Coupon Collateral Bond
	 * @param tZeroCouponBankBond Zero Coupon Credit Risky Bank Bond
	 * @param aTZeroCouponCounterPartyBond Array of Zero Coupon Credit Risky Counter Party Bonds
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TradeablesContainer (
		final org.drip.xva.universe.Tradeable tAsset,
		final org.drip.xva.universe.Tradeable tZeroCouponCollateralBond,
		final org.drip.xva.universe.Tradeable tZeroCouponBankBond,
		final org.drip.xva.universe.Tradeable[] aTZeroCouponCounterPartyBond)
		throws java.lang.Exception
	{
		if (null == (_tAsset = tAsset) || null == (_tZeroCouponCollateralBond = tZeroCouponCollateralBond) ||
			null == (_tZeroCouponBankBond = tZeroCouponBankBond) || null == (_aTZeroCouponCounterPartyBond =
				aTZeroCouponCounterPartyBond))
			throw new java.lang.Exception ("TradeablesContainer Constructor => Invalid Inputs");

		int iNumCounterParty = _aTZeroCouponCounterPartyBond.length;

		if (0 >= iNumCounterParty)
			throw new java.lang.Exception ("TradeablesContainer Constructor => Invalid Inputs");

		for (int i = 0; i < iNumCounterParty; ++i) {
			if (null == _aTZeroCouponCounterPartyBond[i])
				throw new java.lang.Exception ("TradeablesContainer Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Asset
	 * 
	 * @return The Asset
	 */

	public org.drip.xva.universe.Tradeable asset()
	{
		return _tAsset;
	}

	/**
	 * Retrieve the Zero Coupon Collateral Bond
	 * 
	 * @return The Zero Coupon Collateral Bond
	 */

	public org.drip.xva.universe.Tradeable zeroCouponCollateralBond()
	{
		return _tZeroCouponCollateralBond;
	}

	/**
	 * Retrieve the Zero Coupon Credit Risky Bank Bond
	 * 
	 * @return The Zero Coupon Credit Risky Bank Bond
	 */

	public org.drip.xva.universe.Tradeable zeroCouponBankBond()
	{
		return _tZeroCouponBankBond;
	}

	/**
	 * Retrieve the Zero Coupon Zero Recovery Credit Risky Bank Bond
	 * 
	 * @return The Zero Coupon Zero Recovery Credit Risky Bank Bond
	 */

	public org.drip.xva.universe.Tradeable zeroCouponZeroRecoveryBankBond()
	{
		return _tZeroCouponZeroRecoveryBankBond;
	}

	/**
	 * Retrieve the Array of Zero Coupon Credit Risky Counter Party Bonds
	 * 
	 * @return The Array of Zero Coupon Credit Risky Counter Party Bonds
	 */

	public org.drip.xva.universe.Tradeable[] zeroCouponCounterPartyBond()
	{
		return _aTZeroCouponCounterPartyBond;
	}

	/**
	 * Retrieve the Number of Counter Parties
	 * 
	 * @return The Number of Counter Parties
	 */

	public int numCounterParty()
	{
		return _aTZeroCouponCounterPartyBond.length;
	}
}
