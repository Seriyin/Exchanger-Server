package model;

import app.Server;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import proto.Trade;

import java.util.*;

/**
 * A company stores current unresolved transaction queues for buying and selling and peak transactions.
 * TODO Tree structure ordered by price -> Match highest to lowest.
 */
public class Company
{
    private final String name;
    private final NavigableMap<Double, Order> buyOrders;
    private final NavigableMap<Double, Order> sellOrders;
    private final Peak highPeak;
    private final Peak lowPeak;

    @JsonCreator
    public Company(@JsonProperty("name") String name)
    {
        this.name = name;
        Comparator<Double> c = Comparator.naturalOrder();
        this.buyOrders = new TreeMap<>(c.reversed());
        this.sellOrders = new TreeMap<>();
        highPeak = new Peak(0,0);
        lowPeak = new Peak(0,0);
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }


    int checkPeak(double pps,
                  long time)
    {
        int r=0;
        if (highPeak.checkHigh(pps))
        {
            highPeak.setPrice(pps);
            highPeak.setTime(time);
            Server.sendPeak(pps, time, name, true);
        }
        else if (lowPeak.checkLow(pps))
        {
            lowPeak.setPrice(pps);
            lowPeak.setTime(time);
            r = -1;
            Server.sendPeak(pps, time, name, false);
        }
        return r;
    }



    public void registerIncomingBuy(Order b)
    {
        Order match;
        Trade.TradeCompleted[] trades;
        trades = new Trade.TradeCompleted[2];
        if((match = buyOrders.floorEntry(b.getPrice())
                              .getValue())!=null)
        {
            matchLoop(match, trades, b);
        }
        else {
            buyOrders.put(b.getPrice(),b);
        }
    }

    public void registerIncomingSell(Order b)
    {
        Order match;
        Trade.TradeCompleted[] trades;
        trades = new Trade.TradeCompleted[2];
        while((match = sellOrders.floorEntry(b.getPrice())
                              .getValue())!=null)
        {
            matchLoop(match, trades, b);
        }
        if(b.getPrice()>=0) {
            sellOrders.put(b.getPrice(),b);
        }
    }

    private void matchLoop(Order match, Trade.TradeCompleted[] trades, Order b)
    {
        switch (match.matchOrders(b)) {
            case -1:
                trades[0] = match.trashOrder(b)
                                 .setCompany(name)
                                 .setExchange(Server.getEXCHANGE())
                                 .build();
                trades[1] = null;
                Server.sendTrades(trades);
                break;
            case 0:
                int i=0;
                for (Trade.TradeCompleted.Builder t :
                        match.trashOrders(b))
                {
                    trades[i] = t.setCompany(name)
                                 .setExchange(Server.getEXCHANGE())
                                 .build();
                    i++;
                }
                Server.sendTrades(trades);
                break;
            case 1:
                trades[0] = match.trashOrder(b)
                                 .setCompany(name)
                                 .setExchange(Server.getEXCHANGE())
                                 .build();
                trades[1] = null;
                Server.sendTrades(trades);
                break;
            default:
                break;
        }
    }

}
