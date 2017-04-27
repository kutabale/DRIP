
package org.drip.xva.numeraire;

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
 * EntityMarketVertex holds the Vertex Market Numeraire Realizations at a Trajectory Vertex of the given
 *  Entity (i.e., Bank/Counter Party). The References are:
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

public class EntityMarketVertex {
	private double _dblHazard = java.lang.Double.NaN;
	private double _dblRecovery = java.lang.Double.NaN;
	private double _dblSurvival = java.lang.Double.NaN;
	private double _dblFundingSpread = java.lang.Double.NaN;

	/**
	 * EntityMarketVertex Constructor
	 * 
	 * @param dblSurvival The Realized Bank Survival Numeraire
	 * @param dblRecovery The Realized Bank Recovery Numeraire
	 * @param dblHazard The Realized Bank Hazard Numeraire
	 * @param dblFundingSpread The RealizedBank Funding Spread Numeraire
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public EntityMarketVertex (
		final double dblSurvival,
		final double dblRecovery,
		final double dblHazard,
		final double dblFundingSpread)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblSurvival = dblSurvival) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblRecovery = dblRecovery) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblHazard = dblHazard) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblFundingSpread = dblFundingSpread))
			throw new java.lang.Exception ("EntityMarketVertex Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Realized Bank Hazard Numeraire
	 * 
	 * @return The Realized Bank Hazard Numeraire
	 */

	public double hazard()
	{
		return _dblHazard;
	}

	/**
	 * Retrieve the Realized Bank Survival Numeraire
	 * 
	 * @return The Realized Bank Survival Numeraire
	 */

	public double survival()
	{
		return _dblSurvival;
	}

	/**
	 * Retrieve the Realized Bank Recovery Numeraire
	 * 
	 * @return The Realized Bank Recovery Numeraire
	 */

	public double recovery()
	{
		return _dblRecovery;
	}

	/**
	 * Retrieve the Realized Bank Funding Spread
	 * 
	 * @return The Realized Bank Funding Spread
	 */

	public double fundingSpread()
	{
		return _dblFundingSpread;
	}
}
