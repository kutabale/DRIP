
package org.drip.xva.bundle;

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
 * CollateralGroup specific the Details of a Collateral Group. The References are:
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

public class CollateralGroup {
	private java.lang.String _strName = "";
	private double _dblBankThreshold = java.lang.Double.NaN;
	private double _dblCounterPartyThreshold = java.lang.Double.NaN;
	private double _dblMinimumTransferAmount = java.lang.Double.NaN;
	private double _dblSpotCollateralBalance = java.lang.Double.NaN;

	/**
	 * Generate an "Uncollateralized" Instance of the Named Collateral Group
	 * 
	 * @param strName The Collateral Group Name
	 * 
	 * @return The "Uncollateralized" Instance of the Named Collateral Group
	 */

	public static final CollateralGroup Uncollateralized (
		final java.lang.String strName)
	{
		try {
			return new CollateralGroup (strName, 0., 0., 0., 0.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * CollateralGroup Constructor
	 * 
	 * @param strName The Collateral Group Name
	 * @param dblCounterPartyThreshold The Collateral Group Counter Party Threshold
	 * @param dblBankThreshold The Collateral Group Bank Threshold
	 * @param dblMinimumTransferAmount The Collateral Group Minimum Transfer Amount
	 * @param dblSpotCollateralBalance The Spot Collateral Balance in the Collateral Group
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CollateralGroup (
		final java.lang.String strName,
		final double dblCounterPartyThreshold,
		final double dblBankThreshold,
		final double dblMinimumTransferAmount,
		final double dblSpotCollateralBalance)
		throws java.lang.Exception
	{
		if (null == (_strName = strName) || _strName.isEmpty() || !org.drip.quant.common.NumberUtil.IsValid
			(_dblCounterPartyThreshold = dblCounterPartyThreshold) || 0. > _dblCounterPartyThreshold ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblBankThreshold = dblBankThreshold) || 0. >
					_dblBankThreshold || !org.drip.quant.common.NumberUtil.IsValid (_dblMinimumTransferAmount
						= dblMinimumTransferAmount) || !org.drip.quant.common.NumberUtil.IsValid
							(_dblSpotCollateralBalance = dblSpotCollateralBalance))
			throw new java.lang.Exception ("CollateralGroup Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Collateral Group Name
	 * 
	 * @return The Collateral Group Name
	 */

	public java.lang.String name()
	{
		return _strName;
	}

	/**
	 * Retrieve the Collateral Group Counter Party Threshold
	 * 
	 * @return The Collateral Group Counter Party Threshold
	 */

	public double counterPartyThreshold()
	{
		return _dblCounterPartyThreshold;
	}

	/**
	 * Retrieve the Collateral Group Bank Threshold
	 * 
	 * @return The Collateral Group Bank Threshold
	 */

	public double bankThreshold()
	{
		return _dblBankThreshold;
	}

	/**
	 * Retrieve the Collateral Group Minimum Transfer Amount
	 * 
	 * @return The Collateral Group Minimum Transfer Amount
	 */

	public double minimumTransferAmount()
	{
		return _dblMinimumTransferAmount;
	}

	/**
	 * Retrieve the Spot Collateral Balance in the Collateral Group
	 * 
	 * @return The Spot Collateral Balance in the Collateral Group
	 */

	public double spotCollateralBalance()
	{
		return _dblSpotCollateralBalance;
	}

	/**
	 * Retrieve the Flag specifying whether the Collateral Group is Uncollateralized
	 * 
	 * @return TRUE - The COllateral Group is Uncollateralized
	 */

	public boolean isUncollateralized()
	{
		return 0. == _dblCounterPartyThreshold && 0. == _dblBankThreshold;
	}
}
