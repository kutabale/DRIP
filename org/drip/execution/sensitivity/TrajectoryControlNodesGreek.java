
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
 * TrajectoryControlNodesGreek holds the Point Value, the Jacobian, and the Hessian for a Trajectory to the
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

public class TrajectoryControlNodesGreek extends org.drip.execution.sensitivity.ControlNodesGreek {
	private java.util.List<org.drip.execution.sensitivity.ControlNodesGreek> _lsCNGSlice = null;

	/**
	 * TrajectoryControlNodesGreek Constructor
	 * 
	 * @param dblValue The Objective Function Penalty Value
	 * @param adblJacobian The Objective Function Penalty Jacobian
	 * @param aadblHessian The Objective Function Penalty Hessian
	 * @param lsCNGSlice The List of the Slice Control Nodes Greek
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public TrajectoryControlNodesGreek (
		final double dblValue,
		final double[] adblJacobian,
		final double[][] aadblHessian,
		final java.util.List<org.drip.execution.sensitivity.ControlNodesGreek> lsCNGSlice)
		throws java.lang.Exception
	{
		super (dblValue, adblJacobian, aadblHessian);

		if (null == (_lsCNGSlice = lsCNGSlice) || 0 == _lsCNGSlice.size())
			throw new java.lang.Exception ("TrajectoryControlNodesGreek Constructor => Invalid Inputs");
	}

	/**
	 * Retrieve the List of the Slice Control Nodes Greek
	 * 
	 * @return The List of the Slice Control Nodes Greek
	 */

	public java.util.List<org.drip.execution.sensitivity.ControlNodesGreek> sliceGreeks()
	{
		return _lsCNGSlice;
	}

	/**
	 * Retrieve the Inner Jacobian Array
	 * 
	 * @return The Inner Jacobian Array
	 */

	public double[] innerJacobian()
	{
		double[] adblJacobian = jacobian();

		int iNumInnerNode = adblJacobian.length - 2;
		double[] adblInnerJacobian = 0 >= iNumInnerNode ? null : new double[iNumInnerNode];

		if (null == adblInnerJacobian) return null;

		for (int i = 0; i < iNumInnerNode; ++i)
			adblInnerJacobian[i] = adblJacobian[i + 1];

		return adblInnerJacobian;
	}

	/**
	 * Retrieve the Inner Hessian Matrix
	 * 
	 * @return The Inner Hessian Matrix
	 */

	public double[][] innerHessian()
	{
		double[][] aadblHessian = hessian();

		int iNumInnerNode = aadblHessian.length - 2;
		double[][] aadblInnerHessian = 0 >= iNumInnerNode ? null : new double[iNumInnerNode][iNumInnerNode];

		if (null == aadblInnerHessian) return null;

		for (int i = 0; i < iNumInnerNode; ++i) {
			for (int j = 0; j < iNumInnerNode; ++j)
				aadblInnerHessian[i][j] = aadblHessian[i + 1][j + 1];
		}

		return aadblInnerHessian;
	}
}
