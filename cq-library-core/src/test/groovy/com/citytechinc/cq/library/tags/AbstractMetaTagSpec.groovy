/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

import com.adobe.granite.xss.XSSAPI
import com.citytechinc.cq.library.testing.specs.AbstractPageTagSpec

import javax.servlet.ServletRequest

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

abstract class AbstractMetaTagSpec extends AbstractPageTagSpec {

    def setup() {
        def xssApi = Mock(XSSAPI) {
            encodeForHTML(_) >> { String source -> source }
            encodeForHTMLAttr(_) >> { String source -> source }
            getValidHref(_) >> { String source -> source }
        }

        tag.pageContext.getAttribute(DEFAULT_XSSAPI_NAME) >> xssApi
        tag.pageContext.request >> Mock(ServletRequest)
    }
}