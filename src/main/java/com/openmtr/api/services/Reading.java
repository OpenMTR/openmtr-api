package com.openmtr.api.services;

public class Reading {

		/* database layout:
		 * int (auto increment)
		 * nchar (255) url NOT NULL
		 * date yyyy-mm-dd uploadDate NOT NULL
		 * nchar (10) buildVersion NOT NULL
		 * nchar (10) read NOT NULL
		 * nchar (25) readMethod NOT NULL
		 * nchar (25) meterType
		 * totalProcessingTime nchar (15) NOT NULL
		 * nchar (50) errorCode
		 * bit success NOT NULL (DEFAULT 0)
		 * createdBy Nchar(30) NOT NULL
		 * createdDate Date NOT NULL
		 * updatedBy nchar(30) NULL
		 * updatedDate Date NULL
		 */
			private String url;
			private String date;
			private String buildVersion;
			private String read;
			private String readMethod;
			private String meterType;
			private String totalProcessingTime;
			private String errorCode;
			private boolean success;
			private String createdBy;
			private String createdDate;
			private String updatedBy;
			private String updatedDate;
			
			
			/*
			 * default/no argument constructor
			 */
			public Reading(){
			}
			
			
			/*
			 * constructor with all values required
			 * @params url, date, buildVersion, read, readMethod totalProcessingTime, success boolean, createdBy, createdDate (yyyy-mm-dd)
			 * renders meterType, errorCode, updatedBy, and updatedDate null
			 * this will be the most commonly used constructor
			 */
			public Reading(String url, String date, String buildVersion, String read, String readMethod, String totalProcessingTime, boolean success, String createdBy, String createdDate){
				this.url = url;
				this.date = date;
				this.buildVersion = buildVersion;
				this.read = read;
				this.readMethod = readMethod;
				this.totalProcessingTime = totalProcessingTime;
				this.success = success;
				this.createdBy = createdBy;
				this.createdDate = createdDate;
				this.meterType = null;
				this.errorCode = null;
				this.updatedBy = null;
				this.updatedDate = null;
			}
			
			/*
			 * constructor including a meter type
			 * @params url, date, buildVersion, read, readMethod totalProcessingTime, success boolean, createdBy, createdDate (yyyy-mm-dd)
			 * renders errorCode, updatedBy, and updatedDate null
			 */
			public Reading(String url, String date, String buildVersion, String read, String readMethod, String totalProcessingTime, boolean success, String createdBy, String createdDate, String meterType){
				this.url = url;
				this.date = date;
				this.buildVersion = buildVersion;
				this.read = read;
				this.readMethod = readMethod;
				this.totalProcessingTime = totalProcessingTime;
				this.success = success;
				this.createdBy = createdBy;
				this.createdDate = createdDate;
				this.meterType = meterType;
				this.errorCode = null;
				this.updatedBy = null;
				this.updatedDate = null;
			}
			
			/*
			 * constructor including a meterType and errorCode
			 * @params url, date, buildVersion, read, readMethod totalProcessingTime, success boolean, createdBy, createdDate (yyyy-mm-dd), meterType, errorCode
			 * renders updatedBy, and updatedDate null
			 */
			public Reading(String url, String date, String buildVersion, String read, String readMethod, String totalProcessingTime, boolean success, String createdBy, String createdDate, String meterType, String errorCode ){
				this.url = url;
				this.date = date;
				this.buildVersion = buildVersion;
				this.read = read;
				this.readMethod = readMethod;
				this.totalProcessingTime = totalProcessingTime;
				this.success = success;
				this.createdBy = createdBy;
				this.createdDate = createdDate;
				this.meterType = meterType;
				this.errorCode = errorCode;
				this.updatedBy = null;
				this.updatedDate = null;
			}
			
		
				
			
			
			
			
			
			
		
			
			
			

}
