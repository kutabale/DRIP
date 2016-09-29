
package org.drip.sample.overnightfeed;

import org.drip.feed.transformer.OvernightIndexMarksReconstitutor;
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
 * NZDOISSmoothReconstitutor Demonstrates the Cleansing and the Smooth Re-constitution of the NZD Input OIS
 *  Marks.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class NZDOISSmoothReconstitutor {

	public static final void main (
		final String[] args)
	{
		EnvManager.InitEnv ("");

		String strCurrency = "NZD";
		String strFundingMarksLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\OvernightMarks\\" + strCurrency + "OISFormatted.csv";

		OvernightIndexMarksReconstitutor.ShapePreservingRegularization (
			strCurrency,
			strFundingMarksLocation
		);
	}
}
