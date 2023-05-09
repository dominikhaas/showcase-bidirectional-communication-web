package de.qaware.ec2023.bciw;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
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


    /*
     * SSE implementation
     */
    private final OutboundSseEvent.Builder eventBuilder;
    private final SseBroadcaster sseBroadcaster;


    @Inject
    CounterService counterService;

    public CounterResource(@Context final Sse sse) {
        this.eventBuilder = sse.newEventBuilder();
        this.sseBroadcaster = sse.newBroadcaster();
    }

    @PostConstruct
    public void postConstruct() {
        //register as a listener and update SSE subscriptions
        this.counterService.addListener(this::notifySubscriptions);
    }


    @GET
    public CounterValue currentCounter() {
        return new CounterValue(counterService.getCounter());
    }


    @POST
    @Path("/increment")
    public void increment() {
        counterService.increment();
    }

    @POST
    @Path("/decrement")
    public void decrement() {
        counterService.decrement();
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
    public void subscribe(@Context SseEventSink sseEventSink) {
        this.sseBroadcaster.register(sseEventSink);
        //initial update
        sseEventSink.send(createSseCounterUpdateEvent());
    }

    private void notifySse() {
        OutboundSseEvent sseEvent = createSseCounterUpdateEvent();
        this.sseBroadcaster.broadcast(sseEvent);
    }

    private OutboundSseEvent createSseCounterUpdateEvent() {
        return this.eventBuilder
                .name("counter")
                .id(UUID.randomUUID().toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(CounterValue.class, new CounterValue(this.counterService.getCounter()))
                .reconnectDelay(4000)
                .comment("counter changed")
                .build();
    }
}
