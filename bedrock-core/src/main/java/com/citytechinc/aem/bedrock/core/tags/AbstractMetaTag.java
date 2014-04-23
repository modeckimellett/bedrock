package com.citytechinc.aem.bedrock.core.tags;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.widget.Doctype;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_XSSAPI_NAME;

public abstract class AbstractMetaTag extends AbstractPropertyTag {

    @SuppressWarnings("deprecation")
    protected String getTagEnd() {
        final StringBuilder builder = new StringBuilder();

        builder.append('"');
        builder.append(Doctype.isXHTML(pageContext.getRequest()) ? "/" : "");
        builder.append('>');

        return builder.toString();
    }

    protected final XSSAPI getXssApi() {
        return (XSSAPI) pageContext.getAttribute(DEFAULT_XSSAPI_NAME);
    }
}
