
package org.drip.service.scenario;

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
 * AladdinReplicationRun holds the Results of a Full Aladdin Replication Run,
 * 
 * @author Lakshmi Krishnamurthy
 */

public class AladdinReplicationRun {
	private java.util.Map<java.lang.String, org.drip.service.scenario.NamedField> _mapNF = new
		org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.service.scenario.NamedField>();

	private java.util.Map<java.lang.String, org.drip.service.scenario.NamedFieldArray> _mapNFA = new
		org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.service.scenario.NamedFieldArray>();

	/**
	 * Empty AladdinReplicationRun Constructor
	 */

	public AladdinReplicationRun()
	{
	}

	/**
	 * Add a Named Field
	 * 
	 * @param nf The Named Field
	 * 
	 * @return TRUE - The Named Field Successfully added
	 */

	public boolean addNamedField (
		final org.drip.service.scenario.NamedField nf)
	{
		if (null == nf) return false;

		_mapNF.put (nf.name(), nf);

		return true;
	}

	/**
	 * Add a Named Field Array
	 * 
	 * @param nfa The Named Field Array
	 * 
	 * @return TRUE - The Named Field Array Successfully added
	 */

	public boolean addNamedFieldArray (
		final org.drip.service.scenario.NamedFieldArray nfa)
	{
		if (null == nfa) return false;

		_mapNFA.put (nfa.name(), nfa);

		return true;
	}

	/**
	 * Retrieve the Named Field Metrics
	 * 
	 * @return The Named Field Metrics
	 */

	public java.util.Map<java.lang.String, org.drip.service.scenario.NamedField> namedField()
	{
		return _mapNF;
	}

	/**
	 * Retrieve the Named Field Array Metrics
	 * 
	 * @return The Named Field Array Metrics
	 */

	public java.util.Map<java.lang.String, org.drip.service.scenario.NamedFieldArray> namedFieldArray()
	{
		return _mapNFA;
	}
}
