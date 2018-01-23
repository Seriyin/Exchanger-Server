package http;

import app.Server;
import com.google.api.client.http.HttpRequestFactory;
import model.TradePeak;

import java.io.IOException;
import java.util.TimerTask;

/**
 * BuildPeak makes a peak trade to send to directory.
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
            HttpRequestFactory rf = Server.HTTP_TRANSPORT.createRequestFactory()
            Server.JSON_FACTORY.toString(tradePeak);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
