
package org.drip.xva.bilateral;

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
	private org.drip.measure.process.MarginalEvolver _meAssetPriceNumeraire = null;
	private org.drip.measure.process.MarginalEvolver _meBankFundingNumeraire = null;
	private org.drip.measure.process.MarginalEvolver _meCreditRiskFreeNumeraire = null;
	private org.drip.measure.process.MarginalEvolver _meCounterPartyFundingNumeraire = null;

	/**
	 * TwoWayRiskyUniverse Constructor
	 * 
	 * @param meAssetPriceNumeraire The Asset Price Numeraire
	 * @param meCreditRiskFreeNumeraire The Credit Risk Free Numeraire
	 * @param meBankFundingNumeraire The Credit Risky Bank Funding Numeraire
	 * @param meCounterPartyFundingNumeraire The Credit Risky Counter Party Funding Numeraire
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TwoWayRiskyUniverse (
		final org.drip.measure.process.MarginalEvolver meAssetPriceNumeraire,
		final org.drip.measure.process.MarginalEvolver meCreditRiskFreeNumeraire,
		final org.drip.measure.process.MarginalEvolver meBankFundingNumeraire,
		final org.drip.measure.process.MarginalEvolver meCounterPartyFundingNumeraire)
		throws java.lang.Exception
	{
		if (null == (_meAssetPriceNumeraire = meAssetPriceNumeraire) || null == (_meCreditRiskFreeNumeraire =
			meCreditRiskFreeNumeraire) || null == (_meBankFundingNumeraire = meBankFundingNumeraire) || null
				== (_meCounterPartyFundingNumeraire = meCounterPartyFundingNumeraire))
			throw new java.lang.Exception ("TwoWayRiskyUniverse Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Asset Price Numeraire
	 * 
	 * @return The Asset Price Numeraire
	 */

	public org.drip.measure.process.MarginalEvolver assetPriceNumeraire()
	{
		return _meAssetPriceNumeraire;
	}

	/**
	 * Retrieve the Credit Risk Free Numeraire
	 * 
	 * @return The Credit Risk Free Numeraire
	 */

	public org.drip.measure.process.MarginalEvolver creditRiskFreeNumeraire()
	{
		return _meCreditRiskFreeNumeraire;
	}

	/**
	 * Retrieve the Credit Risky Bank Funding Numeraire
	 * 
	 * @return The Credit Risky Bank Funding Numeraire
	 */

	public org.drip.measure.process.MarginalEvolver bankFundingNumeraire()
	{
		return _meBankFundingNumeraire;
	}

	/**
	 * Retrieve the Credit Risky Counter Party Funding Numeraire
	 * 
	 * @return The Credit Risky Counter Party Funding Numeraire
	 */

	public org.drip.measure.process.MarginalEvolver counterPartyFundingNumeraire()
	{
		return _meCounterPartyFundingNumeraire;
	}
}
