
package org.drip.xva.cpty;

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
 * ExposureAdjustmentAggregator aggregates across Multiple Exposure/Adjustment Paths belonging to the Counter
 *  Party. The References are:
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

public class ExposureAdjustmentAggregator {
	private org.drip.xva.cpty.PathExposureAdjustment[] _aPEA = null;

	/**
	 * ExposureAdjustmentAggregator Constructor
	 * 
	 * @param aPEA Array of the Counter Party Group Paths
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ExposureAdjustmentAggregator (
		final org.drip.xva.cpty.PathExposureAdjustment[] aPEA)
		throws java.lang.Exception
	{
		if (null == (_aPEA = aPEA) || 0 == _aPEA.length)
			throw new java.lang.Exception ("ExposureAdjustmentAggregator Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Array of Counter Party Group Paths
	 * 
	 * @return Array of Counter Party Group Paths
	 */

	public org.drip.xva.cpty.PathExposureAdjustment[] counterPartyGroups()
	{
		return _aPEA;
	}

	/**
	 * Retrieve the Array of the Vertex Anchor Dates
	 * 
	 * @return The Array of the Vertex Anchor Dates
	 */

	public org.drip.analytics.date.JulianDate[] anchors()
	{
		return _aPEA[0].anchors();
	}

	/**
	 * Retrieve the Array of Collateralized Exposures
	 * 
	 * @return The Array of Collateralized Exposures
	 */

