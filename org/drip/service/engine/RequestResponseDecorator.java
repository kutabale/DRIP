
package org.drip.service.engine;

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
 * RequestResponseDecorator contains the Functionality behind the DRIP API Compute Service Engine Request and
 *  Response Header Fields Affixing/Decoration.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RequestResponseDecorator {

	/**
	 * Affix the Headers on the JSON Request
	 * 
	 * @param jsonRequest The JSON Request
	 * 
	 * @return TRUE - The Headers successfully affixed
	 */

	@SuppressWarnings ("unchecked") public static final boolean AffixRequestHeaders (
		final org.drip.json.simple.JSONObject jsonRequest)
	{
		if (null == jsonRequest) return false;

		jsonRequest.put ("APITYPE", "REQUEST");

		jsonRequest.put ("REQUESTTIMESTAMP", new java.util.Date().toString());

		jsonRequest.put ("REQUESTID", org.drip.quant.common.StringUtil.GUID());

		return true;
	}

	/**
	 * Affix the Headers on the JSON Response
	 * 
	 * @param jsonResponse The JSON Response
	 * @param jsonRequest The JSON Request
	 * 
	 * @return TRUE - The Headers successfully affixed
	 */

	@SuppressWarnings ("unchecked") public static final boolean AffixResponseHeaders (
		final org.drip.json.simple.JSONObject jsonResponse,
		final org.drip.json.simple.JSONObject jsonRequest)
	{
		if (null == jsonResponse || null == jsonRequest) return false;

    	jsonResponse.put ("APITYPE", "RESPONSE");

    	jsonResponse.put ("REQUESTTIMESTAMP", org.drip.json.parser.Converter.StringEntry
    		(jsonRequest, "REQUESTTIMESTAMP"));

    	jsonResponse.put ("REQUESTID", org.drip.json.parser.Converter.StringEntry (jsonRequest,
    		"REQUESTID"));

    	jsonResponse.put ("RESPONSETIMESTAMP", new java.util.Date().toString());

    	jsonResponse.put ("RESPONSEID", org.drip.quant.common.StringUtil.GUID());

		return true;
	}
}
