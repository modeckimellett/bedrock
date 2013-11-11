/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.resource.predicates.PathPredicate;
import com.citytechinc.cq.library.content.resource.predicates.ResourceTypePredicate;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.foundation.Image;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.citytechinc.cq.library.constants.ComponentConstants.DEFAULT_IMAGE_NAME;
import static com.citytechinc.cq.library.constants.PathConstants.EXTENSION_PNG;
import static com.citytechinc.cq.library.content.link.impl.LinkFunctions.LINK_TO_HREF;
import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultBasicNode extends AbstractNode implements BasicNode {

    private static final Predicate<Resource> ALL = Predicates.alwaysTrue();

    /**
     * Image servlet selector.
     */
    private static final String IMAGE_SELECTOR = "img";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBasicNode.class);

    private static final Function<Rendition, String> RENDITION_TO_PATH = new Function<Rendition, String>() {
        @Override
        public String apply(final Rendition rendition) {
            return rendition.getPath();
        }
    };

    private final ValueMap properties;

    public DefaultBasicNode(final Resource resource) {
        super(resource);

        properties = ResourceUtil.getValueMap(resource);
    }

    @Override
    public ValueMap asMap() {
        return properties;
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return properties.get(checkNotNull(propertyName), defaultValue);
    }

    @Override
    public <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return Optional.fromNullable(properties.get(propertyName, type));
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return getAsHref(propertyName, false);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return getAsHref(propertyName, strict, false);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return getAsLink(propertyName, strict, mapped).transform(LINK_TO_HREF);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return getAsLink(propertyName, false);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return getAsLink(propertyName, strict, false);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return getLinkOptional(get(propertyName, String.class), strict, mapped);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        final T[] defaultValue = (T[]) Array.newInstance(type, 0);

        return Arrays.asList(properties.get(propertyName, defaultValue));
    }

    @Override
    public Optional<PageDecorator> getAsPage(final String propertyName) {
        final Optional<PageDecorator> pageOptional;

        final String path = properties.get(checkNotNull(propertyName), "");

        if (path.isEmpty()) {
            pageOptional = Optional.absent();
        } else {
            pageOptional = resource.getResourceResolver().adaptTo(PageManagerDecorator.class).getPageOptional(path);
        }

        return pageOptional;
    }

    @Override
    public String getHref() {
        return getHref(false);
    }

    @Override
    public String getHref(final boolean mapped) {
        return getLink(mapped).getHref();
    }

    @Override
    public Optional<String> getImageReference() {
        return getImageReference(DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        checkNotNull(name);

        return Optional.fromNullable(properties.get(name + "/" + DownloadResource.PN_REFERENCE, String.class));
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        checkNotNull(renditionName);

        return getImageRendition(DEFAULT_IMAGE_NAME, renditionName);
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        checkNotNull(name);
        checkNotNull(renditionName);

        final Optional<String> imageReferenceOptional = getImageReference(name);

        final Optional<String> imageRenditionOptional;

        if (imageReferenceOptional.isPresent()) {
            final Asset asset = resource.getResourceResolver().getResource(imageReferenceOptional.get()).adaptTo(
                Asset.class);

            if (asset == null) {
                imageRenditionOptional = Optional.absent();
            } else {
                imageRenditionOptional = Iterables.tryFind(asset.getRenditions(), new Predicate<Rendition>() {
                    @Override
                    public boolean apply(final Rendition rendition) {
                        return renditionName.equals(rendition.getName());
                    }
                }).transform(RENDITION_TO_PATH);
            }
        } else {
            imageRenditionOptional = Optional.absent();
        }

        return imageRenditionOptional;
    }

    @Override
    public Optional<String> getImageSource() {
        return getImageSource(DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageSource(final int width) {
        return getImageSource(DEFAULT_IMAGE_NAME, width);
    }

    @Override
    public Optional<String> getImageSource(final String name) {
        return getImageSource(name, -1);
    }

    @Override
    public Optional<String> getImageSource(final String name, final int width) {
        final Optional<String> optionalImageSource;

        if (isHasImage(name)) {
            final StringBuilder builder = new StringBuilder();

            if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                builder.append(resource.getParent().getPath());
            } else {
                builder.append(resource.getPath());
            }

            builder.append('.').append(IMAGE_SELECTOR);

            if (!name.equals(DEFAULT_IMAGE_NAME)) {
                builder.append('.').append(name);
            }

            if (width > -1) {
                builder.append('.').append(width);
            }

            builder.append('.').append(EXTENSION_PNG);

            optionalImageSource = Optional.of(builder.toString());
        } else {
            optionalImageSource = Optional.absent();
        }

        return optionalImageSource;
    }

    @Override
    public int getIndex() {
        return getIndex(ALL);
    }

    @Override
    public int getIndex(final String resourceType) {
        return getIndex(new ResourceTypePredicate(resourceType));
    }

    @Override
    public Link getLink() {
        return getLink(false);
    }

    @Override
    public Link getLink(final boolean mapped) {
        return getLinkBuilder(true).build();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return getLinkBuilder(false);
    }

    @Override
    public LinkBuilder getLinkBuilder(final boolean mapped) {
        return LinkBuilder.forResource(resource, mapped);
    }

    @Override
    public Optional<Node> getNode() {
        return Optional.fromNullable(resource.adaptTo(Node.class));
    }

    @Override
    public String getPath() {
        return resource.getPath();
    }

    @Override
    public List<Property> getProperties(final Predicate<Property> predicate) {
        checkNotNull(predicate);

        final Node node = resource.adaptTo(Node.class);

        final List<Property> properties;

        if (node == null) {
            properties = Collections.emptyList();
        } else {
            properties = Lists.newArrayList();

            try {
                final PropertyIterator iterator = node.getProperties();

                while (iterator.hasNext()) {
                    final Property property = iterator.nextProperty();

                    if (predicate.apply(property)) {
                        properties.add(property);
                    }
                }
            } catch (RepositoryException e) {
                LOG.error("error getting properties for node = " + getPath(), e);
            }
        }

        return properties;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public boolean isHasImage() {
        return isHasImage(DEFAULT_IMAGE_NAME);
    }

    @Override
    public boolean isHasImage(final String name) {
        checkNotNull(name);

        final Resource child = resource.getChild(name);

        return child != null && new Image(resource, name).hasContent();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("path", getPath()).add("properties", Maps.newHashMap(asMap()))
            .toString();
    }

    private int getIndex(final Predicate<Resource> resourceTypePredicate) {
        final Iterable<Resource> resources = Iterables.filter(resource.getParent().getChildren(),
            resourceTypePredicate);

        return Iterables.indexOf(resources, new PathPredicate(getPath()));
    }
}
