
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
 * GroupTrajectoryAggregator aggregates across Multiple Path Projection Runs along the Granularity of a
 *  Netting Group. The References are:
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

public class GroupTrajectoryAggregator {
	private org.drip.xva.netting.GroupTrajectoryPath[] _aGTP = null;

	/**
	 * GroupTrajectoryAggregator Constructor
	 * 
	 * @param aGTP The Array of the Group Trajectory Paths
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public GroupTrajectoryAggregator (
		final org.drip.xva.netting.GroupTrajectoryPath[] aGTP)
		throws java.lang.Exception
	{
		if (null == (_aGTP = aGTP) || 0 == _aGTP.length)
			throw new java.lang.Exception ("GroupTrajectoryAggregator Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Array of the Group Trajectory Paths
	 * 
	 * @return The Array of the Group Trajectory Paths
	 */

	public org.drip.xva.netting.GroupTrajectoryPath[] paths()
	{
		return _aGTP;
	}

	/**
	 * Retrieve the Expected CVA
	 * 
	 * @return The Expected CVA
	 */

	public double cva()
	{
		double dblCVASum = 0.;
		int iNumPath = _aGTP.length;

		for (int i = 0; i < iNumPath; ++i)
			dblCVASum += _aGTP[i].credit();

		return dblCVASum / iNumPath;
	}

	/**
	 * Retrieve the Expected DVA
	 * 
	 * @return The Expected DVA
	 */

	public double dva()
	{
		double dblDVASum = 0.;
		int iNumPath = _aGTP.length;

		for (int i = 0; i < iNumPath; ++i)
			dblDVASum += _aGTP[i].debt();

		return dblDVASum / iNumPath;
	}

	/**
	 * Retrieve the Expected FVA
	 * 
	 * @return The Expected FVA
	 */

	public double fva()
	{
		double dblFVASum = 0.;
		int iNumPath = _aGTP.length;

		for (int i = 0; i < iNumPath; ++i)
			dblFVASum += _aGTP[i].funding();

		return dblFVASum / iNumPath;
	}

	/**
	 * Retrieve the Array of Expected Exposures
	 * 
	 * @return The Array of Expected Exposures
	 */

	public double[] expectedExposure()
	{
		int iNumEdge = _aGTP[0].edges().length;

		int iNumPath = _aGTP.length;
		double[] adblExpectedExposure = new double[iNumEdge];

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedExposure[j] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblExposure = _aGTP[iPathIndex].exposure();

			for (int iEdgeIndex = 0; iEdgeIndex < iNumEdge; ++iEdgeIndex)
				adblExpectedExposure[iEdgeIndex] += adblExposure[iEdgeIndex];
		}

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedExposure[j] /= iNumPath;

		return adblExpectedExposure;
	}

	/**
	 * Retrieve the Array of Expected Exposure PV's
	 * 
	 * @return The Array of Expected Exposure PV's
	 */

	public double[] expectedExposurePV()
	{
		int iNumEdge = _aGTP[0].edges().length;

		int iNumPath = _aGTP.length;
		double[] adblExpectedExposurePV = new double[iNumEdge];

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedExposurePV[j] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblExposurePV = _aGTP[iPathIndex].exposurePV();

			for (int iEdgeIndex = 0; iEdgeIndex < iNumEdge; ++iEdgeIndex)
				adblExpectedExposurePV[iEdgeIndex] += adblExposurePV[iEdgeIndex];
		}

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedExposurePV[j] /= iNumPath;

		return adblExpectedExposurePV;
	}

	/**
	 * Retrieve the Array of Expected Positive Exposures
	 * 
	 * @return The Array of Expected Positive Exposures
	 */

	public double[] expectedPositiveExposure()
	{
		int iNumEdge = _aGTP[0].edges().length;

		int iNumPath = _aGTP.length;
		double[] adblExpectedPositiveExposure = new double[iNumEdge];

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedPositiveExposure[j] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPositiveExposure = _aGTP[iPathIndex].positiveExposure();

			for (int iEdgeIndex = 0; iEdgeIndex < iNumEdge; ++iEdgeIndex)
				adblExpectedPositiveExposure[iEdgeIndex] += adblPositiveExposure[iEdgeIndex];
		}

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedPositiveExposure[j] /= iNumPath;

		return adblExpectedPositiveExposure;
	}

	/**
	 * Retrieve the Array of Expected Negative Exposures
	 * 
	 * @return The Array of Expected Negative Exposures
	 */

	public double[] expectedNegativeExposure()
	{
		int iNumEdge = _aGTP[0].edges().length;

		int iNumPath = _aGTP.length;
		double[] adblExpectedNegativeExposure = new double[iNumEdge];

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedNegativeExposure[j] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblNegativeExposure = _aGTP[iPathIndex].negativeExposure();

			for (int iEdgeIndex = 0; iEdgeIndex < iNumEdge; ++iEdgeIndex)
				adblExpectedNegativeExposure[iEdgeIndex] += adblNegativeExposure[iEdgeIndex];
		}

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedNegativeExposure[j] /= iNumPath;

		return adblExpectedNegativeExposure;
	}

	/**
	 * Retrieve the Array of Expected Positive Exposure PVs
	 * 
	 * @return The Array of Expected Positive Exposure PVs
	 */

	public double[] expectedPositiveExposurePV()
	{
		int iNumEdge = _aGTP[0].edges().length;

		int iNumPath = _aGTP.length;
		double[] adblExpectedPositiveExposurePV = new double[iNumEdge];

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedPositiveExposurePV[j] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPositiveExposurePV = _aGTP[iPathIndex].positiveExposurePV();

			for (int iEdgeIndex = 0; iEdgeIndex < iNumEdge; ++iEdgeIndex)
				adblExpectedPositiveExposurePV[iEdgeIndex] += adblPositiveExposurePV[iEdgeIndex];
		}

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedPositiveExposurePV[j] /= iNumPath;

		return adblExpectedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Expected Negative Exposure PVs
	 * 
	 * @return The Array of Expected Negative Exposure PVs
	 */

	public double[] expectedNegativeExposurePV()
	{
		int iNumEdge = _aGTP[0].edges().length;

		int iNumPath = _aGTP.length;
		double[] adblExpectedNegativeExposurePV = new double[iNumEdge];

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedNegativeExposurePV[j] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblNegativeExposurePV = _aGTP[iPathIndex].negativeExposurePV();

			for (int iEdgeIndex = 0; iEdgeIndex < iNumEdge; ++iEdgeIndex)
				adblExpectedNegativeExposurePV[iEdgeIndex] += adblNegativeExposurePV[iEdgeIndex];
		}

		for (int j = 0; j < iNumEdge; ++j)
			adblExpectedNegativeExposurePV[j] /= iNumPath;

		return adblExpectedNegativeExposurePV;
	}
}
