package model;

import app.OnlineTask;
import app.Server;
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
    private InetSocketAddress at;
    private String name;
    private Map<String,Company> companies;

    @JsonCreator
    public Exchange(@JsonProperty("at") InetSocketAddress at,
                    @JsonProperty("name") String name,
                    @JsonProperty("companies") Map<String, Company> companies)
    {
        this.at = at;
        this.name = name;
        this.companies = companies;
    }

    @JsonProperty
    public InetSocketAddress getAt()
    {
        return at;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public Map<String, Company> getCompanies()
    {
        return companies;
    }


}
