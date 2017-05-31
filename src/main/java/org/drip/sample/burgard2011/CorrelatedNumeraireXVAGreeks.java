
package org.drip.sample.burgard2011;

import org.drip.measure.discrete.SequenceGenerator;
import org.drip.measure.dynamics.*;
import org.drip.measure.process.*;
import org.drip.measure.realization.*;
import org.drip.quant.common.FormatUtil;
import org.drip.quant.linearalgebra.Matrix;
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
 * CorrelatedNumeraireXVAGreeks constructs the XVA Greeks arising out of the Joint Evolution of Numeraires -
 *  the Continuous Asset, the Collateral, the Bank, and the Counter-Party Numeraires involved in the Dynamic
 *  XVA Replication Portfolio of the Burgard and Kjaer (2011) Methodology. The References are:
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

public class CorrelatedNumeraireXVAGreeks {

	private static final EvolutionTrajectoryVertex RunStep (
		final TrajectoryEvolutionScheme tes,
		final SpreadIntensity si,
		final BurgardKjaerOperator bko,
		final EvolutionTrajectoryVertex etvStart,
		final JumpDiffusionEdge jdeAsset,
		final JumpDiffusionEdge jdeOvernightIndex,
		final JumpDiffusionEdge jdeCollateral,
		final JumpDiffusionEdge jdeBank,
		final JumpDiffusionEdge jdeCounterParty)
		throws Exception
	{
		AssetGreekVertex agvStart = etvStart.assetGreekVertex();

		ReplicationPortfolioVertex rpvStart = etvStart.replicationPortfolioVertex();

		double dblDerivativeXVAValueStart = agvStart.derivativeXVAValue();

		double dblTimeWidth = tes.timeIncrement();

		double dblTimeStart = etvStart.time();

		double dblTime = dblTimeStart - 0.5 * dblTimeWidth;

		TradeablesVertex tcvmStart = etvStart.tradeablesVertex();

		TradeablesContainer tcm = tes.universe();

		double dblCollateralBondNumeraire = tcvmStart.collateralSchemeNumeraire().finish();

		TradeablesVertex tcvmFinish = TradeablesVertex.Standard (
			jdeAsset,
			jdeOvernightIndex,
			jdeCollateral,
			jdeBank,
			new JumpDiffusionEdge[] {jdeCounterParty}
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

		double dblGainOnBankDefaultFinish = -1. * (dblDerivativeXVAValueFinish -
			cob.bankDefaultGross (new double[] {dblDerivativeXVAValueFinish}));

		double dblGainOnCounterPartyDefaultFinish = -1. * (dblDerivativeXVAValueFinish - cob.counterPartyDefault
			(0, new double[] {dblDerivativeXVAValueFinish}));

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblTime, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueDeltaFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueGammaFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnBankDefaultFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnCounterPartyDefaultFinish, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bker.derivativeXVAStochasticGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bker.derivativeXVACollateralGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bker.derivativeXVAFundingGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bker.derivativeXVABankDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bker.derivativeXVACounterPartyDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblThetaAssetNumeraireDown, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblTheta, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblThetaAssetNumeraireUp, 1, 6, 1.) + " ||"
		);

		org.drip.xva.derivative.CashAccountEdge cae = tes.rebalanceCash (
			etvStart,
			tcvmFinish
		).cashAccount();

		double dblCashAccountAccumulationFinish = cae.accumulation();

		double dblZeroCouponBankPriceFinish = jdeBank.finish();

		double dblZeroCouponCounterPartyPriceFinish = jdeCounterParty.finish();

		ReplicationPortfolioVertex rpvFinish = ReplicationPortfolioVertex.Standard (
			-1. * dblDerivativeXVAValueDeltaFinish,
			dblGainOnBankDefaultFinish / dblZeroCouponBankPriceFinish,
			new double[] {dblGainOnCounterPartyDefaultFinish / dblZeroCouponCounterPartyPriceFinish},
			rpvStart.cashAccount() + dblCashAccountAccumulationFinish
		);

