package model;

import app.Server;
import proto.Trade;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Order POJO for trading.
 * <p>
 * Also holds current total,
 * and total traded so far.
 */
public class Order
{
    private int quantity;
    private double price;
    private double total;
    private int accumulated;
    private String user;
    private Company c;

    public Order(Trade.TradeOrder t, Company c) {
        quantity = t.getQuant();
        price = t.getPrice();
        user = t.getUser();
        total = 0;
        accumulated = 0;
        this.c = c;
    }

    public Order(int quantity, double price, String user, Company c)
    {
        this.quantity = quantity;
        this.price = price;
        this.user = user;
        total = 0;
        accumulated = 0;
        this.c = c;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public double getPrice()
    {
        return price;
    }

    public String getUser()
    {
        return user;
    }

    public double getTotal()
    {
        return total;
    }

    public int getAccumulated()
    {
        return accumulated;
    }


    /**
     * Match two orders.
     * <p>
     * Since a quantity must be matched in entirety,
     * either one is fulfilled, the other one is or
     * both.
     * @param b Order to match.
     * @return 0 if both fulfill,
     *         1 if supplied Order fulfills,
     *         -1 if this Order fulfills.
     */
    public int matchOrders(Order b) {
        return Integer.compare(b.getQuantity(),getQuantity());
    }


    /**
     * Trash order.
     * Check if a trade is traded at a peak.
     * @param matched Order matched.
     * @return An incomplete builder with trade details.
     */
    public Trade.TradeCompleted.Builder trashOrder(Order matched)
    {
        Trade.TradeCompleted.Builder t;
        t = Trade.TradeCompleted.newBuilder();
        double pps;
        pps = (price+matched.getPrice())/2;
        price = -1;
        long timestamp;
        t.setQuant(accumulated + matched.getQuantity())
         .setUser(user)
         .setTotal(total + (pps * matched.getQuantity()));
        timestamp = System.currentTimeMillis();
        c.checkPeak(pps, timestamp);
        return t.setTimestamp(timestamp);
    }



    /**
     * Update remaining quantity.
     * <p>
     * Use on order not completely fulfilled.
     * @param matched Order matched
     */
    public void updateOrder(Order matched)
    {
        accumulated += matched.getQuantity();
        total = total + (matched.getQuantity() * (price + matched.getPrice()/2));
        quantity -= matched.quantity;
    }

    public Trade.TradeCompleted.Builder[] trashOrders(Order matched)
    {
        double pps;
        long timestamp;
        Trade.TradeCompleted.Builder t[];
        t = new Trade.TradeCompleted.Builder[2];
        t[0] = Trade.TradeCompleted.newBuilder();
        pps = (price + matched.price)/2;
        price = -1;
        matched.price = -1;
        t[0].setQuant(accumulated + quantity)
            .setUser(user)
            .setTotal(total + (pps * quantity));
        t[1].setQuant(matched.accumulated + quantity)
            .setUser(user)
            .setTotal(matched.total + (pps * quantity));
        timestamp = System.currentTimeMillis();
        c.checkPeak(pps, timestamp);
        t[0].setTimestamp(timestamp);
        t[1].setTimestamp(timestamp);
        return t;
    }
}
