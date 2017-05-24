
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
	private double _dblTimeIncrement = java.lang.Double.NaN;
	private org.drip.xva.definition.CloseOutBilateral _cob = null;
	private org.drip.xva.definition.PDEEvolutionControl _pdeec = null;
	private org.drip.xva.universe.TradeableContainerMultilateral _tcm = null;

	/**
	 * TrajectoryEvolutionScheme Constructor
	 * 
	 * @param tcm The Universe of Trade-able Assets
	 * @param cob The Master Agreement Close Out Boundary Conditions
	 * @param pdeec The XVA Control Settings
	 * @param dblTimeIncrement The Time Increment
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TrajectoryEvolutionScheme (
		final org.drip.xva.universe.TradeableContainerMultilateral tcm,
		final org.drip.xva.definition.CloseOutBilateral cob,
		final org.drip.xva.definition.PDEEvolutionControl pdeec,
		final double dblTimeIncrement)
		throws java.lang.Exception
	{
		if (null == (_tcm = tcm) || null == (_cob = cob) || null == (_pdeec = pdeec) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblTimeIncrement = dblTimeIncrement))
			throw new java.lang.Exception ("TrajectoryEvolutionScheme Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Universe of Trade-able Assets
	 * 
	 * @return The Universe of Trade-able Assets
	 */

	public org.drip.xva.universe.TradeableContainerMultilateral universe()
	{
		return _tcm;
	}

	/**
	 * Retrieve the Close Out Boundary Condition
	 * 
	 * @return The Close Out Boundary Condition
	 */

	public org.drip.xva.definition.CloseOutBilateral boundaryCondition()
	{
		return _cob;
	}

	/**
	 * Retrieve the XVA Control Settings
	 * 
	 * @return The XVA Control Settings
	 */

	public org.drip.xva.definition.PDEEvolutionControl pdeec()
	{
		return _pdeec;
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
	 * @param etvStart The Starting Evolution Trajectory Vertex
	 * @param tcvm The Tradeable Container Vertex Instance
	 * 
	 * @return The CashAccountRebalancer Instance
	 */

	public org.drip.xva.derivative.CashAccountRebalancer rebalanceCash (
		final org.drip.xva.derivative.EvolutionTrajectoryVertex etvStart,
		final org.drip.xva.universe.TradeableContainerVertexMultilateral tcvm)
	{
		if (null == etvStart || null == tcvm) return null;

		org.drip.xva.derivative.ReplicationPortfolioVertex rpvStart = etvStart.replicationPortfolioVertex();

		double dblAssetUnitsStart = rpvStart.assetUnits();

		double dblBankBondUnitsStart = rpvStart.bankBondUnits();

		double[] adblCounterPartyBondUnitsStart = rpvStart.counterPartyBondUnits();

		org.drip.measure.realization.JumpDiffusionEdge jdeAsset = tcvm.assetNumeraire();

		org.drip.measure.realization.JumpDiffusionEdge jdeBankBond = tcvm.zeroCouponBankBondNumeraire();

		org.drip.measure.realization.JumpDiffusionEdge[] aJDECounterPartyBond =
			tcvm.zeroCouponCounterPartyBondNumeraire();

		double dblLevelAssetCash = dblAssetUnitsStart * _tcm.asset().cashAccumulationRate() *
			jdeAsset.finish() * _dblTimeIncrement;

		double dblLevelCounterPartyCash = 0.;
		double dblCounterPartyPositionChange = 0.;
		int iNumCounterParty = aJDECounterPartyBond.length;

		org.drip.xva.universe.Tradeable[] aT = _tcm.zeroCouponCounterPartyBond();

		if (aT.length != iNumCounterParty) return null;

		for (int i = 0; i < iNumCounterParty; ++i) {
			dblLevelCounterPartyCash += adblCounterPartyBondUnitsStart[i] * aT[i].cashAccumulationRate() *
				aJDECounterPartyBond[i].finish() * _dblTimeIncrement;

			dblCounterPartyPositionChange += adblCounterPartyBondUnitsStart[i] *
				aJDECounterPartyBond[i].grossChange();
		}

		double dblCashAccountBalance = -1. * etvStart.assetGreekVertex().derivativeXVAValue() -
			dblBankBondUnitsStart * jdeBankBond.finish();

		double dblLevelBankCash = dblCashAccountBalance * (dblCashAccountBalance > 0. ?
			_tcm.zeroCouponCollateralBond().cashAccumulationRate() :
				_tcm.zeroCouponBankBond().cashAccumulationRate()) * _dblTimeIncrement;

		double dblLevelCashAccount = (dblLevelAssetCash + dblLevelCounterPartyCash + dblLevelBankCash) *
			_dblTimeIncrement;

		double dblLevelDerivativeXVAValue = -1. * (dblAssetUnitsStart * jdeAsset.grossChange() +
			dblBankBondUnitsStart * jdeBankBond.grossChange() + dblCounterPartyPositionChange +
				dblLevelCashAccount);

		try {
			return new org.drip.xva.derivative.CashAccountRebalancer (new
				org.drip.xva.derivative.CashAccountEdge (dblLevelAssetCash, dblLevelBankCash *
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
	 * @param etvStart The Starting Evolution Trajectory Vertex
	 * @param tcvm The Tradeable Container Vertex Instance
	 * @param agvFinish The Period End Asset Greek Vertex
	 * 
	 * @return The LevelEvolutionTrajectory Instance
	 */

	public org.drip.xva.derivative.EvolutionTrajectoryEdge move (
		final org.drip.xva.derivative.EvolutionTrajectoryVertex etvStart,
		final org.drip.xva.universe.TradeableContainerVertexMultilateral tcvm,
		final org.drip.xva.derivative.AssetGreekVertex agvFinish)
	{
		if (null == agvFinish) return null;

		org.drip.xva.derivative.CashAccountRebalancer car = rebalanceCash (etvStart, tcvm);

		if (null == car) return null;

		org.drip.measure.realization.JumpDiffusionEdge[] aJDECounterParty =
			tcvm.zeroCouponCounterPartyBondNumeraire();

		double dblGainOnBankDefault = 0.;
		int iNumCounterParty = aJDECounterParty.length;
		double[] adblCloseOutMTM = new double[iNumCounterParty];
		double[] adblGainOnBankDefault = new double[iNumCounterParty];
		double[] adblCounterPartyBondUnits = new double[iNumCounterParty];
		double[] adblGainOnCounterPartyDefault = new double[iNumCounterParty];

		double dblDerivativeXVAValue = agvFinish.derivativeXVAValue();

		org.drip.xva.derivative.CashAccountEdge cae = car.cashAccount();

		double dblCloseOutMTM = org.drip.xva.definition.PDEEvolutionControl.CLOSEOUT_GREGORY_LI_TANG ==
			_pdeec.closeOutScheme() ? agvFinish.derivativeValue() : dblDerivativeXVAValue;

		for (int i = 0; i < iNumCounterParty; ++i)
			adblCloseOutMTM[i] = dblCloseOutMTM;

		double[] adblCounterPartyGainOnBankDefault = _cob.bankDefault (adblCloseOutMTM);

		for (int i = 0; i < iNumCounterParty; ++i) {
			try {
				adblGainOnCounterPartyDefault[i] = -1. * (dblDerivativeXVAValue - _cob.counterPartyDefault
					(i, adblCloseOutMTM));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			dblGainOnBankDefault += (adblGainOnBankDefault[i] = -1. * (dblDerivativeXVAValue -
				adblCounterPartyGainOnBankDefault[i]));

			adblCounterPartyBondUnits[i] = adblGainOnCounterPartyDefault[i] / aJDECounterParty[i].finish();
		}

		try {
			double dblBankBondUnits = dblGainOnBankDefault / tcvm.zeroCouponBankBondNumeraire().finish();

			org.drip.xva.derivative.ReplicationPortfolioVertex prv = new
				org.drip.xva.derivative.ReplicationPortfolioVertex (
					-1. * agvFinish.derivativeXVAValueDelta(),
					dblBankBondUnits,
					adblCounterPartyBondUnits,
					etvStart.replicationPortfolioVertex().cashAccount() + cae.accumulation()
				);

			return new org.drip.xva.derivative.EvolutionTrajectoryEdge (
				etvStart,
				new org.drip.xva.derivative.EvolutionTrajectoryVertex (
					etvStart.time() + _dblTimeIncrement,
					tcvm,
					prv,
					agvFinish,
					adblGainOnBankDefault,
					adblGainOnCounterPartyDefault
				),
				cae
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Execute a Single Euler Time Step Walk
	 * 
	 * @param si The Spread Intensity Instance
	 * @param tcvm The Universe Snapshot
	 * @param bko The Burgard Kjaer Operator Instance
	 * @param etvStart The Starting ETV Instance
	 * 
	 * @return The Evolution Trajectory Edge
	 */

	public org.drip.xva.derivative.EvolutionTrajectoryEdge eulerWalk (
		final org.drip.xva.definition.SpreadIntensity si,
		final org.drip.xva.universe.TradeableContainerVertexMultilateral tcvm,
		final org.drip.xva.pde.BurgardKjaerOperator bko,
		final org.drip.xva.derivative.EvolutionTrajectoryVertex etvStart)
	{
		if (null == si || null == tcvm || null == bko || null == etvStart) return null;

		org.drip.xva.derivative.AssetGreekVertex agvStart = etvStart.assetGreekVertex();

		org.drip.xva.pde.BurgardKjaerEdgeRun lbkr = bko.timeIncrementRun (si, etvStart);

		double dblTimeStart = etvStart.time();

		double dblTimeWidth = timeIncrement();

		if (null == lbkr) return null;

		double dblTheta = lbkr.theta();

		double dblAssetNumeraireBump = lbkr.assetNumeraireBump();

		double dblThetaAssetNumeraireUp = lbkr.thetaAssetNumeraireUp();

		double dblThetaAssetNumeraireDown = lbkr.thetaAssetNumeraireDown();

		double dblDerivativeXVAValueDeltaFinish = agvStart.derivativeXVAValueDelta() + 0.5 *
			(dblThetaAssetNumeraireUp - dblThetaAssetNumeraireDown) * dblTimeWidth / dblAssetNumeraireBump;

		org.drip.measure.realization.JumpDiffusionEdge[] aJDECounterParty =
			tcvm.zeroCouponCounterPartyBondNumeraire();

		double dblGainOnBankDefaultFinish = 0.;
		int iNumCounterParty = aJDECounterParty.length;
		double[] adblCloseOutMTM = new double[iNumCounterParty];
		double[] adblGainOnBankDefaultFinish = new double[iNumCounterParty];
		double[] adblCounterPartyBondUnitsFinish = new double[iNumCounterParty];
		double[] adblGainOnCounterPartyDefaultFinish = new double[iNumCounterParty];

		double dblDerivativeXVAValueFinish = agvStart.derivativeXVAValue() - dblTheta * dblTimeWidth;

		for (int i = 0; i < iNumCounterParty; ++i)
			adblCloseOutMTM[i] = dblDerivativeXVAValueFinish;

		double[] adblCounterPartyGainOnBankDefault = _cob.bankDefault (adblCloseOutMTM);

		for (int i = 0; i < iNumCounterParty; ++i) {
			dblGainOnBankDefaultFinish += (adblGainOnBankDefaultFinish[i] = -1. *
				(dblDerivativeXVAValueFinish - adblCounterPartyGainOnBankDefault[i]));

			try {
				adblGainOnCounterPartyDefaultFinish[i] = -1. * (dblDerivativeXVAValueFinish -
					_cob.counterPartyDefault (i, adblCloseOutMTM));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			adblCounterPartyBondUnitsFinish[i] = adblGainOnCounterPartyDefaultFinish[i] /
				aJDECounterParty[i].finish();
		}

		org.drip.xva.derivative.CashAccountRebalancer car = rebalanceCash (etvStart, tcvm);

		if (null == car) return null;

		org.drip.xva.derivative.CashAccountEdge cae = car.cashAccount();

		try {
			org.drip.xva.derivative.EvolutionTrajectoryVertex etvFinish = new org.drip.xva.derivative.EvolutionTrajectoryVertex (
				dblTimeStart - dblTimeWidth,
				tcvm,
				new org.drip.xva.derivative.ReplicationPortfolioVertex (
					-1. * dblDerivativeXVAValueDeltaFinish,
					dblGainOnBankDefaultFinish / tcvm.zeroCouponBankBondNumeraire().finish(),
					adblCounterPartyBondUnitsFinish,
					etvStart.replicationPortfolioVertex().cashAccount() + cae.accumulation()
				),
				new org.drip.xva.derivative.AssetGreekVertex (
					dblDerivativeXVAValueFinish,
					dblDerivativeXVAValueDeltaFinish,
					agvStart.derivativeXVAValueGamma() + (dblThetaAssetNumeraireUp + dblThetaAssetNumeraireDown - 2. * dblTheta) * dblTimeWidth / (dblAssetNumeraireBump * dblAssetNumeraireBump),
					agvStart.derivativeValue() * java.lang.Math.exp (
						-1. * dblTimeWidth * _tcm.zeroCouponCollateralBond().priceNumeraire().evaluator().drift().value (
							new org.drip.measure.realization.JumpDiffusionVertex (
								dblTimeStart - 0.5 * dblTimeWidth,
								etvStart.tradeableAssetSnapshot().zeroCouponCollateralBondNumeraire().finish(),
								0.,
								false
							)
						)
					)
				),
				adblGainOnBankDefaultFinish,
				adblGainOnCounterPartyDefaultFinish
			);

			return new org.drip.xva.derivative.EvolutionTrajectoryEdge (etvStart, etvFinish, cae);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Execute a Sequential Array of Euler Time Step Walks
	 * 
	 * @param si The Spread Intensity Instance
	 * @param aTCVM Array of Universe Snapshot
	 * @param bko The Burgard Kjaer Operator Instance
	 * @param etvStart The Starting EET Instance
	 * 
	 * @return Array of LevelEvolutionTrajectory Instances
	 */

	public org.drip.xva.derivative.EvolutionTrajectoryEdge[] eulerWalk (
		final org.drip.xva.definition.SpreadIntensity si,
		final org.drip.xva.universe.TradeableContainerVertexMultilateral[] aTCVM,
		final org.drip.xva.pde.BurgardKjaerOperator bko,
		final org.drip.xva.derivative.EvolutionTrajectoryVertex etvStart)
	{
		if (null == aTCVM) return null;

		int iNumTimeStep = aTCVM.length;
		org.drip.xva.derivative.EvolutionTrajectoryVertex etv = etvStart;
		org.drip.xva.derivative.EvolutionTrajectoryEdge[] aETE = 1 >= iNumTimeStep ? null : new
			org.drip.xva.derivative.EvolutionTrajectoryEdge[iNumTimeStep - 1];

		if (0 == iNumTimeStep) return null;

		for (int i = iNumTimeStep - 2; i >= 0; --i) {
			if (null == (aETE[i] = eulerWalk (si, aTCVM[i], bko, etv))) return null;

			etv = aETE[i].vertexFinish();
		}

		return aETE;
	}
}
