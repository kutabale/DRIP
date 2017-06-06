
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
 * LatentStateVertex holds the Vertex Realizations of the Latent States of the Reference Universe. The
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

public class LatentStateVertex {
	private org.drip.measure.realization.JumpDiffusionVertex _jdvAssetNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvOvernightIndexNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvCollateralSchemeNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvBankSeniorFundingNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvCounterPartyFundingNumeraire = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvBankSubordinateFundingNumeraire = null;

	private org.drip.measure.realization.JumpDiffusionVertex _jdvBankHazardRate = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvBankSeniorRecoveryRate = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvCounterPartyHazardRate = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvCounterPartyRecoveryRate = null;
	private org.drip.measure.realization.JumpDiffusionVertex _jdvBankSubordinateRecoveryRate = null;

	/**
	 * Create a LatentStateVertex Instance with Single Bank Senior Funding Instrument
	 * 
	 * @param jdvAssetNumeraire The Asset Numeraire Level Realization JDV
	 * @param jdvOvernightIndexNumeraire The Realized Overnight Index Numeraire JDV
	 * @param jdvCollateralSchemeNumeraire The Realized Collateral Scheme Numeraire JDV
	 * @param jdvBankSeniorFundingNumeraire The Realized Bank Senior Funding Numeraire JDV
	 * @param jdvCounterPartyFundingNumeraire The Realized Counter Party Funding Numeraire JDV
	 * @param jdvBankHazardRate The Realized Bank Hazard Rate JDV
	 * @param jdvBankSeniorRecoveryRate The Realized Bank Senior Recovery Rate JDV
	 * @param jdvCounterPartyHazardRate The Realized Counter Party Hazard Rate JDV
	 * @param jdvCounterPartyRecoveryRate The Realized Counter Party Recovery Rate JDV
	 * 
	 * @return The LatentStateVertex Instance with Single Bank Senior Funding Instrument
	 */

