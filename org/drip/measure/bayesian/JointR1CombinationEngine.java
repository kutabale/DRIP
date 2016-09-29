
package org.drip.measure.bayesian;

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
 * JointR1CombinationEngine implements the Engine that generates the Combined/Posterior Distributions from
 *  the Prior and the Conditional Joint Multivariate R^1 Distributions.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface JointR1CombinationEngine {

	/**
	 * Generate the Joint R^1 Multivariate Combined Distribution
	 * 
	 * @param rmPrior The Prior Distribution
	 * @param rmUnconditional The Unconditional Distribution
	 * @param rmConditional The Conditional Distribution
	 * 
	 * @return The Joint R^1 Multivariate Combined Distribution
	 */

	public abstract org.drip.measure.bayesian.JointPosteriorMetrics process (
		final org.drip.measure.continuousjoint.R1Multivariate rmPrior,
		final org.drip.measure.continuousjoint.R1Multivariate rmUnconditional,
		final org.drip.measure.continuousjoint.R1Multivariate rmConditional);
}
