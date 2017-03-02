
package org.drip.measure.discrete;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * QuadraticResampler Quadratically Re-samples the Input Points to Convert it to a Standard Normal.
 *
 * @author Lakshmi Krishnamurthy
 */

public class QuadraticResampler {
	private int _iBlockSize = -1;
	private boolean _bDebias = false;
	private boolean _bMeanCenter = false;

	/**
	 * QuadraticResampler Constructor
	 * 
	 * @param iBlockSize The Block Size of the Sampling
	 * @param bMeanCenter TRUE - The Sequence is to be Mean Centered
	 * @param bDebias TRUE - Remove the Sampling Bias
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public QuadraticResampler (
		final int iBlockSize,
		final boolean bMeanCenter,
		final boolean bDebias)
		throws java.lang.Exception
	{
		_bDebias = bDebias;
		_iBlockSize = iBlockSize;
		_bMeanCenter = bMeanCenter;
	}

	/**
	 * Retrieve the Block Size of the Sampling
	 * 
	 * @return The Block Size of the Sampling
	 */

	public int blockSize()
	{
		return _iBlockSize;
	}

	/**
	 * Indicate if the Sequence is to be Mean Centered
	 * 
	 * @return TRUE - The Sequence is to be Mean Centered
	 */

	public boolean meanCenter()
	{
		return _bMeanCenter;
	}

	/**
	 * Indicate if the Sampling Bias needs to be Removed
	 * 
	 * @return TRUE - The Sampling Bias needs to be Removed
	 */

	public boolean debias()
	{
		return _bDebias;
	}

	/**
	 * Transform the Input Sequence by applying Quadratic Sampling
	 * 
	 * @param adblSequence The Input Sequence
	 * 
	 * @return The Transformed Sequence
	 */

	public double[] transform (
		final double[] adblSequence)
	{
		if (null == adblSequence) return null;

		double dblMean = 0.;
		double dblVariance = 0.;
		int iSequenceSize = adblSequence.length;
		double[] adblTransfomedSequence = 0 == iSequenceSize ? null : new double[iSequenceSize];

		if (0 == iSequenceSize) return null;

		if (_bMeanCenter) {
			for (int i = 0; i < iSequenceSize; ++i)
				dblMean += adblSequence[i];

			dblMean = dblMean / iSequenceSize;
		}

		for (int i = 0; i < iSequenceSize; ++i) {
			double dblOffset = adblSequence[i] - dblMean;
			dblVariance += dblOffset * dblOffset;
		}

		dblVariance = dblVariance / (_bDebias ? iSequenceSize - 1 : iSequenceSize);

		for (int i = 0; i < iSequenceSize; ++i)
			adblTransfomedSequence[i] = adblSequence[i] / dblVariance;

		return adblTransfomedSequence;
	}

	/**
	 * Transform the Input Sequence Block-by-Block by applying Quadratic Sampling
	 * 
	 * @param adblSequence The Input Sequence
	 * 
	 * @return The Transformed Sequence
	 */

	public double[] transformBlock (
		final double[] adblSequence)
	{
		if (0 >= _iBlockSize) return transform (adblSequence);

		 return transform (adblSequence);
	}
}
