
package org.drip.product.govvie;

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
 * TreasuryComponent implements the Functionality behind a Sovereign/Treasury Bond/Bill/Note.
 *
 * @author Lakshmi Krishnamurthy
 */

public class TreasuryComponent extends org.drip.product.credit.BondComponent {
	private java.lang.String _strTreasuryCode = "";

	/**
	 * TreasuryComponent Constructor
	 * 
	 * @param strTreasuryCode Treasury Code
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TreasuryComponent (
		final java.lang.String strTreasuryCode)
		throws java.lang.Exception
	{
		super();

		if (null == (_strTreasuryCode = strTreasuryCode) || _strTreasuryCode.isEmpty())
			throw new java.lang.Exception ("TreasuryComponent ctr => Invalid Inputs");
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

	@Override public org.drip.product.calib.ProductQuoteSet calibQuoteSet (
		final org.drip.state.representation.LatentStateSpecification[] aLSS)
	{
		try {
			return new org.drip.product.calib.TreasuryBondQuoteSet (aLSS);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.state.estimator.PredictorResponseWeightConstraint govviePRWC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteContainer csqs,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final org.drip.product.calib.ProductQuoteSet pqs)
	{
		if (null == valParams || null == pqs || !(pqs instanceof
			org.drip.product.calib.TreasuryBondQuoteSet))
			return null;

		int iMaturityDate = maturityDate().julian();

		if (valParams.valueDate() > iMaturityDate) return null;

		double dblYield = java.lang.Double.NaN;
		org.drip.product.calib.TreasuryBondQuoteSet tbqs = (org.drip.product.calib.TreasuryBondQuoteSet) pqs;

		if (!tbqs.containsYield()) return null;

		try {
			dblYield = tbqs.yield();
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		org.drip.state.estimator.PredictorResponseWeightConstraint prwc = new
			org.drip.state.estimator.PredictorResponseWeightConstraint();

		if (!prwc.addPredictorResponseWeight (iMaturityDate, 1.)) return null;

		if (!prwc.addDResponseWeightDManifestMeasure ("Yield", iMaturityDate, 1.)) return null;

		if (!prwc.updateValue (dblYield)) return null;

		if (!prwc.updateDValueDManifestMeasure ("Yield", 1.)) return null;

		return prwc;
	}
}
