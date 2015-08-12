package com.citytechinc.aem.bedrock.core.node.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodePropertyExistsPredicate
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodePropertyValuePredicate
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodeResourceTypePredicate
import com.day.cq.commons.DownloadResource
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap
import com.day.cq.commons.inherit.InheritanceValueMap
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.designer.Designer
import com.google.common.base.Function
import com.google.common.base.Objects
import com.google.common.base.Optional
import com.google.common.base.Predicate
import com.google.common.collect.FluentIterable
import com.google.common.collect.Maps
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j
import org.apache.sling.api.resource.Resource

import java.lang.reflect.Array

import static com.citytechinc.aem.bedrock.core.constants.ComponentConstants.DEFAULT_IMAGE_NAME
import static com.citytechinc.aem.bedrock.core.link.impl.LinkFunctions.LINK_TO_HREF
import static com.citytechinc.aem.bedrock.core.node.impl.NodeFunctions.RESOURCE_TO_BASIC_NODE
import static com.citytechinc.aem.bedrock.core.node.impl.NodeFunctions.RESOURCE_TO_COMPONENT_NODE
import static com.google.common.base.Preconditions.checkNotNull

@Slf4j("LOG")
@EqualsAndHashCode(includes = "path")
final class DefaultComponentNode extends AbstractNode implements ComponentNode {

    @Delegate
    private final BasicNode basicNode

    private final InheritanceValueMap properties

    DefaultComponentNode(Resource resource) {
        super(resource)

        basicNode = new DefaultBasicNode(resource)
        properties = new HierarchyNodeInheritanceValueMap(resource)
    }

    @Override
    Optional<ComponentNode> findAncestor(Predicate<ComponentNode> predicate) {
        findAncestorForPredicate(predicate)
    }

    @Override
    List<ComponentNode> findDescendants(Predicate<ComponentNode> predicate) {
        def descendantNodes = []

        componentNodes.each { componentNode ->
            if (predicate.apply(componentNode)) {
                descendantNodes.add(componentNode)
            }

            descendantNodes.addAll(componentNode.findDescendants(predicate))
        }

        descendantNodes
    }

    @Override
    Optional<ComponentNode> findAncestorWithProperty(String propertyName) {
        findAncestorForPredicate(new ComponentNodePropertyExistsPredicate(propertyName))
    }

    @Override
    <V> Optional<ComponentNode> findAncestorWithPropertyValue(String propertyName, V propertyValue) {
        findAncestorForPredicate(new ComponentNodePropertyValuePredicate<V>(propertyName, propertyValue))
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName) {
        getAsHrefInherited(propertyName, false)
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict) {
        getAsHrefInherited(propertyName, strict, false)
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict, boolean mapped) {
        getAsLinkInherited(propertyName, strict, mapped).transform(LINK_TO_HREF)
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName) {
        getAsLinkInherited(propertyName, false)
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict) {
        getAsLinkInherited(propertyName, strict, false)
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict, boolean mapped) {
        getLinkOptional(getInherited(propertyName, String.class), strict, mapped)
    }

    @Override
    <T> List<T> getAsListInherited(String propertyName, Class<T> type) {
        properties.getInherited(checkNotNull(propertyName), Array.newInstance(type, 0)) as List
    }

    @Override
    Optional<PageDecorator> getAsPageInherited(String propertyName) {
        getPageOptional(properties.getInherited(checkNotNull(propertyName), ""))
    }

    @Override
    <AdapterType> Optional<AdapterType> getAsTypeInherited(String propertyName, Class<AdapterType> type) {
        getAsTypeOptional(properties.getInherited(checkNotNull(propertyName), ""), type)
    }

