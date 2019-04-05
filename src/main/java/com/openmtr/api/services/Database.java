package com.openmtr.api.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static final String CONNECTION_STRING = System.getenv("JDBC_STRING");
	private Connection connection = null;
	
	public String imgUrl = "";
	public String fileName = "";
	public String emailAddress = "";
	public String buildVersion = "";
	public String meterRead = "";
	public String meterType = "";
	public String readMethod = "";
	public String processingTime = "";
	public int errorCode = 0;
	public String errorMessage = "";
	public boolean success = false;
	public String createdBy = "";
	public String dialsOnMeter = "";
	public boolean looped = false;

	
	public void setConnection() throws ClassNotFoundException, SQLException {
		if (CONNECTION_STRING != null) {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			this.connection = DriverManager.getConnection(CONNECTION_STRING);
		}
		else {
			throw new SQLException("No Connection String Found");
		}
	}
	
	public void execute() {
		String query = "INSERT INTO Usage (ImgUrl, FileName, EmailAddress, BuildVersion, MeterRead, MeterType, ReadMethod, ProcessingTime, "
				+ " ErrorCode, ErrorMessage, Success, CreatedBy, DialsOnMeter, Looped) " 
				+ " VALUES ('" + this.imgUrl + "', '" + this.fileName + "', '" + this.emailAddress + "', '" + this.buildVersion + "', '" + this.meterRead + "', '" + this.meterType + "', "
				+ " '" + this.readMethod + "', '" + this.processingTime + "', '" + this.errorCode + "', '" + this.errorMessage + "', '" + this.success + "', '" + this.createdBy + "', "
				+ " '" + this.dialsOnMeter + "', '" + this.looped + "')";
		try {
			this.setConnection();
			Statement stm = this.connection.createStatement();
			stm.executeUpdate(query);
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}
	
	public Connection getConnection() {
		return this.connection;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		this.createdBy = emailAddress;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getMeterRead() {
		return meterRead;
	}

	public void setMeterRead(String meterRead) {
		this.meterRead = meterRead;
	}

	public String getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(String readMethod) {
		this.readMethod = readMethod;
	}

	public String getMeterType() {
		return meterType;
	}

	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}

	public String getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(String processingTime) {
		this.processingTime = processingTime;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDialsOnMeter() {
		return dialsOnMeter;
	}

	public void setDialsOnMeter(String dialsOnMeter) {
		this.dialsOnMeter = dialsOnMeter;
	}

	public boolean isLooped() {
		return looped;
	}

	public void setLooped(boolean looped) {
		this.looped = looped;
	}
	
	
	
	
}
