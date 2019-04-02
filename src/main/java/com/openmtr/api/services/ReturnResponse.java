package com.openmtr.api.services;

import java.time.Duration;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

public class ReturnResponse {

	private final String version = "1.0.0";
	private boolean error = false;
	private String error_msg = "";
	private int status_code = 400;

	private Date startProcessing = new Date();
	private Date stopProcessing;
	private String totalProcessingTime = "";

	private String meter_read = "";
	private String open_meter_version = "";
	private String ai_used = "";
	private String meter_type = "";
	private String digitsOnMeter = "";

	/**
	 * Will return a JSON response with the error message given
	 * 
	 * @param message    The message to return
	 * @param statusCode The Status code to return
	 * @return Response
	 */
	public Response error(String message, Integer statusCode) {
		this.error = true;
		this.error_msg = message;
		this.status_code = statusCode;

		return this.error();

	}

	public Response error(String message) {
		this.error = true;
		this.error_msg = message;
		return this.error();
	}

	public Response error() {
		this.error = true;
		this.stopProcessing();
		return Response.status(this.status_code)
				.entity("{" + "\"error\" : " + this.error + ", " + "\"error_msg\" : \"" + this.error_msg + "\", "
						+ "\"api_version\" : \"" + this.version + "\", " + "\"processing_time\" : "
						+ this.totalProcessingTime + " " + "}")
				.build();
	}

	public Response success() {
		this.stopProcessing();
		return Response.status(200)
				.entity("{" + "\"error\" : " + this.error + ", " + "\"error_msg\" : \"" + this.error_msg + "\", "
						+ "\"api_version\" : \"" + this.version + "\", " + "\"meter_read\" : {" + "\"read\" : \""
						+ this.meter_read + "\", " + "\"type\" : \"" + this.meter_type + "\", " + "\"version\" : \""
						+ this.open_meter_version + "\", " + "\"ai_used\" : \"" + this.ai_used + "\", \"dials_on_meter\" : \"" 
						+ this.digitsOnMeter + "\"" + "}, "
						+ "\"processing_time\" : " + this.totalProcessingTime + " " + "}")
				.build();
	}

	public void setErrorMsg(String message) {
		this.error_msg = message;
		this.error = true;
	}

	public String getStartTime() {
		return this.startProcessing.toString();
	}

	public boolean isError() {
		return this.error;
	}

	public void setMeterRead(String read) {
		this.meter_read = read;
	}

	public void setOpenMeterVersion(String version) {
		this.open_meter_version = version;
	}

	public void setAiUsed(String ai) {
		this.ai_used = ai;
	}

	public void setMeterType(String meter_type) {
		this.meter_type = meter_type;
	}

	public void setDigitsOnMeter(String digits) {
		this.digitsOnMeter = digits;
	}

	public void setOpenMeterResponse(String meterResponse) {
		JSONObject jo = new JSONObject(meterResponse);

		// Sometimes the MeterRead is not a string so check first
		String meterRead = (!jo.isNull("meterRead")) ? jo.getString("meterRead") : "";

		if (jo.getString("readMethodUsed").trim().equalsIgnoreCase("failed")) {
			this.setErrorMsg("Could not get a valid meter read. " + meterRead);
		} else if (!jo.isNull("error")) {
			this.setErrorMsg("Something happened. Please try again later. ");
			this.status_code = 500;
		} else {
			this.setMeterRead(meterRead);
			this.setAiUsed(jo.getString("readMethodUsed"));
			this.setOpenMeterVersion(jo.getString("buildVersion"));
			this.setMeterType(jo.getString("meterType"));
		}
	}

	public String getStopTime() {
		return this.stopProcessing.toString();
	}

	private void stopProcessing() {
		this.stopProcessing = new Date();
		Duration totalProcessing = Duration.between(this.startProcessing.toInstant(), this.stopProcessing.toInstant());
		this.totalProcessingTime = "{\"hours\" : \"" + totalProcessing.toHours() + "\", \"minutes\" : \""
				+ totalProcessing.toMinutes() + "\", \"seconds\" : \"" + totalProcessing.getSeconds()
				+ "\", \"milliseconds\" : \"" + totalProcessing.toMillis() + "\"}";
	}

}
