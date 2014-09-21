package com.cardfight.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CardFightResult implements IsSerializable {
	public String equity;
	public String winPct;
	public String tiePct;
	
	public String toString() {
		return "Equity: "+equity+" Win %: "+winPct+" tie%: "+tiePct;
	}
}
