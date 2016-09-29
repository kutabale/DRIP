
package org.drip.sample.algo;

import org.drip.quant.common.*;
import org.drip.spaces.big.BigR1Array;

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
 * R1ArrayInSituSort demonstrates the Functionality that conducts an in-place Sorting of an Instance of
 * 	BigDoubleArray using a variety of Sorting Algorithms.
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1ArrayInSituSort {

	private static void QuickSort (
		final int iNumRandom,
		final String strPrefix)
		throws Exception
	{
		double[] adblA = new double[iNumRandom];
		double[] adblAOrig = new double[iNumRandom];

		for (int i = 0; i < iNumRandom; ++i) {
			adblA[i] = Math.random();

			adblAOrig[i] = adblA[i];
		}

		BigR1Array ba = new BigR1Array (adblA);

		ba.quickSort();

		System.out.println ("\n\t---------------------------------------------");

		for (int i = 0; i < iNumRandom; ++i)
			System.out.println (
				"\t||  " + strPrefix + "  " +
				FormatUtil.FormatDouble (adblAOrig[i], 1, 4, 1.) + " | " +
				FormatUtil.FormatDouble (adblA[i], 1, 4, 1.) + " ||"
			);

		System.out.println ("\t---------------------------------------------");
	}

	private static void MergeSort (
		final int iNumRandom,
		final String strPrefix)
		throws Exception
	{
		double[] adblA = new double[iNumRandom];
		double[] adblAOrig = new double[iNumRandom];

		for (int i = 0; i < iNumRandom; ++i) {
			adblA[i] = Math.random();

			adblAOrig[i] = adblA[i];
		}

		BigR1Array ba = new BigR1Array (adblA);

		ba.mergeSort();

		System.out.println ("\n\t---------------------------------------------");

		for (int i = 0; i < iNumRandom; ++i)
			System.out.println (
				"\t||  " + strPrefix + "  " +
				FormatUtil.FormatDouble (adblAOrig[i], 1, 4, 1.) + " | " +
				FormatUtil.FormatDouble (adblA[i], 1, 4, 1.) + " ||"
			);

		System.out.println ("\t---------------------------------------------");
	}

	private static void HeapSort (
		final int iNumRandom,
		final String strPrefix)
		throws Exception
	{
		double[] adblA = new double[iNumRandom];
		double[] adblAOrig = new double[iNumRandom];

		for (int i = 0; i < iNumRandom; ++i) {
			adblA[i] = Math.random();

			adblAOrig[i] = adblA[i];
		}

		BigR1Array ba = new BigR1Array (adblA);

		ba.heapSort();

		System.out.println ("\n\t---------------------------------------------");

		for (int i = 0; i < iNumRandom; ++i)
			System.out.println (
				"\t||  " + strPrefix + "  " +
				FormatUtil.FormatDouble (adblAOrig[i], 1, 4, 1.) + " | " +
				FormatUtil.FormatDouble (adblA[i], 1, 4, 1.) + " ||"
			);

		System.out.println ("\t---------------------------------------------");
	}

	private static void InsertionSort (
		final int iNumRandom,
		final String strPrefix)
		throws Exception
	{
		double[] adblA = new double[iNumRandom];
		double[] adblAOrig = new double[iNumRandom];

		for (int i = 0; i < iNumRandom; ++i) {
			adblA[i] = Math.random();

			adblAOrig[i] = adblA[i];
		}

		System.out.println ("\n\t---------------------------------------------");

		NumberUtil.Print1DArray (
			"\t|  ORIGINAL  ",
			adblA,
			6,
			false
		);

		System.out.println ("\t---------------------------------------------");

		BigR1Array ba = new BigR1Array (adblA);

		ba.insertionSort();

		System.out.println ("\n\t---------------------------------------------");

		for (int i = 0; i < iNumRandom; ++i)
			System.out.println (
				"\t||  " + strPrefix + "  " +
				FormatUtil.FormatDouble (adblAOrig[i], 1, 4, 1.) + " | " +
				FormatUtil.FormatDouble (adblA[i], 1, 4, 1.) + " ||"
			);

		System.out.println ("\t---------------------------------------------");
	}

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		int iNumRandom = 50;

		QuickSort (
			iNumRandom,
			"QUICKSORT"
		);

		MergeSort (
			iNumRandom,
			"MERGESORT"
		);

		InsertionSort (
			iNumRandom,
			"INSERTIONSORT"
		);

		HeapSort (
			iNumRandom,
			"HEAPSORT"
		);
	}
}
