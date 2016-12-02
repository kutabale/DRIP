
package org.drip.execution.principal;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
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
 * Almgren2003Estimator generates the Gross Profit Distribution and the Information Ratio for a given Level
 *  of Principal Discount for an Optimal Trajectory that is generated using the Almgren (2003) Scheme. The
 *  References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 16 (6) 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class Almgren2003Estimator extends org.drip.execution.principal.GrossProfitEstimator {
	private org.drip.execution.dynamics.Almgren2003Parameters _a2003p =  null;

	/**
	 * Almgren2003Estimator Constructor
	 * 
	 * @param a2003tt The Almgren 2003 Trading Trajectory Instance
	 * @param a2003p The Almgren 2003 Parameters Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public Almgren2003Estimator (
		final org.drip.execution.optimum.Almgren2003PowerImpactContinuous a2003tt,
		final org.drip.execution.dynamics.Almgren2003Parameters a2003p)
		throws java.lang.Exception
	{
		super (a2003tt);

		if (null == (_a2003p = a2003p))
			throw new java.lang.Exception ("Almgren2003Estimator Constructor => Invalid Inputs");
	}

	/**
	 * Generate the Horizon that results in the Optimal Information Ratio
	 * 
	 * @param dblD The Principal Discount "D"
	 * 
	 * @return The Horizon that results in the Optimal Information Ratio
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double optimalInformationRatioHorizon (
		final double dblD)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblD))
			throw new java.lang.Exception
				("Almgren2003Estimator::optimalInformationRatioHorizon => Invalid Inputs");

		org.drip.execution.impact.TransactionFunctionPower tfpTemporaryExpectation =
			_a2003p.powerTemporaryExpectation();

		double dblGamma = _a2003p.linearPermanentExpectation().slope();

		double dblEta = tfpTemporaryExpectation.constant();

		double dblK = tfpTemporaryExpectation.exponent();

		double dblX = efficientTrajectory().tradeSize();

		return dblX * java.lang.Math.pow (dblEta * (dblK + 1.) * (dblK + 1.) / (3. * dblK + 1.) / (dblD - 0.5
			* dblGamma * dblX), 1. / dblK);
	}

	/**
	 * Compute the Optimal Information Ratio
	 * 
	 * @param dblD The Principal Discount "D"
	 * 
	 * @return The Optimal Information Ratio
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double optimalInformationRatio (
		final double dblD)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblD))
			throw new java.lang.Exception
				("Almgren2003Estimator::optimalInformationRatio => Invalid Inputs");

		org.drip.execution.impact.TransactionFunctionPower tfpTemporaryExpectation =
			_a2003p.powerTemporaryExpectation();

		double dblGamma = _a2003p.linearPermanentExpectation().slope();

		double dblEta = tfpTemporaryExpectation.constant();

		double dblSigma = _a2003p.arithmeticPriceDynamicsSettings().volatility();

		double dblK = tfpTemporaryExpectation.exponent();

		double dblX = efficientTrajectory().tradeSize();

		return java.lang.Math.pow (3. * dblK + 1.              , (1. * dblK + 2.) / (2. * dblK)) /
			   java.lang.Math.pow (1. * dblK + 1.              , (3. * dblK + 4.) / (2. * dblK)) *
			   java.lang.Math.pow (dblD - 0.5 * dblGamma * dblX, (1. * dblK + 1.) / (1. * dblK)) /
			   java.lang.Math.pow (dblEta                      , (0. * dblK + 1.) / (1. * dblK)) /
			   (dblX * dblSigma);
	}

	/**
	 * Compute the Principal Discount Hurdle given the Information Ratio
	 * 
	 * @param dblI The Optimal Information Ratio "I"
	 * 
	 * @return The Principal Discount Hurdle
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double principalDiscountHurdle (
		final double dblI)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblI))
			throw new java.lang.Exception
				("Almgren2003Estimator::principalDiscountHurdle => Invalid Inputs");

		org.drip.execution.impact.TransactionFunctionPower tfpTemporaryExpectation =
			_a2003p.powerTemporaryExpectation();

		double dblGamma = _a2003p.linearPermanentExpectation().slope();

		double dblEta = tfpTemporaryExpectation.constant();

		double dblSigma = _a2003p.arithmeticPriceDynamicsSettings().volatility();

		double dblK = tfpTemporaryExpectation.exponent();

		double dblX = efficientTrajectory().tradeSize();

		return java.lang.Math.pow (
			   0.5 * dblGamma * dblX +
			   java.lang.Math.pow (1. * dblK + 1.              , (3. * dblK + 4.) / (2. * dblK)) /
			   java.lang.Math.pow (3. * dblK + 1.              , (1. * dblK + 2.) / (2. * dblK)) *
			   java.lang.Math.pow (dblEta                      , (0. * dblK + 1.) / (1. * dblK)) *
			   (dblX * dblSigma * dblI),
			   dblK / (dblK + 1.)
		);
	}

	/**
	 * Generate the Constant/Exponent Dependencies on the Market Parameters for the Optimal Execution Horizon
	 * 	/ Information Ratio
	 *  
	 * @return The Optimal Execution Horizon/Information Ratio Dependency
	 */

	public org.drip.execution.principal.HorizonInformationRatioDependence optimalMeasures()
	{
		org.drip.execution.impact.TransactionFunctionPower tfpTemporaryExpectation =
			_a2003p.powerTemporaryExpectation();

		double dblK = tfpTemporaryExpectation.exponent();

		try {
			return new org.drip.execution.principal.HorizonInformationRatioDependence (
				new org.drip.execution.principal.OptimalMeasureDependence (
					java.lang.Math.pow ((dblK + 1.) * (dblK + 1.) / (3. * dblK + 1.), 1. / dblK),
					1. / dblK,
					1.,
					0.,
					-1. / dblK
				),
				new org.drip.execution.principal.OptimalMeasureDependence (
					java.lang.Math.pow (3. * dblK + 1., (1. * dblK + 2.) / (2. * dblK)) /
						java.lang.Math.pow (1. * dblK + 1., (3. * dblK + 4.) / (2. * dblK)),
					-1. / dblK,
					-1.,
					-1.,
					(dblK + 1.) / dblK
				)
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
