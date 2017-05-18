
package org.drip.xva.book;

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
 * CounterPartyGroupPathExposure cumulates the Exposures and the Adjustments across Multiple Netting/Funding
 *  Groups on a Single Path Projection Run across multiple Counter Party Groups the constitute a Book. The
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

public class CounterPartyGroupPathExposure {
	private org.drip.xva.cpty.PathExposureAdjustment[] _aPEACounterParty = null;

	/**
	 * CounterPartyGroupPathExposure Constructor
	 * 
	 * @param aPEA Array of the Path Realizations, one per Counter Party Group
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CounterPartyGroupPathExposure (
		final org.drip.xva.cpty.PathExposureAdjustment[] aPEACounterParty)
		throws java.lang.Exception
	{
		if (null == (_aPEACounterParty = aPEACounterParty) || 0 == _aPEACounterParty.length)
			throw new java.lang.Exception ("CounterPartyGroupPathExposure Constructor => Invalid Inputs");

		int iNumCounterPartyGroup = _aPEACounterParty.length;

		if (0 == iNumCounterPartyGroup)
			throw new java.lang.Exception ("CounterPartyGroupPathExposure Constructor => Invalid Inputs");

		for (int i = 0; i < iNumCounterPartyGroup; ++i) {
			if (null == _aPEACounterParty[i])
				throw new java.lang.Exception
					("CounterPartyGroupPathExposure Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Array of Counter Party Group Paths
	 * 
	 * @return Array of Counter Party Group Paths
	 */

	public org.drip.xva.cpty.PathExposureAdjustment[] counterPartyGroupPaths()
	{
		return _aPEACounterParty;
	}

	/**
	 * Retrieve the Array of the Vertex Anchor Dates
	 * 
	 * @return The Array of the Vertex Anchor Dates
	 */

	public org.drip.analytics.date.JulianDate[] anchors()
	{
		return _aPEACounterParty[0].anchors();
	}

	/**
	 * Retrieve the Array of Collateralized Exposures
	 * 
	 * @return The Array of Collateralized Exposures
	 */

	public double[] collateralizedExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCounterPartyGroup = _aPEACounterParty.length;
		double[] adblCollateralizedExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedExposure[j] = 0.;

		for (int iCounterPartyGroupIndex = 0; iCounterPartyGroupIndex < iNumCounterPartyGroup;
			++iCounterPartyGroupIndex) {
			double[] adblCounterPartyGroupCollateralizedExposure =
				_aPEACounterParty[iCounterPartyGroupIndex].collateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedExposure[iVertexIndex] +=
					adblCounterPartyGroupCollateralizedExposure[iVertexIndex];
		}

		return adblCollateralizedExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Exposure PVs
	 * 
	 * @return The Array of Collateralized Exposure PVs
	 */

	public double[] collateralizedExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCounterPartyGroup = _aPEACounterParty.length;
		double[] adblCollateralizedExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedExposurePV[j] = 0.;

		for (int iCounterPartyGroupIndex = 0; iCounterPartyGroupIndex < iNumCounterPartyGroup;
			++iCounterPartyGroupIndex) {
			double[] adblCounterPartyGroupCollateralizedExposurePV =
				_aPEACounterParty[iCounterPartyGroupIndex].collateralizedExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedExposurePV[iVertexIndex] +=
					adblCounterPartyGroupCollateralizedExposurePV[iVertexIndex];
		}

		return adblCollateralizedExposurePV;
	}
}
