
package org.drip.sample.burgard2011;

import org.drip.measure.dynamics.DiffusionEvaluatorLogarithmic;
import org.drip.measure.process.DiffusionEvolver;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.definition.*;
import org.drip.xva.derivative.*;
import org.drip.xva.hedgeerror.PerfectReplication;
import org.drip.xva.pde.*;
import org.drip.xva.universe.*;

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
 * XVAExplain demonstrates the Trajectory Attribtuion of the Bank and Counter-Party Default Based Derivative
 *  Evolution of the Dynamic XVA Replication Porfolio. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Cesari, G., J. Aquilina, N. Charpillon, X. Filipovic, G. Lee, and L. Manda (2009): Modeling, Pricing,
 *  	and Hedging Counter-party Credit Exposure - A Technical Guide, Springer Finance, New York.
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

public class XVAExplain {

	private static final EvolutionTrajectoryVertex RunStep (
		final TrajectoryEvolutionScheme tes,
		final SpreadIntensity si,
		final BurgardKjaerOperator bko,
		final EvolutionTrajectoryVertex etvStart)
		throws Exception
	{
		AssetGreekVertex agvStart = etvStart.assetGreekVertex();

		ReplicationPortfolioVertex rpvStart = etvStart.replicationPortfolioVertex();

		double dblDerivativeXVAValueStart = agvStart.derivativeXVAValue();

		double dblTimeWidth = tes.timeIncrement();

		double dblTimeStart = etvStart.time();

		double dblTime = dblTimeStart - 0.5 * dblTimeWidth;

		LatentStateEdge tvStart = etvStart.tradeablesVertex();

		LatentStateDynamicsContainer tc = tes.universe();

		double dblOvernightIndexBondNumeraire = tvStart.overnightIndexNumeraire().finish();

		double dblCollateralBondNumeraire = tvStart.collateralSchemeNumeraire().finish();

		LatentStateEdge tvFinish = LatentStateEdge.Standard (
			tc.asset().numeraireEvolver().weinerIncrement (
				new JumpDiffusionVertex (
					dblTime,
					tvStart.assetNumeraire().finish(),
					0.,
					false
				),
				dblTimeWidth
			),
			tc.overnightIndex().numeraireEvolver().weinerIncrement (
				new JumpDiffusionVertex (
					dblTime,
					dblOvernightIndexBondNumeraire,
					0.,
					false
				),
				dblTimeWidth
			),
			tc.collateralScheme().numeraireEvolver().weinerIncrement (
				new JumpDiffusionVertex (
					dblTime,
					dblCollateralBondNumeraire,
					0.,
					false
				),
				dblTimeWidth
			),
			tc.bankSeniorFunding().numeraireEvolver().weinerIncrement (
				new JumpDiffusionVertex (
					dblTime,
					tvStart.bankSeniorFundingNumeraire().finish(),
					0.,
					false
				),
				dblTimeWidth
			),
			new JumpDiffusionEdge[] {
				tc.counterPartyFunding()[0].numeraireEvolver().weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						tvStart.counterPartyFundingNumeraire()[0].finish(),
						0.,
						false
					),
					dblTimeWidth
				)
			},
			tc.bankHazardRateEvolver().weinerIncrement (
				new JumpDiffusionVertex (
					dblTime,
					tvStart.bankHazardRate().finish(),
					0.,
					false
				),
				dblTimeWidth
			),
			tc.bankRecoveryRateEvolver().weinerIncrement (
				new JumpDiffusionVertex (
					dblTime,
					tvStart.bankRecoveryRate().finish(),
					0.,
					false
				),
				dblTimeWidth
			)
		);

		CloseOutBilateral cob = tes.boundaryCondition();

		BurgardKjaerEdgeRun bker = bko.timeIncrementRun (
			si,
			etvStart,
			0.
		);

		double dblTheta = bker.theta();

		double dblAssetNumeraireBump = bker.assetNumeraireBump();

		double dblThetaAssetNumeraireUp = bker.thetaAssetNumeraireUp();

		double dblThetaAssetNumeraireDown = bker.thetaAssetNumeraireDown();

		double dblDerivativeXVAValueDeltaFinish = agvStart.derivativeXVAValueDelta() +
			0.5 * (dblThetaAssetNumeraireUp - dblThetaAssetNumeraireDown) * dblTimeWidth / dblAssetNumeraireBump;

		double dblDerivativeXVAValueGammaFinish = agvStart.derivativeXVAValueGamma() +
			(dblThetaAssetNumeraireUp + dblThetaAssetNumeraireDown - 2. * dblTheta) * dblTimeWidth /
				(dblAssetNumeraireBump * dblAssetNumeraireBump);

		double dblDerivativeXVAValueFinish = dblDerivativeXVAValueStart - dblTheta * dblTimeWidth;

		double dblGainOnBankDefaultFinish = -1. * (dblDerivativeXVAValueFinish - cob.bankDefaultGross
			(new double[] {dblDerivativeXVAValueFinish}));

		double dblGainOnCounterPartyDefaultFinish = -1. * (dblDerivativeXVAValueFinish -
			cob.counterPartyDefault (0, new double[] {dblDerivativeXVAValueFinish}));

		org.drip.xva.derivative.CashAccountEdge cae = tes.rebalanceCash (
			etvStart,
			tvFinish
		).cashAccount();

		double dblCashAccountAccumulationFinish = cae.accumulation();

		double dblAssetPriceFinish = tvFinish.assetNumeraire().finish();

		double dblZeroCouponBankPriceFinish = tvFinish.bankSeniorFundingNumeraire().finish();

		double dblZeroCouponCounterPartyPriceFinish = tvFinish.counterPartyFundingNumeraire()[0].finish();

		ReplicationPortfolioVertex rpvFinish = ReplicationPortfolioVertex.Standard (
			-1. * dblDerivativeXVAValueDeltaFinish,
			dblGainOnBankDefaultFinish / dblZeroCouponBankPriceFinish,
			new double[] {dblGainOnCounterPartyDefaultFinish / dblZeroCouponCounterPartyPriceFinish},
			rpvStart.cashAccount() + dblCashAccountAccumulationFinish
		);

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblTime, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblAssetPriceFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblZeroCouponBankPriceFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblZeroCouponCounterPartyPriceFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (tvFinish.collateralSchemeNumeraire().finish(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvFinish.assetNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvFinish.bankSeniorNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvFinish.counterPartyNumeraireUnits()[0], 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvFinish.cashAccount(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblCashAccountAccumulationFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (cae.assetAccumulation(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (cae.bankAccumulation(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (cae.counterPartyAccumulation(), 1, 6, 1.) + " ||"
		);

		return new EvolutionTrajectoryVertex (
			dblTimeStart - dblTimeWidth,
			tvFinish,
			rpvFinish,
			new AssetGreekVertex (
				dblDerivativeXVAValueFinish,
				dblDerivativeXVAValueDeltaFinish,
				dblDerivativeXVAValueGammaFinish,
				agvStart.derivativeFairValue() * Math.exp (
					-1. * dblTimeWidth * tc.collateralScheme().numeraireEvolver().evaluator().drift().value (
						new JumpDiffusionVertex (
							dblTime,
							dblCollateralBondNumeraire,
							0.,
							false
						)
					)
				)
			),
			new double[] {dblGainOnBankDefaultFinish},
			new double[] {dblGainOnCounterPartyDefaultFinish},
			0.,
			0.
		);
	}

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblSensitivityShiftFactor = 0.001;
		double dblBankRecovery = 0.4;
		double dblCounterPartyRecovery = 0.4;
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblAssetRepo = 0.03;
		double dblAssetDividend = 0.02;

		double dblZeroCouponOvernightIndexBondDrift = 0.0025;
		double dblZeroCouponOvernightIndexBondVolatility = 0.01;
		double dblZeroCouponOvernightIndexBondRepo = 0.0;

		double dblZeroCouponCollateralBondDrift = 0.01;
		double dblZeroCouponCollateralBondVolatility = 0.05;
		double dblZeroCouponCollateralBondRepo = 0.005;

		double dblZeroCouponBankBondDrift = 0.03;
		double dblZeroCouponBankBondVolatility = 0.10;
		double dblZeroCouponBankBondRepo = 0.028;

		double dblZeroCouponCounterPartyBondDrift = 0.03;
		double dblZeroCouponCounterPartyBondVolatility = 0.10;
		double dblZeroCouponCounterPartyBondRepo = 0.028;

		double dblInitialBankHazardRate = 0.01;
		double dblBankHazardRateDrift = 0.00;
		double dblBankHazardRateVolatility = 0.001;

		double dblInitialBankRecoveryRate = 0.45;
		double dblBankRecoveryRateDrift = 0.0;
		double dblBankRecoveryRateVolatility = 0.0;

		double dblTimeWidth = 1. / 24.;
		double dblTime = 1.;
		double dblTerminalXVADerivativeValue = 1.;

		PDEEvolutionControl pdeec = new PDEEvolutionControl (
			PDEEvolutionControl.CLOSEOUT_GREGORY_LI_TANG,
			dblSensitivityShiftFactor
		);

		CloseOutBilateral cob = CloseOutBilateral.Standard (
			dblBankRecovery,
			new double[] {dblCounterPartyRecovery}
		);

		DiffusionEvolver deAsset = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblAssetDrift,
				dblAssetVolatility
			)
		);

		DiffusionEvolver deZeroCouponOvernightIndexBond = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponOvernightIndexBondDrift,
				dblZeroCouponOvernightIndexBondVolatility
			)
		);

		DiffusionEvolver deZeroCouponCollateralBond = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponCollateralBondDrift,
				dblZeroCouponCollateralBondVolatility
			)
		);

		DiffusionEvolver deZeroCouponBankBond = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponBankBondDrift,
				dblZeroCouponBankBondVolatility
			)
		);

		DiffusionEvolver deZeroCouponCounterPartyBond = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponCounterPartyBondDrift,
				dblZeroCouponCounterPartyBondVolatility
			)
		);

		DiffusionEvolver deBankHazardRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankHazardRateDrift,
				dblBankHazardRateVolatility
			)
		);

		DiffusionEvolver deBankRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankRecoveryRateDrift,
				dblBankRecoveryRateVolatility
			)
		);

		LatentStateDynamicsContainer tc = LatentStateDynamicsContainer.Standard (
			new Equity (
				deAsset,
				dblAssetRepo,
				dblAssetDividend
			),
			new Tradeable (
				deZeroCouponOvernightIndexBond,
				dblZeroCouponOvernightIndexBondRepo
			),
			new Tradeable (
				deZeroCouponCollateralBond,
				dblZeroCouponCollateralBondRepo
			),
			new Tradeable (
				deZeroCouponBankBond,
				dblZeroCouponBankBondRepo
			),
			new Tradeable[] {
				new Tradeable (
					deZeroCouponCounterPartyBond,
					dblZeroCouponCounterPartyBondRepo
				)
			},
			deBankHazardRate,
			deBankRecoveryRate
		);

		TrajectoryEvolutionScheme tes = new TrajectoryEvolutionScheme (
			tc,
			cob,
			pdeec,
			dblTimeWidth
		);

		BurgardKjaerOperator bko = new PerfectReplication (
			tc,
			cob,
			pdeec
		);

		SpreadIntensity si = SpreadIntensity.Standard (
			dblZeroCouponBankBondDrift - dblZeroCouponCollateralBondDrift,
			(dblZeroCouponBankBondDrift - dblZeroCouponCollateralBondDrift) / dblBankRecovery,
			new double[] {(dblZeroCouponCounterPartyBondDrift - dblZeroCouponCollateralBondDrift) / dblCounterPartyRecovery}
		);

		double dblDerivativeValue = dblTerminalXVADerivativeValue;
		double dblDerivativeXVAValue = dblTerminalXVADerivativeValue;

		AssetGreekVertex agvInitial = new AssetGreekVertex (
			dblDerivativeXVAValue,
			-1.,
			0.,
			dblDerivativeValue
		);

		double dblGainOnBankDefaultInitial = -1. * (dblDerivativeXVAValue -
			cob.bankDefaultGross (new double[] {dblDerivativeXVAValue}));

		double dblGainOnCounterPartyDefaultInitial = -1. * (dblDerivativeXVAValue -
			cob.counterPartyDefault (0, new double[] {dblDerivativeXVAValue}));

		ReplicationPortfolioVertex rpvInitial = ReplicationPortfolioVertex.Standard (
			1.,
			dblGainOnBankDefaultInitial,
			new double[] {dblGainOnCounterPartyDefaultInitial},
			0.
		);

		System.out.println();

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||                                            BILATERAL XVA EVOLVER - BURGARD & KJAER (2011) REPLICATION PORTFOLIO EVOLUTION                                             ||");

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||    L -> R:                                                                                                                                                            ||");

		System.out.println ("\t||            - Time                                                                                                                                                     ||");

		System.out.println ("\t||            - Derivative XVA Value                                                                                                                                     ||");

		System.out.println ("\t||            - Asset Price Realization                                                                                                                                  ||");

		System.out.println ("\t||            - Realization of the Zero Coupon Bank Bond Price                                                                                                           ||");

		System.out.println ("\t||            - Realization of the Zero Coupon Counter Party Bond Price                                                                                                  ||");

		System.out.println ("\t||            - Realization of the Zero Coupon Collateral Bond Price                                                                                                     ||");

		System.out.println ("\t||            - Derivative XVA Asset Replication Units                                                                                                                   ||");

		System.out.println ("\t||            - Derivative XVA Value Bank Bond Replication Units                                                                                                         ||");

		System.out.println ("\t||            - Derivative XVA Value Counter Party Bond Replication Units                                                                                                ||");

		System.out.println ("\t||            - Derivative XVA Value Cash Account Replication Units                                                                                                      ||");

		System.out.println ("\t||            - Derivative Cash Account Accumulation Component                                                                                                           ||");

		System.out.println ("\t||            - Asset Cash Account Accumulation Component                                                                                                                ||");

		System.out.println ("\t||            - Bank Cash Account Accumulation Component                                                                                                                 ||");

		System.out.println ("\t||            - Counter Party Cash Account Accumulation Component                                                                                                        ||");

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblTime, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (agvInitial.derivativeXVAValue(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (1., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (1., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (1., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (1., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvInitial.assetNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvInitial.bankSeniorNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvInitial.counterPartyNumeraireUnits()[0], 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (rpvInitial.cashAccount(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " ||"
		);

		EvolutionTrajectoryVertex etv = new EvolutionTrajectoryVertex (
			dblTime,
			LatentStateEdge.Standard (
				deAsset.weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						dblDerivativeValue,
						0.,
						false
					),
					dblTimeWidth
				),
				deZeroCouponOvernightIndexBond.weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						1.,
						0.,
						false
					),
					dblTimeWidth
				),
				deZeroCouponCollateralBond.weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						1.,
						0.,
						false
					),
					dblTimeWidth
				),
				deZeroCouponBankBond.weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						1.,
						0.,
						false
					),
					dblTimeWidth
				),
				new JumpDiffusionEdge[] {
					deZeroCouponCounterPartyBond.weinerIncrement (
						new JumpDiffusionVertex (
							dblTime,
							1.,
							0.,
							false
						),
						dblTimeWidth
					)
				},
				deBankHazardRate.weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						dblInitialBankHazardRate,
						0.,
						false
					),
					dblTimeWidth
				),
				deBankRecoveryRate.weinerIncrement (
					new JumpDiffusionVertex (
						dblTime,
						dblInitialBankRecoveryRate,
						0.,
						false
					),
					dblTimeWidth
				)
			),
			ReplicationPortfolioVertex.Standard (
				1.,
				0.,
				new double[] {0.},
				0.
			),
			agvInitial,
			new double[] {dblGainOnBankDefaultInitial},
			new double[] {dblGainOnCounterPartyDefaultInitial},
			0.,
			0.
		);

		for (dblTime -= dblTimeWidth; dblTime >= 0.; dblTime -= dblTimeWidth)
			etv = RunStep (
				tes,
				si,
				bko,
				etv
			);

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();
	}
}
