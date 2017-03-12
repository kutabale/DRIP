
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
 * FundingGroupPath rolls up the Path Realizations of the Sequence in a Single Path Projection Run over
 *  Multiple Collateral Groups onto a Single Funding Group - the Purpose being to calculate Funding Valuation
 *  Adjustments. The References are:
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

public class FundingGroupPath extends org.drip.xva.trajectory.ExposureGroupPath {

	/**
	 * Generate a "Mono" FundingGroupPath Instance
	 * 
	 * @param cgp The "Mono" Collateral Group Path
	 * @param np The Numeraire Path
	 * 
	 * @return The "Mono" FundingGroupPath Instance
	 */

	public static final FundingGroupPath Mono (
		final org.drip.xva.trajectory.CollateralGroupPath cgp,
		final org.drip.xva.trajectory.NumerairePath np)
	{
		try {
			return new org.drip.xva.trajectory.FundingGroupPath (new
				org.drip.xva.trajectory.CollateralGroupPath[] {cgp}, np);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * FundingGroupPath Constructor
	 * 
	 * @param aCGP Array of the Collateral Group Trajectory Paths
	 * @param np The Numeraire Path
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public FundingGroupPath (
		final org.drip.xva.trajectory.CollateralGroupPath[] aCGP,
		final org.drip.xva.trajectory.NumerairePath np)
		throws java.lang.Exception
	{
		super (aCGP, np);
	}

	/**
	 * Compute Path Funding Value Adjustment
	 * 
	 * @return The Path Funding Value Adjustment
	 */

	public double fundingValueAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblFundingValueAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread() *
					aNV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread() *
					aNV[iVertexIndex].counterPartySurvival();

			dblFundingValueAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex].anchor().julian() - aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblFundingValueAdjustment;
	}

	/**
	 * Compute Path Funding Debt Adjustment
	 * 
	 * @return The Path Funding Debt Adjustment
	 */

	public double fundingDebtAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblFundingDebtAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * (1. - aNV[iVertexIndex - 1].bankRecovery()) *
					aNV[iVertexIndex - 1].bankHazard() * aNV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * (1. - aNV[iVertexIndex].bankRecovery()) *
					aNV[iVertexIndex].bankHazard() * aNV[iVertexIndex].counterPartySurvival();

			dblFundingDebtAdjustment += 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex].anchor().julian() - aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblFundingDebtAdjustment;
	}

	/**
	 * Compute Path Funding Cost Adjustment
	 * 
	 * @return The Path Funding Cost Adjustment
	 */

	public double fundingCostAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblFundingCostAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread();

			dblFundingCostAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex].anchor().julian() - aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblFundingCostAdjustment;
	}

	/**
	 * Compute Path Funding Benefit Adjustment
	 * 
	 * @return The Path Funding Benefit Adjustment
	 */

	public double fundingBenefitAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double dblFundingBenefitAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread();

			dblFundingBenefitAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex].anchor().julian() - aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblFundingBenefitAdjustment;
	}

	/**
	 * Compute Path Symmetric Funding Value Adjustment
	 * 
	 * @return The Path Symmetric Funding Value Adjustment
	 */

	public double symmetricFundingValueAdjustment()
	{
		double[] adblCollateralizedExposurePV = collateralizedExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedExposurePV.length;
		double dblSymmetricFundingValueAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread();

			dblSymmetricFundingValueAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex].anchor().julian() - aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblSymmetricFundingValueAdjustment;
	}

	/**
	 * Compute Period-wise Path Funding Value Adjustment
	 * 
	 * @return The Period-wise Path Funding Value Adjustment
	 */

	public double[] periodFundingValueAdjustment()
	{
		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = aNV.length;
		double[] adblFundingValueAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread() *
					aNV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread() *
					aNV[iVertexIndex].counterPartySurvival();

			adblFundingValueAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex].anchor().julian() -
					aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblFundingValueAdjustment;
	}

	/**
	 * Compute Period-wise Path Funding Debt Adjustment
	 * 
	 * @return The Period-wise Path Funding Debt Adjustment
	 */

	public double[] periodFundingDebtAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double[] adblFundingDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * (1. - aNV[iVertexIndex - 1].bankRecovery()) *
					aNV[iVertexIndex - 1].bankHazard() * aNV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * (1. - aNV[iVertexIndex].bankRecovery()) *
					aNV[iVertexIndex].bankHazard() * aNV[iVertexIndex].counterPartySurvival();

			adblFundingDebtAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex].anchor().julian() -
					aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblFundingDebtAdjustment;
	}

	/**
	 * Compute Period-wise Path Funding Cost Adjustment
	 * 
	 * @return The Period-wise Path Funding Cost Adjustment
	 */

	public double[] periodFundingCostAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double[] adblFundingCostAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread();

			adblFundingCostAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex].anchor().julian() -
					aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblFundingCostAdjustment;
	}

	/**
	 * Compute Period-wise Path Funding Benefit Adjustment
	 * 
	 * @return The Period-wise Path Funding Benefit Adjustment
	 */

	public double[] periodFundingBenefitAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double[] adblFundingBenefitAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread();

			adblFundingBenefitAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex].anchor().julian() -
					aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblFundingBenefitAdjustment;
	}

	/**
	 * Compute Period-wise Path Symmetric Funding Value Adjustment
	 * 
	 * @return The Period-wise Path Symmetric Funding Value Adjustment
	 */

	public double[] periodSymmetricFundingValueAdjustment()
	{
		double[] adblCollateralizedExposurePV = collateralizedExposurePV();

		org.drip.xva.trajectory.NumeraireVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedExposurePV.length;
		double[] adblSymmetricFundingValueAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedExposurePV[iVertexIndex - 1] *
				aNV[iVertexIndex - 1].bankSurvival() * aNV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedExposurePV[iVertexIndex] *
				aNV[iVertexIndex].bankSurvival() * aNV[iVertexIndex].bankFundingSpread();

			adblSymmetricFundingValueAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex].anchor().julian() -
					aNV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblSymmetricFundingValueAdjustment;
	}
}
