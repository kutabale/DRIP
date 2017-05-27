
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
 * CorrelatedNumeraireXVAAttribution constructs the XVA PnL Attribution arising out of the Joint Evolution of
 * 	Numeraires - the Continuous Asset, the Collateral, the Bank, and the Counter-Party Numeraires involved in
 *  the Dynamic XVA Replication Portfolio of the Burgard and Kjaer (2011) Methodology. The References are:
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

public class CorrelatedNumeraireXVAAttribution {

	private static final EvolutionTrajectoryVertex RunStep (
		final TrajectoryEvolutionScheme tes,
		final SpreadIntensity si,
		final BurgardKjaerOperator bko,
		final EvolutionTrajectoryVertex etvStart,
		final TradeablesVertex tcvm)
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

		CloseOutBilateral cob = tes.boundaryCondition();

		BurgardKjaerEdgeAttribution bkea = bko.timeIncrementRunAttribution (
			si,
			etvStart
		);

		double dblTheta = bkea.theta();

		double dblAssetNumeraireBump = bkea.assetNumeraireBump();

		double dblThetaAssetNumeraireUp = bkea.thetaAssetNumeraireUp();

		double dblThetaAssetNumeraireDown = bkea.thetaAssetNumeraireDown();

		double dblDerivativeXVAValueDeltaFinish = agvStart.derivativeXVAValueDelta() +
			0.5 * (dblThetaAssetNumeraireUp - dblThetaAssetNumeraireDown) * dblTimeWidth / dblAssetNumeraireBump;

		double dblDerivativeXVAValueGammaFinish = agvStart.derivativeXVAValueGamma() +
			(dblThetaAssetNumeraireUp + dblThetaAssetNumeraireDown - 2. * dblTheta) * dblTimeWidth /
				(dblAssetNumeraireBump * dblAssetNumeraireBump);

		double dblDerivativeXVAValueFinish = dblDerivativeXVAValueStart - dblTheta * dblTimeWidth;

		double dblGainOnBankDefaultFinish = -1. * (dblDerivativeXVAValueFinish - cob.bankDefault
			(new double[] {dblDerivativeXVAValueFinish})[0]);

		double dblGainOnCounterPartyDefaultFinish = -1. * (dblDerivativeXVAValueFinish - cob.counterPartyDefault
			(0, new double[] {dblDerivativeXVAValueFinish}));

		org.drip.xva.derivative.CashAccountEdge lca = tes.rebalanceCash (
			etvStart,
			tcvm
		).cashAccount();

		double dblCashAccountAccumulationFinish = lca.accumulation();

		double dblAssetPriceFinish = tcvm.assetNumeraire().finish();

		double dblZeroCouponBankPriceFinish = tcvm.bankFundingNumeraire().finish();

		double dblZeroCouponCounterPartyPriceFinish = tcvm.counterPartyFundingNumeraire()[0].finish();

