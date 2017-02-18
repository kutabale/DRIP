
package org.drip.sample.netting;

import org.drip.analytics.date.*;
import org.drip.measure.discretemarginal.SequenceGenerator;
import org.drip.measure.dynamics.DiffusionEvaluatorLogarithmic;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.netting.GroupTrajectoryVertexExposure;

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
 * PortfolioGroupRun demonstrates the Simulation Run of the Netting Group Exposure. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 *  
 *  - Li, B., and Y. Tang (2007): Quantitative Analysis, Derivatives Modeling, and Trading Strategies in the
 *  	Presence of Counter-party Credit Risk for the Fixed Income Market, World Scientific Publishing,
 *  	Singapore.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PortfolioGroupRun {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iNumStep = 10;
		double dblTime = 5.;
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblCollateralDrift = 0.01;
		double dblBankHazardRate = 0.025;

		double dblTimeWidth = dblTime / iNumStep;
		double[] adblCollateral = new double[iNumStep];
		double[] adblBankSurvival = new double[iNumStep];
		GroupTrajectoryVertexExposure[] aGTVE1 = new GroupTrajectoryVertexExposure[iNumStep];
		GroupTrajectoryVertexExposure[] aGTVE2 = new GroupTrajectoryVertexExposure[iNumStep];

		JulianDate dtSpot = DateUtil.Today();

		DiffusionEvolver meAsset = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblAssetDrift,
				dblAssetVolatility
			)
		);

		JumpDiffusionEdge[] aR1Asset1 = meAsset.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				1.,
				0.,
				false
			),
			UnitRandom.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aR1Asset2 = meAsset.incrementSequence (
			new JumpDiffusionVertex (
				dblTime,
				1.,
				0.,
				false
			),
			UnitRandom.Diffusion (SequenceGenerator.Gaussian (iNumStep)),
			dblTimeWidth
		);

		for (int i = 0; i < aR1Asset1.length; ++i) {
			adblCollateral[i] = Math.exp (0.5 * dblCollateralDrift * (i + 1));

			adblBankSurvival[i] = Math.exp (-0.5 * dblBankHazardRate * (i + 1));

			aGTVE1[i] = new GroupTrajectoryVertexExposure (aR1Asset1[i].finish());

			aGTVE2[i] = new GroupTrajectoryVertexExposure (aR1Asset2[i].finish());

			System.out.println (
				"\t" + dtSpot.addMonths (6 * i + 6) + " => " +
				FormatUtil.FormatDouble (aGTVE1[i].gross(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aGTVE1[i].positive(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aGTVE1[i].negative(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aGTVE2[i].gross(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aGTVE2[i].positive(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (aGTVE2[i].negative(), 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblCollateral[i], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (adblBankSurvival[i], 1, 6, 1.) + " ||"
			);
		}
	}
}
