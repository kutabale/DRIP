
package org.drip.sample.burgard2011;

import org.drip.measure.process.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.xva.custom.Settings;
import org.drip.xva.definition.*;
import org.drip.xva.derivative.*;
import org.drip.xva.pde.*;

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
 * BilateralXVAEvolver demonstrates the Bank and Counter-Party Default Based Derivative Price Evolution. The
 *  References are:
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

public class BilateralXVAEvolver {

	private static final EdgeEvolutionTrajectory RunStep (
		final TrajectoryEvolutionScheme tes,
		final SpreadIntensity si,
		final BurgardKjaerOperator bko,
		final EdgeEvolutionTrajectory eetStart)
		throws Exception
	{
		EdgeReferenceUnderlierGreek erugIn = eetStart.edgeReferenceUnderlierGreek();

		EdgeReplicationPortfolio erp = eetStart.replicationPortfolio();

		double dblDerivativeXVAValue = erugIn.derivativeXVAValue();

		MasterAgreementCloseOut maco = tes.boundaryCondition();

		double dblGainOnBankDefault = -1. * (dblDerivativeXVAValue - maco.bankDefault
			(dblDerivativeXVAValue));

		double dblGainOnCounterPartyDefault = -1. * (dblDerivativeXVAValue - maco.counterPartyDefault
			(dblDerivativeXVAValue));

		TwoWayRiskyUniverse twru = tes.universe();

		double dblTimeWidth = tes.timeIncrement();

		double dblTime = eetStart.time() - dblTimeWidth;

		UniverseSnapshot usStart = eetStart.tradeableAssetSnapshot();

		UniverseSnapshot usFinish = new UniverseSnapshot (
			twru.referenceUnderlier().priceNumeraire().weinerIncrement (
				new MarginalSnap (
					dblTime,
					usStart.assetNumeraire().finish()
				),
				dblTimeWidth
			),
			twru.creditRiskFreeBond().priceNumeraire().weinerIncrement (
				new MarginalSnap (
					dblTime,
					usStart.zeroCouponBondCollateralNumeraire().finish()
				),
				dblTimeWidth
			),
			twru.zeroCouponBankBond().priceNumeraire().weinerIncrement (
				new MarginalSnap (
					dblTime,
					usStart.zeroCouponBankBondNumeraire().finish()
				),
				dblTimeWidth
			),
			twru.zeroCouponCounterPartyBond().priceNumeraire().weinerIncrement (
				new MarginalSnap (
					dblTime,
					usStart.zeroCouponCounterPartyBondNumeraire().finish()
				),
				dblTimeWidth
			)
		);

		LevelBurgardKjaerRun lbkr = bko.timeIncrementRun (
			si,
			new EdgeEvolutionTrajectory (
				dblTime,
				usFinish,
				erp,
				erugIn,
				dblGainOnBankDefault,
				dblGainOnCounterPartyDefault
			)
		);

		double dblTheta = lbkr.theta();

		double dblAssetNumeraireChange = lbkr.assetNumeraireChange();

		double dblThetaAssetNumeraireUp = lbkr.thetaAssetNumeraireUp();

		double dblThetaAssetNumeraireDown = lbkr.thetaAssetNumeraireDown();

		double dblDerivativeXVAValueDeltaNew = erugIn.derivativeXVAValueDelta() +
			0.5 * (dblThetaAssetNumeraireUp - dblThetaAssetNumeraireDown) * dblTimeWidth / dblAssetNumeraireChange;

		double dblDerivativeXVAValueGammaNew = erugIn.derivativeXVAValueGamma() +
			(dblThetaAssetNumeraireUp + dblThetaAssetNumeraireDown - 2. * dblTheta) * dblTimeWidth /
				(dblAssetNumeraireChange * dblAssetNumeraireChange);

		double dblDerivativeXVAValueNew = dblDerivativeXVAValue - dblTheta * dblTimeWidth;

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblTime, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueNew, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueDeltaNew, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblDerivativeXVAValueGammaNew, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnBankDefault, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnCounterPartyDefault, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkr.derivativeXVAStochasticGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkr.derivativeXVARiskFreeGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkr.derivativeXVAFundingGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkr.derivativeXVABankDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkr.derivativeXVACounterPartyDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblThetaAssetNumeraireDown, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblTheta, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblThetaAssetNumeraireUp, 1, 6, 1.) + " ||"
		);

		org.drip.xva.derivative.LevelCashAccount lca = tes.rebalanceCash (
			eetStart,
			usFinish
		).cashAccount();

		return new EdgeEvolutionTrajectory (
			dblTime,
			usFinish,
			new EdgeReplicationPortfolio (
				-1. * dblDerivativeXVAValueDeltaNew,
				dblGainOnBankDefault / usFinish.zeroCouponBankBondNumeraire().finish(),
				dblGainOnCounterPartyDefault / usFinish.zeroCouponCounterPartyBondNumeraire().finish(),
				erp.cashAccount() + lca.accumulation()
			),
			new EdgeReferenceUnderlierGreek (
				dblDerivativeXVAValueNew,
				dblDerivativeXVAValueDeltaNew,
				dblDerivativeXVAValueGammaNew,
				erugIn.derivativeValue()
			),
			dblGainOnBankDefault,
			dblGainOnCounterPartyDefault
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
		double dblCreditRiskFreeDrift = 0.01;
		double dblCreditRiskFreeVolatility = 0.05;
		double dblCreditRiskFreeRepo = 0.005;
		double dblZeroCouponBankBondDrift = 0.03;
		double dblZeroCouponBankBondVolatility = 0.10;
		double dblZeroCouponBankBondRepo = 0.028;
		double dblZeroCouponCounterPartyBondDrift = 0.03;
		double dblZeroCouponCounterPartyBondVolatility = 0.10;
		double dblZeroCouponCounterPartyBondRepo = 0.028;
		double dblTimeWidth = 1. / 24.;
		double dblTime = 1.;
		double dblTerminalXVADerivativeValue = 1.;

		Settings settings = new Settings (
			Settings.CLOSEOUT_GREGORY_LI_TANG,
			dblSensitivityShiftFactor
		);

		MasterAgreementCloseOut maco = new MasterAgreementCloseOut (
			dblBankRecovery,
			dblCounterPartyRecovery
		);

		MarginalEvolver meAsset = MarginalEvolverLogarithmic.Standard (
			dblAssetDrift,
			dblAssetVolatility
		);

		MarginalEvolver meZeroCouponCreditRiskFreeBond = MarginalEvolverLogarithmic.Standard (
			dblCreditRiskFreeDrift,
			dblCreditRiskFreeVolatility
		);

		MarginalEvolver meZeroCouponBankBond = MarginalEvolverLogarithmic.Standard (
			dblZeroCouponBankBondDrift,
			dblZeroCouponBankBondVolatility
		);

		MarginalEvolver meZeroCouponCounterPartyBond = MarginalEvolverLogarithmic.Standard (
			dblZeroCouponCounterPartyBondDrift,
			dblZeroCouponCounterPartyBondVolatility
		);

		TradeableAsset taAsset = new TradeableAssetEquity (
			meAsset,
			dblAssetRepo,
			dblAssetDividend
		);

		TradeableAsset taCreditRiskFree = new TradeableAsset (
			meZeroCouponCreditRiskFreeBond,
			dblCreditRiskFreeRepo
		);

		TradeableAsset taZeroCouponBankBond = new TradeableAsset (
			meZeroCouponBankBond,
			dblZeroCouponBankBondRepo
		);

		TradeableAsset taZeroCouponCounterPartyBond = new TradeableAsset (
			meZeroCouponCounterPartyBond,
			dblZeroCouponCounterPartyBondRepo
		);

		TwoWayRiskyUniverse twru = new TwoWayRiskyUniverse (
			taAsset,
			taCreditRiskFree,
			taZeroCouponBankBond,
			taZeroCouponCounterPartyBond
		);

		TrajectoryEvolutionScheme tes = new TrajectoryEvolutionScheme (
			twru,
			maco,
			settings,
			dblTimeWidth
		);

		BurgardKjaerOperator bko = new BurgardKjaerOperator (
			twru,
			maco,
			settings
		);

		SpreadIntensity si = new SpreadIntensity (
			dblZeroCouponBankBondDrift - dblCreditRiskFreeDrift,
			(dblZeroCouponBankBondDrift - dblCreditRiskFreeDrift) / dblBankRecovery,
			(dblZeroCouponCounterPartyBondDrift - dblCreditRiskFreeDrift) / dblCounterPartyRecovery
		);

		double dblDerivativeValue = dblTerminalXVADerivativeValue;
		double dblDerivativeXVAValue = dblTerminalXVADerivativeValue;

		EdgeReferenceUnderlierGreek erug = new EdgeReferenceUnderlierGreek (
			dblDerivativeXVAValue,
			-1.,
			0.,
			dblDerivativeValue
		);

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
			FormatUtil.FormatDouble (erug.derivativeXVAValue(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erug.derivativeXVAValueDelta(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (erug.derivativeXVAValueGamma(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (-1. * (dblDerivativeXVAValue - maco.bankDefault (dblDerivativeXVAValue)), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (-1. * (dblDerivativeXVAValue - maco.counterPartyDefault (dblDerivativeXVAValue)), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (0., 1, 6, 1.) + " ||"
		);

		EdgeReplicationPortfolio erp = new EdgeReplicationPortfolio (
			1.,
			0.,
			0.,
			0.
		);

		LevelRealization lrAsset = meAsset.weinerIncrement (
			new MarginalSnap (
				dblTime,
				dblDerivativeValue
			),
			dblTimeWidth
		);

		LevelRealization lrCounterPartyBond = meZeroCouponCounterPartyBond.weinerIncrement (
			new MarginalSnap (
				dblTime,
				1.
			),
			dblTimeWidth
		);

		LevelRealization lrBankBond = meZeroCouponBankBond.weinerIncrement (
			new MarginalSnap (
				dblTime,
				1.
			),
			dblTimeWidth
		);

		LevelRealization lrCreditRiskFreeBond = meZeroCouponCreditRiskFreeBond.weinerIncrement (
			new MarginalSnap (
				dblTime,
				1.
			),
			dblTimeWidth
		);

		UniverseSnapshot us = new UniverseSnapshot (
			lrAsset,
			lrCreditRiskFreeBond,
			lrBankBond,
			lrCounterPartyBond
		);

		EdgeEvolutionTrajectory eet = new EdgeEvolutionTrajectory (
			dblTime,
			us,
			erp,
			erug,
			-1. * (dblDerivativeXVAValue - maco.bankDefault (dblDerivativeXVAValue)),
			-1. * (dblDerivativeXVAValue - maco.counterPartyDefault (dblDerivativeXVAValue))
		);

		for (dblTime -= dblTimeWidth; dblTime >= 0.; dblTime -= dblTimeWidth)
			eet = RunStep (
				tes,
				si,
				bko,
				eet
			);

		System.out.println ("\t||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------||");

		System.out.println();
	}
}
