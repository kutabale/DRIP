
package org.drip.measure.marginal;

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
 * DiffusionEvolver implements the Functionality that guides the Single Factor R^1 Diffusion Random Process
 *  Variable Evolution.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DiffusionEvolver {
	private org.drip.measure.process.LocalDeterministicEvaluator _ldevDrift = null;
	private org.drip.measure.process.LocalDeterministicEvaluator _ldevVolatility = null;

	protected org.drip.measure.realization.JumpIndicationEdge eventIndicationDAG (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final org.drip.measure.realization.JumpDiffusionUnit jdu,
		final double dblTimeIncrement)
	{
		return null;
	}

	/**
	 * DiffusionEvolver Constructor
	 * 
	 * @param ldevDrift The LDEV Drift Function of the Marginal Process
	 * @param ldevVolatility The LDEV Volatility Function of the Marginal Process Continuous Component
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public DiffusionEvolver (
		final org.drip.measure.process.LocalDeterministicEvaluator ldevDrift,
		final org.drip.measure.process.LocalDeterministicEvaluator ldevVolatility)
		throws java.lang.Exception
	{
		if (null == (_ldevDrift = ldevDrift) || null == (_ldevVolatility = ldevVolatility))
			throw new java.lang.Exception ("DiffusionEvolver Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the LDEV Drift Function of the Marginal Process
	 * 
	 * @return The LDEV Drift Function of the Marginal Process
	 */

	public org.drip.measure.process.LocalDeterministicEvaluator driftLDEV()
	{
		return _ldevDrift;
	}

	/**
	 * Retrieve the LDEV Volatility Function of the Marginal Process Continuous Component
	 * 
	 * @return The LDEV Volatility Function of the Marginal Process Continuous Component
	 */

	public org.drip.measure.process.LocalDeterministicEvaluator volatilityLDEV()
	{
		return _ldevVolatility;
	}

	/**
	 * Generate the JumpDiffusionDAG Increment Instance from the specified Jump Diffusion Instance
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param jdu The JumpDiffusionUnit Instance
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The JumpDiffusionDAG Increment Instance
	 */

	public org.drip.measure.realization.JumpDiffusionEdge increment (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final org.drip.measure.realization.JumpDiffusionUnit jdu,
		final double dblTimeIncrement)
	{
		if (null == jdv || null == jdu || !org.drip.quant.common.NumberUtil.IsValid (dblTimeIncrement))
			return null;

		try {
			if (jdv.terminationReached())
				return new org.drip.measure.realization.JumpDiffusionEdge (jdv.value(), 0., 0., new
					org.drip.measure.realization.JumpIndicationEdge (true, 0., 0., 0.), new
						org.drip.measure.realization.JumpDiffusionUnit (0., 0.));

			double dblDiffusionUnitRealization = jdu.diffusion();

			return new org.drip.measure.realization.JumpDiffusionEdge (jdv.value(), _ldevDrift.value (jdv) *
				dblTimeIncrement, null == _ldevVolatility ? 0. : _ldevVolatility.value (jdv) *
					dblDiffusionUnitRealization * java.lang.Math.sqrt (java.lang.Math.abs
						(dblTimeIncrement)), eventIndicationDAG (jdv, jdu, dblTimeIncrement), new
							org.drip.measure.realization.JumpDiffusionUnit (dblDiffusionUnitRealization,
								jdu.jump()));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Array of Adjacent JumpDiffusionDAG Increments from the specified Random Variate Array
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param jdu The JumpDiffusionUnit Instance
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Array of Adjacent JumpDiffusionDAG Increments
	 */

	public org.drip.measure.realization.JumpDiffusionEdge[] incrementSequence (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final org.drip.measure.realization.JumpDiffusionUnit[] aJDU,
		final double dblTimeIncrement)
	{
		if (null == aJDU) return null;

		int iNumTimeStep = aJDU.length;
		org.drip.measure.realization.JumpDiffusionVertex jdvLoop = jdv;
		org.drip.measure.realization.JumpDiffusionEdge[] aJDDAG = 0 == iNumTimeStep ? null : new
			org.drip.measure.realization.JumpDiffusionEdge[iNumTimeStep];

		if (0 == iNumTimeStep) return null;

		for (int i = 0; i < iNumTimeStep; ++i) {
			if (null == (aJDDAG[i] = increment (jdvLoop, aJDU[i], dblTimeIncrement))) return null;

			try {
				org.drip.measure.realization.JumpIndicationEdge eiDAG = aJDDAG[i].jumpIndicationEdge();

				boolean bJumpOccurred = false;
				double dblHazardIntegral = 0.;

				if (null != eiDAG) {
					bJumpOccurred = eiDAG.eventOccurred();

					dblHazardIntegral = eiDAG.hazardIntegral();
				}

				jdvLoop = new org.drip.measure.realization.JumpDiffusionVertex (jdvLoop.time() +
					dblTimeIncrement, aJDDAG[i].finish(), jdvLoop.cumulativeHazardIntegral() +
						dblHazardIntegral, bJumpOccurred);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aJDDAG;
	}

	/**
	 * Generate the Array of JumpDiffusionVertex Snaps from the specified Random Variate Array
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param aJDU The Array of JumpDiffusionUnit Instances
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Array of JumpDiffusionVertex Snaps
	 */

	public org.drip.measure.realization.JumpDiffusionVertex[] vertexSequence (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final org.drip.measure.realization.JumpDiffusionUnit[] aJDU,
		final double dblTimeIncrement)
	{
		if (null == aJDU) return null;

		int iNumTimeStep = aJDU.length;
		org.drip.measure.realization.JumpDiffusionVertex jdvPrev = jdv;
		org.drip.measure.realization.JumpDiffusionVertex[] aJDV = 0 == iNumTimeStep ? null : new
			org.drip.measure.realization.JumpDiffusionVertex[iNumTimeStep];

		if (0 == iNumTimeStep) return null;

		for (int i = 0; i < iNumTimeStep; ++i) {
			org.drip.measure.realization.JumpDiffusionEdge jdDAG = increment (jdvPrev, aJDU[i],
				dblTimeIncrement);

			if (null == jdDAG) return null;

			try {
				org.drip.measure.realization.JumpIndicationEdge eiDAG = jdDAG.jumpIndicationEdge();

				boolean bJumpOccurred = false;
				double dblHazardIntegral = 0.;

				if (null != eiDAG) {
					bJumpOccurred = eiDAG.eventOccurred();

					dblHazardIntegral = eiDAG.hazardIntegral();
				}

				jdvPrev = aJDV[i] = new org.drip.measure.realization.JumpDiffusionVertex (jdvPrev.time() +
					dblTimeIncrement, jdDAG.finish(), jdvPrev.cumulativeHazardIntegral() + dblHazardIntegral,
						bJumpOccurred);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return aJDV;
	}

	/**
	 * Generate the Adjacent JumpDiffusionDAG Increment from the specified Random Variate and a Weiner Driver
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent JumpDiffusionDAG Increment
	 */

	public org.drip.measure.realization.JumpDiffusionEdge weinerIncrement (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final double dblTimeIncrement)
	{
		try {
			return increment (jdv, org.drip.measure.realization.JumpDiffusionUnit.GaussianDiffusion(),
				dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent JumpDiffusionDAG Increment from the specified Random Variate and a Jump Driver
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent JumpDiffusionDAG Increment
	 */

	public org.drip.measure.realization.JumpDiffusionEdge jumpIncrement (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final double dblTimeIncrement)
	{
		return increment (jdv, org.drip.measure.realization.JumpDiffusionUnit.UniformJump(),
			dblTimeIncrement);
	}

	/**
	 * Generate the Adjacent JumpDiffusionDAG Increment from the specified Random Variate and Jump/Weiner
	 * 		Drivers
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent JumpDiffusionDAG Increment
	 */

	public org.drip.measure.realization.JumpDiffusionEdge jumpWeinerIncrement (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final double dblTimeIncrement)
	{
		try {
			return increment (jdv, new org.drip.measure.realization.JumpDiffusionUnit
				(org.drip.measure.gaussian.NormalQuadrature.Random(), java.lang.Math.random()),
					dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Adjacent JumpDiffusionDAG Increment from the specified Random Variate and Weiner/Jump
	 * 		Drivers
	 * 
	 * @param jdv The JumpDiffusionVertex Instance
	 * @param dblTimeIncrement The Time Increment Evolution Unit
	 * 
	 * @return The Adjacent JumpDiffusionDAG Increment
	 */

	public org.drip.measure.realization.JumpDiffusionEdge weinerJumpIncrement (
		final org.drip.measure.realization.JumpDiffusionVertex jdv,
		final double dblTimeIncrement)
	{
		try {
			return increment (jdv, new org.drip.measure.realization.JumpDiffusionUnit
				(org.drip.measure.gaussian.NormalQuadrature.Random(), java.lang.Math.random()),
					dblTimeIncrement);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
