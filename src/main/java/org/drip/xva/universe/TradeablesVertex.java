
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
 * TradeablesVertex holds the current Realizations of the Traded Numeraires of the Reference Universe. The
 *  References are:
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

public class TradeablesVertex {
	private org.drip.measure.realization.JumpDiffusionEdge _jdeAssetNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionEdge _jdeOvernightIndexNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionEdge _jdeCollateralSchemeNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionEdge _jdeBankSeniorFundingNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionEdge _jdeBankSubordinateFundingNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionEdge[] _aJDECounterPartyFundingNumeraire = null;

	/**
	 * Create a TradeablesVertex Instance without the Zero Recovery Bank Funding Numeraire
	 * 
	 * @param jdeAssetNumeraire The Asset Numeraire Level Realization
	 * @param jdeOvernightIndexNumeraire The Realized Overnight Index Numeraire
	 * @param jdeCollateralSchemeNumeraire The Realized Collateral Scheme Numeraire
	 * @param jdeBankSeniorFundingNumeraire The Realized Bank Senior Funding Numeraire
	 * @param aJDECounterPartyFundingNumeraire The Realized Counter Party Funding Numeraire Array
	 * 
	 * @return The TradeablesVertex Instance without the Zero Recovery Bank Numeraire
	 */

	public static final TradeablesVertex Standard (
		final org.drip.measure.realization.JumpDiffusionEdge jdeAssetNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeOvernightIndexNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeCollateralSchemeNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeBankSeniorFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge[] aJDECounterPartyFundingNumeraire)
	{
		try {
			return new TradeablesVertex (jdeAssetNumeraire, jdeOvernightIndexNumeraire,
				jdeCollateralSchemeNumeraire, jdeBankSeniorFundingNumeraire, null,
					aJDECounterPartyFundingNumeraire);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * TradeablesVertex Constructor
	 * 
	 * @param jdeAssetNumeraire The Asset Numeraire
	 * @param jdeOvernightIndexNumeraire The Realized Overnight Index Numeraire
	 * @param jdeCollateralSchemeNumeraire The Realized Collateral Scheme Numeraire
	 * @param jdeBankSeniorFundingNumeraire The Realized Bank Senior Funding Numeraire
	 * @param jdeBankSubordinateFundingNumeraire The Realized Bank Subordinate Funding Numeraire
	 * @param aJDECounterPartyFundingNumeraire The Realized Counter Party Funding Numeraire Array
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TradeablesVertex (
		final org.drip.measure.realization.JumpDiffusionEdge jdeAssetNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeOvernightIndexNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeCollateralSchemeNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeBankSeniorFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge jdeBankSubordinateFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionEdge[] aJDECounterPartyFundingNumeraire)
		throws java.lang.Exception
	{
		if (null == (_jdeAssetNumeraire = jdeAssetNumeraire) || null == (_aJDECounterPartyFundingNumeraire =
			aJDECounterPartyFundingNumeraire))
			throw new java.lang.Exception ("TradeablesVertex Constructor => Invalid Inputs");

		_jdeOvernightIndexNumeraire = jdeOvernightIndexNumeraire;
		_jdeCollateralSchemeNumeraire = jdeCollateralSchemeNumeraire;
		_jdeBankSeniorFundingNumeraire = jdeBankSeniorFundingNumeraire;
		int iNumCounterParty = _aJDECounterPartyFundingNumeraire.length;
		_jdeBankSubordinateFundingNumeraire = jdeBankSubordinateFundingNumeraire;

		if (0 >= iNumCounterParty)
			throw new java.lang.Exception ("TradeablesVertex Constructor => Invalid Inputs");

		for (int i = 0; i < iNumCounterParty; ++i) {
			if (null == _aJDECounterPartyFundingNumeraire[i])
				throw new java.lang.Exception ("TradeablesVertex Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Asset Numeraire
	 * 
	 * @return The Asset Numeraire
	 */

	public org.drip.measure.realization.JumpDiffusionEdge assetNumeraire()
	{
		return _jdeAssetNumeraire;
	}

	/**
	 * Retrieve the Realized Overnight Index Numeraire
	 * 
	 * @return The Realized Overnight Index Numeraire
	 */

	public org.drip.measure.realization.JumpDiffusionEdge overnightIndexNumeraire()
	{
		return _jdeOvernightIndexNumeraire;
	}

	/**
	 * Retrieve the Realized Collateral Scheme Numeraire
	 * 
	 * @return The Realized Collateral Scheme Numeraire
	 */

	public org.drip.measure.realization.JumpDiffusionEdge collateralSchemeNumeraire()
	{
		return _jdeCollateralSchemeNumeraire;
	}

	/**
	 * Retrieve the Realized Bank Senior Funding Numeraire
	 * 
	 * @return The Realized Bank Senior Funding Numeraire
	 */

	public org.drip.measure.realization.JumpDiffusionEdge bankSeniorFundingNumeraire()
	{
		return _jdeBankSeniorFundingNumeraire;
	}

	/**
	 * Retrieve the Realized Bank Subordinate Funding Numeraire
	 * 
	 * @return The Realized Bank Subordinate Funding Numeraire
	 */

	public org.drip.measure.realization.JumpDiffusionEdge bankSubordinateFundingNumeraire()
	{
		return _jdeBankSubordinateFundingNumeraire;
	}

	/**
	 * Retrieve the Array of Realized Counter Party Funding Numeraires
	 * 
	 * @return The Array of Realized Counter Party Funding Numeraires
	 */

	public org.drip.measure.realization.JumpDiffusionEdge[] counterPartyFundingNumeraire()
	{
		return _aJDECounterPartyFundingNumeraire;
	}

	/**
	 * Retrieve the Number of Counter Parties
	 * 
	 * @return The Number of Counter Parties
	 */

	public int numCounterParty()
	{
		return _aJDECounterPartyFundingNumeraire.length;
	}
}
