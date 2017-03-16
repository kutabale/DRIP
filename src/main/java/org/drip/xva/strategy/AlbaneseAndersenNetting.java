
package org.drip.xva.strategy;

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
 * AlbaneseAndersenNetting rolls up the Path Realizations of the Sequence in a Single Path Projection Run
 *  over Multiple Collateral Groups onto a Single Netting Group - the Purpose being to calculate Credit
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

public class AlbaneseAndersenNetting extends org.drip.xva.trajectory.NettingGroupPath {

	/**
	 * Generate a "Mono" AlbaneseAndersenNetting Instance
	 * 
	 * @param cgp The "Mono" Collateral Group Path
	 * @param np The Numeraire Path
	 * 
	 * @return The "Mono" AlbaneseAndersenNetting Instance
	 */

	public static final AlbaneseAndersenNetting Mono (
		final org.drip.xva.trajectory.CollateralGroupPath cgp,
		final org.drip.xva.numeraire.MarketPath np)
	{
		try {
			return new org.drip.xva.strategy.AlbaneseAndersenNetting (new
				org.drip.xva.trajectory.CollateralGroupPath[] {cgp}, np);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * AlbaneseAndersenNetting Constructor
	 * 
	 * @param aCGP Array of the Collateral Group Trajectory Paths
	 * @param np The Numeraire Path
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public AlbaneseAndersenNetting (
		final org.drip.xva.trajectory.CollateralGroupPath[] aCGP,
		final org.drip.xva.numeraire.MarketPath np)
		throws java.lang.Exception
	{
		super (aCGP, np);
	}

	@Override public double unilateralCreditAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblUnilateralCreditAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery());

			dblUnilateralCreditAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex - 1].counterPartySurvival() - aNV[iVertexIndex].counterPartySurvival());
		}

		return dblUnilateralCreditAdjustment;
	}

	@Override public double bilateralCreditAdjustment()
	{
		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedPositiveExposurePV.length;
		double dblBilateralCreditAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery()) * aNV[iVertexIndex - 1].bankSurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery()) * aNV[iVertexIndex].bankSurvival();

			dblBilateralCreditAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex - 1].counterPartySurvival() - aNV[iVertexIndex].counterPartySurvival());
		}

		return dblBilateralCreditAdjustment;
	}

	@Override public double contraLiabilityCreditAdjustment()
	{
		return unilateralCreditAdjustment() - bilateralCreditAdjustment();
	}

	@Override public double creditAdjustment()
	{
		return bilateralCreditAdjustment();
	}

	@Override public double debtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = adblCollateralizedNegativeExposurePV.length;
		double dblDebtAdjustment = 0.;

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].bankRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].bankRecovery());

			dblDebtAdjustment -= 0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex - 1].bankSurvival() - aNV[iVertexIndex].bankSurvival());
		}

		return dblDebtAdjustment;
	}

	@Override public double[] periodUnilateralCreditAdjustment()
	{
		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = aNV.length;
		double[] adblPeriodUnilateralCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery());

			adblPeriodUnilateralCreditAdjustment[iVertexIndex - 1] = 0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex - 1].counterPartySurvival() -
					aNV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodUnilateralCreditAdjustment;
	}

	@Override public double[] periodBilateralCreditAdjustment()
	{
		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = aNV.length;
		double[] adblPeriodBilateralCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery()) * aNV[iVertexIndex - 1].bankSurvival();

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery()) * aNV[iVertexIndex].bankSurvival();

			adblPeriodBilateralCreditAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex - 1].counterPartySurvival() -
					aNV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodBilateralCreditAdjustment;
	}

	@Override public double[] periodContraLiabilityCreditAdjustment()
	{
		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = aNV.length;
		double[] adblPeriodContraLiabilityCreditAdjustment = new double[iNumVertex - 1];

		double[] adblCollateralizedPositiveExposurePV = collateralizedPositiveExposurePV();

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedPositiveExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].counterPartyRecovery()) * (1. - aNV[iVertexIndex - 1].bankSurvival());

			double dblPeriodIntegrandEnd = adblCollateralizedPositiveExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].counterPartyRecovery()) * (1. - aNV[iVertexIndex].bankSurvival());

			adblPeriodContraLiabilityCreditAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart +
				dblPeriodIntegrandEnd) * (aNV[iVertexIndex - 1].counterPartySurvival() -
					aNV[iVertexIndex].counterPartySurvival());
		}

		return adblPeriodContraLiabilityCreditAdjustment;
	}

	@Override public double[] periodCreditAdjustment()
	{
		return periodBilateralCreditAdjustment();
	}

	@Override public double[] periodDebtAdjustment()
	{
		double[] adblCollateralizedNegativeExposurePV = collateralizedNegativeExposurePV();

		org.drip.xva.numeraire.MarketVertex[] aNV = numerairePath().vertexes();

		int iNumVertex = aNV.length;
		double[] adblDebtAdjustment = new double[iNumVertex - 1];

		for (int iVertexIndex = 1; iVertexIndex < iNumVertex; ++iVertexIndex) {
			double dblPeriodIntegrandStart = adblCollateralizedNegativeExposurePV[iVertexIndex - 1] * (1. -
				aNV[iVertexIndex - 1].bankRecovery());

			double dblPeriodIntegrandEnd = adblCollateralizedNegativeExposurePV[iVertexIndex] * (1. -
				aNV[iVertexIndex].bankRecovery());

			adblDebtAdjustment[iVertexIndex - 1] = -0.5 * (dblPeriodIntegrandStart + dblPeriodIntegrandEnd) *
				(aNV[iVertexIndex - 1].bankSurvival() - aNV[iVertexIndex].bankSurvival());
		}

		return adblDebtAdjustment;
	}
}
