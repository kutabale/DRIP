
package org.drip.xva.netting;

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
 * GroupTrajectoryEdge holds the Edge that contains the Vertex Realizations of a Projected Path of a Single
 *  Simulation Run along the Granularity of a Netting Group. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
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

public class GroupTrajectoryEdge {
	private org.drip.xva.netting.GroupTrajectoryVertex _gtvHead = null;
	private org.drip.xva.netting.GroupTrajectoryVertex _gtvTail = null;

	/**
	 * GroupTrajectoryEdge Constructor
	 * 
	 * @param gtvHead The Head Vertex
	 * @param gtvTail The Tail Vertex
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public GroupTrajectoryEdge (
		final org.drip.xva.netting.GroupTrajectoryVertex gtvHead,
		final org.drip.xva.netting.GroupTrajectoryVertex gtvTail)
		throws java.lang.Exception
	{
		if (null == (_gtvHead = gtvHead) || null == (_gtvTail = gtvTail) || _gtvHead.vertex().julian() >=
			_gtvTail.vertex().julian())
			throw new java.lang.Exception ("GroupTrajectoryEdge Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Head Vertex
	 * 
	 * @return The Head Vertex
	 */

	public org.drip.xva.netting.GroupTrajectoryVertex head()
	{
		return _gtvHead;
	}

	/**
	 * Retrieve the Tail Vertex
	 * 
	 * @return The Tail Vertex
	 */

	public org.drip.xva.netting.GroupTrajectoryVertex tail()
	{
		return _gtvTail;
	}

	/**
	 * Compute the Period Edge Credit Adjustment
	 * 
	 * @return The Period Edge Credit Adjustment
	 */

	public double credit()
	{
		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnHead = _gtvHead.numeraire();

		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnTail = _gtvTail.numeraire();

		return 0.5 * (_gtvTail.exposure().positive() * gtvnTail.collateral() + _gtvHead.exposure().positive()
			* gtvnHead.collateral()) * (gtvnTail.bankSurvival() - gtvnHead.bankSurvival()) * (1. - 0.5 *
				(gtvnHead.bankRecovery() + gtvnTail.bankRecovery()));
	}

	/**
	 * Compute the Period Edge Debt Adjustment
	 * 
	 * @return The Period Edge Debt Adjustment
	 */

	public double debt()
	{
		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnHead = _gtvHead.numeraire();

		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnTail = _gtvTail.numeraire();

		return 0.5 * (_gtvTail.exposure().negative() * gtvnTail.collateral() + _gtvHead.exposure().negative()
			* gtvnHead.collateral()) * (gtvnTail.counterPartySurvival() - gtvnHead.counterPartySurvival()) *
				(1. - 0.5 * (gtvnHead.counterPartyRecovery() + gtvnTail.counterPartyRecovery()));
	}

	/**
	 * Compute the Period Edge Funding Adjustment
	 * 
	 * @return The Period Edge Funding Adjustment
	 */

	public double funding()
	{
		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnHead = _gtvHead.numeraire();

		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnTail = _gtvTail.numeraire();

		return 0.5 * (_gtvTail.exposure().positive() * gtvnTail.collateral() * gtvnTail.bankFundingSpread() +
			_gtvHead.exposure().positive() * gtvnHead.collateral() * gtvnHead.bankFundingSpread()) *
				(_gtvTail.vertex().julian() - _gtvHead.vertex().julian()) / 365.25;
	}

	/**
	 * Generate the Assorted XVA Adjustment Metrics
	 * 
	 * @return The Assorted XVA Adjustment Metrics
	 */

	public org.drip.xva.netting.GroupTrajectoryEdgeAdjustment generate()
	{
		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnHead = _gtvHead.numeraire();

		org.drip.xva.netting.GroupTrajectoryVertexNumeraire gtvnTail = _gtvTail.numeraire();

		double dblHeadCollateral = gtvnHead.collateral();

		double dblTailCollateral = gtvnTail.collateral();

		double dblHeadPositiveExposure = _gtvHead.exposure().positive();

		double dblTailPositiveExposure = _gtvTail.exposure().positive();

		double dblHeadNegativeExposure = _gtvHead.exposure().negative();

		double dblTailNegativeExposure = _gtvTail.exposure().negative();

		double dblEdgeBankRecovery = 0.5 * (gtvnHead.bankRecovery() + gtvnTail.bankRecovery());

		double dblEdgeCounterPartyRecovery = 0.5 * (gtvnHead.counterPartyRecovery() +
			gtvnTail.counterPartyRecovery());

		double dblHeadPositiveExposurePV = dblHeadPositiveExposure * dblHeadCollateral;
		double dblTailPositiveExposurePV = dblTailPositiveExposure * dblTailCollateral;
		double dblHeadNegativeExposurePV = dblHeadNegativeExposure * dblHeadCollateral;
		double dblTailNegativeExposurePV = dblTailNegativeExposure * dblTailCollateral;
		double dblEdgePositiveExposurePV = 0.5 * (dblTailPositiveExposurePV + dblHeadPositiveExposurePV);
		double dblEdgeNegativeExposurePV = 0.5 * (dblTailNegativeExposurePV + dblHeadNegativeExposurePV);

		try {
			return new org.drip.xva.netting.GroupTrajectoryEdgeAdjustment (
				dblEdgePositiveExposurePV * (gtvnTail.bankSurvival() - gtvnHead.bankSurvival()) *
					(1. - dblEdgeBankRecovery),
				dblEdgeNegativeExposurePV * (gtvnTail.counterPartySurvival() - gtvnHead.counterPartySurvival()) *
					(1. - dblEdgeCounterPartyRecovery),
				0.5 * (
					dblTailPositiveExposurePV * gtvnTail.bankFundingSpread() +
					dblHeadPositiveExposurePV * gtvnHead.bankFundingSpread()
				) * (_gtvTail.vertex().julian() - _gtvHead.vertex().julian()) / 365.25,
				0.5 * (dblTailPositiveExposure + dblHeadPositiveExposure),
				0.5 * (dblTailNegativeExposure + dblHeadNegativeExposure),
				dblEdgePositiveExposurePV,
				dblEdgeNegativeExposurePV,
				dblEdgeBankRecovery,
				dblEdgeCounterPartyRecovery
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
