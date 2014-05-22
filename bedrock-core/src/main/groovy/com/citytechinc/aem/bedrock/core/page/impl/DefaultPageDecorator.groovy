package com.citytechinc.aem.bedrock.core.page.impl

import com.citytechinc.aem.bedrock.api.link.ImageLink
import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.link.NavigationLink
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder
import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.api.page.enums.TitleType
import com.citytechinc.aem.bedrock.core.link.builders.impl.DefaultLinkBuilder
import com.citytechinc.aem.bedrock.core.node.impl.DefaultComponentNode
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodePropertyExistsPredicate
import com.citytechinc.aem.bedrock.core.node.predicates.ComponentNodePropertyValuePredicate
import com.day.cq.commons.Filter
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.google.common.base.Optional
import com.google.common.base.Predicate
import com.google.common.base.Predicates
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ValueMap

import static com.citytechinc.aem.bedrock.core.node.impl.NodeFunctions.RESOURCE_TO_COMPONENT_NODE
import static com.google.common.base.Preconditions.checkNotNull

@ToString(includes = ["path", "title"])
@EqualsAndHashCode(includes = "path")
final class DefaultPageDecorator implements PageDecorator {

    private static final Predicate<PageDecorator> ALL = Predicates.alwaysTrue()

    private static final def ALL_PAGES = new Filter<Page>() {
        @Override
        boolean includes(Page page) {
            true
        }
    }

    private static final def DISPLAYABLE_ONLY = new Predicate<PageDecorator>() {
        @Override
        boolean apply(PageDecorator page) {
            page.contentResource && !page.hideInNav
        }
    }

    @Delegate
    private final Page delegate

    private final Optional<ComponentNode> componentNodeOptional

    DefaultPageDecorator(Page page) {
        this.delegate = page

        componentNodeOptional = Optional.fromNullable(page.contentResource).transform(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    @SuppressWarnings("unchecked")
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == BasicNode || type == ComponentNode) {
            def resource = delegate.contentResource

            result = !resource ? null : (AdapterType) new DefaultComponentNode(resource)
        } else {
            result = delegate.adaptTo(type)
        }

        result
    }

    @Override
    ValueMap asMap() {
        getInternal({ componentNode -> componentNode.asMap() }, ValueMap.EMPTY)
    }

    @Override
    public <T> T get(String propertyName, T defaultValue) {
        getInternal({ componentNode -> componentNode.get(propertyName, defaultValue) }, defaultValue)
    }

    @Override
    public <T> Optional<T> get(String propertyName, Class<T> type) {
        getInternal({ componentNode -> componentNode.get(propertyName, type) }, Optional.absent())
    }

    @Override
    Optional<String> getAsHref(String propertyName) {
        getInternal({ componentNode -> componentNode.getAsHref(propertyName) }, Optional.absent())
    }

    @Override
    Optional<String> getAsHref(String propertyName, boolean strict) {
        getInternal({ componentNode -> componentNode.getAsHref(propertyName, strict) }, Optional.absent())
    }

