/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags
import com.adobe.granite.xss.XSSAPI
import com.citytechinc.cq.library.testing.specs.AbstractPageTagSpec
import com.day.cq.wcm.tags.DefineObjectsTEI

import javax.servlet.ServletRequest

abstract class AbstractMetaTagSpec extends AbstractPageTagSpec {

    @SuppressWarnings("GrDeprecatedAPIUsage")
    @Override
    void setupPage(path) {
        super.setupPage(path)

        createMocks(tag.pageContext)
    }

    void setupPageContext() {
        def pageContext = mockPageContext()

        createMocks(pageContext)

        pageContext.getAttribute(DefineObjectsTEI.ATTR_RESOURCE_RESOLVER_NAME) >> resourceResolver
    }

    void createMocks(pageContext) {
        def xssApi = Mock(XSSAPI) {
            encodeForHTML(_) >> { String source -> source }
            encodeForHTMLAttr(_) >> { String source -> source }
            getValidHref(_) >> { String source -> source }
        }

        pageContext.getAttribute(DefineObjectsTEI.ATTR_XSSAPI_NAME) >> xssApi
        pageContext.request >> Mock(ServletRequest)
    }
}