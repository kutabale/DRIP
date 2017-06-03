
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
 * LatentStateDynamicsContainer describes the Economy with the following Traded Assets - the Overnight Index
 *  Numeraire, the Collateral Scheme Numeraire, the Default-able Bank Bond Numeraire, the Array of
 *  Default-able Counter-party Numeraires, and an Asset that follows Brownian Motion. The References are:
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

public class LatentStateDynamicsContainer {
	private org.drip.xva.universe.Tradeable _tAsset = null;
	private org.drip.xva.universe.Tradeable _tOvernightIndex = null;
	private org.drip.xva.universe.Tradeable _tCollateralScheme = null;
	private org.drip.xva.universe.Tradeable _tBankSeniorFunding = null;
	private org.drip.xva.universe.Tradeable _tCounterPartyFunding = null;
	private org.drip.xva.universe.Tradeable _tBankSubordinateFunding = null;
	private org.drip.measure.process.DiffusionEvolver _deBankHazardRate = null;
	private org.drip.measure.process.DiffusionEvolver _deBankRecoveryRate = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyHazardRate = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyRecoveryRate = null;

	/**
	 * Create a LatentStateDynamicsContainer without the Zero Recovery Bank Funding Tradeable
	 * 
	 * @param tAsset The Asset Tradeable
	 * @param tOvernightIndex The Overnight Index Tradeable
	 * @param tCollateralScheme The Collateral Scheme Tradeable
	 * @param tBankSeniorFunding Bank Senior Funding Tradeable
	 * @param tCounterPartyFunding Counter Party Funding Tradeable
	 * @param deBankHazardRate The Bank Hazard Rate Evolver
	 * @param deBankRecoveryRate The Bank Recovery Rate Evolver
	 * @param deCounterPartyHazardRate The Counter Party Hazard Rate Evolver
	 * @param deCounterPartyRecoveryRate The Counter Party Recovery Rate Evolver
	 * 
	 * @return The LatentStateDynamicsContainer without the Zero Recovery Bank Funding Tradeable
	 */