		ReplicationPortfolioVertex erpFinish = ReplicationPortfolioVertex.Standard (
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
			FormatUtil.FormatDouble (tcvm.collateralSchemeNumeraire().finish(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpFinish.assetNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpFinish.bankNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpFinish.counterPartyNumeraireUnits()[0], 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpFinish.cashAccount(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bkea.derivativeXVAFundingGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bkea.derivativeXVABankDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bkea.derivativeXVACounterPartyDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (bkea.derivativeXVAEarlyTerminationGrowth(), 1, 6, 1.) + " ||"
		);

		return new EvolutionTrajectoryVertex (
			dblTimeStart - dblTimeWidth,
			tcvm,
			erpFinish,
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
			new double[] {dblGainOnCounterPartyDefaultFinish}
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
			{1.00, 0.20, 0.15, 0.05}, // #0 ASSET
			{0.20, 1.00, 0.13, 0.25}, // #1 COLLATERAL
			{0.15, 0.13, 1.00, 0.00}, // #2 BANK
			{0.05, 0.25, 0.00, 1.00}  // #3 COUNTER PARTY
		};
		double dblAssetDrift = 0.06;
		double dblAssetVolatility = 0.15;
		double dblAssetRepo = 0.03;
		double dblAssetDividend = 0.02;
		double dblInitialAssetNumeraire = 1.;

		double dblZeroCouponCollateralBondDrift = 0.01;
		double dblZeroCouponCollateralBondVolatility = 0.05;
		double dblZeroCouponCollateralBondRepo = 0.005;
		double dblInitialCollateralNumeraire = 1.;

		double dblZeroCouponBankBondDrift = 0.03;
		double dblZeroCouponBankBondVolatility = 0.10;
		double dblZeroCouponBankBondRepo = 0.028;
		double dblBankHazardRate = 0.03;
		double dblBankRecoveryRate = 0.45;
		double dblInitialBankNumeraire = 1.;

		double dblZeroCouponCounterPartyBondDrift = 0.03;
		double dblZeroCouponCounterPartyBondVolatility = 0.10;
		double dblZeroCouponCounterPartyBondRepo = 0.028;
		double dblCounterPartyHazardRate = 0.05;
		double dblCounterPartyRecoveryRate = 0.30;
		double dblInitialCounterPartyNumeraire = 1.;

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
			dblBankRecoveryRate,
			new double[] {dblCounterPartyRecoveryRate}
		);

		DiffusionEvolver deAsset = new DiffusionEvolver (
			DiffusionEvaluatorLogarithmic.Standard (
				dblAssetDrift,
				dblAssetVolatility
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

		BurgardKjaerOperator bko = new BurgardKjaerOperator (
			tcm,
			cob,
			pdeec
		);

		SpreadIntensity si = new SpreadIntensity (
			dblZeroCouponBankBondDrift - dblZeroCouponCollateralBondDrift,
			(dblZeroCouponBankBondDrift - dblZeroCouponCollateralBondDrift) / dblBankRecoveryRate,
			new double[] {
				(dblZeroCouponCounterPartyBondDrift - dblZeroCouponCollateralBondDrift) / dblCounterPartyRecoveryRate
			}
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

		JumpDiffusionEdge[] aJDECollateral = deZeroCouponCollateralBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialCollateralNumeraire,
				0.,
				false
			),
			UnitRandomEdge.Diffusion (aadblNumeraireTimeSeries[1]),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aJDEBank = deZeroCouponBankBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialBankNumeraire,
				0.,
				false
			),
			UnitRandomEdge.JumpDiffusion (
				aadblNumeraireTimeSeries[2],
				adblBankDefaultIndicator
			),
			dblTimeWidth
		);

		JumpDiffusionEdge[] aJDECounterParty = deZeroCouponCounterPartyBond.incrementSequence (
			new JumpDiffusionVertex (
				0.,
				dblInitialCounterPartyNumeraire,
				0.,
				false
			),
			UnitRandomEdge.JumpDiffusion (
				aadblNumeraireTimeSeries[3],
				adblCounterPartyDefaultIndicator
			),
			dblTimeWidth
		);

		AssetGreekVertex eagInitial = new AssetGreekVertex (
			dblDerivativeXVAValue,
			-1.,
			0.,
			dblDerivativeValue
		);

		double dblGainOnBankDefaultInitial = -1. * (dblDerivativeXVAValue -
			cob.bankDefaultGross (new double[] {dblDerivativeXVAValue}));

		double dblGainOnCounterPartyDefaultInitial = -1. * (dblDerivativeXVAValue -
			cob.counterPartyDefault (0, new double[] {dblDerivativeXVAValue}));

		ReplicationPortfolioVertex erpInitial = ReplicationPortfolioVertex.Standard (
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

		System.out.println ("\t||            - Derivative XVA Funding Growth                                                                                                                            ||");

		System.out.println ("\t||            - Derivative XVA Bank Default Growth                                                                                                                       ||");

		System.out.println ("\t||            - Derivative XVA Counter Party Default Growth                                                                                                              ||");

		System.out.println ("\t||            - Derivative XVA Early Termination Growth                                                                                                                  ||");

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblTime, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (eagInitial.derivativeXVAValue(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (aJDEAsset[iNumTimeStep - 1].finish(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (aJDEBank[iNumTimeStep - 1].finish(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (aJDECounterParty[iNumTimeStep - 1].finish(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (aJDECollateral[iNumTimeStep - 1].finish(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpInitial.assetNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpInitial.bankNumeraireUnits(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpInitial.counterPartyNumeraireUnits()[0], 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erpInitial.cashAccount(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " ||"
		);

		EvolutionTrajectoryVertex eet = new EvolutionTrajectoryVertex (
			dblTime,
			TradeablesVertex.Standard (
				aJDEAsset[iNumTimeStep - 1],
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
			eagInitial,
			new double[] {dblGainOnBankDefaultInitial},
			new double[] {dblGainOnCounterPartyDefaultInitial}
		);

		for (int i = iNumTimeStep - 2; i >= 0; --i)
			eet = RunStep (
				tes,
				si,
				bko,
				eet,
				TradeablesVertex.Standard (
					aJDEAsset[i],
					aJDECollateral[i],
					aJDEBank[i],
					new JumpDiffusionEdge[] {aJDECounterParty[i]}
				)
			);

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();
	}
}
