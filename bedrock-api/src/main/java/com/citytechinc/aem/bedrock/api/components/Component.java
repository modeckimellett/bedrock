package com.citytechinc.aem.bedrock.api.components;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.google.common.base.Optional;

public interface Component extends ComponentNode {

    /**
     * Get a component instance for the given path.
     *
     * @param path absolute JCR path to the resource of the component
     * @param type component class type
     * @return component instance or absent <code>Optional</code> if path does not resolve to a resource
     */
    <T extends Component> Optional<T> getComponent(final String path, final Class<T> type);

    /**
     * Get a component instance for the given component node.
     *
     * @param componentNode component node representing resource of desired component
     * @param type component class type
     * @return component instance or null if an error occurs
     */
    <T extends Component> T getComponent(final ComponentNode componentNode, final Class<T> type);
}
