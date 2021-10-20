package de.mbe.aws.tests.models;

public final record StockTrade(StockSymbol stockSymbol, TradeType tradeType, double price, long quantity, String id) { }
