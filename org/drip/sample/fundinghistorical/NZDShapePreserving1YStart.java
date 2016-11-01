
package org.drip.sample.fundinghistorical;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.state.FundingCurveMetrics;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.state.FundingCurveAPI;
import org.drip.service.template.LatentMarketStateBuilder;

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
 * NZDShapePreserving1YStart Generates the Historical NZD Shape Preserving Funding Curve Native Compounded
 *  Forward Rate starting at 1Y Tenor.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class NZDShapePreserving1YStart {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strCurrency = "NZD";
		String strClosesLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Transforms\\FundingStateMarks\\" + strCurrency + "ShapePreservingReconstitutor.csv";
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
		String[] astrFixFloatMaturityTenor = new String[] {
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
			strClosesLocation,
			true
		);

		JulianDate[] adtClose = csvGrid.dateArrayAtColumn (0);

		double[] adblFixFloatQuote1Y = csvGrid.doubleArrayAtColumn (1);

		double[] adblFixFloatQuote2Y = csvGrid.doubleArrayAtColumn (2);

		double[] adblFixFloatQuote3Y = csvGrid.doubleArrayAtColumn (3);

		double[] adblFixFloatQuote4Y = csvGrid.doubleArrayAtColumn (4);

		double[] adblFixFloatQuote5Y = csvGrid.doubleArrayAtColumn (5);

		double[] adblFixFloatQuote6Y = csvGrid.doubleArrayAtColumn (6);

		double[] adblFixFloatQuote7Y = csvGrid.doubleArrayAtColumn (7);

		double[] adblFixFloatQuote8Y = csvGrid.doubleArrayAtColumn (8);

		double[] adblFixFloatQuote9Y = csvGrid.doubleArrayAtColumn (9);

		double[] adblFixFloatQuote10Y = csvGrid.doubleArrayAtColumn (10);

		double[] adblFixFloatQuote11Y = csvGrid.doubleArrayAtColumn (11);

		double[] adblFixFloatQuote12Y = csvGrid.doubleArrayAtColumn (12);

		double[] adblFixFloatQuote15Y = csvGrid.doubleArrayAtColumn (13);

		double[] adblFixFloatQuote20Y = csvGrid.doubleArrayAtColumn (14);

		double[] adblFixFloatQuote25Y = csvGrid.doubleArrayAtColumn (15);

		double[] adblFixFloatQuote30Y = csvGrid.doubleArrayAtColumn (16);

		double[] adblFixFloatQuote40Y = csvGrid.doubleArrayAtColumn (17);

		double[] adblFixFloatQuote50Y = csvGrid.doubleArrayAtColumn (18);

		int iNumClose = adtClose.length;
		JulianDate[] adtSpot = new JulianDate[iNumClose];
		double[][] aadblFixFloatQuote = new double[iNumClose][18];

		for (int i = 0; i < iNumClose; ++i) {
			adtSpot[i] = adtClose[i];
			aadblFixFloatQuote[i][0] = adblFixFloatQuote1Y[i];
			aadblFixFloatQuote[i][1] = adblFixFloatQuote2Y[i];
			aadblFixFloatQuote[i][2] = adblFixFloatQuote3Y[i];
			aadblFixFloatQuote[i][3] = adblFixFloatQuote4Y[i];
			aadblFixFloatQuote[i][4] = adblFixFloatQuote5Y[i];
			aadblFixFloatQuote[i][5] = adblFixFloatQuote6Y[i];
			aadblFixFloatQuote[i][6] = adblFixFloatQuote7Y[i];
			aadblFixFloatQuote[i][7] = adblFixFloatQuote8Y[i];
			aadblFixFloatQuote[i][8] = adblFixFloatQuote9Y[i];
			aadblFixFloatQuote[i][9] = adblFixFloatQuote10Y[i];
			aadblFixFloatQuote[i][10] = adblFixFloatQuote11Y[i];
			aadblFixFloatQuote[i][11] = adblFixFloatQuote12Y[i];
			aadblFixFloatQuote[i][12] = adblFixFloatQuote15Y[i];
			aadblFixFloatQuote[i][13] = adblFixFloatQuote20Y[i];
			aadblFixFloatQuote[i][14] = adblFixFloatQuote25Y[i];
			aadblFixFloatQuote[i][15] = adblFixFloatQuote30Y[i];
			aadblFixFloatQuote[i][16] = adblFixFloatQuote40Y[i];
			aadblFixFloatQuote[i][17] = adblFixFloatQuote50Y[i];
		}

		String strDump = "Date";

		for (String strInTenor : astrInTenor) {
			for (String strForTenor : astrForTenor)
				strDump += "," + strInTenor + strForTenor;
		}

		System.out.println (strDump);

		Map<JulianDate, FundingCurveMetrics> mapFCM = FundingCurveAPI.HorizonMetrics (
			adtSpot,
			astrFixFloatMaturityTenor,
			aadblFixFloatQuote,
			astrInTenor,
			astrForTenor,
			strCurrency,
			LatentMarketStateBuilder.SHAPE_PRESERVING
		);

		for (int i = 0; i < iNumClose; ++i) {
			FundingCurveMetrics fcm = mapFCM.get (adtSpot[i]);

			strDump = adtSpot[i].toString();

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
		}
	}
}
