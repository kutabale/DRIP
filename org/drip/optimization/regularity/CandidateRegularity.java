
package org.drip.optimization.regularity;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * CandidateRegularity holds the Results of the Verification of the Regularity Conditions/Constraint
 * 	Qualifications at the specified possible Optimal Variate. The References are:
 * 
 * 	- Boyd, S., and L. van den Berghe (2009): Convex Optimization, Cambridge University Press, Cambridge UK.
 * 
 * 	- Eustaquio, R., E. Karas, and A. Ribeiro (2008): Constraint Qualification for Nonlinear Programming,
 * 		Technical Report, Federal University of Parana.
 * 
 * 	- Karush, A. (1939): Minima of Functions of Several Variables with Inequalities as Side Constraints,
 * 		M. Sc., University of Chicago, Chicago IL.
 * 
 * 	- Kuhn, H. W., and A. W. Tucker (1951): Nonlinear Programming, Proceedings of the Second Berkeley
 * 		Symposium, University of California, Berkeley CA 481-492.
 * 
 * 	- Ruszczynski, A. (2006): Nonlinear Optimization, Princeton University Press, Princeton NJ.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CandidateRegularity {
	private double[] _adblOptimalVariate = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqLCQ = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqCRCQ = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqLICQ = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqMFCQ = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqQNCQ = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqSCCQ = null;
	private org.drip.optimization.regularity.ConstraintQualifier _cqCPLDCQ = null;

	/**
	 * Construct a Standard Instance of CandidateRegularity
	 * 
	 * @param adblOptimalVariate The Optimal Variate Array
	 * @param bValidLCQ The LCQ Validity Flag
	 * @param bValidLICQ The LICQ Validity Flag
	 * @param bValidMFCQ The MFCQ Validity Flag
	 * @param bValidCRCQ The CRCQ Validity Flag
	 * @param bValidCPLDCQ The CPLDCQ Validity Flag
	 * @param bValidQNCQ The QNCQ Validity Flag
	 * @param bValidSCCQ The SCCQ Validity Flag
	 * 
	 * @return The Standard Instance of CandidateRegularity
	 */

	public static final CandidateRegularity Standard (
		final double[] adblOptimalVariate,
		final boolean bValidLCQ,
		final boolean bValidLICQ,
		final boolean bValidMFCQ,
		final boolean bValidCRCQ,
		final boolean bValidCPLDCQ,
		final boolean bValidQNCQ,
		final boolean bValidSCCQ)
	{
		try {
			return new CandidateRegularity (adblOptimalVariate, new
				org.drip.optimization.regularity.ConstraintQualifierLCQ (bValidLCQ), new
					org.drip.optimization.regularity.ConstraintQualifierLICQ (bValidLICQ), new
						org.drip.optimization.regularity.ConstraintQualifierMFCQ (bValidMFCQ), new
							org.drip.optimization.regularity.ConstraintQualifierCRCQ (bValidCRCQ), new
								org.drip.optimization.regularity.ConstraintQualifierCPLDCQ (bValidCPLDCQ),
									new org.drip.optimization.regularity.ConstraintQualifierQNCQ
										(bValidQNCQ), new
											org.drip.optimization.regularity.ConstraintQualifierSCCQ
												(bValidSCCQ));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * CandidateRegularity Constructor
	 * 
	 * @param adblOptimalVariate The Optimal Variate Array
	 * @param cqLCQ LCQ Constraint Qualifier Instance
	 * @param cqLICQ LICQ Constraint Qualifier Instance
	 * @param cqMFCQ MFCQ Constraint Qualifier Instance
	 * @param cqCRCQ CRCQ Constraint Qualifier Instance
	 * @param cqCPLDCQ CPLDCQ Constraint Qualifier Instance
	 * @param cqQNCQ QNCQ Constraint Qualifier Instance
	 * @param cqSCCQ SCCQ Constraint Qualifier Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public CandidateRegularity (
		final double[] adblOptimalVariate,
		final org.drip.optimization.regularity.ConstraintQualifier cqLCQ,
		final org.drip.optimization.regularity.ConstraintQualifier cqLICQ,
		final org.drip.optimization.regularity.ConstraintQualifier cqMFCQ,
		final org.drip.optimization.regularity.ConstraintQualifier cqCRCQ,
		final org.drip.optimization.regularity.ConstraintQualifier cqCPLDCQ,
		final org.drip.optimization.regularity.ConstraintQualifier cqQNCQ,
		final org.drip.optimization.regularity.ConstraintQualifier cqSCCQ)
		throws java.lang.Exception
	{
		if (null == (_adblOptimalVariate = adblOptimalVariate) || 0 == _adblOptimalVariate.length || null ==
			(_cqLCQ = cqLCQ) || null == (_cqLICQ = cqLICQ) || null == (_cqMFCQ = cqMFCQ) || null == (_cqCRCQ
				= cqCRCQ) || null == (_cqCPLDCQ = cqCPLDCQ) || null == (_cqQNCQ = cqQNCQ) || null == (_cqSCCQ
					= cqSCCQ))
			throw new java.lang.Exception ("CandidateRegularity Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the Optimal Variate Array
	 * 
	 * @return The Optimal Variate Array
	 */

	public double[] optimalVariate()
	{
		return _adblOptimalVariate;
	}

	/**
	 * Retrieve the LCQ Constraint Qualifier
	 * 
	 * @return The LCQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier lcq()
	{
		return _cqLCQ;
	}

	/**
	 * Retrieve the LICQ Constraint Qualifier
	 * 
	 * @return The LICQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier licq()
	{
		return _cqLICQ;
	}

	/**
	 * Retrieve the MFCQ Constraint Qualifier
	 * 
	 * @return The MFCQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier mfcq()
	{
		return _cqMFCQ;
	}

	/**
	 * Retrieve the CRCQ Constraint Qualifier
	 * 
	 * @return The CRCQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier crcq()
	{
		return _cqCRCQ;
	}

	/**
	 * Retrieve the CPLDCQ Constraint Qualifier
	 * 
	 * @return The CPLDCQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier cpldcq()
	{
		return _cqCPLDCQ;
	}

	/**
	 * Retrieve the QNCQ Constraint Qualifier
	 * 
	 * @return The QNCQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier qncq()
	{
		return _cqQNCQ;
	}

	/**
	 * Retrieve the SCCQ Constraint Qualifier
	 * 
	 * @return The SCCQ Constraint Qualifier
	 */

	public org.drip.optimization.regularity.ConstraintQualifier sccq()
	{
		return _cqSCCQ;
	}

	/**
	 * Indicate the Gross Regularity Validity across all the Constraint Qualifiers
	 * 
	 * @return TRUE - The Regularity Criteria is satisfied across all the Constraint Qualifiers
	 */

	public boolean valid()
	{
		return _cqCPLDCQ.valid() && _cqCRCQ.valid() && _cqLCQ.valid() && _cqLICQ.valid() && _cqMFCQ.valid()
			&& _cqQNCQ.valid() && _cqSCCQ.valid();
	}
}
