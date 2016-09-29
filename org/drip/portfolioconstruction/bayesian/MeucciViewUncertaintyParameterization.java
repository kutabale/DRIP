
package org.drip.portfolioconstruction.bayesian;

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
 * MeucciViewUncertaintyParameterization demonstrates the Meucci Parameterization for the View Projection
 * 	Uncertainty Matrix. The Reference is:
 *  
 *  - Meucci, A. (2005): Risk and Asset Allocation, Springer Finance.
 *
 * @author Lakshmi Krishnamurthy
 */

public class MeucciViewUncertaintyParameterization {

	/**
	 * Generate the Projection Co-variance from the Scoping Co-variance and the Meucci Alpha Parameter
	 * 
	 * @param aadblScopingCovariance The Scoping Co-variance
	 * @param dblAlpha Meucci Alpha Parameter
	 * 
	 * @return The Projection Co-variance Instance
	 */

	public static final org.drip.measure.gaussian.Covariance ProjectionCovariance (
		final double[][] aadblScopingCovariance,
		final double dblAlpha)
	{
		if (null == aadblScopingCovariance || !org.drip.quant.common.NumberUtil.IsValid (dblAlpha))
			return null;

		int iNumScopingEntity = aadblScopingCovariance.length;
		double[][] aadblProjectionCovariance = 0 == iNumScopingEntity ? null : new
			double[iNumScopingEntity][iNumScopingEntity];

		if (0 == iNumScopingEntity) return null;

		for (int i = 0; i < iNumScopingEntity; ++i) {
			if (null == aadblScopingCovariance[i] || iNumScopingEntity != aadblScopingCovariance[i].length)
				return null;

			for (int j = 0; j < iNumScopingEntity; ++j) {
				if (!org.drip.quant.common.NumberUtil.IsValid (aadblScopingCovariance[i][j])) return null;

				aadblProjectionCovariance[i][j] = dblAlpha * aadblScopingCovariance[i][j];
			}
		}

		try {
			return new org.drip.measure.gaussian.Covariance (aadblProjectionCovariance);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
