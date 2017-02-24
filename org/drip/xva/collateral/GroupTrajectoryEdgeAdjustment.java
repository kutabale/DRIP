
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
 * GroupTrajectoryEdgeAdjustment holds the XVA Adjustment that result from the Vertex Realizations of a
 *  Projected Path of a Single Simulation Run along the Granularity of a Collateral Group. The References
 *  are:
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

public class GroupTrajectoryEdgeAdjustment {
	private double _dblDebt = java.lang.Double.NaN;
	private double _dblCredit = java.lang.Double.NaN;
	private double _dblFunding = java.lang.Double.NaN;
	private double _dblBankRecovery = java.lang.Double.NaN;
	private double _dblCollateralBalance = java.lang.Double.NaN;
	private double _dblCounterPartyRecovery = java.lang.Double.NaN;
	private double _dblCollateralizedExposure = java.lang.Double.NaN;
	private double _dblUncollateralizedExposure = java.lang.Double.NaN;
	private double _dblCollateralizedNegativeExposure = java.lang.Double.NaN;
	private double _dblCollateralizedPositiveExposure = java.lang.Double.NaN;
	private double _dblUncollateralizedNegativeExposure = java.lang.Double.NaN;
	private double _dblUncollateralizedPositiveExposure = java.lang.Double.NaN;
	private double _dblCollateralizedNegativeExposurePV = java.lang.Double.NaN;
	private double _dblCollateralizedPositiveExposurePV = java.lang.Double.NaN;
	private double _dblUncollateralizedNegativeExposurePV = java.lang.Double.NaN;
	private double _dblUncollateralizedPositiveExposurePV = java.lang.Double.NaN;

	/**
	 * GroupTrajectoryEdgeAdjustment Constructor
	 * 
	 * @param dblCredit The Path-specific Credit Value Adjustment
	 * @param dblDebt The Path-specific Debt Value Adjustment
	 * @param dblFunding The Path-specific Funding Value Adjustment
	 * @param dblCollateralizedExposure The Path-specific Collateralized Exposure
	 * @param dblUncollateralizedExposure The Path-specific Uncollateralized Exposure
	 * @param dblCollateralizedPositiveExposure The Path-specific Collateralized Positive Exposure
	 * @param dblCollateralizedNegativeExposure The Path-specific Collateralized Negative Exposure
	 * @param dblCollateralizedPositiveExposurePV The Path-specific Collateralized Positive Exposure PV
	 * @param dblCollateralizedNegativeExposurePV The Path-specific Collateralized Negative Exposure PV
	 * @param dblUncollateralizedPositiveExposure The Path-specific Uncollateralized Positive Exposure
	 * @param dblUncollateralizedNegativeExposure The Path-specific Uncollateralized Negative Exposure
	 * @param dblUncollateralizedPositiveExposurePV The Path-specific Uncollateralized Positive Exposure PV
	 * @param dblUncollateralizedNegativeExposurePV The Path-specific Uncollateralized Negative Exposure PV
	 * @param dblCollateralBalance The Path-specific Collateral Balance
	 * @param dblBankRecovery The Path-specific Bank Recovery
	 * @param dblCounterPartyRecovery The Path-specific Counter Party Recovery
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public GroupTrajectoryEdgeAdjustment (
		final double dblCredit,
		final double dblDebt,
		final double dblFunding,
		final double dblCollateralizedExposure,
		final double dblUncollateralizedExposure,
		final double dblCollateralizedPositiveExposure,
		final double dblCollateralizedNegativeExposure,
		final double dblCollateralizedPositiveExposurePV,
		final double dblCollateralizedNegativeExposurePV,
		final double dblUncollateralizedPositiveExposure,
		final double dblUncollateralizedNegativeExposure,
		final double dblUncollateralizedPositiveExposurePV,
		final double dblUncollateralizedNegativeExposurePV,
		final double dblCollateralBalance,
		final double dblBankRecovery,
		final double dblCounterPartyRecovery)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblCredit = dblCredit) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblDebt = dblDebt) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblFunding = dblFunding) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralizedExposure = dblCollateralizedExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblUncollateralizedExposure = dblUncollateralizedExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralizedPositiveExposure = dblCollateralizedPositiveExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralizedNegativeExposure = dblCollateralizedNegativeExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralizedPositiveExposurePV = dblCollateralizedPositiveExposurePV) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralizedNegativeExposurePV = dblCollateralizedNegativeExposurePV) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblUncollateralizedPositiveExposure = dblUncollateralizedPositiveExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblUncollateralizedNegativeExposure = dblUncollateralizedNegativeExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblUncollateralizedPositiveExposurePV = dblUncollateralizedPositiveExposurePV) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblUncollateralizedNegativeExposurePV = dblUncollateralizedNegativeExposurePV) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCollateralBalance = dblCollateralBalance) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblBankRecovery = dblBankRecovery) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCounterPartyRecovery = dblCounterPartyRecovery))
			throw new java.lang.Exception ("GroupTrajectoryEdgeAdjustment Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Path-specific Credit Adjustment
	 * 
	 * @return The Path-specific Credit Adjustment
	 */

