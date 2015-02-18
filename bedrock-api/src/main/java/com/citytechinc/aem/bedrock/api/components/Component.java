package com.citytechinc.aem.bedrock.api.components;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.request.ComponentRequest;
import com.google.common.base.Optional;
import io.sightly.java.api.Use;

import java.util.List;

/**
 *
 */
public interface Component extends ComponentNode, Use {

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

    /**
     * Get the component request.
     *
     * @return component request
     */
    ComponentRequest getComponentRequest();

    /**
     * Get the current page.
     *
     * @return current page
     */
    PageDecorator getCurrentPage();

    /**
     * Get an OSGi service.
     *
     * @param serviceType the type (class) of the service
     * @param <T> type
     * @return the service instance, or null if it is not available
     */
    <T> T getService(final Class<T> serviceType);

    /**
     * Get one or more OSGi services.
     *
     * @param serviceType the type (class) of the service
     * @param filter optional filter, see OSGi spec for syntax details
     * @param <T> type
     * @return one or more service instances, or null if none are found
     */
    <T> List<T> getServices(final Class<T> serviceType, final String filter);


}
