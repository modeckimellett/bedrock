package com.citytechinc.aem.bedrock.core.components;

import com.citytechinc.aem.bedrock.api.link.Link;
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.api.node.BasicNode;
import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.request.ComponentRequest;
import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import io.sightly.java.api.Use;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.List;

import static com.citytechinc.aem.bedrock.core.bindings.ComponentBindings.COMPONENT_NODE;
import static com.citytechinc.aem.bedrock.core.bindings.ComponentBindings.COMPONENT_REQUEST;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.sling.api.scripting.SlingBindings.RESOURCE;
import static org.apache.sling.api.scripting.SlingBindings.SLING;

/**
 * Base class for AEM component classes instantiated by the {@link com.citytechinc.aem.bedrock.core.tags.ComponentTag} or
 * implemented with Sightly.
 */
@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, isGetterVisibility = NONE)
public abstract class AbstractComponent implements ComponentNode, Use {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponent.class);

    private static final String PRECONDITIONS_ERROR_MESSAGE = "component has not been initialized";

    private ComponentRequest componentRequest;

    private ComponentNode componentNode;

    private Bindings bindings;

    private SlingScriptHelper sling;

    /**
     * Initialize this component.  This default implementation does nothing; subclasses should override this method to
     * provide additional initialization behavior.
     *
     * @param request component request
     */
    @SuppressWarnings("unused")
    public void init(final ComponentRequest request) {

    }

    @Override
    public final void init(final Bindings bindings) {
        this.bindings = bindings;

        componentRequest = (ComponentRequest) bindings.get(COMPONENT_REQUEST);
        componentNode = (ComponentNode) bindings.get(COMPONENT_NODE);
        sling = (SlingScriptHelper) bindings.get(SLING);

        init(componentRequest);
    }

    /**
     * Get a component instance for the given path.
     *
     * @param path absolute JCR path to the resource of the component
     * @param type component class type
     * @return component instance or null if an error occurs
     */
    public <T extends AbstractComponent> Optional<T> getComponent(final String path, final Class<T> type) {
        return getComponentForResource(checkNotNull(componentRequest, PRECONDITIONS_ERROR_MESSAGE).getResourceResolver()
            .getResource(path), type);
    }

    /**
     * Get a component instance for the given component node.
     *
     * @param componentNode component node representing resource of desired component
     * @param type component class type
     * @return component instance or null if an error occurs
     */
    public <T extends AbstractComponent> Optional<T> getComponent(final ComponentNode componentNode,
        final Class<T> type) {
        return getComponentForResource(componentNode.getResource(), type);
    }

    /**
     * Get the component request.
     *
     * @return component request
     */
    public ComponentRequest getComponentRequest() {
        return checkNotNull(componentRequest, PRECONDITIONS_ERROR_MESSAGE);
    }

    /**
     * Get the current page.
     *
     * @return current page
     */
    public PageDecorator getCurrentPage() {
        return checkNotNull(componentRequest, PRECONDITIONS_ERROR_MESSAGE).getCurrentPage();
    }

    /**
     * Get an OSGi service.
     *
     * @param serviceType the type (class) of the service
     * @param <T> type
     * @return the service instance, or null if it is not available
     */
    public final <T> T getService(final Class<T> serviceType) {
        return checkNotNull(sling, PRECONDITIONS_ERROR_MESSAGE).getService(serviceType);
    }

    /**
     * Get one or more OSGi services.
     *
     * @param serviceType the type (class) of the service
     * @param filter optional filter, see OSGi spec for syntax details
     * @param <T> type
     * @return one or more service instances, or null if none are found
     */
    @SuppressWarnings("unchecked")
    public final <T> List<T> getServices(final Class<T> serviceType, final String filter) {
        return (List<T>) ImmutableList.of(checkNotNull(sling, PRECONDITIONS_ERROR_MESSAGE).getServices(serviceType,
            filter));
    }

    // delegate methods

    @Override
    public final ValueMap asMap() {
        return getComponentNode().asMap();
    }

    @Override
    public final Optional<ComponentNode> findAncestor(final Predicate<ComponentNode> predicate) {
        return getComponentNode().findAncestor(predicate);
    }

    @Override
    public final List<ComponentNode> findDescendants(final Predicate<ComponentNode> predicate) {
        return getComponentNode().findDescendants(predicate);
    }

    @Override
    public final Optional<ComponentNode> findAncestorWithProperty(final String propertyName) {
        return getComponentNode().findAncestorWithProperty(propertyName);
    }

    @Override
    public final <T> Optional<ComponentNode> findAncestorWithPropertyValue(final String propertyName,
        final T propertyValue) {
        return getComponentNode().findAncestorWithPropertyValue(propertyName, propertyValue);
    }

    @Override
    public final <T> T get(final String name, final T defaultValue) {
        return getComponentNode().get(name, defaultValue);
    }

    @Override
    public final <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return getComponentNode().get(propertyName, type);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName) {
        return getComponentNode().getAsHref(propertyName);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return getComponentNode().getAsHref(propertyName, strict);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return getComponentNode().getAsHref(propertyName, strict, mapped);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName) {
        return getComponentNode().getAsHrefInherited(propertyName);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName, final boolean strict) {
        return getComponentNode().getAsHrefInherited(propertyName, strict);
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName, final boolean strict,
        final boolean mapped) {
        return getComponentNode().getAsHrefInherited(propertyName, strict, mapped);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName) {
        return getComponentNode().getAsLink(propertyName);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return getComponentNode().getAsLink(propertyName, strict);
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return getComponentNode().getAsLink(propertyName, strict, mapped);
    }

    @Override
    public final <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return getComponentNode().getAsList(propertyName, type);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName) {
        return getComponentNode().getAsLinkInherited(propertyName);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return getComponentNode().getAsLinkInherited(propertyName, strict);
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict,
        final boolean mapped) {
        return getComponentNode().getAsLinkInherited(propertyName, strict, mapped);
    }

    @Override
    public final <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return getComponentNode().getAsListInherited(propertyName, type);
    }

    @Override
    public final Optional<PageDecorator> getAsPage(final String propertyName) {
        return getComponentNode().getAsPage(propertyName);
    }

    @Override
    public final <AdapterType> Optional<AdapterType> getAsType(final String propertyName, final Class<AdapterType> type) {
        return getComponentNode().getAsType(propertyName, type);
    }

    @Override
    public final Optional<PageDecorator> getAsPageInherited(final String propertyName) {
        return getComponentNode().getAsPageInherited(propertyName);
    }

    @Override
    public final <AdapterType> Optional<AdapterType> getAsTypeInherited(final String propertyName,
        final Class<AdapterType> type) {
        return getComponentNode().getAsTypeInherited(propertyName, type);
    }

    @Override
    public final Optional<ComponentNode> getComponentNode(final String relativePath) {
        return getComponentNode().getComponentNode(relativePath);
    }

    @Override
    public final List<ComponentNode> getComponentNodes() {
        return getComponentNode().getComponentNodes();
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final Predicate<ComponentNode> predicate) {
        return getComponentNode().getComponentNodes(predicate);
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final String relativePath) {
        return getComponentNode().getComponentNodes(relativePath);
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final String parsysName, final String resourceType) {
        return getComponentNode().getComponentNodes(parsysName, resourceType);
    }

    @Override
    public final List<ComponentNode> getComponentNodes(final String relativePath,
        final Predicate<ComponentNode> predicate) {
        return getComponentNode().getComponentNodes(relativePath, predicate);
    }

    @Override
    public final Optional<BasicNode> getDesignNode() {
        return getComponentNode().getDesignNode();
    }

    /**
     * Get the path of the current component with a ".html" extension.  This method can be overridden to return an
     * implementation-specific value.
     *
     * @return href of the current component
     */
    @Override
    public String getHref() {
        return getComponentNode().getHref();
    }

    @Override
    public final String getHref(final boolean mapped) {
        return getComponentNode().getHref(mapped);
    }

    @Override
    public final String getId() {
        return getComponentNode().getId();
    }

    @Override
    public final Optional<String> getImageReference() {
        return getComponentNode().getImageReference();
    }

    @Override
    public final Optional<String> getImageReference(final String name) {
        return getComponentNode().getImageReference(name);
    }

    @Override
    public final Optional<String> getImageReferenceInherited() {
        return getComponentNode().getImageReferenceInherited();
    }

    @Override
    public final Optional<String> getImageReferenceInherited(final String name) {
        return getComponentNode().getImageReferenceInherited(name);
    }

    @Override
    public final Optional<String> getImageRendition(final String renditionName) {
        return getComponentNode().getImageRendition(renditionName);
    }

    @Override
    public final Optional<String> getImageRendition(final String name, final String renditionName) {
        return getComponentNode().getImageRendition(name, renditionName);
    }

    @Override
    public final Optional<String> getImageSource() {
        return getComponentNode().getImageSource();
    }

    @Override
    public final Optional<String> getImageSource(final int width) {
        return getComponentNode().getImageSource(width);
    }

    @Override
    public final Optional<String> getImageSource(final String name) {
        return getComponentNode().getImageSource(name);
    }

    @Override
    public final Optional<String> getImageSource(final String name, final int width) {
        return getComponentNode().getImageSource(name, width);
    }

    @Override
    public final Optional<String> getImageSourceInherited() {
        return getComponentNode().getImageSourceInherited();
    }

    @Override
    public final Optional<String> getImageSourceInherited(final int width) {
        return getComponentNode().getImageSourceInherited(width);
    }

    @Override
    public final Optional<String> getImageSourceInherited(final String name) {
        return getComponentNode().getImageSourceInherited(name);
    }

    @Override
    public final Optional<String> getImageSourceInherited(final String name, final int width) {
        return getComponentNode().getImageSourceInherited(name, width);
    }

    @Override
    public final int getIndex() {
        return getComponentNode().getIndex();
    }

    @Override
    public final int getIndex(final String resourceType) {
        return getComponentNode().getIndex(resourceType);
    }

    @Override
    public final <T> T getInherited(final String propertyName, final T defaultValue) {
        return getComponentNode().getInherited(propertyName, defaultValue);
    }

    @Override
    public final <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return getComponentNode().getInherited(propertyName, type);
    }

    /**
     * Get a link object to the path for the current component.  This method can be overridden to modify the default
     * behavior and return a different <code>Link</code> instance or implementation.
     *
     * @return link containing the path for the current component
     */
    @Override
    public Link getLink() {
        return getComponentNode().getLink();
    }

    @Override
    public final Link getLink(final boolean mapped) {
        return getComponentNode().getLink(mapped);
    }

    @Override
    public final LinkBuilder getLinkBuilder() {
        return getComponentNode().getLinkBuilder();
    }

    @Override
    public final LinkBuilder getLinkBuilder(final boolean mapped) {
        return getComponentNode().getLinkBuilder(mapped);
    }

    @Override
    public final Optional<Node> getNode() {
        return getComponentNode().getNode();
    }

    @Override
    public final List<BasicNode> getNodesInherited(final String relativePath) {
        return getComponentNode().getNodesInherited(relativePath);
    }

    @Override
    public final Optional<ComponentNode> getParent() {
        return getComponentNode().getParent();
    }

    @Override
    public final String getPath() {
        return getComponentNode().getPath();
    }

    @Override
    public final List<Property> getProperties(final Predicate<Property> predicate) {
        return getComponentNode().getProperties(predicate);
    }

    @Override
    public final Resource getResource() {
        return getComponentNode().getResource();
    }

    @Override
    public final boolean isHasImage() {
        return getComponentNode().isHasImage();
    }

    @Override
    public final boolean isHasImage(final String name) {
        return getComponentNode().isHasImage(name);
    }

    // internals

    private ComponentNode getComponentNode() {
        return checkNotNull(componentNode, PRECONDITIONS_ERROR_MESSAGE);
    }

    private <T extends AbstractComponent> Optional<T> getComponentForResource(final Resource resource,
        final Class<T> type) {
        T instance = null;

        if (resource != null) {
            final Bindings bindings = new SimpleBindings(checkNotNull(this.bindings, PRECONDITIONS_ERROR_MESSAGE));

            bindings.put(RESOURCE, resource);

            final ComponentBindings componentBindings = new ComponentBindings(bindings);

            try {
                instance = type.newInstance();

                instance.init(componentBindings);
            } catch (InstantiationException e) {
                LOG.error("error instantiating component for type = " + type, e);
            } catch (IllegalAccessException e) {
                LOG.error("error instantiating component for type = " + type, e);
            }
        }

        return Optional.fromNullable(instance);
    }
}
