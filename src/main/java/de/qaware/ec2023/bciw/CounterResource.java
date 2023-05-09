package de.qaware.ec2023.bciw;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.UUID;

@Path("/counter")
@Produces(MediaType.APPLICATION_JSON)
public class CounterResource {
    private int counter = 0;


    /*
     * SSE implementation
     */
    private OutboundSseEvent.Builder eventBuilder;
    private SseBroadcaster sseBroadcaster;


    public CounterResource(@Context final Sse sse) {
        this.eventBuilder = sse.newEventBuilder();
        this.sseBroadcaster = sse.newBroadcaster();
    }

    @GET
    public CounterValue currentCounter() {
        return new CounterValue(counter);
    }

    ;

    @POST
    @Path("/increment")
    public void increment() {
        counter++;
        notifySubscriptions();
    }

    @POST
    @Path("/decrement")
    public void decrement() {
        counter--;
        notifySubscriptions();
    }


    private void notifySubscriptions() {
        notifySse();
    }

    /*
     * SSE implementation
     */
    @GET
    @Path("/subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink sseEventSink) throws InterruptedException {
        this.sseBroadcaster.register(sseEventSink);
        //initial update
        sseEventSink.send(createSseCounterUpdateEvent());
    }

    private void notifySse() {
        OutboundSseEvent sseEvent = createSseCounterUpdateEvent();
        this.sseBroadcaster.broadcast(sseEvent);
    }

    private OutboundSseEvent createSseCounterUpdateEvent() {
        OutboundSseEvent sseEvent = this.eventBuilder
                .name("counter")
                .id(UUID.randomUUID().toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(CounterValue.class, new CounterValue(counter))
                .reconnectDelay(4000)
                .comment("counter changed")
                .build();
        return sseEvent;
    }
}
