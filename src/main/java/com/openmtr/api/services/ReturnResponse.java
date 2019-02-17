package com.openmtr.api.services;


import javax.ws.rs.core.Response;


public class ReturnResponse {

    boolean error;
    String error_msg;
    int status_code = 400;
    String data;


    public ReturnResponse() {
    	this.error_msg = "";
    	this.data = null;
    	this.error = false;
    }

    /**
     * Will return a JSON response with the error message given
     * @param message The message to return
     * @param statusCode The Status code to return
     * @return Response
     */
    public Response error(String message, Integer statusCode ) {
        this.status_code = statusCode;
        this.error = true;
        this.error_msg = message;

        return this.error();

    }
    
    /**
     * Will return a error Response
     * @return Response
     */
    public Response error() {
    	return Response
                .status(this.status_code)
                .entity("{" +
                        "\"error\" : \"" + this.error + "\", " +
                        "\"error_msg\" : \"" + this.error_msg + "\" " +
                        "}"
                )
                .build();
    }
    
    public void setErrorMessage(String errorMsg) {
    	this.error_msg = errorMsg;
    	this.error = true;
    }
    
    public void setStatusCode(int statusCode) {
    	this.status_code = statusCode;
    }


    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }
    
    public Response success() {
    	return Response
    			.ok()
    			.entity("{" +
                "\"error\" : \"" + this.error + "\", " +
                "\"error_msg\" : \"" + this.error_msg + "\", " +
                "\"data\" : \"" + this.data + "\" " +
                "}")
    			.build();
    }

}
