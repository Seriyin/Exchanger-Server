package pt.um.exchanger.model;

import com.google.api.client.util.Key;
import pt.um.exchanger.app.Server;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.apache.xpath.internal.operations.Or;
import pt.um.exchanger.proto.Trade;

import java.util.*;

/**
 * A company stores current unresolved transaction queues for buying and selling and peak transactions.
 * TODO Tree structure ordered by price -> Match highest to lowest.
 */
public class Company
{
    @Key
    private String name;

    private NavigableSet<Order> buyOrders;
    private NavigableSet<Order> sellOrders;
    private Peak highPeak;
    private Peak lowPeak;

    public String getName()
    {
        return name;
    }


    public void initialize() {
        Comparator<Order> c = Comparator.comparingDouble(Order::getTotal).thenComparing(Order::getQuantity);
        this.buyOrders = new TreeSet<>(c.reversed());
        this.sellOrders = new TreeSet<>();
        highPeak = new Peak(0,0);
        lowPeak = new Peak(0,0);
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
        List<Trade.TradeCompleted> trades;
        trades = new ArrayList<>(4);
        while((match = buyOrders.floor(b))!=null)
        {
            matchLoop(match, trades, b, buyOrders);
        }
        if(b.getPrice()>=0)
        {
            buyOrders.add(b);
        }
        Server.sendTrades(trades);
    }

    public void registerIncomingSell(Order b)
    {
        Order match;
        List<Trade.TradeCompleted> trades;
        trades = new ArrayList<>(4);
        while((match = sellOrders.floor(b))!=null)
        {
            matchLoop(match, trades, b, sellOrders);
        }
        if(b.getPrice()>=0) {
            sellOrders.add(b);
        }
        Server.sendTrades(trades);
    }

    private void matchLoop(Order match, List<Trade.TradeCompleted> trades, Order b, NavigableSet<Order> orders)
    {
        switch (match.matchOrders(b)) {
            case -1:
                trades.add(match.trashOrder(b)
                                 .setCompany(name)
                                 .setExchange(Server.getEXCHANGE())
                                 .build());
                orders.remove(match);
                break;
            case 0:
                for (Trade.TradeCompleted.Builder t :
                        match.trashOrders(b))
                {
                    trades.add(t.setCompany(name)
                                .setExchange(Server.getEXCHANGE())
                                .build());
                }
                orders.remove(match);
                break;
            case 1:
                trades.add(match.trashOrder(b)
                                 .setCompany(name)
                                 .setExchange(Server.getEXCHANGE())
                                 .build());
                break;
            default:
                break;
        }
    }

}