    @Override
    Optional<String> getAsHref(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentNode -> componentNode.getAsHref(propertyName, strict, mapped) }, Optional.absent())
    }

    @Override
    Optional<Link> getAsLink(String propertyName) {
        getInternal({ componentNode -> componentNode.getAsLink(propertyName) }, Optional.absent())
    }

    @Override
    Optional<Link> getAsLink(String propertyName, boolean strict) {
        getInternal({ componentNode -> componentNode.getAsLink(propertyName, strict) }, Optional.absent())
    }

    @Override
    Optional<Link> getAsLink(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentNode -> componentNode.getAsLink(propertyName, strict, mapped) }, Optional.absent())
    }

    @Override
    public <T> List<T> getAsList(String propertyName, Class<T> type) {
        getInternal({ componentNode -> componentNode.getAsList(propertyName, type) }, Collections.emptyList())
    }

    @Override
    Optional<PageDecorator> getAsPage(String propertyName) {
        getInternal({ componentNode -> componentNode.getAsPage(propertyName) }, Optional.absent())
    }

    @Override
    Optional<String> getImageReference() {
        getInternal({ componentNode -> componentNode.imageReference }, Optional.absent())
    }

    @Override
    Optional<String> getImageReference(String name) {
        getInternal({ componentNode -> componentNode.getImageReference(name) }, Optional.absent())
    }

    @Override
    Optional<String> getImageRendition(String renditionName) {
        getInternal({ componentNode -> componentNode.getImageRendition(renditionName) }, Optional.absent())
    }

    @Override
    Optional<String> getImageRendition(String name, String renditionName) {
        getInternal({ componentNode -> componentNode.getImageRendition(name, renditionName) }, Optional.absent())
    }

    @Override
    Optional<String> getImageSource() {
        getInternal({ componentNode -> componentNode.imageSource }, Optional.absent())
    }

    @Override
    Optional<String> getImageSource(int width) {
        getInternal({ componentNode -> componentNode.getImageSource(width) }, Optional.absent())
    }

    @Override
    Optional<String> getImageSource(String name) {
        getInternal({ componentNode -> componentNode.getImageSource(name) }, Optional.absent())
    }

    @Override
    Optional<String> getImageSource(String name, int width) {
        getInternal({ componentNode -> componentNode.getImageSource(name, width) }, Optional.absent())
    }

    @Override
    public <T> T getInherited(String propertyName, T defaultValue) {
        getInternal({ componentNode -> componentNode.getInherited(propertyName, defaultValue) }, defaultValue)
    }

    @Override
    public <T> Optional<T> getInherited(String propertyName, Class<T> type) {
        getInternal({ componentNode -> componentNode.getInherited(propertyName, type) }, Optional.absent())
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName) {
        getInternal({ componentNode -> componentNode.getAsHrefInherited(propertyName) }, Optional.absent())
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict) {
        getInternal({ componentNode -> componentNode.getAsHrefInherited(propertyName, strict) }, Optional.absent())
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentNode -> componentNode.getAsHrefInherited(propertyName, strict, mapped) }, Optional.absent())
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName) {
        getInternal({ componentNode -> componentNode.getAsLinkInherited(propertyName) }, Optional.absent())
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict) {
        getInternal({ componentNode -> componentNode.getAsLinkInherited(propertyName, strict) }, Optional.absent())
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentNode -> componentNode.getAsLinkInherited(propertyName, strict, mapped) }, Optional.absent())
    }

    @Override
    public <T> List<T> getAsListInherited(String propertyName, Class<T> type) {
        getInternal({ componentNode -> componentNode.getAsListInherited(propertyName, type) }, Collections.emptyList())
    }

    @Override
    Optional<PageDecorator> getAsPageInherited(String propertyName) {
        getInternal({ componentNode -> componentNode.getAsPageInherited(propertyName) }, Optional.absent())
    }

    @Override
    Optional<String> getImageReferenceInherited() {
        getInternal({ componentNode -> componentNode.imageReferenceInherited }, Optional.absent())
    }

    @Override
    Optional<String> getImageReferenceInherited(String name) {
        getInternal({ componentNode -> componentNode.getImageReferenceInherited(name) }, Optional.absent())
    }

    @Override
    Optional<String> getImageSourceInherited() {
        getInternal({ componentNode -> componentNode.imageSourceInherited }, Optional.absent())
    }

    @Override
    Optional<String> getImageSourceInherited(int width) {
        getInternal({ componentNode -> componentNode.getImageSourceInherited(width) }, Optional.absent())
    }

    @Override
    Optional<String> getImageSourceInherited(String name) {
        getInternal({ componentNode -> componentNode.getImageSourceInherited(name) }, Optional.absent())
    }

    @Override
    Optional<String> getImageSourceInherited(String name, int width) {
        getInternal({ componentNode -> componentNode.getImageSourceInherited(name, width) }, Optional.absent())
    }

    @Override
    Optional<PageDecorator> findAncestor(Predicate<PageDecorator> predicate) {
        PageDecorator page = this
        PageDecorator ancestorPage = null

        while (page) {
            if (predicate.apply(page)) {
                ancestorPage = page
                break
            } else {
                page = page.parent
            }
        }

        Optional.fromNullable(ancestorPage)
    }

    @Override
    Optional<PageDecorator> findAncestorWithProperty(String propertyName) {
        findAncestorForPredicate(new ComponentNodePropertyExistsPredicate(propertyName))
    }

    @Override
    <V> Optional<PageDecorator> findAncestorWithPropertyValue(String propertyName, V propertyValue) {
        findAncestorForPredicate(new ComponentNodePropertyValuePredicate<V>(propertyName, propertyValue))
    }

    @Override
    List<PageDecorator> findDescendants(Predicate<PageDecorator> predicate) {
        def pages = []

        def pageManager = getPageManagerDecorator()

        delegate.listChildren(ALL_PAGES, true).each { child ->
            PageDecorator page = pageManager.getPage(child)

            if (predicate.apply(page)) {
                pages.add(page)
            }
        }

        pages
    }

    @Override
    List<PageDecorator> getChildren() {
        filterChildren(ALL, false)
    }

    @Override
    List<PageDecorator> getChildren(boolean displayableOnly) {
        displayableOnly ? filterChildren(DISPLAYABLE_ONLY, false) : filterChildren(ALL, false)
    }

    @Override
    List<PageDecorator> getChildren(Predicate<PageDecorator> predicate) {
        filterChildren(checkNotNull(predicate), false)
    }

    @Override
    Optional<ComponentNode> getComponentNode() {
        componentNodeOptional
    }

    @Override
    Optional<ComponentNode> getComponentNode(String relativePath) {
        componentNodeOptional.present ? componentNodeOptional.get().getComponentNode(relativePath) : Optional.absent()
    }

    @Override
    String getHref() {
        getHref(false)
    }

    @Override
    String getHref(boolean mapped) {
        getLink(mapped).href
    }

    @Override
    ImageLink getImageLink(String imageSource) {
        DefaultLinkBuilder.forPage(this).setImageSource(checkNotNull(imageSource)).buildImageLink()
    }

    @Override
    boolean isHasImage() {
        getInternal({ ComponentNode componentNode -> componentNode.hasImage }, false)
    }

    @Override
    boolean isHasImage(String name) {
        getInternal({ componentNode -> componentNode.isHasImage(name) }, false)
    }

    @Override
    Link getLink() {
        getLink(false)
    }

    @Override
    Link getLink(boolean mapped) {
        getLinkBuilder(mapped).build()
    }

    @Override
    LinkBuilder getLinkBuilder() {
        getLinkBuilder(false)
    }

    @Override
    LinkBuilder getLinkBuilder(boolean mapped) {
        DefaultLinkBuilder.forPage(this, mapped)
    }

    @Override
    NavigationLink getNavigationLink() {
        DefaultLinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).buildNavigationLink()
    }

    @Override
    NavigationLink getNavigationLink(boolean isActive) {
        DefaultLinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).setActive(isActive).buildNavigationLink()
    }

    // overrides

    @Override
    PageDecorator getAbsoluteParent(int level) {
        getPageDecorator(delegate.getAbsoluteParent(level))
    }

    @Override
    PageManagerDecorator getPageManager() {
        getPageManagerDecorator()
    }

    @Override
    PageDecorator getParent() {
        getPageDecorator(delegate.parent)
    }

    @Override
    PageDecorator getParent(int level) {
        getPageDecorator(delegate.getParent(level))
    }

    @Override
    String getTemplatePath() {
        properties.get(NameConstants.NN_TEMPLATE, "")
    }

    @Override
    Optional<String> getTitle(TitleType titleType) {
        get(titleType.propertyName, String)
    }

    @Override
    String getTitle() {
        properties.get(NameConstants.PN_TITLE, "")
    }

    // internals

    private def getPageDecorator(Page page) {
        page ? new DefaultPageDecorator(page) : null
    }

    private def getInternal(Closure closure, defaultValue) {
        componentNodeOptional.present ? closure.call(componentNodeOptional.get()) : defaultValue
    }

    private def findAncestorForPredicate(Predicate<ComponentNode> predicate) {
        PageDecorator page = this
        PageDecorator ancestorPage = null

        while (page) {
            Optional<ComponentNode> optionalComponentNode = page.componentNode

            if (optionalComponentNode.present && predicate.apply(optionalComponentNode.get())) {
                ancestorPage = page
                break
            } else {
                page = page.parent
            }
        }

        Optional.fromNullable(ancestorPage)
    }

    private def filterChildren(Predicate<PageDecorator> predicate, boolean deep) {
        def pages = []

        def pageManager = getPageManagerDecorator()

        delegate.listChildren(ALL_PAGES, deep).each { child ->
            def page = pageManager.getPage(child)

            if (page && predicate.apply(page)) {
                pages.add(page)
            }
        }

        pages
    }

    private def getPageManagerDecorator() {
        delegate.adaptTo(Resource).getResourceResolver().adaptTo(PageManagerDecorator)
    }
}
