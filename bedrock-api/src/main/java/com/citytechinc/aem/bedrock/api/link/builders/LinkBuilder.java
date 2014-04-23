/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.api.link.builders;

import com.citytechinc.aem.bedrock.api.link.ImageLink;
import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.NavigationLink;
import com.google.common.collect.SetMultimap;

import java.util.List;
import java.util.Map;

public interface LinkBuilder {

    Link build();

    ImageLink buildImageLink();

    NavigationLink buildNavigationLink();

    LinkBuilder addChild(NavigationLink child);

    LinkBuilder addParameter(String name, String value);

    LinkBuilder addParameters(Map<String, String> parameters);

    LinkBuilder addParameters(SetMultimap<String, String> parameters);

    LinkBuilder addProperties(Map<String, String> properties);

    LinkBuilder addProperty(String name, String value);

    LinkBuilder addSelector(String selector);

    LinkBuilder addSelectors(List<String> selectors);

    LinkBuilder setActive(boolean active);

    LinkBuilder setExtension(String extension);

    LinkBuilder setHost(String host);

    LinkBuilder setImageSource(String imageSource);

    LinkBuilder setPort(int port);

    LinkBuilder setSecure(boolean secure);

    LinkBuilder setSuffix(String suffix);

    LinkBuilder setTarget(String target);

    LinkBuilder setTitle(String title);
}
