
package org.drip.sample.bondfixed;

import org.drip.analytics.date.*;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.*;
import org.drip.product.creator.BondBuilder;
import org.drip.product.credit.BondComponent;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.govvie.GovvieCurve;

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
 * MEZZO_MCQGQO demonstrates the Analytics Calculation for MEZZO_MCQGQO.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class MEZZO_MCQGQO {

	private static final MergedDiscountForwardCurve FundingCurve (
		final JulianDate dtSpot,
		final String strCurrency,
		final double dblBump)
		throws Exception
	{
		String[] astrDepositMaturityTenor = new String[] {
			"2D"
		};

		double[] adblDepositQuote = new double[] {
			0.0130411 + dblBump // 2D
		};

		double[] adblFuturesQuote = new double[] {
			0.02995 + dblBump,	// 97.005
			0.01345 + dblBump,	// 98.655
			0.01470 + dblBump,	// 98.530
			0.01575 + dblBump,	// 98.425
			0.01660 + dblBump,	// 98.340
			0.01745 + dblBump   // 98.255
		};

		String[] astrFixFloatMaturityTenor = new String[] {
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"06Y",
			"07Y",
			"08Y",
			"09Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y",
			"40Y",
			"50Y"
		};

		double[] adblFixFloatQuote = new double[] {
			0.016410 + dblBump, //  2Y
			0.017863 + dblBump, //  3Y
			0.019030 + dblBump, //  4Y
			0.020035 + dblBump, //  5Y
			0.020902 + dblBump, //  6Y
			0.021660 + dblBump, //  7Y
			0.022307 + dblBump, //  8Y
			0.022879 + dblBump, //  9Y
			0.023363 + dblBump, // 10Y
			0.023820 + dblBump, // 11Y
			0.024172 + dblBump, // 12Y
			0.024934 + dblBump, // 15Y
			0.025581 + dblBump, // 20Y
			0.025906 + dblBump, // 25Y
			0.025973 + dblBump, // 30Y
			0.025838 + dblBump, // 40Y
			0.025560 + dblBump  // 50Y
		};

		return LatentMarketStateBuilder.SmoothFundingCurve (
			dtSpot,
			strCurrency,
			astrDepositMaturityTenor,
			adblDepositQuote,
			"ForwardRate",
			adblFuturesQuote,
			"ForwardRate",
			astrFixFloatMaturityTenor,
			adblFixFloatQuote,
			"SwapRate"
		);
	}

	private static final GovvieCurve GovvieCurve (
		final JulianDate dtSpot,
		final String strCode)
		throws Exception
	{
		return LatentMarketStateBuilder.GovvieCurve (
			strCode,
			dtSpot,
			new JulianDate[] {
				dtSpot,
				dtSpot,
				dtSpot,
				dtSpot,
				dtSpot,
				dtSpot,
				dtSpot,
				dtSpot
			},
			new JulianDate[] {
				dtSpot.addTenor ("1Y"),
				dtSpot.addTenor ("2Y"),
				dtSpot.addTenor ("3Y"),
				dtSpot.addTenor ("5Y"),
				dtSpot.addTenor ("7Y"),
				dtSpot.addTenor ("10Y"),
				dtSpot.addTenor ("20Y"),
				dtSpot.addTenor ("30Y")
			},
			new double[] {
				0.01219, //  1Y
				0.01391, //  2Y
				0.01590, //  3Y
				0.01937, //  5Y
				0.02200, //  7Y
				0.02378, // 10Y
				0.02677, // 20Y
				0.02927  // 30Y
			},
			new double[] {
				0.01219, //  1Y
				0.01391, //  2Y
				0.01590, //  3Y
				0.01937, //  5Y
				0.02200, //  7Y
				0.02378, // 10Y
				0.02677, // 20Y
				0.02927  // 30Y
			},
			"Yield",
			LatentMarketStateBuilder.SHAPE_PRESERVING
		);
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		JulianDate dtSpot = DateUtil.CreateFromYMD (
			2017,
			DateUtil.JULY,
			10
		);

		double dblFX = 1.;
		int iSettleLag = 3;
		int iCouponFreq = 12;
		String strName = "MCQGQO";
		double dblCleanPrice = 1.;
		double dblIssuePrice = 1.;
		String strCurrency = "USD";
		double dblCouponRate = 0.0425;
		double dblIssueAmount = 2.60e7;
		String strTreasuryCode = "UST";
		String strCouponDayCount = "30/360";

		JulianDate dtEffective = DateUtil.CreateFromYMD (
			2015,
			9,
			18
		);

		JulianDate dtMaturity = DateUtil.CreateFromYMD (
			2025,
			10,
			1
		);

		BondComponent bond = BondBuilder.CreateSimpleFixed (
			strName,
			strCurrency,       // Coupon Currency
			strName,
			dblCouponRate,     // Coupon Rate
			iCouponFreq,       // Coupon Frequency
			strCouponDayCount, // Coupon Day Count
			dtEffective,       // Effective Date
			dtMaturity,        // Maturity Date
			null,
			null
		);

		JulianDate dtSettle = dtSpot.addBusDays (
			iSettleLag,
			strCurrency
		);

		ValuationParams valParams = new ValuationParams (
			dtSpot,
			dtSettle,
			strCurrency
		);

		CurveSurfaceQuoteContainer csqc = MarketParamsBuilder.Create (
			FundingCurve (
				dtSpot,
				strCurrency,
				0.
			),
			GovvieCurve (
				dtSpot,
				strTreasuryCode
			),
			null,
			null,
			null,
			null,
			null
		);

		double dblAccrued = bond.accrued (
			dtSettle.julian(),
			csqc
		);

		WorkoutInfo wi = bond.exerciseYieldFromPrice (
			valParams,
			csqc,
			null,
			dblCleanPrice
		);

		double dblYieldToMaturity = bond.yieldFromPrice (
			valParams,
			csqc,
			null,
			dblCleanPrice
		);

		double dblBondEquivalentYieldToMaturity = bond.yieldFromPrice (
			valParams,
			csqc,
			ValuationCustomizationParams.BondEquivalent (strCurrency),
			dblCleanPrice
		);

		double dblYieldToWorst = bond.yieldFromPrice (
			valParams,
			csqc,
			null,
			wi.date(),
			wi.factor(),
			dblCleanPrice
		);

		double dblNominalYield = bond.yieldFromPrice (
			valParams,
			csqc,
			null,
			dblIssuePrice
		);

		double dblOASToMaturity = bond.oasFromPrice (
			valParams,
			csqc,
			null,
			dblCleanPrice
		);

		double dblOASToWorst = bond.oasFromPrice (
			valParams,
			csqc,
			null,
			wi.date(),
			wi.factor(),
			dblCleanPrice
		);

		double dblZSpreadToMaturity = bond.zspreadFromPrice (
			valParams,
			csqc,
			null,
			dblCleanPrice
		);

		double dblZSpreadToWorst = bond.zspreadFromPrice (
			valParams,
			csqc,
			null,
			wi.date(),
			wi.factor(),
			dblCleanPrice
		);

		System.out.println();

		System.out.println ("\t||------------------------------------------------||");

		System.out.println (
			"\t|| ID                      =>  " +
			strName + "-" + strCurrency
		);

		System.out.println (
			"\t|| Price                   => " +
			FormatUtil.FormatDouble (dblCleanPrice, 3, 3, 100.)
		);

		System.out.println (
			"\t|| Market Value            => " +
			FormatUtil.FormatDouble (dblCleanPrice * dblIssueAmount, 7, 0, 1.)
		);

		System.out.println (
			"\t|| Accrued                 => " +
			FormatUtil.FormatDouble (dblAccrued, 1, 4, 1.)
		);

		System.out.println (
			"\t|| Accrued                 => " +
			FormatUtil.FormatDouble (dblAccrued * dblIssueAmount, 5, 2, 1.)
		);

		System.out.println (
			"\t|| Accrued Interest Factor => " +
			FormatUtil.FormatDouble (dblAccrued * dblFX, 1, 4, 1.)
		);

		System.out.println (
			"\t|| Yield To Maturity       => " +
			FormatUtil.FormatDouble (dblYieldToMaturity, 1, 2, 100.) + "%"
		);

		System.out.println (
			"\t|| Yield To Maturity CBE   => " +
			FormatUtil.FormatDouble (dblBondEquivalentYieldToMaturity, 1, 2, 100.) + "%"
		);

		System.out.println (
			"\t|| YTM fwdCpn              => " +
			FormatUtil.FormatDouble (dblYieldToMaturity, 1, 2, 100.) + "%"
		);

		System.out.println (
			"\t|| Yield To Worst          => " +
			FormatUtil.FormatDouble (dblYieldToWorst, 1, 2, 100.) + "%"
		);

		System.out.println (
			"\t|| YIELD TO CALL           => " +
			FormatUtil.FormatDouble (dblYieldToMaturity, 1, 2, 100.) + "%"
		);

		System.out.println (
			"\t|| Nominal Yield           => " +
			FormatUtil.FormatDouble (dblNominalYield, 1, 2, 100.) + "%"
		);

		System.out.println (
			"\t|| Z_Spread                => " +
			FormatUtil.FormatDouble (dblOASToMaturity, 3, 1, 10000.)
		);

		System.out.println (
			"\t|| Z_Vol_OAS               => " +
			FormatUtil.FormatDouble (dblZSpreadToMaturity, 3, 1, 10000.)
		);

		System.out.println (
			"\t|| OAS                     => " +
			FormatUtil.FormatDouble (dblZSpreadToWorst, 3, 1, 10000.)
		);

		System.out.println (
			"\t|| TSY OAS                 => " +
			FormatUtil.FormatDouble (dblOASToWorst, 3, 1, 10000.)
		);

		System.out.println ("\t||------------------------------------------------||");

		System.out.println();
	}
}
