
package org.drip.sample.treasuryfuturespnl;

import java.util.List;

import org.drip.analytics.date.JulianDate;
import org.drip.feed.loader.*;
import org.drip.historical.attribution.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.product.TreasuryFuturesAPI;

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
 * RX1Attribution demonstrates the Invocation of the Historical PnL Horizon PnL Attribution analysis for the
 *  RX1 Series.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RX1Attribution {

	public static final void main (
		final String[] args)
		throws Exception
	{
		EnvManager.InitEnv ("");

		String strPrintLocation = "C:\\DRIP\\CreditAnalytics\\Daemons\\Transforms\\TreasuryFuturesCloses\\RX1ClosesReconstitutor.csv";

		CSVGrid csvGrid = CSVParser.StringGrid (
			strPrintLocation,
			true
		);

		JulianDate[] adtSpot = csvGrid.dateArrayAtColumn (0);

		double[] adblConversionFactor = csvGrid.doubleArrayAtColumn (1);

		double[] adblCleanPrice = csvGrid.doubleArrayAtColumn (2);

		double[] adblCoupon = csvGrid.doubleArrayAtColumn (3);

		JulianDate[] adtEffective = csvGrid.dateArrayAtColumn (4);

		JulianDate[] adtMaturity = csvGrid.dateArrayAtColumn (5);

		JulianDate[] adtExpiry = csvGrid.dateArrayAtColumn (6);

		List<PositionChangeComponents> lsPCC = TreasuryFuturesAPI.HorizonChangeAttribution (
			"DBR",
			adtEffective,
			adtMaturity,
			adblCoupon,
			adtExpiry,
			adtSpot,
			adblCleanPrice,
			adblConversionFactor
		);

		System.out.println ("FirstDate, SecondDate, ExpiryDate, CTD Bond, Expiry Clean Price, Conversion Factor, 1D Gross PnL, 1D Market PnL, 1D Roll-down PnL, 1D Accrual PnL, 1D Explained PnL, 1D Unexplianed PnL");

		for (PositionChangeComponents pcc : lsPCC) {
			TreasuryFuturesMarketSnap tfpms = (TreasuryFuturesMarketSnap) pcc.pmsSecond();

			System.out.println (
				pcc.firstDate() + ", " +
				pcc.secondDate() + ", " +
				tfpms.expiryDate() + ", " +
				tfpms.ctdName() + ", " +
				FormatUtil.FormatDouble (tfpms.expiryCleanPrice(), 1, 5, 1.) + ", " +
				FormatUtil.FormatDouble (tfpms.conversionFactor(), 1, 5, 1.) + ", " +
				FormatUtil.FormatDouble (pcc.grossChange(), 2, 2, 10000.) + ", " +
				FormatUtil.FormatDouble (pcc.marketRealizationChange(), 2, 2, 10000.) + ", " +
				FormatUtil.FormatDouble (pcc.marketRollDownChange(), 2, 2, 10000.) + ", " +
				FormatUtil.FormatDouble (pcc.accrualChange(), 2, 2, 10000.) + ", " +
				FormatUtil.FormatDouble (pcc.explainedChange(), 2, 2, 10000.) + ", " +
				FormatUtil.FormatDouble (pcc.unexplainedChange(), 2, 2, 10000.)
			);
		}
	}
}
