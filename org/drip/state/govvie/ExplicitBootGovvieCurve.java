
package org.drip.state.govvie;

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
 * ExplicitBootGovvieCurve exposes the Functionality associated with the bootstrapped Govvie Curve.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class ExplicitBootGovvieCurve extends org.drip.state.govvie.GovvieCurve implements
	org.drip.analytics.definition.ExplicitBootCurve {

	protected ExplicitBootGovvieCurve (
		final int iEpochDate,
		final java.lang.String strTreasuryCode,
		final java.lang.String strCurrency)
		throws java.lang.Exception
	{
		super (iEpochDate, strTreasuryCode, strCurrency);
	}

	@Override public boolean setCCIS (
		final org.drip.analytics.input.CurveConstructionInputSet ccis)
	{
		return null != (_ccis = ccis);
	}

	@Override public org.drip.product.definition.CalibratableComponent[] calibComp()
	{
		return null == _ccis ? null : _ccis.components();
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> manifestMeasure (
		final java.lang.String strInstrumentCode)
	{
		if (null == _ccis) return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
			mapQuote = _ccis.quoteMap();

		if (null == mapQuote || !mapQuote.containsKey (strInstrumentCode)) return null;

		return mapQuote.get (strInstrumentCode);
	}
}
