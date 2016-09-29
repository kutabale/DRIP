
package org.drip.execution.evolution;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
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
 * MarketImpactComposite contains the Composite Evolution Increment Components of the Movements exhibited by
 *  an Asset's Manifest Measures owing to the Stochastic and the Deterministic Factors. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 *
 * 	- Chan, L. K. C., and J. Lakonishak (1995): The Behavior of Stock Prices around Institutional Trades,
 * 		Journal of Finance, 50, 1147-1174.
 *
 * 	- Keim, D. B., and A. Madhavan (1997): Transaction Costs and Investment Style: An Inter-exchange
 * 		Analysis of Institutional Equity Trades, Journal of Financial Economics, 46, 265-292.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class MarketImpactComposite extends org.drip.execution.evolution.MarketImpactComponent {
	private org.drip.execution.evolution.MarketImpactComponent _micStochastic = null;
	private org.drip.execution.evolution.MarketImpactComponent _micDeterministic = null;

	/**
	 * Construct a Standard Instance of MarketImpactComposite
	 * 
	 * @param micDeterministic The Deterministic Market Impact Component Instance
	 * @param micStochastic The Stochastic Market Impact Component Instance
	 * 
	 * @return The Standard Instance of MarketImpactComposite
	 */

	public static final MarketImpactComposite Standard (
		final org.drip.execution.evolution.MarketImpactComponent micDeterministic,
		final org.drip.execution.evolution.MarketImpactComponent micStochastic)
	{
		if (null == micDeterministic || null == micStochastic) return null;

		try {
			return new MarketImpactComposite (micDeterministic, micStochastic);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	protected MarketImpactComposite (
		final org.drip.execution.evolution.MarketImpactComponent micDeterministic,
		final org.drip.execution.evolution.MarketImpactComponent micStochastic)
		throws java.lang.Exception
	{
		super (micDeterministic.currentStepMarketCore() + micStochastic.currentStepMarketCore(),
			micDeterministic.previousStepMarketCore() + micStochastic.previousStepMarketCore(),
				micDeterministic.permanentImpact() + micStochastic.permanentImpact(),
					micDeterministic.temporaryImpact() + micStochastic.temporaryImpact());
	}

	/**
	 * Retrieve the Deterministic Impact Component Instance
	 * 
	 * @return The Deterministic Impact Component Instance
	 */

	public org.drip.execution.evolution.MarketImpactComponent deterministic()
	{
		return _micDeterministic;
	}

	/**
	 * Retrieve the Stochastic Impact Component Instance
	 * 
	 * @return The Stochastic Impact Component Instance
	 */

	public org.drip.execution.evolution.MarketImpactComponent stochastic()
	{
		return _micStochastic;
	}
}
