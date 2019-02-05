package com.openmtr.api.services;


import javax.ws.rs.core.Response;


public class ReturnResponse {

    boolean error;
    String error_msg;
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
        if(statusCode == null) {
            statusCode = 400;
        }
        this.error = true;
        this.error_msg = message;

        return Response
                .status(statusCode)
                .entity("{" +
                        "\"error\" : \"" + this.error + "\", " +
                        "\"error_msg\" : \"" + this.error_msg + "\" " +
                        "}"
                )
                .build();

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
                "\"data\" : \"" + this.data + "\" " +
                "}")
    			.build();
    }

}
