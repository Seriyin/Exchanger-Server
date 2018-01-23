package app;

import proto.Addresses;
import proto.Addresses.Address;
import proto.WrapperServer.WrapperMessageServer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * AddressTask executes on the same timer as OnlineTask to alert
 * connected exchange server of exchange address.
 * <p>
 * Should run very infrequently, exchanger should cache address.
 */
public class AddressTask extends TimerTask
{
    private final String name;
    private final BlockingQueue<WrapperMessageServer> outgoing;

    public AddressTask(String name,
                       BlockingQueue<WrapperMessageServer> outgoingQueue)
    {
        this.name = name;
        outgoing = outgoingQueue;
    }

    public void run() {
        WrapperMessageServer.Builder message = WrapperMessageServer.newBuilder();
        message.setIsAddress(true);
        Address.Builder builder = Address.newBuilder();
        builder.setHost("localhost");
        builder.setPort(Server.PORT);
        builder.setName(name);
        builder.setType(Addresses.type.EXCHANGE);
        message.setAddress(builder);
        outgoing.add(message.build());
    }
}
