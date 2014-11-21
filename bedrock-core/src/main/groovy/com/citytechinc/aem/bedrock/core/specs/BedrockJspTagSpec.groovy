package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.prosper.specs.JspTagProxy
import com.day.cq.wcm.api.PageManager

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME

abstract class BedrockJspTagSpec extends BedrockSpec {

    /**
     * Initialize a tag with the resource and page for the given path.
     *
     * @param tag tag under test
     * @param path resource path
     * @return JSP tag instance containing page context and writer
     */
    JspTagProxy init(TagSupport tag, String path) {
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
    JspTagProxy init(TagSupport tag, String path, Map<String, Object> additionalPageContextAttributes) {
        def resource = resourceResolver.getResource(path)
        def currentPage = resourceResolver.adaptTo(PageManager).getContainingPage(resource)

        additionalPageContextAttributes[DEFAULT_RESOURCE_NAME] = resource
        additionalPageContextAttributes[DEFAULT_CURRENT_PAGE_NAME] = currentPage

        super.init(tag, additionalPageContextAttributes)
    }
}
