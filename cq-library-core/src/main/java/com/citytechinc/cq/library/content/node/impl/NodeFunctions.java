/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl;

import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.node.ComponentNode;
import com.google.common.base.Function;
import org.apache.sling.api.resource.Resource;

/**
 * Functions for transforming to and from node decorators.
 */
public final class NodeFunctions {

    public static final Function<Resource, BasicNode> RESOURCE_TO_BASIC_NODE = new Function<Resource, BasicNode>() {
        @Override
        public BasicNode apply(final Resource resource) {
            return new DefaultBasicNode(resource);
        }
    };

    public static final Function<Resource, ComponentNode> RESOURCE_TO_COMPONENT_NODE = new Function<Resource, ComponentNode>() {
        @Override
        public ComponentNode apply(final Resource resource) {
            return new DefaultComponentNode(resource);
        }
    };

    public static final Function<BasicNode, ComponentNode> BASIC_NODE_TO_COMPONENT_NODE = new Function<BasicNode, ComponentNode>() {
        @Override
        public ComponentNode apply(final BasicNode basicNode) {
            return new DefaultComponentNode(basicNode.getResource());
        }
    };

    private NodeFunctions() {

    }
}
