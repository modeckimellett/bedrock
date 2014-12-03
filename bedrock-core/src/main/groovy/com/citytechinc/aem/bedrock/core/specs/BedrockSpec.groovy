package com.citytechinc.aem.bedrock.core.specs

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.citytechinc.aem.prosper.support.JspTagSupport
import com.citytechinc.aem.prosper.support.SightlySupport
import com.citytechinc.aem.prosper.tag.JspTagProxy
import com.day.cq.wcm.api.PageManager
import io.sightly.java.api.Use
import org.apache.sling.api.adapter.AdapterFactory
import spock.lang.Shared

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME

/**
 * Spock specification for testing Bedrock-based components and services.
 */
abstract class BedrockSpec extends ProsperSpec {

    @Shared
    PageManagerDecorator pageManagerDecorator

    @Shared
    SightlySupport sightlySupport

    @Shared
    JspTagSupport jspTagSupport

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    def setupSpec() {
        pageManagerDecorator = resourceResolver.adaptTo(PageManagerDecorator)
        sightlySupport = new SightlySupport(resourceResolver)
        jspTagSupport = new JspTagSupport(resourceResolver)
    }

    ComponentNode getComponentNode(String path) {
        resourceResolver.getResource(path).adaptTo(ComponentNode)
    }

    @Override
    PageDecorator getPage(String path) {
        pageManagerDecorator.getPage(path)
    }

    @Override
    PageManagerDecorator getPageManager() {
        pageManagerDecorator
    }

    // delegate methods

    public <T extends Use> T init(Class<T> type, @DelegatesTo(BindingsBuilder) Closure closure) {
        sightlySupport.init(type, closure)
    }

    public <T extends WCMUse> T activate(Class<T> type, @DelegatesTo(BindingsBuilder) Closure closure) {
        sightlySupport.activate(type, closure)
    }

    public <T extends TagSupport> JspTagProxy init(Class<T> tagClass, String path) {
        init(tagClass, path, [:])
    }

    public <T extends TagSupport> JspTagProxy init(Class<T> tagClass, String path,
        Map<String, Object> additionalPageContextAttributes) {
        def resource = resourceResolver.getResource(path)
        def currentPage = resourceResolver.adaptTo(PageManager).getContainingPage(resource)

        additionalPageContextAttributes[DEFAULT_RESOURCE_NAME] = resource
        additionalPageContextAttributes[DEFAULT_CURRENT_PAGE_NAME] = currentPage
        additionalPageContextAttributes[DEFAULT_BINDINGS_NAME] = new BindingsBuilder(resourceResolver).build {
            setPath(path)
        }

        jspTagSupport.init(tagClass, additionalPageContextAttributes)
    }
}
