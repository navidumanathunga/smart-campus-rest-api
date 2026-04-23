package com.smartcampus.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // Scan for resources and providers under this package
        packages("com.smartcampus.resources", "com.smartcampus.exceptions.mappers", "com.smartcampus.filters");
    }
}
