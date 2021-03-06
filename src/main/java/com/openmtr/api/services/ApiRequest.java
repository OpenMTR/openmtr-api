package com.openmtr.api.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;
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

	protected String dialsOnMeter = "";

	protected byte[] imageByteArray;

	protected File image;
	
	public boolean doLoop = true;

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

	protected void setImageFile() {
		Date d = new Date();
		this.image = new File(this.getImageFolderLocation() + d.getTime() + ".jpg");
	}

	protected File getImageFile() {
		return this.image;
	}

	protected boolean validateEmailAdress(String email) {
		try {
			Pattern reg = Pattern.compile(
					"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?){1,}$");
			Matcher m = reg.matcher(email);
			return m.find();
		} catch (NullPointerException ex) {
			return false;
		}
	}

	protected boolean validateDigitsOnMeterFace(String numberOfDials) {
		try {
			Pattern pat = Pattern.compile("^[9]{4,6}$");
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

			if (!iter.hasNext())
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

			if (!iter.hasNext())
				return null;

			ImageReader reader = iter.next();
			String format = reader.getFormatName();
			iis.close();

			return format;
		} catch (Exception ex) {
			return null;
		}
	}

	protected String determinFileType(byte[] byte_array) throws IOException {
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(byte_array));
		String mimeType = URLConnection.guessContentTypeFromStream(is);
		return mimeType;
	}

	protected String getExtensionFromFiletype(String format) {
		switch (format.toLowerCase()) {
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
			ImageIO.write(bImage, "jpg", bos);
			this.imageByteArray = bos.toByteArray();
		} catch (Exception ex) {
			throw new IOException("Could not extract image.");
		}
	}

	protected byte[] extractByteArray(InputStream is) throws IOException {
		byte[] imgBytes = IOUtils.toByteArray(is);
		return imgBytes;
	}
	
	protected boolean convertPngToJpg(byte[] pngFile, File jpgFile) {
		try {
			InputStream in = new ByteArrayInputStream(pngFile);
			BufferedImage bufferedImage = ImageIO.read(in);
			
			BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
					bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			  newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
			  
		  ImageIO.write(newBufferedImage, "jpg", jpgFile);
		  this.extractByteArray();
		} catch (IOException e) {
			System.out.println("Could not convert PNG to JPEG. " + e.getMessage());
			return false;
		}
		return true;
	}

}
