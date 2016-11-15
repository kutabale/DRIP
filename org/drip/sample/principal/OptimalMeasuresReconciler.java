
package org.drip.sample.principal;

import org.drip.execution.dynamics.Almgren2003Parameters;
import org.drip.execution.generator.Almgren2003TrajectoryScheme;
import org.drip.execution.impact.*;
import org.drip.execution.optimum.Almgren2003TradingTrajectory;
import org.drip.execution.parameters.*;
import org.drip.execution.principal.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * OptimalMeasuresReconciler reconciles the Dependence Exponents on Liquidity, Trade Size, and Permanent
 * 	Impact Adjusted Principal Discount for the Optimal Principal Horizon and the Optional Information Ratio
 * 	with Almgren and Chriss (2003). The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 16 (6) 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OptimalMeasuresReconciler {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblS0 = 50.;
		double dblDailyVolume = 1000000.;
		double dblBidAskSpread = 0.;
		double dblTemporaryImpactFactor = 0.01;
		double dblDailyVolumeExecutionFactor = 0.1;
		double dblDrift = 0.;
		double dblVolatility = 1.;
		double dblSerialCorrelation = 0.;
		double dblX = 100000.;
		double dblFinishTime = 1.;
		int iNumInterval = 13;
		double dblLambda = 5.e-06;

		double[] adblK = new double[] {
			0.5,
			1.0,
			2.0
		};

		double[][] aadblOptimalHorizonDependenceReconciler = new double[][] {
			{0.810, 2.0, 1.0, 0.0, -2.0},
			{1.000, 1.0, 1.0, 0.0, -1.0},
			{1.134, 0.5, 1.0, 0.0, -0.5}
		};

		double[][] aadblOptimalInformationRatioDependenceReconciler = new double[][] {
			{1.063, -2.0, -1.0, -1.0, 3.0},
			{0.707, -1.0, -1.0, -1.0, 2.0},
			{0.449, -0.5, -1.0, -1.0, 1.5}
		};

		System.out.println();

		System.out.println ("\t|-----------------------------------------------------||");

		System.out.println ("\t|    Optimal Market Parameters Horizon Dependence     ||");

		System.out.println ("\t|-----------------------------------------------------||");

		for (double dblK : adblK) {
			ArithmeticPowerImpact apim = new ArithmeticPowerImpact (
				new AssetTransactionSettings (
					dblS0,
					dblDailyVolume,
					dblBidAskSpread
				),
				dblTemporaryImpactFactor,
				dblDailyVolumeExecutionFactor,
				dblK
			);

			Almgren2003Parameters a2003p = new Almgren2003Parameters (
				dblDrift,
				dblVolatility,
				dblSerialCorrelation,
				(ParticipationRateLinear) apim.permanentTransactionFunction(),
				(ParticipationRatePower) apim.temporaryTransactionFunction()
			);

			Almgren2003Estimator a2003e = new Almgren2003Estimator (
				(Almgren2003TradingTrajectory) Almgren2003TrajectoryScheme.Standard (
					dblX,
					dblFinishTime,
					iNumInterval,
					a2003p,
					dblLambda
				).generate(),
				a2003p
			);

			OptimalMeasureDependence omd = a2003e.optimalMeasures().omdHorizon();

			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (dblK, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.constant(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.liquidityExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.blockSizeExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.volatilityExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.adjustedPrincipalDiscountExponent(), 1, 3, 1.) + " ||"
			);
		}

		System.out.println ("\t|-----------------------------------------------------||");

		System.out.println ("\t|-----------------------------------------------------||");

		for (int i = 0; i < adblK.length; ++i)
			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (adblK[i], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalHorizonDependenceReconciler[i][0], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalHorizonDependenceReconciler[i][1], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalHorizonDependenceReconciler[i][2], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalHorizonDependenceReconciler[i][3], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalHorizonDependenceReconciler[i][4], 1, 3, 1.) + " ||"
			);

		System.out.println ("\t|-----------------------------------------------------||");

		System.out.println();

		System.out.println ("\t|-----------------------------------------------------||");

		System.out.println ("\t|     Optimal Market Parameters Information Ratio     ||");

		System.out.println ("\t|-----------------------------------------------------||");

		for (double dblK : adblK) {
			ArithmeticPowerImpact apim = new ArithmeticPowerImpact (
				new AssetTransactionSettings (
					dblS0,
					dblDailyVolume,
					dblBidAskSpread
				),
				dblTemporaryImpactFactor,
				dblDailyVolumeExecutionFactor,
				dblK
			);

			Almgren2003Parameters a2003p = new Almgren2003Parameters (
				dblDrift,
				dblVolatility,
				dblSerialCorrelation,
				(ParticipationRateLinear) apim.permanentTransactionFunction(),
				(ParticipationRatePower) apim.temporaryTransactionFunction()
			);

			Almgren2003Estimator a2003e = new Almgren2003Estimator (
				(Almgren2003TradingTrajectory) Almgren2003TrajectoryScheme.Standard (
					dblX,
					dblFinishTime,
					iNumInterval,
					a2003p,
					dblLambda
				).generate(),
				a2003p
			);

			OptimalMeasureDependence omd = a2003e.optimalMeasures().omdInformationRatio();

			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (dblK, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.constant(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.liquidityExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.blockSizeExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.volatilityExponent(), 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (omd.adjustedPrincipalDiscountExponent(), 1, 3, 1.) + " ||"
			);
		}

		System.out.println ("\t|-----------------------------------------------------||");

		System.out.println ("\t|-----------------------------------------------------||");

		for (int i = 0; i < adblK.length; ++i)
			System.out.println (
				"\t| " +
				FormatUtil.FormatDouble (adblK[i], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalInformationRatioDependenceReconciler[i][0], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalInformationRatioDependenceReconciler[i][1], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalInformationRatioDependenceReconciler[i][2], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalInformationRatioDependenceReconciler[i][3], 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (aadblOptimalInformationRatioDependenceReconciler[i][4], 1, 3, 1.) + " ||"
			);

		System.out.println ("\t|-----------------------------------------------------||");
	}
}
