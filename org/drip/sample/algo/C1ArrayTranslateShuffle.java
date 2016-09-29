
package org.drip.sample.algo;

import org.drip.service.env.EnvManager;
import org.drip.spaces.big.BigC1Array;

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
 * C1ArrayTranslateShuffle demonstrates the Functionality that conducts an in-place Translation and Shuffling
 *  of a Big String Instance.
 *
 * @author Lakshmi Krishnamurthy
 */

public class C1ArrayTranslateShuffle {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strBigString = "abcdefghijklmnopqrstuvwxyzABCDEFGIHJKLMNOPQRSTUVWXYZ0123456789!~@#$%^&*(){}[]<>,.?/";
		int iBigStringPivot = 35;
		int iBigStringBlock = 17;

		BigC1Array bs = new BigC1Array (strBigString.toCharArray());

		System.out.println();

		while (iBigStringPivot > 0) {
			if (iBigStringPivot < iBigStringBlock) iBigStringBlock = iBigStringPivot;

			bs.translateAtPivot (
				iBigStringPivot,
				iBigStringBlock
			);

			System.out.println ("\t" + strBigString + " => " + new java.lang.String (bs.charArray()));

			iBigStringPivot -= iBigStringBlock;
		}
	}
}