    @Override
    Optional<ComponentNode> getComponentNode(String relativePath) {
        Optional.fromNullable(resource.getChild(checkNotNull(relativePath))).transform(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    List<ComponentNode> getComponentNodes() {
        FluentIterable.from(resource.children).transform(RESOURCE_TO_COMPONENT_NODE).toList()
    }

    @Override
    List<ComponentNode> getComponentNodes(Predicate<ComponentNode> predicate) {
        checkNotNull(predicate)

        FluentIterable.from(resource.children).transform(RESOURCE_TO_COMPONENT_NODE).filter(predicate).toList()
    }

    @Override
    List<ComponentNode> getComponentNodes(String relativePath) {
        def child = resource.getChild(checkNotNull(relativePath))
        def nodes

        if (child) {
            nodes = FluentIterable.from(child.children).transform(RESOURCE_TO_COMPONENT_NODE).toList()
        } else {
            nodes = Collections.emptyList()
        }

        nodes
    }

    @Override
    List<ComponentNode> getComponentNodes(String relativePath, String resourceType) {
        getComponentNodes(relativePath, new ComponentNodeResourceTypePredicate(resourceType))
    }

    @Override
    List<ComponentNode> getComponentNodes(String relativePath, Predicate<ComponentNode> predicate) {
        FluentIterable.from(getComponentNodes(checkNotNull(relativePath))).filter(checkNotNull(predicate)).toList()
    }

    @Override
    Optional<BasicNode> getDesignNode() {
        def resourceResolver = resource.resourceResolver
        def style = resourceResolver.adaptTo(Designer).getStyle(resource)
        def styleResource = resourceResolver.getResource(style.getPath())

        Optional.fromNullable(styleResource).transform(RESOURCE_TO_BASIC_NODE)
    }

    @Override
    Optional<String> getImageReferenceInherited() {
        getImageReferenceInherited(DEFAULT_IMAGE_NAME)
    }

    @Override
    Optional<String> getImageReferenceInherited(String name) {
        Optional.fromNullable(properties.getInherited("$name/${DownloadResource.PN_REFERENCE}", String))
    }

    @Override
    Optional<String> getImageSourceInherited() {
        getImageSourceInherited(null)
    }

    @Override
    Optional<String> getImageSourceInherited(int width) {
        getImageSourceInherited(null, width)
    }

    @Override
    Optional<String> getImageSourceInherited(String name) {
        getImageSourceInherited(name, -1)
    }

    @Override
    Optional<String> getImageSourceInherited(String name, int width) {
        def predicate = new Predicate<ComponentNode>() {
            @Override
            boolean apply(ComponentNode componentNode) {
                if (name) {
                    componentNode.isHasImage(name)
                } else {
                    componentNode.isHasImage()
                }
            }
        }

        findAncestor(predicate).transform(new Function<ComponentNode, String>() {
            @Override
            String apply(ComponentNode componentNode) {
                componentNode.getImageSource(name, width).get()
            }
        })
    }

    @Override
    <T> T getInherited(String propertyName, T defaultValue) {
        properties.getInherited(propertyName, defaultValue)
    }

    @Override
    <T> Optional<T> getInherited(String propertyName, Class<T> type) {
        Optional.fromNullable(properties.getInherited(propertyName, type))
    }

    @Override
    Optional<BasicNode> getNodeInherited(String relativePath) {
        findChildResourceInherited(relativePath).transform(RESOURCE_TO_BASIC_NODE)
    }

    @Override
    List<BasicNode> getNodesInherited(String relativePath) {
        def childOptional = findChildResourceInherited(relativePath)

        def nodes

        if (childOptional.present) {
            nodes = FluentIterable.from(childOptional.get().children).transform(RESOURCE_TO_BASIC_NODE).toList()
        } else {
            nodes = Collections.emptyList()
        }

        nodes
    }

    @Override
    Optional<ComponentNode> getParent() {
        Optional.fromNullable(resource.parent).transform(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    String toString() {
        Objects.toStringHelper(this).add("path", getPath()).add("properties", Maps.newHashMap(asMap())).toString()
    }

    // internals

    private Optional<ComponentNode> findAncestorForPredicate(Predicate<ComponentNode> predicate) {
        def pageManager = resource.resourceResolver.adaptTo(PageManagerDecorator)
        def containingPage = pageManager.getContainingPage(resource)

        def path = resource.path

        def relativePath = resource.name.equals(JcrConstants.JCR_CONTENT) ? "" : path.substring(
            containingPage.contentResource.path.length() + 1)

        LOG.debug("relative path = {}", relativePath)

        def componentNodeFunction = new Function<PageDecorator, Optional<ComponentNode>>() {
            @Override
            Optional<ComponentNode> apply(PageDecorator page) {
                relativePath.isEmpty() ? page.componentNode : page.getComponentNode(relativePath)
            }
        }

        def pagePredicate = new Predicate<PageDecorator>() {
            @Override
            boolean apply(PageDecorator page) {
                def componentNodeOptional = componentNodeFunction.apply(page)

                componentNodeOptional.present && predicate.apply(componentNodeOptional.get())
            }
        }

        containingPage.findAncestor(pagePredicate).transform(new Function<PageDecorator, ComponentNode>() {
            @Override
            ComponentNode apply(PageDecorator page) {
                componentNodeFunction.apply(page).get()
            }
        })
    }

    private Optional<Resource> findChildResourceInherited(String relativePath) {
        def pageManager = resource.resourceResolver.adaptTo(PageManagerDecorator)
        def containingPage = pageManager.getContainingPage(resource)

        def builder = new StringBuilder()

        if (resource.name == JcrConstants.JCR_CONTENT) {
            builder.append(relativePath)
        } else {
            builder.append(resource.path.substring(containingPage.contentResource.path.length() + 1))
            builder.append('/')
            builder.append(relativePath)
        }

        // path relative to jcr:content
        def resourcePath = builder.toString()

        LOG.debug("child resource relative path = {}", resourcePath)

        def predicate = new Predicate<PageDecorator>() {
            @Override
            boolean apply(PageDecorator page) {
                page.getContentResource(resourcePath)
            }
        }

        containingPage.findAncestor(predicate).transform(new Function<PageDecorator, Resource>() {
            @Override
            Resource apply(PageDecorator page) {
                page.getContentResource(resourcePath)
            }
        })
    }
}
