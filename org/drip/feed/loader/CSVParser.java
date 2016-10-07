
package org.drip.feed.loader;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
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
 * CSVParser Parses the Lines of a Comma Separated File into appropriate Data Types.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CSVParser {

	/**
	 * Parse the Contents of the CSV File into a List of String Arrays
	 * 
	 * @param strCSVFile The CSV File
	 * @param bIgnoreHeader TRUE - Ignore the Leading Row as a Header
	 * 
	 * @return List of String Arrays
	 */

	@SuppressWarnings ("resource") public static final org.drip.feed.loader.CSVGrid StringGrid (
		final java.lang.String strCSVFile,
		final boolean bIgnoreHeader)
	{
		if (null == strCSVFile || strCSVFile.isEmpty()) return null;

		boolean bHeader = true;
		java.lang.String strCSVLine = "";
		java.io.BufferedReader brCSV = null;

		org.drip.feed.loader.CSVGrid csvGrid = new org.drip.feed.loader.CSVGrid();

		try {
			brCSV = new java.io.BufferedReader (new java.io.FileReader (strCSVFile));

			while (null != (strCSVLine = brCSV.readLine())) {
				java.lang.String[] astrValue = org.drip.quant.common.StringUtil.Split (strCSVLine, ",");

				if (null != astrValue && 0 != astrValue.length) {
					if (!bHeader || !bIgnoreHeader) csvGrid.add (astrValue);

					bHeader = false;
				}
			}

			return csvGrid;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Parse the Contents of the CSV File into a List of Named String Arrays
	 * 
	 * @param strCSVFile The CSV File
	 * 
	 * @return List of String Arrays
	 */

	@SuppressWarnings ("resource") public static final org.drip.feed.loader.CSVGrid NamedStringGrid (
		final java.lang.String strCSVFile)
	{
		if (null == strCSVFile || strCSVFile.isEmpty()) return null;

		boolean bHeader = true;
		java.lang.String strCSVLine = "";
		java.io.BufferedReader brCSV = null;

		org.drip.feed.loader.CSVGrid csvGrid = new org.drip.feed.loader.CSVGrid();

		try {
			brCSV = new java.io.BufferedReader (new java.io.FileReader (strCSVFile));

			while (null != (strCSVLine = brCSV.readLine())) {
				java.lang.String[] astrValue = org.drip.quant.common.StringUtil.Split (strCSVLine, ",");

				if (null != astrValue && 0 != astrValue.length) {
					if (bHeader)
						csvGrid.setHeader (astrValue);
					else
						csvGrid.add (astrValue);

					bHeader = false;
				}
			}

			return csvGrid;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
