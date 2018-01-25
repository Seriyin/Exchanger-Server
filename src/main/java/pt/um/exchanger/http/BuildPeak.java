package pt.um.exchanger.http;

import pt.um.exchanger.app.Server;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.json.JsonHttpContent;
import pt.um.exchanger.model.TradePeak;

import java.io.IOException;
import java.util.TimerTask;

/**
 * BuildPeak makes a trade peak PUT request to send to directory.
 */
public class BuildPeak extends TimerTask
{
    private TradePeak tradePeak;

    public BuildPeak(TradePeak tradePeak)
    {
        this.tradePeak = tradePeak;
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
                    new GenericUrl(String.format("http://localhost:8080/api/exchanges/%s/%s", tradePeak.getExchange(), tradePeak.getName()));
            HttpRequestFactory rf = Server.HTTP_TRANSPORT.createRequestFactory();
            rf.buildPutRequest(url, new JsonHttpContent(Server.JSON_FACTORY, tradePeak)).executeAsync();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
