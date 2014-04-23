package com.citytechinc.aem.bedrock.core.node.impl;

import com.citytechinc.aem.bedrock.api.node.BasicNode;
import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.google.common.base.Function;
import org.apache.sling.api.resource.Resource;

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

    private NodeFunctions() {

    }
}
