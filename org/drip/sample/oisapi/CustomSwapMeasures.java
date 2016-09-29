
package org.drip.sample.oisapi;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.service.product.OvernightIndexSwapAPI;

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
 * CustomSwapMeasures demonstrates the Invocation and Usage of the OIS API.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CustomSwapMeasures {

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		JulianDate dtSpot = DateUtil.Today();

		String strOISCurrency = "USD";
		String strOISTenor = "3W";
		double dblOISCoupon = 0.0043;

		String[] astrOvernightCurveDepositTenor = new String[] {
			"1D"
		};

		double[] adblOvernightCurveDepositQuote = new double[] {
			0.0010
		};

		String[] astrOvernightCurveOISTenor = new String[] {
			"1W",
			"2W",
			"3W",
			"1M",
			"2M",
			"3M",
			"4M",
			"5M",
			"6M",
			"9M",
			"1Y",
			"18M",
			"2Y",
			"3Y",
			"4Y",
			"5Y"
		};

		double[] adblOvernightCurveOISQuote = new double[] {
			0.0020,	// 1W
			0.0028,	// 2W
			0.0043,	// 3W
			0.0064,	// 1M
			0.0086,	// 2M
			0.0109,	// 3M
			0.0133,	// 4M
			0.0154,	// 5M
			0.0171,	// 6M
			0.0210,	// 9M
			0.0231,	// 1Y
			0.0234,	// 18M
			0.0235,	// 2Y
			0.0235,	// 3Y
			0.0237,	// 4Y
			0.0240	// 5Y
		};

		Map<String, Double> mapMeasures = OvernightIndexSwapAPI.ValuationMetrics (
			strOISCurrency,
			strOISTenor,
			dblOISCoupon,
			dtSpot.julian(),
			astrOvernightCurveDepositTenor,
			adblOvernightCurveDepositQuote,
			astrOvernightCurveOISTenor,
			adblOvernightCurveOISQuote,
			false
		);

		Set<String> setstrKeys = mapMeasures.keySet();

		for (String strKey : setstrKeys)
			System.out.println ("\t" + strKey + " => " + mapMeasures.get (strKey));
	}
}
