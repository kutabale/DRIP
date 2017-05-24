
package org.drip.xva.collateral;

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
 * HypothecationGroupVertexCloseOut holds the Close Out Agreement Based Vertex Exposure of a Projected Path
 *  of a Simulation Run of a Collateral Hypothecation Group. The References are:
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

public class HypothecationGroupVertexCloseOut extends org.drip.xva.collateral.HypothecationGroupVertex {
	private org.drip.xva.definition.CloseOutGeneral _cog = null;

	/**
	 * HypothecationGroupVertexCloseOut Constructor
	 * 
	 * @param dtAnchor The Vertex Date Anchor
	 * @param dblForwardPV The Forward PV at the Path Vertex Time Node
	 * @param dblRealizedCashFlow The Default Window Realized Cash-flow at the Path Vertex Time Node
	 * @param dblCollateralBalance The Collateral Balance at the Path Vertex Time Node
	 * @param cog The Generic Close Out Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public HypothecationGroupVertexCloseOut (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblForwardPV,
		final double dblRealizedCashFlow,
		final double dblCollateralBalance,
		final org.drip.xva.definition.CloseOutGeneral cog)
		throws java.lang.Exception
	{
		super (dtAnchor, dblForwardPV, dblRealizedCashFlow, dblCollateralBalance);
	}

	/**
	 * Retrieve the Generic Close Out Instance
	 * 
	 * @return The Generic Close Out Instance
	 */

	public org.drip.xva.definition.CloseOutGeneral closeOut()
	{
		return _cog;
	}

	/**
	 * Retrieve the Collateralized Credit Exposure at the Path Vertex Time Node
	 * 
	 * @return The Collateralized Credit Exposure at the Path Vertex Time Node
	 * 
	 * @throws java.lang.Exception Thrown if the Exposure cannot be calculated
	 */

	public double collateralizedCreditExposure()
		throws java.lang.Exception
	{
		double dblForwardPV = forwardPV();

		return dblForwardPV + realizedCashFlow() - _cog.counterPartyDefault (0, new double[] {dblForwardPV},
			new double[] {collateralBalance()});
	}

	/**
	 * Retrieve the Uncollateralized Credit Exposure at the Path Vertex Time Node
	 * 
	 * @return The Uncollateralized Credit Exposure at the Path Vertex Time Node
	 * 
	 * @throws java.lang.Exception Thrown if the Exposure cannot be calculated
	 */

	public double uncollateralizedCreditExposure()
		throws java.lang.Exception
	{
		double dblForwardPV = forwardPV();

		return dblForwardPV + realizedCashFlow() - _cog.counterPartyDefault (0, new double[]
			{dblForwardPV});
	}

	/**
	 * Retrieve the Collateralized Debt Exposure at the Path Vertex Time Node
	 * 
	 * @return The Collateralized Debt Exposure at the Path Vertex Time Node
	 * 
	 * @throws java.lang.Exception Thrown if the Exposure cannot be calculated
	 */

	public double collateralizedDebtExposure()
		throws java.lang.Exception
	{
		double dblForwardPV = forwardPV();

		return dblForwardPV + realizedCashFlow() - _cog.bankDefaultGross (new double[] {dblForwardPV}, new
			double[] {collateralBalance()});
	}

	/**
	 * Retrieve the Uncollateralized Debt Exposure at the Path Vertex Time Node
	 * 
	 * @return The Uncollateralized Debt Exposure at the Path Vertex Time Node
	 * 
	 * @throws java.lang.Exception Thrown if the Exposure cannot be calculated
	 */

	public double uncollateralizedDebtExposure()
		throws java.lang.Exception
	{
		double dblForwardPV = forwardPV();

		return dblForwardPV + realizedCashFlow() - _cog.bankDefaultGross (new double[] {dblForwardPV});
	}
}
