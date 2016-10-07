
package org.drip.sample.date;

import org.drip.analytics.date.DateUtil;
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
 * Fliegel van Flandern demonstrates Gregorian To-From Julian Date Conversion Functionality. The References are:
 * 
 * 	1) Fliegel, H. F., and T. C. van Flandern (1968): A Machine Algorithm for Processing Calendar Dates,
 * 		Communications of the ACM 11 657.
 * 
 * 	2) Fenton, D. (2001): Julian to Calendar Date Conversion,
 * 		http://mathforum.org/library/drmath/view/51907.html.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FliegelvanFlandernJulian {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iYear = 2001;
		int iMonth = 2;
		int iDate = 24;

		int iJulian = DateUtil.ToJulian (
			iYear,
			iMonth,
			iDate
		);

		System.out.println (
			"\tGregorian: [" + iYear + "/" +
			FormatUtil.FormatDouble (iMonth, 2, 0, 1., false) + "/" +
			FormatUtil.FormatDouble (iDate, 2, 0, 1., false) +
			"]; Julian: " + iJulian
		);

		System.out.println ("\tJulian: " + iJulian + "; Gregorian: [" + DateUtil.YYYYMMDD (iJulian) + "]");
	}
}
