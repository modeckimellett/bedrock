/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.node.ComponentNode;
import com.citytechinc.cq.library.content.node.predicates.ComponentNodePropertyExistsPredicate;
import com.citytechinc.cq.library.content.node.predicates.ComponentNodePropertyValuePredicate;
import com.citytechinc.cq.library.content.node.predicates.ComponentNodeResourceTypePredicate;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import java.util.Collections;
import java.util.List;

import static com.citytechinc.cq.library.constants.Constants.DEFAULT_IMAGE_NAME;
import static com.citytechinc.cq.library.content.link.impl.LinkFunctions.LINK_TO_HREF;
import static com.citytechinc.cq.library.content.link.impl.LinkFunctions.PATH_TO_LINK;
import static com.citytechinc.cq.library.content.node.impl.NodeFunctions.RESOURCE_TO_BASIC_NODE;
import static com.citytechinc.cq.library.content.node.impl.NodeFunctions.RESOURCE_TO_COMPONENT_NODE;
import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultComponentNode implements ComponentNode {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultComponentNode.class);

    private final BasicNode basicNode;

    private final InheritanceValueMap properties;

    private final Resource resource;

    public DefaultComponentNode(final Resource resource) {
        checkNotNull(resource);

        this.resource = resource;

        basicNode = new DefaultBasicNode(resource);
        properties = new HierarchyNodeInheritanceValueMap(resource);
    }

    @Override
    public ValueMap asMap() {
        return basicNode.asMap();
    }

    @Override
    public Optional<ComponentNode> findAncestor(final Predicate<ComponentNode> predicate) {
        return findAncestorForPredicate(predicate);
    }

    @Override
    public Optional<ComponentNode> findAncestorWithProperty(final String propertyName) {
        return findAncestorForPredicate(new ComponentNodePropertyExistsPredicate(propertyName));
    }

    @Override
    public <T> Optional<ComponentNode> findAncestorWithPropertyValue(final String propertyName, final T propertyValue) {
        return findAncestorForPredicate(new ComponentNodePropertyValuePredicate<T>(propertyName, propertyValue));
    }

    @Override
    public <T> Optional<T> get(final String propertyName) {
        return basicNode.get(propertyName);
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return basicNode.get(propertyName, defaultValue);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return basicNode.getAsHref(propertyName);
    }

    @Override
    public String getAsHref(final String propertyName, final String defaultValue) {
        return basicNode.getAsHref(propertyName, defaultValue);
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName) {
        return getAsLinkInherited(propertyName).transform(LINK_TO_HREF);
    }

    @Override
    public String getAsHrefInherited(final String propertyName, final String defaultValue) {
        return getAsHrefInherited(propertyName).or(defaultValue);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return basicNode.getAsLink(propertyName);
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName) {
        final Optional<String> pathOptional = getInherited(propertyName);

        return pathOptional.transform(PATH_TO_LINK);
    }

    @Override
    public Optional<String> getAsMappedHref(final String propertyName) {
        return basicNode.getAsMappedHref(propertyName);
    }

    @Override
    public String getAsMappedHref(final String propertyName, final String defaultValue) {
        return basicNode.getAsMappedHref(propertyName, defaultValue);
    }

    @Override
    public Optional<String> getAsMappedHrefInherited(final String propertyName) {
        return getAsMappedLinkInherited(propertyName).transform(LINK_TO_HREF);
    }

    @Override
    public String getAsMappedHrefInherited(final String propertyName, final String defaultValue) {
        return getAsMappedHrefInherited(propertyName).or(defaultValue);
    }

    @Override
    public Optional<Link> getAsMappedLink(final String propertyName) {
        return basicNode.getAsMappedLink(propertyName);
    }

    @Override
    public Optional<Link> getAsMappedLinkInherited(final String propertyName) {
        final Optional<String> pathOptional = getInherited(propertyName);

        return pathOptional.transform(new Function<String, String>() {
            @Override
            public String apply(final String path) {
                return resource.getResourceResolver().map(path);
            }
        }).transform(PATH_TO_LINK);
    }

    @Override
    public Optional<PageDecorator> getAsPage(final String propertyName) {
        return basicNode.getAsPage(propertyName);
    }

    @Override
    public Optional<PageDecorator> getAsPageInherited(final String propertyName) {
        final String path = properties.getInherited(propertyName, "");

        return resource.getResourceResolver().adaptTo(PageManagerDecorator.class).getPageOptional(path);
    }

    @Override
    public Optional<ComponentNode> getComponentNode(final String relativePath) {
        checkNotNull(relativePath);

        return Optional.fromNullable(resource.getChild(relativePath)).transform(RESOURCE_TO_COMPONENT_NODE);
    }

    @Override
    public List<ComponentNode> getComponentNodes() {
        return FluentIterable.from(resource.getChildren()).transform(RESOURCE_TO_COMPONENT_NODE).toList();
    }

    @Override
    public List<ComponentNode> getComponentNodes(final Predicate<ComponentNode> predicate) {
        checkNotNull(predicate);

        return FluentIterable.from(resource.getChildren()).transform(RESOURCE_TO_COMPONENT_NODE).filter(predicate)
            .toList();
    }

    @Override
    public List<ComponentNode> getComponentNodes(final String relativePath) {
        checkNotNull(relativePath);

        final Resource child = resource.getChild(relativePath);

        final List<ComponentNode> nodes;

        if (child == null) {
            nodes = Collections.emptyList();
        } else {
            nodes = FluentIterable.from(child.getChildren()).transform(RESOURCE_TO_COMPONENT_NODE).toList();
        }

        return nodes;
    }

    @Override
    public List<ComponentNode> getComponentNodes(final String relativePath, final String resourceType) {
        return getComponentNodes(relativePath, new ComponentNodeResourceTypePredicate(resourceType));
    }

    @Override
    public List<ComponentNode> getComponentNodes(final String relativePath, final Predicate<ComponentNode> predicate) {
        checkNotNull(relativePath);
        checkNotNull(predicate);

        return FluentIterable.from(getComponentNodes(relativePath)).filter(predicate).toList();
    }

    @Override
    public Optional<BasicNode> getDesignNode() {
        final ResourceResolver resourceResolver = resource.getResourceResolver();

        final Style style = resourceResolver.adaptTo(Designer.class).getStyle(resource);

        final Resource styleResource = resourceResolver.getResource(style.getPath());

        return Optional.fromNullable(styleResource).transform(RESOURCE_TO_BASIC_NODE);
    }

    @Override
    public String getHref() {
        return getLink().getHref();
    }

    @Override
    public Optional<String> getImageReference() {
        return basicNode.getImageReference();
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        return basicNode.getImageReference(name);
    }

    @Override
    public Optional<String> getImageReferenceInherited() {
        return getImageReferenceInherited(DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageReferenceInherited(final String name) {
        return Optional.fromNullable(properties.getInherited(name + "/" + DownloadResource.PN_REFERENCE, String.class));
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        return basicNode.getImageRendition(renditionName);
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        return basicNode.getImageRendition(name, renditionName);
    }

    @Override
    public Optional<String> getImageSource() {
        return basicNode.getImageSource();
    }

    @Override
    public Optional<String> getImageSource(final int width) {
        return basicNode.getImageSource(width);
    }

    @Override
    public Optional<String> getImageSource(final String name) {
        return basicNode.getImageSource(name);
    }

    @Override
    public Optional<String> getImageSource(final String name, final int width) {
        return basicNode.getImageSource(name, width);
    }

    @Override
    public Optional<String> getImageSourceInherited() {
        return getImageSourceInherited(DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageSourceInherited(final int width) {
        return getImageSourceInherited(DEFAULT_IMAGE_NAME, width);
    }

    @Override
    public Optional<String> getImageSourceInherited(final String name) {
        return getImageSourceInherited(name, -1);
    }

    @Override
    public Optional<String> getImageSourceInherited(final String name, final int width) {
        final Optional<String> optionalImageSource;

        if (isHasImage(name)) {
            optionalImageSource = getImageSource(name, width);
        } else {
            final Predicate<ComponentNode> predicate = new Predicate<ComponentNode>() {
                @Override
                public boolean apply(final ComponentNode componentNode) {
                    return componentNode.isHasImage(name);
                }
            };

            optionalImageSource = findAncestor(predicate).transform(new Function<ComponentNode, String>() {
                @Override
                public String apply(final ComponentNode componentNode) {
                    return componentNode.getImageSource(name, width).get();
                }
            });
        }

        return optionalImageSource;
    }

    @Override
    public int getIndex() {
        return basicNode.getIndex();
    }

    @Override
    public int getIndex(final String resourceType) {
        return basicNode.getIndex(resourceType);
    }

    @Override
    public <T> T getInherited(final String propertyName, final T defaultValue) {
        return properties.getInherited(propertyName, defaultValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInherited(final String propertyName) {
        return (Optional<T>) Optional.fromNullable(properties.getInherited(propertyName, null));
    }

    @Override
    public Link getLink() {
        return basicNode.getLink();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return basicNode.getLinkBuilder();
    }

    @Override
    public String getMappedHref() {
        return basicNode.getMappedHref();
    }

    @Override
    public Link getMappedLink() {
        return basicNode.getMappedLink();
    }

    @Override
    public LinkBuilder getMappedLinkBuilder() {
        return basicNode.getMappedLinkBuilder();
    }

    @Override
    public Optional<Node> getNode() {
        return basicNode.getNode();
    }

    @Override
    public List<BasicNode> getNodesInherited(final String relativePath) {
        final Optional<Resource> childOptional = findChildResourceInherited(relativePath);

        final List<BasicNode> nodes;

        if (childOptional.isPresent()) {
            final Resource child = childOptional.get();

            nodes = FluentIterable.from(child.getChildren()).transform(RESOURCE_TO_BASIC_NODE).toList();
        } else {
            nodes = Collections.emptyList();
        }

        return nodes;
    }

    @Override
    public String getPath() {
        return basicNode.getPath();
    }

    @Override
    public List<Property> getProperties(final Predicate<Property> predicate) {
        return basicNode.getProperties(predicate);
    }

    @Override
    public Resource getResource() {
        return basicNode.getResource();
    }

    @Override
    public boolean isHasImage() {
        return basicNode.isHasImage();
    }

    @Override
    public boolean isHasImage(final String name) {
        return basicNode.isHasImage(name);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("path", getPath()).add("properties", Maps.newHashMap(asMap()))
            .toString();
    }

    private Optional<ComponentNode> findAncestorForPredicate(final Predicate<ComponentNode> predicate) {
        final PageManagerDecorator pageManager = resource.getResourceResolver().adaptTo(PageManagerDecorator.class);
        final PageDecorator containingPage = pageManager.getContainingPage(resource);

        final String path = resource.getPath();

        final String relativePath = resource.getName().equals(JcrConstants.JCR_CONTENT) ? "" : path.substring(
            containingPage.getContentResource().getPath().length() + 1);

        LOG.debug("findAncestorForPredicate() relative path = {}", relativePath);

        final Function<PageDecorator, Optional<ComponentNode>> componentNodeFunction = new Function<PageDecorator, Optional<ComponentNode>>() {
            @Override
            public Optional<ComponentNode> apply(final PageDecorator page) {
                return relativePath.isEmpty() ? page.getComponentNode() : page.getComponentNode(relativePath);
            }
        };

        final Predicate<PageDecorator> pagePredicate = new Predicate<PageDecorator>() {
            @Override
            public boolean apply(final PageDecorator page) {
                final Optional<ComponentNode> componentNodeOptional = componentNodeFunction.apply(page);

                return componentNodeOptional.isPresent() && predicate.apply(componentNodeOptional.get());
            }
        };

        return containingPage.findAncestor(pagePredicate).transform(new Function<PageDecorator, ComponentNode>() {
            @Override
            public ComponentNode apply(final PageDecorator page) {
                return componentNodeFunction.apply(page).get();
            }
        });
    }

    private Optional<Resource> findChildResourceInherited(final String relativePath) {
        final PageManagerDecorator pageManager = resource.getResourceResolver().adaptTo(PageManagerDecorator.class);
        final PageDecorator containingPage = pageManager.getContainingPage(resource);

        final StringBuilder builder = new StringBuilder();

        if (resource.getName().equals(JcrConstants.JCR_CONTENT)) {
            builder.append(relativePath);
        } else {
            builder.append(resource.getPath().substring(containingPage.getContentResource().getPath().length() + 1));
            builder.append('/');
            builder.append(relativePath);
        }

        // path relative to jcr:content
        final String resourcePath = builder.toString();

        LOG.debug("findChildResourceInherited() child resource relative path = {}", resourcePath);

        final Predicate<PageDecorator> predicate = new Predicate<PageDecorator>() {
            @Override
            public boolean apply(final PageDecorator page) {
                return page.getContentResource(resourcePath) != null;
            }
        };

        return containingPage.findAncestor(predicate).transform(new Function<PageDecorator, Resource>() {
            @Override
            public Resource apply(final PageDecorator page) {
                return page.getContentResource(resourcePath);
            }
        });
    }
}
