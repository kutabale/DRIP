
package org.drip.portfolioconstruction.allocator;

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
 * BoundedPortfolioConstructionParameters holds the Parameters needed to build the Portfolio with Bounds on
 * 	the Underlying Assets.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BoundedPortfolioConstructionParameters extends
	org.drip.portfolioconstruction.allocator.PortfolioConstructionParameters {
	private org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.portfolioconstruction.asset.AssetBounds>
		_mapBounds = new
			org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.portfolioconstruction.asset.AssetBounds>();

	/**
	 * BoundedPortfolioConstructionParameters Constructor
	 * 
	 * @param astrAssetID Array of Assets ID
	 * @param qcru The Quadratic Custom Risk Utility Settings
	 * @param pecs The Portfolio Equality Constraint Settings
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BoundedPortfolioConstructionParameters (
		final java.lang.String[] astrAssetID,
		final org.drip.portfolioconstruction.allocator.CustomRiskUtilitySettings qcru,
		final org.drip.portfolioconstruction.allocator.PortfolioEqualityConstraintSettings pecs)
		throws java.lang.Exception
	{
		super (astrAssetID, qcru, pecs);
	}

	/**
	 * Set the Bounds for the specified Asset
	 * 
	 * @param strAssetID The Asset ID
	 * @param dblLowerBound The Asset Share Lower Bound
	 * @param dblUpperBound The Asset Share Upper Bound
	 * 
	 * @return TRUE - The Asset Bounds successfully set
	 */

	public boolean addBound (
		final java.lang.String strAssetID,
		final double dblLowerBound,
		final double dblUpperBound)
	{
		if (null == strAssetID || strAssetID.isEmpty()) return false;

		try {
			_mapBounds.put (strAssetID, new org.drip.portfolioconstruction.asset.AssetBounds (dblLowerBound,
				dblUpperBound));

			return true;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Retrieve the Lower Bound for the Specified Asset ID
	 * 
	 * @param strAssetID The Asset ID
	 * 
	 * @return The Lower Bound for the Specified Asset ID
	 * 
	 * @throws java.lang.Exception Thrown if the Bound cannot be extracted
	 */

	public double lowerBound (
		final java.lang.String strAssetID)
		throws java.lang.Exception
	{
		if (!_mapBounds.containsKey (strAssetID))
			throw new java.lang.Exception
				("BoundedPortfolioConstructionParameters::lowerBound => Invalid Inputs");

		return _mapBounds.get (strAssetID).lower();
	}

	/**
	 * Retrieve the Upper Bound for the Specified Asset ID
	 * 
	 * @param strAssetID The Asset ID
	 * 
	 * @return The Upper Bound for the Specified Asset ID
	 * 
	 * @throws java.lang.Exception Thrown if the Bound cannot be extracted
	 */

	public double upperBound (
		final java.lang.String strAssetID)
		throws java.lang.Exception
	{
		if (!_mapBounds.containsKey (strAssetID))
			throw new java.lang.Exception
				("BoundedPortfolioConstructionParameters::upperBound => Invalid Inputs");

		return _mapBounds.get (strAssetID).upper();
	}

	/**
	 * Retrieve the Array of the Inequality Constraint Functions
	 * 
	 * @param iNumExtraneousVariate Number of Extraneous Variatea
	 * 
	 * @return The Array of the Inequality Constraint Functions
	 */

	public org.drip.function.rdtor1.LinearBoundMultivariate[] boundingConstraints (
		final int iNumExtraneousVariate)
	{
		java.lang.String[] astrAssetID = assets();

		int iNumAsset = astrAssetID.length;

		java.util.List<org.drip.function.rdtor1.LinearBoundMultivariate> lsRdToR1 = new
			java.util.ArrayList<org.drip.function.rdtor1.LinearBoundMultivariate>();

		for (int i = 0; i < iNumAsset; ++i) {
			if (!_mapBounds.containsKey (astrAssetID[i])) continue;

			org.drip.portfolioconstruction.asset.AssetBounds ab = _mapBounds.get (astrAssetID[i]);

			double dblLowerBound = ab.lower();

			double dblUpperBound = ab.upper();

			try {
				if (org.drip.quant.common.NumberUtil.IsValid (dblLowerBound))
					lsRdToR1.add (new org.drip.function.rdtor1.LinearBoundMultivariate (false, i, iNumAsset +
						iNumExtraneousVariate, dblLowerBound));

				if (org.drip.quant.common.NumberUtil.IsValid (dblUpperBound))
					lsRdToR1.add (new org.drip.function.rdtor1.LinearBoundMultivariate (true, i, iNumAsset +
						iNumExtraneousVariate, dblUpperBound));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		int iNumConstraint = lsRdToR1.size();

		if (0 == iNumConstraint) return null;

		org.drip.function.rdtor1.LinearBoundMultivariate[] aRdToR1Constraint = new
			org.drip.function.rdtor1.LinearBoundMultivariate[iNumConstraint];

		for (int i = 0; i < iNumConstraint; ++i)
			aRdToR1Constraint[i] = lsRdToR1.get (i);

		return aRdToR1Constraint;
	}

	/**
	 * Retrieve an Array of Viable Starting Variates From Within the Feasible Region
	 * 
	 * @return An Array of Viable Starting Variates From Within the Feasible Region
	 */

	public double[] feasibleStart()
	{
		boolean bReturnsConstraintPresent = org.drip.quant.common.NumberUtil.IsValid
			(constraintSettings().returnsConstraint());

		java.lang.String[] astrAssetID = assets();

		int iNumAsset = astrAssetID.length;
		double[] adblStartingVariate = new double[iNumAsset + (bReturnsConstraintPresent ? 2 : 1)];

		for (int i = 0; i < iNumAsset; ++i)
			adblStartingVariate[i] = _mapBounds.get (astrAssetID[i]).feasibleStart();

		if (bReturnsConstraintPresent) adblStartingVariate[iNumAsset + 1] = 0.;

		adblStartingVariate[iNumAsset] = 0.;
		return adblStartingVariate;
	}

	/**
	 * Retrieve an Array of Viable Weight Constrained Starting Variates From Within the Feasible Region
	 * 
	 * @return An Array of Viable Weight Constrained Starting Variates From Within the Feasible Region
	 */

	public double[] weightConstrainedFeasibleStart()
	{
		boolean bReturnsConstraintPresent = org.drip.quant.common.NumberUtil.IsValid
			(constraintSettings().returnsConstraint());

		java.lang.String[] astrAssetID = assets();

		double dblCumulativeWeight = 0.;
		int iNumAsset = astrAssetID.length;
		double[] adblStartingVariate = new double[iNumAsset + (bReturnsConstraintPresent ? 2 : 1)];

		for (int i = 0; i < iNumAsset; ++i) {
			adblStartingVariate[i] = _mapBounds.get (astrAssetID[i]).lower();

			dblCumulativeWeight += adblStartingVariate[i];
		}

		if (1. < dblCumulativeWeight) return null;

		double dblWeightGap = (1. - dblCumulativeWeight) / iNumAsset;

		for (int i = 0; i < iNumAsset; ++i)
			adblStartingVariate[i] += dblWeightGap;

		if (bReturnsConstraintPresent) adblStartingVariate[iNumAsset + 1] = 0.;

		adblStartingVariate[iNumAsset] = 0.;
		return adblStartingVariate;
	}
}
