package com.openmtr.api.services;

//import java.awt.Image;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import java.awt.image.WritableRaster;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.Iterator;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.stream.ImageInputStream;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.mattclinard.openmtr.OpenMeter;

@Path("/read_meter")
public class OpenMeterApi {
	
	private ReturnResponse rr = new ReturnResponse();
	
	
//	public OpenMeterApi() {
//		
//	}
	
	

//	@GET
//	@Produces("application/json")
//	public Response downloadFromUrl(
//			@QueryParam("url") String url, 
//			@QueryParam("numberOfDigits") String digits
//			) {
//		//Init the Return Response Class
//		ReturnResponse rr = new ReturnResponse();
//		
//		try {
//			String testVar = System.getenv("TEST");
//		//Check to see if a URL was provided
//		if(url.isEmpty())
//			return rr.error("URL is missing (Environment Variable = " + testVar + ")", 400);
//		} catch(NullPointerException ex) {
//			return rr.error("URL is missing", 400);
//		}
//		
//		//Make sure the URL is a valid URL
//		if(!this.validateURL(url))
//        	return rr.error("The given url is invalid. Please provide a format of http(s)://domain.com/image.extension", 400);
//		
//		if(!this.validateDigitsOnMeterFace(digits))	
//			return rr.error("Error", 400);
//		
//		//Download the image from the URL
//		String imagePath = null;
//		try {
//			imagePath = this.downloadImage(url);
//		} catch (Exception ex) {
//			return rr.error(ex.getMessage(), this.statusCode);
//		}
//		
//		//Extract the byte[] from the downloaded image
//		byte[] image = null;
//		try {
//			image = this.extractByteArray(imagePath);
//		} catch (Exception ex) {
//			return rr.error(ex.getMessage(), this.statusCode);
//		}
//		
//		//Test the byte[] against Matt C's library
//		String meterRead = "";
//		OpenMeter om = new OpenMeter();
//		try {
//			meterRead = om.getMeterRead(image, "9999");
//		} catch (IOException ex) {
//			return rr.error("Could not Read Meter. ", 400);
//		} catch (NullPointerException ex) {
//			return rr.error("Problem with AI, fix comming", 500);
//		}
//		
//		
//		//All good, return the ok
//		rr.setData(meterRead);
//		return rr.success();
//
//		
//	}
	
	@POST
	@Produces("application/json")
	@Consumes({"multipart/form-data", "application/x-www-form-urlencoded"})
	public Response uploadImage(@BeanParam ImageRequest imageRequest) {
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

	
	/**
	 * Download the image from the given URL
	 * @param String url
	 * @return String Image File Location
	 * @throws Exception
	 */
//	private String downloadImage(String url) throws Exception{
//		this.saveImageFolder = this.servletContext.getRealPath("/") + "uploadedImages/";
//		//Need to make sure the Images folder exists
//		File dir = new File(this.saveImageFolder);
//		if(!dir.exists()) {
//			this.statusCode = 500;
//			throw new Exception("Image Folder doesn't exist");
//		}
//		
//		//Get the file name from the url
//		String fileName = url.split("/")[url.split("/").length-1];
//		
//		
//		//Do the actual downloading of the image
//		try {
//			InputStream in = new URL(url).openStream();
//			Files.copy(in, Paths.get(this.saveImageFolder + fileName), StandardCopyOption.REPLACE_EXISTING);
//		} catch(Exception ex) {
//			this.statusCode = 400;
//			throw new Exception("Could not download image from URL: " + url);
//		}
//		
//		//Make sure the file was downloaded
//		File imageFile = new File(this.saveImageFolder + fileName);
//		if(!imageFile.exists()) {
//			this.statusCode = 404;
//			throw new Exception("Image was not downloaded.");
//		}
//		
//		//Make sure the downloaded file is a image and has the correct file type extension
//		String imagePath;
//		try {
//			imagePath = this.determineFileType(this.saveImageFolder + fileName);
//		} catch (Exception ex) {
//			this.statusCode = 400;
//			throw new Exception(ex.getMessage());
//		}
//		
//		
//		
//		
//		//return the image location
//		return imagePath;
//		
//	}
	
}
