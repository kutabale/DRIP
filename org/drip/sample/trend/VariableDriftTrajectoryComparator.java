
package org.drip.sample.trend;

import org.drip.execution.bayesian.*;
import org.drip.execution.cost.*;
import org.drip.execution.impact.ParticipationRateLinear;
import org.drip.measure.gaussian.R1UnivariateNormal;
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
 * VariableDriftTrajectoryComparator demonstrates the Optimal Trajectory for a Price Process with Bayesian
 *  Drift, Arithmetic Volatility, and Linear Temporary Market Impact across a Set of Drifts. The References
 *  are:
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets 1
 * 		1-50.
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Brunnermeier, L. K., and L. H. Pedersen (2005): Predatory Trading, Journal of Finance 60 (4) 1825-1863.
 *
 * 	- Almgren, R., and J. Lorenz (2006): Bayesian Adaptive Trading with a Daily Cycle, Journal of Trading 1
 * 		(4) 38-46.
 * 
 * 	- Kissell, R., and R. Malamut (2007): Algorithmic Decision Making Framework, Journal of Trading 1 (1)
 * 		12-21.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class VariableDriftTrajectoryComparator {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		int iN = 50;
		double dblT = 1.;
		double dblX0 = 1.;
		double dblNu = 1.;
		double dblEta = 0.07;
		double dblSigma = 1.5;
		double dblAlphaBarLow = 0.0;
		double dblAlphaBarMid = 0.7;
		double dblAlphaBarHigh = 1.5;

		double dblTime = 0.;
		double dblTimeWidth = dblT / iN;
		double dblXUnconstrained = dblX0;
		double dblXConstrainedLow = dblX0;
		double dblXConstrainedMid = dblX0;
		double dblXConstrainedHigh = dblX0;

		ParticipationRateLinear prlTemporary = ParticipationRateLinear.SlopeOnly (dblEta);

		PriorDriftDistribution pddLow = new PriorDriftDistribution (
			dblAlphaBarLow,
			dblNu
		);

		PriorDriftDistribution pddMid = new PriorDriftDistribution (
			dblAlphaBarMid,
			dblNu
		);

		PriorDriftDistribution pddHigh = new PriorDriftDistribution (
			dblAlphaBarHigh,
			dblNu
		);

		double[] adblAlphaLow = pddLow.realizedDrift (iN);

		double[] adblAlphaMid = pddMid.realizedDrift (iN);

		double[] adblAlphaHigh = pddHigh.realizedDrift (iN);

		System.out.println();

		System.out.println ("\t|--------------------------------------------------------------------------------||");

		System.out.println ("\t|  L -> R                                                                        ||");

		System.out.println ("\t|--------------------------------------------------------------------------------||");

		System.out.println ("\t|    - Time                                                                      ||");

		System.out.println ("\t|    - Constrained Trade Rate Low Drift                                          ||");

		System.out.println ("\t|    - Constrained Trade Rate Mid Drift                                          ||");

		System.out.println ("\t|    - Constrained Trade Rate High Drift                                         ||");

		System.out.println ("\t|    - Unconstrained Trade Rate                                                  ||");

		System.out.println ("\t|    - Constrained Holdings Low Drift                                            ||");

		System.out.println ("\t|    - Constrained Holdings Mid Drift                                            ||");

		System.out.println ("\t|    - Constrained Holdings High Drift                                           ||");

		System.out.println ("\t|    - Unconstrained Holdings                                                    ||");

		System.out.println ("\t|--------------------------------------------------------------------------------||");

		for (int i = 0; i < iN - 1; ++i) {
			dblTime = dblTime + dblTimeWidth;

			ConditionalPriceDistribution cpdLow = new ConditionalPriceDistribution (
				adblAlphaLow[i],
				dblSigma,
				dblTime
			);

			ConditionalPriceDistribution cpdMid = new ConditionalPriceDistribution (
				adblAlphaMid[i],
				dblSigma,
				dblTime
			);

			ConditionalPriceDistribution cpdHigh = new ConditionalPriceDistribution (
				adblAlphaHigh[i],
				dblSigma,
				dblTime
			);

			double dblPriceSwingLow = cpdLow.priceVolatilitySwing();

			double dblPriceSwingMid = cpdMid.priceVolatilitySwing();

			double dblPriceSwingHigh = cpdHigh.priceVolatilitySwing();

			double dblRealizedPriceChangeLow = adblAlphaLow[i] + dblPriceSwingLow;

			double dblRealizedPriceChangeMid = adblAlphaMid[i] + dblPriceSwingMid;

			double dblRealizedPriceChangeHigh = adblAlphaHigh[i] + dblPriceSwingHigh;

			PriorConditionalCombiner pdcLow = new PriorConditionalCombiner (
				pddLow,
				cpdLow
			);

			PriorConditionalCombiner pdcMid = new PriorConditionalCombiner (
				pddMid,
				cpdMid
			);

			PriorConditionalCombiner pdcHigh = new PriorConditionalCombiner (
				pddHigh,
				cpdHigh
			);

			R1UnivariateNormal r1unPosteriorLow = pdcLow.posteriorDriftDistribution (dblRealizedPriceChangeLow);

			R1UnivariateNormal r1unPosteriorMid = pdcMid.posteriorDriftDistribution (dblRealizedPriceChangeMid);

			R1UnivariateNormal r1unPosteriorHigh = pdcHigh.posteriorDriftDistribution (dblRealizedPriceChangeHigh);

			double dblDriftEstimateLow = r1unPosteriorLow.mean();

			double dblDriftEstimateMid = r1unPosteriorMid.mean();

			double dblDriftEstimateHigh = r1unPosteriorHigh.mean();

			DirectionConstrainedLinearImpact dcliLow = DirectionConstrainedLinearImpact.Standard (
				0.,
				dblT,
				dblXConstrainedLow,
				dblDriftEstimateLow,
				prlTemporary
			);

			DirectionConstrainedLinearImpact dcliMid = DirectionConstrainedLinearImpact.Standard (
				0.,
				dblT,
				dblXConstrainedMid,
				dblDriftEstimateMid,
				prlTemporary
			);

			DirectionConstrainedLinearImpact dcliHigh = DirectionConstrainedLinearImpact.Standard (
				0.,
				dblT,
				dblXConstrainedHigh,
				dblDriftEstimateHigh,
				prlTemporary
			);

			double dblConstrainedInstantaneousTradeRateLow = dcliLow.instantaneousTradeRate();

			dblXConstrainedLow = dblXConstrainedLow - dblConstrainedInstantaneousTradeRateLow * dblTimeWidth;

			if (0 > dblXConstrainedLow) dblXConstrainedLow = 0.;

			double dblConstrainedInstantaneousTradeRateMid = dcliMid.instantaneousTradeRate();

			dblXConstrainedMid = dblXConstrainedMid - dblConstrainedInstantaneousTradeRateMid * dblTimeWidth;

			if (0 > dblXConstrainedMid) dblXConstrainedMid = 0.;

			double dblConstrainedInstantaneousTradeRateHigh = dcliHigh.instantaneousTradeRate();

			dblXConstrainedHigh = dblXConstrainedHigh - dblConstrainedInstantaneousTradeRateHigh * dblTimeWidth;

			if (0 > dblXConstrainedHigh) dblXConstrainedHigh = 0.;

			LinearTemporaryImpactTrajectory ltit = LinearTemporaryImpactTrajectory.Unconstrained (
				dblTime,
				dblT,
				dblXUnconstrained,
				dblDriftEstimateMid,
				prlTemporary
			);

			double dblUnconstrainedInstantaneousTradeRate = ltit.instantaneousTradeRate();

			dblXUnconstrained = dblXUnconstrained - dblUnconstrainedInstantaneousTradeRate * dblTimeWidth;

			System.out.println (
				"\t| " + FormatUtil.FormatDouble (dblTime, 1, 2, 1.) + " => " +
				FormatUtil.FormatDouble (dblConstrainedInstantaneousTradeRateLow, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblConstrainedInstantaneousTradeRateMid, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblConstrainedInstantaneousTradeRateHigh, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblUnconstrainedInstantaneousTradeRate, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblXConstrainedLow, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblXConstrainedMid, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblXConstrainedHigh, 1, 3, 1.) + " | " +
				FormatUtil.FormatDouble (dblXUnconstrained, 1, 3, 1.) + " ||"
			);
		}

		System.out.println ("\t|--------------------------------------------------------------------------------||");
	}
}
