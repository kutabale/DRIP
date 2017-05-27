
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
 * ReplicationPortfolioVertex contains the Dynamic Replicating Portfolio of the Pay-out using the Assets in
 *  the Economy, from the Bank's View Point. The References are:
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

public class ReplicationPortfolioVertex {
	private double _dblCashAccount = java.lang.Double.NaN;
	private double[] _adblCounterPartyNumeraireUnits = null;
	private double _dblBankNumeraireUnits = java.lang.Double.NaN;
	private double _dblAssetNumeraireUnits = java.lang.Double.NaN;
	private double _dblZeroRecoveryBankNumeraireUnits = java.lang.Double.NaN;

	/**
	 * Construct a ReplicationPortfolioVertex Instance without the Zero Recovery Bank Numeraire
	 * 
	 * @param dblAssetNumeraireUnits The Asset Numeraire Units
	 * @param dblBankNumeraireUnits The Bank Numeraire Units
	 * @param adblCounterPartyNumeraireUnits The Array of Counter Party Numeraire Replication Units
	 * @param dblCashAccount The Cash Account
	 * 
	 * @return The ReplicationPortfolioVertex Instance without the Zero Recovery Bank Numeraire
	 */

	public static final ReplicationPortfolioVertex Standard (
		final double dblAssetNumeraireUnits,
		final double dblBankNumeraireUnits,
		final double[] adblCounterPartyNumeraireUnits,
		final double dblCashAccount)
	{
		try {
			return new ReplicationPortfolioVertex (dblAssetNumeraireUnits, dblBankNumeraireUnits, 0.,
				adblCounterPartyNumeraireUnits, dblCashAccount);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ReplicationPortfolioVertex Constructor
	 * 
	 * @param dblAssetNumeraireUnits The Asset Numeraire Units
	 * @param dblBankNumeraireUnits The Bank Numeraire Units
	 * @param dblZeroRecoveryBankNumeraireUnits The Zero Recovery Bank Numeraire Units
	 * @param adblCounterPartyNumeraireUnits The Array of Counter Party Numeraire Units
	 * @param dblCashAccount The Cash Account
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ReplicationPortfolioVertex (
		final double dblAssetNumeraireUnits,
		final double dblBankNumeraireUnits,
		final double dblZeroRecoveryBankNumeraireUnits,
		final double[] adblCounterPartyNumeraireUnits,
		final double dblCashAccount)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblAssetNumeraireUnits = dblAssetNumeraireUnits) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblBankNumeraireUnits = dblBankNumeraireUnits) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblZeroRecoveryBankNumeraireUnits =
					dblZeroRecoveryBankNumeraireUnits) || !org.drip.quant.common.NumberUtil.IsValid
						(_adblCounterPartyNumeraireUnits = adblCounterPartyNumeraireUnits) || 0 >=
							_adblCounterPartyNumeraireUnits.length ||
								!org.drip.quant.common.NumberUtil.IsValid (_dblCashAccount =
									dblCashAccount))
			throw new java.lang.Exception ("ReplicationPortfolioVertex Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Number of Asset Numeraire Units
	 * 
	 * @return The Number of Asset Numeraire Units
	 */

	public double assetNumeraireUnits()
	{
		return _dblAssetNumeraireUnits;
	}

	/**
	 * Retrieve the Number of Bank Numeraire Units
	 * 
	 * @return The Number of Bank Numeraire Units
	 */

	public double bankNumeraireUnits()
	{
		return _dblBankNumeraireUnits;
	}

	/**
	 * Retrieve the Number of Zero Recovery Bank Numeraire Units
	 * 
	 * @return The Number of Zero Recovery Bank Numeraire Units
	 */

	public double zeroRecoveryBankNumeraireUnits()
	{
		return _dblZeroRecoveryBankNumeraireUnits;
	}

	/**
	 * Retrieve the Array of Counter Party Numeraire Units
	 * 
	 * @return The Array of Counter Party Numeraire Units
	 */

	public double[] counterPartyNumeraireUnits()
	{
		return _adblCounterPartyNumeraireUnits;
	}

	/**
	 * Retrieve the Cash Account Amount
	 * 
	 * @return The Cash Account Amount
	 */

	public double cashAccount()
	{
		return _dblCashAccount;
	}

	/**
	 * Compute the Market Value of the Portfolio
	 * 
	 * @param tv The Trade-able Asset Market Snapshot
	 * 
	 * @return The Market Value of the Portfolio
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double value (
		final org.drip.xva.universe.TradeablesVertex tv)
		throws java.lang.Exception
	{
		if (null == tv)
			throw new java.lang.Exception ("ReplicationPortfolioVertex::value => Invalid Inputs");

		double dblValue = -1. * _dblAssetNumeraireUnits * tv.assetNumeraire().finish() - _dblCashAccount;

		int iNumCounterParty = _adblCounterPartyNumeraireUnits.length;

		org.drip.measure.realization.JumpDiffusionEdge[] aJDECounterParty =
			tv.counterPartyFundingNumeraire();

		for (int i = 0; i < iNumCounterParty; ++i)
			dblValue -= _adblCounterPartyNumeraireUnits[i] * aJDECounterParty[i].finish();

		org.drip.measure.realization.JumpDiffusionEdge jdeBankFunding = tv.bankFundingNumeraire();

		if (null != jdeBankFunding) dblValue -= jdeBankFunding.finish() * _dblBankNumeraireUnits;

		org.drip.measure.realization.JumpDiffusionEdge jdeZeroRecoveryBankFunding =
			tv.zeroRecoveryBankFundingNumeraire();

		if (null != jdeZeroRecoveryBankFunding)
			dblValue -= jdeZeroRecoveryBankFunding.finish() * _dblZeroRecoveryBankNumeraireUnits;

		return dblValue;
	}
}
