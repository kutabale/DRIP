
package org.drip.sample.statistics;

import org.drip.feed.loader.*;
import org.drip.measure.statistics.UnivariateMoments;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * UnivariateSequence demonstrates the Generation of the Statistical Measures for the Input Series of
 * 	Univariate Sequences.
 *
 * @author Lakshmi Krishnamurthy
 */

public class UnivariateSequence {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strSeriesLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\AssetReturns\\SeriesRaw.csv";

		CSVGrid csvGrid = CSVParser.NamedStringGrid (strSeriesLocation);

		UnivariateMoments mvTOK = UnivariateMoments.Standard (
			csvGrid.header (1),
			csvGrid.doubleArrayAtColumn (1)
		);

		UnivariateMoments mvEWJ = UnivariateMoments.Standard (
			csvGrid.header (2),
			csvGrid.doubleArrayAtColumn (2)
		);

		UnivariateMoments mvHYG = UnivariateMoments.Standard (
			csvGrid.header (3),
			csvGrid.doubleArrayAtColumn (3)
		);

		UnivariateMoments mvLQD = UnivariateMoments.Standard (
			csvGrid.header (4),
			csvGrid.doubleArrayAtColumn (4)
		);

		UnivariateMoments mvEMD = UnivariateMoments.Standard (
			csvGrid.header (5),
			csvGrid.doubleArrayAtColumn (5)
		);

		UnivariateMoments mvGSG = UnivariateMoments.Standard (
			csvGrid.header (6),
			csvGrid.doubleArrayAtColumn (6)
		);

		UnivariateMoments mvBWX = UnivariateMoments.Standard (
			csvGrid.header (7),
			csvGrid.doubleArrayAtColumn (7)
		);

		System.out.println ("\n\t|----------------------------||");

		System.out.println (
			"\t| " + mvTOK.name() + " | " +
			FormatUtil.FormatDouble (mvTOK.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvTOK.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvTOK.numSample() + " ||"
		);

		System.out.println (
			"\t| " + mvEWJ.name() + " | " +
			FormatUtil.FormatDouble (mvEWJ.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvEWJ.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvEWJ.numSample() + " ||"
		);

		System.out.println (
			"\t| " + mvHYG.name() + " | " +
			FormatUtil.FormatDouble (mvHYG.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvHYG.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvHYG.numSample() + " ||"
		);

		System.out.println (
			"\t| " + mvLQD.name() + " | " +
			FormatUtil.FormatDouble (mvLQD.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvLQD.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvLQD.numSample() + " ||"
		);

		System.out.println (
			"\t| " + mvEMD.name() + " | " +
			FormatUtil.FormatDouble (mvEMD.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvEMD.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvEMD.numSample() + " ||"
		);

		System.out.println (
			"\t| " + mvGSG.name() + " | " +
			FormatUtil.FormatDouble (mvGSG.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvGSG.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvGSG.numSample() + " ||"
		);

		System.out.println (
			"\t| " + mvBWX.name() + " | " +
			FormatUtil.FormatDouble (mvBWX.mean(), 1, 2, 1200) + "% | " +
			FormatUtil.FormatDouble (mvBWX.stdDev(), 2, 1, 100 * Math.sqrt (12)) + "% | " +
			mvBWX.numSample() + " ||"
		);

		System.out.println ("\t|----------------------------||\n");
	}
}
