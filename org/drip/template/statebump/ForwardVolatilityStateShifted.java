
package org.drip.template.statebump;

import java.util.Map;

import org.drip.analytics.date.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.definition.Component;
import org.drip.product.fra.FRAStandardCapFloor;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.*;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.volatility.VolatilityCurve;

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
 * ForwardVolatilityStateShifted demonstrates the Generation and the Usage of Tenor Bumped Forward Volatility
 * 	Curves.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ForwardVolatilityStateShifted {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strFRATenor = "3M";
		String strCurrency = "GBP";
		double dblBump = 0.0000001;
		boolean bIsBumpProportional = false;

		JulianDate dtSpot = DateUtil.Today().addBusDays (
			0,
			strCurrency
		);

		ForwardLabel forwardLabel = ForwardLabel.Create (
			strCurrency,
			strFRATenor
		);

		MergedDiscountForwardCurve dcFunding = LatentMarketStateBuilder.SmoothFundingCurve (
			dtSpot,
			strCurrency,
			new String[] {
				 "30D",
				 "60D",
				 "91D",
				"182D",
				"273D"
			},
			new double[] {
				0.0668750,	//  30D
				0.0675000,	//  60D
				0.0678125,	//  91D
				0.0712500,	// 182D
				0.0750000	// 273D
			},
			"ForwardRate",
			null,
			"ForwardRate",
			new String[] {
				"2Y",
				"3Y",
				"4Y",
				"5Y",
				"7Y",
				"10Y"
			},
			new double[] {
				0.08265,    //  2Y
				0.08550,    //  3Y
				0.08655,    //  4Y
				0.08770,    //  5Y
				0.08910,    //  7Y
				0.08920     // 10Y
			},
			"SwapRate"
		);

		String[] astrMaturityTenor = new String[] {
			"01Y",
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"07Y",
			"10Y"
		};

		double[] adblStrike = new double[] {
			0.0788, //  "1Y",
			0.0839, // 	"2Y",
			0.0864, //  "3Y",
			0.0869, //  "4Y",
			0.0879, //  "5Y",
			0.0890, //  "7Y",
			0.0889  // "10Y"
		};

		double[] adblPrice = new double[] {
			0.0037, //  "1Y",
			0.0062, // 	"2Y",
			0.0124, //  "3Y",
			0.0203, //  "4Y",
			0.0301, //  "5Y",
			0.0528, //  "7Y"
			0.0855  // "10Y"
		};

		ForwardCurve fc = dcFunding.nativeForwardCurve (strFRATenor);

		Map<String, VolatilityCurve> bumpedForwardVolatilityCurve = LatentMarketStateBuilder.BumpedForwardVolatilityCurve (
			dtSpot,
			forwardLabel,
			true,
			astrMaturityTenor,
			adblStrike,
			adblPrice,
			"Price",
			dcFunding,
			fc,
			dblBump,
			bIsBumpProportional
		);

		FRAStandardCapFloor[] aFRACapFloor = OTCInstrumentBuilder.CapFloor (
			dtSpot,
			forwardLabel,
			astrMaturityTenor,
			adblStrike,
			true
		);

		System.out.println ("\n\t|---------------------------------------------------------------------------------------------------------------||");

		ValuationParams valParams = ValuationParams.Spot (dtSpot.julian());

		for (Map.Entry<String, VolatilityCurve> meForwardVolatility : bumpedForwardVolatilityCurve.entrySet()) {
			String strKey = meForwardVolatility.getKey();

			if (!strKey.startsWith ("capfloor")) continue;

			CurveSurfaceQuoteContainer csqc = new CurveSurfaceQuoteContainer();

			csqc.setFundingState (dcFunding);

			csqc.setForwardState (fc);

			csqc.setForwardVolatility (meForwardVolatility.getValue());

			System.out.print ("\t|  [" + meForwardVolatility.getKey() + "] => ");

			for (Component comp : aFRACapFloor)
				System.out.print (FormatUtil.FormatDouble (comp.measureValue (
					valParams,
					null,
					csqc,
					null,
					"Price"
				), 1, 8, 1.) + " |");

			System.out.print ("|\n");
		}

		System.out.println ("\t|---------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\n\t|-------------------------------------------------||");

		CurveSurfaceQuoteContainer csqcBase = new CurveSurfaceQuoteContainer();

		csqcBase.setFundingState (dcFunding);

		csqcBase.setForwardState (fc);

		csqcBase.setForwardVolatility (bumpedForwardVolatilityCurve.get ("Base"));

		CurveSurfaceQuoteContainer csqcBump = new CurveSurfaceQuoteContainer();

		csqcBump.setFundingState (dcFunding);

		csqcBump.setForwardState (fc);

		csqcBump.setForwardVolatility (bumpedForwardVolatilityCurve.get ("Bump"));

		for (Component comp : aFRACapFloor)
			System.out.println (
				"\t| PRICE => " +
				comp.maturityDate() + " | " +
				FormatUtil.FormatDouble (comp.measureValue (
					valParams,
					null,
					csqcBase,
					null,
					"Price"
				), 1, 8, 1.) + " | " +
				FormatUtil.FormatDouble (comp.measureValue (
					valParams,
					null,
					csqcBump,
					null,
					"Price"
				), 1, 8, 1.) + " ||"
			);

		System.out.println ("\t|-------------------------------------------------||");
	}
}
