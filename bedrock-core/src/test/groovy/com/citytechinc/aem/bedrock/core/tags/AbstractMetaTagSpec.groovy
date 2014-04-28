package com.citytechinc.aem.bedrock.core.tags

import com.adobe.granite.xss.XSSAPI
import com.citytechinc.aem.bedrock.core.specs.PageTagSpec

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

abstract class AbstractMetaTagSpec<T> extends PageTagSpec {

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