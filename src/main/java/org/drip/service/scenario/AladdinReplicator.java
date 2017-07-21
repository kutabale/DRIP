
package org.drip.service.scenario;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
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
 * AladdinReplicatorSettings generates a Target Set of Sensitivity and Relative Value Runs.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class AladdinReplicator {
	private int _iSettleLag = -1;
	private double[] _adblGovvieQuote = null;
	private double[] _adblCreditQuote = null;
	private double[] _adblDepositQuote = null;
	private double[] _adblFuturesQuote = null;
	private double[] _adblFixFloatQuote = null;
	private double _dblFX = java.lang.Double.NaN;
	private java.lang.String _strGovvieCode = "";
	private java.lang.String[] _astrCreditTenor = null;
	private java.lang.String[] _astrGovvieTenor = null;
	private java.lang.String[] _astrDepositTenor = null;
	private java.lang.String[] _astrFixFloatTenor = null;
	private double _dblIssuePrice = java.lang.Double.NaN;
	private double _dblIssueAmount = java.lang.Double.NaN;
	private double _dblCurrentPrice = java.lang.Double.NaN;
	private org.drip.analytics.date.JulianDate _dtSpot = null;
	private org.drip.product.credit.BondComponent _bond = null;
	private double _dblCreditCurveTenorBump = java.lang.Double.NaN;
	private double _dblGovvieCurveTenorBump = java.lang.Double.NaN;
	private double _dblFundingCurveBaseBump = java.lang.Double.NaN;
	private double _dblFundingCurveTenorBump = java.lang.Double.NaN;

	private org.drip.analytics.date.JulianDate _dtSettle = null;
	private org.drip.param.valuation.ValuationParams _valParams = null;

	/**
	 * AladdinReplicator Constructor
	 * 
	 * @param dblCurrentPrice Current Price
	 * @param dblIssuePrice Issue Price
	 * @param dblIssueAmount Issue Amount
	 * @param dtSpot Spot Date
	 * @param astrDepositTenor Array of Deposit Tenors
	 * @param adblDepositQuote Array of Deposit Quotes
	 * @param adblFuturesQuote Array of Futures Quotes
	 * @param astrFixFloatTenor Array of Fix-Float Tenors
	 * @param adblFixFloatQuote Array of Fix-Float Quotes
	 * @param dblFundingCurveBaseBump Funding Curve Base Bump
	 * @param dblFundingCurveTenorBump Funding Curve Tenor Bump
	 * @param strGovvieCode Govvie Code
	 * @param astrGovvieTenor Array of Govvie Tenor
	 * @param adblGovvieQuote Array of Govvie Quotes
	 * @param dblGovvieCurveTenorBump Govvie Curve Tenor Bump
	 * @param astrCreditTenor Array of Credit Tenors
	 * @param adblCreditQuote Array of Credit Quotes
	 * @param dblCreditCurveTenorBump Credit Curve Tenor Bump
	 * @param dblFX FX Rate Applicable
	 * @param iSettleLag Settlement Lag
	 * @param bond Bond Component Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public AladdinReplicator (
		final double dblCurrentPrice,
		final double dblIssuePrice,
		final double dblIssueAmount,
		final org.drip.analytics.date.JulianDate dtSpot,
		final java.lang.String[] astrDepositTenor,
		final double[] adblDepositQuote,
		final double[] adblFuturesQuote,
		final java.lang.String[] astrFixFloatTenor,
		final double[] adblFixFloatQuote,
		final double dblFundingCurveBaseBump,
		final double dblFundingCurveTenorBump,
		final java.lang.String strGovvieCode,
		final java.lang.String[] astrGovvieTenor,
		final double[] adblGovvieQuote,
		final double dblGovvieCurveTenorBump,
		final java.lang.String[] astrCreditTenor,
		final double[] adblCreditQuote,
		final double dblCreditCurveTenorBump,
		final double dblFX,
		final int iSettleLag,
		final org.drip.product.credit.BondComponent bond)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblCurrentPrice = dblCurrentPrice) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblIssuePrice = dblIssuePrice) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblIssueAmount = dblIssueAmount) || null ==
					(_dtSpot = dtSpot) || !org.drip.quant.common.NumberUtil.IsValid (_dblFX = dblFX) || 0. >=
						_dblFX || 0 > (_iSettleLag = iSettleLag) || null == (_bond = bond))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		_strGovvieCode = strGovvieCode;
		_adblCreditQuote = adblCreditQuote;
		_astrCreditTenor = astrCreditTenor;
		_adblGovvieQuote = adblGovvieQuote;
		_astrGovvieTenor = astrGovvieTenor;
		_adblDepositQuote = adblDepositQuote;
		_astrDepositTenor = astrDepositTenor;
		_adblFuturesQuote = adblFuturesQuote;
		_adblFixFloatQuote = adblFixFloatQuote;
		_astrFixFloatTenor = astrFixFloatTenor;
		_dblCreditCurveTenorBump = dblCreditCurveTenorBump;
		_dblGovvieCurveTenorBump = dblGovvieCurveTenorBump;
		_dblFundingCurveBaseBump = dblFundingCurveBaseBump;
		_dblFundingCurveTenorBump = dblFundingCurveTenorBump;

		java.lang.String strCurrency = _bond.currency();

		if (null == (_dtSettle = _dtSpot.addBusDays (_iSettleLag, strCurrency)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		_valParams = new org.drip.param.valuation.ValuationParams (_dtSpot, _dtSettle, strCurrency);
	}

	/**
	 * Retrieve the Bond Current Market Price
	 * 
	 * @return The Bond Current Market Price
	 */

	public double currentPrice()
	{
		return _dblCurrentPrice;
	}

	/**
	 * Retrieve the Bond Issue Price
	 * 
	 * @return The Bond Issue Price
	 */

	public double issuePrice()
	{
		return _dblIssuePrice;
	}

	/**
	 * Retrieve the Bond Issue Amount
	 * 
	 * @return The Bond Issue Amount
	 */

	public double issueAmount()
	{
		return _dblIssueAmount;
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return The Spot Date
	 */

	public org.drip.analytics.date.JulianDate spotDate()
	{
		return _dtSpot;
	}

	/**
	 * Retrieve the Array of Deposit Instrument Maturity Tenors
	 * 
	 * @return The Array of Deposit Instrument Maturity Tenors
	 */

	public java.lang.String[] depositTenor()
	{
		return _astrDepositTenor;
	}

	/**
	 * Retrieve the Array of Deposit Instrument Quotes
	 * 
	 * @return The Array of Deposit Instrument Quotes
	 */

	public double[] depositQuote()
	{
		return _adblDepositQuote;
	}

	/**
	 * Retrieve the Array of Futures Instrument Quotes
	 * 
	 * @return The Array of Futures Instrument Quotes
	 */

	public double[] futuresQuote()
	{
		return _adblFuturesQuote;
	}

	/**
	 * Retrieve the Array of Fix-Float IRS Instrument Maturity Tenors
	 * 
	 * @return The Array of Fix-Float IRS Instrument Maturity Tenors
	 */

	public java.lang.String[] fixFloatTenor()
	{
		return _astrFixFloatTenor;
	}

	/**
	 * Retrieve the Array of Fix-Float IRS Instrument Quotes
	 * 
	 * @return The Array of Fix-Float IRS Instrument Quotes
	 */

	public double[] fixFloatQuote()
	{
		return _adblFixFloatQuote;
	}

	/**
	 * Retrieve the Funding Curve Base Bump
	 * 
	 * @return The Funding Curve Base Bump
	 */

	public double fundingCurveBaseBump()
	{
		return _dblFundingCurveBaseBump;
	}

	/**
	 * Retrieve the Funding Curve Tenor Bump
	 * 
	 * @return The Funding Curve Tenor Bump
	 */

	public double fundingCurveTenorBump()
	{
		return _dblFundingCurveTenorBump;
	}

	/**
	 * Retrieve the Govvie Code
	 * 
	 * @return The Govvie Code
	 */

	public java.lang.String govvieCode()
	{
		return _strGovvieCode;
	}

	/**
	 * Retrieve the Array of Govvie Instrument Maturity Tenors
	 * 
	 * @return The Array of Govvie Instrument Maturity Tenors
	 */

	public java.lang.String[] govvieTenor()
	{
		return _astrGovvieTenor;
	}

	/**
	 * Retrieve the Array of Govvie Yield Quotes
	 * 
	 * @return The Array of Govvie Yield Quotes
	 */

	public double[] govvieQuote()
	{
		return _adblGovvieQuote;
	}

	/**
	 * Retrieve the Govvie Curve Tenor Bump
	 * 
	 * @return The Govvie Curve Tenor Bump
	 */

	public double govvieCurveTenorBump()
	{
		return _dblGovvieCurveTenorBump;
	}

	/**
	 * Retrieve the Array of CDS Instrument Maturity Tenors
	 * 
	 * @return The Array of CDS Instrument Maturity Tenors
	 */

	public java.lang.String[] creditTenor()
	{
		return _astrCreditTenor;
	}

	/**
	 * Retrieve the Array of CDS Quotes
	 * 
	 * @return The Array of CDS Quotes
	 */

	public double[] creditQuote()
	{
		return _adblCreditQuote;
	}

	/**
	 * Retrieve the Credit Curve Tenor Bump
	 * 
	 * @return The Credit Curve Tenor Bump
	 */

	public double creditCurveTenorBump()
	{
		return _dblCreditCurveTenorBump;
	}

	/**
	 * Retrieve the FX Rate
	 * 
	 * @return The FX Rate
	 */

	public double fx()
	{
		return _dblFX;
	}

	/**
	 * Retrieve the Settle Lag
	 * 
	 * @return The Settle Lag
	 */

	public double settleLag()
	{
		return _iSettleLag;
	}

	/**
	 * Retrieve the Bond Component Instance
	 * 
	 * @return The Bond Component Instance
	 */

	public org.drip.product.credit.BondComponent bond()
	{
		return _bond;
	}

	/**
	 * Retrieve the Settle Date
	 * 
	 * @return The Settle Date
	 */

	public org.drip.analytics.date.JulianDate settleDate()
	{
		return _dtSettle;
	}

	/**
	 * Retrieve the Valuation Parameters
	 * 
	 * @return The Valuation Parameters
	 */

	public org.drip.param.valuation.ValuationParams valuationParameters()
	{
		return _valParams;
	}
}
