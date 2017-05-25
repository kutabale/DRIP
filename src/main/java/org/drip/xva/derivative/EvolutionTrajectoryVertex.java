
package org.drip.xva.derivative;

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
 * EvolutionTrajectoryVertex holds the Evolution Snapshot of the Trade-able Prices, the Cash Account, the
 *  Replication Portfolio, and the corresponding Derivative Value, as laid out in Burgard and Kjaer (2014).
 *   The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Cesari, G., J. Aquilina, N. Charpillon, X. Filipovic, G. Lee, and L. Manda (2009): Modeling, Pricing,
 *  	and Hedging Counter-party Credit Exposure - A Technical Guide, Springer Finance, New York.
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

public class EvolutionTrajectoryVertex {
	private double _dblTime = java.lang.Double.NaN;
	private double[] _adblBankGainOnCounterPartyDefault = null;
	private double[] _adblCounterPartyGainOnBankDefault = null;
	private org.drip.xva.derivative.AssetGreekVertex _agv = null;
	private org.drip.xva.derivative.ReplicationPortfolioVertex _rpv = null;
	private org.drip.xva.universe.TradeablesVertex _tcvm = null;

	/**
	 * EvolutionTrajectoryVertex Constructor
	 * 
	 * @param dblTime The Evolution Trajectory Edge Time
	 * @param tcvm Realization of the Trade-able Asset Prices
	 * @param rpv The Replication Portfolio Vertex
	 * @param agv The Asset Greek Vertex
	 * @param adblCounterPartyGainOnBankDefault Array of Individual Counter Party Gain On Bank Default
	 * @param adblBankGainOnCounterPartyDefault Array of Bank Gain On Default of Individual Counter Party
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public EvolutionTrajectoryVertex (
		final double dblTime,
		final org.drip.xva.universe.TradeablesVertex tcvm,
		final org.drip.xva.derivative.ReplicationPortfolioVertex rpv,
		final org.drip.xva.derivative.AssetGreekVertex agv,
		final double[] adblCounterPartyGainOnBankDefault,
		final double[] adblBankGainOnCounterPartyDefault)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblTime = dblTime) || null == (_tcvm = tcvm) || null
			== (_rpv = rpv) || null == (_agv = agv) || null == (_adblCounterPartyGainOnBankDefault =
				adblCounterPartyGainOnBankDefault) || null == (_adblBankGainOnCounterPartyDefault =
					adblBankGainOnCounterPartyDefault))
			throw new java.lang.Exception ("EvolutionTrajectoryVertex Constructor => Invalid Inputs");

		int iNumCounterPartyGroup = _adblBankGainOnCounterPartyDefault.length;

		if (0 >= iNumCounterPartyGroup || iNumCounterPartyGroup != _adblCounterPartyGainOnBankDefault.length)
			throw new java.lang.Exception ("EvolutionTrajectoryVertex Constructor => Invalid Inputs");

		for (int i = 0; i < iNumCounterPartyGroup; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblBankGainOnCounterPartyDefault[i]) ||
				!org.drip.quant.common.NumberUtil.IsValid (_adblCounterPartyGainOnBankDefault[i]))
				throw new java.lang.Exception ("EvolutionTrajectoryVertex Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Time Instant
	 * 
	 * @return The Time Instant
	 */

	public double time()
	{
		return _dblTime;
	}

	/**
	 * Retrieve the Realization of the Trade-able Asset Prices
	 * 
	 * @return Realization of the Trade-able Asset Prices
	 */

	public org.drip.xva.universe.TradeablesVertex tradeableAssetSnapshot()
	{
		return _tcvm;
	}

	/**
	 * Retrieve the Replication Portfolio Vertex
	 * 
	 * @return The Replication Portfolio Vertex
	 */

	public org.drip.xva.derivative.ReplicationPortfolioVertex replicationPortfolioVertex()
	{
		return _rpv;
	}

	/**
	 * Retrieve the Asset Greek Vertex
	 * 
	 * @return The Asset Greek Vertex
	 */

	public org.drip.xva.derivative.AssetGreekVertex assetGreekVertex()
	{
		return _agv;
	}

	/**
	 * Retrieve the Array of Individual Counter Party Gain On Bank Default
	 * 
	 * @return The Array of Individual Counter Party Gain On Bank Default
	 */

	public double[] gainOnBankDefault()
	{
		return _adblCounterPartyGainOnBankDefault;
	}

	/**
	 * Retrieve the Gross Counter Party Gain On Bank Default
	 * 
	 * @return The Gross Counter Party Gain On Bank Default
	 */

	public double grossGainOnBankDefault()
	{
		double dblGrossGainOnBankDefault = 0.;
		int iNumCounterPartyGroup = _adblBankGainOnCounterPartyDefault.length;

		for (int i = 0; i < iNumCounterPartyGroup; ++i)
			dblGrossGainOnBankDefault += _adblBankGainOnCounterPartyDefault[i];

		return dblGrossGainOnBankDefault;
	}

	/**
	 * Retrieve the Array of Bank Gains On Individual Counter Party Default
	 * 
	 * @return The Array of Bank Gains On Individual Counter Party Default
	 */

	public double[] gainOnCounterPartyDefault()
	{
		return _adblBankGainOnCounterPartyDefault;
	}
}
