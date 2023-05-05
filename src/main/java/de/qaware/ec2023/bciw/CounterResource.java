package de.qaware.ec2023.bciw;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/counter")
@Produces(MediaType.APPLICATION_JSON)
public class CounterResource {
    private int counter = 0;

    @GET
    public CounterValue currentCounter() {
        return new CounterValue(counter);
    };
}
