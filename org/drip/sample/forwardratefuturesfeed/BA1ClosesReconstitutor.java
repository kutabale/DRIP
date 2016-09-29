
package org.drip.sample.forwardratefuturesfeed;

import org.drip.feed.transformer.FundingFuturesClosesReconstitutor;
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
 * BA1ClosesReconstitutor Cleanses, Transforms, and Re-constitutes the Formatted BA1 Closes Feed.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BA1ClosesReconstitutor {

	public static final void main (
		final String[] args)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iSpotDateIndex = 0;
		int iLastPriceIndex = 2;
		int iFuturesExpiryDateIndex = 8;
		String strFeedLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Feeds\\FundingFutures\\BA1_ClosesFormatted.csv";

		FundingFuturesClosesReconstitutor.RegularizeCloses (
			strFeedLocation,
			iSpotDateIndex,
			iLastPriceIndex,
			iFuturesExpiryDateIndex
		);
	}
}
