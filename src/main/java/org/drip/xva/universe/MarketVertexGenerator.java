
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
	private int[] _aiDate = null;
	private double[] _adblTimeWidth = null;
	private double[][] _aadblCorrelationMatrix = null;

	private org.drip.measure.process.DiffusionEvolver _deAssetNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deCollateralSchemeNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deOvernightIndexPolicyNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSeniorFundingNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSubordinateFundingNumeraire = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyFundingNumeraire = null;

	private org.drip.measure.process.DiffusionEvolver _deBankHazardRate = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSeniorRecoveryRate = null;
	private org.drip.measure.process.DiffusionEvolver _deBankSubordinateRecoveryRate = null;

	private org.drip.measure.process.DiffusionEvolver _deCounterPartyHazardRate = null;
	private org.drip.measure.process.DiffusionEvolver _deCounterPartyRecoveryRate = null;

	/**
	 * Retrieve the Vertex Date Array
	 * 
	 * @return The Vertex Date Array
	 */

	public int[] vertexDates()
	{
		return _aiDate;
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
	 * Retrieve the Asset Numeraire Evolver
	 * 
	 * @return The Asset Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver assetNumeraireEvolver()
	{
		return _deAssetNumeraire;
	}

	/**
	 * Retrieve the Overnight Index Policy Numeraire Evolver
	 * 
	 * @return The Overnight Index Policy Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver overnightIndexPolicyNumeraireEvolver()
	{
		return _deOvernightIndexPolicyNumeraire;
	}

	/**
	 * Retrieve the Collateral Scheme Numeraire Evolver
	 * 
	 * @return The Collateral Scheme Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver collateralSchemeNumeraireEvolver()
	{
		return _deCollateralSchemeNumeraire;
	}

	/**
	 * Retrieve the Bank Senior Funding Numeraire Evolver
	 * 
	 * @return The Bank Senior Funding Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSeniorFundingNumeraireEvolver()
	{
		return _deBankSeniorFundingNumeraire;
	}

	/**
	 * Retrieve the Bank Subordinate Funding Numeraire Evolver
	 * 
	 * @return The Bank Subordinate Funding Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver bankSubordinateFundingNumeraireEvolver()
	{
		return _deBankSubordinateFundingNumeraire;
	}

	/**
	 * Retrieve the Counter Party Funding Numeraire Evolver
	 * 
	 * @return The Counter Party Funding Numeraire Evolver
	 */

	public org.drip.measure.process.DiffusionEvolver counterPartyFundingNumeraireEvolver()
	{
		return _deCounterPartyFundingNumeraire;
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

	public org.drip.xva.universe.MarketVertex[] marketVertex (
		final int iValueDate,
		final org.drip.xva.universe.MarketVertex mvInitial,
		final boolean bBankDefaultIndicator,
		final boolean bCounterPartyDefaultIndicator)
	{
		if (null == mvInitial) return null;

		int iNumForwardVertex = _aiDate.length;
		double[] adblBankDefaultIndicator = null;
		double dblBankSurvivalProbabilityExponent = 0.;
		double[] adblCounterPartyDefaultIndicator = null;
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
			org.drip.xva.universe.MarketVertex[iNumForwardVertex + 1];
		aMV[0] = mvInitial;

		double[][] aadblUnitEvolverSequence = org.drip.quant.linearalgebra.Matrix.Transpose
			(org.drip.measure.discrete.SequenceGenerator.GaussianJoint (iNumForwardVertex,
				_aadblCorrelationMatrix));

		if (null == aadblUnitEvolverSequence) return null;

		if (bBankDefaultIndicator)
			adblBankDefaultIndicator = org.drip.measure.discrete.SequenceGenerator.Uniform
				(iNumForwardVertex);
		else {
			adblBankDefaultIndicator = new double[iNumForwardVertex];

			for (int iForwardVertex = 0; iForwardVertex < iNumForwardVertex; ++iForwardVertex)
				adblBankDefaultIndicator[iForwardVertex] = 0.;
		}

		if (bCounterPartyDefaultIndicator)
			adblCounterPartyDefaultIndicator = org.drip.measure.discrete.SequenceGenerator.Uniform
				(iNumForwardVertex);
		else {
			adblCounterPartyDefaultIndicator = new double[iNumForwardVertex];

			for (int iForwardVertex = 0; iForwardVertex < iNumForwardVertex; ++iForwardVertex)
				adblCounterPartyDefaultIndicator[iForwardVertex] = 0.;
		}

		org.drip.xva.universe.EntityMarketVertex emvBankInitial = mvInitial.bank();

		double dblSubordinateRecoveryRate = emvBankInitial.subordinateRecoveryRate();

		org.drip.xva.universe.EntityMarketVertex emvCounterPartyInitial = mvInitial.bank();

		double dblSubordinateFundingNumeraire = emvBankInitial.subordinateFundingNumeraire();

		try {
			aJDVAssetNumeraire = null == _deAssetNumeraire ? null : _deAssetNumeraire.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate, mvInitial.assetNumeraire(), 0.,
					false), org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
						aadblUnitEvolverSequence[0]), _adblTimeWidth);

			aJDVOvernightIndexPolicyNumeraire = _deOvernightIndexPolicyNumeraire.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					mvInitial.overnightPolicyIndexNumeraire(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[1]), _adblTimeWidth);

			aJDVCollateralSchemeNumeraire = _deCollateralSchemeNumeraire.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					mvInitial.collateralSchemeNumeraire(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[2]), _adblTimeWidth);

			aJDVBankHazardRate = _deBankHazardRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate, emvBankInitial.hazardRate(),
					0., false), org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
						aadblUnitEvolverSequence[3]), _adblTimeWidth);

			aJDVBankSeniorFundingNumeraire = _deBankSeniorFundingNumeraire.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					emvBankInitial.seniorFundingNumeraire(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[4]), _adblTimeWidth);

			aJDVBankSeniorRecoveryRate = _deBankSeniorRecoveryRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					emvBankInitial.seniorRecoveryRate(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[5]), _adblTimeWidth);

			aJDVBankSubordinateFundingNumeraire = !org.drip.quant.common.NumberUtil.IsValid
				(dblSubordinateFundingNumeraire) ? null : _deBankSubordinateFundingNumeraire.vertexSequence
					(new org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
						dblSubordinateFundingNumeraire, 0., false),
							org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
								aadblUnitEvolverSequence[6]), _adblTimeWidth);

			aJDVBankSubordinateRecoveryRate = !org.drip.quant.common.NumberUtil.IsValid
				(dblSubordinateRecoveryRate) ? null : _deBankSubordinateRecoveryRate.vertexSequence (new
					org.drip.measure.realization.JumpDiffusionVertex (iValueDate, dblSubordinateRecoveryRate,
						0., false), org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion
							(_adblTimeWidth, aadblUnitEvolverSequence[7]), _adblTimeWidth);

			aJDVCounterPartyHazardRate = _deCounterPartyHazardRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					emvCounterPartyInitial.hazardRate(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[8]), _adblTimeWidth);

			aJDVCounterPartyFundingNumeraire = _deCounterPartyFundingNumeraire.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					emvCounterPartyInitial.seniorFundingNumeraire(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[9]), _adblTimeWidth);

			aJDVCounterPartyRecoveryRate = _deCounterPartyRecoveryRate.vertexSequence (new
				org.drip.measure.realization.JumpDiffusionVertex (iValueDate,
					emvCounterPartyInitial.seniorRecoveryRate(), 0., false),
						org.drip.measure.realization.JumpDiffusionEdgeUnit.Diffusion (_adblTimeWidth,
							aadblUnitEvolverSequence[10]), _adblTimeWidth);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int iVertex = 1; iVertex <= iNumForwardVertex; ++ iVertex) {
			dblBankSurvivalProbabilityExponent += aJDVBankHazardRate[iVertex - 1].value() *
				_adblTimeWidth[iVertex - 1];

		}

		return aMV;
	}
}
