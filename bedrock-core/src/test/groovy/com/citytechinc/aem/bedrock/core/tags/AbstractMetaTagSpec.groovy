package com.citytechinc.aem.bedrock.core.tags

import com.adobe.granite.xss.XSSAPI
import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec
import com.citytechinc.aem.prosper.specs.JspTag

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

abstract class AbstractMetaTagSpec extends BedrockJspTagSpec {

    @Override
    JspTag init(TagSupport tag, String path) {
        init(tag, path, [:])
    }

    @Override
    JspTag init(TagSupport tag, String path, Map<String, Object> additionalPageContextAttributes) {
        additionalPageContextAttributes[DEFAULT_XSSAPI_NAME] = Mock(XSSAPI) {
            encodeForHTML(_) >> { String source -> source }
            encodeForHTMLAttr(_) >> { String source -> source }
            getValidHref(_) >> { String source -> source }
        }

        super.init(tag, path, additionalPageContextAttributes)
    }
}