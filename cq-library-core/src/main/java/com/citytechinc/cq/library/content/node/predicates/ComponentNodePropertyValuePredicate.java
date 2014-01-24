/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.predicates;

import com.citytechinc.cq.library.content.node.ComponentNode;
import com.google.common.base.Predicate;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ComponentNodePropertyValuePredicate<T> implements Predicate<ComponentNode> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentNodePropertyValuePredicate.class);

    private final String propertyName;

    private final T propertyValue;

    public ComponentNodePropertyValuePredicate(final String propertyName, final T propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = checkNotNull(propertyValue);
    }

    @Override
    public boolean apply(final ComponentNode componentNode) {
        checkNotNull(componentNode);

        final boolean result;

        final ValueMap properties = componentNode.asMap();

        if (properties.containsKey(propertyName)) {
            result = properties.get(propertyName, propertyValue.getClass()).equals(propertyValue);

            LOG.debug("apply() property name = {}, value = {}, result = {} for component node = {}",
                new Object[]{ propertyName, propertyValue, result, componentNode });
        } else {
            LOG.debug("apply() property name = {}, does not exist for component node = {}", propertyName,
                componentNode);

            result = false;
        }

        return result;
    }
}
