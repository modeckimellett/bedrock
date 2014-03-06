/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.tags

import com.adobe.granite.xss.XSSAPI
import com.citytechinc.aem.bedrock.testing.specs.PageTagSpec

import javax.servlet.ServletRequest

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

abstract class AbstractMetaTagSpec extends PageTagSpec {

    def setup() {
        tag.pageContext.request >> Mock(ServletRequest)
    }

    @Override
    Map<String, Object> addPageContextAttributes() {
        def xssApi = Mock(XSSAPI) {
            encodeForHTML(_) >> { String source -> source }
            encodeForHTMLAttr(_) >> { String source -> source }
            getValidHref(_) >> { String source -> source }
        }

        [(DEFAULT_XSSAPI_NAME): xssApi]
    }
}