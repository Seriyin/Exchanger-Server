package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A TradePeak JSONable for HTTP PUT.
 */
public class TradePeak
{
    private final double pps;
    private final long time;
    private final String name;
    private final String exchange;

    @JsonCreator
    public TradePeak(@JsonProperty("pps") double pps,
                     @JsonProperty("time") long time,
                     @JsonProperty("name") String name,
                     @JsonProperty("exchange") String exchange)
    {

        this.pps = pps;
        this.time = time;
        this.name = name;
        this.exchange = exchange;
    }

    @JsonProperty
    public double getPps()
    {
        return pps;
    }

    @JsonProperty
    public long getTime()
    {
        return time;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public String getExchange()
    {
        return exchange;
    }
}