	public double credit()
	{
		return _dblCredit;
	}

	/**
	 * Retrieve the Path-specific Debt Adjustment
	 * 
	 * @return The Path-specific Debt Adjustment
	 */

	public double debt()
	{
		return _dblDebt;
	}

	/**
	 * Retrieve the Path-specific Funding Adjustment
	 * 
	 * @return The Path-specific Funding Adjustment
	 */

	public double funding()
	{
		return _dblFunding;
	}

	/**
	 * Retrieve the Path-specific Collateralized Exposure
	 * 
	 * @return The Path-specific Collateralized Exposure
	 */

	public double collateralizedExposure()
	{
		return _dblCollateralizedExposure;
	}

	/**
	 * Retrieve the Path-specific Uncollateralized Exposure
	 * 
	 * @return The Path-specific Uncollateralized Exposure
	 */

	public double uncollateralizedExposure()
	{
		return _dblUncollateralizedExposure;
	}

	/**
	 * Retrieve the Path-specific Collateralized Negative Exposure
	 * 
	 * @return The Path-specific Collateralized Negative Exposure
	 */

	public double collateralizedNegativeExposure()
	{
		return _dblCollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Path-specific Uncollateralized Negative Exposure
	 * 
	 * @return The Path-specific Uncollateralized Negative Exposure
	 */

	public double uncollateralizedNegativeExposure()
	{
		return _dblUncollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Path-specific Collateralized Positive Exposure
	 * 
	 * @return The Path-specific Collateralized Positive Exposure
	 */

	public double collateralizedPositiveExposure()
	{
		return _dblCollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Path-specific Uncollateralized Positive Exposure
	 * 
	 * @return The Path-specific Uncollateralized Positive Exposure
	 */

	public double uncollateralizedPositiveExposure()
	{
		return _dblUncollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Path-specific Collateralized Negative Exposure PV
	 * 
	 * @return The Path-specific Collateralized Negative Exposure PV
	 */

	public double collateralizedNegativeExposurePV()
	{
		return _dblCollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Path-specific Uncollateralized Negative Exposure PV
	 * 
	 * @return The Path-specific Uncollateralized Negative Exposure PV
	 */

	public double uncollateralizedNegativeExposurePV()
	{
		return _dblUncollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Path-specific Collateralized Positive Exposure PV
	 * 
	 * @return The Path-specific Collateralized Positive Exposure PV
	 */

	public double collateralizedPositiveExposurePV()
	{
		return _dblCollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Path-specific Uncollateralized Positive Exposure PV
	 * 
	 * @return The Path-specific Uncollateralized Positive Exposure PV
	 */

	public double uncollateralizedPositiveExposurePV()
	{
		return _dblUncollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Path-specific Bank Recovery
	 * 
	 * @return The Path-specific Bank Recovery
	 */

	public double bankRecovery()
	{
		return _dblBankRecovery;
	}

	/**
	 * Retrieve the Path-specific Counter Party Recovery
	 * 
	 * @return The Path-specific Counter Party Recovery
	 */

	public double counterPartyRecovery()
	{
		return _dblCounterPartyRecovery;
	}

	/**
	 * Retrieve the Path-specific Collateral Balance
	 * 
	 * @return The Path-specific Collateral Balance
	 */

	public double collateralBalance()
	{
		return _dblCollateralBalance;
	}
}
