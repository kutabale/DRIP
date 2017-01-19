
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
 * TrajectoryEvolutionScheme holds the Evolution Edges of a Trajectory evolved in a Dynamically Adaptive
 *  Manner, as laid out in Burgard and Kjaer (2014). The References are:
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

public class TrajectoryEvolutionScheme {

	/**
	 * Set the Close-out to the Derivative MTM according to Li and Tang (2007) or Gregory (2009) 
	 */

	public static final int CLOSEOUT_GREGORY_LI_TANG = 1;

	/**
	 * Set the Close-out to the Derivative XVA MTM according to Burgard and Kjaer (2014)
	 */

	public static final int CLOSEOUT_BURGARD_KJAER = 2;

	private int _iCloseOutScheme = CLOSEOUT_GREGORY_LI_TANG;
	private double _dblTimeIncrement = java.lang.Double.NaN;
	private org.drip.xva.definition.TwoWayRiskyUniverse _twru = null;
	private org.drip.xva.definition.MasterAgreementCloseOut _maco = null;

	/**
	 * EvolutionTrajectory Constructor
	 * 
	 * @param twru The Universe of Trade-able Assets
	 * @param maco The Master Agreement Close Out Boundary Conditions
	 * @param iCloseOutScheme The Close Out Scheme
	 * @param dblTimeIncrement The Time Increment
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TrajectoryEvolutionScheme (
		final org.drip.xva.definition.TwoWayRiskyUniverse twru,
		final org.drip.xva.definition.MasterAgreementCloseOut maco,
		final int iCloseOutScheme,
		final double dblTimeIncrement)
		throws java.lang.Exception
	{
		if (null == (_twru = twru) || null == (_maco = maco) || (CLOSEOUT_GREGORY_LI_TANG !=
			(_iCloseOutScheme = iCloseOutScheme) && CLOSEOUT_BURGARD_KJAER != _iCloseOutScheme) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblTimeIncrement = dblTimeIncrement))
			throw new java.lang.Exception ("LevelEvolutionTrajectory Constructor => Invalid Inputs");
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
	 * Retrieve the Close-out Scheme
	 * 
	 * @return The Close-out Scheme
	 */

	public int closeOutScheme()
	{
		return _iCloseOutScheme;
	}

	/**
	 * Retrieve the Evolution Time Increment
	 * 
	 * @return The Evolution Time Increment
	 */

	public double timeIncrement()
	{
		return _dblTimeIncrement;
	}

	/**
	 * Re-balance the Cash Account and generate the Derivative Value Update
	 * 
	 * @param eetStart The Starting Evolution Trajectory Edge
	 * @param us The Universe Snap-shot
	 * 
	 * @return The LevelEvolutionTrajectoryRebalanced Instance
	 */

