
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
 * GroupTrajectoryPath accumulates the Vertex Realizations of the Sequence in a Single Path Projection Run
 *  along the Granularity of a Netting Group. The References are:
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

public class GroupTrajectoryPath {
	private org.drip.xva.netting.GroupTrajectoryEdge[] _aGTE = null;

	/**
	 * GroupTrajectoryPath Constructor
	 * 
	 * @param aGTE The Array of Netting Group Trajectory Edges
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public GroupTrajectoryPath (
		final org.drip.xva.netting.GroupTrajectoryEdge[] aGTE)
		throws java.lang.Exception
	{
		if (null == (_aGTE = aGTE))
			throw new java.lang.Exception ("GroupTrajectoryPath Constructor => Invalid Inputs");

		int iNumEdge = _aGTE.length;

		if (1 >= iNumEdge)
			throw new java.lang.Exception ("GroupTrajectoryPath Constructor => Invalid Inputs");

		for (int i = 0; i < iNumEdge; ++i) {
			if (null == _aGTE[i])
				throw new java.lang.Exception ("GroupTrajectoryPath Constructor => Invalid Inputs");

			if (0 != i && _aGTE[i - 1].tail().vertex().julian() != _aGTE[i].head().vertex().julian())
				throw new java.lang.Exception ("GroupTrajectoryPath Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Array of Netting Group Trajectory Edges
	 * 
	 * @return The Array of Netting Group Trajectory Edges
	 */

	public org.drip.xva.netting.GroupTrajectoryEdge[] edges()
	{
		return _aGTE;
	}

	/**
	 * Retrieve the Path-wise Credit Adjustment
	 * 
	 * @return The Path-wise Credit Adjustment
	 */

	public double credit()
	{
		double dblCredit = 0.;

		for (org.drip.xva.netting.GroupTrajectoryEdge gte : _aGTE)
			dblCredit += gte.credit();

		return dblCredit;
	}

	/**
	 * Retrieve the Path-wise Debt Adjustment
	 * 
	 * @return The Path-wise Debt Adjustment
	 */

	public double debt()
	{
		double dblDebt = 0.;

		for (org.drip.xva.netting.GroupTrajectoryEdge gte : _aGTE)
			dblDebt += gte.debt();

		return dblDebt;
	}

	/**
	 * Retrieve the Path-wise Funding Adjustment
	 * 
	 * @return The Path-wise Funding Adjustment
	 */

	public double funding()
	{
		double dblFunding = 0.;

		for (org.drip.xva.netting.GroupTrajectoryEdge gte : _aGTE)
			dblFunding += gte.funding();

		return dblFunding;
	}

	/**
	 * Retrieve the Array of Exposures
	 * 
	 * @return The Array of Exposures
	 */

	public double[] exposure()
	{
		int iNumEdge = _aGTE.length;
		double[] adblExposure = new double[iNumEdge];

		for (int i = 0; i < iNumEdge; ++i)
			adblExposure[i] += _aGTE[i].tail().exposure().gross();

		return adblExposure;
	}

	/**
	 * Retrieve the Array of Positive Exposures
	 * 
	 * @return The Array of Positive Exposures
	 */

	public double[] positiveExposure()
	{
		int iNumEdge = _aGTE.length;
		double[] adblPositiveExposure = new double[iNumEdge];

		for (int i = 0; i < iNumEdge; ++i)
			adblPositiveExposure[i] += _aGTE[i].tail().exposure().positive();

		return adblPositiveExposure;
	}

	/**
	 * Retrieve the Array of Positive Exposure PV's
	 * 
	 * @return The Array of Positive Exposure PV's
	 */

	public double[] positiveExposurePV()
	{
		int iNumEdge = _aGTE.length;
		double[] adblPositiveExposurePV = new double[iNumEdge];

		for (int i = 0; i < iNumEdge; ++i) {
			org.drip.xva.netting.GroupTrajectoryVertex gtvTail =_aGTE[i].tail() ;

			adblPositiveExposurePV[i] += gtvTail.exposure().positive() * gtvTail.numeraire().collateral();
		}

		return adblPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Negative Exposures
	 * 
	 * @return The Array of Negative Exposures
	 */

	public double[] negativeExposure()
	{
		int iNumEdge = _aGTE.length;
		double[] adblNegativeExposure = new double[iNumEdge];

		for (int i = 0; i < iNumEdge; ++i)
			adblNegativeExposure[i] += _aGTE[i].tail().exposure().negative();

		return adblNegativeExposure;
	}

	/**
	 * Retrieve the Array of Negative Exposure PV's
	 * 
	 * @return The Array of Negative Exposure PV's
	 */

	public double[] negativeExposurePV()
	{
		int iNumEdge = _aGTE.length;
		double[] adblNegativeExposurePV = new double[iNumEdge];

		for (int i = 0; i < iNumEdge; ++i) {
			org.drip.xva.netting.GroupTrajectoryVertex gtvTail =_aGTE[i].tail() ;

			adblNegativeExposurePV[i] += gtvTail.exposure().negative() * gtvTail.numeraire().collateral();
		}

		return adblNegativeExposurePV;
	}
}
