package com.citytechinc.aem.bedrock.core.tags;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Render the description for the current page.
 */
public final class DescriptionTag extends AbstractMetaTag {

    private static final Logger LOG = LoggerFactory.getLogger(DescriptionTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<meta name=\"description\" content=\"";

    private static final Escaper ESCAPER = HtmlEscapers.htmlEscaper();

    private String suffix;

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME);
        final ValueMap properties = currentPage.getProperties();

        final StringBuilder builder = new StringBuilder();

        builder.append(TAG_START);

        final String description;

        if (hasPropertyName()) {
            description = properties.get(propertyName, "");
        } else {
            description = properties.get(JcrConstants.JCR_DESCRIPTION, "");
        }

        if (!isNullOrEmpty(description)) {
            final StringBuilder content = new StringBuilder(ESCAPER.escape(description));

            if (!isNullOrEmpty(suffix)) {
                content.append(suffix);
            }

            builder.append(getXssApi().encodeForHTMLAttr(content.toString()));
        }

        builder.append(getTagEnd());

        try {
            pageContext.getOut().write(builder.toString());
        } catch (IOException ioe) {
            LOG.error("error writing description tag for page = " + currentPage.getPath(), ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
}
