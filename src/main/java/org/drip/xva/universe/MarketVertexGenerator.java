
package org.drip.xva.universe;

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
 * MarketVertexGenerator generates the Market Realizations at a Trajectory Vertex needed for computing the
 *  Valuation Adjustment. The References are:
 *  
 *  - Burgard, C., and M. Kjaer (2013): Funding Strategies, Funding Costs, Risk, 24 (12) 82-87.
 *  
 *  - Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party Risk
 *  	and Funding Costs, Journal of Credit Risk, 7 (3) 1-19.
 *  
 *  - Burgard, C., and M. Kjaer (2014): In the Balance, Risk, 24 (11) 72-75.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class MarketVertexGenerator {

	/**
	 * Asset Numeraire Wanderer Index
	 */

	public static final int ASSET = 0;

	/**
	 * Overnight Index Policy Numeraire Wanderer Index
	 */

	public static final int OVERNIGHT_INDEX_POLICY = 1;

	/**
	 * Collateral Scheme Numeraire Wanderer Index
	 */

	public static final int COLLATERAL_SCHEME = 2;

	/**
	 * Bank Hazard Rate Wanderer Index
	 */

	public static final int BANK_HAZARD_RATE = 3;

	/**
	 * Bank Senior Funding Wanderer Index
	 */

	public static final int BANK_SENIOR_FUNDING = 4;

	/**
	 * Bank Senior Recovery Rate Wanderer Index
	 */

	public static final int BANK_SENIOR_RECOVERY_RATE = 5;

	/**
	 * Bank Subordinate Funding Wanderer Index
	 */

	public static final int BANK_SUBORDINATE_FUNDING = 6;

	/**
	 * Bank Subordinate Recovery Rate Wanderer Index
	 */

	public static final int BANK_SUBORDINATE_RECOVERY_RATE = 7;

	/**
	 * Counter Party Hazard Rate Wanderer Index
	 */

	public static final int COUNTER_PARTY_HAZARD_RATE = 8;

	/**
	 * Counter Party Funding Wanderer Index
	 */

	public static final int COUNTER_PARTY_FUNDING = 9;

	/**
	 * Counter Party Recovery Rate Wanderer Index
	 */

	public static final int COUNTER_PARTY_RECOVERY_RATE = 10;

	private int _iSpotDate = -1;
	private int[] _aiEventDate = null;
	private double[] _adblTimeWidth = null;
	private double[][] _aadblCorrelationMatrix = null;

	private org.drip.xva.universe.Tradeable _tAsset = null;
	private org.drip.xva.universe.Tradeable _tOvernightIndexPolicy = null;
	private org.drip.xva.universe.Tradeable _tCollateralScheme = null;
	private org.drip.xva.universe.Tradeable _tBankSeniorFunding = null;
	private org.drip.xva.universe.Tradeable _tBankSubordinateFunding = null;
	private org.drip.xva.universe.Tradeable _tCounterPartyFunding = null;

	private org.drip.measure.process.DiffusionEvolver _deBankHazardRate = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSeniorRecoveryRate = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSubordinateRecoveryRate = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyHazardRate = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyRecoveryRate = null;

	/**
	 * MarketVertexGenerator Constructor
	 * 
	 * @param iSpotDate The Spot Date
	 * @param aiEventDate Array of the Event Dates
	 * @param aadblCorrelationMatrix The Correlation Matrix
	 * @param tAsset The Asset Tradeable
	 * @param tOvernightIndexPolicy The Overnight Index Policy Tradeable
	 * @param tCollateralScheme The Collateral Scheme Tradeable
	 * @param tBankSeniorFunding The Bank Senior Funding Tradeable
	 * @param tBankSubordinateFunding The Bank Subordinate Funding Tradeable
	 * @param tCounterPartyFunding The Counter Party Funding Tradeable
	 * @param deBankHazardRate The Bank Hazard Rate Diffusive Evolver
	 * @param deBankSeniorRecoveryRate The Bank Senior Recovery Rate Diffusive Evolver
	 * @param deBankSubordinateRecoveryRate The Bank Subordinate Rate Diffusive Evolver
	 * @param deCounterPartyHazardRate The Counter Party Hazard Rate Diffusive Evolver
	 * @param deCounterPartyRecoveryRate The Counter Party Recovery Rate Diffusive Evolver
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public MarketVertexGenerator (
		final int iSpotDate,
		final int[] aiEventDate,
		final double[][] aadblCorrelationMatrix,
		final org.drip.xva.universe.Tradeable tAsset,
		final org.drip.xva.universe.Tradeable tOvernightIndexPolicy,
		final org.drip.xva.universe.Tradeable tCollateralScheme,
		final org.drip.xva.universe.Tradeable tBankSeniorFunding,
		final org.drip.xva.universe.Tradeable tBankSubordinateFunding,
		final org.drip.xva.universe.Tradeable tCounterPartyFunding,
		final org.drip.measure.process.DiffusionEvolver deBankHazardRate,
		final org.drip.measure.process.DiffusionEvolver deBankSeniorRecoveryRate,
		final org.drip.measure.process.DiffusionEvolver deBankSubordinateRecoveryRate,
		final org.drip.measure.process.DiffusionEvolver deCounterPartyHazardRate,
		final org.drip.measure.process.DiffusionEvolver deCounterPartyRecoveryRate)
		throws java.lang.Exception
	{
		if (0 >= (_iSpotDate = iSpotDate) ||
			null == (_aiEventDate = aiEventDate) ||
			null == (_aadblCorrelationMatrix = aadblCorrelationMatrix) ||
			null == (_tOvernightIndexPolicy = tOvernightIndexPolicy) ||
			null == (_tCollateralScheme = tCollateralScheme) ||
			null == (_tBankSeniorFunding = tBankSeniorFunding) ||
			null == (_tCounterPartyFunding = tCounterPartyFunding) ||
			null == (_deBankHazardRate = deBankHazardRate) ||
			null == (_deBankSeniorRecoveryRate = deBankSeniorRecoveryRate) ||
			null == (_deCounterPartyHazardRate = deCounterPartyHazardRate) ||
			null == (_deCounterPartyRecoveryRate = deCounterPartyRecoveryRate))
			throw new java.lang.Exception ("MarketVertexGenerator Constructor => Invalid Inputs");

		_tAsset = tAsset;
		int iNumEventVertex = _aiEventDate.length;
		int iNumDimension = _aadblCorrelationMatrix.length;
		_tBankSubordinateFunding = tBankSubordinateFunding;
		_deBankSubordinateRecoveryRate = deBankSubordinateRecoveryRate;
		_adblTimeWidth = 0 == iNumEventVertex ? null : new double[iNumEventVertex];

		if (0 == iNumEventVertex || 11 != iNumDimension || 0. >= (_adblTimeWidth[0] = ((double)
			(_aiEventDate[0] - _iSpotDate)) / 365.25))
			throw new java.lang.Exception ("MarketVertexGenerator Constructor => Invalid Inputs");

		for (int iEventVertex = 1; iEventVertex < iNumEventVertex; ++iEventVertex) {
			if (0. >= (_adblTimeWidth[iEventVertex] = ((double) (_aiEventDate[iEventVertex] -
				_aiEventDate[iEventVertex - 1])) / 365.25))
				throw new java.lang.Exception ("MarketVertexGenerator Constructor => Invalid Inputs");
		}

		for (int iDimension = 0; iDimension < iNumDimension; ++iDimension) {
			if (null == _aadblCorrelationMatrix[iDimension] || 11 !=
				_aadblCorrelationMatrix[iDimension].length || !org.drip.quant.common.NumberUtil.IsValid
					(_aadblCorrelationMatrix[iDimension]))
				throw new java.lang.Exception ("MarketVertexGenerator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return The Spot Date
	 */

	public int spotDate()
	{
		return _iSpotDate;
	}

	/**
	 * Retrieve the Event Date Array
	 * 
	 * @return The Event Date Array
	 */

	public int[] eventDate()
	{
		return _aiEventDate;
	}

	/**
	 * Retrieve the Time Width Array
	 * 
	 * @return The Time Width Array
	 */

	public double[] timeWidth()
	{
		return _adblTimeWidth;
	}

	/**
	 * Retrieve the Latent State Correlation Matrix
	 * 
	 * @return The Latent State Correlation Matrix
	 */

	public double[][] correlationMatrix()
	{
		return _aadblCorrelationMatrix;
	}

	/**
	 * Retrieve the Asset Tradeable
	 * 
	 * @return The Asset Tradeable
	 */

	public org.drip.xva.universe.Tradeable asset()
	{
		return _tAsset;
	}

	/**
	 * Retrieve the Overnight Index Policy
	 * 
	 * @return The Overnight Index Policy
	 */

	public org.drip.xva.universe.Tradeable overnightIndexPolicy()
	{
		return _tOvernightIndexPolicy;
	}

	/**
	 * Retrieve the Collateral Scheme Tradeable
	 * 
	 * @return The Collateral Scheme Tradeable
	 */

	public org.drip.xva.universe.Tradeable collateralScheme()
	{
		return _tCollateralScheme;
	}

	/**
	 * Retrieve the Bank Senior Funding Tradeable
	 * 
	 * @return The Bank Senior Funding Tradeable
	 */

	public org.drip.xva.universe.Tradeable bankSeniorFunding()
	{
		return _tBankSeniorFunding;
	}

	/**
	 * Retrieve the Bank Subordinate Funding Tradeable
	 * 
	 * @return The Bank Subordinate Funding Tradeable
	 */

	public org.drip.xva.universe.Tradeable bankSubordinateFunding()
	{
		return _tBankSubordinateFunding;
	}

	/**
	 * Retrieve the Counter Party Funding Tradeable
	 * 
	 * @return The Counter Party Funding Tradeable
	 */

	public org.drip.xva.universe.Tradeable counterPartyFunding()
	{
		return _tCounterPartyFunding;
	}

	/**
	 * Retrieve the Bank Hazard Rate Evolver
	 * 
	 * @return The Bank Hazard Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankHazardRateEvolver()
	{
		return _deBankHazardRate;
	}

	/**
	 * Retrieve the Bank Senior Recovery Rate Evolver
	 * 
	 * @return The Bank Senior Recovery Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSeniorRecoveryRateEvolver()
	{
		return _deBankSeniorRecoveryRate;
	}

	/**
	 * Retrieve the Bank Subordinate Recovery Rate Evolver
	 * 
	 * @return The Bank Subordinate Recovery Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSubordinateRecoveryRateEvolver()
	{
		return _deBankSubordinateRecoveryRate;
	}

	/**
	 * Retrieve the Counter Party Rate Evolver
	 * 
	 * @return The Counter Party Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver counterPartyHazardRateEvolver()
	{
		return _deCounterPartyHazardRate;
	}

	/**
	 * Retrieve the Counter Party Recovery Rate Evolver
	 * 
	 * @return The Counter Party Recovery Rate Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver counterPartyRecoveryRateEvolver()
	{
		return _deCounterPartyRecoveryRate;
	}

	/**
	 * Generated the Sequence of the Simulated Market Vertexes
	 * 
	 * @param mvInitial The Initial Market Vertex
	 * 
	 * @return The Array of the Simulated Market Vertexes
	 */

	public org.drip.xva.universe.MarketVertex[] marketVertex (
		final org.drip.xva.universe.MarketVertex mvInitial)
	{
		if (null == mvInitial) return null;

		int iNumEventVertex = _aiEventDate.length;
		double dblBankSurvivalProbabilityExponent = 0.;
		double dblCounterPartySurvivalProbabilityExponent = 0.;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVAssetNumeraire = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVBankHazardRate = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVBankSeniorRecoveryRate = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVCounterPartyHazardRate = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVCounterPartyRecoveryRate = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVCollateralSchemeNumeraire = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVBankSeniorFundingNumeraire = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVBankSubordinateRecoveryRate = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVCounterPartyFundingNumeraire = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVOvernightIndexPolicyNumeraire = null;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDVBankSubordinateFundingNumeraire = null;
		org.drip.xva.universe.MarketVertex[] aMV = new
			org.drip.xva.universe.MarketVertex[iNumEventVertex + 1];
		int iTerminalDate = _aiEventDate[iNumEventVertex - 1];
		boolean bAssetEvolutionOn = null != _tAsset;

		double[][] aadblUnitEvolverSequence = org.drip.quant.linearalgebra.Matrix.Transpose
			(org.drip.measure.discrete.SequenceGenerator.GaussianJoint (iNumEventVertex,
				_aadblCorrelationMatrix));

		if (null == aadblUnitEvolverSequence) return null;

		org.drip.xva.universe.EntityMarketVertex emvBankInitial = mvInitial.bank();

		org.drip.xva.universe.EntityMarketVertex emvCounterPartyInitial = mvInitial.bank();

		double dblOvernightIndexPolicyNumeraireStart = mvInitial.overnightPolicyIndexNumeraire();

		double dblCollateralSchemeNumeraireStart = mvInitial.collateralSchemeNumeraire();

		double dblBankSeniorFundingNumeraireStart = emvBankInitial.seniorFundingNumeraire();

		double dblBankSubordinateFundingNumeraireStart = emvBankInitial.subordinateFundingNumeraire();

		double dblBankSubordinateRecoveryRateStart = emvBankInitial.subordinateRecoveryRate();

		double dblCounterPartyFundingNumeraireStart = emvCounterPartyInitial.seniorFundingNumeraire();

		boolean bSingleBankBond = !org.drip.quant.common.NumberUtil.IsValid
			(dblBankSubordinateFundingNumeraireStart) || !org.drip.quant.common.NumberUtil.IsValid
				(dblBankSubordinateFundingNumeraireStart);

		try {
			aJDVAssetNumeraire = !bAssetEvolutionOn ? null : _tAsset.numeraireEvolver().vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (_iSpotDate, mvInitial.assetNumeraire(), 0.,
					false), org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
						aadblUnitEvolverSequence[ASSET]), _adblTimeWidth);

			aJDVOvernightIndexPolicyNumeraire =
				_tOvernightIndexPolicy.numeraireEvolver().vertexSequenceReverse (new
					org.drip.measure.realization.JumpDiffusionVertex (iTerminalDate,
						dblOvernightIndexPolicyNumeraireStart, 0., false),
							org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
								aadblUnitEvolverSequence[OVERNIGHT_INDEX_POLICY]), _adblTimeWidth);

			aJDVCollateralSchemeNumeraire = _tCollateralScheme.numeraireEvolver().vertexSequenceReverse (new
				org.drip.measure.realization.JumpDiffusionVertex (iTerminalDate,
					dblCollateralSchemeNumeraireStart, 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[COLLATERAL_SCHEME]), _adblTimeWidth);

			aJDVBankHazardRate = _deBankHazardRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (_iSpotDate, emvBankInitial.hazardRate(),
					0., false), org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
						aadblUnitEvolverSequence[BANK_HAZARD_RATE]), _adblTimeWidth);

			aJDVBankSeniorFundingNumeraire = _tBankSeniorFunding.numeraireEvolver().vertexSequenceReverse
				(new org.drip.measure.realization.JumpDiffusionVertex (iTerminalDate,
					dblBankSeniorFundingNumeraireStart, 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[BANK_SENIOR_FUNDING]), _adblTimeWidth);

			aJDVBankSeniorRecoveryRate = _deBankSeniorRecoveryRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (_iSpotDate,
					emvBankInitial.seniorRecoveryRate(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[BANK_SENIOR_RECOVERY_RATE]), _adblTimeWidth);

			aJDVBankSubordinateFundingNumeraire = bSingleBankBond ? null :
				_tBankSubordinateFunding.numeraireEvolver().vertexSequenceReverse (new
					org.drip.measure.realization.JumpDiffusionVertex (iTerminalDate,
						dblBankSubordinateFundingNumeraireStart, 0., false),
							org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
								aadblUnitEvolverSequence[BANK_SUBORDINATE_FUNDING]), _adblTimeWidth);

			aJDVBankSubordinateRecoveryRate = bSingleBankBond ? null :
				_deBankSubordinateRecoveryRate.vertexSequence (new
					org.drip.measure.realization.JumpDiffusionVertex (_iSpotDate,
						dblBankSubordinateRecoveryRateStart, 0., false),
							org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
								aadblUnitEvolverSequence[BANK_SUBORDINATE_RECOVERY_RATE]), _adblTimeWidth);

			aJDVCounterPartyHazardRate = _deCounterPartyHazardRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (_iSpotDate,
					emvCounterPartyInitial.hazardRate(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[COUNTER_PARTY_HAZARD_RATE]), _adblTimeWidth);

			aJDVCounterPartyFundingNumeraire = _tCounterPartyFunding.numeraireEvolver().vertexSequenceReverse
				(new org.drip.measure.realization.JumpDiffusionVertex (iTerminalDate,
					dblCounterPartyFundingNumeraireStart, 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[COUNTER_PARTY_FUNDING]), _adblTimeWidth);

			aJDVCounterPartyRecoveryRate = _deCounterPartyRecoveryRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (_iSpotDate,
					emvCounterPartyInitial.seniorRecoveryRate(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[COUNTER_PARTY_RECOVERY_RATE]), _adblTimeWidth);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int iEventVertex = 1; iEventVertex <= iNumEventVertex; ++iEventVertex) {
			double dblBankHazardRate = aJDVBankHazardRate[iEventVertex].value();

			double dblCounterPartyHazardRate = aJDVCounterPartyHazardRate[iEventVertex].value();

			double dblOvernightIndexPolicyNumeraireFinish =
				aJDVOvernightIndexPolicyNumeraire[iEventVertex].value();

			double dblCollateralSchemeNumeraireFinish =
				aJDVCollateralSchemeNumeraire[iEventVertex].value();

			double dblBankSeniorFundingNumeraireFinish =
				aJDVBankSeniorFundingNumeraire[iEventVertex].value();

			double dblBankSubordinateFundingNumeraireFinish = bSingleBankBond ? java.lang.Double.NaN :
				aJDVBankSubordinateFundingNumeraire[iEventVertex].value();

			double dblCounterPartyFundingNumeraireFinish =
				aJDVCounterPartyFundingNumeraire[iEventVertex].value();

			double dblTimeWidth = _adblTimeWidth[iEventVertex - 1];
			dblCounterPartySurvivalProbabilityExponent += dblCounterPartyHazardRate * dblTimeWidth;
			dblBankSurvivalProbabilityExponent += dblBankHazardRate * dblTimeWidth;
			double dblTimeWidthReciprocal = 1. / dblTimeWidth;

			double dblOvernightIndexPolicyRate = dblTimeWidthReciprocal * java.lang.Math.log
				(dblOvernightIndexPolicyNumeraireStart / dblOvernightIndexPolicyNumeraireFinish);

			try {
				org.drip.xva.universe.EntityMarketVertex emvBank = new org.drip.xva.universe.EntityMarketVertex (
					java.lang.Math.exp (-1. * dblBankSurvivalProbabilityExponent),
					dblBankHazardRate,
					aJDVBankSeniorRecoveryRate[iEventVertex].value(),
					dblTimeWidthReciprocal * java.lang.Math.log (dblBankSeniorFundingNumeraireStart /
						dblBankSeniorFundingNumeraireFinish) - dblOvernightIndexPolicyRate,
					dblBankSeniorFundingNumeraireFinish,
					bSingleBankBond ? java.lang.Double.NaN : aJDVBankSubordinateRecoveryRate[iEventVertex].value(),
					bSingleBankBond ? java.lang.Double.NaN : dblTimeWidthReciprocal * java.lang.Math.log
						(dblBankSubordinateFundingNumeraireStart / dblBankSubordinateFundingNumeraireFinish)
						- dblOvernightIndexPolicyRate,
					dblBankSubordinateFundingNumeraireFinish
				);

				org.drip.xva.universe.EntityMarketVertex emvCounterParty = new org.drip.xva.universe.EntityMarketVertex (
					java.lang.Math.exp (-1. * dblCounterPartySurvivalProbabilityExponent),
					dblCounterPartyHazardRate,
					aJDVCounterPartyRecoveryRate[iEventVertex].value(),
					dblTimeWidthReciprocal * java.lang.Math.log (dblCounterPartyFundingNumeraireStart /
						dblCounterPartyFundingNumeraireFinish) - dblOvernightIndexPolicyRate,
					dblCounterPartyFundingNumeraireFinish,
					java.lang.Double.NaN,
					java.lang.Double.NaN,
					java.lang.Double.NaN
				);

				aMV[iEventVertex] = new org.drip.xva.universe.MarketVertex (
					new org.drip.analytics.date.JulianDate (_aiEventDate[iEventVertex - 1]),
					bAssetEvolutionOn ? aJDVAssetNumeraire[iEventVertex].value() : java.lang.Double.NaN,
					dblOvernightIndexPolicyRate,
					dblOvernightIndexPolicyNumeraireFinish,
					dblTimeWidthReciprocal * java.lang.Math.log (dblCollateralSchemeNumeraireStart /
						dblCollateralSchemeNumeraireFinish) - dblOvernightIndexPolicyRate,
					dblCollateralSchemeNumeraireFinish,
					emvBank,
					emvCounterParty
				);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			dblCollateralSchemeNumeraireStart = dblCollateralSchemeNumeraireFinish;
			dblBankSeniorFundingNumeraireStart = dblBankSeniorFundingNumeraireFinish;
			dblCounterPartyFundingNumeraireStart = dblCounterPartyFundingNumeraireFinish;
			dblOvernightIndexPolicyNumeraireStart = dblOvernightIndexPolicyNumeraireFinish;
			dblBankSubordinateFundingNumeraireStart = dblBankSubordinateFundingNumeraireFinish;
		}

		try {
			aMV[0] = new org.drip.xva.universe.MarketVertex (
				mvInitial.anchor(),
				mvInitial.assetNumeraire(),
				aMV[1].overnightPolicyIndexRate(),
				aJDVOvernightIndexPolicyNumeraire[0].value(),
				aMV[1].collateralSchemeRate(),
				aJDVCollateralSchemeNumeraire[0].value(),
				new org.drip.xva.universe.EntityMarketVertex (
					emvBankInitial.survivalProbability(),
					emvBankInitial.hazardRate(),
					emvBankInitial.seniorRecoveryRate(),
					aMV[1].bank().seniorFundingSpread(),
					aJDVBankSeniorFundingNumeraire[0].value(),
					emvBankInitial.subordinateRecoveryRate(),
					aMV[1].bank().subordinateFundingSpread(),
					null == aJDVBankSubordinateFundingNumeraire ? java.lang.Double.NaN :
						aJDVBankSubordinateFundingNumeraire[0].value()
				),
				new org.drip.xva.universe.EntityMarketVertex (
					emvCounterPartyInitial.survivalProbability(),
					emvCounterPartyInitial.hazardRate(),
					emvCounterPartyInitial.seniorRecoveryRate(),
					aMV[1].counterParty().seniorFundingSpread(),
					aJDVCounterPartyFundingNumeraire[0].value(),
					java.lang.Double.NaN,
					java.lang.Double.NaN,
					java.lang.Double.NaN
				)
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		return aMV;
	}
}
