/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node.predicates;

import com.citytechinc.aem.bedrock.content.node.ComponentNode;
import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ComponentNodeResourceTypePredicate implements Predicate<ComponentNode> {

    /**
     * sling:resourceType property value to filter on.
     */
    private final String resourceType;

    public ComponentNodeResourceTypePredicate(final String resourceType) {
        this.resourceType = checkNotNull(resourceType);
    }

    @Override
    public boolean apply(final ComponentNode componentNode) {
        boolean result = false;

        if (componentNode != null) {
            result = resourceType.equals(componentNode.getResource().getResourceType());
        }

        return result;
    }
}
