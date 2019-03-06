package com.openmtr.api.services;

import java.io.IOException;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mattclinard.openmtr.OpenMeter;

@Path("/read_meter")
public class OpenMeterApi {
	
	private ReturnResponse rr = new ReturnResponse();


	@GET
	@Produces("application/json")
	public Response downloadFromUrl(@BeanParam GetRequest getRequest) {
		if(getRequest.validateRequest()) {
			return rr.error("Validation Error. " + getRequest.getErrorMsg());
		}
		else {
			getRequest.processImage();
			if(getRequest.isError()) {
				return rr.error("Error: " + getRequest.getErrorMsg());
			}
		}
		
		
		String meterRead = "";
		OpenMeter om = new OpenMeter();
		try {
			meterRead = om.getMeterRead(getRequest.getImageByteArray(), getRequest.getDialsOnMeter());
		} catch (IOException ex) {
			return rr.error("Could not Read Meter. ", 400);
		}
		
		rr.setData(meterRead);
		return rr.success();

		
	}
	
	@POST
	@Produces("application/json")
	@Consumes({"multipart/form-data", "application/x-www-form-urlencoded"})
	public Response uploadImage(@BeanParam PostRequest imageRequest) {
		if(imageRequest.validateImageRequest()) {
			return rr.error(imageRequest.getErrorMsg(), 400);
		}
		
		String meterRead = "";
		OpenMeter om = new OpenMeter();
		try {
			meterRead = om.getMeterRead(imageRequest.getImageByteArray(), imageRequest.getDialsOnMeter());
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), 400);
		}

		rr.setData(meterRead);
		return rr.success();
	}
	
}
