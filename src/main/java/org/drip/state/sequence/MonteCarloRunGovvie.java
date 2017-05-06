
package org.drip.state.sequence;

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
 * MonteCarloRunGovvie exposes the Functionality to generate a Sequence of Vertex Govvie Curves.
 *
 * @author Lakshmi Krishnamurthy
 */

public class MonteCarloRunGovvie extends org.drip.state.sequence.MonteCarloRun {
	private double[] _adblTreasuryYield = null;
	private double[] _adblTreasuryCoupon = null;
	private java.lang.String[] _astrTenor = null;
	private java.lang.String _strTreasuryCode = "";
	private org.drip.analytics.date.JulianDate _dtSpot = null;
	private org.drip.state.curve.BasisSplineGovvieYield _bsgyGround = null;

	private static final org.drip.state.curve.BasisSplineGovvieYield GovvieCurve (
		final org.drip.analytics.date.JulianDate dtSpot,
		final java.lang.String strCode,
		final java.lang.String[] astrTenor,
		final double[] adblCoupon,
		final double[] adblYield)
		throws Exception
	{
		int iNumInstrument = astrTenor.length;
		org.drip.analytics.date.JulianDate[] adtMaturity = new
			org.drip.analytics.date.JulianDate[iNumInstrument];
		org.drip.analytics.date.JulianDate[] adtEffective = new
			org.drip.analytics.date.JulianDate[iNumInstrument];

		for (int i = 0; i < iNumInstrument; ++i)
			adtMaturity[i] = (adtEffective[i] = dtSpot).addTenor (astrTenor[i]);

		return (org.drip.state.curve.BasisSplineGovvieYield)
			org.drip.service.template.LatentMarketStateBuilder.GovvieCurve (strCode, dtSpot, adtEffective,
				adtMaturity, adblCoupon, adblYield, "Yield",
					org.drip.service.template.LatentMarketStateBuilder.SHAPE_PRESERVING);
	}

	/**
	 * MonteCarloRunGovvie Constructor
	 * 
	 * @param dtSpot The Spot Date
	 * @param strTreasuryCode The Treasury Code
	 * @param astrTenor Array of Maturity Tenors
	 * @param adblTreasuryCoupon Array of Treasury Coupon
	 * @param adblTreasuryYield Array of Treasury Yield
	 * @param cpvd Latent State Evolver CPVD Instance
	 * @param aDE Array of the Latent State Diffusion Evolvers
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MonteCarloRunGovvie (
		final org.drip.analytics.date.JulianDate dtSpot,
		final java.lang.String strTreasuryCode,
		final java.lang.String[] astrTenor,
		final double[] adblTreasuryCoupon,
		final double[] adblTreasuryYield,
		final org.drip.measure.discrete.CorrelatedPathVertexDimension cpvd,
		final org.drip.measure.process.DiffusionEvolver[] aDE)
		throws java.lang.Exception
	{
		super (cpvd, aDE);

		if (null == (_bsgyGround = GovvieCurve (_dtSpot = dtSpot, _strTreasuryCode = strTreasuryCode,
			_astrTenor = astrTenor, _adblTreasuryCoupon = adblTreasuryCoupon, _adblTreasuryYield =
				adblTreasuryYield)))
			throw new java.lang.Exception ("MonteCarloRunGovvie Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return The Spot Date
	 */

	public org.drip.analytics.date.JulianDate spot()
	{
		return _dtSpot;
	}

	/**
	 * Retrieve the Treasury Code
	 * 
	 * @return The Treasury Code
	 */

	public java.lang.String code()
	{
		return _strTreasuryCode;
	}

	/**
	 * Retrieve the Treasury Maturity Tenor Array
	 * 
	 * @return The Treasury Maturity Tenor Array
	 */

	public java.lang.String[] tenors()
	{
		return _astrTenor;
	}

	/**
	 * Retrieve the Calibration Treasury Coupon Array
	 * 
	 * @return The Calibration Treasury Coupon Array
	 */

	public double[] coupon()
	{
		return _adblTreasuryCoupon;
	}

	/**
	 * Retrieve the Calibration Treasury Yield Array
	 * 
	 * @return The Calibration Treasury Yield Array
	 */

	public double[] yield()
	{
		return _adblTreasuryYield;
	}

	/**
	 * Generate the R^d Path/Vertex Govvie Curves using the Initial R^d and the Evolution Time Width
	 * 
	 * @param dblTimeIncrement The Evolution Time Width
	 * 
	 * @return The R^d Path//Vertex Govvie Curves
	 */

	public org.drip.state.govvie.GovvieCurve[][] pathVertex (
		final double dblTimeIncrement)
	{
		double[][][] aaadblPathForward = pathVertex (_bsgyGround.flatForward (_astrTenor).nodeValues(),
			dblTimeIncrement);

		if (null == aaadblPathForward) return null;

		org.drip.measure.discrete.CorrelatedPathVertexDimension cpvd = cpvd();

		int iNumPath = cpvd.numPath();

		int iNumVertex = cpvd.numVertex();

		org.drip.state.nonlinear.FlatForwardGovvieCurve[][] aaFFGC = new
			org.drip.state.nonlinear.FlatForwardGovvieCurve[iNumPath][iNumVertex];

		for (int iTimeVertex = 0; iTimeVertex < iNumVertex; ++iTimeVertex) {
			org.drip.analytics.date.JulianDate dtEvent = _dtSpot.addYears (iTimeVertex + 1);

			if (null == dtEvent) return null;

			int iEventDate = dtEvent.julian();

			int[] aiDate = org.drip.analytics.support.Helper.TenorToDate (dtEvent, _astrTenor);

			for (int iPath = 0; iPath < iNumPath; ++iPath) {
				try {
					if (null == (aaFFGC[iPath][iTimeVertex] = new
						org.drip.state.nonlinear.FlatForwardGovvieCurve (iEventDate, _strTreasuryCode,
							_bsgyGround.currency(), aiDate, aaadblPathForward[iPath][iTimeVertex])))
						return null;
				} catch (java.lang.Exception e) {
					e.printStackTrace();

					return null;
				}
			}
		}

		return aaFFGC;
	}
}
