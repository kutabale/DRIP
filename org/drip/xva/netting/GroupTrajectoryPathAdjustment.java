
package org.drip.xva.netting;

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
 * GroupTrajectoryPathAdjustment holds the Adjustments from the Exposure Sequence in a Single Path Projection
 *  Run along the Granularity of a Netting Group. The References are:
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

public class GroupTrajectoryPathAdjustment {
	private double[] _adblExposure = null;
	private double[] _adblExposurePV = null;
	private double[] _adblNegativeExposure = null;
	private double[] _adblPositiveExposure = null;
	private double[] _adblNegativeExposurePV = null;
	private double[] _adblPositiveExposurePV = null;
	private org.drip.analytics.date.JulianDate[] _adtVertex = null;

	/**
	 * GroupTrajectoryPathAdjustment Constructor
	 * 
	 * @param adtVertex Array of Vertex Dates
	 * @param adblExposure The Array of Exposures
	 * @param adblExposurePV The Array of Exposure PVs
	 * @param adblPositiveExposure The Array of Positive Exposures
	 * @param adblPositiveExposurePV The Array of Positive Exposure PVs
	 * @param adblNegativeExposure The Array of Negative Exposures
	 * @param adblNegativeExposurePV The Array of Negative Exposure PVs
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public GroupTrajectoryPathAdjustment (
		final org.drip.analytics.date.JulianDate[] adtVertex,
		final double[] adblExposure,
		final double[] adblExposurePV,
		final double[] adblPositiveExposure,
		final double[] adblPositiveExposurePV,
		final double[] adblNegativeExposure,
		final double[] adblNegativeExposurePV)
		throws java.lang.Exception
	{
		if (null == (_adtVertex = adtVertex) || null == (_adblExposure = adblExposure) || null ==
			(_adblExposurePV = adblExposurePV) || null == (_adblPositiveExposure = adblPositiveExposure) ||
				null == (_adblPositiveExposurePV = adblPositiveExposurePV) || null == (_adblNegativeExposure
					= adblNegativeExposure) || null == (_adblNegativeExposurePV = adblNegativeExposurePV))
			throw new java.lang.Exception ("GroupTrajectoryPathAdjustment Constructor => Invalid Inputs");

		int iNumEdge = _adtVertex.length;

		if (0 == iNumEdge || iNumEdge != _adblExposure.length || iNumEdge != _adblExposurePV.length ||
			iNumEdge != _adblPositiveExposure.length || iNumEdge != _adblPositiveExposurePV.length ||
				iNumEdge != _adblNegativeExposurePV.length || iNumEdge != _adblNegativeExposurePV.length ||
					!org.drip.quant.common.NumberUtil.IsValid (_adblExposure) ||
						!org.drip.quant.common.NumberUtil.IsValid (_adblExposurePV) ||
							!org.drip.quant.common.NumberUtil.IsValid (_adblPositiveExposure) ||
								!org.drip.quant.common.NumberUtil.IsValid (_adblPositiveExposurePV) ||
									!org.drip.quant.common.NumberUtil.IsValid (_adblNegativeExposure) ||
										!org.drip.quant.common.NumberUtil.IsValid (_adblNegativeExposurePV))
			throw new java.lang.Exception ("GroupTrajectoryPathAdjustment Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Array of Vertex Dates
	 * 
	 * @return The Array of Vertex Dates
	 */

	public org.drip.analytics.date.JulianDate[] vertex()
	{
		return _adtVertex;
	}

	/**
	 * Retrieve the Array of Exposures
	 * 
	 * @return The Array of Exposures
	 */

	public double[] exposure()
	{
		return _adblExposure;
	}

	/**
	 * Retrieve the Array of Exposure PVs
	 * 
	 * @return The Array of Exposure PVs
	 */

	public double[] exposurePV()
	{
		return _adblExposurePV;
	}

	/**
	 * Retrieve the Array of Positive Exposures
	 * 
	 * @return The Array of Positive Exposures
	 */

	public double[] positiveExposure()
	{
		return _adblPositiveExposure;
	}

	/**
	 * Retrieve the Array of Positive Exposure PVs
	 * 
	 * @return The Array of Positive Exposure PVs
	 */

	public double[] positiveExposurePV()
	{
		return _adblPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Negative Exposures
	 * 
	 * @return The Array of Negative Exposures
	 */

	public double[] negativeExposure()
	{
		return _adblNegativeExposure;
	}

	/**
	 * Retrieve the Array of Negative Exposure PVs
	 * 
	 * @return The Array of Negative Exposure PVs
	 */

	public double[] negativeExposurePV()
	{
		return _adblNegativeExposurePV;
	}
}
