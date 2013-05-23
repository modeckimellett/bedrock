/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.adapter;

import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.node.ComponentNode;
import com.citytechinc.cq.library.content.node.impl.DefaultBasicNode;
import com.citytechinc.cq.library.content.node.impl.DefaultComponentNode;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.page.impl.DefaultPageManagerDecorator;
import com.day.cq.wcm.api.Page;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;

@Component
@Service(AdapterFactory.class)
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "CQ Library Content API Adapter Factory"),
    @Property(name = Constants.SERVICE_VENDOR, value = "CITYTECH, Inc."),
    @Property(name = "adaptables", value = {
        "org.apache.sling.api.resource.Resource",
        "org.apache.sling.api.resource.ResourceResolver"
    }),
    @Property(name = "adapters", value = {
        "com.citytechinc.cq.library.content.page.PageManagerDecorator",
        "com.citytechinc.cq.library.content.page.PageDecorator",
        "com.citytechinc.cq.library.content.node.ComponentNode",
        "com.citytechinc.cq.library.content.node.BasicNode"
    })
})
public final class ContentAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(final Object adaptable, final Class<AdapterType> type) {
        final AdapterType result;

        if (adaptable instanceof ResourceResolver) {
            result = getAdapter((ResourceResolver) adaptable, type);
        } else if (adaptable instanceof Resource) {
            result = getAdapter((Resource) adaptable, type);
        } else {
            result = null;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getAdapter(final ResourceResolver resourceResolver,
        final Class<AdapterType> type) {
        final AdapterType result;

        if (type == PageManagerDecorator.class) {
            result = (AdapterType) new DefaultPageManagerDecorator(resourceResolver);
        } else {
            result = null;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getAdapter(final Resource resource, final Class<AdapterType> type) {
        final AdapterType result;

        final PageManagerDecorator pageManager = resource.getResourceResolver().adaptTo(PageManagerDecorator.class);

        if (type == PageDecorator.class) {
            final Page page = resource.adaptTo(Page.class);

            result = (AdapterType) pageManager.getPage(page);
        } else if (type == ComponentNode.class) {
            result = (AdapterType) new DefaultComponentNode(resource);
        } else if (type == BasicNode.class) {
            result = (AdapterType) new DefaultBasicNode(resource);
        } else {
            result = null;
        }

        return result;
    }
}
