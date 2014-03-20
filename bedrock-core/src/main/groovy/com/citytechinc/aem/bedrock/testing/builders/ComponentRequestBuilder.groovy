/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.builders

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.content.request.impl.DefaultComponentServletRequest
import com.citytechinc.aem.bedrock.testing.mocks.MockComponentRequest
import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.day.cq.wcm.api.WCMMode
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.api.scripting.SlingScriptHelper

class ComponentRequestBuilder {

    @Delegate RequestBuilder requestBuilder

    @Delegate ResponseBuilder responseBuilder = new ResponseBuilder()

    def mode

    def services = [:]

    def servicesWithFilters = [:]

    ComponentRequestBuilder(ResourceResolver resourceResolver) {
        requestBuilder = new RequestBuilder(resourceResolver)
    }

    /**
     * Set the WCMMode for this request.
     *
     * @param mode WCMMode value
     */
    void setMode(WCMMode mode) {
        this.mode = mode
    }

    /**
     * Add a service instance to this request for testing within a component.  When calling the
     * <code>getService</code> method in a class extending <code>AbstractComponent</code> or
     * <code>AbstractSightlyComponent</code>, the service instance registered here will be returned for the given
     * service type.
     *
     * @param serviceType type of service to register for this request
     * @param instance service instance (real or mocked)
     */
    public <T> void addService(Class<T> serviceType, T instance) {
        services[serviceType] = instance
    }

    /**
     * Add service instances and associated filter criteria to this request for testing within a component.  When
     * calling the <code>getServices</code> method in a class extending <code>AbstractComponent</code> or
     * <code>AbstractSightlyComponent</code>, if the filter string matches the value provided here,
     * the corresponding array of service instances also registered here will be returned.  This method can be called
     * multiple times to register different service and filter value combinations.
     *
     * @param serviceType type of service to register for this request
     * @param instances array of service instances for the given service type and filter
     * @param filter filter string
     */
    public <T> void addServices(Class<T> serviceType, T[] instances, String filter) {
        def map = servicesWithFilters[serviceType] ?: [:]

        map[filter] = instances

        servicesWithFilters[serviceType] = map
    }

    ComponentRequest build() {
        build(null)
    }

    ComponentRequest build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        setSlingScriptHelper()

        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        if (mode) {
            mode.toRequest(slingRequest)
        }

        def componentServletRequest = new DefaultComponentServletRequest(slingRequest, slingResponse)

        new MockComponentRequest(componentServletRequest)
    }

    private def setSlingScriptHelper() {
        def scriptHelper = [
            getService: { Class serviceType ->
                services[serviceType]
            },
            getServices: { Class serviceType, String filter ->
                servicesWithFilters[serviceType]?.get(filter)
            }
        ] as SlingScriptHelper

        def bindings = new SlingBindings()

        bindings.sling = scriptHelper

        requestBuilder.setAttribute SlingBindings.class.name, bindings
    }
}
