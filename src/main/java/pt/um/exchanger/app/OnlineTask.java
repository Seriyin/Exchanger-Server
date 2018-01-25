package pt.um.exchanger.app;

import com.google.protobuf.MessageLite;
import org.zeromq.ZMQ;
import pt.um.exchanger.proto.WrapperServer.WrapperMessageServer;
import pt.um.exchanger.proto.Heartbeat.Online;

import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * OnlineTask executes on a timer to alert connected exchange
 * server of availability.
 */
public class OnlineTask extends TimerTask
{
    private final String name;
    private final BlockingQueue<WrapperMessageServer> outgoing;

    public OnlineTask(String name,
                      BlockingQueue<WrapperMessageServer> outgoingQueue)
    {
        this.name = name;
        outgoing = outgoingQueue;
    }

    public void run() {
        WrapperMessageServer.Builder message = WrapperMessageServer.newBuilder();
        message.setIsOnline(true);
        Online.Builder builder = Online.newBuilder();
        builder.setName(name);
        builder.setStatus(200);
        message.setOnline(builder);
        outgoing.add(message.build());
    }

}
