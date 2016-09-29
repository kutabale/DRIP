
package org.drip.service.state;

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
 * CreditCurveAPI computes the Metrics associated the Credit Curve State.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CreditCurveAPI {

	/**
	 * Generate the Horizon Metrics for the Specified Inputs
	 * 
	 * @param adtSpot Array of Horizon Dates
	 * @param astrFundingFixingMaturityTenor Array of the Funding Fixing Curve Calibration Instrument Tenors
	 * @param aadblFundingFixingQuote Array of the Funding Fixing Curve Calibration Instrument Quotes
	 * @param astrFullCreditIndexName Array of the Full Credit Index Names
	 * @param adblCreditIndexQuotedSpread Array of the Credit Index Quoted Spreads
	 * @param astrForTenor Array of the "For" Tenors
	 * 
	 * @return Map of the Dated Credit Curve Metrics
	 */

	public static final org.drip.historical.state.CreditCurveMetrics DailyMetrics (
		final org.drip.analytics.date.JulianDate dtSpot,
		final java.lang.String[] astrFundingFixingMaturityTenor,
		final double[] adblFundingFixingQuote,
		final java.lang.String strFullCreditIndexName,
		final double dblCreditIndexQuotedSpread,
		final java.lang.String[] astrForTenor)
	{
		if (null == dtSpot || null == astrFundingFixingMaturityTenor || null == adblFundingFixingQuote ||
			null == astrForTenor)
			return null;

		int iNumForTenor = astrForTenor.length;
		int iNumFundingFixingInstrument = astrFundingFixingMaturityTenor.length;

		if (0 == iNumFundingFixingInstrument || iNumFundingFixingInstrument != adblFundingFixingQuote.length
			|| 0 == iNumForTenor)
			return null;

		org.drip.market.otc.CreditIndexConvention cic =
			org.drip.market.otc.CreditIndexConventionContainer.ConventionFromFullName
				(strFullCreditIndexName);

		if (null == cic) return null;

		org.drip.state.discount.MergedDiscountForwardCurve dcFundingFixing =
			org.drip.service.template.LatentMarketStateBuilder.FundingCurve (dtSpot, cic.currency(), null,
				null, "ForwardRate", null, "ForwardRate", astrFundingFixingMaturityTenor,
					adblFundingFixingQuote, "SwapRate",
						org.drip.service.template.LatentMarketStateBuilder.SHAPE_PRESERVING);

		if (null == dcFundingFixing) return null;

		org.drip.state.credit.CreditCurve cc = org.drip.service.template.LatentMarketStateBuilder.CreditCurve
			(dtSpot, new org.drip.product.definition.CreditDefaultSwap[] {cic.indexCDS()}, new double[]
				{dblCreditIndexQuotedSpread}, "FairPremium", dcFundingFixing);

		if (null == cc) return null;

		try {
			org.drip.historical.state.CreditCurveMetrics ccm = new
				org.drip.historical.state.CreditCurveMetrics (dtSpot);

			for (int j = 0; j < iNumForTenor; ++j) {
				org.drip.analytics.date.JulianDate dtFor = dtSpot.addTenor (astrForTenor[j]);

				if (null == dtFor || !ccm.addSurvivalProbability (dtFor, cc.survival (dtFor)) ||
					!ccm.addRecoveryRate (dtFor, cc.recovery (dtFor)))
					continue;
			}

			return ccm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Horizon Metrics for the Specified Inputs
	 * 
	 * @param adtSpot Array of Horizon Dates
	 * @param astrFundingFixingMaturityTenor Array of the Funding Fixing Curve Calibration Instrument Tenors
	 * @param aadblFundingFixingQuote Array of the Funding Fixing Curve Calibration Instrument Quotes
	 * @param astrFullCreditIndexName Array of the Full Credit Index Names
	 * @param adblCreditIndexQuotedSpread Array of the Credit Index Quoted Spreads
	 * @param astrForTenor Array of the "For" Tenors
	 * 
	 * @return Map of the Dated Credit Curve Metrics
	 */

	public static final java.util.TreeMap<org.drip.analytics.date.JulianDate,
		org.drip.historical.state.CreditCurveMetrics> HorizonMetrics (
			final org.drip.analytics.date.JulianDate[] adtSpot,
			final java.lang.String[] astrFundingFixingMaturityTenor,
			final double[][] aadblFundingFixingQuote,
			final java.lang.String[] astrFullCreditIndexName,
			final double[] adblCreditIndexQuotedSpread,
			final java.lang.String[] astrForTenor)
	{
		if (null == adtSpot || null == astrFundingFixingMaturityTenor || null == aadblFundingFixingQuote ||
			null == astrFullCreditIndexName || null == adblCreditIndexQuotedSpread || null == astrForTenor)
			return null;

		int iNumSpot = adtSpot.length;
		int iNumForTenor = astrForTenor.length;
		int iNumFundingFixingInstrument = astrFundingFixingMaturityTenor.length;

		if (0 == iNumSpot || iNumSpot != aadblFundingFixingQuote.length || iNumSpot !=
			astrFullCreditIndexName.length || iNumSpot != adblCreditIndexQuotedSpread.length || 0 ==
				iNumFundingFixingInstrument || 0 == iNumForTenor)
			return null;

		java.util.TreeMap<org.drip.analytics.date.JulianDate, org.drip.historical.state.CreditCurveMetrics>
			mapCCM = new java.util.TreeMap<org.drip.analytics.date.JulianDate,
				org.drip.historical.state.CreditCurveMetrics>();

		for (int i = 0; i < iNumSpot; ++i) {
			org.drip.market.otc.CreditIndexConvention cic =
				org.drip.market.otc.CreditIndexConventionContainer.ConventionFromFullName
					(astrFullCreditIndexName[i]);

			if (null == cic) continue;

			org.drip.state.discount.MergedDiscountForwardCurve dcFundingFixing =
				org.drip.service.template.LatentMarketStateBuilder.FundingCurve (adtSpot[i], cic.currency(),
					null, null, "ForwardRate", null, "ForwardRate", astrFundingFixingMaturityTenor,
						aadblFundingFixingQuote[i], "SwapRate",
							org.drip.service.template.LatentMarketStateBuilder.SHAPE_PRESERVING);

			if (null == dcFundingFixing) continue;

			org.drip.state.credit.CreditCurve cc =
				org.drip.service.template.LatentMarketStateBuilder.CreditCurve (adtSpot[i], new
					org.drip.product.definition.CreditDefaultSwap[] {cic.indexCDS()}, new double[]
						{adblCreditIndexQuotedSpread[i]}, "FairPremium", dcFundingFixing);

			if (null == cc) continue;

			try {
				org.drip.historical.state.CreditCurveMetrics ccm = new
					org.drip.historical.state.CreditCurveMetrics (adtSpot[i]);

				for (int j = 0; j < iNumForTenor; ++j) {
					org.drip.analytics.date.JulianDate dtFor = adtSpot[i].addTenor (astrForTenor[j]);

					if (null == dtFor) continue;

					if (!ccm.addSurvivalProbability (dtFor, cc.survival (dtFor)) || !ccm.addRecoveryRate
						(dtFor, cc.recovery (dtFor)))
						continue;
				}

				mapCCM.put (adtSpot[i], ccm);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				continue;
			}
		}

		return mapCCM;
	}
}
