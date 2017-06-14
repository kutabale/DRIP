
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
 * MarketVertex holds the Market Realizations at a Market Trajectory Vertex needed for computing the
 *  Valuation Adjustment. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2013): Funding Strategies, Funding Costs, Risk, 24 (12) 82-87.
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class MarketVertex {
	public double _dblAssetNumeraire = java.lang.Double.NaN;
	private org.drip.analytics.date.JulianDate _dtAnchor = null;
	private double _dblOvernightIndexRate = java.lang.Double.NaN;
	private org.drip.xva.universe.EntityMarketVertex _emvBank = null;
	private double _dblCollateralSchemeSpread = java.lang.Double.NaN;
	private double _dblOvernightIndexNumeraire = java.lang.Double.NaN;
	private double _dblCollateralSchemeNumeraire = java.lang.Double.NaN;
	private org.drip.xva.universe.EntityMarketVertex _emvCounterParty = null;

	/**
	 * Construct a Standard Instance of the MarketVertex
	 * 
	 * @param dtAnchor The Vertex Date Anchor
	 * @param dblOvernightIndexRate The Realized Overnight Index Rate
	 * @param dblBankSurvival The Realized Bank Survival Rate
	 * @param dblBankSeniorRecoveryRate The Realized Bank Senior Recovery Rate
	 * @param dblBankSeniorFundingSpread The Realized Bank Senior Funding Spread
	 * @param dblCounterPartySurvival The Realized Counter Party Survival Probability
	 * @param dblCounterPartyRecoveryRate The Realized Counter Party Recovery Rate
	 * 
	 * @return The Standard Instance of MarketVertex
	 */

	public static final MarketVertex Standard (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblOvernightIndexRate,
		final double dblBankSurvival,
		final double dblBankSeniorRecoveryRate,
		final double dblBankSeniorFundingSpread,
		final double dblCounterPartySurvival,
		final double dblCounterPartyRecoveryRate)
	{
		try {
			return new MarketVertex (
				dtAnchor,
				java.lang.Double.NaN,
				dblOvernightIndexRate,
				1.,
				0.,
				1.,
				new org.drip.xva.universe.EntityMarketVertex (
					dblBankSurvival,
					dblBankSeniorFundingSpread / (1. - dblBankSeniorRecoveryRate),
					dblBankSeniorRecoveryRate,
					dblBankSeniorFundingSpread,
					1.,
					0.,
					dblBankSeniorFundingSpread / (1. - dblBankSeniorRecoveryRate),
					1.
				),
				new org.drip.xva.universe.EntityMarketVertex (
					dblCounterPartySurvival,
					0.,
					dblCounterPartyRecoveryRate,
					0.,
					1.,
					java.lang.Double.NaN,
					java.lang.Double.NaN,
					java.lang.Double.NaN
				)
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * MarketVertex Constructor
	 * 
	 * @param dtAnchor The Vertex Date Anchor
	 * @param dblAssetNumeraire The Asset Numeraire Realization
	 * @param dblOvernightIndexRate The Realized Overnight Index Rate
	 * @param dblOvernightIndexNumeraire The Realized Overnight Index Numeraire
	 * @param dblCollateralSchemeSpread The Realized Collateral Scheme Spread
	 * @param dblCollateralSchemeNumeraire The Realized Collateral Scheme Numeraire
	 * @param emvBank Bank Entity Market Vertex Instance
	 * @param emvCounterParty Counter Party Market Vertex Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MarketVertex (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblAssetNumeraire,
		final double dblOvernightIndexRate,
		final double dblOvernightIndexNumeraire,
		final double dblCollateralSchemeSpread,
		final double dblCollateralSchemeNumeraire,
		final org.drip.xva.universe.EntityMarketVertex emvBank,
		final org.drip.xva.universe.EntityMarketVertex emvCounterParty)
		throws java.lang.Exception
	{
		if (null == (_dtAnchor = dtAnchor) || !org.drip.quant.common.NumberUtil.IsValid
			(_dblOvernightIndexRate = dblOvernightIndexRate) || !org.drip.quant.common.NumberUtil.IsValid
				(_dblOvernightIndexNumeraire = dblOvernightIndexNumeraire) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralSchemeSpread =
						dblCollateralSchemeSpread) || !org.drip.quant.common.NumberUtil.IsValid
							(_dblCollateralSchemeNumeraire = dblCollateralSchemeNumeraire) || null ==
								(_emvBank = emvBank) || null == (_emvCounterParty = emvCounterParty))
			throw new java.lang.Exception ("MarketVertex Constructor => Invalid Inputs");

		_dblAssetNumeraire = dblAssetNumeraire;
	}

	/**
	 * Retrieve the Date Anchor
	 * 
	 * @return The Date Anchor
	 */

	public org.drip.analytics.date.JulianDate anchor()
	{
		return _dtAnchor;
	}

	/**
	 * Retrieve the Asset Numeraire
	 * 
	 * @return The Asset Numeraire
	 */

	public double assetNumeraire()
	{
		return _dblAssetNumeraire;
	}

	/**
	 * Retrieve the Realized Overnight Index Rate
	 * 
	 * @return The Realized Overnight Index Rate
	 */

	public double overnightIndexRate()
	{
		return _dblOvernightIndexRate;
	}

	/**
	 * Retrieve the Realized Overnight Index Numeraire
	 * 
	 * @return The Realized Overnight Index Numeraire
	 */

	public double overnightIndexNumeraire()
	{
		return _dblOvernightIndexNumeraire;
	}

	/**
	 * Retrieve the Realized Spread over the Overnight Policy Rate corresponding to the Collateral Scheme
	 * 
	 * @return The Realized Spread over the Overnight Policy Rate corresponding to the Collateral Scheme
	 */

	public double collateralSchemeSpread()
	{
		return _dblCollateralSchemeSpread;
	}

	/**
	 * Retrieve the Realized Collateral Scheme Numeraire
	 * 
	 * @return The Realized Collateral Scheme Numeraire
	 */

	public double collateralSchemeNumeraire()
	{
		return _dblCollateralSchemeNumeraire;
	}

	/**
	 * Retrieve the Realized Collateral Scheme Rate
	 * 
	 * @return The Realized Collateral Scheme Rate
	 */

	public double collateralSchemeRate()
	{
		return _dblOvernightIndexRate + _dblCollateralSchemeSpread;
	}

	/**
	 * Retrieve the Realized Bank Senior Market Vertex
	 * 
	 * @return The Realized Bank Senior Market Vertex
	 */

	public org.drip.xva.universe.EntityMarketVertex bank()
	{
		return _emvBank;
	}

	/**
	 * Retrieve the Realized Counter Party Market Vertex
	 * 
	 * @return The Realized Counter Party Market Vertex
	 */

	public org.drip.xva.universe.EntityMarketVertex counterParty()
	{
		return _emvCounterParty;
	}
}
