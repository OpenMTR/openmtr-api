package com.openmtr.api.services;


import javax.ws.rs.core.Response;


public class ReturnResponse {


    private boolean error = false;
    private String error_msg = "";
    private String data = null;


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
                        "\"error_msg\" : \"" + this.error_msg + "\", " +
                        "\"Environment_Variables\" : {" +
                        	"\"TENSORFLOW_PREFIX\" : \"" + System.getenv("TENSORFLOW_PREFIX") + "\", " +
                        	"\"TESSDATA_PREFIX\" : \"" + System.getenv("TESSDATA_PREFIX") + "\"} " +
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
                "\"data\" : " + this.data + ", " +
                "\"Environment_Variables\" : {" +
	            	"\"TENSORFLOW_PREFIX\" : \"" + System.getenv("TENSORFLOW_PREFIX") + "\", " +
	            	"\"TESSDATA_PREFIX\" : \"" + System.getenv("TESSDATA_PREFIX") + "\"} " +
                "}")
    			.build();
    }

}
