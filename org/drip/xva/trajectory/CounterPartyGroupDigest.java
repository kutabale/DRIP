
package org.drip.xva.trajectory;

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
 * CounterPartyGroupDigest holds the "thin" Statistics of the Aggregations across Multiple Path Projection
 *  Runs along the Granularity of a Counter Party Group (i.e., across multiple Netting groups). The
 *  References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 *  
 *  - Li, B., and Y. Tang (2007): Quantitative Analysis, Derivatives Modeling, and Trading Strategies in the
 *  	Presence of Counter-party Credit Risk for the Fixed Income Market, World Scientific Publishing,
 *  	Singapore.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CounterPartyGroupDigest {
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTCollateralizedExposure = null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTUncollateralizedExposure = null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTCollateralizedExposurePV = null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTUncollateralizedExposurePV = null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTCollateralizedPositiveExposure = null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTCollateralizedNegativeExposure = null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTUncollateralizedPositiveExposure =
		null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTUncollateralizedNegativeExposure =
		null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTCollateralizedPositiveExposurePV =
		null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTCollateralizedNegativeExposurePV =
		null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTUncollateralizedPositiveExposurePV =
		null;
	private org.drip.measure.statistics.UnivariateDiscreteThin[] _aUDTUncollateralizedNegativeExposurePV =
		null;

	/**
	 * CounterPartyGroupDigest Constructor
	 * 
	 * @param aadblCollateralizedExposure Double Array of Collateralized Exposure
	 * @param aadblCollateralizedExposurePV Double Array of Collateralized Exposure PV
	 * @param aadblCollateralizedPositiveExposure Double Array of Collateralized Positive Exposure
	 * @param aadblCollateralizedPositiveExposurePV Double Array of Collateralized Positive Exposure PV
	 * @param aadblCollateralizedNegativeExposure Double Array of Collateralized Negative Exposure
	 * @param aadblCollateralizedNegativeExposurePV Double Array of Collateralized Negative Exposure PV
	 * @param aadblUncollateralizedExposure Double Array of Uncollateralized Exposure
	 * @param aadblUncollateralizedExposurePV Double Array of Uncollateralized Exposure PV
	 * @param aadblUncollateralizedPositiveExposure Double Array of Uncollateralized Positive Exposure
	 * @param aadblUncollateralizedPositiveExposurePV Double Array of Uncollateralized Positive Exposure PV
	 * @param aadblUncollateralizedNegativeExposure Double Array of Uncollateralized Negative Exposure
	 * @param aadblUncollateralizedNegativeExposurePV Double Array of Uncollateralized Negative Exposure PV
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CounterPartyGroupDigest (
		final double[][] aadblCollateralizedExposure,
		final double[][] aadblCollateralizedExposurePV,
		final double[][] aadblCollateralizedPositiveExposure,
		final double[][] aadblCollateralizedPositiveExposurePV,
		final double[][] aadblCollateralizedNegativeExposure,
		final double[][] aadblCollateralizedNegativeExposurePV,
		final double[][] aadblUncollateralizedExposure,
		final double[][] aadblUncollateralizedExposurePV,
		final double[][] aadblUncollateralizedPositiveExposure,
		final double[][] aadblUncollateralizedPositiveExposurePV,
		final double[][] aadblUncollateralizedNegativeExposure,
		final double[][] aadblUncollateralizedNegativeExposurePV)
		throws java.lang.Exception
	{
		if (null == aadblCollateralizedExposure || null == aadblCollateralizedExposurePV || null ==
			aadblCollateralizedPositiveExposure || null == aadblCollateralizedPositiveExposurePV || null ==
				aadblCollateralizedNegativeExposure || null == aadblCollateralizedNegativeExposurePV || null
					== aadblUncollateralizedExposure || null == aadblUncollateralizedExposurePV || null ==
						aadblUncollateralizedPositiveExposure || null ==
							aadblUncollateralizedPositiveExposurePV || null ==
								aadblUncollateralizedNegativeExposure || null ==
									aadblUncollateralizedNegativeExposurePV)
			throw new java.lang.Exception ("CounterPartyGroupDigest Constructor => Invalid Inputs");

		int iNumVertex = aadblCollateralizedExposure.length;
		_aUDTCollateralizedExposure = new org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTUncollateralizedExposure = new org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTCollateralizedExposurePV = new org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTUncollateralizedExposurePV = new org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTCollateralizedPositiveExposure = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTCollateralizedPositiveExposurePV = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTCollateralizedNegativeExposure = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTCollateralizedNegativeExposurePV = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTUncollateralizedPositiveExposure = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTUncollateralizedPositiveExposurePV = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTUncollateralizedNegativeExposure = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];
		_aUDTUncollateralizedNegativeExposurePV = new
			org.drip.measure.statistics.UnivariateDiscreteThin[iNumVertex];

		if (0 == iNumVertex || iNumVertex != aadblCollateralizedExposurePV.length || iNumVertex !=
			aadblCollateralizedPositiveExposure.length || iNumVertex !=
				aadblCollateralizedPositiveExposurePV.length || iNumVertex !=
					aadblCollateralizedNegativeExposure.length || iNumVertex !=
						aadblCollateralizedNegativeExposurePV.length || iNumVertex !=
							aadblUncollateralizedExposure.length || iNumVertex !=
								aadblUncollateralizedExposurePV.length || iNumVertex !=
									aadblUncollateralizedPositiveExposure.length || iNumVertex !=
										aadblCollateralizedPositiveExposurePV.length || iNumVertex !=
											aadblCollateralizedNegativeExposurePV.length || iNumVertex !=
												aadblCollateralizedNegativeExposurePV.length)
			throw new java.lang.Exception ("CounterPartyGroupDigest Constructor => Invalid Inputs");

		for (int i = 0 ; i < iNumVertex; ++i) {
			_aUDTCollateralizedExposure[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblCollateralizedExposure[i]);

			_aUDTCollateralizedExposurePV[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblCollateralizedExposurePV[i]);

			_aUDTCollateralizedPositiveExposure[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblCollateralizedPositiveExposure[i]);

			_aUDTCollateralizedPositiveExposurePV[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblCollateralizedPositiveExposurePV[i]);

			_aUDTCollateralizedNegativeExposure[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblCollateralizedNegativeExposure[i]);

			_aUDTCollateralizedNegativeExposurePV[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblCollateralizedNegativeExposurePV[i]);

			_aUDTUncollateralizedExposure[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblUncollateralizedExposure[i]);

			_aUDTUncollateralizedExposurePV[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblUncollateralizedExposurePV[i]);

			_aUDTUncollateralizedPositiveExposure[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblUncollateralizedPositiveExposure[i]);

			_aUDTUncollateralizedPositiveExposurePV[i] = new
				org.drip.measure.statistics.UnivariateDiscreteThin
					(aadblUncollateralizedPositiveExposurePV[i]);

			_aUDTUncollateralizedNegativeExposure[i] = new org.drip.measure.statistics.UnivariateDiscreteThin
				(aadblUncollateralizedNegativeExposure[i]);

			_aUDTUncollateralizedNegativeExposurePV[i] = new
				org.drip.measure.statistics.UnivariateDiscreteThin
					(aadblUncollateralizedNegativeExposurePV[i]);
		}
	}

	/**
	 * Retrieve the Univariate This Statistics for the Collateralized Exposure
	 * 
	 * @return Univariate This Statistics for the Collateralized Exposure
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] collateralizedExposure()
	{
		return _aUDTCollateralizedExposure;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Collateralized Exposure PV
	 * 
	 * @return Univariate This Statistics for the Collateralized Exposure PV
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] collateralizedExposurePV()
	{
		return _aUDTCollateralizedExposurePV;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Collateralized Positive Exposure
	 * 
	 * @return Univariate This Statistics for the Collateralized Positive Exposure
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] collateralizedPositiveExposure()
	{
		return _aUDTCollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Collateralized Positive Exposure PV
	 * 
	 * @return Univariate This Statistics for the Collateralized Positive Exposure PV
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] collateralizedPositiveExposurePV()
	{
		return _aUDTCollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Collateralized Negative Exposure
	 * 
	 * @return Univariate This Statistics for the Collateralized Negative Exposure
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] collateralizedNegativeExposure()
	{
		return _aUDTCollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Collateralized Negative Exposure PV
	 * 
	 * @return Univariate This Statistics for the Collateralized Negative Exposure PV
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] collateralizedNegativeExposurePV()
	{
		return _aUDTCollateralizedNegativeExposurePV;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Uncollateralized Exposure
	 * 
	 * @return Univariate This Statistics for the Uncollateralized Exposure
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] uncollateralizedExposure()
	{
		return _aUDTUncollateralizedExposure;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Uncollateralized Exposure PV
	 * 
	 * @return Univariate This Statistics for the Uncollateralized Exposure PV
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] uncollateralizedExposurePV()
	{
		return _aUDTUncollateralizedExposurePV;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Uncollateralized Positive Exposure
	 * 
	 * @return Univariate This Statistics for the Uncollateralized Positive Exposure
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] uncollateralizedPositiveExposure()
	{
		return _aUDTUncollateralizedPositiveExposure;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Uncollateralized Positive Exposure PV
	 * 
	 * @return Univariate This Statistics for the Uncollateralized Positive Exposure PV
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] uncollateralizedPositiveExposurePV()
	{
		return _aUDTUncollateralizedPositiveExposurePV;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Uncollateralized Negative Exposure
	 * 
	 * @return Univariate This Statistics for the Uncollateralized Negative Exposure
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] uncollateralizedNegativeExposure()
	{
		return _aUDTUncollateralizedNegativeExposure;
	}

	/**
	 * Retrieve the Univariate This Statistics for the Uncollateralized Negative Exposure PV
	 * 
	 * @return Univariate This Statistics for the Uncollateralized Negative Exposure PV
	 */

	public org.drip.measure.statistics.UnivariateDiscreteThin[] uncollateralizedNegativeExposurePV()
	{
		return _aUDTUncollateralizedNegativeExposurePV;
	}
}
