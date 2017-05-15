
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
 * CreditDebtGroupPath rolls up the Path Realizations of the Sequence in a Single Path Projection Run over
 *  Multiple Collateral Hypothecation Groups onto a Single Credit/Debt Netting Group - the Purpose being to
 *  calculate Credit Valuation Adjustments. The References are:
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

public abstract class CreditDebtGroupPath extends org.drip.xva.netting.ExposureGroupPath {

	protected CreditDebtGroupPath (
		final org.drip.xva.collateral.HypothecationGroupPath[] aHGP,
		final org.drip.xva.numeraire.MarketPath mp)
		throws java.lang.Exception
	{
		super (aHGP, mp);
	}

	/**
	 * Compute Path Unilateral Credit Adjustment
	 * 
	 * @return The Path Unilateral Credit Adjustment
	 */

	public double unilateralCreditAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblUnilateralCreditAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].counterPartyRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].counterPartyRecovery());

			dblUnilateralCreditAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex - 1].counterPartySurvival() - aMV[iVertexIndex].counterPartySurvival());
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

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblBilateralCreditAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].counterPartyRecovery()) * aMV[iVertexIndex - 1].bankSurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].counterPartyRecovery()) * aMV[iVertexIndex].bankSurvival();

			dblBilateralCreditAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex - 1].counterPartySurvival() - aMV[iVertexIndex].counterPartySurvival());
		}

		return dblBilateralCreditAdjustment;
	}

	/**
	 * Compute Path Contra-Liability Credit Adjustment
	 * 
	 * @return The Path Contra-Liability Credit Adjustment
	 */

	public double contraLiabilityCreditAdjustment()
	{
		return unilateralCreditAdjustment() - bilateralCreditAdjustment();
	}

	/**
	 * Compute Path Unilateral Debt Adjustment
	 * 
	 * @return The Path Unilateral Debt Adjustment
	 */

	public double unilateralDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double dblUnilateralDebtAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].bankRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].bankRecovery());

			dblUnilateralDebtAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex - 1].bankSurvival() - aMV[iVertexIndex].bankSurvival());
		}

		return dblUnilateralDebtAdjustment;
	}

	/**
	 * Compute Path Bilateral Debt Adjustment
	 * 
	 * @return The Path Bilateral Debt Adjustment
	 */

	public double bilateralDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double dblBilateralDebtAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].bankRecovery()) * aMV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].bankRecovery()) * aMV[iVertexIndex].counterPartySurvival();

			dblBilateralDebtAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aMV[iVertexIndex - 1].bankSurvival() - aMV[iVertexIndex].bankSurvival());
		}

		return dblBilateralDebtAdjustment;
	}

	/**
	 * Compute Period-wise Uni-lateral Credit Adjustment
	 * 
	 * @return The Period-wise Uni-lateral Credit Adjustment
	 */

	public double[] periodUnilateralCreditAdjustment()
	{
		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = aMV.length;
		double[] adblPeriodUnilateralCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].counterPartyRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].counterPartyRecovery());

			adblPeriodUnilateralCreditAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex - 1].counterPartySurvival() -
					aMV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodUnilateralCreditAdjustment;
	}

	/**
	 * Compute Period-wise Bilateral Credit Adjustment
	 * 
	 * @return The Period-wise Bilateral Credit Adjustment
	 */

	public double[] periodBilateralCreditAdjustment()
	{
		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = aMV.length;
		double[] adblPeriodBilateralCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].counterPartyRecovery()) * aMV[iVertexIndex - 1].bankSurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].counterPartyRecovery()) * aMV[iVertexIndex].bankSurvival();

			adblPeriodBilateralCreditAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex - 1].counterPartySurvival() -
					aMV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodBilateralCreditAdjustment;
	}

	/**
	 * Compute Period-wise Contra-Liability Credit Adjustment
	 * 
	 * @return The Period-wise Contra-Liability Credit Adjustment
	 */

	public double[] periodContraLiabilityCreditAdjustment()
	{
		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = aMV.length;
		double[] adblPeriodContraLiabilityCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].counterPartyRecovery()) * (1. - aMV[iVertexIndex - 1].bankSurvival());

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].counterPartyRecovery()) * (1. - aMV[iVertexIndex].bankSurvival());

			adblPeriodContraLiabilityCreditAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex - 1].counterPartySurvival() -
					aMV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodContraLiabilityCreditAdjustment;
	}

	/**
	 * Compute Period-wise Unilateral Debt Adjustment
	 * 
	 * @return The Period-wise Unilateral Debt Adjustment
	 */

	public double[] periodUnilateralDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = aMV.length;
		double[] adblUnilateralDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].bankRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].bankRecovery());

			adblUnilateralDebtAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex - 1].bankSurvival() -
					aMV[iVertexIndex].bankSurvival());
		}

		return adblUnilateralDebtAdjustment;
	}

	/**
	 * Compute Period-wise Bilateral Debt Adjustment
	 * 
	 * @return The Period-wise Bilateral Debt Adjustment
	 */

	public double[] periodBilateralDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aMV = marketPath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double[] adblBilateralDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aMV[iVertexIndex - 1].bankRecovery()) * aMV[iVertexIndex - 1].counterPartySurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aMV[iVertexIndex].bankRecovery()) * aMV[iVertexIndex].counterPartySurvival();

			adblBilateralDebtAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aMV[iVertexIndex - 1].bankSurvival() -
					aMV[iVertexIndex].bankSurvival());
		}

		return adblBilateralDebtAdjustment;
	}

	/**
	 * Compute Path Credit Adjustment
	 * 
	 * @return The Path Credit Adjustment
	 */

	public abstract double creditAdjustment();

	/**
	 * Compute Path Debt Adjustment
	 * 
	 * @return The Path Debt Adjustment
	 */

	public abstract double debtAdjustment();

	/**
	 * Compute Period-wise Credit Adjustment
	 * 
	 * @return The Period-wise Credit Adjustment
	 */

	public abstract double[] periodCreditAdjustment();

	/**
	 * Compute Period-wise Debt Adjustment
	 * 
	 * @return The Period-wise Debt Adjustment
	 */

	public abstract double[] periodDebtAdjustment();
}