		return new EvolutionTrajectoryVertex (
			dblTimeStart - dblTimeWidth,
			tcvmFinish,
			rpvFinish,
			new AssetGreekVertex (
				dblDerivativeXVAValueFinish,
				dblDerivativeXVAValueDeltaFinish,
				dblDerivativeXVAValueGammaFinish,
				agvStart.derivativeFairValue() * Math.exp (
					-1. * dblTimeWidth * tcm.collateralScheme().numeraireEvolver().evaluator().drift().value (
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

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblTimeWidth = 1. / 24.;
		double dblTime = 1.;
		double[][] aadblCorrelation = new double[][] {
			{1.00, 0.00, 0.20, 0.15, 0.05}, // #0 ASSET
			{0.00, 1.00, 0.00, 0.00, 0.00}, // #1 OVERNIGHT
			{0.20, 0.00, 1.00, 0.13, 0.25}, // #2 COLLATERAL
			{0.15, 0.00, 0.13, 1.00, 0.00}, // #3 BANK
			{0.05, 0.00, 0.25, 0.00, 1.00}  // #4 COUNTER PARTY
		};
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblAssetRepo = 0.03;
		double dblAssetDividend = 0.02;
		double dblInitialAssetNumeraire = 1.;

		double dblZeroCouponOvernightIndexBondDrift = 0.0025;
		double dblZeroCouponOvernightIndexBondVolatility = 0.01;
		double dblZeroCouponOvernightIndexBondRepo = 0.0;
		double dblZeroCouponOvernightIndexNumeraire = 1.;

		double dblZeroCouponCollateralBondDrift = 0.01;
		double dblZeroCouponCollateralBondVolatility = 0.05;
		double dblZeroCouponCollateralBondRepo = 0.005;
		double dblInitialCollateralNumeraire = 1.;

		double dblZeroCouponBankBondDrift = 0.03;
		double dblZeroCouponBankBondVolatility = 0.10;
		double dblZeroCouponBankBondRepo = 0.028;
		double dblBankHazardRate = 0.03;
		double dblBankRecoveryRate = 0.45;
		double dblTerminalBankNumeraire = 1.;

		double dblZeroCouponCounterPartyBondDrift = 0.03;
		double dblZeroCouponCounterPartyBondVolatility = 0.10;
		double dblZeroCouponCounterPartyBondRepo = 0.028;
		double dblCounterPartyHazardRate = 0.05;
		double dblCounterPartyRecoveryRate = 0.30;
		double dblTerminalCounterPartyNumeraire = 1.;

		double dblTerminalXVADerivativeValue = 1.;

		double dblSensitivityShiftFactor = 0.001;

		int iNumTimeStep = (int) (1. / dblTimeWidth);
		double dblDerivativeValue = dblTerminalXVADerivativeValue;
		double dblDerivativeXVAValue = dblTerminalXVADerivativeValue;

		PDEEvolutionControl pdeec = new PDEEvolutionControl (
			PDEEvolutionControl.CLOSEOUT_GREGORY_LI_TANG,
			dblSensitivityShiftFactor
		);

		CloseOutBilateral cob = CloseOutBilateral.Standard (
			dblBankRecoveryRate,
			new double[] {dblCounterPartyRecoveryRate}
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

		DiffusionEvolver deZeroCouponBankBond = new JumpDiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponBankBondDrift,
				dblZeroCouponBankBondVolatility
			),
			HazardJumpEvaluator.Standard (
				dblBankHazardRate,
				dblBankRecoveryRate
			)
		);

		DiffusionEvolver deZeroCouponCounterPartyBond = new JumpDiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponCounterPartyBondDrift,
				dblZeroCouponCounterPartyBondVolatility
			),
			HazardJumpEvaluator.Standard (
				dblCounterPartyHazardRate,
				dblCounterPartyRecoveryRate
			)
		);

		TradeablesContainer tcm = TradeablesContainer.Standard (
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
			}
		);

		TrajectoryEvolutionScheme tes = new TrajectoryEvolutionScheme (
			tcm,
			cob,
			pdeec,
			dblTimeWidth
		);

		BurgardKjaerOperator bko = new PerfectReplication (
			tcm,
			cob,
			pdeec
		);

		SpreadIntensity si = SpreadIntensity.Standard (
			dblZeroCouponBankBondDrift - dblZeroCouponCollateralBondDrift,
			(dblZeroCouponBankBondDrift - dblZeroCouponCollateralBondDrift) / dblBankRecoveryRate,
			new double[] {(dblZeroCouponCounterPartyBondDrift - dblZeroCouponCollateralBondDrift) / dblCounterPartyRecoveryRate}
		);

