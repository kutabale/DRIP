
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
	private double _dblZSpreadBump = java.lang.Double.NaN;
	private double _dblCurrentPrice = java.lang.Double.NaN;
	private double _dblCustomYieldBump = java.lang.Double.NaN;
	private org.drip.analytics.date.JulianDate _dtSpot = null;
	private org.drip.product.credit.BondComponent _bond = null;
	private double _dblCustomCreditBasisBump = java.lang.Double.NaN;
	private double _dblSpreadDurationMultiplier = java.lang.Double.NaN;

	private double _dblResetRate = java.lang.Double.NaN;
	private int _iResetDate = java.lang.Integer.MIN_VALUE;
	private org.drip.analytics.date.JulianDate _dtSettle = null;
	private org.drip.param.valuation.ValuationParams _valParams = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcCreditBase = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcCredit01Up = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcFundingBase = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcFunding01Up = null;
	private org.drip.param.market.CurveSurfaceQuoteContainer _csqcFundingEuroDollar = null;

	private java.util.Map<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
		_mapCSQCCredit = new
			org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.param.market.CurveSurfaceQuoteContainer>();

	private java.util.Map<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
		_mapCSQCGovvie = new
			org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.param.market.CurveSurfaceQuoteContainer>();

	private java.util.Map<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
		_mapCSQCFunding = new
			org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.param.market.CurveSurfaceQuoteContainer>();

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
	 * @param dblCustomYieldBump Custom Yield Bump
	 * @param dblCustomCreditBasisBump Custom Credit Basis Bump
	 * @param dblZSpreadBump Z Spread Bump
	 * @param dblSpreadDurationMultiplier Spread Duration Multiplier
	 * @param strGovvieCode Govvie Code
	 * @param astrGovvieTenor Array of Govvie Tenor
	 * @param adblGovvieQuote Array of Govvie Quotes
	 * @param astrCreditTenor Array of Credit Tenors
	 * @param adblCreditQuote Array of Credit Quotes
	 * @param dblFX FX Rate Applicable
	 * @param dblResetRate Reset Rate Applicable
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
		final double dblCustomYieldBump,
		final double dblCustomCreditBasisBump,
		final double dblZSpreadBump,
		final double dblSpreadDurationMultiplier,
		final java.lang.String strGovvieCode,
		final java.lang.String[] astrGovvieTenor,
		final double[] adblGovvieQuote,
		final java.lang.String[] astrCreditTenor,
		final double[] adblCreditQuote,
		final double dblFX,
		final double dblResetRate,
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

		_dblResetRate = dblResetRate;
		_strGovvieCode = strGovvieCode;
		_dblZSpreadBump = dblZSpreadBump;
		_adblCreditQuote = adblCreditQuote;
		_astrCreditTenor = astrCreditTenor;
		_adblGovvieQuote = adblGovvieQuote;
		_astrGovvieTenor = astrGovvieTenor;
		_adblDepositQuote = adblDepositQuote;
		_astrDepositTenor = astrDepositTenor;
		_adblFuturesQuote = adblFuturesQuote;
		_adblFixFloatQuote = adblFixFloatQuote;
		_astrFixFloatTenor = astrFixFloatTenor;
		_dblCustomYieldBump = dblCustomYieldBump;
		_dblCustomCreditBasisBump = dblCustomCreditBasisBump;
		_dblSpreadDurationMultiplier = dblSpreadDurationMultiplier;

		java.lang.String strCurrency = _bond.currency();

		if (null == (_dtSettle = _dtSpot.addBusDays (_iSettleLag, strCurrency)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		_valParams = new org.drip.param.valuation.ValuationParams (_dtSpot, _dtSettle, strCurrency);

		org.drip.state.discount.MergedDiscountForwardCurve mdfc =
			org.drip.service.template.LatentMarketStateBuilder.SmoothFundingCurve (_dtSpot, strCurrency,
				_astrDepositTenor, _adblDepositQuote, "ForwardRate", _adblFuturesQuote, "ForwardRate",
					_astrFixFloatTenor, _adblFixFloatQuote, "SwapRate");

		if (null == mdfc) throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		org.drip.analytics.date.JulianDate[] adtSpot = org.drip.analytics.support.Helper.SpotDateArray
			(_dtSpot, null == _astrGovvieTenor ? 0 : _astrGovvieTenor.length);

		org.drip.analytics.date.JulianDate[] adtMaturity = org.drip.analytics.support.Helper.FromTenor
			(_dtSpot, _astrGovvieTenor);

		org.drip.state.govvie.GovvieCurve gc = org.drip.service.template.LatentMarketStateBuilder.GovvieCurve
			(_strGovvieCode, _dtSpot, adtSpot, adtMaturity, _adblGovvieQuote, _adblGovvieQuote, "Yield",
				org.drip.service.template.LatentMarketStateBuilder.SHAPE_PRESERVING);

		if (null == gc) throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (_bond.isFloater()) {
			org.drip.analytics.cashflow.CompositeFloatingPeriod cfp =
				(org.drip.analytics.cashflow.CompositeFloatingPeriod) _bond.stream().containingPeriod
					(_dtSpot.julian());

			_iResetDate = ((org.drip.analytics.cashflow.ComposableUnitFloatingPeriod) (cfp.periods().get
				(0))).referenceIndexPeriod().fixingDate();
		}

		if (null == (_csqcFundingBase = org.drip.param.creator.MarketParamsBuilder.Create (mdfc, gc, null,
			null, null, null, null)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		org.drip.state.identifier.ForwardLabel fl = _bond.isFloater() ? _bond.floaterSetting().fri() : null;

		if (_bond.isFloater() && !_csqcFundingBase.setFixing (_iResetDate, fl, _dblResetRate))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (null == (_csqcFunding01Up = org.drip.param.creator.MarketParamsBuilder.Create
			(org.drip.service.template.LatentMarketStateBuilder.SmoothFundingCurve (_dtSpot, strCurrency,
				_astrDepositTenor, org.drip.analytics.support.Helper.ParallelNodeBump (_adblDepositQuote,
					0.0001), "ForwardRate", org.drip.analytics.support.Helper.ParallelNodeBump
						(_adblFuturesQuote, 0.0001), "ForwardRate", _astrFixFloatTenor,
							org.drip.analytics.support.Helper.ParallelNodeBump (_adblFixFloatQuote, 0.0001),
								"SwapRate"), gc, null, null, null, null, null)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (_bond.isFloater() && !_csqcFunding01Up.setFixing (_iResetDate, fl, _dblResetRate + 0.0001))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (null == (_csqcFundingEuroDollar = org.drip.param.creator.MarketParamsBuilder.Create
			(org.drip.service.template.LatentMarketStateBuilder.SmoothFundingCurve (_dtSpot, strCurrency,
				_astrDepositTenor, _adblDepositQuote, "ForwardRate", _adblFuturesQuote, "ForwardRate", null,
					null, "SwapRate"), gc, null, null, null, null, null)))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		if (_bond.isFloater() && !_csqcFundingEuroDollar.setFixing (_iResetDate, fl, _dblResetRate))
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		java.util.Map<java.lang.String, org.drip.state.discount.MergedDiscountForwardCurve> mapTenorFunding =
			org.drip.service.template.LatentMarketStateBuilder.BumpedFundingCurve (_dtSpot, strCurrency,
				_astrDepositTenor, _adblDepositQuote, "ForwardRate", _adblFuturesQuote, "ForwardRate",
					_astrFixFloatTenor, _adblFixFloatQuote, "SwapRate",
						org.drip.service.template.LatentMarketStateBuilder.SMOOTH, 0.0001, false);

		if (null == mapTenorFunding)
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		for (java.util.Map.Entry<java.lang.String, org.drip.state.discount.MergedDiscountForwardCurve>
			meTenorFunding : mapTenorFunding.entrySet()) {
			org.drip.param.market.CurveSurfaceQuoteContainer csqcFundingTenor =
				org.drip.param.creator.MarketParamsBuilder.Create (meTenorFunding.getValue(), gc, null, null,
					null, null, null);

			if (null == csqcFundingTenor)
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			_mapCSQCFunding.put (meTenorFunding.getKey(), csqcFundingTenor);

			if (_bond.isFloater() && !csqcFundingTenor.setFixing (_iResetDate, fl, _dblResetRate + 0.0001))
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");
		}

		java.util.Map<java.lang.String, org.drip.state.govvie.GovvieCurve> mapTenorGovvie =
			org.drip.service.template.LatentMarketStateBuilder.BumpedGovvieCurve (_strGovvieCode, _dtSpot,
				adtSpot, adtMaturity,_adblGovvieQuote, _adblGovvieQuote, "Yield",
					org.drip.service.template.LatentMarketStateBuilder.SHAPE_PRESERVING, 0.0001, false);

		if (null == mapTenorGovvie)
			throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

		for (java.util.Map.Entry<java.lang.String, org.drip.state.govvie.GovvieCurve> meTenorGovvie :
			mapTenorGovvie.entrySet()) {
			org.drip.param.market.CurveSurfaceQuoteContainer csqcGovvieTenor =
				org.drip.param.creator.MarketParamsBuilder.Create (mdfc, meTenorGovvie.getValue(), null,
					null, null, null, null);

			if (null == csqcGovvieTenor)
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			_mapCSQCGovvie.put (meTenorGovvie.getKey(), csqcGovvieTenor);

			if (_bond.isFloater() && !csqcGovvieTenor.setFixing (_iResetDate, fl, _dblResetRate))
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");
		}

		org.drip.state.identifier.CreditLabel cl = _bond.creditLabel();

		if (null != cl) {
			java.lang.String strReferenceEntity = cl.referenceEntity();

			if (null == (_csqcCreditBase = org.drip.param.creator.MarketParamsBuilder.Create (mdfc, gc,
				org.drip.service.template.LatentMarketStateBuilder.CreditCurve (_dtSpot, strReferenceEntity,
					_astrCreditTenor, _adblCreditQuote, _adblCreditQuote, "FairPremium", mdfc), null, null,
						null, null)))
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			if (_bond.isFloater() && !_csqcCreditBase.setFixing (_iResetDate, fl, _dblResetRate))
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			if (null == (_csqcCredit01Up = org.drip.param.creator.MarketParamsBuilder.Create (mdfc, gc,
				org.drip.service.template.LatentMarketStateBuilder.CreditCurve (_dtSpot, strReferenceEntity,
					_astrCreditTenor, _adblCreditQuote, org.drip.analytics.support.Helper.ParallelNodeBump
						(_adblCreditQuote, 1.), "FairPremium", mdfc), null, null, null, null)))
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			if (_bond.isFloater() && !_csqcCredit01Up.setFixing (_iResetDate, fl, _dblResetRate))
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			java.util.Map<java.lang.String, org.drip.state.credit.CreditCurve> mapTenorCredit =
				org.drip.service.template.LatentMarketStateBuilder.BumpedCreditCurve (_dtSpot,
					strReferenceEntity, _astrCreditTenor, _adblCreditQuote, _adblCreditQuote, "FairPremium",
						mdfc, 1., false);

			if (null == mapTenorCredit)
				throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

			for (java.util.Map.Entry<java.lang.String, org.drip.state.credit.CreditCurve> meTenorCredit :
				mapTenorCredit.entrySet()) {
				org.drip.param.market.CurveSurfaceQuoteContainer csqcCreditTenor =
					org.drip.param.creator.MarketParamsBuilder.Create (mdfc, gc, meTenorCredit.getValue(),
						null, null, null, null);

				if (null == csqcCreditTenor)
					throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");

				_mapCSQCCredit.put (meTenorCredit.getKey(), csqcCreditTenor);

				if (_bond.isFloater() && !csqcCreditTenor.setFixing (_iResetDate, fl, _dblResetRate))
					throw new java.lang.Exception ("AladdinReplicator Constructor => Invalid Inputs");
			}
		}
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
	 * Retrieve the Custom Yield Bump
	 * 
	 * @return The Custom Yield Bump
	 */

	public double customYieldBump()
	{
		return _dblCustomYieldBump;
	}

	/**
	 * Retrieve the Custom Credit Basis Bump
	 * 
	 * @return The Custom Credit Basis Bump
	 */

	public double customCreditBasisBump()
	{
		return _dblCustomCreditBasisBump;
	}

	/**
	 * Retrieve the Z Spread Bump
	 * 
	 * @return The Z Spread Bump
	 */

	public double zSpreadBump()
	{
		return _dblZSpreadBump;
	}

	/**
	 * Retrieve the Spread Duration Multiplier
	 * 
	 * @return The Spread Duration Multiplier
	 */

	public double spreadDurationMultiplier()
	{
		return _dblSpreadDurationMultiplier;
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
	 * Retrieve the Map of the Tenor Bumped Instances of the Funding Curve CSQC
	 * 
	 * @return The Map of the Tenor Bumped Instances of the Funding Curve CSQC
	 */

	public java.util.Map<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
		fundingTenorCSQC()
	{
		return _mapCSQCFunding;
	}

	/**
	 * Retrieve the Map of the Tenor Bumped Instances of the Govvie Curve CSQC
	 * 
	 * @return The Map of the Tenor Bumped Instances of the Govvie Curve CSQC
	 */

	public java.util.Map<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
		govvieTenorCSQC()
	{
		return _mapCSQCGovvie;
	}

	/**
	 * Retrieve the Map of the Tenor Bumped Instances of the Credit Curve CSQC
	 * 
	 * @return The Map of the Tenor Bumped Instances of the Credit Curve CSQC
	 */

	public java.util.Map<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
		creditTenorCSQC()
	{
		return _mapCSQCCredit;
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
	 * Retrieve the CSQC built out of the Base Euro Dollar Curve
	 * 
	 * @return The CSQC built out of the Base Euro Dollar Curve
	 */

	public org.drip.param.market.CurveSurfaceQuoteContainer fundingEuroDollarCSQC()
	{
		return _csqcFundingEuroDollar;
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

	/**
	 * Retrieve the Reset Date
	 * 
	 * @return The Reset Date
	 */

	public int resetDate()
	{
		return _iResetDate;
	}

	/**
	 * Retrieve the Reset Rate
	 * 
	 * @return The Reset Rate
	 */

	public double resetRate()
	{
		return _dblResetRate;
	}

	/**
	 * Generate an Instance of a Replication Run
	 * 
	 * @return Instance of a Replication Run
	 */

	public org.drip.service.scenario.AladdinReplicationRun generateRun()
	{
		int iMaturityDate = _bond.maturityDate().julian();

		double dblNextPutFactor = 1.;
		double dblNextCallFactor = 1.;
		int iNextPutDate = iMaturityDate;
		int iNextCallDate = iMaturityDate;

		int iSpotDate = _dtSpot.julian();

		java.lang.String strCurrency = _bond.currency();

		org.drip.product.params.EmbeddedOptionSchedule eosPut = _bond.putSchedule();

		org.drip.product.params.EmbeddedOptionSchedule eosCall = _bond.callSchedule();

		org.drip.service.scenario.AladdinReplicationRun arr = new
			org.drip.service.scenario.AladdinReplicationRun();

		org.drip.param.valuation.WorkoutInfo wi = _bond.exerciseYieldFromPrice (_valParams, _csqcFundingBase,
			null, _dblCurrentPrice);

		if (null == wi) return null;

		int iWorkoutDate = wi.date();

		double dblWorkoutFactor = wi.factor();

		double dblYieldToExercise = wi.yield();

		java.util.Map<java.lang.String, java.lang.Double> mapLIBORKRD = new
			org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		java.util.Map<java.lang.String, java.lang.Double> mapLIBORKPRD = new
			org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		java.util.Map<java.lang.String, java.lang.Double> mapGovvieKRD = new
			org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		java.util.Map<java.lang.String, java.lang.Double> mapGovvieKPRD = new
			org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		java.util.Map<java.lang.String, java.lang.Double> mapCreditKRD = new
			org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		java.util.Map<java.lang.String, java.lang.Double> mapCreditKPRD = new
			org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		try {
			if (null != eosCall) {
				iNextCallDate = eosCall.nextDate (iSpotDate);

				dblNextCallFactor = eosCall.nextFactor (iSpotDate);
			}

			if (null != eosPut) {
				iNextPutDate = eosPut.nextDate (iSpotDate);

				dblNextPutFactor = eosPut.nextFactor (iSpotDate);
			}

			double dblAccrued = _bond.accrued (_dtSettle.julian(), _csqcFundingBase);

			double dblYieldToMaturity = _bond.yieldFromPrice (_valParams, _csqcFundingBase, null,
				_dblCurrentPrice);

			double dblBondEquivalentYieldToMaturity = _bond.yieldFromPrice (_valParams, _csqcFundingBase,
				org.drip.param.valuation.ValuationCustomizationParams.BondEquivalent (strCurrency),
					_dblCurrentPrice);

			double dblYieldToMaturityFwdCoupon = _bond.yieldFromPrice (_valParams, _csqcFundingBase, new
				org.drip.param.valuation.ValuationCustomizationParams (_bond.couponDC(), _bond.freq(), false,
					null, strCurrency, false, true), _dblCurrentPrice);

			double dblYieldFromPriceNextCall = _bond.yieldFromPrice (_valParams, _csqcFundingBase, null,
				iNextCallDate, dblNextCallFactor, _dblCurrentPrice);

			double dblYieldFromPriceNextPut = _bond.yieldFromPrice (_valParams, _csqcFundingBase, null,
				iNextPutDate, dblNextPutFactor, _dblCurrentPrice);

			double dblNominalYield = _bond.yieldFromPrice (_valParams, _csqcFundingBase, null,
				_dblIssuePrice);

			double dblOASToMaturity = _bond.oasFromPrice (_valParams, _csqcFundingBase, null,
				_dblCurrentPrice);

			double dblZSpreadToMaturity = _bond.isFloater() ? _bond.discountMarginFromPrice (_valParams,
				_csqcFundingBase, null, _dblCurrentPrice) : _bond.zSpreadFromPrice (_valParams,
					_csqcFundingBase, null, _dblCurrentPrice);

			double dblZSpreadToExercise = _bond.isFloater() ? _bond.discountMarginFromPrice (_valParams,
				_csqcFundingBase, null, iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice) :
					_bond.zSpreadFromPrice (_valParams, _csqcFundingBase, null, iWorkoutDate,
						dblWorkoutFactor, _dblCurrentPrice);

			double dblBondBasisToMaturity = _bond.bondBasisFromPrice (_valParams, _csqcFundingBase, null,
				_dblCurrentPrice);

			double dblBondBasisToExercise = _bond.bondBasisFromPrice (_valParams, _csqcFundingBase, null,
				iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice);

			double dblModifiedDurationFromBondBasis = (_dblCurrentPrice - _bond.priceFromBondBasis
				(_valParams, _csqcFunding01Up, null, dblBondBasisToMaturity)) / _dblCurrentPrice;

			double dblMacaulayDurationToMaturity = _bond.macaulayDurationFromPrice (_valParams,
				_csqcFundingBase, null, _dblCurrentPrice);

			double dblModifiedDurationFromBondBasisToWorst = (_dblCurrentPrice - _bond.priceFromBondBasis
				(_valParams, _csqcFunding01Up, null, iWorkoutDate, dblWorkoutFactor, dblBondBasisToExercise))
					/ _dblCurrentPrice;

			double dblDV01 = 0.5 * (_bond.priceFromYield (_valParams, _csqcFundingBase, null, iWorkoutDate,
				dblWorkoutFactor, dblYieldToExercise - 0.0001 * _dblCustomYieldBump) - _bond.priceFromYield
					(_valParams, _csqcFundingBase, null, iWorkoutDate, dblWorkoutFactor, dblYieldToExercise +
						0.0001 * _dblCustomYieldBump)) / _dblCustomYieldBump;

			double dblCreditBasisToExercise = _bond.creditBasisFromPrice (_valParams, _csqcCreditBase, null,
				iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice);

			double dblParCreditBasisToExercise = _bond.creditBasisFromPrice (_valParams, _csqcCreditBase,
				null, iWorkoutDate, dblWorkoutFactor, 1.);

			double dblEffectiveDurationAdjusted = 0.5 * (_bond.priceFromCreditBasis (_valParams,
				_csqcCreditBase, null, iWorkoutDate, dblWorkoutFactor, dblCreditBasisToExercise -
					_dblCustomCreditBasisBump) - _bond.priceFromCreditBasis (_valParams, _csqcCreditBase,
						null, iWorkoutDate, dblWorkoutFactor, dblCreditBasisToExercise +
							_dblCustomCreditBasisBump)) / _dblCurrentPrice / _dblCustomCreditBasisBump;

			double dblSpreadDuration = _dblSpreadDurationMultiplier * (_dblCurrentPrice - (_bond.isFloater()
				? _bond.priceFromDiscountMargin (_valParams, _csqcFundingBase, null, iWorkoutDate,
					dblWorkoutFactor, dblZSpreadToExercise + 0.0001 * _dblZSpreadBump) :
						_bond.priceFromZSpread (_valParams, _csqcFundingBase, null, iWorkoutDate,
							dblWorkoutFactor, dblZSpreadToExercise + 0.0001 * _dblZSpreadBump))) /
								_dblCurrentPrice;

			double dblCV01 = _dblCurrentPrice - _bond.priceFromCreditBasis (_valParams, _csqcCredit01Up,
				null, iWorkoutDate, dblWorkoutFactor, dblCreditBasisToExercise);

			double dblConvexityToExercise = _bond.convexityFromPrice (_valParams, _csqcFundingBase, null,
				iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice);

			double dblDiscountMarginToExercise = dblYieldToExercise - _csqcFundingBase.fundingState
				(_bond.fundingLabel()).libor (_valParams.valueDate(), "1M");

			double dblESpreadToExercise = _bond.isFloater() ? _bond.discountMarginFromPrice (_valParams,
				_csqcFundingEuroDollar, null, iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice) :
					_bond.zSpreadFromPrice (_valParams, _csqcFundingEuroDollar, null, iWorkoutDate,
						dblWorkoutFactor, _dblCurrentPrice);

			double dblISpreadToExercise = _bond.iSpreadFromPrice (_valParams, _csqcFundingBase, null,
				iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice);

			double dblJSpreadToExercise = _bond.jSpreadFromPrice (_valParams, _csqcFundingBase, null,
				iWorkoutDate, dblWorkoutFactor, _dblCurrentPrice);

			double dblWALToExercise = _bond.weightedAverageLife (_valParams, _csqcFundingBase, iWorkoutDate,
				dblWorkoutFactor);

			double dblWALPrincipalOnlyToExercise = _bond.weightedAverageLifePrincipalOnly (_valParams,
				_csqcFundingBase, iWorkoutDate, dblWorkoutFactor);

			double dblWALLossOnlyToExercise = _bond.weightedAverageLifeLossOnly (_valParams, _csqcCreditBase,
				iWorkoutDate, dblWorkoutFactor);

			double dblWALCouponOnlyToExercise = _bond.weightedAverageLifeCouponOnly (_valParams,
				_csqcFundingBase, iWorkoutDate, dblWorkoutFactor);

			double dblOASToExercise = _bond.oasFromPrice (_valParams, _csqcFundingBase, null, iWorkoutDate,
				dblWorkoutFactor, _dblCurrentPrice);

			double dblParOASToExercise = _bond.oasFromPrice (_valParams, _csqcFundingBase, null,
				iWorkoutDate, dblWorkoutFactor, 1.);

			double dblAccruedInterestFactor = dblAccrued * _dblFX;
			double dblEffectiveDuration = dblDV01 / _dblCurrentPrice;
			double dblSpreadDuration$ = dblSpreadDuration * _dblIssueAmount;

			for (java.util.Map.Entry<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
				meCSQC : _mapCSQCFunding.entrySet()) {
				java.lang.String strKey = meCSQC.getKey();

				org.drip.param.market.CurveSurfaceQuoteContainer csqcTenor = meCSQC.getValue();

				double dblTenorPrice = _bond.isFloater() ? _bond.priceFromDiscountMargin (_valParams,
					csqcTenor, null, iWorkoutDate, dblWorkoutFactor, dblZSpreadToExercise) :
						_bond.priceFromZSpread (_valParams, csqcTenor, null, iWorkoutDate, dblWorkoutFactor,
							dblZSpreadToExercise);

				mapLIBORKRD.put (strKey, (_dblCurrentPrice - dblTenorPrice) / _dblCurrentPrice);

				mapLIBORKPRD.put (strKey, 1. - dblTenorPrice);
			}

			for (java.util.Map.Entry<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
				meCSQC : _mapCSQCGovvie.entrySet()) {
				java.lang.String strKey = meCSQC.getKey();

				org.drip.param.market.CurveSurfaceQuoteContainer csqcTenor = meCSQC.getValue();

				mapGovvieKRD.put (strKey, (_dblCurrentPrice - _bond.priceFromOAS (_valParams, csqcTenor,
					null, iWorkoutDate, dblWorkoutFactor, dblOASToExercise)) / _dblCurrentPrice);

				mapGovvieKPRD.put (strKey, (1. - _bond.priceFromOAS (_valParams, csqcTenor, null,
					iWorkoutDate, dblWorkoutFactor, dblParOASToExercise)));
			}

			for (java.util.Map.Entry<java.lang.String, org.drip.param.market.CurveSurfaceQuoteContainer>
				meCSQC : _mapCSQCCredit.entrySet()) {
				java.lang.String strKey = meCSQC.getKey();

				org.drip.param.market.CurveSurfaceQuoteContainer csqcTenor = meCSQC.getValue();

				mapCreditKRD.put (strKey, (_dblCurrentPrice - _bond.priceFromCreditBasis (_valParams,
					csqcTenor, null, iWorkoutDate, dblWorkoutFactor, dblCreditBasisToExercise)) /
						_dblCurrentPrice);

				mapCreditKPRD.put (strKey, (1. - _bond.priceFromCreditBasis (_valParams, csqcTenor, null,
					iWorkoutDate, dblWorkoutFactor, dblParCreditBasisToExercise)));
			}

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Price", _dblCurrentPrice)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Market Value",
				_dblCurrentPrice * _dblIssueAmount)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Accrued", dblAccrued)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Accrued$", dblAccrued *
				_dblIssueAmount)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Accrued Interest Factor",
				dblAccruedInterestFactor)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Yield To Maturity",
				dblYieldToMaturity)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Yield To Maturity CBE",
				dblBondEquivalentYieldToMaturity)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("YTM fwdCpn",
				dblYieldToMaturityFwdCoupon)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Yield To Worst",
				dblYieldToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("YIELD TO CALL",
				dblYieldFromPriceNextCall)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("YIELD TO PUT",
				dblYieldFromPriceNextPut)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Nominal Yield",
				dblNominalYield)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Z_Spread", dblOASToMaturity)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Z_Vol_OAS",
				dblZSpreadToMaturity)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("OAS", dblZSpreadToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("TSY OAS", dblOASToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("MOD DUR",
				dblModifiedDurationFromBondBasis)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("MACAULAY DURATION",
				dblMacaulayDurationToMaturity)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("MOD DUR TO WORST",
				dblModifiedDurationFromBondBasisToWorst)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("EFFECTIVE DURATION",
				dblEffectiveDuration)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("EFFECTIVE DURATION ADJ",
				dblEffectiveDurationAdjusted)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("OAD MULT",
				dblEffectiveDurationAdjusted / dblEffectiveDuration)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Spread Dur",
				dblSpreadDuration)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Spread Dur $",
				dblSpreadDuration$)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("DV01", dblDV01))) return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("CV01", dblCV01))) return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Convexity",
				dblConvexityToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("Modified Convexity",
				dblConvexityToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("DISCOUNT MARGIN",
				dblDiscountMarginToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("E-Spread",
				dblESpreadToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("I-Spread",
				dblISpreadToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("J-Spread",
				dblJSpreadToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("WAL To Worst",
				dblWALToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("WAL",
				dblWALPrincipalOnlyToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("WAL2",
				dblWALLossOnlyToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("WAL3",
				dblWALCouponOnlyToExercise)))
				return null;

			if (!arr.addNamedField (new org.drip.service.scenario.NamedField ("WAL4", dblWALToExercise)))
				return null;

			if (!arr.addNamedFieldMap (new org.drip.service.scenario.NamedFieldMap ("LIBOR KRD",
				mapLIBORKRD)))
				return null;

			if (!arr.addNamedFieldMap (new org.drip.service.scenario.NamedFieldMap ("LIBOR KPRD",
				mapLIBORKPRD)))
				return null;

			if (!arr.addNamedFieldMap (new org.drip.service.scenario.NamedFieldMap ("Govvie KRD",
				mapGovvieKRD)))
				return null;

			if (!arr.addNamedFieldMap (new org.drip.service.scenario.NamedFieldMap ("Govvie KPRD",
				mapGovvieKPRD)))
				return null;

			if (!arr.addNamedFieldMap (new org.drip.service.scenario.NamedFieldMap ("Credit KRD",
				mapCreditKRD)))
				return null;

			if (!arr.addNamedFieldMap (new org.drip.service.scenario.NamedFieldMap ("Credit KPRD",
				mapCreditKPRD)))
				return null;
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		return arr;
	}
}
