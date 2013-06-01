/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.components;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.node.ComponentNode;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.request.ComponentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for CQ component classes instantiated by the {@link com.citytechinc.cq.library.tags.ComponentTag}.
 * <p/>
 * For documentation on the methods available here, see {@link com.citytechinc.cq.library.content.node.ComponentNode}.
 */
public abstract class AbstractComponent implements ComponentNode {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponent.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Predicate<Property> PROTECTED = new Predicate<Property>() {
        @Override
        public boolean apply(final Property property) {
            boolean isProtected = false;

            try {
                isProtected = property.getDefinition().isProtected();
            } catch (RepositoryException e) {
                LOG.error("error getting property definition", e);
            }

            return isProtected;
        }
    };

    protected final ComponentRequest request;

    protected final PageDecorator currentPage;

    private final ComponentNode componentNode;

    /**
     * Construct a component instance that is decoupled from a request.  For example, this constructor could be used to
     * instantiate an arbitrary component instance from within another component class.
     *
     * @param componentNode node for component
     */
    public AbstractComponent(final ComponentNode componentNode) {
        request = null;

        final Resource resource = componentNode.getResource();
        final PageManagerDecorator pageManager = resource.getResourceResolver().adaptTo(PageManagerDecorator.class);

        currentPage = pageManager.getContainingPage(resource);

        this.componentNode = componentNode;
    }

    /**
     * Construct a component instance from a request.  Typically this is called from the constructor of the concrete
     * class that extends this class (which itself was instantiated by the {@link com.citytechinc.cq.library.tags.ComponentTag}).
     *
     * @param request component request
     */
    public AbstractComponent(final ComponentRequest request) {
        this.request = request;

        currentPage = request.getCurrentPage();
        componentNode = request.getComponentNode();
    }

    /**
     * Get the properties for this component as JSON.
     *
     * @return JSON map of non-protected properties for this component
     * @throws JsonProcessingException if error occurs writing property map as JSON
     */
    public final String getPropertiesAsJson() throws JsonProcessingException {
        final List<Property> protectedProperties = getProperties(PROTECTED);
        final Set<String> protectedPropertyNames = Sets.newHashSet();

        for (final Property property : protectedProperties) {
            try {
                protectedPropertyNames.add(property.getName());
            } catch (RepositoryException e) {
                LOG.error("error getting property name", e);
            }
        }

        final Map<String, Object> propertyMap = Maps.filterKeys(asMap(), new Predicate<String>() {
            @Override
            public boolean apply(final String key) {
                return !protectedPropertyNames.contains(key);
            }
        });

        return MAPPER.writeValueAsString(propertyMap);
    }

    @Override
    public final ValueMap asMap() {
        return componentNode.asMap();
    }

    @Override
    public final Optional<ComponentNode> findAncestor(final Predicate<ComponentNode> predicate) {
        return componentNode.findAncestor(predicate);
    }

    @Override
    public final Optional<ComponentNode> findAncestorWithProperty(final String propertyName) {
        return componentNode.findAncestorWithProperty(propertyName);
    }

    @Override
    public final <T> Optional<T> get(final String name) {
        return componentNode.get(name);
    }

    @Override
    public final <T> T get(final String name, final T defaultValue) {
        return componentNode.get(name, defaultValue);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName) {
        return componentNode.getAsHref(propertyName);
    }

    @Override
    public final String getAsHref(final String propertyName, final String defaultValue) {
        return componentNode.getAsHref(propertyName, defaultValue);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName) {
        return componentNode.getAsHrefInherited(propertyName);
    }

    @Override
    public final String getAsHrefInherited(final String propertyName, final String defaultValue) {
        return componentNode.getAsHrefInherited(propertyName, defaultValue);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName) {
        return componentNode.getAsLink(propertyName);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName) {
        return componentNode.getAsLinkInherited(propertyName);
    }

    @Override
    public final Optional<String> getAsMappedHref(final String propertyName) {
        return componentNode.getAsMappedHref(propertyName);
    }

    @Override
    public final String getAsMappedHref(final String propertyName, final String defaultValue) {
        return componentNode.getAsMappedHref(propertyName, defaultValue);
    }

    @Override
    public final Optional<String> getAsMappedHrefInherited(final String propertyName) {
        return componentNode.getAsMappedHrefInherited(propertyName);
    }

    @Override
    public final String getAsMappedHrefInherited(final String propertyName, final String defaultValue) {
        return componentNode.getAsMappedHrefInherited(propertyName, defaultValue);
    }

