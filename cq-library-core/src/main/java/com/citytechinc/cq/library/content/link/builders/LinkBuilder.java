/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link.builders;

import com.citytechinc.cq.library.constants.PathConstants;
import com.citytechinc.cq.library.constants.PropertyConstants;
import com.citytechinc.cq.library.content.link.ImageLink;
import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.NavigationLink;
import com.citytechinc.cq.library.content.link.enums.LinkTarget;
import com.citytechinc.cq.library.content.link.impl.DefaultImageLink;
import com.citytechinc.cq.library.content.link.impl.DefaultLink;
import com.citytechinc.cq.library.content.link.impl.DefaultNavigationLink;
import com.citytechinc.cq.library.content.page.enums.TitleType;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Charsets;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Builder for creating <code>Link</code>, <code>ImageLink</code>, and <code>NavigationLink</code> objects.
 */
public final class LinkBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(LinkBuilder.class);

    private static final String UTF_8 = Charsets.UTF_8.name();

    private final List<NavigationLink> children = new ArrayList<NavigationLink>();

    private final boolean external;

    private final SetMultimap<String, String> parameters = LinkedHashMultimap.create();

    private final String path;

    private final Map<String, String> properties = new HashMap<String, String>();

    private final List<String> selectors = new ArrayList<String>();

    private boolean active;

    private String extension;

    private String host;

    private String imageSource = "";

    private int port;

    private boolean secure;

    private String target = LinkTarget.SELF.getTarget();

    private String title = "";

    private LinkBuilder(final String path) {
        this.path = path;

        external = isExternal(path);
    }

    /**
     * Get a builder instance for an existing <code>Link</code>.  The path, extension, title, and target are copied from
     * the link argument.
     *
     * @param link existing link
     * @return builder
     */
    public static LinkBuilder forLink(final Link link) {
        checkNotNull(link);

        return new LinkBuilder(link.getPath()).setExtension(link.getExtension()).setTitle(link.getTitle()).setTarget(
            link.getTarget());
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @return builder containing the path of the given page
     */
    public static LinkBuilder forPage(final Page page) {
        return forPage(page, false, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final Page page, final TitleType titleType) {
        checkNotNull(page);

        final String title = page.getProperties().get(titleType.getPropertyName(), page.getTitle());

        return new LinkBuilder(getPagePath(page, false)).setTitle(title);
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @param mappedPath if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given page
     */
    public static LinkBuilder forPage(final Page page, final boolean mappedPath) {
        return forPage(page, mappedPath, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param mappedPath if true, link path will be mapped through resource resolver
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final Page page, final boolean mappedPath, final TitleType titleType) {
        checkNotNull(page);

        final String title = page.getProperties().get(titleType.getPropertyName(), page.getTitle());

        return new LinkBuilder(getPagePath(page, mappedPath)).setTitle(title);
    }

    /**
     * Get a builder instance for a path.
     *
     * @param path content or external path
     * @return builder containing the given path
     */
    public static LinkBuilder forPath(final String path) {
        checkNotNull(path);

        return new LinkBuilder(path);
    }

    /**
     * Get a builder instance for a resource.
     *
     * @param resource resource
     * @return builder containing the path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource) {
        return forResource(resource, false);
    }

    /**
     * Get a builder instance for a resource using the mapped path on the returned builder.
     *
     * @param resource resource
     * @param mappedPath if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource, final boolean mappedPath) {
        checkNotNull(resource);

        final String path;

        if (mappedPath) {
            final ResourceResolver resourceResolver = resource.getResourceResolver();

            path = resourceResolver.map(resource.getPath());
        } else {
            path = resource.getPath();
        }

        return new LinkBuilder(path);
    }

    private static String getPagePath(final Page page, final boolean mapped) {
        final String redirect = page.getProperties().get(PropertyConstants.REDIRECT_TARGET, "");
        final String path = redirect.isEmpty() ? page.getPath() : redirect;

        final String result;

        if (mapped) {
            final ResourceResolver resourceResolver = page.adaptTo(Resource.class).getResourceResolver();

            result = resourceResolver.map(path);
        } else {
            result = path;
        }

        return result;
    }

    /**
     * Add a child link.  This is only applicable when building navigation links, returned by calling
     * <code>buildNavigationLink()</code>.
     *
     * @param child child navigation link instance
     * @return builder
     */
    public LinkBuilder addChild(final NavigationLink child) {
        children.add(checkNotNull(child));

        return this;
    }

    /**
     * Add a query parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @return builder
     */
    public LinkBuilder addParameter(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        parameters.put(name, value);

        return this;
    }

    /**
     * Add query parameters.
     *
     * @param parameters map of parameter names to their values
     * @return builder
     */
    public LinkBuilder addParameters(final Map<String, String> parameters) {
        this.parameters.putAll(Multimaps.forMap(checkNotNull(parameters)));

        return this;
    }

    /**
     * Add query parameters.
     *
     * @param parameters map of parameter names to their values
     * @return builder
     */
    public LinkBuilder addParameters(final SetMultimap<String, String> parameters) {
        this.parameters.putAll(checkNotNull(parameters));

        return this;
    }

    /**
     * Add properties (arbitrary map of properties that are stored on the returned link instance).
     *
     * @param properties map of properties names to their values
     * @return builder
     */
    public LinkBuilder addProperties(final Map<String, String> properties) {
        this.properties.putAll(checkNotNull(properties));

        return this;
    }

    /**
     * Add a property (arbitrary key-value pair stored on the returned link instance).
     *
     * @param name property name
     * @param value property value
     * @return builder
     */
    public LinkBuilder addProperty(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        properties.put(name, value);

        return this;
    }

    /**
     * Add a selector.
     *
     * @param selector selector value
     * @return builder
     */
    public LinkBuilder addSelector(final String selector) {
        selectors.add(checkNotNull(selector));

        return this;
    }

    /**
     * Add selectors.
     *
     * @param selectors list of selector values
     * @return builder
     */
    public LinkBuilder addSelectors(final List<String> selectors) {
        this.selectors.addAll(checkNotNull(selectors));

        return this;
    }

    /**
     * Build a link using the properties of the current builder.
     *
     * @return link
     */
    public Link build() {
        final StringBuilder builder = new StringBuilder();

        builder.append(buildHost());
        builder.append(buildPathWithSelectors());

        final String extension;

        if (path.contains(PathConstants.SELECTOR)) {
            extension = path.substring(path.indexOf(PathConstants.SELECTOR) + 1);
        } else {
            extension = this.extension == null ? PathConstants.EXTENSION_HTML : this.extension;

            if (!external && !extension.isEmpty()) {
                builder.append('.');
                builder.append(extension);
            }
        }

        final String queryString = buildQueryString();

        builder.append(queryString);

        final String href = builder.toString();

        LOG.debug("build() href = {}", href);

        return new DefaultLink(path, extension, href, selectors, queryString, external, target, title, properties);
    }

    /**
     * Build an image link using the properties of the current builder.  If <code>setImage()</code> was called on the
     * builder, this is the only method that will return a link containing the image source property.
     *
     * @return image link
     */
    public ImageLink buildImageLink() {
        final Link link = build();

        return new DefaultImageLink(link, imageSource);
    }

    /**
     * Build a navigation link using the properties of the current builder.  If <code>setActive()</code> or
     * <code>addChild()</code> was called on the builder, this is only method that will return a link containing an
     * active state and child links.
     *
     * @return builder
     */
    public NavigationLink buildNavigationLink() {
        final Link link = build();

        return new DefaultNavigationLink(link, active, children);
    }

    /**
     * Get the href property for the link being built.  This is a shortcut for calling <code>build().getHref()</code>.
     *
     * @return href for this link
     */
    public String getHref() {
        return build().getHref();
    }

    /**
     * Set the active state for the link.  This only applies to navigation links returned by calling
     * <code>buildNavigationLink()</code>.
     *
     * @param active active state
     * @return builder
     */
    public LinkBuilder setActive(final boolean active) {
        this.active = active;

        return this;
    }

    /**
     * Set the extension, without '.'.  Defaults to "html" if none is provided.
     *
     * @param extension link extension
     * @return builder
     */
    public LinkBuilder setExtension(final String extension) {
        this.extension = extension;

        return this;
    }

    /**
     * Set the host.  If the host is set, the href of the built link will be absolute rather than relative.
     *
     * @param host host name
     * @return builder
     */
    public LinkBuilder setHost(final String host) {
        this.host = host;

        return this;
    }

    /**
     * Set an image source.  This only applies to image links returned by calling <code>buildImageLink()</code>.
     *
     * @param imageSource
     * @return builder
     */
    public LinkBuilder setImageSource(final String imageSource) {
        this.imageSource = imageSource;

        return this;
    }

    /**
     * Set the port.
     *
     * @param port port number
     * @return builder
     */
    public LinkBuilder setPort(final int port) {
        this.port = port;

        return this;
    }

    /**
     * Set secure.  If true, the returned link will be "https" instead of "http".  This only applies when a host name is
     * set.
     *
     * @param secure secure
     * @return builder
     */
    public LinkBuilder setSecure(final boolean secure) {
        this.secure = secure;

        return this;
    }

    /**
     * Set the link target.
     *
     * @param target link target
     * @return builder
     */
    public LinkBuilder setTarget(final String target) {
        this.target = target;

        return this;
    }

    /**
     * Set the link title.
     *
     * @param title title
     * @return builder
     */
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

    private String buildQueryString() {
        final StringBuilder builder = new StringBuilder();

        if (!parameters.isEmpty()) {
            builder.append('?');

            for (final String name : parameters.keySet()) {
                final Set<String> values = parameters.get(name);

                for (final String value : values) {
                    try {
                        builder.append(URLEncoder.encode(name, UTF_8));
                        builder.append('=');
                        builder.append(URLEncoder.encode(value, UTF_8));
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
