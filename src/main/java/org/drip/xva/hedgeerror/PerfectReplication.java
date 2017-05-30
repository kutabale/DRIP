
package org.drip.xva.hedgeerror;

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
 * PerfectReplication corresponds to the case of no Hedge Error based Evolution under the Burgard Kjaer
 * 	(2011, 2013) Methodology. This results in zero FCA. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2011): PDE Representations of Derivatives with Bilateral Counter Party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2011): In the Balance, Risk, 22 (11) 72-75.
 *  
 *  - Burgard, C., and M. Kjaer (2013): Funding Costs and Funding Strategies, Risk, 24 (12) 82-87.
 *  
 *  - Hull, J., and A. White (2012): The FVA Debate, Risk, 23 (7) 83-85.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PerfectReplication extends org.drip.xva.pde.BurgardKjaerOperator {

	protected double hedgeError (
		final double dblBankRecovery,
		final double dblBankDefaultBoundaryCondition,
		final double dblDerivativeFairValue,
		final double dblDerivativeXVAValue,
		final double dblCollateral)
	{
		return 0.;
	}

	/**
	 * PerfectReplication Constructor
	 * 
	 * @param tc The Universe of Tradeable Assets
	 * @param cob The Master Agreement Close Out Boundary Conditions
	 * @param pdeec The XVA Control Settings
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public PerfectReplication (
		final org.drip.xva.universe.TradeablesContainer tc,
		final org.drip.xva.definition.CloseOutBilateral cob,
		final org.drip.xva.definition.PDEEvolutionControl pdeec)
		throws java.lang.Exception
	{
		super (tc, cob, pdeec);
	}
}
