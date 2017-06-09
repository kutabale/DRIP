
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
 * EntityMarketVertex holds the Realizations at a Market Trajectory Vertex of the given Entity (i.e.,
 *  Bank/Counter Party). The References are:
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
	private double _dblHazardRate = java.lang.Double.NaN;
	private double _dblSeniorRecoveryRate = java.lang.Double.NaN;
	private double _dblSeniorFundingSpread = java.lang.Double.NaN;
	private double _dblSurvivalProbability = java.lang.Double.NaN;
	private double _dblSeniorFundingNumeraire = java.lang.Double.NaN;
	private double _dblSubordinateRecoveryRate = java.lang.Double.NaN;
	private double _dblSubordinateFundingSpread = java.lang.Double.NaN;
	private double _dblSubordinateFundingNumeraire = java.lang.Double.NaN;

	/**
	 * EntityMarketVertex Constructor
	 * 
	 * @param dblSurvivalProbability The Realized Entity Survival Probability
	 * @param dblHazardRate The Realized Entity Hazard Rate
	 * @param dblSeniorRecoveryRate The Entity Senior Recovery Rate
	 * @param dblSeniorFundingSpread The Entity Senior Funding Spread
	 * @param dblSeniorFundingNumeraire The Entity Senior Funding Numeraire
	 * @param dblSubordinateRecoveryRate The Entity Subordinate Recovery Rate
	 * @param dblSubordinateFundingSpread The Entity Subordinate Funding Spread
	 * @param dblSubordinateFundingNumeraire The Entity Subordinate Funding Numeraire
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public EntityMarketVertex (
		final double dblSurvivalProbability,
		final double dblHazardRate,
		final double dblSeniorRecoveryRate,
		final double dblSeniorFundingSpread,
		final double dblSeniorFundingNumeraire,
		final double dblSubordinateRecoveryRate,
		final double dblSubordinateFundingSpread,
		final double dblSubordinateFundingNumeraire)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblSurvivalProbability = dblSurvivalProbability) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblSeniorRecoveryRate = dblSeniorRecoveryRate) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblHazardRate = dblHazardRate) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblSeniorFundingSpread =
						dblSeniorFundingSpread) || !org.drip.quant.common.NumberUtil.IsValid
							(_dblSeniorFundingNumeraire = dblSeniorFundingNumeraire))
			throw new java.lang.Exception ("EntityMarketVertex Constructor => Invalid Inputs");

		_dblSubordinateRecoveryRate = dblSubordinateRecoveryRate;
		_dblSubordinateFundingSpread = dblSubordinateFundingSpread;
		_dblSubordinateFundingNumeraire = dblSubordinateFundingNumeraire;
	}

	/**
	 * Retrieve the Realized Entity Hazard Rate
	 * 
	 * @return The Realized Entity Hazard Rate
	 */

	public double hazardRate()
	{
		return _dblHazardRate;
	}

	/**
	 * Retrieve the Realized Entity Survival Probability
	 * 
	 * @return The Realized Entity Survival Probability
	 */

	public double survivalProbability()
	{
		return _dblSurvivalProbability;
	}

	/**
	 * Retrieve the Realized Entity Senior Recovery Rate
	 * 
	 * @return The Realized Entity Senior Recovery Rate
	 */

	public double seniorRecoveryRate()
	{
		return _dblSeniorRecoveryRate;
	}

	/**
	 * Retrieve the Realized Entity Senior Funding Spread
	 * 
	 * @return The Realized Entity Senior Funding Spread
	 */

	public double seniorFundingSpread()
	{
		return _dblSeniorFundingSpread;
	}

	/**
	 * Retrieve the Realized Entity Senior Funding Numeraire
	 * 
	 * @return The Realized Entity Senior Funding Numeraire
	 */

	public double seniorFundingNumeraire()
	{
		return _dblSeniorFundingNumeraire;
	}

	/**
	 * Retrieve the Realized Entity Subordinate Recovery Rate
	 * 
	 * @return The Realized Entity Subordinate Recovery Rate
	 */

	public double subordinateRecoveryRate()
	{
		return _dblSubordinateRecoveryRate;
	}

	/**
	 * Retrieve the Realized Entity Subordinate Funding Spread
	 * 
	 * @return The Realized Entity Subordinate Funding Spread
	 */

	public double subordinateFundingSpread()
	{
		return _dblSubordinateFundingSpread;
	}

	/**
	 * Retrieve the Realized Entity Subordinate Funding Numeraire
	 * 
	 * @return The Realized Entity Subordinate Funding Numeraire
	 */

	public double subordinateFundingNumeraire()
	{
		return _dblSubordinateFundingNumeraire;
	}
}