	public static final LatentStateDynamicsContainer Standard (
		final org.drip.xva.universe.Tradeable tAsset,
		final org.drip.xva.universe.Tradeable tOvernightIndex,
		final org.drip.xva.universe.Tradeable tCollateralScheme,
		final org.drip.xva.universe.Tradeable tBankSeniorFunding,
		final org.drip.xva.universe.Tradeable tCounterPartyFunding,
		final org.drip.measure.process.DiffusionEvolver deBankHazardRate,
		final org.drip.measure.process.DiffusionEvolver deBankRecoveryRate,
		final org.drip.measure.process.DiffusionEvolver deCounterPartyHazardRate,
		final org.drip.measure.process.DiffusionEvolver deCounterPartyRecoveryRate)
	{
		try {
			return new LatentStateDynamicsContainer (tAsset, tOvernightIndex, tCollateralScheme,
				tBankSeniorFunding, null, tCounterPartyFunding, deBankHazardRate, deBankRecoveryRate,
					deCounterPartyHazardRate, deCounterPartyRecoveryRate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * LatentStateDynamicsContainer Constructor
	 * 
	 * @param tAsset The Asset Tradeable
	 * @param tOvernightIndex The Overnight Index Tradeable
	 * @param tCollateralScheme The Collateral Scheme Tradeable
	 * @param tBankSeniorFunding Bank Senior Funding Tradeable
	 * @param tBankSubordinateFunding Bank Subordinate Funding Tradeable
	 * @param tCounterPartyFunding Counter Party Funding Tradeable
	 * @param deBankHazardRate The Bank Hazard Rate Evolver
	 * @param deBankRecoveryRate The Bank Recovery Rate Evolver
	 * @param deCounterPartyHazardRate The Counter Party Hazard Rate Evolver
	 * @param deCounterPartyRecoveryRate The Counter Party Recovery Rate Evolver
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LatentStateDynamicsContainer (
		final org.drip.xva.universe.Tradeable tAsset,
		final org.drip.xva.universe.Tradeable tOvernightIndex,
		final org.drip.xva.universe.Tradeable tCollateralScheme,
		final org.drip.xva.universe.Tradeable tBankSeniorFunding,
		final org.drip.xva.universe.Tradeable tBankSubordinateFunding,
		final org.drip.xva.universe.Tradeable tCounterPartyFunding,
		final org.drip.measure.process.DiffusionEvolver deBankHazardRate,
		final org.drip.measure.process.DiffusionEvolver deBankRecoveryRate,
		final org.drip.measure.process.DiffusionEvolver deCounterPartyHazardRate,
		final org.drip.measure.process.DiffusionEvolver deCounterPartyRecoveryRate)
		throws java.lang.Exception
	{
		if (null == (_tAsset = tAsset) || null == (_tCounterPartyFunding = tCounterPartyFunding))
			throw new java.lang.Exception ("LatentStateDynamicsContainer Constructor => Invalid Inputs");

		_tOvernightIndex = tOvernightIndex;
		_deBankHazardRate = deBankHazardRate;
		_tCollateralScheme = tCollateralScheme;
		_tBankSeniorFunding = tBankSeniorFunding;
		_deBankRecoveryRate = deBankRecoveryRate;
		_tBankSubordinateFunding = tBankSubordinateFunding;
		_deCounterPartyHazardRate = deCounterPartyHazardRate;
		_deCounterPartyRecoveryRate = deCounterPartyRecoveryRate;
	}

	/**
	 * Retrieve the Asset Tradeable
	 * 
	 * @return The Asset Tradeable
	 */

	public org.drip.xva.universe.Tradeable asset()
	{
		return _tAsset;
	}

	/**
	 * Retrieve the Overnight Index Tradeable
	 * 
	 * @return The Overnight Index Tradeable
	 */

	public org.drip.xva.universe.Tradeable overnightIndex()
	{
		return _tOvernightIndex;
	}

	/**
	 * Retrieve the Collateral Scheme Tradeable
	 * 
	 * @return The Collateral Scheme Tradeable
	 */

	public org.drip.xva.universe.Tradeable collateralScheme()
	{
		return _tCollateralScheme;
	}

	/**
	 * Retrieve the Bank Senior Funding Tradeable
	 * 
	 * @return The Bank Senior Funding Tradeable
	 */

	public org.drip.xva.universe.Tradeable bankSeniorFunding()
	{
		return _tBankSeniorFunding;
	}

	/**
	 * Retrieve the Bank Subordinate Funding Tradeable
	 * 
	 * @return The Bank Subordinate Funding Tradeable
	 */

	public org.drip.xva.universe.Tradeable bankSubordinateFunding()
	{
		return _tBankSubordinateFunding;
	}

	/**
	 * Retrieve the Counter Party Funding Tradeable
	 * 
	 * @return The Counter Party Funding Tradeable
	 */

	public org.drip.xva.universe.Tradeable counterPartyFunding()
	{
		return _tCounterPartyFunding;
	}

	/**
	 * Retrieve the Bank Hazard Rate Evolver
	 * 
	 * @return The Bank Hazard Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankHazardRateEvolver()
	{
		return _deBankHazardRate;
	}

	/**
	 * Retrieve the Bank Recovery Rate Evolver
	 * 
	 * @return The Bank Recovery Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankRecoveryRateEvolver()
	{
		return _deBankRecoveryRate;
	}

	/**
	 * Retrieve the Counter Party Hazard Rate Evolver
	 * 
	 * @return The Counter Party Hazard Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver counterPartyHazardRateEvolver()
	{
		return _deCounterPartyHazardRate;
	}

	/**
	 * Retrieve the Counter Party Recovery Rate Evolver
	 * 
	 * @return The Counter Party Recovery Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver counterPartyRecoveryRateEvolver()
	{
		return _deCounterPartyRecoveryRate;
	}
}
