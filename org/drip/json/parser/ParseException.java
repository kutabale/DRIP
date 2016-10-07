
package org.drip.json.parser;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
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
 * ParseException is an Adaptation of the ParseException Class from the RFC4627 compliant JSON Simple
 *  (https://code.google.com/p/json-simple/).
 *
 * @author Fang Yidong
 * @author Lakshmi Krishnamurthy
 */

public class ParseException extends java.lang.Exception {
    private static final long serialVersionUID = -7880698968187728548L;
    
    public static final int ERROR_UNEXPECTED_CHAR = 0;
    public static final int ERROR_UNEXPECTED_TOKEN = 1;
    public static final int ERROR_UNEXPECTED_EXCEPTION = 2;

    private int errorType;
    private Object unexpectedObject;
    private int position;
    
    public ParseException(int errorType){
            this(-1, errorType, null);
    }
    
    public ParseException(int errorType, Object unexpectedObject){
            this(-1, errorType, unexpectedObject);
    }
    
    public ParseException(int position, int errorType, Object unexpectedObject){
            this.position = position;
            this.errorType = errorType;
            this.unexpectedObject = unexpectedObject;
    }
    
    public int getErrorType() {
            return errorType;
    }
    
    public void setErrorType(int errorType) {
            this.errorType = errorType;
    }
    
    /**
     * @see org.drip.json.parser.JSONParser#getPosition()
     * 
     * @return The character position (starting with 0) of the input where the error occurs.
     */
    public int getPosition() {
            return position;
    }
    
    public void setPosition(int position) {
            this.position = position;
    }
    
    /**
     * @see org.drip.json.parser.Yytoken
     * 
     * @return One of the following base on the value of errorType:
     *                      ERROR_UNEXPECTED_CHAR           java.lang.Character
     *                      ERROR_UNEXPECTED_TOKEN          org.json.simple.parser.Yytoken
     *                      ERROR_UNEXPECTED_EXCEPTION      java.lang.Exception
     */
    public Object getUnexpectedObject() {
            return unexpectedObject;
    }
    
    public void setUnexpectedObject(Object unexpectedObject) {
            this.unexpectedObject = unexpectedObject;
    }
    
    public String toString(){
            StringBuffer sb = new StringBuffer();
            
            switch(errorType){
            case ERROR_UNEXPECTED_CHAR:
                    sb.append("Unexpected character (").append(unexpectedObject).append(") at position ").append(position).append(".");
                    break;
            case ERROR_UNEXPECTED_TOKEN:
                    sb.append("Unexpected token ").append(unexpectedObject).append(" at position ").append(position).append(".");
                    break;
            case ERROR_UNEXPECTED_EXCEPTION:
                    sb.append("Unexpected exception at position ").append(position).append(": ").append(unexpectedObject);
                    break;
            default:
                    sb.append("Unkown error at position ").append(position).append(".");
                    break;
            }
            return sb.toString();
    }
}
