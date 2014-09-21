package com.cardfight.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CardFightSummary implements IsSerializable {
	public static final int ENUMERATION = 1;
	public static final int MONTECARLO  = 2;
	
	private static final String typeStrings[] = {"", "Enumeration", "MonteCarlo"};
	
	private ArrayList<CardFightResult> results;
	private long count;
	private int resultType;
	private long milliTime;
	
	public void setResults( ArrayList<CardFightResult> results ) {
		this.results = results;
	}
	
	public ArrayList<CardFightResult> getResults() {
		return results;
	}
	
	public void setCount( long count ) {
		this.count = count;
	}
	
	public long getCount() {
		return count;
	}
	
	public void setResultType( int type ) {
		resultType = type;
	}
	
	public long getTime() {
		return milliTime;
	}
	
	public void setTime( long time ) {
		milliTime = time;
	}
	
	public int getResultType() {
		return resultType;
	}
	
	public String getResultTypeString() {
		return typeStrings[resultType];
	}
	
	public String toString() {
		return "";
	}
}
