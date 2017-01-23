
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

	private static final void RunStep (
		final MasterAgreementCloseOut maco,
		final SpreadIntensity si,
		final double dblXVADerivativeValue,
		final double dblXVADerivativeValueDelta,
		final double dblXVADerivativeValueGamma,
		final double dblDerivativeValue,
		final double dblAssetUnits,
		final double dblBankBondUnits,
		final double dblCounterPartyUnits,
		final double dblCashAccount,
		final MarginalEvolver meAsset,
		final MarginalEvolver meZeroCouponCreditRiskFreeBond,
		final MarginalEvolver meZeroCouponBankBond,
		final MarginalEvolver meZeroCouponCounterPartyBond,
		final BurgardKjaerOperator bko,
		final double dblZeroCouponCreditRiskFreeBondNumeraire,
		final double dblZeroCouponBankBondNumeraire,
		final double dblZeroCouponCounterPartyBondNumeraire,
		final double dblTime,
		final double dblTimeWidth)
		throws Exception
	{
		double dblGainOnBankDefault = -1. * (dblXVADerivativeValue - maco.bankDefault
			(dblXVADerivativeValue));

		double dblGainOnCounterPartyDefault = -1. * (dblXVADerivativeValue - maco.counterPartyDefault
			(dblXVADerivativeValue));

		EdgeReferenceUnderlierGreek erug = new EdgeReferenceUnderlierGreek (
			dblXVADerivativeValue,
			dblXVADerivativeValueDelta,
			dblXVADerivativeValueGamma,
			dblDerivativeValue
		);

		EdgeReplicationPortfolio erp = new EdgeReplicationPortfolio (
			dblAssetUnits,
			dblBankBondUnits,
			dblCounterPartyUnits,
			dblCashAccount
		);

		LevelRealization lrAsset = meAsset.weinerIncrement (
			new MarginalSnap (
				dblTime,
				dblXVADerivativeValue
			),
			dblTimeWidth
		);

		LevelRealization lrCounterPartyBond = meZeroCouponCounterPartyBond.weinerIncrement (
			new MarginalSnap (
				dblTime,
				dblZeroCouponCounterPartyBondNumeraire
			),
			dblTimeWidth
		);

		LevelRealization lrBankBond = meZeroCouponBankBond.weinerIncrement (
			new MarginalSnap (
				dblTime,
				dblZeroCouponBankBondNumeraire
			),
			dblTimeWidth
		);

		LevelRealization lrCreditRiskFreeBond = meZeroCouponCreditRiskFreeBond.weinerIncrement (
			new MarginalSnap (
				dblTime,
				dblZeroCouponCreditRiskFreeBondNumeraire
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
			dblGainOnBankDefault,
			dblGainOnCounterPartyDefault
		);

		LevelBurgardKjaerRun lbkrBase = bko.timeIncrementRun (
			si,
			eet
		);

		System.out.println ("\t||" +
			FormatUtil.FormatDouble (dblGainOnBankDefault, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (dblGainOnCounterPartyDefault, 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkrBase.derivativeXVAStochasticGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkrBase.derivativeXVARiskFreeGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkrBase.derivativeXVAFundingGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkrBase.derivativeXVABankDefaultGrowth(), 1, 6, 1.) + " | " +
			FormatUtil.FormatDouble (lbkrBase.derivativeXVACounterPartyDefaultGrowth(), 1, 6, 1.) + " ||"
		);
	}

	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

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
		double dblTimeWidth = 1. / 12.;
		double dblTime = 1.;
		double dblTerminalXVADerivativeValue = 1.;

		Settings settings = new Settings (Settings.CLOSEOUT_GREGORY_LI_TANG);

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
		double dblXVADerivativeValue = dblTerminalXVADerivativeValue;

		RunStep (
			maco,
			si,
			dblXVADerivativeValue,
			1.,
			0.,
			dblDerivativeValue,
			1.,
			0.,
			0.,
			0.,
			meAsset,
			meZeroCouponCreditRiskFreeBond,
			meZeroCouponBankBond,
			meZeroCouponCounterPartyBond,
			bko,
			1.,
			1.,
			1.,
			dblTime,
			dblTimeWidth
		);
	}
}
