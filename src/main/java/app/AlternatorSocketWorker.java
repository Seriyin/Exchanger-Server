package app;

import com.google.protobuf.InvalidProtocolBufferException;
import org.zeromq.ZMQ;
import proto.WrapperServer.WrapperMessageServer;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A worker over the dealer which alternates between reading
 * from the incoming and outgoing queues.
 */
public class AlternatorSocketWorker implements Runnable
{
    private final BlockingQueue<WrapperMessageServer> incomingQueue;
    private final BlockingQueue<WrapperMessageServer> outgoingQueue;
    private final ZMQ.Socket exchanger;
    private final int backOffBase;
    private int backOffIncoming;
    private int backOffOutgoing;
    private int tries;

    public AlternatorSocketWorker(BlockingQueue<WrapperMessageServer> incomingQueue,
                                  BlockingQueue<WrapperMessageServer> outgoingQueue,
                                  ZMQ.Socket exchanger)
    {
        this.incomingQueue = incomingQueue;
        this.outgoingQueue = outgoingQueue;
        this.exchanger = exchanger;
        backOffBase = 500;
        backOffIncoming = backOffOutgoing = backOffBase;
        tries = 10;
    }


    /**
     * TODO AlternatorSocketWorker to work on exponential backoff + reset.
     *
     */
    @Override
    public void run()
    {
        try
        {
            exchanger.setReceiveTimeOut(backOffIncoming);
            ByteBuffer incoming = ByteBuffer.allocate(4096);
            WrapperMessageServer outgoing;
            while(true) {
                exchanger.recvByteBuffer(incoming,0);
                if(exchanger.recvByteBuffer(incoming,0)!=-1)
                {
                    //Should probably move parsing into
                    //main thread(s).
                    try
                    {
                        incomingQueue.add(WrapperMessageServer
                                                  .parseFrom(incoming.array()
                                                                     .clone()));
                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }
                }
                incoming.clear();
                if((outgoing = outgoingQueue.poll(backOffOutgoing, TimeUnit.MICROSECONDS))!=null) {
                    exchanger.send(outgoing.toByteArray());
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            WrapperMessageServer.Builder message = WrapperMessageServer.newBuilder();
            message.setIsOnline(true);
            Online.Builder builder = Online.newBuilder();
            builder.setName(Server.getEXCHANGE());
            builder.setStatus(404);
            message.setOnline(builder);
            exchanger.add(message.build().toByteArray());
        }
    }

            /*
        int tryI=0, tryO=0;

        while(true) {
            if(tryI == tries)
            {
                tryI = 0;
                backOffIncoming = backOffBase;
                exchanger.setReceiveTimeOut(backOffIncoming);
            }
            else {
                tryO = 0;
                backOffOutgoing = backOffBase;
            }
            while (tryI < tries && tryO < tries)
            {
                if(exchanger.recvByteBuffer(incoming,0)==-1) {
                    tryI++;
                    backOffIncoming *= 1.5;
                }
                else {

                }

            }

        }
        */
}
