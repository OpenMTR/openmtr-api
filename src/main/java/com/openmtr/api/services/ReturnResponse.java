package com.openmtr.api.services;


import java.time.Duration;
import java.util.Date;

import javax.ws.rs.core.Response;


public class ReturnResponse {


    private boolean error = false;
    private String error_msg = "";
    private String data = null;
    private int status_code = 400;
    private Date startProcessing = new Date();
	private Date stopProcessing;
    private String totalProcessingTime = "";


    /**
     * Will return a JSON response with the error message given
     * @param message The message to return
     * @param statusCode The Status code to return
     * @return Response
     */
    public Response error(String message, Integer statusCode ) {
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
    	return Response
                .status(this.status_code)
                .entity("{" +
                        "\"error\" : \"" + this.error + "\", " +
                        "\"error_msg\" : \"" + this.error_msg + "\", " +
                        "\"processing_time\" : " + this.totalProcessingTime + " " +
                        "}"
                )
                .build();
    }
    
    public Response success() {
    	this.stopProcessing();
    	return Response
    			.status(200)
    			.entity("{" +
                "\"error\" : \"" + this.error + "\", " +
                "\"error_msg\" : \"" + this.error_msg + "\", " +
                "\"data\" : " + this.data + ", " +
                "\"processing_time\" : " + this.totalProcessingTime + " " +
                "}")
    			.build();
    }
    
    public void setErrorMsg(String message) {
    	this.error_msg = message;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }
    
    
    public String getStartTime() {
    	return this.startProcessing.toString();
    }
    
    public String getStopTime() {
    	return this.stopProcessing.toString();
    }
    
	
	private void stopProcessing() {
		this.stopProcessing = new Date();
		Duration totalProcessing = Duration.between(this.startProcessing.toInstant(), this.stopProcessing.toInstant());
		this.totalProcessingTime = "{\"hours\" : \"" + totalProcessing.toHours() + "\", \"minutes\" : \"" + totalProcessing.toMinutes() + "\", \"seconds\" : \"" + totalProcessing.getSeconds() + "\", \"nanoseconds\" : \"" + totalProcessing.toNanos() + "\"}";
	}

}
