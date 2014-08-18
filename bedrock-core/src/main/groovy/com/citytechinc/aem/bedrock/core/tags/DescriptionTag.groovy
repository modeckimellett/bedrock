package com.citytechinc.aem.bedrock.core.tags

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.Page
import com.google.common.html.HtmlEscapers
import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspTagException

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME

/**
 * Render the description for the current page.
 */
@Slf4j("LOG")
final class DescriptionTag extends AbstractMetaTag {

    private static final def TAG_START = "<meta name=\"description\" content=\""

    private static final def ESCAPER = HtmlEscapers.htmlEscaper()

    String suffix

    @Override
    int doEndTag() throws JspTagException {
        def currentPage = pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME) as Page
        def properties = currentPage.properties

        def builder = new StringBuilder()

        builder.append(TAG_START)

        def description

        if (hasPropertyName()) {
            description = properties.get(propertyName, "")
        } else {
            description = properties.get(JcrConstants.JCR_DESCRIPTION, "")
        }

        if (description) {
            def content = new StringBuilder(ESCAPER.escape(description))

            if (suffix) {
                content.append(suffix)
            }

            builder.append(xssApi.encodeForHTMLAttr(content.toString()))
        }

        builder.append(tagEnd)

        try {
            pageContext.out.write(builder.toString())
        } catch (IOException ioe) {
            LOG.error "error writing description tag for page = ${currentPage.path}", ioe

            throw new JspTagException(ioe)
        }

        EVAL_PAGE
    }
}
