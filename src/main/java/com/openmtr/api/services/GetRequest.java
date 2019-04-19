package com.openmtr.api.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;

import org.glassfish.jersey.media.multipart.FormDataParam;

public class GetRequest extends ApiRequest {

	private String url = "";

	@Override
	@FormDataParam("email")
	public void setEmailAddress(String email) {
		this.email = email;
	}

	@Override
	@FormDataParam("numberOfDials")
	public void setDialsOnMeter(String dialsOnMeter) {
		if(dialsOnMeter == null || dialsOnMeter.length() == 0) {
			this.dialsOnMeter = "999999";
		}
		else {
			this.dialsOnMeter = dialsOnMeter;	
			this.doLoop = false;
		}

	}

	@FormDataParam("url")
	public void setUrl(@DefaultValue("") String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	protected boolean processImage() {
		if (this.error) {
			return false;
		}
		try {
			this.downloadImage(this.url);
			this.extractByteArray();
		} catch (FileNotFoundException ex) {
			this.setErrorMsg(ex.getMessage());
		} catch (IOException ex) {
			this.setErrorMsg(ex.getMessage());
		}

		return false;
	}

	public boolean validateRequest() {
		if (!this.isValidEmail()) {
			this.setErrorMsg("Email address is invalid");
		} else if (!this.isValidUrl()) {
			this.setErrorMsg("The URL address is invalid");
		}
		return this.isError();
	}

	protected void createImageFileName() {
		Date date = new Date();
		this.image = new File(this.getImageFolderLocation() + date.getTime() + ".jpg");
	}

	private void downloadImage(String url) throws FileNotFoundException {
		try {
			this.createImageFileName();
			String ext = this.determineFileType(new URL(url).openStream());
			System.out.println("File type: " + ext);
			if(ext.equalsIgnoreCase("png")) {
				this.convertPngToJpg(this.extractByteArray(new URL(url).openStream()), this.image);
			}
			else if(ext.equalsIgnoreCase("jpeg")) {
				Files.copy(new URL(url).openStream(), Paths.get(this.getImageFile().getPath()),
						StandardCopyOption.REPLACE_EXISTING);
				if (!this.getImageFile().exists()) {
					throw new FileNotFoundException("Could not download image from URL: " + url);
				}
				if (this.determineFileType(this.getImageFile().getPath()) == null) {
					this.getImageFile().delete();
					throw new IOException("File downloaded was not an image");
				}
			}
			else {
				throw new IOException("Image must be either png or jpg");
			}
		} catch (Exception ex) {
			throw new FileNotFoundException(ex.getMessage());
		}

	}

	public boolean isValidUrl() {
		try {
			Pattern reg = Pattern.compile(
					"^((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$");
			Matcher m = reg.matcher(this.url);
			return m.find();
		} catch (NullPointerException ex) {
			return false;
		}
		
	
	}

}
