package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.prosper.specs.JspTag
import com.citytechinc.aem.prosper.specs.JspTagSpec
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME

abstract class BedrockJspTagSpec extends JspTagSpec {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    /**
     * Initialize a tag with the resource and page for the given path.
     *
     * @param tag tag under test
     * @param path resource path
     * @return JSP tag instance containing page context and writer
     */
    JspTag init(TagSupport tag, String path) {
        init(tag, path, [:])
    }

    /**
     * Initialize a tag with the resource and page for the given path.
     *
     * @param tag tag under test
     * @param path resource path
     * @param additionalPageContextAttributes additional attributes to add to page context
     * @return JSP tag instance containing page context and writer
     */
    JspTag init(TagSupport tag, String path, Map<String, Object> additionalPageContextAttributes) {
        def resource = resourceResolver.getResource(path)
        def currentPage = resourceResolver.adaptTo(PageManager).getContainingPage(resource)

        additionalPageContextAttributes[DEFAULT_RESOURCE_NAME] = resource
        additionalPageContextAttributes[DEFAULT_CURRENT_PAGE_NAME] = currentPage

        super.init(tag, additionalPageContextAttributes)
    }
}
