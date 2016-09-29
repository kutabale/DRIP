
package org.drip.state.discount;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * TurnListDiscountFactor implements the discounting based off of the turns list. Its functions add a turn
 * 	instance to the current set, and concurrently apply the discount factor inside the range to each relevant
 * 	turn.
 *
 * @author Lakshmi Krishnamurthy
 */

public class TurnListDiscountFactor {
	private java.util.List<org.drip.analytics.definition.Turn> _lsTurn = null;

	/**
	 * Empty TurnListDiscountFactor constructor
	 */

	public TurnListDiscountFactor()
	{
	}

	/**
	 * Add a Turn Instance to the Discount Curve
	 * 
	 * @param turn The Turn Instance to be added
	 * 
	 * @return TRUE => Successfully added
	 */

	public boolean addTurn (
		final org.drip.analytics.definition.Turn turn)
	{
		if (null == turn) return false;

		if (null == _lsTurn) _lsTurn = new java.util.ArrayList<org.drip.analytics.definition.Turn>();

		_lsTurn.add (turn);

		return true;
	}

	/**
	 * Apply the Turns' DF Adjustment
	 * 
	 * @param iStartDate Turn Start Date
	 * @param iFinishDate Turn Finish Date
	 * 
	 * @return Turns' DF Adjustment
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public double turnAdjust (
		final int iStartDate,
		final int iFinishDate)
		throws java.lang.Exception
	{
		if (null == _lsTurn || 0 == _lsTurn.size()) return 1.;

		if (iStartDate >= iFinishDate) return 1.;

		double dblTurnAdjust = 1.;

		for (org.drip.analytics.definition.Turn turn : _lsTurn) {
			if (null == turn || iStartDate >= turn.finishDate() || iFinishDate <= turn.startDate()) continue;

			int iEffectiveStart = turn.startDate() > iStartDate ? turn.startDate() : iStartDate;

			int iEffectiveFinish = turn.finishDate() < iFinishDate ? turn.finishDate() : iFinishDate;

			dblTurnAdjust *= java.lang.Math.exp (turn.spread() * (iEffectiveStart - iEffectiveFinish) /
				365.25);
		}

		return dblTurnAdjust;
	}
}
