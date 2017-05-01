
package org.drip.sample.discountmc;

import java.util.List;

import org.drip.analytics.date.*;
import org.drip.analytics.support.Helper;
import org.drip.measure.crng.RandomNumberGenerator;
import org.drip.measure.discrete.*;
import org.drip.measure.dynamics.DiffusionEvaluatorLogarithmic;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.JumpDiffusionVertex;
import org.drip.measure.realization.UnitRandom;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.state.curve.BasisSplineGovvieYield;
import org.drip.state.govvie.GovvieCurve;

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
 * TreasuryPath demonstrates the (Re-)construction of Treasury Curves from the Simulations of the Treasury
 * 	Forward Nodes.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TreasuryPath {

	private static final GovvieCurve GovvieCurve (
		final JulianDate dtSpot,
		final String strCode,
		final String[] astrTenor,
		final double[] adblCoupon,
		final double[] adblYield)
		throws Exception
	{
		JulianDate[] adtMaturity = new JulianDate[astrTenor.length];
		JulianDate[] adtEffective = new JulianDate[astrTenor.length];

		for (int i = 0; i < astrTenor.length; ++i) {
			adtEffective[i] = dtSpot;

			adtMaturity[i] = dtSpot.addTenor (astrTenor[i]);
		}

		return LatentMarketStateBuilder.GovvieCurve (
			strCode,
			dtSpot,
			adtEffective,
			adtMaturity,
			adblCoupon,
			adblYield,
			"Yield",
			LatentMarketStateBuilder.SHAPE_PRESERVING
		);
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		JulianDate dtSpot = DateUtil.CreateFromYMD (
			2017,
			DateUtil.MARCH,
			24
		);

		int iNumPath = 1;
		int iNumVertex = 10;
		double dblVolatility = 0.10;
		String strTreasuryCode = "UST";

		String[] astrTenor = new String[] {
			"01Y",
			"02Y",
			"03Y",
			"05Y",
			"07Y",
			"10Y",
			"20Y",
			"30Y"
		};

		double[] adblTreasuryCoupon = new double[] {
			0.0100,
			0.0100,
			0.0125,
			0.0150,
			0.0200,
			0.0225,
			0.0250,
			0.0300
		};

		double[] adblTreasuryYield = new double[] {
			0.0083,	//  1Y
			0.0122, //  2Y
			0.0149, //  3Y
			0.0193, //  5Y
			0.0227, //  7Y
			0.0248, // 10Y
			0.0280, // 20Y
			0.0308  // 30Y
		};

		BasisSplineGovvieYield bsgy = (BasisSplineGovvieYield) GovvieCurve (
			dtSpot,
			strTreasuryCode,
			astrTenor,
			adblTreasuryCoupon,
			adblTreasuryYield
		);

		double[] adblInitialForward = bsgy.flatForward (astrTenor).nodeValues();

		DiffusionEvolver de = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				0.,
				dblVolatility
			)
		);

		DiffusionEvolver[] aDE = new DiffusionEvolver[astrTenor.length];
		double[][] aadblCorrelation = new double[astrTenor.length][astrTenor.length];
		JumpDiffusionVertex[] aJDVInitial = new JumpDiffusionVertex[astrTenor.length];

		for (int i = 0; i < astrTenor.length; ++i) {
			aDE[i] = de;

			aJDVInitial[i] = new JumpDiffusionVertex (
				0.,
				adblInitialForward[i],
				0.,
				false
			);

			for (int j = 0; j < astrTenor.length; ++j)
				aadblCorrelation[i][j] = i == j ? 1. : 0.;
		}

		System.out.println ("\n\t||---------------------------------------------------------------------------------------||");

		String strDump = "\t|| TENOR";

		for (int i = 0; i < adblInitialForward.length; ++i)
			strDump += " |   " + astrTenor[i] + "  ";

		System.out.println (strDump + " ||");

		strDump = "\t||  BASE";

		System.out.println ("\t||---------------------------------------------------------------------------------------||");

		for (int i = 0; i < adblInitialForward.length; ++i)
			strDump += " | " + FormatUtil.FormatDouble (adblInitialForward[i], 2, 2, 100.) + "%";

		System.out.println (strDump + " ||");

		System.out.println ("\t||---------------------------------------------------------------------------------------||\n");

		double[] adblTenorToYearFraction = Helper.TenorToYearFraction (
			astrTenor,
			true
		);

		CorrelatedPathVertexDimension cpvd = new CorrelatedPathVertexDimension (
			new RandomNumberGenerator(),
			aadblCorrelation,
			iNumVertex,
			iNumPath,
			false,
			null
		);

		VertexRd[] aVertexRd = cpvd.multiPathVertexRd();

		for (int p = 0; p < iNumPath; ++p) {
			List<double[]> lsVertexRd = aVertexRd[p].vertexList();

			UnitRandom[] aUR = new UnitRandom[lsVertexRd.size()];

			for (int t = 0; t < lsVertexRd.size(); ++t) {
				double[] adblRd = lsVertexRd.get (t);

				strDump = "\t||" + FormatUtil.FormatDouble (t, 2, 0, 1.) + " => ";

				for (int f = 0; f < adblRd.length; ++f)
					aUR[f] = new UnitRandom (
						adblRd[f],
						0.
					);

				for (int f = 0; f < adblRd.length; ++f) {
					strDump = strDump + " " + FormatUtil.FormatDouble (adblRd[f], 1, 5, 1.) + " |";
				}

				System.out.println (strDump + "|");
			}
		}
	}
}
