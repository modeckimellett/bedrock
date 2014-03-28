/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.resource.predicates;

import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ResourceTypePredicate implements Predicate<Resource> {

    /**
     * sling:resourceType property value to filter on.
     */
    private final String resourceType;

    public ResourceTypePredicate(final String resourceType) {
        this.resourceType = checkNotNull(resourceType);
    }

    @Override
    public boolean apply(final Resource resource) {
        return resource != null && resourceType.equals(resource.getResourceType());
    }
}
