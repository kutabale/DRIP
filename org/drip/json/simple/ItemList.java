
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
 * ItemList is an Adaptation of the ItemList Interface from the RFC4627 compliant JSON Simple
 *  (https://code.google.com/p/json-simple/).
 *
 * 		|a:b:c| => |a|,|b|,|c|
 * 		|:| => ||,||
 * 		|a:| => |a|,||
 * 
 * @author Fang Yidong
 * @author Lakshmi Krishnamurthy
 */

public class ItemList {
    private String sp=",";
    @SuppressWarnings ("rawtypes") java.util.List items=new java.util.ArrayList();
    
    
    public ItemList(){}
    
    
    public ItemList(String s){
            this.split(s,sp,items);
    }
    
    public ItemList(String s,String sp){
            this.sp=s;
            this.split(s,sp,items);
    }
    
    public ItemList(String s,String sp,boolean isMultiToken){
            split(s,sp,items,isMultiToken);
    }
    
    @SuppressWarnings ("rawtypes") public java.util.List getItems(){
            return this.items;
    }
    
    public String[] getArray(){
            return (String[])this.items.toArray();
    }
    
    @SuppressWarnings ({"rawtypes", "unchecked"}) public void split(String s,String sp,java.util.List append,boolean isMultiToken){
            if(s==null || sp==null)
                    return;
            if(isMultiToken){
            	java.util.StringTokenizer tokens=new java.util.StringTokenizer(s,sp);
                    while(tokens.hasMoreTokens()){
                            append.add(tokens.nextToken().trim());
                    }
            }
            else{
                    this.split(s,sp,append);
            }
    }
    
    @SuppressWarnings ({"rawtypes", "unchecked"}) public void split(String s,String sp,java.util.List append){
            if(s==null || sp==null)
                    return;
            int pos=0;
            int prevPos=0;
            do{
                    prevPos=pos;
                    pos=s.indexOf(sp,pos);
                    if(pos==-1)
                            break;
                    append.add(s.substring(prevPos,pos).trim());
                    pos+=sp.length();
            }while(pos!=-1);
            append.add(s.substring(prevPos).trim());
    }
    
    public void setSP(String sp){
            this.sp=sp;
    }
    
    @SuppressWarnings ("unchecked") public void add(int i,String item){
            if(item==null)
                    return;
            items.add(i,item.trim());
    }

    @SuppressWarnings ("unchecked") public void add(String item){
            if(item==null)
                    return;
            items.add(item.trim());
    }
    
    @SuppressWarnings ("unchecked") public void addAll(ItemList list){
            items.addAll(list.items);
    }
    
    public void addAll(String s){
            this.split(s,sp,items);
    }
    
    public void addAll(String s,String sp){
            this.split(s,sp,items);
    }
    
    public void addAll(String s,String sp,boolean isMultiToken){
            this.split(s,sp,items,isMultiToken);
    }
    
    /**
     * @param i 0-based
     * @return
     */
    public String get(int i){
            return (String)items.get(i);
    }
    
    public int size(){
            return items.size();
    }

    public String toString(){
            return toString(sp);
    }
    
    public String toString(String sp){
            StringBuffer sb=new StringBuffer();
            
            for(int i=0;i<items.size();i++){
                    if(i==0)
                            sb.append(items.get(i));
                    else{
                            sb.append(sp);
                            sb.append(items.get(i));
                    }
            }
            return sb.toString();

    }
    
    public void clear(){
            items.clear();
    }
    
    public void reset(){
            sp=",";
            items.clear();
    }
}