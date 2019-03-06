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

import javax.ws.rs.QueryParam;

public class GetRequest extends ApiRequest {
	
	
	private String url = "";

	@Override
	@QueryParam("email")
	public void setEmailAddress(String email) {
		this.email = email;
	}

	@Override
	@QueryParam("numberOfDials")
	public void setDialsOnMeter(String dialsOnMeter) {
		this.dialsOnMeter = dialsOnMeter;
		
	}
	
	@QueryParam("url")
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	@Override
	protected boolean processImage() {
		if(this.error) {
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
		if(!this.isValidEmail()) {
			this.setErrorMsg("Email address is invalid");
		}
		else if (!this.isValidDialsOnMeter()) {
			this.setErrorMsg("The dials on meter is invalid");
		}
		else if (!this.isValidUrl()) {
			this.setErrorMsg("The URL address is invalid");
		}
		return this.isError();
	}
	
	
	protected void createImageFileName(String imageExtension) {
		Date date = new Date();
		this.image = new File(this.getImageFolderLocation() + date.getTime() + imageExtension);
	}
	

	
	private void downloadImage(String url) throws FileNotFoundException {
		try {
			this.createImageFileName(this.getExtensionFromFiletype(this.determineFileType(new URL(url).openStream())));
			Files.copy(new URL(url).openStream(), Paths.get(this.getImageFile().getPath()), StandardCopyOption.REPLACE_EXISTING);		
			if(!this.getImageFile().exists()) {
				throw new FileNotFoundException("Could not download image from URL: " + url);
			}
			if(this.determineFileType(this.getImageFile().getPath()) == null) {
				this.getImageFile().delete();
				throw new IOException("File downloaded was not an image");
			}
		} catch(Exception ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
		
	}

	public boolean isValidUrl() {
		Pattern reg = Pattern.compile("^((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$");
		Matcher m = reg.matcher(this.url);
		return m.find();
	}

	
	

}
