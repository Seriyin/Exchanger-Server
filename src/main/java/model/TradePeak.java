package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A TradePeak JSONable for HTTP PUT.
 */
public class TradePeak
{
    @JsonCreator
    public TradePeak(double pps, long time, String name, String exchange)
    {

    }
}
