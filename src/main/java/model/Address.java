package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Address is a simple POJO for an Exchange network hostname + port.
 */
public class Address
{
    private String host;
    private int port;
    private String name;

    @JsonCreator
    public Address(@JsonProperty("host") String host,
                   @JsonProperty("port") int port,
                   @JsonProperty("name") String name)
    {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    @JsonProperty
    public String getHost()
    {
        return host;
    }

    @JsonProperty
    public int getPort()
    {
        return port;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }
}
