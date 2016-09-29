
package org.drip.sample.treasuryfutures;

import org.drip.market.exchange.*;
import org.drip.service.env.EnvManager;

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
 * ContractDefinitions contains all the pre-fixed Definitions of Exchange-traded Treasury Futures Contracts.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ContractDefinitions {

	private static final void DisplayContractDefinition (
		final String strFuturesCode)
		throws Exception
	{
		TreasuryFuturesContract tfc = TreasuryFuturesContractContainer.TreasuryFuturesContract (strFuturesCode);

		System.out.println (
			"\t| " + strFuturesCode
			+ " | " + tfc.id()
			+ " | " + tfc.code()
			+ " | " + tfc.tenor()
			+ " | " + tfc.type() + " ||"
		);
	}

	public static final void main (
		final String[] args)
		throws Exception
	{
		EnvManager.InitEnv ("");

		System.out.println ("\n\t|------------------------------||");

		System.out.println ("\t|   TREASURY FUTURES CONTRACT  ||");

		System.out.println ("\t|   -------- ------- --------  ||");

		System.out.println ("\t|                              ||");

		System.out.println ("\t|   L -> R:                    ||");

		System.out.println ("\t|                              ||");

		System.out.println ("\t|          Futures Code        ||");

		System.out.println ("\t|          Futures ID          ||");

		System.out.println ("\t|          Treasury Code       ||");

		System.out.println ("\t|          Futures Tenor       ||");

		System.out.println ("\t|          Treasury Type       ||");

		System.out.println ("\t|                              ||");

		System.out.println ("\t|------------------------------||");

		DisplayContractDefinition ("G1");

		DisplayContractDefinition ("CN1");

		DisplayContractDefinition ("DGB");

		DisplayContractDefinition ("DU1");

		DisplayContractDefinition ("FV1");

		DisplayContractDefinition ("IK1");

		DisplayContractDefinition ("JB1");

		DisplayContractDefinition ("OE1");

		DisplayContractDefinition ("RX1");

		DisplayContractDefinition ("TU1");

		DisplayContractDefinition ("TY1");

		DisplayContractDefinition ("UB1");

		DisplayContractDefinition ("US1");

		DisplayContractDefinition ("WB1");

		DisplayContractDefinition ("WN1");

		DisplayContractDefinition ("XM1");

		DisplayContractDefinition ("YM1");

		DisplayContractDefinition ("BOBL");

		DisplayContractDefinition ("BUND");

		DisplayContractDefinition ("BUXL");

		DisplayContractDefinition ("FBB1");

		DisplayContractDefinition ("OAT1");

		DisplayContractDefinition ("ULTRA");

		DisplayContractDefinition ("GSWISS");

		DisplayContractDefinition ("SCHATZ");

		System.out.println ("\t|------------------------------||");
	}
}