		double[][] aadblNumeraireTimeSeries = Matrix.Transpose (
			SequenceGenerator.GaussianJoint (
				iNumTimeStep,
				aadblCorrelation
			)
		);

		double[] adblBankDefaultIndicator = SequenceGenerator.Uniform (iNumTimeStep);

		double[] adblCounterPartyDefaultIndicator = SequenceGenerator.Uniform (iNumTimeStep);

		JumpDiffusionEdge[] aJDEAsset = deAsset.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialAssetNumeraire,
				0.,
				false
			),
			UnitRandomEdge.Diffusion (aadblNumeraireTimeSeries[0]),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aJDEOvernightIndex = deZeroCouponOvernightIndexBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblZeroCouponOvernightIndexNumeraire,
				0.,
				false
			),
			UnitRandomEdge.Diffusion (aadblNumeraireTimeSeries[1]),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aJDECollateral = deZeroCouponCollateralBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialCollateralNumeraire,
				0.,
				false
			),
			UnitRandomEdge.Diffusion (aadblNumeraireTimeSeries[2]),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aJDEBank = deZeroCouponBankBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblTerminalBankNumeraire,
				0.,
				false
			),
			UnitRandomEdge.JumpDiffusion (
				aadblNumeraireTimeSeries[3],
				adblBankDefaultIndicator
			),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aJDECounterParty = deZeroCouponCounterPartyBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblTerminalCounterPartyNumeraire,
				0.,
				false
			),
			UnitRandomEdge.JumpDiffusion (
				aadblNumeraireTimeSeries[4],
				adblCounterPartyDefaultIndicator
			),
			dblTimeWidth
		);

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

		System.out.println();

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||                                                    BILATERAL XVA EVOLVER - BURGARD & KJAER (2011) GREEKS EVOLUTION                                                    ||");

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||    L -> R:                                                                                                                                                            ||");

		System.out.println ("\t||            - Time                                                                                                                                                     ||");

		System.out.println ("\t||            - Derivative XVA Value                                                                                                                                     ||");

		System.out.println ("\t||            - Derivative XVA Value Delta                                                                                                                               ||");

		System.out.println ("\t||            - Derivative XVA Value Gamma                                                                                                                               ||");

		System.out.println ("\t||            - Gain at Bank Default                                                                                                                                     ||");

		System.out.println ("\t||            - Gain at Counter Party Default                                                                                                                            ||");

		System.out.println ("\t||            - Derivative XVA Asset Growth Theta                                                                                                                        ||");

		System.out.println ("\t||            - Derivative XVA Collateral Numeraire Growth Theta                                                                                                         ||");

		System.out.println ("\t||            - Derivative XVA Bank Funding Growth Theta                                                                                                                 ||");

		System.out.println ("\t||            - Derivative XVA Bank Default Growth Theta                                                                                                                 ||");

		System.out.println ("\t||            - Derivative XVA Counter Party Default Growth Theta                                                                                                        ||");

		System.out.println ("\t||            - Derivative XVA Theta Based on Asset Numeraire Down                                                                                                       ||");

		System.out.println ("\t||            - Derivative XVA Theta                                                                                                                                     ||");

		System.out.println ("\t||            - Derivative XVA Theta Based on Asset Numeraire Up                                                                                                         ||");

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblTime, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (agvInitial.derivativeXVAValue(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (agvInitial.derivativeXVAValueDelta(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (agvInitial.derivativeXVAValueGamma(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnBankDefaultInitial, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnCounterPartyDefaultInitial, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " ||"
		);

		EvolutionTrajectoryVertex etv = new EvolutionTrajectoryVertex (
			dblTime,
			TradeablesVertex.Standard (
				aJDEAsset[iNumTimeStep - 1],
				aJDEOvernightIndex[iNumTimeStep - 1],
				aJDECollateral[iNumTimeStep - 1],
				aJDEBank[iNumTimeStep - 1],
				new JumpDiffusionEdge[] {aJDECounterParty[iNumTimeStep - 1]}
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

		for (int i = iNumTimeStep - 2; i >= 0; --i)
			etv = RunStep (
				tes,
				si,
				bko,
				etv,
				aJDEAsset[i],
				aJDEOvernightIndex[i],
				aJDECollateral[i],
				aJDEBank[i],
				aJDECounterParty[i]
			);

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();
	}
}