	public static final LatentStateVertex BankSenior (
		final org.drip.measure.realization.JumpDiffusionVertex jdvAssetNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvOvernightIndexNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCollateralSchemeNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankSeniorFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCounterPartyFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankHazardRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankSeniorRecoveryRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCounterPartyHazardRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCounterPartyRecoveryRate)
	{
		try {
			return new LatentStateVertex (jdvAssetNumeraire, jdvOvernightIndexNumeraire,
				jdvCollateralSchemeNumeraire, jdvBankSeniorFundingNumeraire, null,
					jdvCounterPartyFundingNumeraire, jdvBankHazardRate, jdvBankSeniorRecoveryRate,
						null, jdvCounterPartyHazardRate, jdvCounterPartyRecoveryRate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * LatentStateVertex Constructor
	 * 
	 * @param jdvAssetNumeraire The Asset Numeraire JDV
	 * @param jdvOvernightIndexNumeraire The Realized Overnight Index Numeraire JDV
	 * @param jdvCollateralSchemeNumeraire The Realized Collateral Scheme Numeraire JDV
	 * @param jdvBankSeniorFundingNumeraire The Realized Bank Senior Funding Numeraire JDV
	 * @param jdvBankSubordinateFundingNumeraire The Realized Bank Subordinate Funding Numeraire JDV
	 * @param jdvCounterPartyFundingNumeraire The Realized Counter Party Funding Numeraire JDV
	 * @param jdvBankHazardRate The Realized Bank Hazard Rate JDV
	 * @param jdvBankSeniorRecoveryRate The Realized Bank Senior Recovery Rate JDV
	 * @param jdvBankSubordinateRecoveryRate The Realized Bank Subordinate Recovery Rate JDV
	 * @param jdvCounterPartyHazardRate The Realized Counter Party Hazard Rate JDV
	 * @param jdvCounterPartyRecoveryRate The Realized Counter Party Recovery Rate JDV
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LatentStateVertex (
		final org.drip.measure.realization.JumpDiffusionVertex jdvAssetNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvOvernightIndexNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCollateralSchemeNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankSeniorFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankSubordinateFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCounterPartyFundingNumeraire,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankHazardRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankSeniorRecoveryRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvBankSubordinateRecoveryRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCounterPartyHazardRate,
		final org.drip.measure.realization.JumpDiffusionVertex jdvCounterPartyRecoveryRate)
		throws java.lang.Exception
	{
		if (null == (_jdvAssetNumeraire = jdvAssetNumeraire) || null == (_jdvOvernightIndexNumeraire =
			jdvOvernightIndexNumeraire) || null == (_jdvCollateralSchemeNumeraire =
				jdvCollateralSchemeNumeraire) || null == (_jdvBankSeniorFundingNumeraire =
					jdvBankSeniorFundingNumeraire) || null == (_jdvCounterPartyFundingNumeraire =
						jdvCounterPartyFundingNumeraire) || null == (_jdvBankHazardRate = jdvBankHazardRate)
							|| null == (_jdvBankSeniorRecoveryRate = jdvBankSeniorRecoveryRate) || null ==
								(_jdvCounterPartyHazardRate = jdvCounterPartyHazardRate) || null ==
									(_jdvCounterPartyRecoveryRate = jdvCounterPartyRecoveryRate) )
			throw new java.lang.Exception ("LatentStateVertex Constructor => Invalid Inputs");

		_jdvBankSubordinateRecoveryRate = jdvBankSubordinateRecoveryRate;
		_jdvBankSubordinateFundingNumeraire = jdvBankSubordinateFundingNumeraire;
	}

	/**
	 * Retrieve the Latent State Vertex Instant
	 * 
	 * @return The Latent State Vertex Instant
	 */

	public double vertex()
	{
		return _jdvAssetNumeraire.time();
	}

	/**
	 * Retrieve the Asset Numeraire Vertex
	 * 
	 * @return The Asset Numeraire Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex assetNumeraire()
	{
		return _jdvAssetNumeraire;
	}

	/**
	 * Retrieve the Realized Overnight Index Numeraire Vertex
	 * 
	 * @return The Realized Overnight Index Numeraire Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex overnightIndexNumeraire()
	{
		return _jdvOvernightIndexNumeraire;
	}

	/**
	 * Retrieve the Realized Collateral Scheme Numeraire Vertex
	 * 
	 * @return The Realized Collateral Scheme Numeraire Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex collateralSchemeNumeraire()
	{
		return _jdvCollateralSchemeNumeraire;
	}

	/**
	 * Retrieve the Realized Bank Senior Funding Numeraire Vertex
	 * 
	 * @return The Realized Bank Senior Funding Numeraire Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex bankSeniorFundingNumeraire()
	{
		return _jdvBankSeniorFundingNumeraire;
	}

	/**
	 * Retrieve the Realized Bank Subordinate Funding Numeraire Vertex
	 * 
	 * @return The Realized Bank Subordinate Funding Numeraire Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex bankSubordinateFundingNumeraire()
	{
		return _jdvBankSubordinateFundingNumeraire;
	}

	/**
	 * Retrieve the Realized Counter Party Funding Numeraire Vertex
	 * 
	 * @return The Realized Counter Party Funding Numeraire Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex counterPartyFundingNumeraire()
	{
		return _jdvCounterPartyFundingNumeraire;
	}

	/**
	 * Retrieve the Realized Bank Hazard Rate Vertex
	 * 
	 * @return The Realized Bank Hazard Rate Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex bankHazardRate()
	{
		return _jdvBankHazardRate;
	}

	/**
	 * Retrieve the Realized Bank Senior Recovery Rate Vertex
	 * 
	 * @return The Realized Bank Senior Recovery Rate Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex bankSeniorRecoveryRate()
	{
		return _jdvBankSeniorRecoveryRate;
	}

	/**
	 * Retrieve the Realized Bank Subordinate Recovery Rate Vertex
	 * 
	 * @return The Realized Bank Subordinate Recovery Rate Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex bankSubordinateRecoveryRate()
	{
		return _jdvBankSubordinateRecoveryRate;
	}

	/**
	 * Retrieve the Realized Counter Party Hazard Rate Vertex
	 * 
	 * @return The Realized Counter Party Hazard Rate Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex counterPartyHazardRate()
	{
		return _jdvCounterPartyHazardRate;
	}

	/**
	 * Retrieve the Realized Counter Party Recovery Rate Vertex
	 * 
	 * @return The Realized Counter Party Recovery Rate Vertex
	 */

	public org.drip.measure.realization.JumpDiffusionVertex counterPartyRecoveryRate()
	{
		return _jdvCounterPartyRecoveryRate;
	}
}
