package com.citytechinc.aem.bedrock.core.adapter

import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.node.impl.DefaultBasicNode
import com.citytechinc.aem.bedrock.core.node.impl.DefaultComponentNode
import com.citytechinc.aem.bedrock.core.page.impl.DefaultPageDecorator
import com.citytechinc.aem.bedrock.core.page.impl.DefaultPageManagerDecorator
import com.day.cq.wcm.api.Page
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingConstants
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.osgi.framework.Constants

@Component
@Service(BedrockAdapterFactory)
@Properties([
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Bedrock Core Adapter Factory"),
    @Property(name = SlingConstants.PROPERTY_ADAPTABLE_CLASSES, value = [
        "org.apache.sling.api.resource.Resource",
        "org.apache.sling.api.resource.ResourceResolver"
    ]),
    @Property(name = SlingConstants.PROPERTY_ADAPTER_CLASSES, value = [
        "com.citytechinc.aem.bedrock.api.page.PageManagerDecorator",
        "com.citytechinc.aem.bedrock.api.page.PageDecorator",
        "com.citytechinc.aem.bedrock.api.node.ComponentNode",
        "com.citytechinc.aem.bedrock.api.node.BasicNode"
    ])
])
final class BedrockAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        final AdapterType result

        if (adaptable instanceof ResourceResolver) {
            result = getResourceResolverAdapter((ResourceResolver) adaptable, type)
        } else if (adaptable instanceof Resource) {
            result = getResourceAdapter((Resource) adaptable, type)
        } else {
            result = null
        }

        result
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getResourceResolverAdapter(ResourceResolver resourceResolver,
        Class<AdapterType> type) {
        def result

        if (type == PageManagerDecorator) {
            result = (AdapterType) new DefaultPageManagerDecorator(resourceResolver)
        } else {
            result = null
        }

        result
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> AdapterType getResourceAdapter(Resource resource, Class<AdapterType> type) {
        def result

        if (type == PageDecorator) {
            def page = resource.adaptTo(Page)

            result = page == null ? null : (AdapterType) new DefaultPageDecorator(page)
        } else if (type == ComponentNode) {
            result = (AdapterType) new DefaultComponentNode(resource)
        } else if (type == BasicNode) {
            result = (AdapterType) new DefaultBasicNode(resource)
        } else {
            result = null
        }

        result
    }
}
