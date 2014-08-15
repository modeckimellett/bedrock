package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.adapter.BedrockAdapterFactory
import com.citytechinc.aem.prosper.specs.JspTagSpec
import org.apache.sling.api.adapter.AdapterFactory

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME

abstract class BedrockJspTagSpec<T extends TagSupport> extends JspTagSpec<T> {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new BedrockAdapterFactory()]
    }

    /**
     * Set a <code>PageDecorator</code> for the given path in the <code>PageContext</code> for the tag under test.
     *
     * @param path page path
     */
    void setupPage(String path) {
        def page = resourceResolver.adaptTo(PageManagerDecorator).getPage(path)

        tag.pageContext.setAttribute DEFAULT_CURRENT_PAGE_NAME, page
    }

    /**
     * Set a <code>Resource</code> for the given path in the <code>PageContext</code> for the tag under test.
     *
     * @param path resource path
     */
    void setupResource(String path) {
        tag.pageContext.setAttribute DEFAULT_RESOURCE_NAME, resourceResolver.getResource(path)
    }
}
