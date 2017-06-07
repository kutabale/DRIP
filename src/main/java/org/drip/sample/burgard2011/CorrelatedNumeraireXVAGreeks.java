
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
		final BurgardKjaerOperator bko,
		final EvolutionTrajectoryVertex etvStart,
		final LatentStateVertex lsvStart,
		final LatentStateVertex lsvFinish)
		throws Exception
	{
		AssetGreekVertex agvStart = etvStart.assetGreekVertex();

		ReplicationPortfolioVertex rpvStart = etvStart.replicationPortfolioVertex();

		double dblDerivativeXVAValueStart = agvStart.derivativeXVAValue();

		double dblTimeWidth = tes.timeIncrement();

		double dblTimeStart = etvStart.time();

		double dblTime = dblTimeStart - 0.5 * dblTimeWidth;

		LatentStateVertex tcvmStart = etvStart.latentStateVertex();

		LatentStateDynamicsContainer tcm = tes.universe();

		double dblCollateralBondNumeraire = tcvmStart.collateralSchemeNumeraire().value();

		BurgardKjaerEdgeRun bker = bko.edgeRun (
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

		CloseOutGeneral cog = new CloseOutBilateral (
			lsvFinish.bankSeniorRecoveryRate().value(),
			lsvFinish.counterPartyRecoveryRate().value()
		);

		double dblGainOnBankDefaultFinish = -1. * (dblDerivativeXVAValueFinish - cog.bankDefault (dblDerivativeXVAValueFinish));

		double dblGainOnCounterPartyDefaultFinish = -1. * (dblDerivativeXVAValueFinish - cog.counterPartyDefault
			(dblDerivativeXVAValueFinish));

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
			lsvStart,
			lsvFinish
		).cashAccount();

		double dblCashAccountAccumulationFinish = cae.accumulation();

		double dblZeroCouponBankPriceFinish = lsvFinish.bankSeniorFundingNumeraire().value();

		double dblZeroCouponCounterPartyPriceFinish = lsvFinish.counterPartyFundingNumeraire().value();

		ReplicationPortfolioVertex rpvFinish = ReplicationPortfolioVertex.Standard (
			-1. * dblDerivativeXVAValueDeltaFinish,
			dblGainOnBankDefaultFinish / dblZeroCouponBankPriceFinish,
			dblGainOnCounterPartyDefaultFinish / dblZeroCouponCounterPartyPriceFinish,
			rpvStart.cashAccount() + dblCashAccountAccumulationFinish
		);

		return new EvolutionTrajectoryVertex (
			dblTimeStart - dblTimeWidth,
			lsvFinish,
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
			dblGainOnBankDefaultFinish,
			dblGainOnCounterPartyDefaultFinish,
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
			{1.00, 0.00, 0.20, 0.15, 0.05, 0.00, 0.00, 0.00, 0.00}, // #0 ASSET
			{0.00, 1.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00}, // #1 OVERNIGHT
			{0.20, 0.00, 1.00, 0.13, 0.25, 0.00, 0.00, 0.00, 0.00}, // #2 COLLATERAL
			{0.15, 0.00, 0.13, 1.00, 0.00, 0.00, 0.00, 0.00, 0.00}, // #3 BANK
			{0.05, 0.00, 0.25, 0.00, 1.00, 0.00, 0.00, 0.00, 0.00}, // #4 COUNTER PARTY
			{0.00, 0.00, 0.00, 0.00, 0.00, 1.00, 0.00, 0.00, 0.00}, // #5 BANK HAZARD
			{0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1.00, 0.00, 0.00}, // #6 BANK SENIOR RECOVERY
			{0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1.00, 0.00}, // #8 COUNTER PARTY HAZARD
			{0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 1.00}  // #9 COUNTER PARTY RECOVERY
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
		double dblTerminalBankNumeraire = 1.;

		double dblInitialBankHazardRate = 0.03;
		double dblBankHazardRateDrift = 0.00;
		double dblBankHazardRateVolatility = 0.005;

		double dblInitialBankSeniorRecoveryRate = 0.45;
		double dblBankSeniorRecoveryRateDrift = 0.0;
		double dblBankSeniorRecoveryRateVolatility = 0.0;

		double dblZeroCouponCounterPartyBondDrift = 0.03;
		double dblZeroCouponCounterPartyBondVolatility = 0.10;
		double dblZeroCouponCounterPartyBondRepo = 0.028;
		double dblTerminalCounterPartyNumeraire = 1.;

		double dblInitialCounterPartyHazardRate = 0.05;
		double dblCounterPartyHazardRateDrift = 0.00;
		double dblCounterPartyHazardRateVolatility = 0.005;

		double dblInitialCounterPartyRecoveryRate = 0.30;
		double dblCounterPartyRecoveryRateDrift = 0.0;
		double dblCounterPartyRecoveryRateVolatility = 0.0;

		double dblTerminalXVADerivativeValue = 1.;

		double dblSensitivityShiftFactor = 0.001;

		int iNumTimeStep = (int) (1. / dblTimeWidth);
		double dblDerivativeValue = dblTerminalXVADerivativeValue;
		double dblDerivativeXVAValue = dblTerminalXVADerivativeValue;

		PDEEvolutionControl pdeec = new PDEEvolutionControl (
			PDEEvolutionControl.CLOSEOUT_GREGORY_LI_TANG,
			dblSensitivityShiftFactor
		);

		CloseOutBilateral cob = new CloseOutBilateral (
			dblInitialBankSeniorRecoveryRate,
			dblInitialCounterPartyRecoveryRate
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
				dblInitialBankHazardRate,
				dblInitialBankSeniorRecoveryRate
			)
		);

		DiffusionEvolver deZeroCouponCounterPartyBond = new JumpDiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblZeroCouponCounterPartyBondDrift,
				dblZeroCouponCounterPartyBondVolatility
			),
			HazardJumpEvaluator.Standard (
				dblInitialCounterPartyHazardRate,
				dblInitialCounterPartyRecoveryRate
			)
		);

		DiffusionEvolver deBankHazardRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankHazardRateDrift,
				dblBankHazardRateVolatility
			)
		);

		DiffusionEvolver deBankSeniorRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblBankSeniorRecoveryRateDrift,
				dblBankSeniorRecoveryRateVolatility
			)
		);

		DiffusionEvolver deCounterPartyHazardRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblCounterPartyHazardRateDrift,
				dblCounterPartyHazardRateVolatility
			)
		);

		DiffusionEvolver deCounterPartyRecoveryRate = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblCounterPartyRecoveryRateDrift,
				dblCounterPartyRecoveryRateVolatility
			)
		);

		LatentStateDynamicsContainer tcm = LatentStateDynamicsContainer.BankSenior (
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
			new Tradeable (
				deZeroCouponCounterPartyBond,
				dblZeroCouponCounterPartyBondRepo
			),
			deBankHazardRate,
			deBankSeniorRecoveryRate,
			deCounterPartyHazardRate,
			deCounterPartyRecoveryRate
		);

		TrajectoryEvolutionScheme tes = new TrajectoryEvolutionScheme (
			tcm,
			pdeec,
			dblTimeWidth
		);

		BurgardKjaerOperator bko = new PerfectReplication (
			tcm,
			pdeec
		);

		double[][] aadblNumeraireTimeSeries = Matrix.Transpose (
			SequenceGenerator.GaussianJoint (
				iNumTimeStep,
				aadblCorrelation
			)
		);

		double[] adblBankDefaultIndicator = SequenceGenerator.Uniform (iNumTimeStep);

		double[] adblCounterPartyDefaultIndicator = SequenceGenerator.Uniform (iNumTimeStep);

		LatentStateVertex[] aLSV = new LatentStateVertex[iNumTimeStep + 1];
		double[] adblTimeWidth = new double[iNumTimeStep];

		for (int i = 0; i < iNumTimeStep; ++i)
			adblTimeWidth[i] = dblTimeWidth;

		JumpDiffusionVertex[] aJDVAsset = deAsset.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialAssetNumeraire,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[0]
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVOvernightIndex = deZeroCouponOvernightIndexBond.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblZeroCouponOvernightIndexNumeraire,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[1]
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVCollateral = deZeroCouponCollateralBond.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialCollateralNumeraire,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[2]
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVBank = deZeroCouponBankBond.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblTerminalBankNumeraire,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.JumpDiffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[3],
				adblBankDefaultIndicator
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVCounterParty = deZeroCouponCounterPartyBond.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblTerminalCounterPartyNumeraire,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.JumpDiffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[4],
				adblCounterPartyDefaultIndicator
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVBankHazardRate = deBankHazardRate.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialBankHazardRate,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[5]
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVBankSeniorRecoveryRate = deBankSeniorRecoveryRate.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialBankSeniorRecoveryRate,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[6]
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVCounterPartyHazardRate = deCounterPartyHazardRate.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialCounterPartyHazardRate,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[7]
			),
			dblTimeWidth
		);

		JumpDiffusionVertex[] aJDVCounterPartyRecoveryRate = deCounterPartyRecoveryRate.vertexSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialCounterPartyRecoveryRate,
				0.,
				false
			),
			JumpDiffusionEdgeUnit.Diffusion (
				adblTimeWidth,
				aadblNumeraireTimeSeries[8]
			),
			dblTimeWidth
		);

		AssetGreekVertex agvInitial = new AssetGreekVertex (
			dblDerivativeXVAValue,
			-1.,
			0.,
			dblDerivativeValue
		);

		double dblGainOnBankDefaultInitial = -1. * (dblDerivativeXVAValue - cob.bankDefault (dblDerivativeXVAValue));

		double dblGainOnCounterPartyDefaultInitial = -1. * (dblDerivativeXVAValue - cob.counterPartyDefault (dblDerivativeXVAValue));

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

		for (int i = 0; i <= iNumTimeStep; ++i)
			aLSV[i] = LatentStateVertex.BankSenior (
				aJDVAsset[i],
				aJDVOvernightIndex[i],
				aJDVCollateral[i],
				aJDVBank[i],
				aJDVCounterParty[i],
				aJDVBankHazardRate[i],
				aJDVBankSeniorRecoveryRate[i],
				aJDVCounterPartyHazardRate[i],
				aJDVCounterPartyRecoveryRate[i]
			);

		EvolutionTrajectoryVertex etv = new EvolutionTrajectoryVertex (
			dblTime,
			aLSV[iNumTimeStep],
			ReplicationPortfolioVertex.Standard (
				1.,
				0.,
				0.,
				0.
			),
			agvInitial,
			dblGainOnBankDefaultInitial,
			dblGainOnCounterPartyDefaultInitial,
			0.,
			0.
		);

		for (int i = iNumTimeStep - 1; i >= 1; --i)
			etv = RunStep (
				tes,
				bko,
				etv,
				aLSV[i + 1],
				aLSV[i]
			);

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();
	}
}
