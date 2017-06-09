
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
 * MarketVertexGenerator generates the Market Realizations at a Trajectory Vertex needed for computing the
 *  Valuation Adjustment. The References are:
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

public class MarketVertexGenerator {
	private int[] _aiDate = null;
	private int _iVertexCount = -1;
	private double[][] _aadblCorrelationMatrix = null;
	private org.drip.measure.process.DiffusionEvolver _deAsset = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSeniorRecoveryRate = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSeniorFundingSpread = null;
	private org.drip.measure.process.DiffusionEvolver _deOvernightIndexNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deCollateralSchemeNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSeniorFundingNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyFundingNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSubordinateFundingNumeraire = null;

	/**
	 * Retrieve the Number of Vertexes to be generated
	 * 
	 * @return The Number of Vertexes to be generated
	 */

	public int vertexCount()
	{
		return _iVertexCount;
	}

	/**
	 * Retrieve the Vertex Date Array
	 * 
	 * @return The Vertex Date Array
	 */

	public int[] vertexDates()
	{
		return _aiDate;
	}

	/**
	 * Retrieve the Latent State Correlation Matrix
	 * 
	 * @return The Latent State Correlation Matrix
	 */

	public double[][] correlationMatrix()
	{
		return _aadblCorrelationMatrix;
	}

	/**
	 * Retrieve the Asset Evolver
	 * 
	 * @return The Asset Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver assetEvolver()
	{
		return _deAsset;
	}

	/**
	 * Retrieve the Overnight Index Numeraire Evolver
	 * 
	 * @return The Overnight Index Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver overnightIndexNumeraireEvolver()
	{
		return _deOvernightIndexNumeraire;
	}

	/**
	 * Retrieve the Collateral Scheme Numeraire Evolver
	 * 
	 * @return The Collateral Scheme Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver collateralSchemeNumeraireEvolver()
	{
		return _deCollateralSchemeNumeraire;
	}

	/**
	 * Retrieve the Bank Senior Recovery Rate Evolver
	 * 
	 * @return The Bank Senior Recovery Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSeniorRecoveryRateEvolver()
	{
		return _deBankSeniorRecoveryRate;
	}

	/**
	 * Retrieve the Bank Senior Funding Spread Evolver
	 * 
	 * @return The Bank Senior Funding Spread Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSeniorFundingSpreadEvolver()
	{
		return _deBankSeniorFundingSpread;
	}

	/**
	 * Retrieve the Bank Senior Funding Numeraire Evolver
	 * 
	 * @return The Bank Senior Funding Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSeniorFundingNumeraireEvolver()
	{
		return _deBankSeniorFundingNumeraire;
	}

	/**
	 * Retrieve the Bank Subordinate Funding Numeraire Evolver
	 * 
	 * @return The Bank Subordinate Funding Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSubordinateFundingNumeraireEvolver()
	{
		return _deBankSubordinateFundingNumeraire;
	}

	/**
	 * Retrieve the Counter Party Funding Numeraire Evolver
	 * 
	 * @return The Counter Party Funding Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver counterPartyFundingNumeraireEvolver()
	{
		return _deCounterPartyFundingNumeraire;
	}
}
