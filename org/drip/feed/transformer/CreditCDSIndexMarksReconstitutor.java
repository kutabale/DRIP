
package org.drip.feed.transformer;

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
 * CreditCDSIndexMarksReconstitutor transforms the Credit CDS Index Closes - Feed Inputs into Formats
 *  suitable for Valuation Metrics and Sensitivities Generation.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CreditCDSIndexMarksReconstitutor {

	private static final double[] FundingFixingMarksIndex (
		final org.drip.feed.loader.CSVGrid csvGridFundingFixingMarks,
		final org.drip.analytics.date.JulianDate[] adtFundingFixingSpot,
		final org.drip.analytics.date.JulianDate dtCreditIndexSpot)
	{
		int iNumFundingFixingSpot = adtFundingFixingSpot.length;
		int iNumFundingFixingMaturityTenor =
			org.drip.feed.transformer.FundingFixFloatMarksReconstitutor.s_astrFixFloatTenor.length;

		for (int i = 0; i < iNumFundingFixingSpot; ++i) {
			if (dtCreditIndexSpot.julian() == adtFundingFixingSpot[i].julian()) {
				double[] adblFundingFixingFixFloatQuote = new double[iNumFundingFixingMaturityTenor];

				for (int j = 0; j < iNumFundingFixingMaturityTenor; ++j)
					adblFundingFixingFixFloatQuote[j] = csvGridFundingFixingMarks.doubleArrayAtColumn
						(j + 1)[i];

				return adblFundingFixingFixFloatQuote;
			}
		}

		return null;
	}

	/**
	 * Regularize the Credit Index Feed Marks
	 * 
	 * @param strFundingFixingMarksLocation The Funding Fixing Marks Location
	 * @param strCreditIndexMarksLocation The Credit Index Marks Location
	 * @param strIndexFullName Full Name of the Index
	 * @param iSpotDateIndex Spot Date Column Index
	 * @param iQuotedSpreadIndex Quoted Spread Column Index
	 * 
	 * @return TRUE - The Regularization is Successful
	 */

	public static final boolean RegularizeCloses (
		final java.lang.String strFundingFixingMarksLocation,
		final java.lang.String strCreditIndexMarksLocation,
		final java.lang.String strIndexFullName,
		final int iSpotDateIndex,
		final int iQuotedSpreadIndex)
	{
		org.drip.feed.loader.CSVGrid csvGridFundingFixingMarks = org.drip.feed.loader.CSVParser.StringGrid
			(strFundingFixingMarksLocation, true);

		if (null == csvGridFundingFixingMarks) return false;

		org.drip.analytics.date.JulianDate[] adtFundingFixingMarksSpot =
			csvGridFundingFixingMarks.dateArrayAtColumn (0);

		if (null == adtFundingFixingMarksSpot || 0 == adtFundingFixingMarksSpot.length) return false;

		org.drip.feed.loader.CSVGrid csvGridCreditIndexMarks = org.drip.feed.loader.CSVParser.StringGrid
			(strCreditIndexMarksLocation, true);

		if (null == csvGridCreditIndexMarks) return false;

		org.drip.analytics.date.JulianDate[] adtCreditIndexMarksSpot =
			csvGridCreditIndexMarks.dateArrayAtColumn (iSpotDateIndex);

		if (null == adtCreditIndexMarksSpot) return false;

		int iNumClose = adtCreditIndexMarksSpot.length;
		int iNumFundingFixingMaturityTenor =
			org.drip.feed.transformer.FundingFixFloatMarksReconstitutor.s_astrFixFloatTenor.length;

		if (0 == iNumClose) return false;

		double[] adblQuotedSpread = csvGridCreditIndexMarks.doubleArrayAtColumn (iQuotedSpreadIndex);

		if (null == adblQuotedSpread || iNumClose != adblQuotedSpread.length) return false;

		java.lang.String strDump = "CloseDate";

		for (int j = 0; j < iNumFundingFixingMaturityTenor; ++j)
			strDump += "," +
				org.drip.feed.transformer.FundingFixFloatMarksReconstitutor.s_astrFixFloatTenor[j];

		System.out.println (strDump + ",CreditIndexName,QuotedSpread");

		for (int i = 0; i < iNumClose; ++i) {
			double[] adblFundingFixingFixFloatQuote = FundingFixingMarksIndex (csvGridFundingFixingMarks,
				adtFundingFixingMarksSpot, adtCreditIndexMarksSpot[i]);

			if (null == adblFundingFixingFixFloatQuote) continue;

			strDump = "";

			for (int j = 0; j < iNumFundingFixingMaturityTenor; ++j)
				strDump += "," + adblFundingFixingFixFloatQuote[j];

			System.out.println (adtCreditIndexMarksSpot[i] + strDump + "," + strIndexFullName + "," + (0.0001
				* adblQuotedSpread[i]));
		}

		return true;
	}
}
