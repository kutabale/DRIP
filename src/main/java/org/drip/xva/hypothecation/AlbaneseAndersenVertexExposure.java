
package org.drip.xva.hypothecation;

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
 * AlbaneseAndersenVertexExposure holds the Albanese and Andersen (2014) Vertex Exposures of a Projected Path
 *  of a Simulation Run of a Collateral Hypothecation Group. The References are:
 *  
 *  - Albanese, C., and L. Andersen (2014): Accounting for OTC Derivatives: Funding Adjustments and the
 *  	Re-Hypothecation Option, https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2482955, eSSRN.
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

public class AlbaneseAndersenVertexExposure implements
	org.drip.xva.hypothecation.CollateralGroupVertexExposure {
	private double _dblForwardExposure = java.lang.Double.NaN;
	private double _dblRealizedCashFlow = java.lang.Double.NaN;
	private double _dblCollateralBalance = java.lang.Double.NaN;
	private org.drip.analytics.date.JulianDate _dtAnchor = null;

	/**
	 * AlbaneseAndersenVertexExposure Constructor
	 * 
	 * @param dtAnchor The Vertex Date Anchor
	 * @param dblForwardExposure The Forward Exposure at the Path Vertex Time Node
	 * @param dblRealizedCashFlow The Default Window Realized Cash-flow at the Path Vertex Time Node
	 * @param dblCollateralBalance The Collateral Balance at the Path Vertex Time Node
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public AlbaneseAndersenVertexExposure (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblForwardExposure,
		final double dblRealizedCashFlow,
		final double dblCollateralBalance)
		throws java.lang.Exception
	{
		if (null == (_dtAnchor = dtAnchor) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblForwardExposure = dblForwardExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblRealizedCashFlow = dblRealizedCashFlow) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralBalance = dblCollateralBalance))
			throw new java.lang.Exception ("AlbaneseAndersenVertexExposure Constructor => Invalid Inputs");
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
	 * Retrieve the Forward Exposure at the Path Vertex Time Node
	 * 
	 * @return The Forward Exposure at the Path Vertex Time Node
	 */

	public double forwardExposure()
	{
		return _dblForwardExposure;
	}

	/**
	 * Retrieve the Collateral Balance at the Path Vertex Time Node
	 * 
	 * @return The Collateral Balance at the Path Vertex Time Node
	 */

	public double collateralBalance()
	{
		return _dblCollateralBalance;
	}

	/**
	 * Retrieve the Default Window Realized Cash-flow at the Path Vertex Time Node
	 * 
	 * @return The Default Window Realized Cash-flow at the Path Vertex Time Node
	 */

	public double realizedCashFlow()
	{
		return _dblRealizedCashFlow;
	}

	/**
	 * Retrieve the Total Collateralized Exposure at the Path Vertex Time Node
	 * 
	 * @return The Total Collateralized Exposure at the Path Vertex Time Node
	 */

	public double collateralizedExposure()
	{
		return _dblForwardExposure + _dblRealizedCashFlow - _dblCollateralBalance;
	}

	/**
	 * Retrieve the Total Collateralized Exposure at the Path Vertex Time Node
	 * 
	 * @return The Total Collateralized Exposure at the Path Vertex Time Node
	 */

	public double uncollateralizedExposure()
	{
		return _dblForwardExposure + _dblRealizedCashFlow;
	}

	@Override public double credit()
	{
		double dblCreditExposure = collateralizedExposure();

		return 0. < dblCreditExposure ? dblCreditExposure : 0.;
	}

	@Override public double debt()
	{
		double dblDebtExposure = collateralizedExposure();

		return 0. > dblDebtExposure ? dblDebtExposure : 0.;
	}

	@Override public double funding()
	{
		double dblCreditExposure = collateralizedExposure();

		return 0. < dblCreditExposure ? dblCreditExposure : 0.;
	}
}
