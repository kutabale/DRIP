
package org.drip.sample.algo;

import java.util.*;

import org.drip.service.env.EnvManager;
import org.drip.spaces.big.SubStringSetExtractor;

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
 * SubStringSetExtraction demonstrates the Extraction of Permuted and Contiguous Sub-string Sets.
 *
 * @author Lakshmi Krishnamurthy
 */

public class SubStringSetExtraction {

	public static void main (
		final String[] astrArgs)
	{
		EnvManager.InitEnv ("");

		String strMaster = "abc";

		List<String> lsTarget = new ArrayList<String>();

		for (int i = 1; i <= strMaster.length(); ++i)
			lsTarget.addAll (SubStringSetExtractor.ExhaustivePermutationScan (strMaster, i));

		System.out.println ();

		System.out.println ("Permuted Set: " + lsTarget);

		System.out.println ("Permuted String Set Size: " + lsTarget.size());

		System.out.println ("Contiguous Set: " + SubStringSetExtractor.Contiguous (strMaster));

		System.out.println ();
	}
}
