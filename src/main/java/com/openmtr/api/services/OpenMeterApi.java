package com.openmtr.api.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;

import com.mattclinard.openmtr.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/read_meter")
public class OpenMeterApi {
	private String saveImageFolder;
	private int statusCode;

	/**
	 * Holds the Email Address to record in the Database
	 */
	private String emailAddress;

	/**
	 * Holds the Number of digits on Meter Face for the Meter Read
	 */
	private String numberOfDigits;

	private ReturnResponse rr = new ReturnResponse();

	@Context
	ServletContext servletContext;

	public OpenMeterApi() {

	}

	@GET
	@Produces("application/json")
	public Response downloadFromUrl(@QueryParam("url") String url) {
		
		this.validateURL(url);

		if (rr.error)
			return rr.error();

		// Download the image from the URL and extract byte[]
		String imagePath = null;
		byte[] image = null;
		try {
			imagePath = this.downloadImage(url);
			image = this.extractByteArray(imagePath);
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), this.statusCode);
		}

		// Test the byte[] against Matt C's library
		String meterRead = "";
		OpenMeter om = new OpenMeter();
		try {
			meterRead = om.getMeterRead(image, this.numberOfDigits);
		} catch (IOException ex) {
			return rr.error("Could not Read Meter. ", 400);
		} catch (NullPointerException ex) {
			return rr.error("Problem with AI, fix comming", 500);
		}

		// All good, return the ok
		rr.setData(meterRead);
		return rr.success();

	}

	@QueryParam("email")
	@FormDataParam("email")
	/**
	 * Set the email address
	 * 
	 * @param email
	 */
	public void setEmailAddress(String email) {
		try {
			if (email.isEmpty())
				rr.setErrorMessage("Email address is required");
		} catch (NullPointerException ex) {
			rr.setErrorMessage("The parameter email is missing");
		}

		this.emailAddress = email;
	}

	@POST
	@Produces("application/json")
	@Consumes({ "multipart/form-data", "application/x-www-form-urlencoded" })
	public Response uploadImage(@FormDataParam("file") InputStream inputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		if (rr.error)
			return rr.error();

		// Check for empty file
		try {
			if (fileDetail.getFileName().isEmpty())
				return rr.error("No file was uploaded", 400);
		} catch (Exception ex) {
			return rr.error("No file was uploaded", 400);
		}

		// The file location
		String imageLocation = servletContext.getRealPath("/") + "uploadedImages/" + fileDetail.getFileName();
		byte[] imageBytes = null;

		// save the file to the imageLocation
		try {
			this.saveImage(inputStream, imageLocation);
			imageLocation = this.determineFileType(imageLocation);
			imageBytes = this.extractByteArray(imageLocation);
		} catch (Exception ex) {
			return rr.error(ex.getMessage(), this.statusCode);
		}

		// test the image against Matt's Library
		String meterRead = "";
		OpenMeter om = new OpenMeter();
		try {
			meterRead = om.getMeterRead(imageBytes, this.numberOfDigits);
		} catch (IOException ex) {
			return rr.error("Could not Read Meter", 400);
		} catch (NullPointerException ex) {
			return rr.error("Problem with AI, fix coming", 500);
		}

		// Set the data
		rr.setData("POST SUCCESS " + meterRead);

		// return the ok
		return rr.success();
	}

	/**
	 * Validate a URL
	 * 
	 * @param String url
	 * @return boolean
	 */
	private void validateURL(String url) {
		try {
			// Must start with http(s):// for a valid URL
			Pattern urlReg = Pattern.compile(
					"^((http[s]?|ftp):\\/\\/){1,1}\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$");
			Matcher m = urlReg.matcher(url);
			if (!m.find())
				rr.setErrorMessage(
						"The given url is invalid. Please provide a format of http(s)://domain.com/image.extension");
		} catch (NullPointerException ex) {
			rr.setErrorMessage("The required url parameter is missing");
		}
	}

	@QueryParam("numberOfDigits")
	@FormDataParam("numberOfDigits")
	/**
	 * Will check to make sure the number of digits supplied is valid for a Meter
	 * Face
	 * 
	 * @param String numberOfDigits
	 * @return String
	 */
	private void validateDigitsOnMeterFace(String numberOfDigits) {
		try {
			if (numberOfDigits.isEmpty())
				rr.setErrorMessage("The parameter numberOfDigits is empty");
			// Check to make sure that the numberOfDigits is in the range of 3 to 6 digits
			if (numberOfDigits.length() < 3 || numberOfDigits.length() > 6)
				rr.setErrorMessage("The number of digits allowed on the Meter Face is between 3 and 6 digits.");

			this.numberOfDigits = numberOfDigits;
		} catch (NullPointerException ex) {
			rr.setErrorMessage("Parameter numberOfDigits not provided.");
		}

	}

	/**
	 * Will validate an email address
	 * 
	 * @param String email
	 * @return boolean
	 */
	private boolean validateEmailAdress(String email) {
		Pattern reg = Pattern.compile(
				"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?){1,}$");
		Matcher m = reg.matcher(email);
		return (m.find()) ? true : false;
	}

	/**
	 * Download the image from the given URL
	 * 
	 * @param String url
	 * @return String Image File Location
	 * @throws Exception
	 */
	private String downloadImage(String url) throws Exception {
		this.saveImageFolder = this.servletContext.getRealPath("/") + "uploadedImages/";
		// Need to make sure the Images folder exists
		File dir = new File(this.saveImageFolder);
		if (!dir.exists()) {
			this.statusCode = 500;
			throw new Exception("Image Folder doesn't exist");
		}

		// Get the file name from the url
		String fileName = url.split("/")[url.split("/").length - 1];

		// Do the actual downloading of the image
		try {
			InputStream in = new URL(url).openStream();
			Files.copy(in, Paths.get(this.saveImageFolder + fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception ex) {
			this.statusCode = 400;
			throw new Exception("Could not download image from URL: " + url);
		}

		// Make sure the file was downloaded
		File imageFile = new File(this.saveImageFolder + fileName);
		if (!imageFile.exists()) {
			this.statusCode = 404;
			throw new Exception("Image was not downloaded.");
		}

		// Make sure the downloaded file is a image and has the correct file type
		// extension
		String imagePath;
		try {
			imagePath = this.determineFileType(this.saveImageFolder + fileName);
		} catch (Exception ex) {
			this.statusCode = 400;
			throw new Exception(ex.getMessage());
		}

		// return the image location
		return imagePath;

	}

	/**
	 * Will extract a byte array to test against Matt C OpenMeter
	 * 
	 * @param String imagePath
	 * @return byte[]
	 * @throws Exception
	 */
	private byte[] extractByteArray(String imagePath) throws Exception {
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
			throw new Exception("Could not read image " + ex.getMessage());
		}

		return data.getData();
	}

	/**
	 * Will test to see if the given file path is a image or not
	 * 
	 * @param String imagePath
	 * @return boolean
	 */
	private boolean testIfImage(String imagePath) {
		try {
			Image image = ImageIO.read(new File(imagePath));
			if (image == null)
				return false;
		} catch (Exception ex) {
			return false;
		}

		return true;
	}

	/**
	 * Will save the uploaded file to the given path
	 * 
	 * @param InputStream inputStream
	 * @param String      imagePath
	 * @throws Exception Thanks to
	 *                   https://www.mkyong.com/webservices/jax-rs/file-upload-example-in-jersey/
	 */
	private void saveImage(InputStream inputStream, String imagePath) throws Exception {
		try {
			// create the stream
			OutputStream out = new FileOutputStream(new File(imagePath));
			int read = 0;
			// grab 1024 bytes at a time
			byte[] bytes = new byte[1024];
			// loop through the image stream in 1024 byte chunks
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			// flush out the buffer and close the file
			out.flush();
			out.close();
		} catch (IOException ex) {
			this.statusCode = 500;
			throw new Exception("Could not save file");
		}

		// check to make sure the file exists
		File image = new File(imagePath);
		if (!image.exists()) {
			this.statusCode = 404;
			throw new Exception("The image could not be found");
		}
	}

	/**
	 * Will determine what type of format the image is, and if no extension is given
	 * to the file name, one will be appended
	 * 
	 * @param String imagePath
	 * @return String
	 * @throws Exception
	 */
	private String determineFileType(String imagePath) throws Exception {

		// Quick check to see if the image is an image
		if (!this.testIfImage(imagePath))
			throw new Exception("File is not an image");

		// create the File Class
		File file = new File(imagePath);

		// create the stream
		ImageInputStream iis = ImageIO.createImageInputStream(file);

		// get all the iterators
		Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

		if (!iter.hasNext())
			throw new Exception("Could not determine image format");

		// get the first reader
		ImageReader reader = iter.next();

		// Get the image format
		String format = reader.getFormatName();

		// Close the Stream
		iis.close();

		// check for the extension on the image
		Pattern extension = Pattern.compile("(?i)^[\\w\\/\\\\\\.\\-]*\\.(jpg|png)$");
		Matcher m = extension.matcher(imagePath);

		// Check to see what type of format the image is and if there is no extension on
		// the file, then add the correct extension
		switch (format) {
		case "JPEG":
		case "jpeg":
			if (!m.find()) {
				imagePath += ".jpg";
				if (!this.renameImage(file, imagePath)) {
					System.out.println("Could not rename the file");
				}
			}
			break;
		case "PNG":
		case "png":
			file.delete();
			throw new Exception("Only JPEG images are supported at this time.");
		default:
			file.delete();
			throw new Exception(
					"Could not determine the image format. Only .jpg and .png format allowed. Format given: " + format);
		}

		return imagePath;

	}

	/**
	 * Will rename a file to the given string
	 * 
	 * @param File   file
	 * @param String imagePathAndName
	 * @return boolean
	 */
	private boolean renameImage(File file, String imagePathAndName) {
		File changeTo = new File(imagePathAndName);
		if (file.renameTo(changeTo))
			return true;
		else
			return false;
	}
}
