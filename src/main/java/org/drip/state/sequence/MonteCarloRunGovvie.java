
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
	private double[] _adblForwardYieldGround = null;
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

	private org.drip.state.govvie.GovvieCurve[][] curveVertex (
		final double[][][] aaadblPathForward)
	{
		if (null == aaadblPathForward) return null;

		org.drip.measure.discrete.CorrelatedPathVertexDimension cpvd = cpvd();

		int iNumPath = cpvd.numPath();

		int iNumVertex = cpvd.numVertex();

		java.lang.String strCurrency = _bsgyGround.currency();

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
							strCurrency, aiDate, aaadblPathForward[iPath][iTimeVertex])))
						return null;
				} catch (java.lang.Exception e) {
					e.printStackTrace();

					return null;
				}
			}
		}

		return aaFFGC;
	}

	/**
	 * Generate a Standard Instance of MonteCarloRunGovvie
	 * 
	 * @param dtSpot The Spot Date
	 * @param strTreasuryCode The Treasury Code
	 * @param astrTenor Array of Maturity Tenors
	 * @param adblTreasuryCoupon Array of Treasury Coupon
	 * @param adblTreasuryYield Array of Treasury Yield
	 * @param cpvd Latent State Evolver CPVD Instance
	 * @param de The Latent State Diffusion Evolver
	 * 
	 * @return Standard Instance of MonteCarloRun
	 */

	public static final MonteCarloRunGovvie Standard (
		final org.drip.analytics.date.JulianDate dtSpot,
		final java.lang.String strTreasuryCode,
		final java.lang.String[] astrTenor,
		final double[] adblTreasuryCoupon,
		final double[] adblTreasuryYield,
		final org.drip.measure.discrete.CorrelatedPathVertexDimension cpvd,
		final org.drip.measure.process.DiffusionEvolver de)
	{
		if (null == cpvd || null == de) return null;

		int iNumDimension = cpvd.numDimension();

		org.drip.measure.process.DiffusionEvolver[] aDE = new
			org.drip.measure.process.DiffusionEvolver[iNumDimension];

		for (int iDimension = 0; iDimension < iNumDimension; ++iDimension)
			aDE[iDimension] = de;

		try {
			return new MonteCarloRunGovvie (dtSpot, strTreasuryCode, astrTenor, adblTreasuryCoupon,
				adblTreasuryYield, cpvd, aDE);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
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

		org.drip.state.nonlinear.FlatForwardDiscountCurve ffdcGround = _bsgyGround.flatForward (_astrTenor);

		if (null == ffdcGround || null == (_adblForwardYieldGround = ffdcGround.nodeValues()))
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
	 * Retrieve the Ground State Govvie Curve
	 * 
	 * @return The Ground State Govvie Curve
	 */

	public org.drip.state.curve.BasisSplineGovvieYield groundState()
	{
		return _bsgyGround;
	}

	/**
	 * Retrieve the Ground Forward Yield Array
	 * 
	 * @return The Ground Forward Yield Array
	 */

	public double[] groundForwardYield()
	{
		return _adblForwardYieldGround;
	}

	/**
	 * Generate the R^d Path/Vertex Govvie Curves using the Initial R^d and the Evolution Time Width
	 * 
	 * @param adblTimeIncrement Array of the Evolution Time Widths
	 * 
	 * @return The R^d Path//Vertex Govvie Curves
	 */

	public org.drip.state.govvie.GovvieCurve[][] pathVertex (
		final double[] adblTimeIncrement)
	{
		return curveVertex (pathVertex (_adblForwardYieldGround, adblTimeIncrement));
	}

	/**
	 * Generate the R^d Path/Vertex Govvie Curves using the Initial R^d and the Evolution Time Width
	 * 
	 * @param dblTimeIncrement The Evolution Time Widths
	 * 
	 * @return The R^d Path//Vertex Govvie Curves
	 */

	public org.drip.state.govvie.GovvieCurve[][] pathVertex (
		final double dblTimeIncrement)
	{
		return curveVertex (pathVertex (_adblForwardYieldGround, dblTimeIncrement));
	}

	/**
	 * Generate the R^d Path/Vertex Govvie Curves using the Initial R^d and the Array of Event Tenors
	 * 
	 * @param astrEventTenor The Array of Event Tenors
	 * 
	 * @return The R^d Path//Vertex Govvie Curves
	 */

	public org.drip.state.govvie.GovvieCurve[][] pathVertex (
		final java.lang.String[] astrEventTenor)
	{
		return curveVertex (pathVertex (_adblForwardYieldGround, astrEventTenor));
	}
}
