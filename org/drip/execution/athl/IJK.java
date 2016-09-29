
package org.drip.execution.athl;

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
 * IJK holds the Empirical Signals that have been emitted off of a Transaction Run using the Scheme by
 *  Almgren, Thum, Hauptmann, and Li (2005), using the Parameterization of Almgren (2003). The References
 *  are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 *
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 97-102.
 * 
 * 	- Almgren, R., C. Thum, E. Hauptmann, and H. Li (2005): Equity Market Impact, Risk 18 (7) 57-62.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class IJK {
	private org.drip.execution.athl.TransactionSignal _tsI = null;
	private org.drip.execution.athl.TransactionSignal _tsK = null;

	/**
	 * IJK Constructor
	 * 
	 * @param tsI The "I" Transaction Signal
	 * @param tsK The "K" Transaction Signal
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public IJK (
		final org.drip.execution.athl.TransactionSignal tsI,
		final org.drip.execution.athl.TransactionSignal tsK)
		throws java.lang.Exception
	{
		if (null == (_tsI = tsI) || null == (_tsK = tsK))
			throw new java.lang.Exception ("IJK Constructor => Invalid Inputs");
	}

	/**
	 * The Almgren-Thum-Hauptmann-Li "I" Transaction Signal
	 * 
	 * @return The Almgren-Thum-Hauptmann-Li "I" Transaction Signal
	 */

	public org.drip.execution.athl.TransactionSignal i()
	{
		return _tsI;
	}

	/**
	 * The Almgren-Thum-Hauptmann-Li "K" Transaction Signal
	 * 
	 * @return The Almgren-Thum-Hauptmann-Li "K" Transaction Signal
	 */

	public org.drip.execution.athl.TransactionSignal k()
	{
		return _tsK;
	}

	/**
	 * The Almgren-Thum-Hauptmann-Li "J" Transaction Signal
	 * 
	 * @return The Almgren-Thum-Hauptmann-Li "J" Transaction Signal
	 */

	public org.drip.execution.athl.TransactionSignal j()
	{
		try {
			return new org.drip.execution.athl.TransactionSignal (_tsK.drift() + 0.5 * _tsI.drift(),
				_tsK.iWander() + 0.5 * _tsI.iWander(), _tsK.jWander());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
