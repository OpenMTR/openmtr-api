package com.openmtr.api.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
			this.dialsOnMeter = "99999";
		}
		else {
			this.dialsOnMeter = numberOfDials;	
		}
	}

	@FormDataParam("file")
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
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
		this.setImageFile(fileDetail.getFileName());
	}
	
	public boolean validateImageRequest() {
		if(!this.isValidEmail()) {
			this.setErrorMsg("Email address is invalid");
		} 
		else if(!this.isValidDialsOnMeter()) {
			this.setErrorMsg("Number of dials on meter face is invalid");
		}
		else if(!this.validFile()) {
			this.setErrorMsg("File is empty");
		}
		try {
			this.extractByteArray();
		} catch (IOException ex) {
			this.setErrorMsg("Could not extract image");
		}
		return this.error;
	}
	
	
	private boolean validFile() {
		if(this.fileDetail == null || this.fileDetail.getFileName().length() == 0) {
			return false;
		}
		else if(!this.processImage()) {
			return false;
		}
		return true;
	}
	
	protected boolean processImage() {
		try {
			OutputStream out = new FileOutputStream(this.image);
			int read = 0;
			
			byte[] bytes = new byte[1024];
			
			while((read = this.inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			if(!this.image.exists()) {
				this.setErrorMsg("Could not save Image");
				return false;
			}
		} catch (IOException ex) {
			this.setErrorMsg("Could not save file");
			return false;
		}
		return true;
	}
	
	
	

}
