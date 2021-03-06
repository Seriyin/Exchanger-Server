package pt.um.exchanger.http;

import pt.um.exchanger.app.Server;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.json.JsonHttpContent;
import pt.um.exchanger.model.Address;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Build Live status report with PUT method.
 */
public class BuildLive extends TimerTask
{
    private final Address localhost;

    public BuildLive(Address localhost)
    {
        this.localhost = localhost;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run()
    {
        try
        {
            GenericUrl url =
                    new GenericUrl("http://localhost:8080/api/exchanges");
            HttpRequestFactory rf = Server.HTTP_TRANSPORT.createRequestFactory();
            rf.buildPutRequest(url, new JsonHttpContent(Server.JSON_FACTORY, localhost)).executeAsync();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
