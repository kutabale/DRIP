
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
 * MarketVertex holds the Market Realizations at a Trajectory Vertex. The References are:
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
	private org.drip.analytics.date.JulianDate _dtAnchor = null;
	private double _dblCollateralSchemeSpread = java.lang.Double.NaN;
	private double _dblOvernightPolicyIndexRate = java.lang.Double.NaN;
	private org.drip.xva.universe.EntityMarketVertex[] _aEMVCounterParty = null;
	private org.drip.xva.universe.EntityMarketVertex _emvBankSeniorFunding = null;
	private org.drip.xva.universe.EntityMarketVertex _emvBankSubordinateFunding = null;

	/**
	 * Construct a Standard Instance of the MarketVertex
	 * 
	 * @param dtAnchor The Vertex Date Anchor
	 * @param dblOvernightPolicyIndexRate The Realized Overnight Policy Index Rate
	 * @param dblBankSurvival The Realized Bank Survival Rate
	 * @param dblBankSeniorRecovery The Realized Bank Senior Recovery Rate
	 * @param dblBankSeniorFundingSpread The Realized Bank Senior Funding Spread
	 * @param dblCounterPartySurvival The Realized Counter Party Survival Probability
	 * @param dblCounterPartyRecovery The Realized Counter Party Recovery Rate
	 * 
	 * @return The Standard Instance of MarketVertex
	 */

	public static final MarketVertex Standard (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblOvernightPolicyIndexRate,
		final double dblBankSurvival,
		final double dblBankSeniorRecovery,
		final double dblBankSeniorFundingSpread,
		final double dblCounterPartySurvival,
		final double dblCounterPartyRecovery)
	{
		try {
			return new MarketVertex (
				dtAnchor,
				dblOvernightPolicyIndexRate,
				0.,
				new org.drip.xva.universe.EntityMarketVertex (
					dblBankSurvival,
					dblBankSeniorRecovery,
					dblBankSeniorFundingSpread / (1. - dblBankSeniorRecovery),
					dblBankSeniorFundingSpread
				),
				null,
				new org.drip.xva.universe.EntityMarketVertex[] {
					new org.drip.xva.universe.EntityMarketVertex (
						dblCounterPartySurvival,
						dblCounterPartyRecovery,
						0.,
						0.
					)
				}
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
	 * @param dblOvernightPolicyIndexRate The Realized Overnight Policy Index Rate
	 * @param dblCollateralSchemeSpread The Realized Collateral Scheme Spread
	 * @param emvBankSeniorFunding Bank Senior Funding Entity Market Vertex Instance
	 * @param emvBankSubordinateFunding Bank Subordinate Funding Entity Market Vertex Instance
	 * @param aEMVCounterParty Array of Counter Party Market Vertex Instances
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MarketVertex (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblOvernightPolicyIndexRate,
		final double dblCollateralSchemeSpread,
		final org.drip.xva.universe.EntityMarketVertex emvBankSeniorFunding,
		final org.drip.xva.universe.EntityMarketVertex emvBankSubordinateFunding,
		final org.drip.xva.universe.EntityMarketVertex[] aEMVCounterParty)
		throws java.lang.Exception
	{
		if (null == (_dtAnchor = dtAnchor) || !org.drip.quant.common.NumberUtil.IsValid
			(_dblOvernightPolicyIndexRate = dblOvernightPolicyIndexRate) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralSchemeSpread =
					dblCollateralSchemeSpread) || null == (_emvBankSeniorFunding = emvBankSeniorFunding) ||
						null == (_aEMVCounterParty = aEMVCounterParty))
			throw new java.lang.Exception ("MarketVertex Constructor => Invalid Inputs");

		int iNumCounterParty = _aEMVCounterParty.length;
		_emvBankSubordinateFunding = emvBankSubordinateFunding;

		if (0 == iNumCounterParty)
			throw new java.lang.Exception ("MarketVertex Constructor => Invalid Inputs");

		for (int i = 0; i < iNumCounterParty; ++i) {
			if (null == _aEMVCounterParty[i])
				throw new java.lang.Exception ("MarketVertex Constructor => Invalid Inputs");
		}
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
	 * Retrieve the Realized Overnight Policy Index Rate
	 * 
	 * @return The Realized Overnight Policy Index Rate
	 */

	public double overnightPolicyIndexRate()
	{
		return _dblOvernightPolicyIndexRate;
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
	 * Retrieve the Realized Collateral Scheme Rate
	 * 
	 * @return The Realized Collateral Scheme Rate
	 */

	public double collateralSchemeRate()
	{
		return _dblOvernightPolicyIndexRate + _dblCollateralSchemeSpread;
	}

	/**
	 * Retrieve the Realized Bank Senior Funding Market Numeraire Vertex
	 * 
	 * @return The Realized Bank Senior Funding Market Numeraire Vertex
	 */

	public org.drip.xva.universe.EntityMarketVertex bankMarketSeniorFunding()
	{
		return _emvBankSeniorFunding;
	}

	/**
	 * Retrieve the Realized Bank Subordinate Funding Market Numeraire Vertex
	 * 
	 * @return The Realized Bank Subordinate Funding Market Numeraire Vertex
	 */

	public org.drip.xva.universe.EntityMarketVertex bankMarketSubordinateFunding()
	{
		return _emvBankSubordinateFunding;
	}

	/**
	 * Retrieve the Array of Realized Counter Party Market Vertexes
	 * 
	 * @return The Array of Realized Counter Party Market Vertexes
	 */

	public org.drip.xva.universe.EntityMarketVertex[] counterPartyMarket()
	{
		return _aEMVCounterParty;
	}

	/**
	 * Retrieve the Array of Realized Counter Party Survival Probabilities
	 * 
	 * @return The Array of Realized Counter Party Survival Probabilities
	 */

	public double[] counterPartySurvival()
	{
		int iNumCounterParty = _aEMVCounterParty.length;
		double[] adblCounterPartySurvival = new double[iNumCounterParty];

		for (int i = 0; i < iNumCounterParty; ++i)
			adblCounterPartySurvival[i] = _aEMVCounterParty[i].survival();

		return adblCounterPartySurvival;
	}

	/**
	 * Retrieve the Array of Realized Counter Party Recovery Rates
	 * 
	 * @return The Array of Realized Counter Party Recovery Rates
	 */

	public double[] counterPartyRecovery()
	{
		int iNumCounterParty = _aEMVCounterParty.length;
		double[] adblCounterPartyRecovery = new double[iNumCounterParty];

		for (int i = 0; i < iNumCounterParty; ++i)
			adblCounterPartyRecovery[i] = _aEMVCounterParty[i].recovery();

		return adblCounterPartyRecovery;
	}

	/**
	 * Retrieve the Realized Counter Party Survival Probability
	 * 
	 * @param iCounterParty The Counter Party Index
	 * 
	 * @return The Realized Counter Party Survival Probability
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double counterPartySurvival (
		final int iCounterParty)
		throws java.lang.Exception
	{
		if (iCounterParty >= _aEMVCounterParty.length)
			throw new java.lang.Exception ("MarketVertex::counterPartySurvival => Invalid Inputs");

		return _aEMVCounterParty[iCounterParty].survival();
	}

	/**
	 * Retrieve the Realized Counter Party Recovery Rate
	 * 
	 * @param iCounterParty The Counter Party Index
	 * 
	 * @return The Realized Counter Party Recovery Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double counterPartyRecovery (
		final int iCounterParty)
		throws java.lang.Exception
	{
		if (iCounterParty >= _aEMVCounterParty.length)
			throw new java.lang.Exception ("MarketVertex::counterPartyRecovery => Invalid Inputs");

		return _aEMVCounterParty[iCounterParty].recovery();
	}
}
