
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
 * BurgardKjaerVertex holds the Vertex Realizations off an Imperfect Hedge Based Incremental Burgard Kjaer
 *  Simulation Step. The References are:
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

public class BurgardKjaerVertex {
	private org.drip.xva.strategy.BurgardKjaerExposure _bke = null;
	private org.drip.xva.derivative.ReplicationPortfolioVertex _rpv = null;
	private org.drip.xva.strategy.BurgardKjaerValuationAdjustment _bkva = null;

	/**
	 * Create a Standard Instance of BurgardKjaerVertex
	 * 
	 * @param bke The Burgard Kjaer (2013) Exposures
	 * @param rpv The Replication Portfolio Vertex Instance
	 * @param mv The Market Vertex
	 * @param dblTimeIncrement Time Increment
	 * 
	 * @return The Standard Instance of BurgardKjaerVertex
	 */

	public static final BurgardKjaerVertex Standard (
		final org.drip.xva.strategy.BurgardKjaerExposure bke,
		final org.drip.xva.derivative.ReplicationPortfolioVertex rpv,
		final org.drip.xva.universe.MarketVertex mv,
		final double dblTimeIncrement)
	{
		if (null == mv || !org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement)) return null;

		org.drip.xva.universe.EntityMarketVertex emvCounterParty = mv.counterParty();

		org.drip.xva.universe.EntityMarketVertex emvBank = mv.bank();

		double dblBankHazardRate = emvBank.hazardRate();

		double dblBilateralDFIncrement = -1. * mv.overnightIndexNumeraire() *
			emvCounterParty.survivalProbability() * emvBank.survivalProbability() * dblTimeIncrement;

		try {
			return new BurgardKjaerVertex (
				bke, new org.drip.xva.strategy.BurgardKjaerValuationAdjustment (
					dblBilateralDFIncrement * emvCounterParty.hazardRate() * bke.credit(),
					dblBilateralDFIncrement * dblBankHazardRate * bke.debt(),
					dblBilateralDFIncrement * dblBankHazardRate * bke.hedgeError(),
					dblBilateralDFIncrement * mv.collateralSchemeSpread() * bke.collateralAmount()
				), rpv
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * BurgardKjaerVertex Constructor
	 * 
	 * @param bke The Burgard Kjaer (2013) Exposures
	 * @param bkva The Burgard Kjaer (2013) Valuation Adjustment
	 * @param rpv The Replication Portfolio Vertex Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BurgardKjaerVertex (
		final org.drip.xva.strategy.BurgardKjaerExposure bke,
		final org.drip.xva.strategy.BurgardKjaerValuationAdjustment bkva,
		final org.drip.xva.derivative.ReplicationPortfolioVertex rpv)
		throws java.lang.Exception
	{
		if (null == (_bke = bke) || null == (_bkva = bkva) || null == (_rpv = rpv))
			throw new java.lang.Exception ("BurgardKjaerVertex Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Burgard Kjaer (2013) Exposures
	 * 
	 * @return The Burgard Kjaer (2013) Exposures
	 */

	public org.drip.xva.strategy.BurgardKjaerExposure exposure()
	{
		return _bke;
	}

	/**
	 * Retrieve the Burgard Kjaer (2013) Valuation Adjustment
	 * 
	 * @return The Burgard Kjaer (2013) Valuation Adjustment
	 */

	public org.drip.xva.strategy.BurgardKjaerValuationAdjustment adjustment()
	{
		return _bkva;
	}

	/**
	 * Retrieve the Replication Portfolio Vertex Instance
	 * 
	 * @return The Replication Portfolio Vertex Instance
	 */

	public org.drip.xva.derivative.ReplicationPortfolioVertex replicationPortfolioVertex()
	{
		return _rpv;
	}
}
