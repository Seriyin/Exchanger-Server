package app;

import proto.Trade;
import proto.Trade.TradeCompleted;
import proto.WrapperServer;
import proto.WrapperServer.WrapperMessageServer;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Description.
 */
public class TradeTask extends TimerTask
{
    private final List<TradeCompleted> trades;
    private final BlockingQueue<WrapperMessageServer> outgoingQueue;


    public TradeTask(List<TradeCompleted> trades,
                     BlockingQueue<WrapperMessageServer> outgoingQueue)
    {
        this.trades = trades;
        this.outgoingQueue = outgoingQueue;
    }


    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run()
    {
        for (TradeCompleted t : trades) {
            WrapperMessageServer.Builder message;
            message = WrapperMessageServer.newBuilder()
                                          .setIsTrade(true)
                                          .setTrade(t);
            outgoingQueue.add(message.build());
        }
    }
}
