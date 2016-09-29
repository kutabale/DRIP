
package org.drip.json.simple;

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
 * JSONObject is an Adaptation of the JSONObject Class from the RFC4627 compliant JSON Simple
 *  (https://code.google.com/p/json-simple/).
 *
 * @author Fang Yidong
 * @author Lakshmi Krishnamurthy
 */

@SuppressWarnings ("rawtypes") public class JSONObject extends java.util.HashMap implements java.util.Map, JSONAware, JSONStreamAware{
    
    private static final long serialVersionUID = -503443796854799292L;
    
    
    public JSONObject() {
            super();
    }

    /**
     * Allows creation of a JSONObject from a Map. After that, both the
     * generated JSONObject and the Map can be modified independently.
     * 
     * @param map
     */

    @SuppressWarnings ("unchecked") public JSONObject(java.util.Map map) {
            super(map);
    }


/**
 * Encode a map into JSON text and write it to out.
 * If this map is also a JSONAware or JSONStreamAware, JSONAware or JSONStreamAware specific behaviours will be ignored at this top level.
 * 
 * @see org.json.simple.JSONValue#writeJSONString(Object, Writer)
 * 
 * @param map
 * @param out
 */
    public static void writeJSONString(java.util.Map map, java.io.Writer out) throws java.io.IOException {
            if(map == null){
                    out.write("null");
                    return;
            }
            
            boolean first = true;
            java.util.Iterator iter=map.entrySet().iterator();
            
    out.write('{');
            while(iter.hasNext()){
        if(first)
            first = false;
        else
            out.write(',');
        java.util.Map.Entry entry=(java.util.Map.Entry)iter.next();
        out.write('\"');
        out.write(escape(String.valueOf(entry.getKey())));
        out.write('\"');
        out.write(':');
                    JSONValue.writeJSONString(entry.getValue(), out);
            }
            out.write('}');
    }

    public void writeJSONString(java.io.Writer out) throws java.io.IOException{
            writeJSONString(this, out);
    }
    
    /**
     * Convert a map to JSON text. The result is a JSON object. 
     * If this map is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
     * 
     * @see org.json.simple.JSONValue#toJSONString(Object)
     * 
     * @param map
     * @return JSON text, or "null" if map is null.
     */
    public static String toJSONString(java.util.Map map){
            if(map == null)
                    return "null";
            
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    java.util.Iterator iter=map.entrySet().iterator();
            
    sb.append('{');
            while(iter.hasNext()){
        if(first)
            first = false;
        else
            sb.append(',');
        
        java.util.Map.Entry entry=(java.util.Map.Entry)iter.next();
                    toJSONString(String.valueOf(entry.getKey()),entry.getValue(), sb);
            }
    sb.append('}');
            return sb.toString();
    }
    
    public String toJSONString(){
            return toJSONString(this);
    }
    
    private static String toJSONString(String key,Object value, StringBuffer sb){
            sb.append('\"');
    if(key == null)
        sb.append("null");
    else
        JSONValue.escape(key, sb);
            sb.append('\"').append(':');
            
            sb.append(JSONValue.toJSONString(value));
            
            return sb.toString();
    }
    
    public String toString(){
            return toJSONString();
    }

    public static String toString(String key,Object value){
    StringBuffer sb = new StringBuffer();
            toJSONString(key, value, sb);
    return sb.toString();
    }
    
    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     * It's the same as JSONValue.escape() only for compatibility here.
     * 
     * @see org.json.simple.JSONValue#escape(String)
     * 
     * @param s
     * @return
     */
    public static String escape(String s){
            return JSONValue.escape(s);
    }
}
