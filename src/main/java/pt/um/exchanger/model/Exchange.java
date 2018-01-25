package pt.um.exchanger.model;

import com.google.api.client.util.Key;
import pt.um.exchanger.app.OnlineTask;
import pt.um.exchanger.app.Server;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TimerTask;

/**
 * Exchange contains company information, it's name and whether
 * it is online and on what address it is reachable.
 */
public class Exchange
{
    @Key
    private String host;

    @Key
    private int port;

    @Key
    private String name;

    @Key
    private Map<String,Company> companies;


    public InetSocketAddress getAt()
    {
        return new InetSocketAddress(host, port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName()
    {
        return name;
    }

    public Map<String, Company> getCompanies()
    {
        return companies;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
