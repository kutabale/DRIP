
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
 * LatentStateEdge holds the Vertex Realizations of the Latent States of the Reference Universe along an
 * 	Evolution Edge. The References are:
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

public class LatentStateEdge {
	private org.drip.xva.universe.LatentStateVertex _lsvStart = null;
	private org.drip.xva.universe.LatentStateVertex _lsvFinish = null;

	/**
	 * LatentStateEdge Constructor
	 * 
	 * @param lsvStart The Starting Latent State Vertex
	 * @param lsvFinish The Ending Latent State Vertex
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LatentStateEdge (
		final org.drip.xva.universe.LatentStateVertex lsvStart,
		final org.drip.xva.universe.LatentStateVertex lsvFinish)
		throws java.lang.Exception
	{
		if (null == (_lsvStart = lsvStart) || null == (_lsvFinish = lsvFinish) || _lsvStart.time() >=
			_lsvFinish.time())
			throw new java.lang.Exception ("LatentStateEdge Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Latent State Vertex Increment
	 * 
	 * @return The Latent State Vertex Increment
	 */

	public double vertexIncrement()
	{
		return _lsvFinish.time() - _lsvStart.time();
	}

	/**
	 * Retrieve the Start Latent State Vertex
	 * 
	 * @return The Start Latent State Vertex
	 */

	public org.drip.xva.universe.LatentStateVertex start()
	{
		return _lsvStart;
	}

	/**
	 * Retrieve the Finish Latent State Vertex
	 * 
	 * @return The Finish Latent State Vertex
	 */

	public org.drip.xva.universe.LatentStateVertex finish()
	{
		return _lsvFinish;
	}

	/**
	 * Compute the Bank Senior Funding Spread
	 * 
	 * @return The Bank Senior Funding Spread
	 */

	public double bankSeniorFundingSpread()
	{
		org.drip.measure.realization.JumpDiffusionVertex jdvOvernightIndexNumeraireStart =
			_lsvStart.overnightIndexNumeraire();

		org.drip.measure.realization.JumpDiffusionVertex jdvBankSeniorFundingNumeraireStart =
			_lsvStart.bankSeniorFundingNumeraire();

		org.drip.measure.realization.JumpDiffusionVertex jdvOvernightIndexNumeraireFinish =
			_lsvFinish.overnightIndexNumeraire();

		org.drip.measure.realization.JumpDiffusionVertex jdvBankSeniorFundingNumeraireFinish =
			_lsvFinish.bankSeniorFundingNumeraire();

		return java.lang.Math.log (jdvBankSeniorFundingNumeraireStart.value() /
			jdvBankSeniorFundingNumeraireFinish.value()) - java.lang.Math.log
				(jdvOvernightIndexNumeraireStart.value() / jdvOvernightIndexNumeraireFinish.value());
	}

	/**
	 * Compute the Bank Subordinate Funding Spread
	 * 
	 * @return The Bank Subordinate Funding Spread
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double bankSubordinateFundingSpread()
		throws java.lang.Exception
	{
		org.drip.measure.realization.JumpDiffusionVertex jdvBankSubordinateFundingNumeraireStart =
			_lsvStart.bankSubordinateFundingNumeraire();

		org.drip.measure.realization.JumpDiffusionVertex jdvBankSubordinateFundingNumeraireFinish =
			_lsvFinish.bankSubordinateFundingNumeraire();

		if (null == jdvBankSubordinateFundingNumeraireStart || null ==
			jdvBankSubordinateFundingNumeraireFinish)
			throw new java.lang.Exception
				("LatentStateEdge::bankSubordinateFundingSpread => Invalid Inputs");

		org.drip.measure.realization.JumpDiffusionVertex jdvOvernightIndexNumeraireStart =
			_lsvStart.overnightIndexNumeraire();

		org.drip.measure.realization.JumpDiffusionVertex jdvOvernightIndexNumeraireFinish =
			_lsvFinish.overnightIndexNumeraire();

		return java.lang.Math.log (jdvBankSubordinateFundingNumeraireStart.value() /
			jdvBankSubordinateFundingNumeraireFinish.value()) - java.lang.Math.log
				(jdvOvernightIndexNumeraireStart.value() / jdvOvernightIndexNumeraireFinish.value());
	}
}
