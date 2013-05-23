/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.predicates;

import com.citytechinc.cq.library.content.node.BasicNode;
import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BasicNodeResourceTypePredicate implements Predicate<BasicNode> {

    /**
     * sling:resourceType property value to filter on.
     */
    private final String resourceType;

    public BasicNodeResourceTypePredicate(final String resourceType) {
        this.resourceType = checkNotNull(resourceType);
    }

    @Override
    public boolean apply(final BasicNode basicNode) {
        checkNotNull(basicNode);

        final Resource resource = basicNode.getResource();

        return resourceType.equals(resource.getResourceType());
    }
}
