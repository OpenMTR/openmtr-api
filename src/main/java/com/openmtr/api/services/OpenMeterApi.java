package com.openmtr.api.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;

import com.mattclinard.openmtr.*;

@Path("/read_meter")
public class OpenMeterApi {
	private String saveImageFolder;
	private int statusCode;
	
	@Context
	ServletContext servletContext;
	
	public OpenMeterApi() {
		
	}
	
	
	
	@GET
	@Produces("application/json")
	public Response downloadFromUrl(@QueryParam("url") String url) {
		//Init the Return Response Class
		ReturnResponse rr = new ReturnResponse();
		
		try {
		//Check to see if a URL was provided
		if(url.isEmpty())
			return rr.error("URL is missing", 400);
		} catch(NullPointerException ex) {
			return rr.error("URL is missing", 400);
		}
		
		//Make sure the URL is a valid URL
		if(!this.validateURL(url))
        	return rr.error("The given url is invalid. Please provide a format of http(s)://domain.com/image.extension", 400);
		
		//Download the image from the URL
		String imagePath = null;
		try {
			imagePath = this.downloadImage(url);
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), this.statusCode);
		}
		
		//Extract the byte[] from the downloaded image
		byte[] image = null;
		try {
			image = this.extractByteArray(imagePath);
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), this.statusCode);
		}
		
		//Test the byte[] against Matt C's library
		String meterRead = "";
		try {
			meterRead = OpenMeter.getMeterRead(image);
		} catch (Exception ex) {
			return rr.error("Could not get Meter Read", 400);
		}
		
		//All good, return the ok
		rr.setData(meterRead);
		return rr.success();

		
	}
	
	@POST
	@Produces("application/json")
	public Response putImage() {
		return Response.ok().entity("{\"Response\" : \"POST Ok\"}").build();
	}
	
	/**
	 * Validate a URL
	 * @param String url
	 * @return boolean
	 */
	private boolean validateURL(String url) {
		//Must start with http(s):// for a valid URL
        Pattern urlReg = Pattern.compile("^((http[s]?|ftp):\\/\\/){1,1}\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$");
        Matcher m = urlReg.matcher(url);
        if(!m.find())
        	return false;
        return true;
	}
	
	/**
	 * Download the image from the given URL
	 * @param String url
	 * @return String Image File Location
	 * @throws Exception
	 */
	private String downloadImage(String url) throws Exception{
		this.saveImageFolder = this.servletContext.getRealPath("/") + "uploadedImages/";
		//Need to make sure the Images folder exists
		File dir = new File(this.saveImageFolder);
		if(!dir.exists()) {
			this.statusCode = 500;
			throw new Exception("Image Folder doesn't exist");
		}
		
		//Get the file name from the url
		String fileName = url.split("/")[url.split("/").length-1];
		
		//Do the actual downloading of the image
		try {
			InputStream in = new URL(url).openStream();
			Files.copy(in, Paths.get(this.saveImageFolder + fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch(Exception ex) {
			this.statusCode = 400;
			throw new Exception("Could not download image from URL: " + url);
		}
		
		//Make sure the file was downloaded
		File imageFile = new File(this.saveImageFolder + fileName);
		if(!imageFile.exists()) {
			this.statusCode = 404;
			throw new Exception("Image was not downloaded.");
		}
		
		//Make sure the downloaded file is a image
		if(!this.testIfImage(this.saveImageFolder + fileName)) {
			this.statusCode = 400;
			throw new Exception("The given URL was not an Image");
		}
		
		//return the image location
		return this.saveImageFolder + fileName;
		
	}

	/**
	 * Will extract a byte array to test against Matt C OpenMeter
	 * @param String imagePath
	 * @return byte[]
	 * @throws Exception
	 */
	private byte[] extractByteArray(String imagePath) throws Exception{
		File image = null;
    	BufferedImage bufferedImage = null;
    	WritableRaster raster = null;
    	DataBufferByte data = null;
    	try {
	    	image = new File(imagePath);
	    	bufferedImage = ImageIO.read(image);
	    	
	    	raster = bufferedImage.getRaster();
	    	data = (DataBufferByte) raster.getDataBuffer();
    	} catch (Exception ex) {
    		this.statusCode = 400;
    		throw new Exception(ex.getMessage());
    	}
    	
    	return data.getData();
	}

	/**
	 * Will test to see if the given file path is a image or not
	 * @param String imagePath
	 * @return boolean
	 */
	private boolean testIfImage(String imagePath) {
		try {
			Image image = ImageIO.read(new File(imagePath));
			if(image == null) 
				return false;
		} catch (Exception ex) {
			return false;
		}
		
		return true;
	}
}
