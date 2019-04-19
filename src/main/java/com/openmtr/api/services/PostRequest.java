package com.openmtr.api.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class PostRequest extends ApiRequest{
	
	private InputStream inputStream;
	
	private FormDataContentDisposition fileDetail;	

	@FormDataParam("email")
	public void setEmailAddress(String email) {
		if(email == null || email.length() == 0) {
			this.email = "";
		}
		else {
			this.email = email;
		}
	}
	
	@FormDataParam("numberOfDials")
	public void setDialsOnMeter(String numberOfDials) {
		if(numberOfDials == null || numberOfDials.length() == 0) {
			this.dialsOnMeter = "999999";
		}
		else {
			this.dialsOnMeter = numberOfDials;	
			this.doLoop = false;
		}
	}

	@FormDataParam("file")
	public void setByteArrayFromInputStream(InputStream inputStream) {
		
		try {
			this.imageByteArray = this.extractByteArray(inputStream);
		} catch (IOException e) {
			this.setErrorMsg(e.getMessage());
		} catch (NullPointerException ex) {
			this.setErrorMsg("No File was uploaded");
		}
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public FormDataContentDisposition getFileDetail() {
		return fileDetail;
	}

	@FormDataParam("file")
	public void setFileDetail(FormDataContentDisposition fileDetail) {
		this.fileDetail = fileDetail;
	}
	
	public boolean validateImageRequest() {
		if(this.isError()) {
			return this.error;
		}
		if(!this.isValidEmail()) {
			this.setErrorMsg("Email address is invalid");
		} 
		return this.error;
	}
	
	protected boolean processImage() {
		try {
			this.setImageFile();
			String ext = this.determinFileType(getImageByteArray());
			System.out.println("Extension is: " + ext);
			if( ext.equalsIgnoreCase("image/png")) {
				this.convertPngToJpg(getImageByteArray(), this.image);
			}
			else if( ext.equalsIgnoreCase("image/jpeg")) {
				FileOutputStream fos = new FileOutputStream(this.image);
				fos.write(getImageByteArray());
				fos.close();
			}
			else {
				this.setErrorMsg("Image type must be jpg.");
				return false;
			}
		} catch (IOException ex) {
			this.setErrorMsg("Could not save file");
			return false;
		} catch (NullPointerException ex) {
			this.setErrorMsg("No file was given");
			return false;
		}
		return true;
	}
	
	
	

}
