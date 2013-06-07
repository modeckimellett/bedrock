/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.builders;

import com.citytechinc.cq.library.constants.Constants;
import com.citytechinc.cq.library.constants.PathConstants;
import com.citytechinc.cq.library.constants.PropertyConstants;
import com.citytechinc.cq.library.content.link.ImageLink;
import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.NavigationLink;
import com.citytechinc.cq.library.content.link.enums.LinkTarget;
import com.citytechinc.cq.library.content.link.impl.DefaultImageLink;
import com.citytechinc.cq.library.content.link.impl.DefaultLink;
import com.citytechinc.cq.library.content.link.impl.DefaultNavigationLink;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.google.common.base.Charsets;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.citytechinc.cq.library.utils.PathUtils.isExternal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class LinkBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(LinkBuilder.class);

    private final boolean external;

    private final List<NavigationLink> children = new ArrayList<NavigationLink>();

    private final SetMultimap<String, String> parameters = LinkedHashMultimap.create();

    private final Map<String, String> properties = new HashMap<String, String>();

    private final List<String> selectors = new ArrayList<String>();

    private final String path;

    private String extension;

    private boolean active;

    private String imageSrc = "";

    private int port;

    private boolean secure;

    private String host;

    private String target = LinkTarget.SELF.getTarget();

    private String title = "";

    private LinkBuilder(final String path) {
        this.path = path;

        external = isExternal(path);
    }

    private LinkBuilder() {
        path = "";
        external = true;
    }

    public static LinkBuilder forLink(final Link link) {
        checkNotNull(link);

        return new LinkBuilder(link.getPath()).setExtension(link.getExtension()).setTitle(link.getTitle()).setTarget(
            link.getTarget());
    }

    public static LinkBuilder forNode(final Node node) {
        checkNotNull(node);

        LinkBuilder builder;

        try {
            builder = new LinkBuilder(node.getPath());
        } catch (RepositoryException re) {
            LOG.error("error getting path for node", re);

            builder = new LinkBuilder();
        }

        return builder;
    }

    public static LinkBuilder forNavigationPage(final PageDecorator page) {
        checkNotNull(page);

        final String redirect = page.getProperties().get(PropertyConstants.REDIRECT_TARGET, "");
        final String path = redirect.isEmpty() ? page.getPath() : redirect;
        final String title = page.getNavigationTitleOptional().or(page.getTitle());

        return new LinkBuilder(path).setTitle(title);
    }

    public static LinkBuilder forMappedNavigationPage(final PageDecorator page) {
        checkNotNull(page);

        final String redirect = page.getProperties().get(PropertyConstants.REDIRECT_TARGET, "");
        final String path = redirect.isEmpty() ? page.getPath() : redirect;
        final String title = page.getNavigationTitleOptional().or(page.getTitle());

        final ResourceResolver resourceResolver = page.adaptTo(Resource.class).getResourceResolver();

        return new LinkBuilder(resourceResolver.map(path)).setTitle(title);
    }

    public static LinkBuilder forMappedPage(final PageDecorator page) {
        checkNotNull(page);

        final String redirect = page.getProperties().get(PropertyConstants.REDIRECT_TARGET, "");
        final String path = redirect.isEmpty() ? page.getPath() : redirect;

        final ResourceResolver resourceResolver = page.adaptTo(Resource.class).getResourceResolver();

        return new LinkBuilder(resourceResolver.map(path)).setTitle(page.getTitle());
    }

    public static LinkBuilder forPage(final PageDecorator page) {
        checkNotNull(page);

        final String redirect = page.getProperties().get(PropertyConstants.REDIRECT_TARGET, "");
        final String path = redirect.isEmpty() ? page.getPath() : redirect;

        return new LinkBuilder(path).setTitle(page.getTitle());
    }

    public static LinkBuilder forPath(final String path) {
        checkNotNull(path);

        return path.isEmpty() ? new LinkBuilder() : new LinkBuilder(path);
    }

    public static LinkBuilder forResource(final Resource resource) {
        checkNotNull(resource);

        return new LinkBuilder(resource.getPath());
    }

    public static LinkBuilder forMappedResource(final Resource resource) {
        checkNotNull(resource);

        final ResourceResolver resourceResolver = resource.getResourceResolver();

        return new LinkBuilder(resourceResolver.map(resource.getPath()));
    }

    public LinkBuilder addChild(final NavigationLink child) {
        children.add(checkNotNull(child));

        return this;
    }

    public LinkBuilder addParameter(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        parameters.put(name, value);

        return this;
    }

    public LinkBuilder addParameters(final Map<String, String> parameters) {
        this.parameters.putAll(Multimaps.forMap(checkNotNull(parameters)));

        return this;
    }

    public LinkBuilder addProperties(final Map<String, String> properties) {
        this.properties.putAll(checkNotNull(properties));

        return this;
    }

    public LinkBuilder addProperty(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        properties.put(name, value);

        return this;
    }

    public LinkBuilder addSelector(final String selector) {
        selectors.add(checkNotNull(selector));

        return this;
    }

    public LinkBuilder addSelectors(final List<String> selectors) {
        this.selectors.addAll(checkNotNull(selectors));

        return this;
    }

    public Link build() {
        final StringBuilder builder = new StringBuilder();

        builder.append(buildHost());
        builder.append(buildPathWithSelectors());

        final String extension;

        if (path.contains(PathConstants.SELECTOR)) {
            extension = path.substring(path.indexOf(PathConstants.SELECTOR) + 1);
        } else {
            extension = this.extension == null ? Constants.EXTENSION_HTML : this.extension;

            if (!external && !extension.isEmpty()) {
                builder.append('.');
                builder.append(extension);
            }
        }

        final String queryString = buildQuery();

        builder.append(queryString);

        final String href = builder.toString();

        LOG.debug("build() href = {}", href);

        return new DefaultLink(path, extension, href, selectors, queryString, external, target, title, properties);
    }

    public ImageLink buildImageLink() {
        final Link link = build();

        return new DefaultImageLink(link, imageSrc);
    }

    public NavigationLink buildNavigationLink() {
        final Link link = build();

        return new DefaultNavigationLink(link, active, children);
    }

    /**
     * @return href for this link
     */
    public String getHref() {
        return build().getHref();
    }

    public LinkBuilder setActive(final boolean active) {
        this.active = active;

        return this;
    }

    public LinkBuilder setExtension(final String extension) {
        this.extension = extension;

        return this;
    }

    public LinkBuilder setHost(final String host) {
        this.host = host;

        return this;
    }

    public LinkBuilder setImage(final String imageSrc) {
        this.imageSrc = imageSrc;

        return this;
    }

    public LinkBuilder setPort(final int port) {
        this.port = port;

        return this;
    }

    public LinkBuilder setSecure(final boolean secure) {
        this.secure = secure;

        return this;
    }

    public LinkBuilder setTarget(final String target) {
        this.target = target;

        return this;
    }

    public LinkBuilder setTitle(final String title) {
        this.title = title;

        return this;
    }

    private String buildHost() {
        final StringBuilder builder = new StringBuilder();

        if (!external && isNotEmpty(host)) {
            builder.append(secure ? "https" : "http");
            builder.append("://");
            builder.append(host);

            if (port > 0) {
                builder.append(':');
                builder.append(port);
            }
        }

        return builder.toString();
    }

    private String buildPathWithSelectors() {
        final StringBuilder builder = new StringBuilder();

        builder.append(path);
        builder.append(buildSelectors());

        return builder.toString();
    }

    private String buildQuery() {
        final StringBuilder builder = new StringBuilder();

        if (!parameters.isEmpty()) {
            builder.append('?');

            for (final String name : parameters.keySet()) {
                final Set<String> values = parameters.get(name);

                for (final String value : values) {
                    try {
                        builder.append(URLEncoder.encode(name, Charsets.UTF_8.name()));
                        builder.append('=');
                        builder.append(URLEncoder.encode(value, Charsets.UTF_8.name()));
                    } catch (UnsupportedEncodingException uee) {
                        LOG.error("invalid encoding for parameter = " + name + "=" + value, uee);
                    }

                    builder.append('&');
                }
            }

            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String buildSelectors() {
        final StringBuilder builder = new StringBuilder();

        if (!external) {
            for (final String selector : selectors) {
                builder.append('.');
                builder.append(selector);
            }
        }

        return builder.toString();
    }
}