    @Override
    public final Optional<Link> getAsMappedLink(final String propertyName) {
        return componentNode.getAsMappedLink(propertyName);
    }

    @Override
    public final Optional<Link> getAsMappedLinkInherited(final String propertyName) {
        return componentNode.getAsMappedLinkInherited(propertyName);
    }

    @Override
    public final Optional<PageDecorator> getAsPage(final String propertyName) {
        return componentNode.getAsPage(propertyName);
    }

    @Override
    public final Optional<PageDecorator> getAsPageInherited(final String propertyName) {
        return componentNode.getAsPageInherited(propertyName);
    }

    @Override
    public final Optional<ComponentNode> getComponentNode(final String relativePath) {
        return componentNode.getComponentNode(relativePath);
    }

    @Override
    public final List<ComponentNode> getComponentNodes() {
        return componentNode.getComponentNodes();
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final Predicate<ComponentNode> predicate) {
        return componentNode.getComponentNodes(predicate);
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final String relativePath) {
        return componentNode.getComponentNodes(relativePath);
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final String parsysName, final String resourceType) {
        return componentNode.getComponentNodes(parsysName, resourceType);
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final String relativePath,
                                                       final Predicate<ComponentNode> predicate) {
        return componentNode.getComponentNodes(relativePath, predicate);
    }

    @Override
    public final Optional<BasicNode> getDesignNode() {
        return componentNode.getDesignNode();
    }

    @Override
    public final String getHref() {
        return componentNode.getHref();
    }

    @Override
    public final Optional<String> getImageReference() {
        return componentNode.getImageReference();
    }

    @Override
    public final Optional<String> getImageReference(final String name) {
        return componentNode.getImageReference(name);
    }

    @Override
    public final Optional<String> getImageReferenceInherited() {
        return componentNode.getImageReferenceInherited();
    }

    @Override
    public final Optional<String> getImageReferenceInherited(final String name) {
        return componentNode.getImageReferenceInherited(name);
    }

    @Override
    public final Optional<String> getImageRendition(final String renditionName) {
        return componentNode.getImageRendition(renditionName);
    }

    @Override
    public final Optional<String> getImageRendition(final String name, final String renditionName) {
        return componentNode.getImageRendition(name, renditionName);
    }

    @Override
    public final int getIndex() {
        return componentNode.getIndex();
    }

    @Override
    public final int getIndex(final String resourceType) {
        return componentNode.getIndex(resourceType);
    }

    @Override
    public final <T> T getInherited(final String propertyName, final T defaultValue) {
        return componentNode.getInherited(propertyName, defaultValue);
    }

    @Override
    public final <T> Optional<T> getInherited(final String propertyName) {
        return componentNode.getInherited(propertyName);
    }

    @Override
    public final Link getLink() {
        return componentNode.getLink();
    }

    @Override
    public final LinkBuilder getLinkBuilder() {
        return componentNode.getLinkBuilder();
    }

    @Override
    public final String getMappedHref() {
        return componentNode.getMappedHref();
    }

    @Override
    public final Link getMappedLink() {
        return componentNode.getMappedLink();
    }

    @Override
    public final LinkBuilder getMappedLinkBuilder() {
        return componentNode.getMappedLinkBuilder();
    }

    @Override
    public final Optional<Node> getNode() {
        return componentNode.getNode();
    }

    @Override
    public final List<BasicNode> getNodesInherited(final String relativePath) {
        return componentNode.getNodesInherited(relativePath);
    }

    @Override
    public final String getPath() {
        return componentNode.getPath();
    }

    @Override
    public final List<Property> getProperties(final Predicate<Property> predicate) {
        return componentNode.getProperties(predicate);
    }

    @Override
    public final Resource getResource() {
        return componentNode.getResource();
    }

    /**
     * Get an OSGi service.
     *
     * @param serviceType the type (class) of the service
     * @param <T>         type
     * @return the service instance, or null if it is not available
     */
    public final <T> T getService(final Class<T> serviceType) {
        checkNotNull(request, "cannot get a service reference when request is null");

        final SlingBindings bindings = (SlingBindings) request.getSlingRequest().getAttribute(
                SlingBindings.class.getName());

        checkNotNull(bindings, "request does not contain bindings attribute");

        final SlingScriptHelper sling = bindings.getSling();

        checkNotNull(sling, "bindings do not contain " + SlingScriptHelper.class.getName());

        return sling.getService(serviceType);
    }

    @Override
    public final boolean isHasImage() {
        return componentNode.isHasImage();
    }

    @Override
    public final boolean isHasImage(final String name) {
        return componentNode.isHasImage(name);
    }
}
