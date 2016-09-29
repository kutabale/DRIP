
package org.drip.sample.statistics;

import org.drip.feed.loader.*;
import org.drip.measure.statistics.MultivariateMoments;
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

public class MultivariateSequence {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strSeriesLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\AssetReturns\\FormattedSeries1.csv";

		CSVGrid csvGrid = CSVParser.NamedStringGrid (strSeriesLocation);

		String[] astrVariateHeader = csvGrid.headers();

		String[] astrVariateName = new String[astrVariateHeader.length - 1];
		double[][] aadblVariateSample = new double[astrVariateHeader.length - 1][];

		for (int i = 0; i < astrVariateName.length; ++i) {
			astrVariateName[i] = astrVariateHeader[i + 1];

			aadblVariateSample[i] = csvGrid.doubleArrayAtColumn (i + 1);
		}

		MultivariateMoments mvm = MultivariateMoments.Standard (
			astrVariateName,
			aadblVariateSample
		);

		System.out.println ("\n\n\t|-------------------------------------------------------------------||");

		for (int i = 0; i < astrVariateName.length; ++i)
			System.out.println (
				"\t| Mean/Variance/Standard Deviation [" + astrVariateName[i] + "] => " +
				FormatUtil.FormatDouble (mvm.mean (astrVariateName[i]), 1, 2, 1200.) + "% | " +
				FormatUtil.FormatDouble (mvm.variance (astrVariateName[i]), 1, 2, 1200.) + " | " +
				FormatUtil.FormatDouble (mvm.variance (astrVariateName[i]), 1, 2, 100. * Math.sqrt (12)) + "% ||"
			);

		System.out.println ("\t|-------------------------------------------------------------------||\n\n");

		System.out.println ("\n\n\t|------------------------------------------------------||");

		String strHeader = "\t|     |";

		for (int i = 0; i < astrVariateName.length; ++i)
			strHeader += " " + astrVariateName[i] + "  |";

		System.out.println (strHeader + "|");

		System.out.println ("\t|------------------------------------------------------||");

		for (int i = 0; i < astrVariateName.length; ++i) {
			String strDump = "\t| " + astrVariateName[i] + " ";

			for (int j = 0; j < astrVariateName.length; ++j)
				strDump += "|" + FormatUtil.FormatDouble (
					mvm.covariance (
						astrVariateName[i],
						astrVariateName[j]
					), 1, 2, 1200.) + " ";

			System.out.println (strDump + "||");
		}

		System.out.println ("\t|------------------------------------------------------||\n\n");

		System.out.println ("\n\n\t|------------------------------------------------------||");

		strHeader = "\t|     |";

		for (int i = 0; i < astrVariateName.length; ++i)
			strHeader += " " + astrVariateName[i] + "  |";

		System.out.println (strHeader + "|");

		System.out.println ("\t|------------------------------------------------------||");

		for (int i = 0; i < astrVariateName.length; ++i) {
			String strDump = "\t| " + astrVariateName[i] + " ";

			for (int j = 0; j < astrVariateName.length; ++j)
				strDump += "|" + FormatUtil.FormatDouble (
					mvm.correlation (
						astrVariateName[i],
						astrVariateName[j]
					), 1, 2, 1.) + " ";

			System.out.println (strDump + "||");
		}

		System.out.println ("\t|------------------------------------------------------||\n\n");
	}
}
