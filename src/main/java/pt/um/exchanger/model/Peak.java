package pt.um.exchanger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;

/**
 * A POJO based on order, JSONable.
 */
public class Peak
{
    @Key
    private double price;

    @Key
    private long time;

    public Peak(){}

    public Peak(double price,
                long time)
    {
        this.price = price;
        this.time = time;
    }

    public double getPrice()
    {
        return price;
    }

    public long getTime()
    {
        return time;
    }

    public boolean checkHigh(double price) {
        return price > getPrice();
    }

    public boolean checkLow(double price)
    {
        return price < getPrice();
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setTime(long time)
    {
        this.time = time;
    }
}
