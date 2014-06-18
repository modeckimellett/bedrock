package com.citytechinc.aem.bedrock.core.link.builders.impl;

import com.citytechinc.aem.bedrock.api.link.ImageLink;
import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.NavigationLink;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.link.enums.LinkTarget;
import com.citytechinc.aem.bedrock.core.constants.PathConstants;
import com.citytechinc.aem.bedrock.core.link.impl.DefaultImageLink;
import com.citytechinc.aem.bedrock.core.link.impl.DefaultLink;
import com.citytechinc.aem.bedrock.core.link.impl.DefaultNavigationLink;
import com.citytechinc.aem.bedrock.core.utils.PathUtils;
import com.google.common.base.Charsets;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class DefaultLinkBuilder implements LinkBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLinkBuilder.class);

    private static final String UTF_8 = Charsets.UTF_8.name();

    private final SetMultimap<String, String> parameters = LinkedHashMultimap.create();

    private final String path;

    private final Map<String, String> properties = Maps.newHashMap();

    private final List<String> selectors = Lists.newArrayList();

    private final List<NavigationLink> children = Lists.newArrayList();

    // initialized with default values

    private boolean isExternal = false;

    private boolean isActive = false;

    private String extension = null;

    private String host = null;

    private String imageSource = "";

    private int port = 0;

    private boolean secure = false;

    private String suffix = "";

    private String target = LinkTarget.SELF.getTarget();

    private String title = "";

    public DefaultLinkBuilder(final String path) {
        this.path = path;

        isExternal = PathUtils.isExternal(path);
    }

    @Override
    public LinkBuilder addChild(final NavigationLink child) {
        children.add(checkNotNull(child));

        return this;
    }

    @Override
    public LinkBuilder addParameter(final String name, final String value) {
        parameters.put(checkNotNull(name), checkNotNull(value));

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
        properties.put(checkNotNull(name), checkNotNull(value));

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
        builder.append(path);
        builder.append(buildSelectors());

        final String extension;

        if (path.contains(PathConstants.SELECTOR)) {
            extension = path.substring(path.indexOf(PathConstants.SELECTOR) + 1);
        } else {
            if (isExternal) {
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

        LOG.debug("building href = {}", href);

        return new DefaultLink(path, extension, suffix, href, selectors, queryString, isExternal, target, title,
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

        return new DefaultNavigationLink(link, isActive, children);
    }

    @Override
    public LinkBuilder setActive(final boolean isActive) {
        this.isActive = isActive;

        return this;
    }

    @Override
    public LinkBuilder setExtension(final String extension) {
        this.extension = extension;

        return this;
    }

    @Override
    public LinkBuilder setExternal(final boolean isExternal) {
        this.isExternal = isExternal;

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
    public LinkBuilder setSecure(final boolean isSecure) {
        this.secure = isSecure;

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

        if (!isExternal && isNotEmpty(host)) {
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

        if (!isExternal) {
            for (final String selector : selectors) {
                builder.append('.');
                builder.append(selector);
            }
        }

        return builder.toString();
    }
}
