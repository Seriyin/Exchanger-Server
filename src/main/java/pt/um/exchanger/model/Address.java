package pt.um.exchanger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;

/**
 * Address is a simple POJO for an Exchange network hostname + port.
 */
public class Address
{
    @Key
    private String host;

    @Key
    private int port;

    @Key
    private String name;

    public Address() {}

    public Address(String host,
                   int port,
                   String name)
    {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getName()
    {
        return name;
    }
}
