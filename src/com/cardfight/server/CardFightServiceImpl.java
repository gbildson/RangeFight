package com.cardfight.server;

import java.util.ArrayList;


import com.cardfight.client.CardFightResult;
import com.cardfight.client.CardFightSummary;
import com.cardfight.client.CardFightService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class CardFightServiceImpl extends RemoteServiceServlet implements CardFightService {

	public CardFightSummary calculate(ArrayList<String> playersHands, String board, String discard) {
		ArrayList<CardFightResult> results = new ArrayList<CardFightResult>();
		for (int i = 0; i < playersHands.size(); i++) {
			CardFightResult result = new CardFightResult();
			result.equity = "25.00";
			result.winPct = "24.00";
			result.tiePct = "1.00";
			results.add(result);
		}
		CardFightSummary summary = new CardFightSummary();
		summary.setResults(results);
		return summary;
	}


}

