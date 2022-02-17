package com.hari.Test;
import java.io.IOException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
@Path("fetchdomain")
public class FetchDOMAIN {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt() throws IOException {
    	return String.valueOf(Poller.domainv);
    }
}
