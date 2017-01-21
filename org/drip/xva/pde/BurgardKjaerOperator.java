
package org.drip.xva.pde;

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
 * BurgardKjaerOperator sets up the Parabolic Differential Equation PDE based on the Ito Evolution
 * 	Differential for the Reference Underlier Asset, as laid out in Burgard and Kjaer (2014). The References
 * 	are:
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

public class BurgardKjaerOperator {
	private org.drip.xva.custom.Settings _settings = null;
	private org.drip.xva.definition.TwoWayRiskyUniverse _twru = null;
	private org.drip.xva.definition.MasterAgreementCloseOut _maco = null;

	/**
	 * BurgardKjaerOperator Constructor
	 * 
	 * @param twru The Universe of Trade-able Assets
	 * @param maco The Master Agreement Close Out Boundary Conditions
	 * @param settings The XVA Control Settings
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BurgardKjaerOperator (
		final org.drip.xva.definition.TwoWayRiskyUniverse twru,
		final org.drip.xva.definition.MasterAgreementCloseOut maco,
		final org.drip.xva.custom.Settings settings)
		throws java.lang.Exception
	{
		if (null == (_twru = twru) || null == (_maco = maco) || null == (_settings = settings))
			throw new java.lang.Exception ("BurgardKjaerOperator Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Universe of Trade-able Assets
	 * 
	 * @return The Universe of Trade-able Assets
	 */

	public org.drip.xva.definition.TwoWayRiskyUniverse universe()
	{
		return _twru;
	}

	/**
	 * Retrieve the Close Out Boundary Condition
	 * 
	 * @return The Close Out Boundary Condition
	 */

	public org.drip.xva.definition.MasterAgreementCloseOut boundaryCondition()
	{
		return _maco;
	}

	/**
	 * Retrieve the XVA Control Settings
	 * 
	 * @return The XVA Control Settings
	 */

	public org.drip.xva.custom.Settings settings()
	{
		return _settings;
	}

	/**
	 * Generate the Time Increment using the Burgard Kjaer Scheme
	 * 
	 * @param si The Spread Intensity Instance
	 * @param eet The Edge Evolution Trajectory Instance
	 * 
	 * @return The Time Increment using the Burgard Kjaer Scheme
	 */

	public org.drip.xva.pde.LevelBurgardKjaerRun timeIncrementRun (
		final org.drip.xva.definition.SpreadIntensity si,
		final org.drip.xva.derivative.EdgeEvolutionTrajectory eet)
	{
		if (null == si || null == eet) return null;

		double dblTime = eet.time();

		double dblBankDefaultCloseOut = eet.gainOnBankDefault();

		org.drip.xva.definition.UniverseSnapshot us = eet.tradeableAssetSnapshot();

		double dblDerivativeXVAValue = eet.edgeReferenceUnderlierGreek().derivativeXVAValue();

		double dblBankDefaultDerivativeValue = dblDerivativeXVAValue + dblBankDefaultCloseOut;

		try {
			return new org.drip.xva.pde.LevelBurgardKjaerRun (
				-1. * new org.drip.xva.pde.ParabolicDifferentialOperator (_twru.referenceUnderlier()).apply (
					eet,
					us.assetNumeraire().finish()
				),
				_twru.creditRiskFreeBond().priceNumeraire().driftLDEV().value (
					new org.drip.measure.process.MarginalSnap (
						dblTime,
						us.creditRiskFreeNumeraire().finish()
					)
				) * dblDerivativeXVAValue,
				si.bankFundingSpread() * (
					dblBankDefaultDerivativeValue > 0. ? dblBankDefaultDerivativeValue : 0.
				),
				-1. * si.bankDefaultIntensity() * dblBankDefaultCloseOut,
				-1. * si.counterPartyDefaultIntensity() * eet.gainOnCounterPartyDefault()
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Time Increment Run Attribution using the Burgard Kjaer Scheme
	 * 
	 * @param si The Spread Intensity Instance
	 * @param eet The Edge Evolution Trajectory Instance
	 * 
	 * @return The Time Increment Run Attribution using the Burgard Kjaer Scheme
	 */

	public org.drip.xva.pde.LevelBurgardKjaerAttribution timeIncrementRunAttribution (
		final org.drip.xva.definition.SpreadIntensity si,
		final org.drip.xva.derivative.EdgeEvolutionTrajectory eet)
	{
		if (null == si || null == eet) return null;

		double dblTime = eet.time();

		double dblBankDefaultIntensity = si.bankDefaultIntensity();

		double dblCounterPartyDefaultIntensity = si.counterPartyDefaultIntensity();

		org.drip.xva.definition.UniverseSnapshot us = eet.tradeableAssetSnapshot();

		double dblDerivativeXVAValue = eet.edgeReferenceUnderlierGreek().derivativeXVAValue();

		double dblCloseOutMTM = org.drip.xva.custom.Settings.CLOSEOUT_GREGORY_LI_TANG ==
			_settings.closeOutScheme() ? dblDerivativeXVAValue : dblDerivativeXVAValue;

		double dblBankExposure = dblCloseOutMTM > 0. ? dblCloseOutMTM : _maco.bankRecovery() *
			dblCloseOutMTM;

		try {
			return new org.drip.xva.pde.LevelBurgardKjaerAttribution (
				-1. * new org.drip.xva.pde.ParabolicDifferentialOperator (_twru.referenceUnderlier()).apply (
					eet,
					us.assetNumeraire().finish()
				),
				_twru.creditRiskFreeBond().priceNumeraire().driftLDEV().value (
					new org.drip.measure.process.MarginalSnap (
						dblTime,
						us.creditRiskFreeNumeraire().finish()
					)
				) * dblDerivativeXVAValue,
				(dblBankDefaultIntensity + dblCounterPartyDefaultIntensity) * dblDerivativeXVAValue,
				si.bankFundingSpread() * dblBankExposure,
				-1. * dblBankDefaultIntensity * dblBankExposure,
				-1. * dblCounterPartyDefaultIntensity * (dblCloseOutMTM < 0. ? dblCloseOutMTM :
					_maco.counterPartyRecovery() * dblCloseOutMTM)
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
