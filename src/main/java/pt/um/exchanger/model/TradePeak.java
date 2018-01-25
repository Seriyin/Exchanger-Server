package pt.um.exchanger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;

/**
 * A TradePeak JSONable for HTTP PUT.
 */
public class TradePeak
{
    @Key
    private double pps;

    @Key
    private long time;

    @Key
    private String name;

    @Key
    private String exchange;

    public TradePeak() {}


    public TradePeak(double pps,
                     long time,
                     String name,
                     String exchange)
    {

        this.pps = pps;
        this.time = time;
        this.name = name;
        this.exchange = exchange;
    }

    public double getPps()
    {
        return pps;
    }

    public long getTime()
    {
        return time;
    }

    public String getName()
    {
        return name;
    }

    public String getExchange()
    {
        return exchange;
    }
}
