
package org.drip.xva.trajectory;

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
 * NettingGroupPath rolls up the Path Realizations of the Sequence in a Single Path Projection Run over
 *  Multiple Collateral Groups onto a Single Netting Group. The References are:
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

public class NettingGroupPath {
	private org.drip.xva.trajectory.NumerairePath _np = null;
	private org.drip.xva.trajectory.CollateralGroupPath[] _aCGP = null;

	/**
	 * Generate a "Mono" CollateralGroupDigest Instance
	 * 
	 * @param cgp The "Mono" Collateral Group Path
	 * @param np The Numeraire Path
	 * 
	 * @return The "Mono" CollateralGroupDigest Instance
	 */

	public static final NettingGroupPath Mono (
		final org.drip.xva.trajectory.CollateralGroupPath cgp,
		final org.drip.xva.trajectory.NumerairePath np)
	{
		try {
			return new org.drip.xva.trajectory.NettingGroupPath (new
				org.drip.xva.trajectory.CollateralGroupPath[] {cgp}, np);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * NettingGroupPath Constructor
	 * 
	 * @param aCGP Array of the Collateral Group Trajectory Paths
	 * @param np The Numeraire Path
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NettingGroupPath (
		final org.drip.xva.trajectory.CollateralGroupPath[] aCGP,
		final org.drip.xva.trajectory.NumerairePath np)
		throws java.lang.Exception
	{
		if (null == (_aCGP = aCGP) || 0 == _aCGP.length || null == (_np = np))
			throw new java.lang.Exception ("NettingGroupPath Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Array of the Collateral Group Trajectory Paths
	 * 
	 * @return Array of the Collateral Group Trajectory Paths
	 */

	public org.drip.xva.trajectory.CollateralGroupPath[] collateralGroupPaths()
	{
		return _aCGP;
	}

	/**
	 * Retrieve the Numeraire Paths
	 * 
	 * @return The Numeraire Paths
	 */

	public org.drip.xva.trajectory.NumerairePath numerairePath()
	{
		return _np;
	}

	/**
	 * Retrieve the Array of the Vertex Anchor Dates
	 * 
	 * @return The Array of the Vertex Anchor Dates
	 */

	public org.drip.analytics.date.JulianDate[] anchors()
	{
		return _aCGP[0].anchors();
	}

	/**
	 * Retrieve the Array of Collateralized Exposures
	 * 
	 * @return The Array of Collateralized Exposures
	 */

	public double[] collateralizedExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblCollateralizedExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedExposure[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].collateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedExposure[iVertexIndex] +=
					adblCollateralGroupCollateralizedExposure[iVertexIndex];
		}

		return adblCollateralizedExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Exposure PV
	 * 
	 * @return The Array of Collateralized Exposure PV
	 */

	public double[] collateralizedExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblCollateralizedExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedExposurePV[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].collateralizedExposure();

			org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedExposurePV[iVertexIndex] +=
					adblCollateralGroupCollateralizedExposure[iVertexIndex] / aNV[iVertexIndex].csa();
		}

		return adblCollateralizedExposurePV;
	}

	/**
	 * Retrieve the Array of Collateralized Positive Exposures
	 * 
	 * @return The Array of Collateralized Positive Exposures
	 */

	public double[] collateralizedPositiveExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblCollateralizedPositiveExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedPositiveExposure[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].collateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblCollateralizedExposure = adblCollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. < dblCollateralizedExposure)
					adblCollateralizedPositiveExposure[iVertexIndex] += dblCollateralizedExposure;
			}
		}

		return adblCollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Positive Exposure PV
	 * 
	 * @return The Array of Collateralized Positive Exposures PV
	 */

	public double[] collateralizedPositiveExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblCollateralizedPositiveExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedPositiveExposurePV[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].collateralizedExposure();

			org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblCollateralizedExposure = adblCollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. < dblCollateralizedExposure)
					adblCollateralizedPositiveExposurePV[iVertexIndex] += dblCollateralizedExposure /
						aNV[iVertexIndex].csa();
			}
		}

		return adblCollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Collateralized Negative Exposures
	 * 
	 * @return The Array of Collateralized Negative Exposures
	 */

	public double[] collateralizedNegativeExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblCollateralizedNegativeExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedNegativeExposure[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].collateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblCollateralizedExposure = adblCollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. > dblCollateralizedExposure)
					adblCollateralizedNegativeExposure[iVertexIndex] += dblCollateralizedExposure;
			}
		}

		return adblCollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Negative Exposure PV
	 * 
	 * @return The Array of Collateralized Negative Exposure PV
	 */

	public double[] collateralizedNegativeExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblCollateralizedNegativeExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblCollateralizedNegativeExposurePV[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].collateralizedExposure();

			org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblCollateralizedExposure = adblCollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. > dblCollateralizedExposure)
					adblCollateralizedNegativeExposurePV[iVertexIndex] += dblCollateralizedExposure /
						aNV[iVertexIndex].csa();
			}
		}

		return adblCollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Exposures
	 * 
	 * @return The Array of Uncollateralized Exposures
	 */

	public double[] uncollateralizedExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblUncollateralizedExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblUncollateralizedExposure[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupUncollateralizedExposure =
				_aCGP[iCollateralGroupIndex].uncollateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedExposure[iVertexIndex] +=
					adblCollateralGroupUncollateralizedExposure[iVertexIndex];
		}

		return adblUncollateralizedExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Exposure PV
	 * 
	 * @return The Array of Uncollateralized Exposure PV
	 */

	public double[] uncollateralizedExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblUncollateralizedExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblUncollateralizedExposurePV[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupUncollateralizedExposure =
				_aCGP[iCollateralGroupIndex].uncollateralizedExposure();

			org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedExposurePV[iVertexIndex] +=
					adblCollateralGroupUncollateralizedExposure[iVertexIndex] / aNV[iVertexIndex].csa();
		}

		return adblUncollateralizedExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Positive Exposures
	 * 
	 * @return The Array of Uncollateralized Positive Exposures
	 */

	public double[] uncollateralizedPositiveExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblUncollateralizedPositiveExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblUncollateralizedPositiveExposure[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblUncollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].uncollateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblUncollateralizedExposure =
					adblUncollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. < dblUncollateralizedExposure)
					adblUncollateralizedPositiveExposure[iVertexIndex] += dblUncollateralizedExposure;
			}
		}

		return adblUncollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Positive Exposure PV
	 * 
	 * @return The Array of Uncollateralized Positive Exposure PV
	 */

	public double[] uncollateralizedPositiveExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblUncollateralizedPositiveExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblUncollateralizedPositiveExposurePV[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblUncollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].uncollateralizedExposure();

			org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblUncollateralizedExposure =
					adblUncollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. < dblUncollateralizedExposure)
					adblUncollateralizedPositiveExposurePV[iVertexIndex] += dblUncollateralizedExposure /
						aNV[iVertexIndex].csa();
			}
		}

		return adblUncollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Negative Exposures
	 * 
	 * @return The Array of Uncollateralized Negative Exposures
	 */

	public double[] uncollateralizedNegativeExposure()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblUncollateralizedNegativeExposure = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblUncollateralizedNegativeExposure[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblCollateralGroupUncollateralizedExposure =
				_aCGP[iCollateralGroupIndex].uncollateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblUncollateralizedExposure =
					adblCollateralGroupUncollateralizedExposure[iVertexIndex];

				if (0. > dblUncollateralizedExposure)
					adblUncollateralizedNegativeExposure[iVertexIndex] += dblUncollateralizedExposure;
			}
		}

		return adblUncollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Negative Exposure PV
	 * 
	 * @return The Array of Uncollateralized Negative Exposure PV
	 */

	public double[] uncollateralizedNegativeExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumCollateralGroup = _aCGP.length;
		double[] adblUncollateralizedNegativeExposurePV = new double[iNumVertex];

		for (int j = 0; j < iNumVertex; ++j)
			adblUncollateralizedNegativeExposurePV[j] = 0.;

		for (int iCollateralGroupIndex = 0; iCollateralGroupIndex < iNumCollateralGroup;
			++iCollateralGroupIndex) {
			double[] adblUncollateralGroupCollateralizedExposure =
				_aCGP[iCollateralGroupIndex].uncollateralizedExposure();

			org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				double dblUncollateralizedExposure =
					adblUncollateralGroupCollateralizedExposure[iVertexIndex];

				if (0. > dblUncollateralizedExposure)
					adblUncollateralizedNegativeExposurePV[iVertexIndex] += dblUncollateralizedExposure /
						aNV[iVertexIndex].csa();
			}
		}

		return adblUncollateralizedNegativeExposurePV;
	}

	/**
	 * Compute Path Unilateral Credit Adjustment
	 * 
	 * @return The Path Unilateral Credit Adjustment
	 */

	public double unilateralCreditAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblUnilateralCreditAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartLoss = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery());

			double dblPeriodEndLoss = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery());

			dblUnilateralCreditAdjustment -= 0.5 * (dblPeriodStartLoss + dblPeriodEndLoss) *
				(aNV[iVertexIndex - 1].counterPartySurvival() - aNV[iVertexIndex].counterPartySurvival());
		}

		return dblUnilateralCreditAdjustment;
	}

	/**
	 * Compute Path Bilateral Credit Adjustment
	 * 
	 * @return The Path Bilateral Credit Adjustment
	 */

	public double bilateralCreditAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblBilateralCreditAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartLoss = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery()) * aNV[iVertexIndex - 1].bankSurvival();

			double dblPeriodEndLoss = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery()) * aNV[iVertexIndex].bankSurvival();

			dblBilateralCreditAdjustment -= 0.5 * (dblPeriodStartLoss + dblPeriodEndLoss) *
				(aNV[iVertexIndex - 1].counterPartySurvival() - aNV[iVertexIndex].counterPartySurvival());
		}

		return dblBilateralCreditAdjustment;
	}

	/**
	 * Compute Path Credit Adjustment
	 * 
	 * @return The Path Credit Adjustment
	 */

	public double creditAdjustment()
	{
		return bilateralCreditAdjustment();
	}

	/**
	 * Compute Period-wise Path Unilateral Credit Adjustment
	 * 
	 * @return Period-wise Path Unilateral Credit Adjustment
	 */

	public double[] periodUnilateralCreditAdjustment()
	{
		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = aNV.length;
		double[] adblPeriodUnilateralCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartLoss = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery());

			double dblPeriodEndLoss = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery());

			adblPeriodUnilateralCreditAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodStartLoss +
				dblPeriodEndLoss) * (aNV[iVertexIndex - 1].counterPartySurvival() -
					aNV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodUnilateralCreditAdjustment;
	}

	/**
	 * Compute Period-wise Path Bilateral Credit Adjustment
	 * 
	 * @return Period-wise Path Bilateral Credit Adjustment
	 */

	public double[] periodBilateralCreditAdjustment()
	{
		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = aNV.length;
		double[] adblPeriodBilateralCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartLoss = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery()) * aNV[iVertexIndex - 1].bankSurvival();

			double dblPeriodEndLoss = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery()) * aNV[iVertexIndex].bankSurvival();

			adblPeriodBilateralCreditAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodStartLoss +
				dblPeriodEndLoss) * (aNV[iVertexIndex - 1].counterPartySurvival() -
					aNV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodBilateralCreditAdjustment;
	}

	/**
	 * Compute Period-wise Path Credit Adjustment
	 * 
	 * @return Period-wise Path Credit Adjustment
	 */

	public double[] periodCreditAdjustment()
	{
		return periodBilateralCreditAdjustment();
	}

	/**
	 * Compute Path Debt Adjustment
	 * 
	 * @return The Path Debt Adjustment
	 */

	public double debtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double dblDebtAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartLoss = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].bankRecovery());

			double dblPeriodEndLoss = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].bankRecovery());

			dblDebtAdjustment -= 0.5 * (dblPeriodStartLoss + dblPeriodEndLoss) *
				(aNV[iVertexIndex - 1].bankSurvival() - aNV[iVertexIndex].bankSurvival());
		}

		return dblDebtAdjustment;
	}

	/**
	 * Compute Period-wise Path Debt Adjustment
	 * 
	 * @return Period-wise Path Debt Adjustment
	 */

	public double[] periodDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = aNV.length;
		double[] adblDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartLoss = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].bankRecovery());

			double dblPeriodEndLoss = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].bankRecovery());

			adblDebtAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodStartLoss + dblPeriodEndLoss) *
				(aNV[iVertexIndex - 1].bankSurvival() - aNV[iVertexIndex].bankSurvival());
		}

		return adblDebtAdjustment;
	}

	/**
	 * Compute Path Funding Adjustment
	 * 
	 * @return The Path Funding Adjustment
	 */

	public double fundingAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblFundingAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartNotionalSpread = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodEndNotionalSpread = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankFundingSpread();

			dblFundingAdjustment -= 0.5 * (dblPeriodStartNotionalSpread + dblPeriodEndNotionalSpread) *
				(aNV[iVertexIndex].anchor().julian() - aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblFundingAdjustment;
	}

	/**
	 * Compute Period-wise Path Funding Adjustment
	 * 
	 * @return The Period-wise Path Funding Adjustment
	 */

	public double[] periodFundingAdjustment()
	{
		org.drip.xva.trajectory.NumeraireVertex[] aNV = _np.vertexes();

		int iNumVertex = aNV.length;
		double[] adblFundingAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodStartNotionalSpread = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodEndNotionalSpread = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankFundingSpread();

			adblFundingAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodStartNotionalSpread +
				dblPeriodEndNotionalSpread) * (aNV[iVertexIndex].anchor().julian() -
					aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblFundingAdjustment;
	}
}
