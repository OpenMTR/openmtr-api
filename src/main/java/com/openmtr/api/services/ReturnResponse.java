package com.openmtr.api.services;


import javax.ws.rs.core.Response;


public class ReturnResponse {


    private boolean error = false;
    private String error_msg = "";
    private String data = null;
    private int status_code = 400;


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
    	return Response
                .status(this.status_code)
                .entity("{" +
                        "\"error\" : \"" + this.error + "\", " +
                        "\"error_msg\" : \"" + this.error_msg + "\" " +
                        "}"
                )
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
    
    public Response success() {
    	return Response
    			.status(200)
    			.entity("{" +
                "\"error\" : \"" + this.error + "\", " +
                "\"error_msg\" : \"" + this.error_msg + "\", " +
                "\"data\" : " + this.data + " " +
                "}")
    			.build();
    }

}
