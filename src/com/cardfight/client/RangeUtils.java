package com.cardfight.client;

import java.util.ArrayList;
import java.util.HashSet;


//import com.cardfight.client.RangeFight.HandID;

public class RangeUtils {
	  private static String rank[] = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
	  private static String suit[] = {"s", "h", "d", "c"};
	  private static HashSet<String> rankSet = null;
	  private static HashSet<String> suitSet = null;
	  private static final int PAIR     = 1;
	  private static final int SUITED   = 2;
	  private static final int UNSUITED = 3;
	  
	  static { 
		  rankSet = new HashSet<String>();
		  for (int i = 0; i < rank.length; i++)
			  rankSet.add(rank[i]);
		  suitSet = new HashSet<String>();
		  for (int i = 0; i < suit.length; i++)
			  suitSet.add(suit[i]);
	  }
	  
	  static class HandID {
		  int topRank;
		  int bottomRank;
		  int type;
		  
		  public String toString() {
			  return " top:"+topRank+" bot:"+ bottomRank+ " type: "+type;
		  }
	  }
	  
	  
	  public static HashSet<String> parseRange(String range) {
		  HashSet<String> results = new HashSet<String>();
		  HandID handID;
		  HandID handID2;
	      int    size;

		  range = range.replaceAll(" ", "");
		  range = range.replaceAll(",", "");
		  range = range.replaceAll(";", "");
		  
		  //System.out.println("FULL RANGE: " +range);
		  
		  while (range.length() > 0) {
			  handID = parseOneHand(range);
			  System.out.println(" one: " +handID);
	          if ( handID == null ) return null; 
	          if ( handID.type == PAIR) {
	        	  results.add(rank[handID.topRank]+rank[handID.bottomRank]);
	        	  size = 2;
	        	  range = range.substring(size);
	          } else {
	        	  results.add(rank[handID.topRank]+rank[handID.bottomRank]+ (handID.type == SUITED ? "s" : "o"));
	        	  size = 3;
	        	  range = range.substring(size);
	          }
	          if ( range.length() == 0) break;
	          
	          String nextChar = range.substring(0,1);
	          
	          int incrementTop;
	          int incrementBottom;
	          if ( nextChar.equals("+") ){
	        	  System.out.println("+");
	        	  range = range.substring(1);
	        	  if ( handID.type == PAIR )
	            	  incrementTop = 0;
	        	  else
	        		  incrementTop = handID.topRank+1;
	        	  incrementBottom = handID.bottomRank;
	          } else if ( nextChar.equals("-") ) {
	        	  System.out.println("-");
	        	  range = range.substring(1);
	        	  
	        	  handID2 = parseOneHand(range);
	    		  //System.out.println(" one: " +handID2);

	        	  if ( handID2.type != handID.type ) return null;
	        	  if ( handID.type != PAIR && handID.topRank != handID2.topRank ) return null;
	        	  range = range.substring(size);
	        	  
	        	  if ( handID.bottomRank > handID2.bottomRank ) {
	        		  HandID temp = handID;
	        		  handID  = handID2;
	        		  handID2 = temp;
	        	  }
	        	  incrementTop    =   handID.bottomRank;
	        	  incrementBottom =   handID2.bottomRank;
	          } else {
	        	  continue;
			  }
	          //System.out.println("loop bot: "+incrementBottom +" top: "+incrementTop);
	          for ( int i = incrementBottom; i >= incrementTop; i-- ) {
	        	  if ( handID.type == PAIR )
	        		  results.add(rank[i]+rank[i]);
	        	  else {
	            	  //System.out.println("RANGE: "+rank[handID.topRank] + rank[i] + (handID.type == SUITED ? "s" : "o"));
	        		  results.add(rank[handID.topRank] + rank[i] + (handID.type == SUITED ? "s" : "o"));
	        	  }
	          }
		  }
		  
		  return results;
	  }
	  
	  private static HandID parseOneHand(String range) {
		  HandID result = new HandID();
		  String c1;
		  String c2;
		  String c3;
		  
		  c1 = range.substring(0,1);
		  c1 = c1.toUpperCase();
		  c2 = range.substring(1,2);
		  c2 = c2.toUpperCase();
		  c3 = "";
		  if ( range.length() > 2 ) {
			  c3 = range.substring(2,3);
			  c3 = c3.toLowerCase();
		  }
			  
		  if ( rankSet.contains(c1) && rankSet.contains(c2) ) {
			  // Handle pairs
			  if ( c1.equals(c2) ) {
				  result.topRank = getIndexOf(c1);
				  result.bottomRank = result.topRank;
				  result.type = PAIR;
			  }
			  else { 
				  result.topRank    = getIndexOf(c1);
				  result.bottomRank = getIndexOf(c2);
				  if ( result.topRank > result.bottomRank ) {
					  int temp          = result.topRank;
					  result.topRank    = result.bottomRank;
					  result.bottomRank = temp;
				  }
				  if ( c3.equals("s") ) {  
				      result.type = SUITED;
				  } else if ( c3.equals("o") ){
				      result.type = UNSUITED;
				  } else {
					  return null;
				  }
			  }
		  } else
			  return null;
		  
		  return result;
	  }
	  
	  
	  private static int getIndexOf(String c) {
		  for ( int i = 0; i < rank.length; i++ )
			  if ( rank[i].equals(c) )
				  return i;
		  return -1;
	  }
	  
	  public static ArrayList<String> parseCards(String cards) {
		  ArrayList<String> results = new ArrayList<String>();
		  cards = cards.replaceAll(" ", "");
		  cards = cards.replaceAll(",", "");
		  cards = cards.replaceAll(";", "");
		  

		  String c1;
		  String c2;

		  while (cards.length() > 0) {
			  c1 = cards.substring(0,1);
			  c1 = c1.toUpperCase();
			  c2 = cards.substring(1,2);
			  c2 = c2.toLowerCase();
			  if ( rankSet.contains(c1) && suitSet.contains(c2) ) {
				  results.add(c1+c2);
			  } else
				  return null;
			  cards = cards.substring(2);
		  }
		  return results;
	  }
	 
	  public static int parseRank(String rank) {
		  int idx = getIndexOf(rank);
		  return (12 - idx);
	  }
}
