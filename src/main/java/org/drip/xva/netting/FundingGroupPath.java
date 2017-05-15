
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
 * FundingGroupPath holds up the Strategy Abstract Realizations of the Sequence in a Single Path Projection
 *  Run over Multiple Collateral Groups onto a Single Funding Group - the Purpose being to calculate Funding
 *  Valuation Adjustments. The References are:
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

public abstract class FundingGroupPath extends org.drip.xva.netting.ExposureGroupPath {

	protected FundingGroupPath (
		final org.drip.xva.collateral.HypothecationGroupPath[] aHGP,
		final org.drip.xva.numeraire.MarketPath mp)
		throws java.lang.Exception
	{
		super (aHGP, mp);
	}

	/**
	 * Compute Path Unilateral Funding Value Adjustment
	 * 
	 * @return The Path Unilateral Funding Value Adjustment
	 */

	public double unilateralFundingValueAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblUnilateralFundingCostAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * aMV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * aMV[iVertexIndex].bankFundingSpread();

			dblUnilateralFundingCostAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex].anchor().julian() - aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblUnilateralFundingCostAdjustment;
	}

	/**
	 * Compute Path Bilateral Funding Value Adjustment
	 * 
	 * @return The Path Bilateral Funding Value Adjustment
	 */

	public double bilateralFundingValueAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblBilateralFundingValueAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * aMV[iVertexIndex - 1].bankFundingSpread() *
					aMV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * aMV[iVertexIndex].bankFundingSpread() *
					aMV[iVertexIndex].counterPartySurvival();

			dblBilateralFundingValueAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex].anchor().julian() - aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblBilateralFundingValueAdjustment;
	}

	/**
	 * Compute Path Unilateral Funding Debt Adjustment
	 * 
	 * @return The Path Unilateral Funding Debt Adjustment
	 */

	public double unilateralFundingDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double dblUnilateralFundingBenefitAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * aMV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * aMV[iVertexIndex].bankFundingSpread();

			dblUnilateralFundingBenefitAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd)
				* (aMV[iVertexIndex].anchor().julian() - aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblUnilateralFundingBenefitAdjustment;
	}

	/**
	 * Compute Path Bilateral Funding Debt Adjustment
	 * 
	 * @return The Path Bilateral Funding Debt Adjustment
	 */

	public double bilateralFundingDebtAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblBilateralFundingDebtAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * (1. - aMV[iVertexIndex - 1].bankRecovery()) *
					aMV[iVertexIndex - 1].bankHazard() * aMV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * (1. - aMV[iVertexIndex].bankRecovery()) *
					aMV[iVertexIndex].bankHazard() * aMV[iVertexIndex].counterPartySurvival();

			dblBilateralFundingDebtAdjustment += 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex].anchor().julian() - aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblBilateralFundingDebtAdjustment;
	}

	/**
	 * Compute Path Symmetric Funding Value Adjustment
	 * 
	 * @return The Path Symmetric Funding Value Adjustment
	 */

	public double symmetricFundingValueAdjustment()
	{
		double[] adblCollateralizedExposurePV = collateralizedExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedExposurePV.length;
		double dblSymmetricFundingValueAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * aMV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * aMV[iVertexIndex].bankFundingSpread();

			dblSymmetricFundingValueAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex].anchor().julian() - aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return dblSymmetricFundingValueAdjustment;
	}

	/**
	 * Compute Period-wise Unilateral Path Funding Value Adjustment
	 * 
	 * @return The Period-wise Unilateral Path Funding Value Adjustment
	 */

	public double[] periodUnilateralFundingValueAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double[] adblUnilateralFundingDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * (1. - aMV[iVertexIndex - 1].bankRecovery()) *
					aMV[iVertexIndex - 1].bankHazard();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * (1. - aMV[iVertexIndex].bankRecovery()) *
					aMV[iVertexIndex].bankHazard();

			adblUnilateralFundingDebtAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex].anchor().julian() -
					aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblUnilateralFundingDebtAdjustment;
	}

	/**
	 * Compute Period-wise Bilateral Path Funding Value Adjustment
	 * 
	 * @return The Period-wise Bilateral Path Funding Value Adjustment
	 */

	public double[] periodBilateralFundingValueAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double[] adblBilateralFundingDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * (1. - aMV[iVertexIndex - 1].bankRecovery()) *
					aMV[iVertexIndex - 1].bankHazard() * aMV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * (1. - aMV[iVertexIndex].bankRecovery()) *
					aMV[iVertexIndex].bankHazard() * aMV[iVertexIndex].counterPartySurvival();

			adblBilateralFundingDebtAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex].anchor().julian() -
					aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblBilateralFundingDebtAdjustment;
	}

	/**
	 * Compute Period-wise Path Unilateral Funding Debt Adjustment
	 * 
	 * @return The Period-wise Path Unilateral Funding Debt Adjustment
	 */

	public double[] periodUnilateralFundingDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double[] adblUnilateralFundingDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * aMV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * aMV[iVertexIndex].bankFundingSpread();

			adblUnilateralFundingDebtAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex].anchor().julian() -
					aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblUnilateralFundingDebtAdjustment;
	}

	/**
	 * Compute Period-wise Path Bilateral Funding Debt Adjustment
	 * 
	 * @return The Period-wise Path Bilateral Funding Debt Adjustment
	 */

	public double[] periodBilateralFundingDebtAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double[] adblBilateralFundingDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * (1. - aMV[iVertexIndex - 1].bankRecovery()) *
					aMV[iVertexIndex - 1].bankHazard() * aMV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * (1. - aMV[iVertexIndex].bankRecovery()) *
					aMV[iVertexIndex].bankHazard() * aMV[iVertexIndex].counterPartySurvival();

			adblBilateralFundingDebtAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex].anchor().julian() -
					aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblBilateralFundingDebtAdjustment;
	}

	/**
	 * Compute Period-wise Path Symmetric Funding Value Adjustment
	 * 
	 * @return The Period-wise Path Symmetric Funding Value Adjustment
	 */

	public double[] periodSymmetricFundingValueAdjustment()
	{
		double[] adblCollateralizedExposurePV = collateralizedExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedExposurePV.length;
		double[] adblSymmetricFundingValueAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedExposurePV[iVertexIndex - 1] *
				aMV[iVertexIndex - 1].bankSurvival() * aMV[iVertexIndex - 1].bankFundingSpread();

			double dblPeriodIntegrandEnd = adblCollateralizedExposurePV[iVertexIndex] *
				aMV[iVertexIndex].bankSurvival() * aMV[iVertexIndex].bankFundingSpread();

			adblSymmetricFundingValueAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex].anchor().julian() -
					aMV[iVertexIndex - 1].anchor().julian()) / 365.25;
		}

		return adblSymmetricFundingValueAdjustment;
	}

	/**
	 * Compute Path Funding Value Adjustment
	 * 
	 * @return The Path Funding Value Adjustment
	 */

	public abstract double fundingValueAdjustment();

	/**
	 * Compute Path Funding Debt Adjustment
	 * 
	 * @return The Path Funding Debt Adjustment
	 */

	public abstract double fundingDebtAdjustment();

	/**
	 * Compute Path Funding Cost Adjustment
	 * 
	 * @return The Path Funding Cost Adjustment
	 */

	public abstract double fundingCostAdjustment();

	/**
	 * Compute Path Funding Benefit Adjustment
	 * 
	 * @return The Path Funding Benefit Adjustment
	 */

	public abstract double fundingBenefitAdjustment();

	/**
	 * Compute Period-wise Path Funding Value Adjustment
	 * 
	 * @return The Period-wise Path Funding Value Adjustment
	 */

	public abstract double[] periodFundingValueAdjustment();

	/**
	 * Compute Period-wise Path Funding Debt Adjustment
	 * 
	 * @return The Period-wise Path Funding Debt Adjustment
	 */

	public abstract double[] periodFundingDebtAdjustment();

	/**
	 * Compute Period-wise Path Funding Cost Adjustment
	 * 
	 * @return The Period-wise Path Funding Cost Adjustment
	 */

	public abstract double[] periodFundingCostAdjustment();

	/**
	 * Compute Period-wise Path Funding Benefit Adjustment
	 * 
	 * @return The Period-wise Path Funding Benefit Adjustment
	 */

	public abstract double[] periodFundingBenefitAdjustment();
}
