package de.mbe.aws.tests;

import de.mbe.aws.tests.models.StockPrice;
import de.mbe.aws.tests.models.StockSymbol;
import de.mbe.aws.tests.models.StockTrade;
import de.mbe.aws.tests.models.TradeType;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class StockTradeGenerator {

    private static final List<StockPrice> STOCK_PRICES = List.of(
            new StockPrice(StockSymbol.AAPL, 119.72),
            new StockPrice(StockSymbol.XOM, 91.56),
            new StockPrice(StockSymbol.GOOG, 527.83),
            new StockPrice(StockSymbol.BRKA, 223999.88),
            new StockPrice(StockSymbol.MSFT, 42.36),
            new StockPrice(StockSymbol.WFC, 54.21),
            new StockPrice(StockSymbol.JNJ, 99.78),
            new StockPrice(StockSymbol.WMT, 85.91),
            new StockPrice(StockSymbol.CHL, 66.96),
            new StockPrice(StockSymbol.GE, 24.64),
            new StockPrice(StockSymbol.NVS, 102.46),
            new StockPrice(StockSymbol.PG, 85.05),
            new StockPrice(StockSymbol.JPM, 57.82),
            new StockPrice(StockSymbol.RDSA, 66.72),
            new StockPrice(StockSymbol.CVX, 110.43),
            new StockPrice(StockSymbol.PFE, 33.07),
            new StockPrice(StockSymbol.FB, 74.44),
            new StockPrice(StockSymbol.VZ, 49.09),
            new StockPrice(StockSymbol.PTR, 111.08),
            new StockPrice(StockSymbol.BUD, 120.39),
            new StockPrice(StockSymbol.ORCL, 43.40),
            new StockPrice(StockSymbol.KO, 41.23),
            new StockPrice(StockSymbol.T, 34.64),
            new StockPrice(StockSymbol.DIS, 101.73),
            new StockPrice(StockSymbol.AMZN, 370.56));

    /** The ratio of the deviation from the mean price **/
    private static final double MAX_DEVIATION = 0.2; // ie 20%

    /** The number of shares is picked randomly between 1 and the MAX_QUANTITY **/
    private static final int MAX_QUANTITY = 10000;

    /** Probability of trade being a sell **/
    private static final double PROBABILITY_SELL = 0.4; // ie 40%

    private static final Random RANDOM = new Random();

    /**
     * Return a random stock trade with a unique id every time.
     *
     */
    public static StockTrade getRandomStockTrade() {
        // pick a random stock
        final var stockPrice = STOCK_PRICES.get(RANDOM.nextInt(STOCK_PRICES.size()));
        // pick a random deviation between -MAX_DEVIATION and +MAX_DEVIATION
        final var deviation = (RANDOM.nextDouble() - 0.5) * 2.0 * MAX_DEVIATION;
        // set the price using the deviation and mean price
        var price = stockPrice.price() * (1 + deviation);
        // round price to 2 decimal places
        price = Math.round(price * 100.0) / 100.0;

        // set the trade type to buy or sell depending on the probability of sell
        final var tradeType = (RANDOM.nextDouble() < PROBABILITY_SELL) ? TradeType.SELL : TradeType.BUY;

        // randomly pick a quantity of shares
        // add 1 because nextInt() will return between 0 (inclusive) and MAX_QUANTITY (exclusive). we want at least 1 share.
        final var quantity = RANDOM.nextInt(MAX_QUANTITY) + 1;

        return new StockTrade(stockPrice.stockSymbol(), tradeType, price, quantity, UUID.randomUUID().toString());
    }
}
