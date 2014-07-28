package com.citytechinc.aem.bedrock.api.services;

import java.util.List;

/**
 * OSGi service provider.
 */
public interface ServiceProvider {

    /**
     * Get an OSGi service.
     *
     * @param serviceType the type (class) of the service
     * @param <T> type
     * @return the service instance, or null if it is not available
     */
    <T> T getService(Class<T> serviceType);

    /**
     * Get one or more OSGi services.
     *
     * @param serviceType the type (class) of the service
     * @param filter optional filter, see OSGi spec for syntax details
     * @param <T> type
     * @return one or more service instances, or null if none are found
     */
    <T> List<T> getServices(Class<T> serviceType, String filter);
}
