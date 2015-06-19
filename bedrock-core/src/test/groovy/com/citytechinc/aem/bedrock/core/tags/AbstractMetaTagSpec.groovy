package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.prosper.tag.JspTagProxy
import org.apache.sling.xss.XSSAPI

import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME

abstract class AbstractMetaTagSpec extends BedrockSpec {

    @Override
    public <T extends TagSupport> JspTagProxy<T> init(Class<T> tagClass, String path) {
        init(tagClass, path, [:])
    }

    @Override
    public <T extends TagSupport> JspTagProxy<T> init(Class<T> tagClass, String path,
        Map<String, Object> additionalPageContextAttributes) {
        additionalPageContextAttributes[DEFAULT_XSSAPI_NAME] = Mock(XSSAPI) {
            encodeForHTML(_) >> { String source -> source }
            encodeForHTMLAttr(_) >> { String source -> source }
            getValidHref(_) >> { String source -> source }
        }

        super.init(tagClass, path, additionalPageContextAttributes)
    }
}