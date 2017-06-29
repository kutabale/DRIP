
package org.drip.xva.hypothecation;

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
 * BurgardKjaerVertexBuilder contains the Builders that construct the Burgard Kjaer Vertex using a Variant of
 *  the Generalized Burgard Kjaer (2013) Scheme. The References are:
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

public class BurgardKjaerVertexBuilder {

	/**
	 * Construct a Standard Instance of BurgardKjaerVertex
	 * 
	 * @param dtAnchor The Vertex Date Anchor
	 * @param dblExposure The Exposure at the Path Vertex Time Node
	 * @param dblRealizedCashFlow The Default Window Realized Cash-flow at the Path Vertex Time Node
	 * @param dblCollateralBalance The Collateral Balance at the Path Vertex Time Node
	 * @param mv The Market Vertex
	 * @param cog The Generic Close-Out Evaluator Instance
	 * 
	 * @return The Standard Instance of BurgardKjaerVertex
	 */

	public static final org.drip.xva.hypothecation.BurgardKjaerVertex SemiReplicationNoShortfall (
		final org.drip.analytics.date.JulianDate dtAnchor,
		final double dblExposure,
		final double dblRealizedCashFlow,
		final double dblCollateralBalance,
		final org.drip.xva.universe.MarketVertex mv,
		final org.drip.xva.definition.CloseOutGeneral cog)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblExposure) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblRealizedCashFlow) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblCollateralBalance) || null == mv || null ==
					cog)
			return null;

		org.drip.xva.derivative.ReplicationPortfolioVertexBank rpvb = null;
		double dblUncollateralizedExposure = dblExposure + dblRealizedCashFlow;
		double dblCollateralizedExposure = dblUncollateralizedExposure - dblCollateralBalance;
		double dblDebtExposure = 0. > dblCollateralizedExposure ? dblCollateralizedExposure : 0.;
		double dblCreditExposure = 0. < dblCollateralizedExposure ? dblCollateralizedExposure : 0.;
		double dblFundingExposure = 0. < dblCollateralizedExposure ? dblCollateralizedExposure : 0.;
		double dblAdjustedExposure = dblExposure + dblCreditExposure + dblDebtExposure + dblFundingExposure;

		org.drip.xva.universe.EntityMarketVertex emvBank = mv.bank();

		org.drip.xva.universe.NumeraireMarketVertex nmvBankSenior = emvBank.seniorFundingNumeraire();

		double dblBankSeniorRecovery = emvBank.seniorRecoveryRate();

		double dblBankSeniorNumeraire = nmvBankSenior.forward();

		org.drip.xva.universe.NumeraireMarketVertex nmvBankSubordinate =
			emvBank.subordinateFundingNumeraire();

		try {
			double dblBankDefaultCloseOut = cog.bankDefault (dblUncollateralizedExposure,
				dblCollateralBalance);

			if (null == nmvBankSubordinate)
				rpvb = org.drip.xva.derivative.ReplicationPortfolioVertexBank.Standard
					((dblBankDefaultCloseOut - dblAdjustedExposure) / dblBankSeniorNumeraire);
			else {
				double dblBankSubordinateRecovery = emvBank.subordinateRecoveryRate();

				double dblBankSubordinateNumeraire = nmvBankSubordinate.forward();

				rpvb = new org.drip.xva.derivative.ReplicationPortfolioVertexBank (
					(dblFundingExposure + dblBankSubordinateRecovery * dblAdjustedExposure - dblBankDefaultCloseOut) /
						(dblBankSeniorRecovery - dblBankSubordinateRecovery) / dblBankSeniorNumeraire,
					(dblFundingExposure + dblBankSeniorRecovery * dblAdjustedExposure - dblBankDefaultCloseOut) /
						(dblBankSubordinateRecovery - dblBankSeniorRecovery) / dblBankSubordinateNumeraire
				);
			}

			new org.drip.xva.hypothecation.BurgardKjaerVertex (
				dtAnchor,
				dblExposure,
				dblRealizedCashFlow,
				dblCollateralBalance,
				dblBankDefaultCloseOut,
				cog.counterPartyDefault (
					dblUncollateralizedExposure,
					dblCollateralBalance
				),
				dblCreditExposure,
				dblDebtExposure,
				dblFundingExposure,
				rpvb
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
