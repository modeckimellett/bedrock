package com.citytechinc.aem.bedrock.core.tags

import com.adobe.granite.xss.XSSAPI
import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

abstract class AbstractMetaTagSpec<T extends TagSupport> extends BedrockJspTagSpec<T> {

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