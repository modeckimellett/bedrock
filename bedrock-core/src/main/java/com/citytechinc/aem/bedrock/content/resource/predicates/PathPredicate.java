/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.resource.predicates;

import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PathPredicate implements Predicate<Resource> {

    /**
     * JCR path to match against predicate input.
     */
    private final String path;

    public PathPredicate(final String path) {
        this.path = checkNotNull(path);
    }

    @Override
    public boolean apply(final Resource resource) {
        return resource != null && path.equals(resource.getPath());
    }
}
