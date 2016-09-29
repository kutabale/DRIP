
package org.drip.service.json;

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
 * ForwardRateFuturesProcessor Sets Up and Executes a JSON Based In/Out Forward Rate Futures Valuation
 *  Processor.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ForwardRateFuturesProcessor {

	/**
	 * JSON Based in/out Funding Futures Curve Metrics Thunker
	 * 
	 * @param jsonParameter JSON Funding Futures Request Parameters
	 * 
	 * @return JSON Funding Futures Curve Metrics Response
	 */

	@SuppressWarnings ("unchecked") static final org.drip.json.simple.JSONObject CurveMetrics (
		final org.drip.json.simple.JSONObject jsonParameter)
	{
		org.drip.state.discount.MergedDiscountForwardCurve dcFunding =
			org.drip.service.json.LatentStateProcessor.FundingCurve (jsonParameter);

		if (null == dcFunding) return null;

		org.drip.param.market.CurveSurfaceQuoteContainer csqc = new
			org.drip.param.market.CurveSurfaceQuoteContainer();

		if (!csqc.setFundingState (dcFunding)) return null;

		org.drip.analytics.date.JulianDate dtSpot = dcFunding.epoch();

		org.drip.product.rates.SingleStreamComponent futures =
			org.drip.service.template.ExchangeInstrumentBuilder.ForwardRateFutures (dtSpot.addTenor
				(org.drip.json.parser.Converter.StringEntry (jsonParameter, "FuturesEffectiveTenor")),
					dcFunding.currency());

		if (null == futures) return null;

		java.util.Map<java.lang.String, java.lang.Double> mapResult = futures.value
			(org.drip.param.valuation.ValuationParams.Spot (dtSpot.julian()), null, csqc, null);

		if (null == mapResult) return null;

		org.drip.json.simple.JSONObject jsonResponse = new org.drip.json.simple.JSONObject();

		for (java.util.Map.Entry<java.lang.String, java.lang.Double> me : mapResult.entrySet())
			jsonResponse.put (me.getKey(), me.getValue());

		org.drip.json.simple.JSONArray jsonCashFlowArray = new org.drip.json.simple.JSONArray();

		for (org.drip.analytics.cashflow.CompositePeriod cp : futures.couponPeriods()) {
			org.drip.json.simple.JSONObject jsonCashFlow = new org.drip.json.simple.JSONObject();

			try {
				jsonCashFlow.put ("StartDate", new org.drip.analytics.date.JulianDate
					(cp.startDate()).toString());

				jsonCashFlow.put ("EndDate", new org.drip.analytics.date.JulianDate
					(cp.endDate()).toString());

				jsonCashFlow.put ("PayDate", new org.drip.analytics.date.JulianDate
					(cp.payDate()).toString());

				jsonCashFlow.put ("FixingDate", new org.drip.analytics.date.JulianDate
					(cp.fxFixingDate()).toString());

				jsonCashFlow.put ("CouponDCF", cp.couponDCF());

				jsonCashFlow.put ("PayDiscountFactor", cp.df (csqc));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			jsonCashFlow.put ("BaseNotional", cp.baseNotional());

			jsonCashFlow.put ("Tenor", cp.tenor());

			jsonCashFlow.put ("FundingLabel", cp.fundingLabel().fullyQualifiedName());

			jsonCashFlow.put ("ForwardLabel", cp.forwardLabel().fullyQualifiedName());

			jsonCashFlow.put ("ReferenceRate", cp.couponMetrics (dtSpot.julian(), csqc).rate());

			jsonCashFlowArray.add (jsonCashFlow);
		}

		jsonResponse.put ("FloatingCashFlow", jsonCashFlowArray);

		return jsonResponse;
	}
}
