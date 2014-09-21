package com.cardfight.client;

import com.google.gwt.user.client.rpc.RemoteService;
import java.util.ArrayList;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

 //RemoteServiceRelativePath("../GWTHandRanges/CardFightService")
//RemoteServiceRelativePath("CardFightService")
// /GWTHandRanges/gwthandranges/CardFightService
// @RemoteServiceRelativePath("../gwthandranges/CardFightService")


@RemoteServiceRelativePath("../../GWTHandRanges/gwthandranges/CardFightService")
public interface CardFightService extends RemoteService {
	  public CardFightSummary calculate(ArrayList<String> playersHands, String board, String discard);
}
