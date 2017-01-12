
package org.drip.xva.collateral;

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
 * FundingBasisEvolver implements a Two Factor Stochastic Funding Model Evolver with a Log Normal Forward
 * 	Process and a Mean Reverting Diffusion Process for the Funding Spread. The References are:
 *  
 *  - Antonov, A., and M. Arneguy (2009): Analytical Formulas for Pricing CMS Products in the LIBOR Market
 *  	Model with Stochastic Volatility, https://papers.ssrn.com/sol3/Papers.cfm?abstract_id=1352606, eSSRN.
 *  
 *  - Burgard, C., and M. Kjaer (2009): Modeling and successful Management of Credit Counter-party Risk of
 *  	Derivative Portfolios, ICBI Conference, Rome.
 *  
 *  - Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk, Risk 20 (2) 86-90.
 *  
 *  - Johannes, M., and S. Sundaresan (2007): Pricing Collateralized Swaps, Journal of Finance 62 383-410.
 * 
 *  - Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing, Risk
 *  	21 (2) 97-102.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FundingBasisEvolver {
	private double _dblCorrelation = java.lang.Double.NaN;
	private org.drip.measure.process.MarginalEvolverLogarithmic _pmlUnderlying = null;
	private org.drip.measure.process.MarginalEvolverMeanReversion _pmmrFundingSpread = null;

	/**
	 * FundingBasisEvolver Constructor
	 * 
	 * @param pmlUnderlying The Underlying Dynamics Stochastic Process
	 * @param pmmrFundingSpread The Funding Spread Dynamics Stochastic Process
	 * @param dblCorrelation Correlation between the Underlying and the Funding Spread Processes
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public FundingBasisEvolver (
		final org.drip.measure.process.MarginalEvolverLogarithmic pmlUnderlying,
		final org.drip.measure.process.MarginalEvolverMeanReversion pmmrFundingSpread,
		final double dblCorrelation)
		throws java.lang.Exception
	{
		if (null == (_pmlUnderlying = pmlUnderlying) || null == (_pmmrFundingSpread = pmmrFundingSpread) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblCorrelation = dblCorrelation) || 1. <
				_dblCorrelation || -1. > _dblCorrelation)
			throw new java.lang.Exception ("FundingBasisEvolver Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Underlying Dynamics Stochastic Process
	 * 
	 * @return The Underlying Dynamics Stochastic Process
	 */

	public org.drip.measure.process.MarginalEvolverLogarithmic underlyingProcess()
	{
		return _pmlUnderlying;
	}

	/**
	 * Retrieve the Funding Spread Dynamics Stochastic Process
	 * 
	 * @return The Funding Spread Dynamics Stochastic Process
	 */

	public org.drip.measure.process.MarginalEvolverMeanReversion fundingSpreadProcess()
	{
		return _pmmrFundingSpread;
	}

	/**
	 * Retrieve the Correlation between the Underlying and the Funding Spread Processes
	 * 
	 * @return The Correlation between the Underlying and the Funding Spread Processes
	 */

	public double underlyingFundingSpreadCorrelation()
	{
		return _dblCorrelation;
	}

	/**
	 * Generate the Dynamics of the Marginal Process for the CSA Forward
	 * 
	 * @return The Dynamics of the Marginal Process for the CSA Forward
	 */

	public org.drip.measure.process.MarginalEvolver csaForwardProcess()
	{
		try {
			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.process.MarginalSnap ms)
					throws java.lang.Exception
				{
					return 0.;
				}
			};

			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.process.MarginalSnap ms)
					throws java.lang.Exception
				{
					if (null == ms)
						throw new java.lang.Exception
							("FundingBasisEvolver::CSAForwardVolatilityLDEV::value => Invalid Inputs");

					return ms.value() * _pmlUnderlying.volatility();
				}
			};

			return new org.drip.measure.process.MarginalEvolver (ldevDrift, ldevVolatility);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Dynamics of the Marginal Process for the Funding Numeraire
	 * 
	 * @param strTenor The Tenor of the Underlying Forward
	 * 
	 * @return The Dynamics of the Marginal Process for the Funding Numeraire
	 */

	public org.drip.measure.process.MarginalEvolver fundingNumeraireProcess (
		final java.lang.String strTenor)
	{
		try {
			double dblMeanReversionSpeed = _pmmrFundingSpread.meanReversionRate();

			double dblB = org.drip.analytics.support.Helper.TenorToYearFraction (strTenor);

			if (0. != dblMeanReversionSpeed)
				dblB = (1. - java.lang.Math.exp (-1. * dblMeanReversionSpeed * dblB)) /
					dblMeanReversionSpeed;

			final double dblPiterbarg2010BFactor = dblB;

			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.process.MarginalSnap ms)
					throws java.lang.Exception
				{
					return 0.;
				}
			};

			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.process.MarginalSnap ms)
					throws java.lang.Exception
				{
					if (null == ms)
						throw new java.lang.Exception
							("FundingBasisEvolver::CSAFundingNumeraireLDEV::value => Invalid Inputs");

					return -1. * ms.value() * dblPiterbarg2010BFactor * _pmmrFundingSpread.volatility();
				}
			};

			return new org.drip.measure.process.MarginalEvolver (ldevDrift, ldevVolatility);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Dynamics of the Marginal Process for the Funding Spread Numeraire
	 * 
	 * @param strTenor The Tenor of the Underlying Forward
	 * 
	 * @return The Dynamics of the Marginal Process for the Funding Spread Numeraire
	 */

	public org.drip.measure.process.MarginalEvolver fundingSpreadNumeraireProcess (
		final java.lang.String strTenor)
	{
		try {
			double dblMeanReversionSpeed = _pmmrFundingSpread.meanReversionRate();

			double dblB = org.drip.analytics.support.Helper.TenorToYearFraction (strTenor);

			if (0. != dblMeanReversionSpeed)
				dblB = (1. - java.lang.Math.exp (-1. * dblMeanReversionSpeed * dblB)) /
					dblMeanReversionSpeed;

			final double dblPiterbarg2010BFactor = dblB;

			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevDrift = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.process.MarginalSnap ms)
					throws java.lang.Exception
				{
					return 0.;
				}
			};

			org.drip.measure.process.LocalDeterministicEvolutionFunction ldevVolatility = new
				org.drip.measure.process.LocalDeterministicEvolutionFunction() {
				@Override public double value (
					final org.drip.measure.process.MarginalSnap ms)
					throws java.lang.Exception
				{
					if (null == ms)
						throw new java.lang.Exception
							("FundingBasisEvolver::CSAFundingSpreadNumeraireLDEV::value => Invalid Inputs");

					return -1. * ms.value() * dblPiterbarg2010BFactor * _pmmrFundingSpread.volatility();
				}
			};

			return new org.drip.measure.process.MarginalEvolver (ldevDrift, ldevVolatility);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the CSA vs. No CSA Forward Ratio
	 * 
	 * @param strTenor The Tenor of the Underlying Forward
	 * 
	 * @return The CSA vs. No CSA Forward Ratio
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double CSANoCSARatio (
		final java.lang.String strTenor)
		throws java.lang.Exception
	{
		double dblUnderlyingVolatility = _pmlUnderlying.volatility();

		double dblFundingSpreadVolatility = _pmmrFundingSpread.volatility();

		double dblMeanReversionSpeed = _pmmrFundingSpread.meanReversionRate();

		double dblMaturity = org.drip.analytics.support.Helper.TenorToYearFraction (strTenor);

		if (0. == dblMeanReversionSpeed)
			return java.lang.Math.exp (-0.5 * _dblCorrelation * dblUnderlyingVolatility *
				dblFundingSpreadVolatility * dblMaturity * dblMaturity);

		double dblB = (1. - java.lang.Math.exp (-1. * dblMeanReversionSpeed * dblMaturity)) /
			dblMeanReversionSpeed;

		return java.lang.Math.exp (-1. * _dblCorrelation * dblUnderlyingVolatility *
			dblFundingSpreadVolatility * (dblMaturity - dblB) / dblMeanReversionSpeed);
	}
}
