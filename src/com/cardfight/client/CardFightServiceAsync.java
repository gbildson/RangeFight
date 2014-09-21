package com.cardfight.client;

import java.util.ArrayList;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CardFightServiceAsync {
    public void calculate(ArrayList<String> playersHands, String board, String discard, AsyncCallback<CardFightSummary> callback);
}
