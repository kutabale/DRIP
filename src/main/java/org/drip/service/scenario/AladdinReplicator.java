
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
	private double _dblFundingCurveFlatBump = java.lang.Double.NaN;

	private org.drip.state.govvie.GovvieCurve _gcBase = null;
	private org.drip.analytics.date.JulianDate _dtSettle = null;
	private org.drip.param.valuation.ValuationParams _valParams = null;
	private org.drip.state.discount.MergedDiscountForwardCurve _mdfcBase = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcCreditBase = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcCredit01Up = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcFundingBase = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcFunding01Up = null;
	private java.util.Map<java.lang.String, org.drip.state.discount.MergedDiscountForwardCurve>
		_mapTenorFunding = null;

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
	 * @param dblFundingCurveFlatBump Base Funding Curve Flat Bump
	 * @param strGovvieCode Govvie Code
	 * @param astrGovvieTenor Array of Govvie Tenor
	 * @param adblGovvieQuote Array of Govvie Quotes
	 * @param astrCreditTenor Array of Credit Tenors
	 * @param adblCreditQuote Array of Credit Quotes
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
		final double dblFundingCurveFlatBump,
		final java.lang.String strGovvieCode,
		final java.lang.String[] astrGovvieTenor,
		final double[] adblGovvieQuote,
		final java.lang.String[] astrCreditTenor,
		final double[] adblCreditQuote,
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
		_dblFundingCurveFlatBump = dblFundingCurveFlatBump;

		java.lang.String strCurrency = _bond.currency();

		if (null == (_dtSettle = _dtSpot.addBusDays (_iSettleLag, strCurrency)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		_valParams = new org.drip.param.valuation.ValuationParams (_dtSpot, _dtSettle, strCurrency);

		if (null == (_mdfcBase = org.drip.service.template.LatentMarketStateBuilder.SmoothFundingCurve
			(_dtSpot, strCurrency, _astrDepositTenor, _adblDepositQuote, "ForwardRate", _adblFuturesQuote,
				"ForwardRate", _astrFixFloatTenor, _adblFixFloatQuote, "SwapRate")))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (null == (_mapTenorFunding = org.drip.service.template.LatentMarketStateBuilder.BumpedFundingCurve
			(_dtSpot, strCurrency, _astrDepositTenor, _adblDepositQuote, "ForwardRate", _adblFuturesQuote,
				"ForwardRate", _astrFixFloatTenor, _adblFixFloatQuote, "SwapRate",
					org.drip.service.template.LatentMarketStateBuilder.SMOOTH, 0.0001, false)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (null == (_gcBase = org.drip.service.template.LatentMarketStateBuilder.GovvieCurve
			(_strGovvieCode, _dtSpot, org.drip.analytics.support.Helper.SpotDateArray (_dtSpot, null ==
				_astrGovvieTenor ? 0 : _astrGovvieTenor.length), org.drip.analytics.support.Helper.FromTenor
					(_dtSpot, _astrGovvieTenor), _adblGovvieQuote, _adblGovvieQuote, "Yield",
						org.drip.service.template.LatentMarketStateBuilder.SHAPE_PRESERVING)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (null == (_csqcFundingBase = org.drip.param.creator.MarketParamsBuilder.Create (_mdfcBase,
			_gcBase, null, null, null, null, null)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		org.drip.state.identifier.CreditLabel cl = _bond.creditLabel();

		if (null != cl) {
			java.lang.String strReferenceEntity = cl.referenceEntity();

			_csqcCreditBase = org.drip.param.creator.MarketParamsBuilder.Create (_mdfcBase, _gcBase,
				org.drip.service.template.LatentMarketStateBuilder.CreditCurve (_dtSpot, strReferenceEntity,
					_astrCreditTenor, _adblCreditQuote, _adblCreditQuote, "FairPremium", _mdfcBase), null,
						null, null, null);

			_csqcCredit01Up = org.drip.param.creator.MarketParamsBuilder.Create (_mdfcBase, _gcBase,
				org.drip.service.template.LatentMarketStateBuilder.CreditCurve (_dtSpot, strReferenceEntity,
					_astrCreditTenor, _adblCreditQuote, org.drip.analytics.support.Helper.ParallelNodeBump
						(_adblCreditQuote, 1.), "FairPremium", _mdfcBase), null, null, null, null);
		}

		if (null == (_csqcFunding01Up = org.drip.param.creator.MarketParamsBuilder.Create
			(org.drip.service.template.LatentMarketStateBuilder.SmoothFundingCurve (_dtSpot, strCurrency,
				_astrDepositTenor, org.drip.analytics.support.Helper.ParallelNodeBump (_adblDepositQuote,
					0.0001), "ForwardRate", org.drip.analytics.support.Helper.ParallelNodeBump
						(_adblFuturesQuote, 0.0001), "ForwardRate", _astrFixFloatTenor,
							org.drip.analytics.support.Helper.ParallelNodeBump (_adblFixFloatQuote, 0.0001),
								"SwapRate"), _gcBase, null, null, null, null, null)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");
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
	 * Retrieve the Base Funding Curve Flat Bump
	 * 
	 * @return The Base Funding Curve Flat Bump
	 */

	public double fundingCurveFlatBump()
	{
		return _dblFundingCurveFlatBump;
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

	/**
	 * Retrieve the Base Instance of the Funding Curve
	 * 
	 * @return The Base Instance of the Funding Curve
	 */

	public org.drip.state.discount.MergedDiscountForwardCurve fundingBase()
	{
		return _mdfcBase;
	}

	/**
	 * Retrieve the Map of the Tenor Bumped Instances of the Funding Curve
	 * 
	 * @return The Map of the Tenor Bumped Instances of the Funding Curve
	 */

	public java.util.Map<java.lang.String, org.drip.state.discount.MergedDiscountForwardCurve>
		fundingTenorBumped()
	{
		return _mapTenorFunding;
	}

	/**
	 * Retrieve the Base Instance of the Govvie Curve
	 * 
	 * @return The Base Instance of the Govvie Curve
	 */

	public org.drip.state.govvie.GovvieCurve govvieBase()
	{
		return _gcBase;
	}

	/**
	 * Retrieve the CSQC built out of the Base Funding Curve
	 * 
	 * @return The CSQC built out of the Base Funding Curve
	 */

	public org.drip.param.market.CurveSurfaceQuoteContainer fundingBaseCSQC()
	{
		return _csqcFundingBase;
	}

	/**
	 * Retrieve the CSQC built out of the Base Credit Curve
	 * 
	 * @return The CSQC built out of the Base Credit Curve
	 */

	public org.drip.param.market.CurveSurfaceQuoteContainer creditBaseCSQC()
	{
		return _csqcCreditBase;
	}

	/**
	 * Retrieve the CSQC built out of the Funding Curve Flat Bumped 1 bp
	 * 
	 * @return The CSQC built out of the Funding Curve Flat Bumped 1 bp
	 */

	public org.drip.param.market.CurveSurfaceQuoteContainer funding01UpCSQC()
	{
		return _csqcFunding01Up;
	}

	/**
	 * Retrieve the CSQC built out of the Credit Curve Flat Bumped 1 bp
	 * 
	 * @return The CSQC built out of the Credit Curve Flat Bumped 1 bp
	 */

	public org.drip.param.market.CurveSurfaceQuoteContainer credit01UpCSQC()
	{
		return _csqcCredit01Up;
	}
}
