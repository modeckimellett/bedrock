/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.node.predicates;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ComponentNodePropertyExistsPredicate implements Predicate<ComponentNode> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentNodePropertyExistsPredicate.class);

    private final String propertyName;

    public ComponentNodePropertyExistsPredicate(final String propertyName) {
        this.propertyName = checkNotNull(propertyName);
    }

    @Override
    public boolean apply(final ComponentNode componentNode) {
        boolean result = false;

        if (componentNode != null) {
            final Optional<Node> nodeOptional = componentNode.getNode();

            if (nodeOptional.isPresent()) {
                try {
                    result = nodeOptional.get().hasProperty(propertyName);
                } catch (RepositoryException e) {
                    LOG.error("error checking property existence for component node", e);
                }
            }
        }

        return result;
    }
}
