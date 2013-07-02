/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.services;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Session;

/**
 * Base class for services that require a <code>Session</code> and/or <code>ResourceResolver</code>.
 */
@Component(componentAbstract = true)
public abstract class AbstractSlingService {

    @Reference
    protected SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    protected Session session;

    protected ResourceResolver resourceResolver;

    @Activate
    protected void activate() throws Exception {
        session = repository.loginAdministrative(null);
        resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
    }

    @Deactivate
    protected void deactivate() throws Exception {
        if (session != null) {
            session.logout();
        }

        if (resourceResolver != null) {
            resourceResolver.close();
        }
    }
}
