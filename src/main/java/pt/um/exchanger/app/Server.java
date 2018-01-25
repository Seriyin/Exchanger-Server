package pt.um.exchanger.app;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import pt.um.exchanger.http.BuildLive;
import pt.um.exchanger.http.BuildPeak;
import pt.um.exchanger.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import pt.um.exchanger.proto.Trade;
import pt.um.exchanger.proto.WrapperServer.WrapperMessageServer;

/**
 * Exchange server main class, sets up connections and serves HTTP requests.
 */
public class Server
{
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY;
    public static final Timer TIMER;
    private static int PORT;
    public static final BlockingQueue<WrapperMessageServer> outgoingQueue;
    public static final BlockingQueue<WrapperMessageServer> incomingQueue;
    private static String EXCHANGE;
    private final ZMQ.Context context;
    private final Socket exchanger;
    private final Exchange exchange;
    private final Thread t;

    static {
        JSON_FACTORY = new JacksonFactory();
        TIMER = new Timer(true);
        outgoingQueue = new ArrayBlockingQueue<>(500);
        incomingQueue = new ArrayBlockingQueue<>(500);
    }

    public static String getEXCHANGE()
    {
        return EXCHANGE;
    }


    public static int getPORT() {
        return PORT;
    }

    public Server(String arg) throws IOException
    {
        exchange = JSON_FACTORY.fromInputStream(ClassLoader.getSystemResourceAsStream(  arg + ".json"), Exchange.class);
        exchange.getCompanies().values().forEach(c -> c.initialize());
        context = ZMQ.context(1);
        exchanger = context.socket(ZMQ.DEALER);
        PORT = exchanger.bindToRandomPort("tcp://localhost",10002,45000);
        exchange.setPort(PORT);
        exchange.setHost("localhost");
        exchanger.connect("tcp://localhost:10000");
        t = setAlternatorTaskOngoing();
        EXCHANGE = exchange.getName();
    }

    /**
     * Called with arg[0] set to either [asiaeast, asiawest,
     * dawjobes, psi-3].
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException, InterruptedException
    {
        if(args.length >= 1)
        {
            Server s = new Server(args[0]);
            try
            {
                s.run();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                s.cancel();
            }
        }
    }

    private void cancel()
    {
        try
        {
            if (t.isAlive())
            {
                t.interrupt();
                t.join();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally {
            buildDeadRequest();
        }
    }

    private void buildDeadRequest()
    {
        TIMER.schedule(new BuildLive(new Address("clean exit", 0, EXCHANGE)), 0);
    }


    private void run() throws InterruptedException
    {
        setStatusUpdating();
        buildLiveRequest();
        while(true) {
            WrapperMessageServer incoming = incomingQueue.take();
            if(incoming.hasAddressRequest()) {
                respondWithAddress();
            }
            else if(incoming.hasTradeRequest()) {
                registerTrade(incoming.getTradeRequest());
            }
        }
    }

    private void buildLiveRequest()
    {
        TIMER.schedule(new BuildLive(new Address("localhost", PORT, EXCHANGE)), 0);
    }

    private void registerTrade(Trade.TradeOrder trade)
    {
        if (exchange.getCompanies().containsKey(trade.getCompany())) {
            Company c = exchange.getCompanies().get(trade.getCompany());
            Order o = new Order(trade, c);
            if(trade.getBuyOrSell()) {
                c.registerIncomingBuy(o);
            }
            else {
                c.registerIncomingSell(o);
            }
        }
    }

    private Thread setAlternatorTaskOngoing()
    {
        return new Thread(new AlternatorSocketWorker(incomingQueue, outgoingQueue, exchanger));
    }


    private void setStatusUpdating()
    {
        Server.TIMER.scheduleAtFixedRate(
                new OnlineTask(exchange.getName(),
                               outgoingQueue),
                0,
                5000);
    }

    private void respondWithAddress()
    {
        Server.TIMER.schedule(new AddressTask(exchange.getName(),
                                              outgoingQueue),
                              0);
    }



    public static void sendPeak(double pps, long time, String name, boolean b)
    {
        Server.TIMER.schedule(new BuildPeak(new TradePeak(pps, time, name, EXCHANGE)),0);
    }



    public static void sendTrades(List<Trade.TradeCompleted> trades)
    {
        Server.TIMER.schedule(new TradeTask(trades, outgoingQueue),0);
    }
}
