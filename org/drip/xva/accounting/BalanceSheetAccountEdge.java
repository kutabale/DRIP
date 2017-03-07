
package org.drip.xva.accounting;

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
 * BalanceSheetAccountVertex implements the Balance Sheet Edge Component of the Streamlined Accounting
 *  Framework for OTC Derivatives, as described in Albanese and Andersen (2014). The References are:
 *  
 *  - Albanese, C., and L. Andersen (2014): Accounting for OTC Derivatives: Funding Adjustments and the
 *  	Re-Hypothecation Option, https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2482955, eSSRN.
 *  
 *  - BCBS (2012): Consultative Document: Application of Own Credit Risk Adjustments to Derivatives, Basel
 *  	Committee on Banking Supervision.
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class BalanceSheetAccountEdge {
	private org.drip.xva.accounting.BalanceSheetAccountVertex _bsavHead = null;
	private org.drip.xva.accounting.BalanceSheetAccountVertex _bsavTail = null;

	/**
	 * BalanceSheetAccountEdge Constructor
	 * 
	 * @param bsavHead Balance Sheet Account Vertex Head Instance
	 * @param bsavTail Balance Sheet Account Vertex Tail Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BalanceSheetAccountEdge (
		final org.drip.xva.accounting.BalanceSheetAccountVertex bsavHead,
		final org.drip.xva.accounting.BalanceSheetAccountVertex bsavTail)
		throws java.lang.Exception
	{
		if (null == (_bsavHead = bsavHead) || null == (_bsavTail = bsavTail))
			throw new java.lang.Exception ("BalanceSheetAccountEdge Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Balance Sheet Account Vertex Head Instance
	 * 
	 * @return The Balance Sheet Account Vertex Head Instance
	 */

	public org.drip.xva.accounting.BalanceSheetAccountVertex head()
	{
		return _bsavHead;
	}

	/**
	 * Retrieve the Balance Sheet Account Vertex Tail Instance
	 * 
	 * @return The Balance Sheet Account Vertex Tail Instance
	 */

	public org.drip.xva.accounting.BalanceSheetAccountVertex tail()
	{
		return _bsavTail;
	}

	/**
	 * Compute the CET1 Change
	 * 
	 * @return The CET1 Change
	 */

	public double cet1Change()
	{
		return _bsavTail.retainedEarnings() - _bsavTail.contraAsset() - _bsavHead.retainedEarnings() +
			_bsavHead.contraAsset();
	}

	/**
	 * Compute the "Income"
	 * 
	 * @return The "Income"
	 */

	public double income()
	{
		return _bsavTail.retainedEarnings() + _bsavTail.contraLiability() - _bsavTail.contraAsset() -
			_bsavHead.retainedEarnings() + _bsavHead.contraAsset() - _bsavHead.contraLiability();
	}
}
