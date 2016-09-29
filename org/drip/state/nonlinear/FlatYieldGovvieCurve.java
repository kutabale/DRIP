
package org.drip.state.nonlinear;

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
 * FlatYieldGovvieCurve manages the Govvie Latent State, using the Flat Yield as the State Response
 *  Representation.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FlatYieldGovvieCurve extends org.drip.state.govvie.ExplicitBootGovvieCurve {
	private int[] _aiDate = null;
	private double[] _adblYield = null;

	/**
	 * Construct a Govvie curve from an array of dates and Yields
	 * 
	 * @param iEpochDate Epoch Date
	 * @param strTreasuryCode Treasury Code
	 * @param strCurrency Currency
	 * @param aiDate Array of Dates
	 * @param adblYield Array of Yields
	 * 
	 * @throws java.lang.Exception Thrown if the curve cannot be created
	 */

	public FlatYieldGovvieCurve (
		final int iEpochDate,
		final java.lang.String strTreasuryCode,
		final java.lang.String strCurrency,
		final int[] aiDate,
		final double[] adblYield)
		throws java.lang.Exception
	{
		super (iEpochDate, strTreasuryCode, strCurrency);

		if (null == (_aiDate = aiDate) || null == (_adblYield = adblYield))
			throw new java.lang.Exception ("FlatYieldGovvieCurve Constructor => Invalid Inputs!");

		int iNumNode = _aiDate.length;

		if (0 == iNumNode || iNumNode != _adblYield.length)
			throw new java.lang.Exception ("FlatYieldGovvieCurve Constructor => Invalid Inputs!");
	}

	@Override public double yield (
		final int iDate)
		throws java.lang.Exception
	{
		if (iDate <= _iEpochDate) return _adblYield[0];

		int iNumDate = _aiDate.length;

		for (int i = 1; i < iNumDate; ++i) {
			if (iDate > _aiDate[i - 1] && iDate <= _aiDate[i]) return _adblYield[i];
		}

		return _adblYield[iNumDate - 1];
	}

	@Override public boolean setNodeValue (
		final int iNodeIndex,
		final double dblValue)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValue)) return false;

		int iNumDate = _aiDate.length;

		if (iNodeIndex > iNumDate) return false;

		for (int i = iNodeIndex; i < iNumDate; ++i)
			_adblYield[i] = dblValue;

		return true;
	}

	@Override public boolean bumpNodeValue (
		final int iNodeIndex,
		final double dblValue)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValue)) return false;

		int iNumDate = _aiDate.length;

		if (iNodeIndex > iNumDate) return false;

		for (int i = iNodeIndex; i < iNumDate; ++i)
			_adblYield[i] += dblValue;

		return true;
	}

	@Override public boolean setFlatValue (
		final double dblValue)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValue)) return false;

		int iNumDate = _aiDate.length;

		for (int i = 0; i < iNumDate; ++i)
			_adblYield[i] = dblValue;

		return true;
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDForwardDManifestMeasure (
		final java.lang.String strManifestMeasure,
		final int iDate)
	{
		return null;
	}
}
