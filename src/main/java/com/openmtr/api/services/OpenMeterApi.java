package com.openmtr.api.services;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.kub.openmtr.OpenMeter;

@Path("/read_meter")
public class OpenMeterApi {
	
	private ReturnResponse rr = new ReturnResponse();


	@Path("/url")
	@POST
	@Produces("application/json")
	@Consumes({"multipart/form-data", "application/x-www-form-urlencoded"})
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
		
		try {
			String meterResponse = this.readMeter(getRequest.getImageByteArray(), getRequest.getDialsOnMeter(), getRequest.doLoop);			
			rr.setOpenMeterResponse(meterResponse);
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), 400);
		}
		
		if(rr.isError()) {
			return rr.error();
		}
		
		return rr.success();

		
	}
	
	@POST
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response uploadImage(@BeanParam PostRequest imageRequest) {
		if(imageRequest.validateImageRequest()) {
			return rr.error(imageRequest.getErrorMsg(), 400);
		}
		imageRequest.processImage();
		if(imageRequest.isError()) {
			return rr.error(imageRequest.getErrorMsg());
		}
		
		try {
			String meterResponse = this.readMeter(imageRequest.getImageByteArray(), imageRequest.getDialsOnMeter(), imageRequest.doLoop);			
			rr.setOpenMeterResponse(meterResponse);
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), 400);
		}
		
		if(rr.isError()) {
			return rr.error();
		}
		
		return rr.success();
	}
	
	private String readMeter(byte[] byteArray, String dialsOnMeter, boolean doLoop) throws IOException {
		String meterResponse = "";
		String dials = dialsOnMeter;
		OpenMeter om = new OpenMeter();
		Pattern pat = Pattern.compile("^[0-9]{3,}$");
		int min = 5;
		if(doLoop) {
			min = 3;
		}
		for(int i = 6; i > min; i--) {
			meterResponse = om.getMeterRead(byteArray, dials);
			JSONObject jo = new JSONObject(meterResponse);
			
			if(!jo.isNull("error")) {
				break;
			}
	
			String meterRead = (!jo.isNull("meterRead")) ? jo.getString("meterRead") : "";
			Matcher m = pat.matcher(meterRead.trim());
			if(m.matches()) {
				rr.setDigitsOnMeter(dials);
				break;
			}
			else {
				dials = dials.substring(0, dials.length() - 1);
			}
		}
		return meterResponse;
	
	}
	
}
