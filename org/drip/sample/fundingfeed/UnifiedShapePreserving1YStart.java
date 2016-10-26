
package org.drip.sample.fundingfeed;

import java.io.BufferedWriter;
import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.state.FundingCurveMetrics;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.rates.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.state.FundingCurveAPI;
import org.drip.service.template.*;
import org.drip.state.discount.MergedDiscountForwardCurve;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
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
 * UnifiedShapePreserving1YStart demonstrates the unified re-constitution and Metrics Generation.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class UnifiedShapePreserving1YStart {

	public static final void main (
		final String[] args)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strCurrency = "USD";
		String strFundingMarksLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\FundingMarks\\" + strCurrency + "Formatted.csv";
		String strFundingMetricsLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Metrics\\FundingCurve\\Unified\\" + strCurrency + ".csv";

		String[] astrPreFixFloatTenor = new String[] {
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y",
			"9Y",
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
		String[] astrInTenor = new String[] {
			"1Y"
		};
		String[] astrForTenor = new String[] {
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y",
			"9Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
		};
		String[] astrPostFixFloatMaturityTenor = new String[] {
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y",
			"9Y",
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

		CSVGrid csvGrid = CSVParser.StringGrid (
			strFundingMarksLocation,
			false
		);

		Map<JulianDate, InstrumentSetTenorQuote> mapISTQ = csvGrid.groupedOrderedDouble (0.01);

		int iNumClose = mapISTQ.size();

		int iCloseDate = 0;
		String strDump = "Date";
		JulianDate[] adtClose = new JulianDate[iNumClose];
		double[][] aadblFixFloatQuote = new double[iNumClose][18];

		for (String strInTenor : astrInTenor) {
			for (String strForTenor : astrForTenor)
				strDump += "," + strInTenor + strForTenor;
		}

		System.out.println (strDump);

		BufferedWriter bwMetrics = new BufferedWriter (new java.io.FileWriter (strFundingMetricsLocation));

		bwMetrics.write (strDump + "\n");

		for (Map.Entry<JulianDate, InstrumentSetTenorQuote> meISTQ : mapISTQ.entrySet()) {
			if (null == meISTQ) continue;

			JulianDate dtSpot = meISTQ.getKey();

			InstrumentSetTenorQuote istq = meISTQ.getValue();

			if (null == dtSpot || null == istq) continue;

			double[] adblDepositQuote = istq.instrumentQuote ("DEPOSIT");

			String[] astrDepositMaturityTenor = istq.instrumentTenor ("DEPOSIT");

			double[] adblFixFloatQuote = istq.instrumentQuote ("FIXFLOAT");

			String[] astrFixFloatMaturityTenor = istq.instrumentTenor ("FIXFLOAT");

			int iNumDepositQuote = null == adblDepositQuote ? 0 : adblDepositQuote.length;
			int iNumFixFloatQuote = null == adblFixFloatQuote ? 0 : adblFixFloatQuote.length;
			int iNumDepositTenor = null == astrDepositMaturityTenor ? 0 : astrDepositMaturityTenor.length;
			int iNumFixFloatTenor = null == astrFixFloatMaturityTenor ? 0 : astrFixFloatMaturityTenor.length;

			if (0 == iNumFixFloatQuote || iNumDepositQuote != iNumDepositTenor || iNumFixFloatQuote !=
				iNumFixFloatTenor)
				continue;

			MergedDiscountForwardCurve dcFunding = LatentMarketStateBuilder.FundingCurve (
				dtSpot,
				strCurrency,
				astrDepositMaturityTenor,
				adblDepositQuote,
				"ForwardRate",
				null,
				"ForwardRate",
				astrFixFloatMaturityTenor,
				adblFixFloatQuote,
				"SwapRate",
				LatentMarketStateBuilder.SHAPE_PRESERVING
			);

			CurveSurfaceQuoteContainer csqc = new CurveSurfaceQuoteContainer();

			if (!csqc.setFundingState (dcFunding)) continue;

			ValuationParams valParams = ValuationParams.Spot (dtSpot.julian());

			FixFloatComponent[] aFFC = OTCInstrumentBuilder.FixFloatStandard (
				dtSpot,
				strCurrency,
				"ALL",
				astrPreFixFloatTenor,
				"MAIN",
				0.
			);

			for (int j = 0; j < aFFC.length; ++j)
				aadblFixFloatQuote[iCloseDate][j] = aFFC[j].measureValue (
					valParams,
					null,
					csqc,
					null,
					"SwapRate"
				);

			adtClose[iCloseDate] = dtSpot;

			if (++iCloseDate >= iNumClose) break;
		}

		Map<JulianDate, FundingCurveMetrics> mapFCM = FundingCurveAPI.HorizonMetrics (
			adtClose,
			astrPostFixFloatMaturityTenor,
			aadblFixFloatQuote,
			astrInTenor,
			astrForTenor,
			strCurrency,
			LatentMarketStateBuilder.SHAPE_PRESERVING
		);

		for (int i = 0; i < iNumClose; ++i) {
			FundingCurveMetrics fcm = mapFCM.get (adtClose[i]);

			strDump = adtClose[i].toString();

			for (String strInTenor : astrInTenor) {
				for (String strForTenor : astrForTenor)
					strDump += "," + FormatUtil.FormatDouble (
						fcm.nativeForwardRate (
							strInTenor,
							strForTenor
						), 1, 5, 100.
					);
			}

			System.out.println (strDump);

			bwMetrics.write (strDump + "\n");
		}

		bwMetrics.close();
	}
}
