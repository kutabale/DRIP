
package org.drip.execution.dynamics;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * TradingEnhancedVolatilityParameters uses the Linear Temporary Drift/Volatility Impact Functions specified
 *  for the Trading-Enhanced Risk Scenario in Almgren (2003). The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 97-102.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TradingEnhancedVolatilityParameters extends
	org.drip.execution.dynamics.ArithmeticPriceEvolutionParameters {

	/**
	 * TradingEnhancedVolatilityParameters Constructor
	 * 
	 * @param dblPriceVolatility The Daily Price Volatility Parameter
	 * @param bprlTemporaryExpectation The Background Participation Linear Temporary Market Impact
	 * 	Expectation Function
	 * @param bprlTemporaryVolatility The Background Participation Linear Temporary Market Impact
	 * 	Volatility Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TradingEnhancedVolatilityParameters (
		final double dblPriceVolatility,
		final org.drip.execution.profiletime.BackgroundParticipationRateLinear bprlTemporaryExpectation,
		final org.drip.execution.profiletime.BackgroundParticipationRateLinear bprlTemporaryVolatility)
		throws java.lang.Exception
	{
		super (new org.drip.execution.parameters.ArithmeticPriceDynamicsSettings (0., new
			org.drip.function.r1tor1.FlatUnivariate (dblPriceVolatility), 0.), new
				org.drip.execution.profiletime.UniformParticipationRate
					(org.drip.execution.impact.ParticipationRateLinear.NoImpact()), bprlTemporaryExpectation,
					 new org.drip.execution.profiletime.UniformParticipationRate
						(org.drip.execution.impact.ParticipationRateLinear.NoImpact()),
							bprlTemporaryVolatility);
	}

	/**
	 * Retrieve the Linear Temporary Expectation Function
	 * 
	 * @return The Linear Temporary Expectation Function
	 */

	public org.drip.execution.impact.TransactionFunctionLinear linearTemporaryExpectation()
	{
		return (org.drip.execution.impact.TransactionFunctionLinear)
			temporaryExpectation().epochImpactFunction();
	}

	/**
	 * Retrieve the Background Participation Linear Temporary Market Impact Volatility Function
	 * 
	 * @return The Background Participation Linear Temporary Market Impact Volatility Function
	 */

	public org.drip.execution.profiletime.BackgroundParticipationRateLinear linearTemporaryVolatility()
	{
		return (org.drip.execution.profiletime.BackgroundParticipationRateLinear) temporaryVolatility();
	}
}