	public double[] collateralizedExposure()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblCollateralizedExposure = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedExposure[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedExposure = _aPEA[iPathIndex].collateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedExposure[iVertexIndex] += adblPathCollateralizedExposure[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedExposure[iVertexIndex] /= iNumPath;

		return adblCollateralizedExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Exposure PV's
	 * 
	 * @return The Array of Collateralized Exposure PV's
	 */

	public double[] collateralizedExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblCollateralizedExposurePV = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedExposurePV[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedExposurePV = _aPEA[iPathIndex].collateralizedExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedExposurePV[iVertexIndex] += adblPathCollateralizedExposurePV[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedExposurePV[iVertexIndex] /= iNumPath;

		return adblCollateralizedExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Exposures
	 * 
	 * @return The Array of Uncollateralized Exposures
	 */

	public double[] uncollateralizedExposure()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblUncollateralizedExposure = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedExposure[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathUncollateralizedExposure = _aPEA[iPathIndex].uncollateralizedExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedExposure[iVertexIndex] += adblPathUncollateralizedExposure[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedExposure[iVertexIndex] /= iNumPath;

		return adblUncollateralizedExposure;
	}

	/**
	 * Retrieve the Array of Uncollateralized Exposure PV's
	 * 
	 * @return The Array of Uncollateralized Exposure PV's
	 */

	public double[] uncollateralizedExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblUncollateralizedExposurePV = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedExposurePV[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathUncollateralizedExposurePV = _aPEA[iPathIndex].uncollateralizedExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedExposurePV[iVertexIndex] +=
					adblPathUncollateralizedExposurePV[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedExposurePV[iVertexIndex] /= iNumPath;

		return adblUncollateralizedExposurePV;
	}

	/**
	 * Retrieve the Array of Collateralized Positive Exposures
	 * 
	 * @return The Array of Collateralized Positive Exposures
	 */

	public double[] collateralizedPositiveExposure()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblCollateralizedPositiveExposure = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedPositiveExposure[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedPositiveExposure =
				_aPEA[iPathIndex].collateralizedPositiveExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedPositiveExposure[iVertexIndex] +=
					adblPathCollateralizedPositiveExposure[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedPositiveExposure[iVertexIndex] /= iNumPath;

		return adblCollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Array of Collateralized Positive Exposure PV
	 * 
	 * @return The Array of Collateralized Positive Exposure PV
	 */

	public double[] collateralizedPositiveExposurePV()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblCollateralizedPositiveExposurePV = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedPositiveExposurePV[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedPositiveExposurePV =
				_aPEA[iPathIndex].collateralizedPositiveExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedPositiveExposurePV[iVertexIndex] +=
					adblPathCollateralizedPositiveExposurePV[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedPositiveExposurePV[iVertexIndex] /= iNumPath;

		return adblCollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Positive Exposures
	 * 
	 * @return The Array of Uncollateralized Positive Exposures
	 */

	public double[] uncollateralizedPositiveExposure()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblUncollateralizedPositiveExposure = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedPositiveExposure[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathUncollateralizedPositiveExposure =
				_aPEA[iPathIndex].uncollateralizedPositiveExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedPositiveExposure[iVertexIndex] +=
					adblPathUncollateralizedPositiveExposure[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedPositiveExposure[iVertexIndex] /= iNumPath;

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

		int iNumPath = _aPEA.length;
		double[] adblUncollateralizedPositiveExposurePV = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedPositiveExposurePV[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathUncollateralizedPositiveExposurePV =
				_aPEA[iPathIndex].uncollateralizedPositiveExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedPositiveExposurePV[iVertexIndex] +=
					adblPathUncollateralizedPositiveExposurePV[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedPositiveExposurePV[iVertexIndex] /= iNumPath;

		return adblUncollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Array of Collateralized Negative Exposures
	 * 
	 * @return The Array of Collateralized Negative Exposures
	 */

	public double[] collateralizedNegativeExposure()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblCollateralizedNegativeExposure = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedNegativeExposure[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedNegativeExposure =
				_aPEA[iPathIndex].collateralizedNegativeExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedNegativeExposure[iVertexIndex] +=
					adblPathCollateralizedNegativeExposure[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedNegativeExposure[iVertexIndex] /= iNumPath;

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

		int iNumPath = _aPEA.length;
		double[] adblCollateralizedNegativeExposurePV = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedNegativeExposurePV[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedNegativeExposurePV =
				_aPEA[iPathIndex].collateralizedNegativeExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblCollateralizedNegativeExposurePV[iVertexIndex] +=
					adblPathCollateralizedNegativeExposurePV[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblCollateralizedNegativeExposurePV[iVertexIndex] /= iNumPath;

		return adblCollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Array of Uncollateralized Negative Exposures
	 * 
	 * @return The Array of Uncollateralized Negative Exposures
	 */

	public double[] uncollateralizedNegativeExposure()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblUncollateralizedNegativeExposure = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedNegativeExposure[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathUncollateralizedNegativeExposure =
				_aPEA[iPathIndex].uncollateralizedNegativeExposure();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedNegativeExposure[iVertexIndex] +=
					adblPathUncollateralizedNegativeExposure[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedNegativeExposure[iVertexIndex] /= iNumPath;

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

		int iNumPath = _aPEA.length;
		double[] adblUncollateralizedNegativeExposurePV = new double[iNumVertex];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedNegativeExposurePV[iVertexIndex] = 0.;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathUncollateralizedNegativeExposurePV =
				_aPEA[iPathIndex].uncollateralizedNegativeExposurePV();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
				adblUncollateralizedNegativeExposurePV[iVertexIndex] +=
					adblPathUncollateralizedNegativeExposurePV[iVertexIndex];
		}

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex)
			adblUncollateralizedNegativeExposurePV[iVertexIndex] /= iNumPath;

		return adblUncollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Expected Unilateral CVA
	 * 
	 * @return The Expected Unilateral CVA
	 */

	public double ucva()
	{
		double dblUCVA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblUCVA += _aPEA[iPathIndex].unilateralCreditAdjustment();

		return dblUCVA / iNumPath;
	}

	/**
	 * Retrieve the Expected Bilateral/FTD CVA
	 * 
	 * @return The Expected Bilateral/FTD CVA
	 */

	public double ftdcva()
	{
		double dblFTDCVA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblFTDCVA += _aPEA[iPathIndex].bilateralCreditAdjustment();

		return dblFTDCVA / iNumPath;
	}

	/**
	 * Retrieve the Expected CVA
	 * 
	 * @return The Expected CVA
	 */

	public double cva()
	{
		return ftdcva();
	}

	/**
	 * Retrieve the Expected CVA Contra-Liability
	 * 
	 * @return The Expected CVA Contra-Liability
	 */

	public double cvacl()
	{
		double dblCVACL = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblCVACL += _aPEA[iPathIndex].contraLiabilityCreditAdjustment();

		return dblCVACL / iNumPath;
	}

	/**
	 * Retrieve the Expected DVA
	 * 
	 * @return The Expected DVA
	 */

	public double dva()
	{
		double dblDVA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblDVA += _aPEA[iPathIndex].debtAdjustment();

		return dblDVA / iNumPath;
	}

	/**
	 * Retrieve the Expected FVA
	 * 
	 * @return The Expected FVA
	 */

	public double fva()
	{
		double dblFVA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblFVA += _aPEA[iPathIndex].fundingValueAdjustment();

		return dblFVA / iNumPath;
	}

	/**
	 * Retrieve the Expected FDA
	 * 
	 * @return The Expected FDA
	 */

	public double fda()
	{
		double dblFDA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblFDA += _aPEA[iPathIndex].fundingDebtAdjustment();

		return dblFDA / iNumPath;
	}

	/**
	 * Retrieve the Expected FCA
	 * 
	 * @return The Expected FCA
	 */

	public double fca()
	{
		double dblFCA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblFCA += _aPEA[iPathIndex].fundingCostAdjustment();

		return dblFCA / iNumPath;
	}

	/**
	 * Retrieve the Expected FBA
	 * 
	 * @return The Expected FBA
	 */

	public double fba()
	{
		double dblFBA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblFBA += _aPEA[iPathIndex].fundingBenefitAdjustment();

		return dblFBA / iNumPath;
	}

	/**
	 * Retrieve the Expected SFVA
	 * 
	 * @return The Expected SFVA
	 */

	public double sfva()
	{
		double dblSFVA = 0.;
		int iNumPath = _aPEA.length;

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex)
			dblSFVA += _aPEA[iPathIndex].symmetricFundingValueAdjustment();

		return dblSFVA / iNumPath;
	}

	/**
	 * Retrieve the Total VA
	 * 
	 * @return The Total VA
	 */

	public double total()
	{
		return cva() + dva() + fva();
	}

	/**
	 * Generate the "Digest" containing the "Thin" Path Statistics
	 * 
	 * @return The "Digest" containing the "Thin" Path Statistics
	 */

	public org.drip.xva.cpty.ExposureAdjustmentDigest digest()
	{
		int iNumVertex = anchors().length;

		int iNumPath = _aPEA.length;
		double[] adblCVA = new double[iNumPath];
		double[] adblDVA = new double[iNumPath];
		double[] adblFBA = new double[iNumPath];
		double[] adblFCA = new double[iNumPath];
		double[] adblFDA = new double[iNumPath];
		double[] adblFVA = new double[iNumPath];
		double[] adblUCVA = new double[iNumPath];
		double[] adblSFVA = new double[iNumPath];
		double[] adblCVACL = new double[iNumPath];
		double[] adblFTDCVA = new double[iNumPath];
		double[] adblTotalVA = new double[iNumPath];
		double[][] aadblCollateralizedExposure = new double[iNumVertex][iNumPath];
		double[][] aadblUncollateralizedExposure = new double[iNumVertex][iNumPath];
		double[][] aadblCollateralizedExposurePV = new double[iNumVertex][iNumPath];
		double[][] aadblUncollateralizedExposurePV = new double[iNumVertex][iNumPath];
		double[][] aadblCollateralizedPositiveExposure = new double[iNumVertex][iNumPath];
		double[][] aadblCollateralizedNegativeExposure = new double[iNumVertex][iNumPath];
		double[][] aadblUncollateralizedPositiveExposure = new double[iNumVertex][iNumPath];
		double[][] aadblUncollateralizedNegativeExposure = new double[iNumVertex][iNumPath];
		double[][] aadblCollateralizedPositiveExposurePV = new double[iNumVertex][iNumPath];
		double[][] aadblCollateralizedNegativeExposurePV = new double[iNumVertex][iNumPath];
		double[][] aadblUncollateralizedPositiveExposurePV = new double[iNumVertex][iNumPath];
		double[][] aadblUncollateralizedNegativeExposurePV = new double[iNumVertex][iNumPath];

		for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
			for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
				aadblCollateralizedExposure[iVertexIndex][iPathIndex] = 0.;
				aadblUncollateralizedExposure[iVertexIndex][iPathIndex] = 0.;
				aadblCollateralizedExposurePV[iVertexIndex][iPathIndex] = 0.;
				aadblUncollateralizedExposurePV[iVertexIndex][iPathIndex] = 0.;
				aadblCollateralizedPositiveExposure[iVertexIndex][iPathIndex] = 0.;
				aadblCollateralizedNegativeExposure[iVertexIndex][iPathIndex] = 0.;
				aadblUncollateralizedPositiveExposure[iVertexIndex][iPathIndex] = 0.;
				aadblUncollateralizedNegativeExposure[iVertexIndex][iPathIndex] = 0.;
				aadblCollateralizedPositiveExposurePV[iVertexIndex][iPathIndex] = 0.;
				aadblCollateralizedNegativeExposurePV[iVertexIndex][iPathIndex] = 0.;
				aadblUncollateralizedPositiveExposurePV[iVertexIndex][iPathIndex] = 0.;
				aadblUncollateralizedNegativeExposurePV[iVertexIndex][iPathIndex] = 0.;
			}
		}

		for (int iPathIndex = 0; iPathIndex < iNumPath; ++iPathIndex) {
			double[] adblPathCollateralizedExposure = _aPEA[iPathIndex].collateralizedExposure();

			double[] adblPathCollateralizedExposurePV = _aPEA[iPathIndex].collateralizedExposurePV();

			double[] adblPathCollateralizedPositiveExposure =
				_aPEA[iPathIndex].collateralizedPositiveExposure();

			double[] adblPathCollateralizedPositiveExposurePV =
				_aPEA[iPathIndex].collateralizedPositiveExposurePV();

			double[] adblPathCollateralizedNegativeExposure =
				_aPEA[iPathIndex].collateralizedNegativeExposure();

			double[] adblPathCollateralizedNegativeExposurePV =
				_aPEA[iPathIndex].collateralizedNegativeExposurePV();

			double[] adblPathUncollateralizedExposure = _aPEA[iPathIndex].uncollateralizedExposure();

			double[] adblPathUncollateralizedExposurePV = _aPEA[iPathIndex].uncollateralizedExposurePV();

			double[] adblPathUncollateralizedPositiveExposure =
				_aPEA[iPathIndex].uncollateralizedPositiveExposure();

			double[] adblPathUncollateralizedPositiveExposurePV =
				_aPEA[iPathIndex].uncollateralizedPositiveExposurePV();

			double[] adblPathUncollateralizedNegativeExposure =
				_aPEA[iPathIndex].uncollateralizedNegativeExposure();

			double[] adblPathUncollateralizedNegativeExposurePV =
				_aPEA[iPathIndex].uncollateralizedNegativeExposurePV();

			adblCVA[iPathIndex] = _aPEA[iPathIndex].creditAdjustment();

			adblDVA[iPathIndex] = _aPEA[iPathIndex].debtAdjustment();

			adblFCA[iPathIndex] = _aPEA[iPathIndex].fundingCostAdjustment();

			adblFDA[iPathIndex] = _aPEA[iPathIndex].fundingDebtAdjustment();

			adblFVA[iPathIndex] = _aPEA[iPathIndex].fundingValueAdjustment();

			adblFBA[iPathIndex] = _aPEA[iPathIndex].fundingBenefitAdjustment();

			adblUCVA[iPathIndex] = _aPEA[iPathIndex].unilateralCreditAdjustment();

			adblSFVA[iPathIndex] = _aPEA[iPathIndex].symmetricFundingValueAdjustment();

			adblCVACL[iPathIndex] = _aPEA[iPathIndex].contraLiabilityCreditAdjustment();

			adblFTDCVA[iPathIndex] = _aPEA[iPathIndex].bilateralCreditAdjustment();

			adblTotalVA[iPathIndex] = _aPEA[iPathIndex].totalAdjustment();

			for (int iVertexIndex = 0; iVertexIndex < iNumVertex; ++iVertexIndex) {
				aadblCollateralizedExposure[iVertexIndex][iPathIndex] =
					adblPathCollateralizedExposure[iVertexIndex];
				aadblCollateralizedExposurePV[iVertexIndex][iPathIndex] =
					adblPathCollateralizedExposurePV[iVertexIndex];
				aadblCollateralizedPositiveExposure[iVertexIndex][iPathIndex] =
					adblPathCollateralizedPositiveExposure[iVertexIndex];
				aadblCollateralizedPositiveExposurePV[iVertexIndex][iPathIndex] =
					adblPathCollateralizedPositiveExposurePV[iVertexIndex];
				aadblCollateralizedNegativeExposure[iVertexIndex][iPathIndex] =
					adblPathCollateralizedNegativeExposure[iVertexIndex];
				aadblCollateralizedNegativeExposurePV[iVertexIndex][iPathIndex] =
					adblPathCollateralizedNegativeExposurePV[iVertexIndex];
				aadblUncollateralizedExposure[iVertexIndex][iPathIndex] =
					adblPathUncollateralizedExposure[iVertexIndex];
				aadblUncollateralizedExposurePV[iVertexIndex][iPathIndex] =
					adblPathUncollateralizedExposurePV[iVertexIndex];
				aadblUncollateralizedPositiveExposure[iVertexIndex][iPathIndex] =
					adblPathUncollateralizedPositiveExposure[iVertexIndex];
				aadblUncollateralizedPositiveExposurePV[iVertexIndex][iPathIndex] =
					adblPathUncollateralizedPositiveExposurePV[iVertexIndex];
				aadblUncollateralizedNegativeExposure[iVertexIndex][iPathIndex] =
					adblPathUncollateralizedNegativeExposure[iVertexIndex];
				aadblUncollateralizedNegativeExposurePV[iVertexIndex][iPathIndex] =
					adblPathUncollateralizedNegativeExposurePV[iVertexIndex];
			}
		}

		try {
			return new org.drip.xva.cpty.ExposureAdjustmentDigest (
				adblUCVA,
				adblFTDCVA,
				adblCVA,
				adblCVACL,
				adblDVA,
				adblFVA,
				adblFDA,
				adblFCA,
				adblFBA,
				adblSFVA,
				adblTotalVA,
				aadblCollateralizedExposure,
				aadblCollateralizedExposurePV,
				aadblCollateralizedPositiveExposure,
				aadblCollateralizedPositiveExposurePV,
				aadblCollateralizedNegativeExposure,
				aadblCollateralizedNegativeExposurePV,
				aadblUncollateralizedExposure,
				aadblUncollateralizedExposurePV,
				aadblUncollateralizedPositiveExposure,
				aadblUncollateralizedPositiveExposurePV,
				aadblUncollateralizedNegativeExposure,
				aadblUncollateralizedNegativeExposurePV
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
