
package org.drip.sample.treasury;

import org.drip.market.issue.*;
import org.drip.service.env.EnvManager;

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
 * GovvieBondDefinitions contains the Details of the Standard Built-in Govvie Bonds.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class GovvieBondDefinitions {

	private static final void DisplayDetails (
		final String strTreasuryCode)
	{
		TreasurySetting ts = TreasurySettingContainer.TreasurySetting (strTreasuryCode);

		System.out.println (
			"\t| " + ts.code() +
			" | " + ts.currency() +
			" | " + ts.frequency() +
			" | " + ts.dayCount() +
			" | " + ts.calendar() + " ||"
		);
	}

	private static final void DefaultTreasuryCode (
		final String strCurrency)
	{
		System.out.println ("\t| " + strCurrency + " => " + TreasurySettingContainer.CurrencyBenchmarkCode (strCurrency) + " ||");
	}

	public static final void main (
		final String[] args)
		throws Exception
	{
		EnvManager.InitEnv ("");

		System.out.println ("\n\t|-------------------------------------||");

		System.out.println ("\t| BUILT-IN GOVVIE BOND STATIC DETAILS ||");

		System.out.println ("\t| -------- ------ ---- ------ ------- ||");

		System.out.println ("\t|                                     ||");

		System.out.println ("\t|      L -> R                         ||");

		System.out.println ("\t|            Treasury Code            ||");

		System.out.println ("\t|            Currency                 ||");

		System.out.println ("\t|            Frequency                ||");

		System.out.println ("\t|            Day Count                ||");

		System.out.println ("\t|            Calendar                 ||");

		System.out.println ("\t|                                     ||");

		System.out.println ("\t|-------------------------------------||");

		DisplayDetails ("AGB");

		DisplayDetails ("BTPS");

		DisplayDetails ("CAN");

		DisplayDetails ("DBR");

		DisplayDetails ("DGB");

		DisplayDetails ("FRTR");

		DisplayDetails ("GGB");

		DisplayDetails ("GILT");

		DisplayDetails ("GSWISS");

		DisplayDetails ("JGB");

		DisplayDetails ("MBONO");

		DisplayDetails ("NGB");

		DisplayDetails ("NZGB");

		DisplayDetails ("SGB");

		DisplayDetails ("SPGB");

		DisplayDetails ("UST");

		System.out.println ("\t|-------------------------------------||");

		System.out.println ("\n\n\t|------------||");

		System.out.println ("\t|            ||");

		System.out.println ("\t|   GOVVIE   ||");

		System.out.println ("\t|    BOND    ||");

		System.out.println ("\t|  CURRENCY  ||");

		System.out.println ("\t|  DEFAULTS  ||");

		System.out.println ("\t|            ||");

		System.out.println ("\t|------------||");

		DefaultTreasuryCode ("AUD");

		DefaultTreasuryCode ("CAD");

		DefaultTreasuryCode ("CHF");

		DefaultTreasuryCode ("EUR");

		DefaultTreasuryCode ("GBP");

		DefaultTreasuryCode ("JPY");

		DefaultTreasuryCode ("MXN");

		DefaultTreasuryCode ("NOK");

		DefaultTreasuryCode ("SEK");

		DefaultTreasuryCode ("USD");

		DefaultTreasuryCode ("AUD");

		System.out.println ("\t|------------||");
	}
}