	public org.drip.xva.derivative.CashAccountRebalancer rebalanceCash (
		final org.drip.xva.derivative.EdgeEvolutionTrajectory eetStart,
		final org.drip.xva.definition.UniverseSnapshot us)
	{
		if (null == eetStart || null == us) return null;

		org.drip.xva.derivative.EdgeReplicationPortfolio erpStart = eetStart.replicationPortfolio();

		double dblAssetUnitsStart = erpStart.assetUnits();

		double dblBankBondUnitsStart = erpStart.bankBondUnits();

		double dblCounterPartyBondUnitsStart = erpStart.counterPartyBondUnits();

		org.drip.measure.process.LevelRealization lrAsset = us.assetNumeraire();

		org.drip.measure.process.LevelRealization lrBankBond = us.bankBondNumeraire();

		org.drip.measure.process.LevelRealization lrCounterPartyBond = us.counterPartyBondNumeraire();

		double dblLevelAssetCash = dblAssetUnitsStart * _twru.referenceUnderlier().cashAccumulationRate() *
			lrAsset.finish() * _dblTimeIncrement;

		double dblLevelCounterPartyCash = dblCounterPartyBondUnitsStart *
			_twru.zeroCouponCounterPartyBond().cashAccumulationRate() * lrCounterPartyBond.finish() *
				_dblTimeIncrement;

		double dblCashAccountBalance = -1. * eetStart.edgeReferenceUnderlierGreek().derivativeXVAValue() -
			dblBankBondUnitsStart * lrBankBond.finish();

		double dblLevelBankCash = dblCashAccountBalance * (dblCashAccountBalance > 0. ?
			_twru.creditRiskFreeBond().cashAccumulationRate() :
				_twru.zeroCouponBankBond().cashAccumulationRate()) * _dblTimeIncrement;

		double dblLevelCashAccount = (dblLevelAssetCash + dblLevelCounterPartyCash + dblLevelBankCash) *
			_dblTimeIncrement;

		double dblLevelDerivativeXVAValue = -1. * (dblAssetUnitsStart * lrAsset.grossChange() +
			dblBankBondUnitsStart * lrBankBond.grossChange() + dblCounterPartyBondUnitsStart *
				lrCounterPartyBond.grossChange() + dblLevelCashAccount);

		try {
			return new org.drip.xva.derivative.CashAccountRebalancer (new
				org.drip.xva.derivative.LevelCashAccount (dblLevelAssetCash, dblLevelBankCash *
					 _dblTimeIncrement, dblLevelCounterPartyCash * _dblTimeIncrement),
					 	dblLevelDerivativeXVAValue);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Re-balance AND move the Cash Account and generate the Updated Derivative Value/Replication Portfolio
	 * 
	 * @param eetStart The Starting Evolution Trajectory Edge
	 * @param us The Universe Snap-shot
	 * @param erugFinish The Period End EdgeReferenceUnderlierGreek Instance
	 * 
	 * @return The LevelEvolutionTrajectory Instance
	 */

	public org.drip.xva.derivative.LevelEvolutionTrajectory move (
		final org.drip.xva.derivative.EdgeEvolutionTrajectory eetStart,
		final org.drip.xva.definition.UniverseSnapshot us,
		final org.drip.xva.derivative.EdgeReferenceUnderlierGreek erugFinish)
	{
		if (null == erugFinish) return null;

		org.drip.xva.derivative.CashAccountRebalancer car = rebalanceCash (eetStart, us);

		if (null == car) return null;

		org.drip.xva.derivative.LevelCashAccount lca = car.cashAccount();

		double dblDerivativeXVAValue = erugFinish.derivativeXVAValue();

		double dblCloseOutMTM = CLOSEOUT_GREGORY_LI_TANG == _iCloseOutScheme ? erugFinish.derivativeValue() :
			dblDerivativeXVAValue;

		try {
			double dblBankDefaultCloseOut = -1. * (dblDerivativeXVAValue - _maco.bankDefault
				(dblCloseOutMTM));

			double dblBankBondUnits = dblBankDefaultCloseOut / us.bankBondNumeraire().finish();

			double dblCounterPartyDefaultCloseOut = -1. * (dblDerivativeXVAValue - _maco.counterPartyDefault
				(dblCloseOutMTM));

			double dblCounterPartyBondUnits = dblCounterPartyDefaultCloseOut /
				us.counterPartyBondNumeraire().finish();

			org.drip.xva.derivative.EdgeReplicationPortfolio erp = new
				org.drip.xva.derivative.EdgeReplicationPortfolio (-1. * erugFinish.derivativeXVAValueDelta(),
					dblBankBondUnits, dblCounterPartyBondUnits, eetStart.replicationPortfolio().cashAccount()
						+ lca.accumulation());

			return new org.drip.xva.derivative.LevelEvolutionTrajectory (eetStart, new
				org.drip.xva.derivative.EdgeEvolutionTrajectory (eetStart.time() + _dblTimeIncrement, us,
					erp, erugFinish, dblBankDefaultCloseOut, dblCounterPartyDefaultCloseOut), lca);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
