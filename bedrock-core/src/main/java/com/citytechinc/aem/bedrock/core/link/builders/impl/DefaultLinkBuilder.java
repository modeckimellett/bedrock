package com.citytechinc.aem.bedrock.core.link.builders.impl;

import com.citytechinc.aem.bedrock.core.constants.PathConstants;
import com.citytechinc.aem.bedrock.core.constants.PropertyConstants;
import com.citytechinc.aem.bedrock.api.link.ImageLink;
import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.NavigationLink;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.link.enums.LinkTarget;
import com.citytechinc.aem.bedrock.core.link.impl.DefaultImageLink;
import com.citytechinc.aem.bedrock.core.link.impl.DefaultLink;
import com.citytechinc.aem.bedrock.core.link.impl.DefaultNavigationLink;
import com.citytechinc.aem.bedrock.api.page.enums.TitleType;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Charsets;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.citytechinc.aem.bedrock.core.utils.PathUtils.isExternal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.sling.api.resource.Resource.RESOURCE_TYPE_NON_EXISTING;

public final class DefaultLinkBuilder implements LinkBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLinkBuilder.class);

    private static final String UTF_8 = Charsets.UTF_8.name();

    private final List<NavigationLink> children = Lists.newArrayList();

    private final boolean external;

    private final SetMultimap<String, String> parameters = LinkedHashMultimap.create();

    private final String path;

    private final Map<String, String> properties = Maps.newHashMap();

    private final List<String> selectors = Lists.newArrayList();

    private boolean active;

    private String extension;

    private String host;

    private String imageSource = "";

    private int port;

    private boolean secure;

    private String suffix = "";

    private String target = LinkTarget.SELF.getTarget();

    private String title = "";

    private DefaultLinkBuilder(final ResourceResolver resourceResolver, final String path) {
        this.path = path;

        external = RESOURCE_TYPE_NON_EXISTING.equals(resourceResolver.resolve(path).getResourceType());
    }

    private DefaultLinkBuilder(final String path) {
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

        return new DefaultLinkBuilder(link.getPath()).setExtension(link.getExtension()).setTitle(link.getTitle()).setTarget(
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

        return new DefaultLinkBuilder(getPagePath(page, false)).setTitle(title);
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @param mapped if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given page
     */
    public static LinkBuilder forPage(final Page page, final boolean mapped) {
        return forPage(page, mapped, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param mapped if true, link path will be mapped through resource resolver
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final Page page, final boolean mapped, final TitleType titleType) {
        checkNotNull(page);

        final String title = page.getProperties().get(titleType.getPropertyName(), page.getTitle());

        return new DefaultLinkBuilder(getPagePath(page, mapped)).setTitle(title);
    }

    /**
     * Get a builder instance for a path.
     *
     * @param path content or external path
     * @return builder containing the given path
     */
    public static LinkBuilder forPath(final String path) {
        checkNotNull(path);

        return new DefaultLinkBuilder(path);
    }

    /**
     * Get a builder instance for a path, using strict resource resolution to determine if the path is external.  Links
     * will only be considered external if the given path does not resolve to a resource.
     *
     * @param resourceResolver resource resolver
     * @param path content or external path
     * @return builder containing the given path
     */
    public static LinkBuilder forPath(final ResourceResolver resourceResolver, final String path) {
        checkNotNull(resourceResolver);
        checkNotNull(path);

        return new DefaultLinkBuilder(resourceResolver, path);
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
     * @param mapped if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource, final boolean mapped) {
        checkNotNull(resource);

        final String path;

        if (mapped) {
            final ResourceResolver resourceResolver = resource.getResourceResolver();

            path = resourceResolver.map(resource.getPath());
        } else {
            path = resource.getPath();
        }

        return new DefaultLinkBuilder(path);
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

    @Override
    public LinkBuilder addChild(final NavigationLink child) {
        children.add(checkNotNull(child));

        return this;
    }

    @Override
    public LinkBuilder addParameter(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        parameters.put(name, value);

        return this;
    }

    @Override
    public LinkBuilder addParameters(final Map<String, String> parameters) {
        this.parameters.putAll(Multimaps.forMap(checkNotNull(parameters)));

        return this;
    }

    @Override
    public LinkBuilder addParameters(final SetMultimap<String, String> parameters) {
        this.parameters.putAll(checkNotNull(parameters));

        return this;
    }

    @Override
    public LinkBuilder addProperties(final Map<String, String> properties) {
        this.properties.putAll(checkNotNull(properties));

        return this;
    }

    @Override
    public LinkBuilder addProperty(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        properties.put(name, value);

        return this;
    }

    @Override
    public LinkBuilder addSelector(final String selector) {
        selectors.add(checkNotNull(selector));

        return this;
    }

    @Override
    public LinkBuilder addSelectors(final List<String> selectors) {
        this.selectors.addAll(checkNotNull(selectors));

        return this;
    }

    @Override
    public Link build() {
        final StringBuilder builder = new StringBuilder();

        builder.append(buildHost());
        builder.append(buildPathWithSelectors());

        final String extension;

        if (path.contains(PathConstants.SELECTOR)) {
            extension = path.substring(path.indexOf(PathConstants.SELECTOR) + 1);
        } else {
            if (external) {
                extension = "";
            } else {
                extension = this.extension == null ? PathConstants.EXTENSION_HTML : this.extension;

                if (!extension.isEmpty()) {
                    builder.append('.');
                    builder.append(extension);
                }
            }
        }

        builder.append(suffix);

        final String queryString = buildQueryString();

        builder.append(queryString);

        final String href = builder.toString();

        LOG.debug("build() href = {}", href);

        return new DefaultLink(path, extension, suffix, href, selectors, queryString, external, target, title,
            properties);
    }

    @Override
    public ImageLink buildImageLink() {
        final Link link = build();

        return new DefaultImageLink(link, imageSource);
    }

    @Override
    public NavigationLink buildNavigationLink() {
        final Link link = build();

        return new DefaultNavigationLink(link, active, children);
    }

    @Override
    public LinkBuilder setActive(final boolean active) {
        this.active = active;

        return this;
    }

    @Override
    public LinkBuilder setExtension(final String extension) {
        this.extension = extension;

        return this;
    }

    @Override
    public LinkBuilder setHost(final String host) {
        this.host = host;

        return this;
    }

    @Override
    public LinkBuilder setImageSource(final String imageSource) {
        this.imageSource = imageSource;

        return this;
    }

    @Override
    public LinkBuilder setPort(final int port) {
        this.port = port;

        return this;
    }

    @Override
    public LinkBuilder setSecure(final boolean secure) {
        this.secure = secure;

        return this;
    }

    @Override
    public LinkBuilder setSuffix(final String suffix) {
        this.suffix = suffix;

        return this;
    }

    @Override
    public LinkBuilder setTarget(final String target) {
        this.target = target;

        return this;
    }

    @Override
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
        return path + buildSelectors();
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
