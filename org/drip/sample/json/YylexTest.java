
package org.drip.sample.json;

import java.io.*;

import org.drip.json.parser.*;

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
 * YylexTest is an Adaptation of the YylexTest Class from the RFC4627 compliant JSON Simple
 *  (https://code.google.com/p/json-simple/).
 *
 * @author Fang Yidong
 * @author Lakshmi Krishnamurthy
 */

public class YylexTest {

    public static final void testYylex() throws Exception{
            String s="\"\\/\"";
            System.out.println(s);
            StringReader in = new StringReader(s);
            Yylex lexer=new Yylex(in);
            Yytoken token=lexer.yylex();
            System.out.println (Yytoken.TYPE_VALUE == token.type);
            System.out.println ("/".equalsIgnoreCase ((String) token.value));
            
            s="\"abc\\/\\r\\b\\n\\t\\f\\\\\"";
            System.out.println(s);
            in = new StringReader(s);
            lexer=new Yylex(in);
            token=lexer.yylex();
            System.out.println (Yytoken.TYPE_VALUE == token.type);
            System.out.println ("abc/\r\b\n\t\f\\".equalsIgnoreCase ((String)token.value));
            
            s="[\t \n\r\n{ \t \t\n\r}";
            System.out.println(s);
            in = new StringReader(s);
            lexer=new Yylex(in);
            token=lexer.yylex();
            System.out.println (Yytoken.TYPE_LEFT_SQUARE == token.type);
            token=lexer.yylex();
            System.out.println (Yytoken.TYPE_LEFT_BRACE == token.type);
            token=lexer.yylex();
            System.out.println (Yytoken.TYPE_RIGHT_BRACE == token.type);
            
            s="\b\f{";
            System.out.println(s);
            in = new StringReader(s);
            lexer=new Yylex(in);
            ParseException err=null;
            try{
                    token=lexer.yylex();
            }
            catch(ParseException e){
                    err=e;
                    System.out.println("error:"+err);
                    System.out.println (ParseException.ERROR_UNEXPECTED_CHAR == e.getErrorType());
                    System.out.println (0 == e.getPosition());
                    System.out.println (new Character('\b') == e.getUnexpectedObject());
            }
            catch(IOException ie){
                    throw ie;
            }
            System.out.println (err!=null);
            
            s="{a : b}";
            System.out.println(s);
            in = new StringReader(s);
            lexer=new Yylex(in);
            err=null;
            try{
                    lexer.yylex();
                    token=lexer.yylex();
            }
            catch(ParseException e){
                    err=e;
                    System.out.println("error:"+err);
                    System.out.println (ParseException.ERROR_UNEXPECTED_CHAR == e.getErrorType());
                    System.out.println (new Character('a') == e.getUnexpectedObject());
                    System.out.println (1 == e.getPosition());
            }
            catch(IOException ie){
                    throw ie;
            }
            System.out.println (err!=null);
    }

    public static final void main (
    	final String[] astrArgs)
    	throws Exception
	{
    	testYylex();
	}
}
