
package org.drip.xva.definition;

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
 * TwoWayRiskyUniverse describes the Economy with Four Traded Assets - the Default-free Zero Coupon Bond, the
 * 	Default-able Zero Coupon Bank Bond, the Default-able Zero Coupon Counter-party, and an Asset the follows
 * 	Brownian Motion. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Cesari, G., J. Aquilina, N. Charpillon, X. Filipovic, G. Lee, and L. Manda (2009): Modeling, Pricing,
 *  	and Hedging Counter-party Credit Exposure - A Technical Guide, Springer Finance, New York.
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

public class TwoWayRiskyUniverse {
	private org.drip.xva.definition.TradeableAsset _taCreditRiskFree = null;
	private org.drip.xva.definition.TradeableAsset _taReferenceUnderlier = null;
	private org.drip.xva.definition.TradeableAsset _taZeroCouponBankBond = null;
	private org.drip.xva.definition.TradeableAsset _taZeroCouponCounterPartyBond = null;

	/**
	 * TwoWayRiskyUniverse Constructor
	 * 
	 * @param taReferenceUnderlier The Reference Underlier Trade-able Asset
	 * @param taCreditRiskFree The Credit Risk Free Trade-able Asset
	 * @param taZeroCouponBankBond The Zero Coupon Credit Risky Bank Bond
	 * @param taZeroCouponCounterPartyBond The Zero Coupon Credit Risky Counter Party Bond
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TwoWayRiskyUniverse (
		final org.drip.xva.definition.TradeableAsset taReferenceUnderlier,
		final org.drip.xva.definition.TradeableAsset taCreditRiskFree,
		final org.drip.xva.definition.TradeableAsset taZeroCouponBankBond,
		final org.drip.xva.definition.TradeableAsset taZeroCouponCounterPartyBond)
		throws java.lang.Exception
	{
		if (null == (_taReferenceUnderlier = taReferenceUnderlier) || null == (_taCreditRiskFree =
			taCreditRiskFree) || null == (_taZeroCouponBankBond = taZeroCouponBankBond) || null ==
				(_taZeroCouponCounterPartyBond = taZeroCouponCounterPartyBond))
			throw new java.lang.Exception ("TwoWayRiskyUniverse Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Reference Underlier
	 * 
	 * @return The Reference Underlier
	 */

	public org.drip.xva.definition.TradeableAsset referenceUnderlier()
	{
		return _taReferenceUnderlier;
	}

	/**
	 * Retrieve the Credit Risk Free Trade-able Asset
	 * 
	 * @return The Credit Risk Free Trade-able Asset
	 */

	public org.drip.xva.definition.TradeableAsset creditRiskFreeNumeraire()
	{
		return _taCreditRiskFree;
	}

	/**
	 * Retrieve the Zero Coupon Credit Risky Bank Bond
	 * 
	 * @return The Zero Coupon Credit Risky Bank Bond
	 */

	public org.drip.xva.definition.TradeableAsset zeroCouponBankBond()
	{
		return _taZeroCouponBankBond;
	}

	/**
	 * Retrieve the Zero Coupon Credit Risky Counter Party Bond
	 * 
	 * @return The Zero Coupon Credit Risky Counter Party Bond
	 */

	public org.drip.xva.definition.TradeableAsset zeroCouponCounterPartyBond()
	{
		return _taZeroCouponCounterPartyBond;
	}
}
