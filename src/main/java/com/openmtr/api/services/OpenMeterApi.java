package com.openmtr.api.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/read_meter")
public class OpenMeterApi {

	
	@GET
	@Produces("application/json")
	public Response downloadFromUrl(@QueryParam("url") String url) {
		return Response.ok().entity("{\"Response\" : \"GET Ok\"}").build();
	}
	
	@POST
	@Produces("application/json")
	public Response putImage() {
		return Response.ok().entity("{\"Response\" : \"POST Ok\"}").build();
	}
	
}
