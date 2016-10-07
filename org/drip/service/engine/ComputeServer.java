
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
 * ComputeServer contains the Functionality behind the DRIP API Compute Service Engine.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ComputeServer {

	/**
	 * The DRIP compute Service Engine Port
	 */

	public static final int DRIP_COMPUTE_ENGINE_PORT = 9090;

	private int _iListenerPort = -1;
	private java.net.ServerSocket _socketListener = null;

	/**
	 * Create a Standard Instance of the ComputeServer
	 * 
	 * @return The Standard ComputeServer Instance
	 */

	public static final ComputeServer Standard()
	{
		try {
			ComputeServer cs = new ComputeServer (DRIP_COMPUTE_ENGINE_PORT);

			return cs.initialize() ? cs : null;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ComputServer Constructor
	 * 
	 * @param iListenerPort The Listener Port
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ComputeServer (
		final int iListenerPort)
		throws java.lang.Exception
	{
		if (0 >= (_iListenerPort = iListenerPort))
			throw new java.lang.Exception ("ComputeServer Constructor => Invalid Inputs");
	}

	/**
	 * Initialize the Compute Server Engine Listener Setup
	 * 
	 * @return TRUE - The Compute Server Engine Listener Setup
	 */

	public boolean initialize()
	{
		try {
			_socketListener = new java.net.ServerSocket (_iListenerPort);

			return true;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Spin on the Listener Loop
	 * 
	 * @return FALSE - Spinning Terminated
	 */

	public boolean spin()
	{
        while (true) {
        	try {
            	java.net.Socket s = _socketListener.accept();

	        	if (null == s) return false;

	        	java.lang.String strJSONRequest = new java.io.BufferedReader (new java.io.InputStreamReader
	        		(s.getInputStream())).readLine();

	        	System.out.println (strJSONRequest);

	        	java.lang.Object objRequest = org.drip.json.simple.JSONValue.parse (strJSONRequest);

		    	if (null == objRequest || !(objRequest instanceof org.drip.json.simple.JSONObject))
		    		return false;

		    	org.drip.json.simple.JSONObject jsonRequest = (org.drip.json.simple.JSONObject) objRequest;

		    	java.lang.Object objResponse = org.drip.json.simple.JSONValue.parse
	    			(org.drip.service.json.KeyHoleSkeleton.Thunker (strJSONRequest));

		    	if (null == objResponse) return false;

		    	org.drip.json.simple.JSONObject jsonResponse = (org.drip.json.simple.JSONObject) objResponse;

		    	if (!org.drip.service.engine.RequestResponseDecorator.AffixResponseHeaders (jsonResponse,
		    		jsonRequest))
		    		return false;

	        	System.out.println ("\n\n" + jsonResponse.toJSONString());

            	java.io.PrintWriter pw = new java.io.PrintWriter (s.getOutputStream(), true);

            	pw.write (jsonResponse.toJSONString() + "\n");

            	pw.flush();
        	} catch (java.lang.Exception e) {
        		e.printStackTrace();

        		return false;
        	}
        }
	}

	public static final void main (
		final java.lang.String[] astrArgs)
		throws java.lang.Exception
	{
		org.drip.service.env.EnvManager.InitEnv ("");

		ComputeServer cs = ComputeServer.Standard();

		cs.spin();
	}
}
