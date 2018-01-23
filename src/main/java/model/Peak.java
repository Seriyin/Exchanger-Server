package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A POJO based on order, JSONable.
 */
public class Peak
{
    private double price;
    private long time;

    @JsonCreator
    public Peak(@JsonProperty("price") double price,
                @JsonProperty("time") long time)
    {
        this.price = price;
        this.time = time;
    }

    @JsonProperty
    public double getPrice()
    {
        return price;
    }

    @JsonProperty
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

    @JsonProperty
    public void setPrice(double price)
    {
        this.price = price;
    }

    @JsonProperty
    public void setTime(long time)
    {
        this.time = time;
    }
}
