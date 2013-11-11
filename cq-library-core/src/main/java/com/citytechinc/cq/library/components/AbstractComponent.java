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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;

import javax.jcr.Node;
import javax.jcr.Property;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for CQ component classes instantiated by the {@link com.citytechinc.cq.library.tags.ComponentTag}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AbstractComponent implements ComponentNode {

    protected final PageDecorator currentPage;

    protected final ComponentRequest request;

    private final ComponentNode componentNode;

    /**
     * Construct a component instance that is decoupled from a request.  For example, this constructor could be used to
     * instantiate an arbitrary component instance from within another component class.
     *
     * @param componentNode node for component
     */
    public AbstractComponent(final ComponentNode componentNode) {
        this.componentNode = componentNode;

        request = null;

        final Resource resource = componentNode.getResource();
        final PageManagerDecorator pageManager = resource.getResourceResolver().adaptTo(PageManagerDecorator.class);

        currentPage = pageManager.getContainingPage(resource);
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

    @Override
    public final ValueMap asMap() {
        return componentNode.asMap();
    }

    @Override
    public final Optional<ComponentNode> findAncestor(final Predicate<ComponentNode> predicate) {
        return componentNode.findAncestor(predicate);
    }

    @Override
    public final List<ComponentNode> findDescendants(final Predicate<ComponentNode> predicate) {
        return componentNode.findDescendants(predicate);
    }

    @Override
    public final Optional<ComponentNode> findAncestorWithProperty(final String propertyName) {
        return componentNode.findAncestorWithProperty(propertyName);
    }

    @Override
    public final <T> Optional<ComponentNode> findAncestorWithPropertyValue(final String propertyName,
        final T propertyValue) {
        return componentNode.findAncestorWithPropertyValue(propertyName, propertyValue);
    }

    @Override
    public final <T> T get(final String name, final T defaultValue) {
        return componentNode.get(name, defaultValue);
    }

    @Override
    public final <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return componentNode.get(propertyName, type);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName) {
        return componentNode.getAsHref(propertyName);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return componentNode.getAsHref(propertyName, strict);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return componentNode.getAsHref(propertyName, strict, mapped);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName) {
        return componentNode.getAsHrefInherited(propertyName);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName, final boolean strict) {
        return componentNode.getAsHrefInherited(propertyName, strict);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName, final boolean strict,
        final boolean mapped) {
        return componentNode.getAsHrefInherited(propertyName, strict, mapped);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName) {
        return componentNode.getAsLink(propertyName);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return componentNode.getAsLink(propertyName, strict);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return componentNode.getAsLink(propertyName, strict, mapped);
    }

    @Override
    public final <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return componentNode.getAsList(propertyName, type);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName) {
        return componentNode.getAsLinkInherited(propertyName);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return componentNode.getAsLinkInherited(propertyName, strict);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict,
        final boolean mapped) {
        return componentNode.getAsLinkInherited(propertyName, strict, mapped);
    }

    @Override
    public final <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return componentNode.getAsListInherited(propertyName, type);
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

    /**
     * Get the path of the current component with a ".html" extension.  This method can be overridden to return an
     * implementation-specific value.
     *
     * @return href of the current component
     */
    @Override
    public String getHref() {
        return componentNode.getHref();
    }

    @Override
    public final String getHref(final boolean mapped) {
        return componentNode.getHref(mapped);
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
    public final Optional<String> getImageSource() {
        return componentNode.getImageSource();
    }

    @Override
    public final Optional<String> getImageSource(final int width) {
        return componentNode.getImageSource(width);
    }

    @Override
    public final Optional<String> getImageSource(final String name) {
        return componentNode.getImageSource(name);
    }

    @Override
    public final Optional<String> getImageSource(final String name, final int width) {
        return componentNode.getImageSource(name, width);
    }

    @Override
    public final Optional<String> getImageSourceInherited() {
        return componentNode.getImageSourceInherited();
    }

    @Override
    public final Optional<String> getImageSourceInherited(final int width) {
        return componentNode.getImageSourceInherited(width);
    }

    @Override
    public final Optional<String> getImageSourceInherited(final String name) {
        return componentNode.getImageSourceInherited(name);
    }

    @Override
    public final Optional<String> getImageSourceInherited(final String name, final int width) {
        return componentNode.getImageSourceInherited(name, width);
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
    public final <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return componentNode.getInherited(propertyName, type);
    }

    /**
     * Get a link object to the path for the current component.  This method can be overridden to modify the default
     * behavior and return a different <code>Link</code> instance or implementation.
     *
     * @return link containing the path for the current component
     */
    @Override
    public Link getLink() {
        return componentNode.getLink();
    }

    @Override
    public final Link getLink(final boolean mapped) {
        return componentNode.getLink(mapped);
    }

    @Override
    public final LinkBuilder getLinkBuilder() {
        return componentNode.getLinkBuilder();
    }

    @Override
    public final LinkBuilder getLinkBuilder(final boolean mapped) {
        return componentNode.getLinkBuilder(mapped);
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
     * @param <T> type
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
