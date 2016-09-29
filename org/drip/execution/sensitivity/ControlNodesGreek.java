
package org.drip.execution.sensitivity;

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
 * ControlNodesGreek holds the Point Value, the Jacobian, and the Hessian for a Trajectory/Slice to the
 *  Holdings Control Nodes. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 *
 * 	- Chan, L. K. C., and J. Lakonishak (1995): The Behavior of Stock Prices around Institutional Trades,
 * 		Journal of Finance, 50, 1147-1174.
 *
 * 	- Keim, D. B., and A. Madhavan (1997): Transaction Costs and Investment Style: An Inter-exchange
 * 		Analysis of Institutional Equity Trades, Journal of Financial Economics, 46, 265-292.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ControlNodesGreek {
	private double[] _adblJacobian = null;
	private double[][] _aadblHessian = null;
	private double _dblValue = java.lang.Double.NaN;

	/**
	 * ControlNodesGreek Constructor
	 * 
	 * @param dblValue The Objective Function Penalty Value
	 * @param adblJacobian The Objective Function Penalty Jacobian
	 * @param aadblHessian The Objective Function Penalty Hessian
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ControlNodesGreek (
		final double dblValue,
		final double[] adblJacobian,
		final double[][] aadblHessian)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblValue = dblValue) || null == (_adblJacobian =
			adblJacobian) || null == (_aadblHessian = aadblHessian))
			throw new java.lang.Exception ("ControlNodesGreek Constructor => Invalid Inputs");

		int iNumInterval = _adblJacobian.length;

		if (0 == iNumInterval || iNumInterval != _aadblHessian.length)
			throw new java.lang.Exception ("ControlNodesGreek Constructor => Invalid Inputs");

		for (int i = 0; i < iNumInterval; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblJacobian[i]) || null == _aadblHessian[i] ||
				iNumInterval != _aadblHessian[i].length || !org.drip.quant.common.NumberUtil.IsValid
					(_aadblHessian[i]))
				throw new java.lang.Exception ("ControlNodesGreek Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Objective Function Penalty Value
	 * 
	 * @return The Objective Function Penalty Value
	 */

	public double value()
	{
		return _dblValue;
	}

	/**
	 * Retrieve the Objective Function Penalty Jacobian
	 * 
	 * @return The Objective Function Penalty Jacobian
	 */

	public double[] jacobian()
	{
		return _adblJacobian;
	}

	/**
	 * Retrieve the Objective Function Penalty Hessian
	 * 
	 * @return The Objective Function Penalty Hessian
	 */

	public double[][] hessian()
	{
		return _aadblHessian;
	}
}
