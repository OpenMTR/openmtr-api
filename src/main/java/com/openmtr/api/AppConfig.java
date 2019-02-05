package com.openmtr.api;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
	//Tell Jersey to offer these services in package com.openmtr.api.services
	public AppConfig() {
		register(MultiPartFeature.class);
		packages("com.openmtr.api.services");
		
	}
}
