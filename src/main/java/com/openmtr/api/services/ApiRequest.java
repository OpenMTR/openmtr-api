package com.openmtr.api.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;

public abstract class ApiRequest {

	protected String email = "";
	
	protected String dialsOnMeter =  "";
	
	protected byte[] imageByteArray;
	
	protected File image;
	
	protected boolean error = false;
	
	protected String error_msg = "";
	
	@Context 
	protected ServletContext servletContext;
	
	
	/* Implement in extended classes */
	public abstract void setEmailAddress(String email);
	
	public abstract void setDialsOnMeter(String dialsOnMeter);
	
	protected abstract boolean processImage();
	
	
	protected void setErrorMsg(String error_msg) {
		this.error = true;
		this.error_msg = error_msg;
	}
	
	public boolean isError() {
		return this.error;
	}
	
	public String getErrorMsg() {
		return this.error_msg;
	}
	
	
	public String getEmailAddress() {
		return this.email;
	}
	
	public boolean isValidEmail() {
		return this.validateEmailAdress(this.email);
	}
	
	
	public String getDialsOnMeter() {
		return this.dialsOnMeter;
	}
	
	public boolean isValidDialsOnMeter() {
		return this.validateDigitsOnMeterFace(this.dialsOnMeter);
	}
	
	public byte[] getImageByteArray() {
		return this.imageByteArray;
	}
	
	protected String getImageFolderLocation() {
		return this.servletContext.getRealPath("/") + "uploadedImages/";
	}
	
	protected void setImageFile(String file_name) {
		this.image = new File(this.getImageFolderLocation() + file_name);
	}
	
	protected File getImageFile() {
		return this.image;
	}
	
	
	
	protected boolean validateEmailAdress(String email) {
		Pattern reg = Pattern.compile(
				"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?){1,}$");
		Matcher m = reg.matcher(email);
		return m.find();
	}
	
	protected boolean validateDigitsOnMeterFace(String numberOfDials) {
		try {
		Pattern pat = Pattern.compile("^[9]{3,6}$");
		Matcher m = pat.matcher(numberOfDials);
		return m.find();
		} catch (NullPointerException ex) {
			return false;
		}
	}
	
	protected String determineFileType(String imagePath) {
		try {
			File file = new File(imagePath);
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			
			if(!iter.hasNext())
				return null;
			
			ImageReader reader = iter.next();
			String format = reader.getFormatName();
			iis.close();
			
			return format;
		} catch (Exception ex) {
			return null;
		}
	}
	protected String determineFileType(InputStream inputStream) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			
			if(!iter.hasNext())
				return null;
			
			ImageReader reader = iter.next();
			String format = reader.getFormatName();
			iis.close();
			
			return format;
		} catch (Exception ex) {
			return null;
		}
	}
	
	protected String getExtensionFromFiletype(String format) {
		switch(format.toLowerCase()) {
		case "png":
			return ".png";
		case "jpeg":
		default:
			return ".jpg";
		}
	}
	
	protected void extractByteArray() throws IOException {
    	try {
	    	BufferedImage bImage = ImageIO.read(this.image);
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(bImage, "jpg", bos );
	        this.imageByteArray = bos.toByteArray();
    	} catch(Exception ex) {
    		System.out.println("Could not extract byte[]. " + ex.getMessage());
    		throw new IOException("Could not extract image.");
    	}
	}
	
	protected byte[] extractByteArray(InputStream is) throws IOException {
		byte[] imgBytes = IOUtils.toByteArray(is);
		return imgBytes;
	}
	
}
